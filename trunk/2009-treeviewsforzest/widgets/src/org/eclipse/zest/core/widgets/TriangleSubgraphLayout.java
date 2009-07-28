package org.eclipse.zest.core.widgets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.Shape;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Color;
import org.eclipse.zest.layout.interfaces.ConnectionLayout;
import org.eclipse.zest.layout.interfaces.EntityLayout;
import org.eclipse.zest.layout.interfaces.GraphStructureListener;
import org.eclipse.zest.layout.interfaces.LayoutContext;
import org.eclipse.zest.layout.interfaces.NodeLayout;
import org.eclipse.zest.layout.interfaces.SubgraphLayout;

/**
 * A subgraph that is visualized in a graph as a triangle. It assumes that nodes
 * in context that uses them are arranged in a tree structure and the nodes
 * added to the subgraph are a subtree (except for the subtree's root, which
 * should not be added).
 * 
 * The triangle has three features that show the properties of a subtree
 * contained within it:
 * <ul>
 * <li><b>Height of the triangle</b> is proportional to the height of the
 * subtree. If the subtree contains the whole tree, the triangle's height will
 * be equal to value provided with
 * {@link TriangleSubgraphLayout#setReferenceHeight(double)} (default is 50).</li>
 * <li><b>Length of the triangle's base</b> depends on the number of leaves in
 * the subtree. More precisely, it is proportional to the logarithm of the
 * percent that the subtree's leaves make of the whole context's leaves. The
 * proportion factor is adjusted so that for a subtree containing all the leaves
 * the base has length provided with {@link TriangleSubgraphLayout}
 * {@link #setReferenceBase(double)} (default is 50) and for a subtree
 * containing only one leaf the base has length 1.</li>
 * <li><b>Background color of the tirangle</b> depends on average number of
 * children for nodes in the subtree. The less is this value, the more bright is
 * the color (up to white for a subtree with average number of children equal to
 * 1). The average value is calculated only for nodes that have at least one
 * child. The root of the subtree (which is not directly added to this subgraph)
 * is also accounted.</li>
 * </ul>
 * 
 * When the first subgraph of this class is created for a layout context, a
 * GraphStructureListener is registered in the context. Triangle subgraphs must
 * keep track of changes in the graph structure, so events related to it should
 * not be intercepted by other listeners before they reach the subgraph's
 * listener.
 */
public class TriangleSubgraphLayout extends FigureSubgraphLayout {

	public final static SubgraphFactory FACTORY = new SubgraphFactory() {
		public SubgraphLayout createSubgraph(InternalNodeLayout[] nodes, InternalLayoutContext context) {
			return new TriangleSubgraphLayout(nodes, context);
		}
	};
	
	private final static GraphStructureListener GRAPH_STRUCTURE_LISTENER = new GraphStructureListener() {
		
		public boolean nodeRemoved(LayoutContext context, NodeLayout node) {
			int numOfNodes = ((Integer) contextToNumOfNodes.get(context)).intValue();
			contextToNumOfNodes.put(context, new Integer(numOfNodes + 1));
			TreeNodeInfo spaceTreeNode = (TreeNodeInfo) layoutToNodeInfo.get(node);
			spaceTreeNode.parent.children.remove(spaceTreeNode);
			refreshTreeStructure(context, node);
			return false;
		}
		
		public boolean nodeAdded(LayoutContext context, NodeLayout node) {
			int numOfNodes = ((Integer) contextToNumOfNodes.get(context)).intValue();
			contextToNumOfNodes.put(context, new Integer(numOfNodes + 1));
			TreeNodeInfo superRoot = (TreeNodeInfo) contextToTree.get(context);
			superRoot.addChild(createTreeNodeInfo(node));
			refreshTreeStructure(context, node);
			return false;
		}
		
		public boolean connectionRemoved(LayoutContext context, ConnectionLayout connection) {
			TreeNodeInfo superRoot = (TreeNodeInfo) contextToTree.get(context);
			TreeNodeInfo node1 = (TreeNodeInfo) layoutToNodeInfo.get(connection.getSource());
			TreeNodeInfo node2 = (TreeNodeInfo) layoutToNodeInfo.get(connection.getTarget());
			if (node1.parent == node2) {
				node1.findNewParent(superRoot);
				refreshTreeStructure(context, node1.node);
			}
			if (node2.parent == node1) {
				node2.findNewParent(superRoot);
				refreshTreeStructure(context, node2.node);
			}
			return false;
		}
		
		public boolean connectionAdded(LayoutContext context, ConnectionLayout connection) {
			TreeNodeInfo source = (TreeNodeInfo) layoutToNodeInfo.get(connection.getSource());
			TreeNodeInfo target = (TreeNodeInfo) layoutToNodeInfo.get(connection.getTarget());
			if (source == target)
				return false;
			if (TreeNodeInfo.isBetterParent(target.parent, source)) {
				target.parent.children.remove(target);
				source.addChild(target);
				refreshTreeStructure(context, target.node);
			}
			if (!connection.isDirected() && TreeNodeInfo.isBetterParent(source.parent, target)) {
				source.parent.children.remove(source);
				target.addChild(source);
				refreshTreeStructure(context, source.node);
			}
			return false;
		}
		
		private void refreshTreeStructure(LayoutContext context, NodeLayout node) {
			((TreeNodeInfo) contextToTree.get(context)).precomputeTree();
			if (node.getSubgraph() instanceof TriangleSubgraphLayout)
				((TriangleSubgraphLayout) node.getSubgraph()).updateFigure();
		}
	};

