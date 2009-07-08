/*******************************************************************************
 * Copyright 2005, CHISEL Group, University of Victoria, Victoria, BC, Canada.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     The Chisel Group, University of Victoria
 *******************************************************************************/
package org.eclipse.zest.layout.algorithms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.zest.layout.dataStructures.DisplayIndependentRectangle;
import org.eclipse.zest.layout.interfaces.ConnectionLayout;
import org.eclipse.zest.layout.interfaces.ContextListener;
import org.eclipse.zest.layout.interfaces.EntityLayout;
import org.eclipse.zest.layout.interfaces.ExpandCollapseManager;
import org.eclipse.zest.layout.interfaces.GraphStructureListener;
import org.eclipse.zest.layout.interfaces.LayoutAlgorithm;
import org.eclipse.zest.layout.interfaces.LayoutContext;
import org.eclipse.zest.layout.interfaces.NodeLayout;
import org.eclipse.zest.layout.interfaces.SubgraphLayout;

import sun.misc.Queue;

/**
 * The TreeLayoutAlgorithm class implements a simple algorithm to arrange graph
 * nodes in a layered tree-like layout.
 * 
 * @version 3.0
 * @author Mateusz Matela
 * @author Casey Best and Rob Lintern (version 2.0)
 * @author Jingwei Wu (version 1.0)
 */
public class SpaceTreeLayoutAlgorithm implements LayoutAlgorithm, ExpandCollapseManager {

	/**
	 * Tree direction constant for which root is placed at the top and branches
	 * spread downwards
	 */
	public final static int TOP_DOWN = 1;

	/**
	 * Tree direction constant for which root is placed at the bottom and
	 * branches spread upwards
	 */
	public final static int BOTTOM_UP = 2;

	/**
	 * Tree direction constant for which root is placed at the left and branches
	 * spread to the right
	 */
	public final static int LEFT_RIGHT = 3;

	/**
	 * Tree direction constant for which root is placed at the right and
	 * branches spread to the left
	 */
	public final static int RIGHT_LEFT = 4;

	private class NodeInfo {
		public NodeInfo(NodeLayout node) {
			this.node = node;
			if (node != null)
				nodeToInfo.put(node, this);
		}

		public void addChild(NodeInfo child) {
			children.add(child);
			child.parent = this;

			if (node == null || (expanded && !node.isPruned())) {
				// unprune and refresh expanded state
				child.node.prune(null);
				setExpanded(child.node, child.expanded);
			} else {
				List allChildren = getAllChildren(child);
				allChildren.add(child.node);
				if (node.isPruned()) {
					// prune into a subgraph containing this node
					node.getSubgraph().addNodes((NodeLayout[]) allChildren.toArray(new NodeLayout[allChildren.size()]));
				} else {
					// prune into subgraph under this node
					subgraph.addNodes((NodeLayout[]) allChildren.toArray(new NodeLayout[allChildren.size()]));
				}
			}
		}

		public void precomputeTree() {
			if (children.isEmpty()) {
				numOfLeaves = 1;
				height = 0;
			} else {
				numOfLeaves = 0;
				height = 0;
				for (Iterator iterator = children.iterator(); iterator.hasNext();) {
					NodeInfo child = (NodeInfo) iterator.next();
					child.depth = this.depth + 1;
					child.precomputeTree();
					this.numOfLeaves += child.numOfLeaves;
					this.height = Math.max(this.height, child.height + 1);
				}
			}
		}

		public void delete() {
			parent.children.remove(this);
			for (Iterator iterator = children.iterator(); iterator.hasNext();) {
				NodeInfo child = (NodeInfo) iterator.next();
				superRoot.addChild(child);
			}
		}

