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
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
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

	private class SpaceTreeNode {
		final public NodeLayout node;
		public int height = 0;
		public int depth = -1;
		public int numOfLeaves = 0;
		public int order = 0;
		public final List children = new ArrayList();
		public SpaceTreeNode parent;
		public boolean firstChild = false, lastChild = false;
		public SubgraphLayout subgraph = null;

		public boolean expanded = false;
		public double positionInLevel;

		public SpaceTreeNode(NodeLayout node) {
			this.node = node;
			if (node != null)
				layoutToSpaceTree.put(node, this);
		}

		public void addChild(SpaceTreeNode child) {
			children.add(child);
			child.parent = this;
			child.setSubgraph(null);

			if (this == superRoot || subgraph == null) {
				child.node.prune(null);
			} else {
				SubgraphLayout destinationSubgraph = (node.isPruned()) ? node.getSubgraph() : subgraph;
				child.collapseAllChildrenIntoSubgraph(destinationSubgraph, true);
			}
		}

		public void precomputeTree() {
			if (children.isEmpty()) {
				numOfLeaves = 1;
				height = 0;
			} else {
				numOfLeaves = 0;
				height = 0;
				for (ListIterator iterator = children.listIterator(); iterator.hasNext();) {
					SpaceTreeNode child = (SpaceTreeNode) iterator.next();
					if (child.depth != this.depth + 1) {
						if (child.depth >= 0)
							((SpaceTreeLayer) spaceTreeLayers.get(child.depth)).removeNode(child);
						child.depth = this.depth + 1;
					}
					child.precomputeTree();
					child.order = this.order + this.numOfLeaves;
					child.firstChild = (this.numOfLeaves == 0);
					child.lastChild = !iterator.hasNext();

					this.numOfLeaves += child.numOfLeaves;
					this.height = Math.max(this.height, child.height + 1);
				}
			}
			if (this == superRoot) {
				expanded = true;
				while (spaceTreeLayers.size() <= this.height)
					spaceTreeLayers.add(new SpaceTreeLayer(spaceTreeLayers.size()));
			}
		}

		public void delete() {
			parent.children.remove(this);
			for (Iterator iterator = children.iterator(); iterator.hasNext();) {
				SpaceTreeNode child = (SpaceTreeNode) iterator.next();
				superRoot.addChild(child);
			}
		}

		public SubgraphLayout collapseAllChildrenIntoSubgraph(SubgraphLayout subgraph, boolean includeYourself) {
			ArrayList allChildren = new ArrayList();
			LinkedList nodesToVisit = new LinkedList();
			nodesToVisit.addLast(this);
			while (!nodesToVisit.isEmpty()) {
				SpaceTreeNode currentNode = (SpaceTreeNode) nodesToVisit.removeFirst();
				for (Iterator iterator = currentNode.children.iterator(); iterator.hasNext();) {
					SpaceTreeNode child = (SpaceTreeNode) iterator.next();
					allChildren.add(child.node);
					child.setSubgraph(null);
					nodesToVisit.addLast(child);
				}
			}
			if (includeYourself)
				allChildren.add(this.node);
			if (allChildren.isEmpty()) {
				setSubgraph(null);
				return null;
			}
			NodeLayout[] childrenArray = (NodeLayout[]) allChildren.toArray(new NodeLayout[allChildren.size()]);
			if (subgraph == null) {
				subgraph = context.createSubgraph(childrenArray);
			} else {
				subgraph.addNodes(childrenArray);
			}
			if (!includeYourself)
				setSubgraph(subgraph);
			return subgraph;
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
			if (this.subgraph != subgraph) {
				this.subgraph = subgraph;
				refreshSubgraphLocation();
			}
		}

		public void refreshSubgraphLocation() {
			if (subgraph != null && subgraph.isGraphEntity()) {
				DisplayIndependentPoint nodeLocation = node.getLocation();
				DisplayIndependentDimension nodeSize = node.getSize();
				DisplayIndependentDimension subgraphSize = subgraph.getSize();
				double x = 0, y = 0;
				switch (direction) {
				case TOP_DOWN:
					x = nodeLocation.x;
					y = nodeLocation.y + (nodeSize.height + subgraphSize.height) / 2;
					break;
				case BOTTOM_UP:
					x = nodeLocation.x;
					y = nodeLocation.y - (nodeSize.height + subgraphSize.height) / 2;
					break;
				case LEFT_RIGHT:
					x = nodeLocation.x + (nodeSize.width + subgraphSize.width) / 2;
					y = nodeLocation.y;
					break;
				case RIGHT_LEFT:
					x = nodeLocation.x - (nodeSize.width + subgraphSize.height) / 2;
					y = nodeLocation.y;
					break;
				}
				subgraph.setLocation(x, y);
			}
			((SpaceTreeLayer) spaceTreeLayers.get(depth)).refreshThickness();
		}

		public double spaceRequiredForNode() {
			if (node == null)
				return 0;
			switch (direction) {
			case TOP_DOWN:
			case BOTTOM_UP:
				return node.getSize().width;
			case LEFT_RIGHT:
			case RIGHT_LEFT:
				return node.getSize().height;
			}
			throw new RuntimeException("invalid direction");
		}

		public double spaceRequiredForChildren() {
			double result = 0;
			for (Iterator iterator = children.iterator(); iterator.hasNext();) {
				SpaceTreeNode child = (SpaceTreeNode) iterator.next();
				result += child.spaceRequiredForNode();
			}
			result += leafGap * (children.size() - 1);
			return result;
		}

		public void maximizeExpansion() {
			double availableSpace = getAvailableSpace();
			double requiredSpace = 0;

			ArrayList nodesInThisLevel = null;
			ArrayList nodesInNextLevel = new ArrayList();
			nodesInNextLevel.add(this);
			double spaceRequiredInNextLevel = spaceRequiredForNode();
			for (int level = 0; !nodesInNextLevel.isEmpty(); level++) {
				requiredSpace = Math.max(requiredSpace, spaceRequiredInNextLevel);
				spaceRequiredInNextLevel = 0;

				nodesInThisLevel = nodesInNextLevel;
				nodesInNextLevel = new ArrayList();
				
				for (Iterator iterator = nodesInThisLevel.iterator(); iterator.hasNext();) {
					SpaceTreeNode node = (SpaceTreeNode) iterator.next();
					node.expanded = true;
					spaceRequiredInNextLevel += node.spaceRequiredForChildren();
					nodesInNextLevel.addAll(node.children);
				}
				spaceRequiredInNextLevel += branchGap * (nodesInThisLevel.size() - 1);

				if (spaceRequiredInNextLevel > requiredSpace && spaceRequiredInNextLevel > availableSpace && level > (this == superRoot ? 2 : 1)) {
					for (Iterator iterator = nodesInThisLevel.iterator(); iterator.hasNext();) {
						SpaceTreeNode node = (SpaceTreeNode) iterator.next();
						node.expanded = false;
					}
					((SpaceTreeLayer) spaceTreeLayers.get(this.depth + level + 1)).removeNodesChildren(nodesInThisLevel);
					nodesInNextLevel.clear();
					level--;
					break;
				} else if (!nodesInNextLevel.isEmpty()) {
					SpaceTreeLayer childLayer = (SpaceTreeLayer) spaceTreeLayers.get(this.depth + level + 1);
					childLayer.addNodesChildren(nodesInThisLevel);
				}
			}

			// center nodes at the bottom
			ArrayList nodesToCenter = nodesInNextLevel.isEmpty() ? (nodesInThisLevel.isEmpty() ? null : nodesInThisLevel) : nodesInNextLevel;
			if (nodesToCenter != null) {
				protectedNodesStart = this.order;
				SpaceTreeNode lastNode = ((SpaceTreeNode) nodesToCenter.get(nodesToCenter.size() - 1));
				protectedNodesEnd = lastNode.order;
				double centerPosition = getAvailableSpace() / 2;
				((SpaceTreeLayer) spaceTreeLayers.get(lastNode.depth)).fitWithinBounds(nodesToCenter, centerPosition - requiredSpace / 2,
						centerPosition + requiredSpace / 2);
			}

			centerParentsBottomUp();
		}

		public void centerParentsBottomUp() {
			if (!children.isEmpty() && expanded) {
				for (Iterator iterator = children.iterator(); iterator.hasNext();) {
					((SpaceTreeNode) iterator.next()).centerParentsBottomUp();
				}

				if (depth >= 0) {
					SpaceTreeNode firstChild = (SpaceTreeNode) children.get(0);
					SpaceTreeNode lastChild = (SpaceTreeNode) children.get(children.size() - 1);
					SpaceTreeLayer layer = (SpaceTreeLayer) spaceTreeLayers.get(depth);
					layer.moveNode(this, (firstChild.positionInLevel + lastChild.positionInLevel) / 2, false);
				}
			}
		}

		public void flushExpansionChanges() {
			if (node != null)
				node.prune(null);
			if (this.expanded) {
				setSubgraph(null);
				for (Iterator iterator = children.iterator(); iterator.hasNext();) {
					((SpaceTreeNode) iterator.next()).flushExpansionChanges();
				}
			}
			if (!this.expanded && subgraph == null) {
				collapseAllChildrenIntoSubgraph(null, false);
			}
		}

		public boolean flushCollapseChanges() {
			if (!expanded) {
				int numberOfChildrenInSubgraph = subgraph == null ? 0 : subgraph.countNodes();
				collapseAllChildrenIntoSubgraph(subgraph, false);
				return numberOfChildrenInSubgraph != (subgraph == null ? 0 : subgraph.countNodes());
			}
			if (expanded && subgraph == null) {
				boolean madeChagnes = false;
				for (Iterator iterator = children.iterator(); iterator.hasNext();) {
					madeChagnes = ((SpaceTreeNode) iterator.next()).flushCollapseChanges() || madeChagnes;
				}
				return madeChagnes;
			}
			return false;
		}

		public boolean flushLocationChanges(double thicknessSoFar) {
			boolean madeChanges = false;
			if (node != null) {
				DisplayIndependentDimension nodeSize = node.getSize();
				double x = 0, y = 0;
				switch (direction) {
				case TOP_DOWN:
					x = bounds.x + positionInLevel;
					y = thicknessSoFar + nodeSize.height / 2;
					break;
				case BOTTOM_UP:
					x = bounds.x + positionInLevel;
					y = bounds.y + bounds.height - thicknessSoFar - nodeSize.height / 2;
					break;
				case LEFT_RIGHT:
					x = thicknessSoFar + nodeSize.height / 2;
					y = bounds.y + positionInLevel;
					break;
				case RIGHT_LEFT:
					x = bounds.x + bounds.width - thicknessSoFar + nodeSize.height / 2;
					y = bounds.y + positionInLevel;
					break;
				}
				DisplayIndependentPoint currentLocation = node.getLocation();
				if (currentLocation.x != x || currentLocation.y != y) {
					node.setLocation(x, y);
					refreshSubgraphLocation();
					madeChanges = true;
				}
			}
			if (expanded && subgraph == null) {
				thicknessSoFar += (depth >= 0 ? ((SpaceTreeLayer) spaceTreeLayers.get(depth)).thickness : 0) + levelGap;
				for (Iterator iterator = children.iterator(); iterator.hasNext();) {
					SpaceTreeNode child = (SpaceTreeNode) iterator.next();
					madeChanges = child.flushLocationChanges(thicknessSoFar) || madeChanges;
				}
			}
			return madeChanges;
		}

		public String toString() {
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < depth; i++)
				sb.append(" ");
			if (node != null)
				sb.append(node.toString());
			sb.append("|" + this.order);
			sb.append('\n');
			for (Iterator iterator = children.iterator(); iterator.hasNext();) {
				SpaceTreeNode child = (SpaceTreeNode) iterator.next();
				sb.append(child.toString());
			}
			return sb.toString();
		}
	}

	private class SpaceTreeLayer {
		public ArrayList nodes = new ArrayList();
		private final int depth;
		private double thickness = 0;
		
		public SpaceTreeLayer(int depth) {
			this.depth = depth;
		}
		
		public void addNodesChildren(List nodes2) {
			ListIterator layerIterator = nodes.listIterator();
			for (Iterator iterator = nodes2.iterator(); iterator.hasNext();) {
				SpaceTreeNode node = (SpaceTreeNode) iterator.next();
				for (Iterator iterator2 = node.children.iterator(); iterator2.hasNext();) {
					SpaceTreeNode childToAdd = (SpaceTreeNode) iterator2.next();

					SpaceTreeNode nodeInLayer = null;
					while (layerIterator.hasNext()) {
						nodeInLayer = (SpaceTreeNode) layerIterator.next();
						if (nodeInLayer.order >= childToAdd.order)
							break;
					}

					if (nodeInLayer == null)
						layerIterator.add(childToAdd);
					else if (nodeInLayer.order == childToAdd.order)
						layerIterator.set(childToAdd);
					else {
						if (nodeInLayer.order > childToAdd.order)
							layerIterator.previous();
						layerIterator.add(childToAdd);
					}
				}
			}
			refreshThickness();
		}
		
		public void removeNode(SpaceTreeNode node) {
			if (nodes.remove(node))
				refreshThickness();
		}

		public void removeNodesChildren(List parentNodes) {
			boolean changed = false;
			for (Iterator iterator = parentNodes.iterator(); iterator.hasNext();) {
				SpaceTreeNode node = (SpaceTreeNode) iterator.next();
				changed = nodes.removeAll(node.children) || changed;
				if (!node.children.isEmpty())
					((SpaceTreeLayer) spaceTreeLayers.get(depth + 1)).removeNodesChildren(node.children);
			}
			if (changed)
				refreshThickness();
		}

		public void checkThickness(SpaceTreeNode node) {
			if (node.depth != this.depth)
				throw new RuntimeException();
			double nodeThickness = 0;
			DisplayIndependentDimension size = node.node.getSize();
			nodeThickness = (direction == TOP_DOWN || direction == BOTTOM_UP) ? size.height : size.width;
			if (node.subgraph != null && node.subgraph.isGraphEntity()) {
				size = node.subgraph.getSize();
				nodeThickness += (direction == TOP_DOWN || direction == BOTTOM_UP) ? size.height : size.width;
			}
			this.thickness = Math.max(this.thickness, nodeThickness);
		}

		public void refreshThickness() {
			this.thickness = 0;
			for (Iterator iterator = nodes.iterator(); iterator.hasNext();) {
				checkThickness((SpaceTreeNode) iterator.next());
			}
		}

		public void fitWithinBounds(List nodesToFit, double start, double end) {
			SpaceTreeNode startNode = (SpaceTreeNode) nodesToFit.get(0);

			SpaceTreeNode endNode = (SpaceTreeNode) nodesToFit.get(nodesToFit.size() - 1);
			
			moveNode(startNode, start + startNode.spaceRequiredForNode() / 2, start < 0 || end > getAvailableSpace());
			moveNode(endNode, end - endNode.spaceRequiredForNode() / 2, start < 0 || end > getAvailableSpace());
		}

		public void moveNode(SpaceTreeNode node, double newPosition, boolean extendBounds) {
			if (newPosition >= node.positionInLevel)
				moveNodeForward(node, newPosition, extendBounds);
			if (newPosition <= node.positionInLevel)
				moveNodeBackward(node, newPosition, extendBounds);
		}

		private void moveNodeForward(SpaceTreeNode nodeToMove, double newPosition, boolean extendBounds) {
			int nodeIndex = nodes.indexOf(nodeToMove);
			// move forward -> check neighbors to the right
			while (nodeIndex < nodes.size() - 1) {
				double requiredSpace = 0;
				SpaceTreeNode previousNode = nodeToMove;
				for (int i = nodeIndex + 1; i < nodes.size(); i++) {
					SpaceTreeNode nextNode = (SpaceTreeNode) nodes.get(i);
					requiredSpace += expectedDistance(previousNode, nextNode);
					previousNode = nextNode;
				}
				requiredSpace += previousNode.spaceRequiredForNode() / 2;
				if (requiredSpace > getAvailableSpace() - newPosition) {
					// find nodes to remove
					int i = nodeIndex + 1;
					for (; i < nodes.size(); i++) {

						SpaceTreeNode nextNode = ((SpaceTreeNode) nodes.get(i));
						if (nextNode.order > protectedNodesEnd) {
							collapseNode(nextNode.parent);

							break;
						}
					}
					if (i == nodes.size()) {

						// not enough space, but we can't collapse anything...
						if (!extendBounds) {
							newPosition = getAvailableSpace() - requiredSpace;
							if (newPosition < nodeToMove.positionInLevel)
								return;
						}
						break;
					}
				} else
					break; // there is enough space
			}
			// move...
			for (int i = nodeIndex; i < nodes.size(); i++) {
				nodeToMove.positionInLevel = newPosition;
				// move parent if moved node is its first child
				if (nodeToMove.firstChild) {
					SpaceTreeNode parent = nodeToMove.parent;
					if (depth > 0 && parent.positionInLevel <= newPosition) {
						SpaceTreeLayer parentLayer = (SpaceTreeLayer) spaceTreeLayers.get(depth - 1);
						parentLayer.moveNodeForward(parent, newPosition, extendBounds);
						newPosition = nodeToMove.positionInLevel = parent.positionInLevel;
					}
				}
				// move children if necessary
				if (nodeToMove.expanded && !nodeToMove.children.isEmpty()) {
					SpaceTreeNode lastChild = (SpaceTreeNode) nodeToMove.children.get(nodeToMove.children.size() - 1);
					if (lastChild.positionInLevel < newPosition) {
						SpaceTreeNode firstChild = (SpaceTreeNode) nodeToMove.children.get(0);
						SpaceTreeLayer childLayer = (SpaceTreeLayer) spaceTreeLayers.get(depth + 1);
						double expectedDistanceBetweenChildren = nodeToMove.spaceRequiredForChildren() - firstChild.spaceRequiredForNode() / 2
								- lastChild.spaceRequiredForNode() / 2;
						childLayer.moveNodeForward(firstChild, newPosition - expectedDistanceBetweenChildren, extendBounds);
						newPosition = nodeToMove.positionInLevel = lastChild.positionInLevel;
					}
				}

				if (i < nodes.size() - 1) {

					SpaceTreeNode nextNode = (SpaceTreeNode) nodes.get(i + 1);
					newPosition += expectedDistance(nodeToMove, nextNode);
					nodeToMove = nextNode;
					if (nodeToMove.positionInLevel > newPosition)
						break;
				}
			}
		}


		private void moveNodeBackward(SpaceTreeNode nodeToMove, double newPosition, boolean extendBounds) {
			int nodeIndex = nodes.indexOf(nodeToMove);
			// move backward -> check neighbors to the left
			while (nodeIndex > 0) {
				double requiredSpace = 0;
				SpaceTreeNode previousNode = nodeToMove;
				for (int i = nodeIndex - 1; i >= 0; i--) {
					SpaceTreeNode nextNode = (SpaceTreeNode) nodes.get(i);
					requiredSpace += expectedDistance(previousNode, nextNode);
					previousNode = nextNode;
				}
				requiredSpace += previousNode.spaceRequiredForNode() / 2;
				if (requiredSpace > newPosition) {
					// find nodes to remove
					int i = nodeIndex - 1;

					for (; i >= 0; i--) {
						SpaceTreeNode nextNode = ((SpaceTreeNode) nodes.get(i));
						if (nextNode.order < protectedNodesStart) {
							collapseNode(nextNode.parent);
							nodeIndex -= nextNode.parent.children.size();
							break;
						}
					}

					if (i == -1) {
						// not enough space, but we can't collapse anything...
						if (!extendBounds) {
							newPosition = requiredSpace;
							if (newPosition > nodeToMove.positionInLevel)
								return;
						}
						break;
					}
				} else
					break; // there is enough space
			}
			// move...
			for (int i = nodeIndex; i >= 0; i--) {
				nodeToMove.positionInLevel = newPosition;
				// move parent if moved node is its last child
				if (nodeToMove.lastChild) {
					SpaceTreeNode parent = nodeToMove.parent;
					if (depth > 0 && parent.positionInLevel >= newPosition) {
						SpaceTreeLayer parentLayer = (SpaceTreeLayer) spaceTreeLayers.get(depth - 1);
						parentLayer.moveNodeBackward(parent, newPosition, extendBounds);
						newPosition = nodeToMove.positionInLevel = parent.positionInLevel;
					}
				}
				// move children if necessary
				if (nodeToMove.expanded && !nodeToMove.children.isEmpty()) {
					SpaceTreeNode firstChild = (SpaceTreeNode) nodeToMove.children.get(0);
					if (firstChild.positionInLevel > newPosition) {
						SpaceTreeNode lastChild = (SpaceTreeNode) nodeToMove.children.get(nodeToMove.children.size() - 1);
						SpaceTreeLayer childLayer = (SpaceTreeLayer) spaceTreeLayers.get(depth + 1);
						double expectedDistanceBetweenChildren = nodeToMove.spaceRequiredForChildren() - firstChild.spaceRequiredForNode() / 2
								- lastChild.spaceRequiredForNode() / 2;
						childLayer.moveNodeBackward(lastChild, newPosition + expectedDistanceBetweenChildren, extendBounds);
						newPosition = nodeToMove.positionInLevel = firstChild.positionInLevel;
					}
				}


				if (i > 0) {
					SpaceTreeNode nextNode = (SpaceTreeNode) nodes.get(i - 1);
					newPosition -= expectedDistance(nodeToMove, nextNode);
					nodeToMove = nextNode;
					if (nodeToMove.positionInLevel < newPosition)
						break;
				}
			}
		}

		private double expectedDistance(SpaceTreeNode node, SpaceTreeNode neighbor) {
			double expectedDistance = (node.spaceRequiredForNode() + neighbor.spaceRequiredForNode()) / 2;
			expectedDistance += (node.parent == neighbor.parent) ? leafGap : branchGap;
			return expectedDistance;
		}

		public String toString() {
			StringBuffer buffer = new StringBuffer();
			buffer.append("Layer ").append(depth).append(": ");
			for (Iterator iterator = nodes.iterator(); iterator.hasNext();) {
				SpaceTreeNode node = (SpaceTreeNode) iterator.next();
				buffer.append(node.node).append(", ");
			}
			return buffer.toString();
		}

		private void collapseNode(SpaceTreeNode node) {
			node.expanded = false;
			SpaceTreeLayer layer = (SpaceTreeLayer) spaceTreeLayers.get(node.depth + 1);
			layer.removeNodesChildren(Arrays.asList(new Object[] { node }));
			for (Iterator iterator = node.children.iterator(); iterator.hasNext();) {
				SpaceTreeNode child = (SpaceTreeNode) iterator.next();
				if (child.expanded)
					collapseNode(child);
			}
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
			bounds = context.getBounds();
			if (bounds.width * bounds.height > 0) {
				superRoot.maximizeExpansion();
				refreshLayout(false);
			}
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

	private double leafGap = 15;
	private double branchGap = leafGap + 5;
	private double levelGap = 20;

	private boolean refreshOn = true;

	private LayoutContext context;

	private DisplayIndependentRectangle bounds;

	private ArrayList spaceTreeLayers = new ArrayList();

	private int protectedNodesStart = Integer.MAX_VALUE, protectedNodesEnd = -1;

	private SpaceTreeNode superRoot;

	private HashMap layoutToSpaceTree = new HashMap();

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

	public void applyLayout() {

		internalApplyLayout();
	}

	public void setLayoutContext(LayoutContext context) {
		if (this.context != null) {
			this.context.setExpandCollapseManager(null);
			this.context.removeGraphStructureListener(structureListener);
			this.context.removeContextListener(contextListener);
			this.context.removeLayoutListener(layoutListener);
		}
		this.context = context;
		context.setExpandCollapseManager(this);
		context.addGraphStructureListener(structureListener);
		context.addContextListener(contextListener);
		context.addLayoutListener(layoutListener);

		bounds = context.getBounds();
		superRoot = new SpaceTreeNode(null);
		createTrees(context.getNodes());
	}

	public void setExpanded(NodeLayout node, boolean expanded) {
		SpaceTreeNode spaceTreeNode = (SpaceTreeNode) layoutToSpaceTree.get(node);
		if (expanded) {
			spaceTreeNode.maximizeExpansion();
			refreshLayout(true);
		} else if (spaceTreeNode.expanded) {
			spaceTreeNode.expanded = false;
			((SpaceTreeLayer) spaceTreeLayers.get(spaceTreeNode.depth + 1)).removeNodesChildren(Arrays.asList(new Object[] { spaceTreeNode }));
			refreshLayout(true);
		}
	}

	protected void refreshLayout(boolean animation) {
		if (refreshOn) {
			if (animation && superRoot.flushCollapseChanges())
				context.flushChanges(true);
			if (superRoot.flushLocationChanges(0) && animation)
				context.flushChanges(true);
			superRoot.flushExpansionChanges();
			superRoot.flushLocationChanges(0);
			context.flushChanges(animation);
		}
	}

	void internalApplyLayout() {
		if (bounds.width * bounds.height == 0)
			return;

		superRoot.maximizeExpansion();
		superRoot.flushExpansionChanges();
		superRoot.flushLocationChanges(0);
	}

	private boolean isBetterParent(SpaceTreeNode base, SpaceTreeNode better) {
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

	private double getAvailableSpace() {
		return (direction == TOP_DOWN || direction == BOTTOM_UP) ? bounds.width : bounds.height;
	}
}