	private class IsoscelesTriangle extends Shape {

		private PointList points = new PointList(3);

		protected void fillShape(Graphics graphics) {
			graphics.fillPolygon(points);
		}

		protected void outlineShape(Graphics graphics) {
			graphics.drawPolygon(points);
		}

		protected void primTranslate(int dx, int dy) {
			super.primTranslate(dx, dy);
			points.translate(dx, dy);
		}

		public void validate() {
			super.validate();
			Rectangle r = new Rectangle();
			r.setBounds(getBounds());
			r.crop(getInsets());
			points.removeAllPoints();
			switch (direction) {
			case TOP_DOWN:
				points.addPoint(r.x + r.width / 2, r.y);
				points.addPoint(r.x, r.y + r.height);
				points.addPoint(r.x + r.width, r.y + r.height);
				break;
			case BOTTOM_UP:
				points.addPoint(r.x + r.width / 2, r.y + r.height);
				points.addPoint(r.x, r.y);
				points.addPoint(r.x + r.width, r.y);
				break;
			case LEFT_RIGHT:
				points.addPoint(r.x, r.y + r.height / 2);
				points.addPoint(r.x + r.width, r.y);
				points.addPoint(r.x + r.width, r.y + r.height);
				break;
			case RIGHT_LEFT:
				points.addPoint(r.x + r.width, r.y + r.height / 2);
				points.addPoint(r.x, r.y);
				points.addPoint(r.x, r.y + r.height);
				break;
			}
		}
	}

	private static class TreeNodeInfo {
		final public NodeLayout node;
		public int height = 0;
		public int depth = -1;
		public int numOfLeaves = 0;
		public int order = 0;
		public final List children = new ArrayList();
		public TreeNodeInfo parent;

		private TreeNodeInfo(NodeLayout node) {
			this.node = node;
		}

		public void addChild(TreeNodeInfo child) {
			children.add(child);
			child.parent = this;
		}

		public void precomputeTree() {
			if (children.isEmpty()) {
				numOfLeaves = 1;
				height = 0;
			} else {
				numOfLeaves = 0;
				height = 0;
				for (ListIterator iterator = children.listIterator(); iterator.hasNext();) {
					TreeNodeInfo child = (TreeNodeInfo) iterator.next();
					child.depth = this.depth + 1;
					child.precomputeTree();
					child.order = this.order + this.numOfLeaves;

					this.numOfLeaves += child.numOfLeaves;
					this.height = Math.max(this.height, child.height + 1);
				}
			}
		}

		public void findNewParent(TreeNodeInfo superRoot) {
			if (parent != null)
				parent.children.remove(this);
			NodeLayout[] predecessingNodes = node.getPredecessingNodes();
			TreeNodeInfo bestParent;
			if (predecessingNodes.length > 0) {
				bestParent = (TreeNodeInfo) layoutToNodeInfo.get(predecessingNodes[0]);
				for (int i = 1; i < predecessingNodes.length; i++) {
					TreeNodeInfo potentialParent = (TreeNodeInfo) layoutToNodeInfo.get(predecessingNodes[i]);
					if (isBetterParent(bestParent, potentialParent))
						bestParent = potentialParent;
				}
			} else
				bestParent = superRoot;
			bestParent.addChild(this);
		}