		public void findNewParent() {
			if (parent != null)
				parent.children.remove(this);
			NodeLayout[] predecessingNodes = node.getPredecessingNodes();
			NodeInfo bestParent;
			if (predecessingNodes.length > 0) {
				bestParent = (NodeInfo) nodeToInfo.get(predecessingNodes[0]);
				for (int i = 1; i < predecessingNodes.length; i++) {
					NodeInfo potentialParent = (NodeInfo) nodeToInfo.get(predecessingNodes[i]);
					if (isBetterParent(bestParent, potentialParent))
						bestParent = potentialParent;
				}
			} else
				bestParent = superRoot;

			bestParent.addChild(this);
			superRoot.precomputeTree();
		}

		final public NodeLayout node;
		public int height = 0;
		public int depth = -1;
		public int numOfLeaves = 0;
		public boolean expanded = false;
		public final List children = new ArrayList();
		public NodeInfo parent;

		/**
		 * If node is collapsed and not pruned, all it's children are placed in
		 * this subgraph
		 */
		public SubgraphLayout subgraph = null;

		public String toString() {
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < depth; i++)
				sb.append(" ");
			if (node != null)
				sb.append(node.toString());
			sb.append('\n');
			for (Iterator iterator = children.iterator(); iterator.hasNext();) {
				NodeInfo child = (NodeInfo) iterator.next();
				sb.append(child.toString());
			}
			return sb.toString();
		}
	}

	private GraphStructureListener structureListener = new GraphStructureListener() {

		public boolean nodeRemoved(LayoutContext context, NodeLayout node) {
			NodeInfo info = (NodeInfo) nodeToInfo.get(node);
			info.delete();
			superRoot.precomputeTree();
			refreshLayout();
			return false;
		}

		public boolean nodeAdded(LayoutContext context, NodeLayout node) {
			superRoot.addChild(new NodeInfo(node));
			superRoot.precomputeTree();
			refreshLayout();
			return false;
		}

		public boolean connectionRemoved(LayoutContext context, ConnectionLayout connection) {
			NodeInfo info1 = (NodeInfo) nodeToInfo.get(connection.getSource());
			NodeInfo info2 = (NodeInfo) nodeToInfo.get(connection.getTarget());
			if (info1.parent == info2) {
				info1.findNewParent();
			}
			if (info2.parent == info1) {
				info2.findNewParent();
			}
			refreshLayout();
			return false;
		}

		public boolean connectionAdded(LayoutContext context, ConnectionLayout connection) {
			NodeInfo source = (NodeInfo) nodeToInfo.get(connection.getSource());
			NodeInfo target = (NodeInfo) nodeToInfo.get(connection.getTarget());
			if (isBetterParent(target.parent, source)) {
				target.parent.children.remove(target);
				source.addChild(target);
				superRoot.precomputeTree();
			}
			if (!connection.isDirected() && isBetterParent(source.parent, target)) {
				source.parent.children.remove(source);
				target.addChild(source);
				superRoot.precomputeTree();
			}
			refreshLayout();
			return false;
		}
	};

	private ContextListener contextListener = new ContextListener.Stub() {
		public boolean boundsChanged(LayoutContext context) {
			refreshLayout();
			return false;
		}
	};

	private int direction = TOP_DOWN;

	private boolean resize = false;

	private LayoutContext context;

	private DisplayIndependentRectangle bounds;

	private double leafSize, layerSize;

	private NodeInfo superRoot;

	private HashMap nodeToInfo = new HashMap();

	public SpaceTreeLayoutAlgorithm() {
	}

	public SpaceTreeLayoutAlgorithm(int direction) {
		setDirection(direction);
	}

	public int getDirection() {
		return direction;
	}

	public void setDirection(int direction) {
		if (direction == TOP_DOWN || direction == BOTTOM_UP || direction == LEFT_RIGHT || direction == RIGHT_LEFT)
			this.direction = direction;
		else
			throw new RuntimeException("Invalid direction: " + direction);
	}

	/**
	 * 
	 * @return true if this algorithm is set to resize elements
	 */
	public boolean isResizing() {
		return resize;
	}

	/**
	 * 
	 * @param resizing
	 *            true if this algorithm should resize elements (default is
	 *            false)
	 */
	public void setResizing(boolean resizing) {
		resize = resizing;
	}

	public void applyLayout() {
		internalApplyLayout();
	}

	public void setLayoutContext(LayoutContext context) {
		if (this.context != null) {
			this.context.setExpandCollapseManager(null);
			this.context.removeGraphStructureListener(structureListener);
			this.context.removeContextListener(contextListener);
		}
		this.context = context;
		context.setExpandCollapseManager(this);
		context.addGraphStructureListener(structureListener);
		context.addContextListener(contextListener);

		superRoot = new NodeInfo(null);
		createTrees(context.getNodes());
		collapseAll();
	}

	public void setExpanded(NodeLayout node, boolean expanded) {
		NodeInfo info = (NodeInfo) nodeToInfo.get(node);
		info.expanded = expanded;
		if (node.isPruned())
			return;
		if (expanded) {
			expand(info);
		} else {
			collapse(info);
		}
		refreshLayout();
	}

	protected void refreshLayout() {
		internalApplyLayout();
		context.flushChanges(true);
	}

	void internalApplyLayout() {
		bounds = context.getBounds();
		if (direction == TOP_DOWN || direction == BOTTOM_UP) {
			leafSize = bounds.width / superRoot.numOfLeaves;
			layerSize = bounds.height / superRoot.height;
		} else {
			leafSize = bounds.height / superRoot.numOfLeaves;
			layerSize = bounds.width / superRoot.height;
		}
		int leafCountSoFar = 0;
		for (Iterator iterator = superRoot.children.iterator(); iterator.hasNext();) {
			NodeInfo rootInfo = (NodeInfo) iterator.next();
			computePositionRecursively(rootInfo, leafCountSoFar);
			leafCountSoFar = leafCountSoFar + rootInfo.numOfLeaves;
		}
	}

	private static boolean isBetterParent(NodeInfo base, NodeInfo better) {
		if (better.depth < base.depth && better.depth != -1)
			return true;
		if (base.depth == -1 && better.depth >= 0)
			return true;
		return false;
	}

	/**
	 * Builds a tree structure using BFS method. Created trees are children of
	 * {@link #superRoot}.
	 * 
	 * @param entities
	 */
	private void createTrees(NodeLayout[] entities) {
		HashSet alreadyVisited = new HashSet();
		Queue nodesToAdd = new Queue();
		for (int i = 0; i < entities.length; i++) {
			EntityLayout root = findRoot(entities[i], alreadyVisited);
			if (root != null) {
				alreadyVisited.add(root);
				nodesToAdd.enqueue(new Object[] { root, superRoot });
			}
		}
		while (!nodesToAdd.isEmpty()) {
			NodeLayout node;
			NodeInfo parentEntityInfo;
			try {
				Object[] dequeued = (Object[]) nodesToAdd.dequeue();
				node = (NodeLayout) dequeued[0];
				parentEntityInfo = (NodeInfo) dequeued[1];
			} catch (InterruptedException e) {
				throw new RuntimeException("This should never happen");
			}
			NodeInfo currentNodeInfo = new NodeInfo(node);
			parentEntityInfo.addChild(currentNodeInfo);
			NodeLayout[] children = node.getSuccessingEntities();
			for (int i = 0; i < children.length; i++) {
				if (!alreadyVisited.contains(children[i])) {
					alreadyVisited.add(children[i]);
					nodesToAdd.enqueue(new Object[] { children[i], currentNodeInfo });
				}
			}
		}
		superRoot.precomputeTree();
	}

	/**
	 * Searches for a root of a tree containing given entity by continuously
	 * grabbing a predecessor of current entity. If it reaches an entity that
	 * exists in alreadyVisited set, it returns null. If it detects a cycle, it
	 * returns the first found entity of that cycle. If it reaches an entity
	 * that has no predecessors, it returns that entity.
	 * 
	 * @param entityLayout
	 *            starting entity
	 * @param alreadyVisited
	 *            set of entities that can't lay on path to the root (if one
	 *            does, method stops and returns null).
	 * @return
	 */
	private EntityLayout findRoot(EntityLayout entityLayout, Set alreadyVisited) {
		HashSet alreadyVisitedRoot = new HashSet();
		while (true) {
			if (alreadyVisited.contains(entityLayout))
				return null;
			if (alreadyVisitedRoot.contains(entityLayout))
				return entityLayout;
			alreadyVisitedRoot.add(entityLayout);
			NodeLayout[] predecessingEntities = entityLayout.getPredecessingEntities();
			if (predecessingEntities.length > 0) {
				entityLayout = predecessingEntities[0];
			} else {
				return entityLayout;
			}
		}
	}

	private List getAllChildren(NodeInfo info) {
		ArrayList result = new ArrayList();
		if (info != null) {
			try {
				Queue infosToVisit = new Queue();
				infosToVisit.enqueue(info);
				while (!infosToVisit.isEmpty()) {
					info = (NodeInfo) infosToVisit.dequeue();
					for (Iterator iterator = info.children.iterator(); iterator.hasNext();) {
						NodeInfo info2 = (NodeInfo) iterator.next();
						result.add(info2.node);
						infosToVisit.enqueue(info2);
					}
				}
			} catch (InterruptedException e) {
				throw new RuntimeException("This should never happen");
			}
		}
		return result;
	}

	private void expand(NodeInfo info) {
		info.subgraph = null;
		for (Iterator iterator = info.children.iterator(); iterator.hasNext();) {
			NodeInfo info2 = (NodeInfo) iterator.next();
			info2.node.prune(null);
			if (info2.expanded) {
				expand(info2);
			} else {
				collapse(info2);
			}
		}
	}

	private void collapse(NodeInfo info) {
		List children = getAllChildren(info);
		info.subgraph = context.addSubgraph((NodeLayout[]) children.toArray(new NodeLayout[children.size()]));
	}

	/**
	 * Computes positions recursively until the leaf nodes are reached.
	 */
	private void computePositionRecursively(NodeInfo entityInfo, int relativePosition) {
		if (entityInfo.node.isPruned())
			return;
		
		double breadthPosition = relativePosition + entityInfo.numOfLeaves / 2.0;
		double depthPosition = (entityInfo.depth + 0.5);

		switch (direction) {
		case TOP_DOWN:
			entityInfo.node.setLocation(breadthPosition * leafSize, depthPosition * layerSize);
			break;
		case BOTTOM_UP:
			entityInfo.node.setLocation(breadthPosition * leafSize, bounds.height - depthPosition * layerSize);
			break;
		case LEFT_RIGHT:
			entityInfo.node.setLocation(bounds.width - depthPosition * layerSize, breadthPosition * leafSize);
			break;
		case RIGHT_LEFT:
			entityInfo.node.setLocation(depthPosition * layerSize, breadthPosition * leafSize);
			break;
		}

		for (Iterator iterator = entityInfo.children.iterator(); iterator.hasNext();) {
			NodeInfo childInfo = (NodeInfo) iterator.next();
			computePositionRecursively(childInfo, relativePosition);
			relativePosition += childInfo.numOfLeaves;
		}
	}

	private void collapseAll() {
		for (Iterator iterator = nodeToInfo.values().iterator(); iterator.hasNext();) {
			NodeInfo info = (NodeInfo) iterator.next();
			info.expanded = false;
			info.node.setMinimized(true);
		}
		for (Iterator iterator = superRoot.children.iterator(); iterator.hasNext();) {
			NodeInfo info = (NodeInfo) iterator.next();
			info.node.setMinimized(false);
			List children = getAllChildren(info);
			context.addSubgraph((NodeLayout[]) children.toArray(new NodeLayout[children.size()]));
		}
	}
}
