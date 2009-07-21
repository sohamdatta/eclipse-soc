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
		public double positionInLayer;

		/**
		 * This constructor should not be used. Use
		 * {@link SpaceTreeLayoutAlgorithm#createSpaceTreeNode(NodeLayout)}
		 * instead.
		 * 
		 * @param node
		 */
		private SpaceTreeNode(NodeLayout node) {
			this.node = node;
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

		public boolean isAncestorOf(SpaceTreeNode descendant) {
			while (descendant.depth > this.depth)
				descendant = descendant.parent;
			return descendant == this;
		}

		public void setSubgraph(SubgraphLayout subgraph) {
			if (this.subgraph != subgraph) {
				this.subgraph = subgraph;
				refreshSubgraphLocation();
			}
		}

		/**
		 * Moves the node back to its layer, preserving its location
		 */
		public void adjustPosition() {
			protectedNode = superRoot;

			DisplayIndependentPoint newLocation = node.getLocation();
			double newPositionInLayer = (direction == BOTTOM_UP || direction == TOP_DOWN) ? newLocation.x : newLocation.y;
			((SpaceTreeLayer) spaceTreeLayers.get(depth)).moveNode(this, newPositionInLayer, false);
			centerParentsTopDown();
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

			ArrayList nodesInThisLayer = null;
			ArrayList nodesInNextLayer = new ArrayList();
			nodesInNextLayer.add(this);
			double spaceRequiredInNextLayer = spaceRequiredForNode();
			for (int layer = 0; !nodesInNextLayer.isEmpty(); layer++) {
				requiredSpace = Math.max(requiredSpace, spaceRequiredInNextLayer);
				spaceRequiredInNextLayer = 0;

				nodesInThisLayer = nodesInNextLayer;
				nodesInNextLayer = new ArrayList();
				
				for (Iterator iterator = nodesInThisLayer.iterator(); iterator.hasNext();) {
					SpaceTreeNode node = (SpaceTreeNode) iterator.next();
					node.expanded = true;
					spaceRequiredInNextLayer += node.spaceRequiredForChildren();
					nodesInNextLayer.addAll(node.children);
				}
				spaceRequiredInNextLayer += branchGap * (nodesInThisLayer.size() - 1);

				if (spaceRequiredInNextLayer > requiredSpace && spaceRequiredInNextLayer > availableSpace && layer > (this == superRoot ? 1 : 0)) {
					for (Iterator iterator = nodesInThisLayer.iterator(); iterator.hasNext();) {
						SpaceTreeNode node = (SpaceTreeNode) iterator.next();
						node.expanded = false;
					}
					((SpaceTreeLayer) spaceTreeLayers.get(this.depth + layer + 1)).removeNodesChildren(nodesInThisLayer);
					nodesInNextLayer.clear();
					break;
				} else if (!nodesInNextLayer.isEmpty()) {
					SpaceTreeLayer childLayer = (SpaceTreeLayer) spaceTreeLayers.get(this.depth + layer + 1);
					childLayer.addNodesChildren(nodesInThisLayer);
				}
			}

			// center nodes at the bottom
			ArrayList nodesToCenter = nodesInNextLayer.isEmpty() ? (nodesInThisLayer.isEmpty() ? null : nodesInThisLayer) : nodesInNextLayer;
			if (nodesToCenter != null) {
				SpaceTreeNode firstNode = (SpaceTreeNode) nodesToCenter.get(0);
				SpaceTreeNode lastNode = ((SpaceTreeNode) nodesToCenter.get(nodesToCenter.size() - 1));
				protectedNode = commonAncestor(firstNode, lastNode);
				requiredSpace -= firstNode.spaceRequiredForNode() / 2 + firstNode.spaceRequiredForNode() / 2;
				double centerPosition = getAvailableSpace() / 2;
				((SpaceTreeLayer) spaceTreeLayers.get(lastNode.depth)).fitNodesWithinBounds(nodesToCenter, centerPosition - requiredSpace / 2,
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
					layer.moveNode(this, (firstChild.positionInLayer + lastChild.positionInLayer) / 2, false);
				}
			}
		}

		public void centerParentsTopDown() {
			if (!children.isEmpty() && expanded) {
				SpaceTreeNode firstChild = (SpaceTreeNode) children.get(0);
				SpaceTreeNode lastChild = (SpaceTreeNode) children.get(children.size() - 1);
				double offset = this.positionInLayer - (firstChild.positionInLayer + lastChild.positionInLayer) / 2;
				if (firstChild.positionInLayer - firstChild.spaceRequiredForNode() / 2 + offset < 0)
					offset = -firstChild.positionInLayer + firstChild.spaceRequiredForNode() / 2;
				double availableSpace = getAvailableSpace();
				if (lastChild.positionInLayer + lastChild.spaceRequiredForNode() / 2 + offset > availableSpace) {
					offset = availableSpace - lastChild.positionInLayer - lastChild.spaceRequiredForNode() / 2;
				}
				SpaceTreeLayer layer = (SpaceTreeLayer) spaceTreeLayers.get(depth + 1);
				layer.fitNodesWithinBounds(children, firstChild.positionInLayer + offset, lastChild.positionInLayer + offset);

				for (Iterator iterator = children.iterator(); iterator.hasNext();) {
					((SpaceTreeNode) iterator.next()).centerParentsTopDown();
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

		/**
		 * Sets locations of nodes in the graph depending on their current layer
		 * and position in layer.
		 * 
		 * @param thicknessSoFar
		 *            sum of thickness of and gaps for all layers 'above' this
		 *            node (NaN if it's unknown and should be calculated in this
		 *            method)
		 * @return true if location of at least one node has changed
		 */
		public boolean flushLocationChanges(double thicknessSoFar) {
			boolean madeChanges = false;
			if (node != null) {
				DisplayIndependentDimension nodeSize = node.getSize();
				double x = 0, y = 0;
				switch (direction) {
				case TOP_DOWN:
					x = bounds.x + positionInLayer;
					y = thicknessSoFar + nodeSize.height / 2;
					break;
				case BOTTOM_UP:
					x = bounds.x + positionInLayer;
					y = bounds.y + bounds.height - thicknessSoFar - nodeSize.height / 2;
					break;
				case LEFT_RIGHT:
					x = thicknessSoFar + nodeSize.height / 2;
					y = bounds.y + positionInLayer;
					break;
				case RIGHT_LEFT:
					x = bounds.x + bounds.width - thicknessSoFar - nodeSize.height / 2;
					y = bounds.y + positionInLayer;
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
				thicknessSoFar += (depth >= 0 ? ((SpaceTreeLayer) spaceTreeLayers.get(depth)).thickness : 0) + layerGap;
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
		public double thickness = 0;
		
		public SpaceTreeLayer(int depth) {
			this.depth = depth;
		}
		
		public void addNodesChildren(List nodes2) {
			ListIterator layerIterator = nodes.listIterator();
			SpaceTreeNode previousNode = null;
			for (Iterator iterator = nodes2.iterator(); iterator.hasNext();) {
				SpaceTreeNode node = (SpaceTreeNode) iterator.next();
				for (Iterator iterator2 = node.children.iterator(); iterator2.hasNext();) {
					SpaceTreeNode childToAdd = (SpaceTreeNode) iterator2.next();

					SpaceTreeNode nodeInLayer = null;
					while (layerIterator.hasNext()) {
						nodeInLayer = (SpaceTreeNode) layerIterator.next();
						if (nodeInLayer.order >= childToAdd.order)
							break;
						double expectedPostion = (previousNode == null) ? 0 : previousNode.positionInLayer
								+ expectedDistance(previousNode, nodeInLayer);
						nodeInLayer.positionInLayer = Math.max(nodeInLayer.positionInLayer, expectedPostion);
						previousNode = nodeInLayer;
					}

					if (nodeInLayer == null) {
						layerIterator.add(childToAdd);
					} else if (nodeInLayer.order == childToAdd.order) {
						layerIterator.set(childToAdd);
					} else {
						if (nodeInLayer.order > childToAdd.order)
							layerIterator.previous();
						layerIterator.add(childToAdd);
					}
					layerIterator.previous();
				}
			}
			// move the rest of nodes so that they don't overlap
			while (layerIterator.hasNext()) {
				SpaceTreeNode nodeInLayer = (SpaceTreeNode) layerIterator.next();
				double expectedPostion = (previousNode == null) ? 0 : previousNode.positionInLayer + expectedDistance(previousNode, nodeInLayer);
				nodeInLayer.positionInLayer = Math.max(nodeInLayer.positionInLayer, expectedPostion);
				previousNode = nodeInLayer;
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

		public void fitNodesWithinBounds(List nodes, double startPosition, double endPosition) {
			boolean extendBounds = startPosition < 0 || endPosition > getAvailableSpace();
			double position = startPosition;
			double initialStartPosition = ((SpaceTreeNode) nodes.get(0)).positionInLayer;
			double initialNodesBredth = ((SpaceTreeNode) nodes.get(nodes.size() - 1)).positionInLayer - initialStartPosition;
			Iterator iterator = nodes.iterator();
			SpaceTreeNode previousNode = (SpaceTreeNode) iterator.next();
			moveNode(previousNode, position, extendBounds);
			while (iterator.hasNext()) {
				SpaceTreeNode nextNode = (SpaceTreeNode) iterator.next();
				double initialPositionAsPercentage = (nextNode.positionInLayer - initialStartPosition) / initialNodesBredth;
				position = Math.max(position + expectedDistance(previousNode, nextNode), startPosition + initialPositionAsPercentage
						* (endPosition - startPosition));
				moveNode(nextNode, position, extendBounds);
				previousNode = nextNode;
			}
			moveNode(previousNode, endPosition, extendBounds);
		}

		public void moveNode(SpaceTreeNode node, double newPosition, boolean extendBounds) {
			double positionInLayerAtStart = node.positionInLayer;
			if (newPosition >= positionInLayerAtStart)
				moveNodeForward(node, newPosition, extendBounds);
			if (newPosition <= positionInLayerAtStart)
				moveNodeBackward(node, newPosition, extendBounds);
		}

		private void moveNodeForward(SpaceTreeNode nodeToMove, double newPosition, boolean extendBounds) {
			int nodeIndex = nodes.indexOf(nodeToMove);
			// move forward -> check space to the 'right'
			while (true) {
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
						if (protectedNode == null || (!protectedNode.isAncestorOf(nextNode) && !nextNode.parent.isAncestorOf(protectedNode))) {
							collapseNode(nextNode.parent);
							break;
						}
					}
					if (i == nodes.size()) {
						// not enough space, but we can't collapse anything...
						if (!extendBounds) {
							newPosition = getAvailableSpace() - requiredSpace;
							if (newPosition < nodeToMove.positionInLayer)
								return;
						}
						break;
					}
				} else
					break; // there is enough space
			}
			// move...
			for (int i = nodeIndex; i < nodes.size(); i++) {
				nodeToMove.positionInLayer = newPosition;
				// move parent if moved node is its first child
				if (nodeToMove.firstChild) {
					SpaceTreeNode parent = nodeToMove.parent;
					if (depth > 0 && parent.positionInLayer <= newPosition) {
						SpaceTreeLayer parentLayer = (SpaceTreeLayer) spaceTreeLayers.get(depth - 1);
						parentLayer.moveNodeForward(parent, newPosition, extendBounds);
						newPosition = nodeToMove.positionInLayer = parent.positionInLayer;
					}
				}
				// move children if necessary
				if (nodeToMove.expanded && !nodeToMove.children.isEmpty()) {
					SpaceTreeNode lastChild = (SpaceTreeNode) nodeToMove.children.get(nodeToMove.children.size() - 1);
					if (lastChild.positionInLayer < newPosition) {
						SpaceTreeNode firstChild = (SpaceTreeNode) nodeToMove.children.get(0);
						SpaceTreeLayer childLayer = (SpaceTreeLayer) spaceTreeLayers.get(depth + 1);
						double expectedDistanceBetweenChildren = nodeToMove.spaceRequiredForChildren() - firstChild.spaceRequiredForNode() / 2
								- lastChild.spaceRequiredForNode() / 2;
						childLayer.moveNodeForward(firstChild, newPosition - expectedDistanceBetweenChildren, extendBounds);
						newPosition = nodeToMove.positionInLayer = lastChild.positionInLayer;
					}
				}

				if (i < nodes.size() - 1) {
					SpaceTreeNode nextNode = (SpaceTreeNode) nodes.get(i + 1);
					newPosition += expectedDistance(nodeToMove, nextNode);
					nodeToMove = nextNode;
					if (nodeToMove.positionInLayer > newPosition)
						break;
				}
			}
		}


		private void moveNodeBackward(SpaceTreeNode nodeToMove, double newPosition, boolean extendBounds) {
			int nodeIndex = nodes.indexOf(nodeToMove);
			// move backward -> check space to the 'left'
			// move and collapse until there's enough space
			while (true) {
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
						if (protectedNode == null || (!protectedNode.isAncestorOf(nextNode) && !nextNode.parent.isAncestorOf(protectedNode))) {
							collapseNode(nextNode.parent);
							nodeIndex -= nextNode.parent.children.size();
							break;
						}
					}
					if (i == -1) {
						// not enough space, but we can't collapse anything...
						if (!extendBounds) {
							newPosition = requiredSpace;
							if (newPosition > nodeToMove.positionInLayer)
								return;
						}
						break;
					}
				} else
					break; // there is enough space
			}
			// move...
			for (int i = nodeIndex; i >= 0; i--) {
				nodeToMove.positionInLayer = newPosition;
				// move parent if moved node is its last child
				if (nodeToMove.lastChild) {
					SpaceTreeNode parent = nodeToMove.parent;
					if (depth > 0 && parent.positionInLayer >= newPosition) {
						SpaceTreeLayer parentLayer = (SpaceTreeLayer) spaceTreeLayers.get(depth - 1);
						parentLayer.moveNodeBackward(parent, newPosition, extendBounds);
						newPosition = nodeToMove.positionInLayer = parent.positionInLayer;
					}
				}
				// move children if necessary
				if (nodeToMove.expanded && !nodeToMove.children.isEmpty()) {
					SpaceTreeNode firstChild = (SpaceTreeNode) nodeToMove.children.get(0);
					if (firstChild.positionInLayer > newPosition) {
						SpaceTreeNode lastChild = (SpaceTreeNode) nodeToMove.children.get(nodeToMove.children.size() - 1);
						SpaceTreeLayer childLayer = (SpaceTreeLayer) spaceTreeLayers.get(depth + 1);
						double expectedDistanceBetweenChildren = nodeToMove.spaceRequiredForChildren() - firstChild.spaceRequiredForNode() / 2
								- lastChild.spaceRequiredForNode() / 2;
						childLayer.moveNodeBackward(lastChild, newPosition + expectedDistanceBetweenChildren, extendBounds);
						newPosition = nodeToMove.positionInLayer = firstChild.positionInLayer;
					}
				}
				if (i > 0) {
					SpaceTreeNode nextNode = (SpaceTreeNode) nodes.get(i - 1);
					newPosition -= expectedDistance(nodeToMove, nextNode);
					nodeToMove = nextNode;
					if (nodeToMove.positionInLayer < newPosition)
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
			superRoot.addChild(createSpaceTreeNode(node));
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
			boolean previousBoundsWrong = (bounds == null || bounds.width * bounds.height <= 0);
			bounds = context.getBounds();
			if (bounds.width * bounds.height > 0 && previousBoundsWrong) {
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
			SpaceTreeNode spaceTreeNode = (SpaceTreeNode) layoutToSpaceTree.get(node);
			spaceTreeNode.adjustPosition();
			superRoot.flushLocationChanges(0);
			spaceTreeNode.refreshSubgraphLocation();
			context.flushChanges(false);
			return false;
		}
	};

	private int direction = TOP_DOWN;

	private double leafGap = 15;
	private double branchGap = leafGap + 5;
	private double layerGap = 20;

	private boolean refreshOn = true;

	private LayoutContext context;

	private DisplayIndependentRectangle bounds;

	private ArrayList spaceTreeLayers = new ArrayList();

	private SpaceTreeNode protectedNode = null;

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
			throw new IllegalArgumentException("Invalid direction: " + direction);
	}

	public void applyLayout() {
		if (bounds.width * bounds.height == 0)
			return;

		superRoot = createSpaceTreeNode(null);
		createTrees(context.getNodes());
		superRoot.maximizeExpansion();
		superRoot.flushExpansionChanges();
		superRoot.flushLocationChanges(0);
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

	private SpaceTreeNode createSpaceTreeNode(NodeLayout node) {
		SpaceTreeNode spaceTreeNode = (SpaceTreeNode) layoutToSpaceTree.get(node);
		if (spaceTreeNode == null) {
			spaceTreeNode = new SpaceTreeNode(node);
			layoutToSpaceTree.put(node, spaceTreeNode);
		}
		spaceTreeNode.children.clear();
		spaceTreeNode.subgraph = null;
		return spaceTreeNode;
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
			SpaceTreeNode currentNode = createSpaceTreeNode((NodeLayout) dequeued[0]);
			SpaceTreeNode currentRoot = (SpaceTreeNode) dequeued[1];

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
	 * grabbing a predecessor of current node. If it reaches an node that exists
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

	private double getAvailableSpace() {
		return (direction == TOP_DOWN || direction == BOTTOM_UP) ? bounds.width : bounds.height;
	}

	private SpaceTreeNode commonAncestor(SpaceTreeNode node1, SpaceTreeNode node2) {
		while (node1.depth < node2.depth)
			node1 = node1.parent;
		while (node2.depth < node1.depth)
			node2 = node2.parent;
		while (node1 != node2 && node1 != null && node2 != null) {
			node1 = node1.parent;
			node2 = node2.parent;
		}
		return node1;
	}
}