		private static boolean isBetterParent(TreeNodeInfo base, TreeNodeInfo better) {
			if (better.depth < base.depth && better.depth != -1)
				return true;
			if (base.depth == -1 && better.depth >= 0)
				return true;
			return false;
		}
	}

	private static Color defaultColor = ColorConstants.black;

	private static int defaultDirection = TOP_DOWN;
	
	private static double referenceHeight = 50;

	private static double referenceBase = 50;

	private static HashMap layoutToNodeInfo = new HashMap();
	
	private static HashMap contextToNumOfNodes = new HashMap();
	
	private static HashMap contextToTree = new HashMap();

	private Color color = defaultColor;

	private int direction = defaultDirection;

	/**
	 * Changes the default color for newly created subgraphs.
	 * 
	 * @param color
	 *            color to use
	 */
	public static void setDefaultBackgroundColor(Color color) {
		defaultColor = color;
	}

	/**
	 * Changes the default direction for newly cretaed subgraphs.
	 * 
	 * @param direction
	 *            direction to use, can be {@link SubgraphLayout#TOP_DOWN},
	 *            {@link SubgraphLayout#BOTTOM_UP},
	 *            {@link SubgraphLayout#LEFT_RIGHT}, or
	 *            {@link SubgraphLayout#RIGHT_LEFT}
	 */
	public static void setDefaultDirection(int direction) {
		if (direction == TOP_DOWN || direction == BOTTOM_UP || direction == LEFT_RIGHT || direction == RIGHT_LEFT) {
			defaultDirection = direction;
		} else
			throw new IllegalArgumentException("invalid direction");
	}

	/**
	 * Sets the maximum height for the triangle visualizing this subgraph.
	 * 
	 * @param referenceHeight
	 *            height to use
	 */
	public void setReferenceHeight(double referenceHeight) {
		TriangleSubgraphLayout.referenceHeight = referenceHeight;
	}

	/**
	 * Sets the maximum base length for the triangle visualizing this subgraph.
	 * 
	 * @param referenceBase
	 *            base length to use
	 */
	public void setReferenceBase(double referenceBase) {
		TriangleSubgraphLayout.referenceBase = referenceBase;
	}

	protected TriangleSubgraphLayout(NodeLayout[] nodes, InternalLayoutContext context) {
		super(nodes, context);
		if (contextToTree.get(context) == null) {
			context.addGraphStructureListener(GRAPH_STRUCTURE_LISTENER);
			NodeLayout[] nodesInContext = context.getNodes();
			contextToNumOfNodes.put(context, new Integer(nodesInContext.length));
			contextToTree.put(context, createTrees(nodesInContext));
		}
	}

	protected void createFigure() {
		figure = new IsoscelesTriangle();
		figure.setBackgroundColor(defaultColor);
		figure.setForegroundColor(defaultColor);
		updateFigure();
	}

	private double log(double value, double base) {
		return Math.log(value) / Math.log(base);
	}

	protected void updateFigure() {
		TreeNodeInfo rootInfo = (TreeNodeInfo) layoutToNodeInfo.get(nodes.iterator().next());
		if (rootInfo == null)
			return;
		while (nodes.contains(rootInfo.node))
			rootInfo = rootInfo.parent;
		
		TreeNodeInfo superRoot = (TreeNodeInfo) contextToTree.get(context);
		double triangleHeight = referenceHeight * rootInfo.height / superRoot.height;
		
		int numOfNodes = ((Integer) contextToNumOfNodes.get(context)).intValue();
		int numOfNodesWithChildren = numOfNodes - superRoot.numOfLeaves + 1;
		double logBase = (numOfNodesWithChildren > 0) ? (double) numOfNodes / numOfNodesWithChildren : 1; //average number of children for whole context
		double triangleBaseModifier = (double) (referenceBase - 1) / log(superRoot.numOfLeaves, logBase);
		double triangleBase = referenceBase + triangleBaseModifier * log((double) rootInfo.numOfLeaves / superRoot.numOfLeaves, logBase);

		if (direction == 0)
			direction = defaultDirection;
		if (direction == TOP_DOWN || direction == BOTTOM_UP) {
			figure.setSize((int) (triangleBase + 0.5), (int) (triangleHeight + 0.5));
		} else {
			figure.setSize((int) (triangleHeight + 0.5), (int) (triangleBase + 0.5));
		}

		if (color == null)
			color = defaultColor;
		int numOfNodesWithChildrenInSubgraph = nodes.size() - rootInfo.numOfLeaves + 1;
		double avgNumOfChildrenInSugbraph = (numOfNodesWithChildrenInSubgraph > 0) ? (double) nodes.size() / numOfNodesWithChildrenInSubgraph : 1;
		int r = (int) (color.getRed() + ((double)255 - color.getRed()) / avgNumOfChildrenInSugbraph);
		int g = (int) (color.getRed() + ((double)255 - color.getRed()) / avgNumOfChildrenInSugbraph);
		int b = (int) (color.getRed() + ((double)255 - color.getRed()) / avgNumOfChildrenInSugbraph);
		figure.setBackgroundColor(new Color(color.getDevice(), r, g, b));
		figure.setForegroundColor(color);
	}

