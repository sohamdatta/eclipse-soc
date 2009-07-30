package org.eclipse.zest.core.widgets;

import java.util.Iterator;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.zest.layout.dataStructures.DisplayIndependentDimension;
import org.eclipse.zest.layout.dataStructures.DisplayIndependentPoint;
import org.eclipse.zest.layout.interfaces.EntityLayout;
import org.eclipse.zest.layout.interfaces.NodeLayout;

/**
 * A subgraph layout that represents a subgraph as a single figure.
 */
public abstract class FigureSubgraphLayout extends DummySubgraphLayout {

	protected Figure figure;
	private DisplayIndependentPoint location;

	/**
	 * Creates a figure for this subgraph and stores it in {@link #figure}. All
	 * nodes contained in this subgraph are moved to the center of the figure
	 * (so that collapsing and expanding animation looks cool).
	 */
	protected abstract void createFigure();

	/**
	 * Updates the figure stored in {@link #figure} depending on current nodes
	 * contained in this subgraph.
	 */
	protected abstract void updateFigure();

	protected FigureSubgraphLayout(NodeLayout[] nodes, InternalLayoutContext context) {
		super(context);
		addNodes(nodes);
		createFigure();
		context.container.addSubgraphFigure(figure);
	}

	public void addNodes(NodeLayout[] nodes) {
		int initialCount = this.nodes.size();
		super.addNodes(nodes);
		if (this.nodes.size() > initialCount && figure != null) {
			updateFigure();
			if (location != null)
				for (int i = 0; i < nodes.length; i++) {
					nodes[i].setLocation(location.x, location.y);
				}
		}
	}

	public void removeNodes(NodeLayout[] nodes) {
		int initialCount = this.nodes.size();
		super.removeNodes(nodes);
		if (this.nodes.size() < initialCount && !disposed)
			updateFigure();
	}

	public EntityLayout[] getSuccessingEntities() {
		// TODO Auto-generated method stub
		return super.getSuccessingEntities();
	}

	public EntityLayout[] getPredecessingEntities() {
		// TODO Auto-generated method stub
		return super.getPredecessingEntities();
	}

	public DisplayIndependentDimension getSize() {
		Dimension size = figure.getSize();
		return new DisplayIndependentDimension(size.width, size.height);
	}

	public DisplayIndependentPoint getLocation() {
		if (location == null) {
			Point location2 = figure.getLocation();
			Dimension size = figure.getSize();
			return new DisplayIndependentPoint(location2.x + size.width / 2, location2.y + size.height / 2);
		}
		return new DisplayIndependentPoint(location);
	}

	public void setLocation(double x, double y) {
		super.setLocation(x, y);
		for (Iterator iterator = nodes.iterator(); iterator.hasNext();) {
			NodeLayout node = (NodeLayout) iterator.next();
			node.setLocation(x, y);
		}

		if (location != null) {
			location.x = x;
			location.y = y;
		} else {
			location = new DisplayIndependentPoint(x, y);
			// the first location change will be applied immediately
			applyLayoutChanges();
		}
	}

	public boolean isGraphEntity() {
		return true;
	}

	public boolean isMovable() {
		return true;
	}

	protected void dispose() {
		if (!disposed) {
			super.dispose();
			IFigure parent = figure.getParent();
			parent.remove(figure);
		}
	}

	protected void applyLayoutChanges() {
		if (location != null) {
			Dimension size = figure.getSize();
			figure.setLocation(new Point(location.x - size.width / 2, location.y - size.height / 2));
		}
	}

}
