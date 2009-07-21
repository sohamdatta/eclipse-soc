package org.mati.zest.core.widgets;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.Shape;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Color;
import org.eclipse.zest.layout.interfaces.NodeLayout;
import org.eclipse.zest.layout.interfaces.SubgraphLayout;

public class TriangleSubgraphLayout extends FigureSubgraphLayout {

	public final static SubgraphFactory FACTORY = new SubgraphFactory() {
		public SubgraphLayout createSubgraph(NodeLayout[] nodes, InternalLayoutContext context) {
			return new TriangleSubgraphLayout(nodes, context);
		}
	};
	

	private class IsoscelesTriangle extends Shape {

		private PointList points = new PointList(3);;

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


	private static Color defaultColor = ColorConstants.black;

	private static int defaultDirection = TOP_DOWN;

	private Color color = defaultColor;

	private int direction = defaultDirection;

	private int treeHeight = 0;

	private int numOfLeafs = 0;

	private double avgNumOfChildren = 1;

	/**
	 * Changes the default color for newly created subgraphs.
	 * 
	 * @param color
	 *            color to use
	 */
	public static void setDefaultBackgroundColor(Color color) {
		defaultColor = color;
	}

	public static void setDefaultDirection(int direction) {

	}

	protected TriangleSubgraphLayout(NodeLayout[] nodes, InternalLayoutContext context) {
		super(nodes, context);
	}

	protected void createFigure() {
		figure = new IsoscelesTriangle();
		figure.setBackgroundColor(defaultColor);
		figure.setForegroundColor(defaultColor);
		updateFigure();
	}

	protected void updateFigure() {
		calculateTreeProperties();
		if (direction == 0)
			direction = defaultDirection;
		if (direction == TOP_DOWN || direction == BOTTOM_UP) {
			figure.setSize(numOfLeafs, treeHeight * 3);
		} else {
			figure.setSize(treeHeight * 3, numOfLeafs);
		}

		if (color == null)
			color = defaultColor;
		double colorModifier = 1 - 1 / avgNumOfChildren;
		int r = 255 - (int) ((255 - color.getRed()) * colorModifier);
		int g = 255 - (int) ((255 - color.getGreen()) * colorModifier);
		int b = 255 - (int) ((255 - color.getBlue()) * colorModifier);
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
	// calculateTreeProperties();
	// ((Label) figure).setText("" + treeHeight + "/" + numOfLeafs + "/" +
	// nodes.size() + "/" + avgNumOfChildren);
	// }

	public boolean isDirectionDependant() {
		return true;
	}

	public void setDirection(int direction) {
		if (direction == TOP_DOWN || direction == BOTTOM_UP || direction == LEFT_RIGHT || direction == RIGHT_LEFT) {
			this.direction = direction;
			updateFigure();
		} else
			throw new IllegalArgumentException("invalid direction");
	}

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
	private NodeLayout findRoot(NodeLayout nodeLayout, Set alreadyVisited) {
		HashSet alreadyVisitedRoot = new HashSet();
		while (true) {
			if (alreadyVisited.contains(nodeLayout))
				return null;
			if (alreadyVisitedRoot.contains(nodeLayout))
				return nodeLayout;
			alreadyVisitedRoot.add(nodeLayout);
			NodeLayout[] predecessingNodes = nodeLayout.getPredecessingNodes();
			if (predecessingNodes.length > 0 && nodes.contains(predecessingNodes[0])) {
				nodeLayout = predecessingNodes[0];
			} else {
				return nodeLayout;
			}
		}
	}

	private void calculateTreeProperties() {
		HashSet alreadyVisited = new HashSet();
		LinkedList nodesToAdd = new LinkedList();
		Integer currentHeight = new Integer(0);
		numOfLeafs = 0;
		for (Iterator iterator = nodes.iterator(); iterator.hasNext();) {
			NodeLayout node = (NodeLayout) iterator.next();
			NodeLayout root = findRoot(node, alreadyVisited);
			if (root != null) {
				alreadyVisited.add(root);
				nodesToAdd.addLast(new Object[] { root, currentHeight });
			}
		}
		while (!nodesToAdd.isEmpty()) {
			Object[] dequeued = (Object[]) nodesToAdd.removeFirst();
			NodeLayout currentNode = (NodeLayout) dequeued[0];
			currentHeight = new Integer(((Integer) dequeued[1]).intValue() + 1);

			NodeLayout[] children = currentNode.getSuccessingNodes();
			int childrenCount = 0;
			for (int i = 0; i < children.length; i++) {
				if (nodes.contains(children[i]) && !alreadyVisited.contains(children[i])) {
					childrenCount++;
					alreadyVisited.add(children[i]);
					nodesToAdd.addLast(new Object[] { children[i], currentHeight });
				}
			}
			if (childrenCount == 0) {
				numOfLeafs++;
			}
		}
		treeHeight = currentHeight.intValue();
		int numOfNodesWithChildren = nodes.size() - numOfLeafs + 1;
		avgNumOfChildren = nodes.size() / numOfNodesWithChildren;
	}

}
