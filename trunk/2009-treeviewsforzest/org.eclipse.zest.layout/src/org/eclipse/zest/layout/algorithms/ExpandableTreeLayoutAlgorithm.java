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

	private class ExpandableTreeNode {
		final public NodeLayout node;
		public int height = 0;
		public int depth = -1;
		public int numOfLeaves = 0;
		public boolean expanded = false;
		public final List children = new ArrayList();
		public ExpandableTreeNode parent;

		/**
		 * If node is collapsed and not pruned, all it's children are placed in
		 * this subgraph (if there are no children, it's null).
		 */
		public SubgraphLayout subgraph = null;

		public ExpandableTreeNode(NodeLayout node) {
			this.node = node;
			if (node != null)
				layoutToTree.put(node, this);
		}

		public void addChild(ExpandableTreeNode child) {
			children.add(child);
			child.parent = this;

			if (this == superRoot || (expanded && !node.isPruned())) {
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
					ExpandableTreeNode child = (ExpandableTreeNode) iterator.next();
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
				ExpandableTreeNode child = (ExpandableTreeNode) iterator.next();
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
				ExpandableTreeNode child = (ExpandableTreeNode) iterator.next();
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
				ExpandableTreeNode currentNode = (ExpandableTreeNode) nodesToVisit.removeFirst();
				for (Iterator iterator = currentNode.children.iterator(); iterator.hasNext();) {
					ExpandableTreeNode child = (ExpandableTreeNode) iterator.next();
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
			parent = null;
			for (int i = 1; i < predecessingNodes.length; i++) {
				ExpandableTreeNode potentialParent = (ExpandableTreeNode) layoutToTree.get(predecessingNodes[i]);
				if (!children.contains(potentialParent) && isBetterParent(parent, potentialParent))
					parent = potentialParent;
			}
			if (parent == null)
				parent = superRoot;

			parent.addChild(this);
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

		public boolean isAncestorOf(ExpandableTreeNode descendant) {
			while (descendant.depth > this.depth)
				descendant = descendant.parent;
			return descendant == this;
		}

		public String toString() {
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < depth; i++)
				sb.append(" ");
			if (node != null)
				sb.append(node.toString());
			sb.append('\n');
			for (Iterator iterator = children.iterator(); iterator.hasNext();) {
				ExpandableTreeNode child = (ExpandableTreeNode) iterator.next();
				sb.append(child.toString());
			}
			return sb.toString();
		}
	}

	private GraphStructureListener structureListener = new GraphStructureListener() {

		public boolean nodeRemoved(LayoutContext context, NodeLayout node) {
			ExpandableTreeNode spaceTreeNode = (ExpandableTreeNode) layoutToTree.get(node);
			spaceTreeNode.delete();
			superRoot.precomputeTree();
			refreshLayout(true);
			return false;
		}

		public boolean nodeAdded(LayoutContext context, NodeLayout node) {
			superRoot.addChild(new ExpandableTreeNode(node));
			superRoot.precomputeTree();
			refreshLayout(true);
			return false;
		}

		public boolean connectionRemoved(LayoutContext context, ConnectionLayout connection) {
			ExpandableTreeNode node1 = (ExpandableTreeNode) layoutToTree.get(connection.getSource());
			ExpandableTreeNode node2 = (ExpandableTreeNode) layoutToTree.get(connection.getTarget());
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
			ExpandableTreeNode source = (ExpandableTreeNode) layoutToTree.get(connection.getSource());
			ExpandableTreeNode target = (ExpandableTreeNode) layoutToTree.get(connection.getTarget());
			if (source == target)
				return false;
			if (isBetterParent(target, source)) {
				target.parent.children.remove(target);
				source.addChild(target);
				superRoot.precomputeTree();
			}
			if (!connection.isDirected() && isBetterParent(source, target)) {
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
			((ExpandableTreeNode) layoutToTree.get(node)).refreshSubgraphLocation();
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

	private ExpandableTreeNode superRoot;

	private HashMap layoutToTree = new HashMap();

	public ExpandableTreeLayoutAlgorithm() {
	}

	public ExpandableTreeLayoutAlgorithm(int direction) {
		setDirection(direction);
	}

	public int getDirection() {
		return direction;
	}

	public void setDirection(int direction) {
		if (direction == this.direction)
			return;
		if (direction == TOP_DOWN || direction == BOTTOM_UP || direction == LEFT_RIGHT || direction == RIGHT_LEFT) {
			this.direction = direction;
		}
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

	public void applyLayout(boolean clean) {
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

		superRoot = new ExpandableTreeNode(null);
		createTrees(context.getNodes());
		collapseAll();
	}

	public void initExpansion(LayoutContext context) {
		// do nothing - initialization performed in #setLayoutContext()
	}

	public void setExpanded(LayoutContext context, NodeLayout node, boolean expanded) {
		ExpandableTreeNode spaceTreeNode = (ExpandableTreeNode) layoutToTree.get(node);
		spaceTreeNode.setExpanded(expanded);
		refreshLayout(false);
	}

	public boolean canExpand(LayoutContext context, NodeLayout node) {
		ExpandableTreeNode spaceTree = (ExpandableTreeNode) layoutToTree.get(node);
		if (spaceTree != null) {
			return !spaceTree.expanded && !spaceTree.children.isEmpty();
		}
		return false;
	}

	public boolean canCollapse(LayoutContext context, NodeLayout node) {
		ExpandableTreeNode spaceTree = (ExpandableTreeNode) layoutToTree.get(node);
		if (spaceTree != null) {
			return spaceTree.expanded && !spaceTree.children.isEmpty();
		}
		return false;
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
			ExpandableTreeNode root = (ExpandableTreeNode) iterator.next();
			computePositionRecursively(root, leafCountSoFar);
			leafCountSoFar = leafCountSoFar + root.numOfLeaves;
		}
	}

	private static boolean isBetterParent(ExpandableTreeNode node, ExpandableTreeNode potentialParent) {
		if (node.parent == null && !node.isAncestorOf(potentialParent))
			return true;
		if (potentialParent.depth <= node.depth && potentialParent.depth != -1)
			return true;
		if (node.parent.depth == -1 && potentialParent.depth >= 0 && !node.isAncestorOf(potentialParent))
			return true;
		return false;
	}

	/**
	 * Builds a tree structure using BFS method. Created trees are children of
	 * {@link #superRoot}.
	 * 
	 * @param nodes
	 */
	private void createTrees(NodeLayout[] nodes) {
		HashSet alreadyVisited = new HashSet();
		LinkedList nodesToAdd = new LinkedList();
		for (int i = 0; i < nodes.length; i++) {
			NodeLayout root = findRoot(nodes[i], alreadyVisited);
			if (root != null) {
				alreadyVisited.add(root);
				nodesToAdd.addLast(new Object[] { root, superRoot });
			}
		}
		while (!nodesToAdd.isEmpty()) {
			Object[] dequeued = (Object[]) nodesToAdd.removeFirst();
			ExpandableTreeNode currentNode = new ExpandableTreeNode((NodeLayout) dequeued[0]);
			ExpandableTreeNode currentRoot = (ExpandableTreeNode) dequeued[1];

			currentRoot.addChild(currentNode);
			NodeLayout[] children = currentNode.node.getSuccessingNodes();
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
	 * Searches for a root of a tree containing given node by continuously
	 * grabbing a predecessor of current node. If it reaches a node that exists
	 * in alreadyVisited set, it returns null. If it detects a cycle, it returns
	 * the first found node of that cycle. If it reaches a node that has no
	 * predecessors, it returns that node.
	 * 
	 * @param nodeLayout
	 *            starting node
	 * @param alreadyVisited
	 *            set of nodes that can't lay on path to the root (if one does,
	 *            method stops and returns null).
	 * @return
	 */
	private NodeLayout findRoot(NodeLayout nodeLayout, Set alreadyVisited) {
		HashSet alreadyVisitedRoot = new HashSet();
		while (true) {
			if (alreadyVisited.contains(nodeLayout))
				return null;
			if (alreadyVisitedRoot.contains(nodeLayout))
				return nodeLayout;
			alreadyVisitedRoot.add(nodeLayout);
			NodeLayout[] predecessingNodes = nodeLayout.getPredecessingNodes();
			if (predecessingNodes.length > 0) {
				nodeLayout = predecessingNodes[0];
			} else {
				return nodeLayout;
			}
		}
	}

	private void collapseAll() {
		for (Iterator iterator = superRoot.children.iterator(); iterator.hasNext();) {
			ExpandableTreeNode root = (ExpandableTreeNode) iterator.next();
			root.collapse();
		}
	}

	/**
	 * Computes positions recursively until the leaf nodes are reached.
	 */
	private void computePositionRecursively(ExpandableTreeNode node, int relativePosition) {
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
			ExpandableTreeNode child = (ExpandableTreeNode) iterator.next();
			computePositionRecursively(child, relativePosition);
			relativePosition += child.numOfLeaves;
		}
	}
}
