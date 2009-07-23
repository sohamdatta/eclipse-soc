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
							// child has changed its depth, so it should be
							// removed from its previous layer
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
			if (children.isEmpty())
				return 0;
			double result = 0;
			for (Iterator iterator = children.iterator(); iterator.hasNext();) {
				SpaceTreeNode child = (SpaceTreeNode) iterator.next();
				result += child.spaceRequiredForNode();
			}
			result += leafGap * (children.size() - 1);
			return result;
		}

		public void maximizeExpansion() {
			protectedNode = this;
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

				int numOfNodesWithChildren = 0;
				for (Iterator iterator = nodesInThisLayer.iterator(); iterator.hasNext();) {
					SpaceTreeNode node = (SpaceTreeNode) iterator.next();
					if (!node.children.isEmpty()) {
						node.expanded = true;
						spaceRequiredInNextLayer += node.spaceRequiredForChildren();
						nodesInNextLayer.addAll(node.children);
						numOfNodesWithChildren++;
					}
				}

				if (numOfNodesWithChildren == 0)
					break;

				spaceRequiredInNextLayer += branchGap * (numOfNodesWithChildren - 1);

				boolean addedNewLayer = false;
				if ((spaceRequiredInNextLayer <= requiredSpace || spaceRequiredInNextLayer <= availableSpace || layer < (this == superRoot ? 2 : 1))
						&& !nodesInNextLayer.isEmpty()) {
					// add next layer and center its nodes
					NodeSnapshot[][] snapShot = takeSnapShot();
					SpaceTreeLayer childLayer = (SpaceTreeLayer) spaceTreeLayers.get(this.depth + layer + 1);
					childLayer.addNodes(nodesInNextLayer);
					SpaceTreeNode firstChild = ((SpaceTreeNode) nodesInNextLayer.get(0));
					SpaceTreeNode lastChild = ((SpaceTreeNode) nodesInNextLayer.get(nodesInNextLayer.size() - 1));
					double boundsWidth = spaceRequiredInNextLayer - firstChild.spaceRequiredForNode() / 2 - lastChild.spaceRequiredForNode() / 2;
					boundsWidth = Math.max(boundsWidth, lastChild.positionInLayer - firstChild.positionInLayer);
					childLayer.fitNodesWithinBounds(nodesInNextLayer, (availableSpace - boundsWidth) / 2, (availableSpace + boundsWidth) / 2);
					if (layer >= (this == superRoot ? 2 : 1) && !childrenPositionsOK(nodesInThisLayer)) {
						revertToShanpshot(snapShot);
					} else
						addedNewLayer = true;
				}
				if (!addedNewLayer) {
					// not enough space -> collapse nodes in this level, clean
					// up and stop
					for (Iterator iterator = nodesInThisLayer.iterator(); iterator.hasNext();) {
						SpaceTreeNode node = (SpaceTreeNode) iterator.next();
						node.expanded = false;
					}
					((SpaceTreeLayer) spaceTreeLayers.get(this.depth + layer + 1)).removeNodes(nodesInNextLayer);
					break;
				}
			}
			centerParentsBottomUp();
			centerParentsTopDown();
		}

		/**
		 * Checks if nodes in given list have proper positions according to
		 * their children (a parent's position cannot be smaller than its first
		 * child's nor bigger than its last child's position).
		 * 
		 * @param nodesToCheck
		 * @return true if all locations are correct or could be corrected while
		 *         checking.
		 */
		public boolean childrenPositionsOK(ArrayList nodesToCheck) {
			for (Iterator iterator = nodesToCheck.iterator(); iterator.hasNext();) {
				SpaceTreeNode node = (SpaceTreeNode) iterator.next();
				if (node.children.isEmpty())
					continue;
				SpaceTreeNode child = ((SpaceTreeNode) node.children.get(0));
				if (child.positionInLayer > node.positionInLayer) {
					((SpaceTreeLayer) spaceTreeLayers.get(node.depth)).moveNode(node, child.positionInLayer, false);
					if (child.positionInLayer > node.positionInLayer) {
						((SpaceTreeLayer) spaceTreeLayers.get(child.depth)).moveNode(child, node.positionInLayer, false);
						if (child.positionInLayer > node.positionInLayer) {
							return false;
						}
					}
				}
				child = ((SpaceTreeNode) node.children.get(node.children.size() - 1));
				if (child.positionInLayer < node.positionInLayer) {
					((SpaceTreeLayer) spaceTreeLayers.get(node.depth)).moveNode(node, child.positionInLayer, false);
					if (child.positionInLayer < node.positionInLayer) {
						((SpaceTreeLayer) spaceTreeLayers.get(child.depth)).moveNode(child, node.positionInLayer, false);
						if (child.positionInLayer < node.positionInLayer) {
							return false;
						}
					}
				}
			}
			return true;
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
			if (this == superRoot) {
				this.positionInLayer = getAvailableSpace() / 2;
			}
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
		 *            sum of thicknesses and gaps for all layers 'above' this
		 *            node (should be 0 if called on superRoot)
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

		public void addNodes(List nodesToAdd) {
			ListIterator layerIterator = nodes.listIterator();
			SpaceTreeNode previousNode = null;
			for (Iterator iterator = nodesToAdd.iterator(); iterator.hasNext();) {
				SpaceTreeNode nodeToAdd = (SpaceTreeNode) iterator.next();

				SpaceTreeNode nodeInLayer = null;
				while (layerIterator.hasNext()) {
					nodeInLayer = (SpaceTreeNode) layerIterator.next();
					if (nodeInLayer.order >= nodeToAdd.order)
						break;
					double expectedPostion = (previousNode == null) ? 0 : previousNode.positionInLayer + expectedDistance(previousNode, nodeInLayer);
					nodeInLayer.positionInLayer = Math.max(nodeInLayer.positionInLayer, expectedPostion);
					previousNode = nodeInLayer;
				}

				if (nodeInLayer == null) {
					layerIterator.add(nodeToAdd);
				} else if (nodeInLayer.order == nodeToAdd.order) {
					layerIterator.set(nodeToAdd);
				} else {
					if (nodeInLayer.order > nodeToAdd.order)
						layerIterator.previous();
					layerIterator.add(nodeToAdd);
				}
				layerIterator.previous();
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

		public void removeNodes(List nodesToRemove) {
			if (this.nodes.removeAll(nodesToRemove)) {
				SpaceTreeLayer nextLayer = ((SpaceTreeLayer) spaceTreeLayers.get(depth + 1));
				for (Iterator iterator = nodesToRemove.iterator(); iterator.hasNext();) {
					SpaceTreeNode nodeToRemove = (SpaceTreeNode) iterator.next();
					nextLayer.removeNodes(nodeToRemove.children);
				}
				refreshThickness();
			}
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

		public void fitNodesWithinBounds(List nodeList, double startPosition, double endPosition) {
			NodeSnapshot[][] snapShot = takeSnapShot();
			SpaceTreeNode[] nodes = (SpaceTreeNode[]) nodeList.toArray(new SpaceTreeNode[nodeList.size()]);
			double initialStartPosition = nodes[0].positionInLayer;
			double initialNodesBredth = nodes[nodes.length - 1].positionInLayer - initialStartPosition;
			double[] desiredPositions = new double[nodes.length];
			// calculate desired positions for every node, regarding their
			// initial initial proportions
			for (int i = 0; i < nodes.length; i++) {
				double initialPositionAsPercent = (initialNodesBredth > 0) ? (nodes[i].positionInLayer - initialStartPosition) / initialNodesBredth
						: 0;
				desiredPositions[i] = initialPositionAsPercent * (endPosition - startPosition);
			}
			// make sure there's proper distance between each pair of
			// consecutive nodes
			for (int i = 1; i < nodes.length; i++) {
				SpaceTreeNode node = nodes[i];
				SpaceTreeNode previousNode = nodes[i - 1];
				double expectedDistance = expectedDistance(previousNode, node);
				if (desiredPositions[i] - desiredPositions[i - 1] < expectedDistance) {
					desiredPositions[i] = desiredPositions[i - 1] + expectedDistance;
				}
			}
			// if the above operation caused some nodes to fall out of requested
			// bounds, push them back
			if (desiredPositions[nodes.length - 1] > (endPosition - startPosition)) {
				desiredPositions[nodes.length - 1] = (endPosition - startPosition);
				for (int i = nodes.length - 1; i > 0; i--) {
					SpaceTreeNode node = nodes[i];
					SpaceTreeNode previousNode = nodes[i - 1];
					double expectedDistance = expectedDistance(previousNode, node);
					if (desiredPositions[i] - desiredPositions[i - 1] < expectedDistance) {
						desiredPositions[i - 1] = desiredPositions[i] - expectedDistance;
					} else
						break;
				}
			}
			boolean extendBounds = startPosition < 0 || endPosition > getAvailableSpace();

			for (int i = 0; i < nodeList.size(); i++) {
				SpaceTreeNode node = (SpaceTreeNode) nodeList.get(i);
				double desiredPosition = startPosition + desiredPositions[i];
				moveNode(node, desiredPosition, extendBounds);
				if (Math.abs(node.positionInLayer - desiredPosition) > 0.5) {
					startPosition += (node.positionInLayer - desiredPosition);
					i = -1;
					revertToShanpshot(snapShot);
				}
			}
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
			if (nodeIndex == -1)
				throw new IllegalArgumentException("node not on this layer");
			// move forward -> check space to the 'right'
			NodeSnapshot[][] snapShot = takeSnapShot();
			mainLoop: while (nodeToMove.positionInLayer < newPosition) {
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
					boolean removed = false;
					for (int i = nodeIndex; i < nodes.size(); i++) {
						SpaceTreeNode nextNode = ((SpaceTreeNode) nodes.get(i));
						if (protectedNode == null || (!protectedNode.isAncestorOf(nextNode) && !nextNode.parent.isAncestorOf(protectedNode))) {
							collapseNode(nextNode.parent);
							if (nextNode.parent == nodeToMove.parent)
								break mainLoop;
							removed = true;
							break;
						}
					}
					if (!removed && !extendBounds) {
						// not enough space, but we can't collapse anything...
						newPosition = getAvailableSpace() - requiredSpace;
						revertToShanpshot(snapShot);
						continue mainLoop;
					}
				}
				// move the node and all its neighbors to the 'right'
				SpaceTreeNode currentNodeToMove = nodeToMove;
				double newPositionForCurrent = newPosition;
				for (int i = nodeIndex; i < nodes.size(); i++) {
					currentNodeToMove.positionInLayer = newPositionForCurrent;
					// move parent if moved node is its first child
					if (currentNodeToMove.firstChild) {
						SpaceTreeNode parent = currentNodeToMove.parent;
						if (depth > 0 && parent.positionInLayer <= newPositionForCurrent) {
							SpaceTreeLayer parentLayer = (SpaceTreeLayer) spaceTreeLayers.get(depth - 1);
							parentLayer.moveNodeForward(parent, newPositionForCurrent, extendBounds);
							if (parent.positionInLayer < newPositionForCurrent) {
								double delta = newPositionForCurrent - parent.positionInLayer;
								newPosition -= delta;
								revertToShanpshot(snapShot);
								continue mainLoop;
							}
						}
					}
					// move children if necessary
					if (currentNodeToMove.expanded && !currentNodeToMove.children.isEmpty()) {
						SpaceTreeNode lastChild = (SpaceTreeNode) currentNodeToMove.children.get(currentNodeToMove.children.size() - 1);
						if (lastChild.positionInLayer < newPositionForCurrent) {
							// try to move all the children, that is move the
							// first child and the rest will be pushed
							SpaceTreeNode firstChild = (SpaceTreeNode) currentNodeToMove.children.get(0);
							SpaceTreeLayer childLayer = (SpaceTreeLayer) spaceTreeLayers.get(depth + 1);
							double expectedDistanceBetweenChildren = currentNodeToMove.spaceRequiredForChildren() - firstChild.spaceRequiredForNode()
									/ 2 - lastChild.spaceRequiredForNode() / 2;
							childLayer.moveNodeForward(firstChild, newPositionForCurrent - expectedDistanceBetweenChildren, extendBounds);
							if (currentNodeToMove.expanded && lastChild.positionInLayer < newPositionForCurrent) {
								// the previous attempt failed -> try to move
								// only the last child
								childLayer.moveNodeForward(lastChild, newPositionForCurrent, extendBounds);
								if (lastChild.positionInLayer < newPositionForCurrent) {
									// child couldn't be moved as far as needed
									// -> move current node back to the position
									// over the child
									double delta = newPositionForCurrent - lastChild.positionInLayer;
									newPosition -= delta;
									revertToShanpshot(snapShot);
									continue mainLoop;
								}
							}
						}
					}

					if (i < nodes.size() - 1) {
						SpaceTreeNode nextNode = (SpaceTreeNode) nodes.get(i + 1);
						newPositionForCurrent += expectedDistance(currentNodeToMove, nextNode);
						currentNodeToMove = nextNode;
						if (currentNodeToMove.positionInLayer > newPositionForCurrent)
							break;
					}
				}
			}
		}

		private void moveNodeBackward(SpaceTreeNode nodeToMove, double newPosition, boolean extendBounds) {
			int nodeIndex = nodes.indexOf(nodeToMove);
			if (nodeIndex == -1)
				throw new IllegalArgumentException("node not on this layer");
			// move backward -> check space to the 'left'
			// move and collapse until there's enough space
			NodeSnapshot[][] snapShot = takeSnapShot();
			mainLoop: while (nodeToMove.positionInLayer > newPosition) {
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
					boolean removed = false;
					for (int i = nodeIndex; i >= 0; i--) {
						SpaceTreeNode nextNode = ((SpaceTreeNode) nodes.get(i));
						if (protectedNode == null || (!protectedNode.isAncestorOf(nextNode) && !nextNode.parent.isAncestorOf(protectedNode))) {
							collapseNode(nextNode.parent);
							if (nextNode.parent == nodeToMove.parent)
								break mainLoop;
							nodeIndex -= nextNode.parent.children.size();
							removed = true;
							break;
						}
					}
					if (!removed && !extendBounds) {
						// not enough space, but we can't collapse anything...
						newPosition = requiredSpace;
						revertToShanpshot(snapShot);
						continue mainLoop;
					}
				}
				// move the node and all its neighbors to the 'left'
				SpaceTreeNode currentNodeToMove = nodeToMove;
				double newPositionForCurrent = newPosition;
				for (int i = nodeIndex; i >= 0; i--) {
					currentNodeToMove.positionInLayer = newPositionForCurrent;
					// move parent if moved node is its last child
					if (currentNodeToMove.lastChild) {
						SpaceTreeNode parent = currentNodeToMove.parent;
						if (depth > 0 && parent.positionInLayer >= newPositionForCurrent) {
							SpaceTreeLayer parentLayer = (SpaceTreeLayer) spaceTreeLayers.get(depth - 1);
							parentLayer.moveNodeBackward(parent, newPositionForCurrent, extendBounds);
							if (parent.positionInLayer > newPositionForCurrent) {
								double delta = parent.positionInLayer - newPositionForCurrent;
								newPosition += delta;
								revertToShanpshot(snapShot);
								continue mainLoop;
							}
						}
					}
					// move children if necessary
					if (currentNodeToMove.expanded && !currentNodeToMove.children.isEmpty()) {
						SpaceTreeNode firstChild = (SpaceTreeNode) currentNodeToMove.children.get(0);
						if (firstChild.positionInLayer > newPositionForCurrent) {
							// try to move all the children, that is move the
							// last child and the rest will be pushed
							SpaceTreeNode lastChild = (SpaceTreeNode) currentNodeToMove.children.get(currentNodeToMove.children.size() - 1);
							SpaceTreeLayer childLayer = (SpaceTreeLayer) spaceTreeLayers.get(depth + 1);
							double expectedDistanceBetweenChildren = currentNodeToMove.spaceRequiredForChildren() - firstChild.spaceRequiredForNode()
									/ 2 - lastChild.spaceRequiredForNode() / 2;
							childLayer.moveNodeBackward(lastChild, newPositionForCurrent + expectedDistanceBetweenChildren, extendBounds);
							if (currentNodeToMove.expanded && firstChild.positionInLayer > newPositionForCurrent) {
								// the previous attempt failed -> try to move
								// only the first child
								childLayer.moveNodeBackward(firstChild, newPositionForCurrent, extendBounds);
								if (firstChild.positionInLayer > newPositionForCurrent) {
									// child couldn't be moved as far as needed
									// -> move current node back to the position
									// over the child
									double delta = firstChild.positionInLayer - newPositionForCurrent;
									newPosition += delta;
									revertToShanpshot(snapShot);
									continue mainLoop;
								}
							}
						}
					}
					if (i > 0) {
						SpaceTreeNode nextNode = (SpaceTreeNode) nodes.get(i - 1);
						newPositionForCurrent -= expectedDistance(currentNodeToMove, nextNode);
						currentNodeToMove = nextNode;
						if (currentNodeToMove.positionInLayer < newPositionForCurrent)
							break;
					}
				}
			}
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
			layer.removeNodes(node.children);
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
			spaceTreeNode.parent.children.remove(spaceTreeNode);
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

	/**
	 * If not null, this node and all of its children shall not be collapsed
	 * during node movements.
	 */
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
			((SpaceTreeLayer) spaceTreeLayers.get(spaceTreeNode.depth + 1)).removeNodes(spaceTreeNode.children);
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

	private double expectedDistance(SpaceTreeNode node, SpaceTreeNode neighbor) {
		double expectedDistance = (node.spaceRequiredForNode() + neighbor.spaceRequiredForNode()) / 2;
		expectedDistance += (node.parent == neighbor.parent) ? leafGap : branchGap;
		return expectedDistance;
	}

	private class NodeSnapshot {
		SpaceTreeNode node;
		double position;
		boolean expanded;
	}

	private NodeSnapshot[][] takeSnapShot() {
		NodeSnapshot[][] result = new NodeSnapshot[spaceTreeLayers.size()][];
		for (int i = 0; i < result.length; i++) {
			SpaceTreeLayer layer = (SpaceTreeLayer) spaceTreeLayers.get(i);
			result[i] = new NodeSnapshot[layer.nodes.size()];
			for (int j = 0; j < result[i].length; j++) {
				result[i][j] = new NodeSnapshot();
				result[i][j].node = ((SpaceTreeNode) layer.nodes.get(j));
				result[i][j].position = result[i][j].node.positionInLayer;
				result[i][j].expanded = result[i][j].node.expanded;
			}
		}
		return result;
	}

	private void revertToShanpshot(NodeSnapshot[][] snapShot) {
		for (int i = 0; i < snapShot.length; i++) {
			SpaceTreeLayer layer = (SpaceTreeLayer) spaceTreeLayers.get(i);
			layer.nodes.clear();
			for (int j = 0; j < snapShot[i].length; j++) {
				snapShot[i][j].node.positionInLayer = snapShot[i][j].position;
				snapShot[i][j].node.expanded = snapShot[i][j].expanded;
				layer.nodes.add(snapShot[i][j].node);
			}
		}
	}
}
