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
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.zest.layout.dataStructures.DisplayIndependentDimension;
import org.eclipse.zest.layout.dataStructures.DisplayIndependentPoint;
import org.eclipse.zest.layout.dataStructures.DisplayIndependentRectangle;
import org.eclipse.zest.layout.interfaces.ConnectionLayout;
import org.eclipse.zest.layout.interfaces.ContextListener;
import org.eclipse.zest.layout.interfaces.EntityLayout;
import org.eclipse.zest.layout.interfaces.ExpandCollapseManager;
import org.eclipse.zest.layout.interfaces.GraphStructureListener;
import org.eclipse.zest.layout.interfaces.LayoutAlgorithm;
import org.eclipse.zest.layout.interfaces.LayoutContext;
import org.eclipse.zest.layout.interfaces.LayoutListener;
import org.eclipse.zest.layout.interfaces.NodeLayout;
import org.eclipse.zest.layout.interfaces.SubgraphLayout;

/**
 * The TreeLayoutAlgorithm class implements a simple algorithm to arrange graph
 * nodes in a layered tree-like layout.
 * 
 * @version 3.0
 * @author Mateusz Matela
 * @author Casey Best and Rob Lintern (version 2.0)
 * @author Jingwei Wu (version 1.0)
 */
public class ExpandableTreeLayoutAlgorithm implements LayoutAlgorithm, ExpandCollapseManager {

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

	private class SpaceTreeNode {
		final public NodeLayout node;
		public int height = 0;
		public int depth = -1;
		public int numOfLeaves = 0;
		public boolean expanded = false;
		public final List children = new ArrayList();
		public SpaceTreeNode parent;

		/**
		 * If node is collapsed and not pruned, all it's children are placed in
		 * this subgraph (if there are no children, it's null).
		 */
		public SubgraphLayout subgraph = null;

		public SpaceTreeNode(NodeLayout node) {
			this.node = node;
			if (node != null)
				layoutToSpaceTree.put(node, this);
		}

		public void addChild(SpaceTreeNode child) {
			children.add(child);
			child.parent = this;

			if (node == null || (expanded && !node.isPruned())) {
				// (node == null) means this is superRoot
				// unprune and refresh expanded state
				child.node.prune(null);
				child.setExpanded(child.expanded);
			} else {
				List allChildren = child.getAllChildrenLayouts();
				allChildren.add(child.node);
				NodeLayout[] childrenArray = (NodeLayout[]) allChildren.toArray(new NodeLayout[allChildren.size()]);
				if (node.isPruned()) {
					// prune into a subgraph containing this node
					node.getSubgraph().addNodes(childrenArray);
				} else {
					// prune into subgraph under this node
					if (subgraph != null)
						subgraph.addNodes(childrenArray);
					else
						setSubgraph(context.createSubgraph(childrenArray));
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
					SpaceTreeNode child = (SpaceTreeNode) iterator.next();
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
				SpaceTreeNode child = (SpaceTreeNode) iterator.next();
				superRoot.addChild(child);
			}
		}

		public void setExpanded(boolean expanded) {
			this.expanded = expanded;
			if (node.isPruned())
				return;
			if (expanded) {
				expand();
			} else {
				collapse();
			}
		}
		
		private void expand() {
			setSubgraph(null);
			for (Iterator iterator = children.iterator(); iterator.hasNext();) {
				SpaceTreeNode child = (SpaceTreeNode) iterator.next();
				child.node.prune(null);
				if (child.expanded) {
					child.expand();
				} else {
					child.collapse();
				}
			}
		}

		private void collapse() {
			List children = getAllChildrenLayouts();
			if (!children.isEmpty()) {
				setSubgraph(context.createSubgraph((NodeLayout[]) children.toArray(new NodeLayout[children.size()])));
			}
		}

		public List getAllChildrenLayouts() {
			ArrayList result = new ArrayList();
			LinkedList nodesToVisit = new LinkedList();
			nodesToVisit.addLast(this);
			while (!nodesToVisit.isEmpty()) {
				SpaceTreeNode currentNode = (SpaceTreeNode) nodesToVisit.removeFirst();
				for (Iterator iterator = currentNode.children.iterator(); iterator.hasNext();) {
					SpaceTreeNode child = (SpaceTreeNode) iterator.next();
					result.add(child.node);
					nodesToVisit.addLast(child);
				}
			}
			return result;
		}

		public void findNewParent() {
			if (parent != null)
				parent.children.remove(this);
			NodeLayout[] predecessingNodes = node.getPredecessingNodes();
			SpaceTreeNode bestParent;
			if (predecessingNodes.length > 0) {
				bestParent = (SpaceTreeNode) layoutToSpaceTree.get(predecessingNodes[0]);
				for (int i = 1; i < predecessingNodes.length; i++) {
					SpaceTreeNode potentialParent = (SpaceTreeNode) layoutToSpaceTree.get(predecessingNodes[i]);
					if (isBetterParent(bestParent, potentialParent))
						bestParent = potentialParent;
				}
			} else
				bestParent = superRoot;

			bestParent.addChild(this);
		}

		public void setSubgraph(SubgraphLayout subgraph) {
			this.subgraph = subgraph;
			refreshSubgraphLocation();
		}

		public void refreshSubgraphLocation() {
			if (subgraph != null && subgraph.isGraphEntity()) {
				DisplayIndependentPoint nodeLocation = node.getLocation();
				DisplayIndependentDimension nodeSize = node.getSize();
				DisplayIndependentDimension subgraphSize = subgraph.getSize();
				switch (direction) {
				case TOP_DOWN:
					subgraph.setLocation(nodeLocation.x, nodeLocation.y + (nodeSize.height + subgraphSize.height) / 2);
					break;
				case BOTTOM_UP:
					subgraph.setLocation(nodeLocation.x, nodeLocation.y - (nodeSize.height + subgraphSize.height) / 2);
					break;
				case LEFT_RIGHT:
					subgraph.setLocation(nodeLocation.x + (nodeSize.width + subgraphSize.width) / 2, nodeLocation.y);
					break;
				case RIGHT_LEFT:
					subgraph.setLocation(nodeLocation.x - (nodeSize.width + subgraphSize.height) / 2, nodeLocation.y);
					break;
				}
			}
		}

		public String toString() {
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < depth; i++)
				sb.append(" ");
			if (node != null)
				sb.append(node.toString());
			sb.append('\n');
			for (Iterator iterator = children.iterator(); iterator.hasNext();) {
				SpaceTreeNode child = (SpaceTreeNode) iterator.next();
				sb.append(child.toString());
			}
			return sb.toString();
		}
	}