	// protected void createFigure() {
	// figure = new GraphLabel(false);
	// figure.setForegroundColor(ColorConstants.black);
	// figure.setBackgroundColor(ColorConstants.yellow);
	// updateFigure();
	// }
	//
	// protected void updateFigure() {
	// if (nodes == null || layoutToNodeInfo.isEmpty())
	// return;
	// TreeNodeInfo rootInfo = (TreeNodeInfo)
	// layoutToNodeInfo.get(nodes.iterator().next());
	// while (nodes.contains(rootInfo.node))
	// rootInfo = rootInfo.parent;
	//
	// int numOfLeafs = rootInfo.numOfLeaves;
	// int treeHeight = rootInfo.height;
	// int numOfNodesWithChildren = nodes.size() - numOfLeafs + 1;
	// double avgNumOfChildren = (numOfNodesWithChildren > 0) ? (double)
	// nodes.size() / numOfNodesWithChildren : 1;
	// ((Label) figure).setText("" + treeHeight + "/" + numOfLeafs + "/" +
	// nodes.size() + "/" + avgNumOfChildren);
	// }

	public boolean isDirectionDependant() {
		return true;
	}

	public void setDirection(int direction) {
		if (this.direction == direction)
			return;
		if (direction == TOP_DOWN || direction == BOTTOM_UP || direction == LEFT_RIGHT || direction == RIGHT_LEFT) {
			this.direction = direction;
			updateFigure();
		} else
			throw new IllegalArgumentException("invalid direction");
	}

	/**
	 * Changes the color of the triangle visualizing this subgraph.
	 * 
	 * @param color
	 *            color to use
	 */
	public void setColor(Color color) {
		this.color = color;
		updateFigure();
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
	private static NodeLayout findRoot(NodeLayout nodeLayout, Set alreadyVisited) {
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

	/**
	 * Builds a tree structure using BFS method.
	 * 
	 * @param nodes
	 * @return a super root for roots of all created trees
	 */
	private static TreeNodeInfo createTrees(NodeLayout[] nodes) {
		HashSet alreadyVisited = new HashSet();
		LinkedList nodesToAdd = new LinkedList();
		TreeNodeInfo superRoot = new TreeNodeInfo(null);
		for (int i = 0; i < nodes.length; i++) {
			EntityLayout root = findRoot(nodes[i], alreadyVisited);
			if (root != null) {
				alreadyVisited.add(root);
				nodesToAdd.addLast(new Object[] { root, superRoot });
			}
		}
		while (!nodesToAdd.isEmpty()) {
			Object[] dequeued = (Object[]) nodesToAdd.removeFirst();
			NodeLayout node = (NodeLayout) dequeued[0];
			TreeNodeInfo parentNodeInfo = (TreeNodeInfo) dequeued[1];
			TreeNodeInfo currentNodeInfo = createTreeNodeInfo(node);
			parentNodeInfo.addChild(currentNodeInfo);
			NodeLayout[] children = node.getSuccessingEntities();
			for (int i = 0; i < children.length; i++) {
				if (!alreadyVisited.contains(children[i])) {
					alreadyVisited.add(children[i]);
					nodesToAdd.addLast(new Object[] { children[i], currentNodeInfo });
				}
			}
		}
		superRoot.precomputeTree();
		return superRoot;
	}
	
	private static TreeNodeInfo createTreeNodeInfo(NodeLayout layout) {
		TreeNodeInfo treeNodeInfo = (TreeNodeInfo) layoutToNodeInfo.get(layout);
		if (treeNodeInfo == null) {
			treeNodeInfo = new TreeNodeInfo(layout);
			layoutToNodeInfo.put(layout, treeNodeInfo);
		}
		treeNodeInfo.children.clear();
		return treeNodeInfo;
	}
}