	private GraphStructureListener structureListener = new GraphStructureListener() {

		public boolean nodeRemoved(LayoutContext context, NodeLayout node) {
			SpaceTreeNode spaceTreeNode = (SpaceTreeNode) layoutToSpaceTree.get(node);
			spaceTreeNode.delete();
			superRoot.precomputeTree();
			refreshLayout(true);
			return false;
		}

		public boolean nodeAdded(LayoutContext context, NodeLayout node) {
			superRoot.addChild(new SpaceTreeNode(node));
			superRoot.precomputeTree();
			refreshLayout(true);
			return false;
		}

		public boolean connectionRemoved(LayoutContext context, ConnectionLayout connection) {
			SpaceTreeNode node1 = (SpaceTreeNode) layoutToSpaceTree.get(connection.getSource());
			SpaceTreeNode node2 = (SpaceTreeNode) layoutToSpaceTree.get(connection.getTarget());
			if (node1.parent == node2) {
				node1.findNewParent();
			}
			if (node2.parent == node1) {
				node2.findNewParent();
			}
			superRoot.precomputeTree();
			refreshLayout(true);
			return false;
		}

		public boolean connectionAdded(LayoutContext context, ConnectionLayout connection) {
			SpaceTreeNode source = (SpaceTreeNode) layoutToSpaceTree.get(connection.getSource());
			SpaceTreeNode target = (SpaceTreeNode) layoutToSpaceTree.get(connection.getTarget());
			if (source == target)
				return false;
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
			refreshLayout(true);
			return false;
		}
	};

	private ContextListener contextListener = new ContextListener.Stub() {
		public boolean boundsChanged(LayoutContext context) {
			refreshLayout(false);
			return false;
		}
	};

	private LayoutListener layoutListener = new LayoutListener() {

		public boolean subgraphResized(LayoutContext context, SubgraphLayout node) {
			// do nothing
			return false;
		}

		public boolean subgraphMoved(LayoutContext context, SubgraphLayout node) {
			// do nothing
			return false;
		}

		public boolean nodeResized(LayoutContext context, NodeLayout node) {
			return defaultHandle(context, node);
		}

		public boolean nodeMoved(LayoutContext context, NodeLayout node) {
			return defaultHandle(context, node);
		}

		private boolean defaultHandle(LayoutContext context, NodeLayout node) {
			((SpaceTreeNode) layoutToSpaceTree.get(node)).refreshSubgraphLocation();
			context.flushChanges(false);
			return false;
		}
	};

	private int direction = TOP_DOWN;

	private boolean resize = false;

	private boolean refreshOn = true;

	private LayoutContext context;

	private DisplayIndependentRectangle bounds;

	private double leafSize, layerSize;

	private SpaceTreeNode superRoot;

	private HashMap layoutToSpaceTree = new HashMap();

	public ExpandableTreeLayoutAlgorithm() {
	}

	public ExpandableTreeLayoutAlgorithm(int direction) {
		setDirection(direction);
	}

	public int getDirection() {
		return direction;
	}

	public void setDirection(int direction) {
		if (direction == TOP_DOWN || direction == BOTTOM_UP || direction == LEFT_RIGHT || direction == RIGHT_LEFT)
			this.direction = direction;
		else
			throw new IllegalArgumentException("Invalid direction: " + direction);
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
		context.addLayoutListener(layoutListener);

		superRoot = new SpaceTreeNode(null);
		createTrees(context.getNodes());
		collapseAll();
	}

	public void setExpanded(NodeLayout node, boolean expanded) {
		SpaceTreeNode spaceTreeNode = (SpaceTreeNode) layoutToSpaceTree.get(node);
		spaceTreeNode.setExpanded(expanded);
		refreshLayout(false);
	}

	protected void refreshLayout(boolean animation) {
		if (refreshOn) {
			internalApplyLayout();
			context.flushChanges(animation);
		}
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
			SpaceTreeNode root = (SpaceTreeNode) iterator.next();
			computePositionRecursively(root, leafCountSoFar);
			leafCountSoFar = leafCountSoFar + root.numOfLeaves;
		}
	}

	private static boolean isBetterParent(SpaceTreeNode base, SpaceTreeNode better) {
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
		LinkedList nodesToAdd = new LinkedList();
		for (int i = 0; i < entities.length; i++) {
			EntityLayout root = findRoot(entities[i], alreadyVisited);
			if (root != null) {
				alreadyVisited.add(root);
				nodesToAdd.addLast(new Object[] { root, superRoot });
			}
		}
		while (!nodesToAdd.isEmpty()) {
			Object[] dequeued = (Object[]) nodesToAdd.removeFirst();
			SpaceTreeNode currentNode = new SpaceTreeNode((NodeLayout) dequeued[0]);
			SpaceTreeNode currentRoot = (SpaceTreeNode) dequeued[1];

			currentRoot.addChild(currentNode);
			NodeLayout[] children = currentNode.node.getSuccessingEntities();
			for (int i = 0; i < children.length; i++) {
				if (!alreadyVisited.contains(children[i])) {
					alreadyVisited.add(children[i]);
					nodesToAdd.addLast(new Object[] { children[i], currentNode });
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

	private void collapseAll() {
		for (Iterator iterator = superRoot.children.iterator(); iterator.hasNext();) {
			SpaceTreeNode root = (SpaceTreeNode) iterator.next();
			root.collapse();
		}
	}

	/**
	 * Computes positions recursively until the leaf nodes are reached.
	 */
	private void computePositionRecursively(SpaceTreeNode node, int relativePosition) {
		double breadthPosition = relativePosition + node.numOfLeaves / 2.0;
		double depthPosition = (node.depth + 0.5);

		switch (direction) {
		case TOP_DOWN:
			node.node.setLocation(breadthPosition * leafSize, depthPosition * layerSize);
			break;
		case BOTTOM_UP:
			node.node.setLocation(breadthPosition * leafSize, bounds.height - depthPosition * layerSize);
			break;
		case LEFT_RIGHT:
			node.node.setLocation(depthPosition * layerSize, breadthPosition * leafSize);
			break;
		case RIGHT_LEFT:
			node.node.setLocation(bounds.width - depthPosition * layerSize, breadthPosition * leafSize);
			break;
		}
		node.refreshSubgraphLocation();

		if (node.node.isPruned())
			return;

		for (Iterator iterator = node.children.iterator(); iterator.hasNext();) {
			SpaceTreeNode child = (SpaceTreeNode) iterator.next();
			computePositionRecursively(child, relativePosition);
			relativePosition += child.numOfLeaves;
		}
	}
}
