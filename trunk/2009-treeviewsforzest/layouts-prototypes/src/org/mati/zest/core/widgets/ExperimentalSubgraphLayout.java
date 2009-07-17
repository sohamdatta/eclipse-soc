package org.mati.zest.core.widgets;

import java.util.Iterator;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.zest.core.widgets.internal.GraphLabel;
import org.eclipse.zest.layout.dataStructures.DisplayIndependentDimension;
import org.eclipse.zest.layout.dataStructures.DisplayIndependentPoint;
import org.eclipse.zest.layout.interfaces.NodeLayout;
import org.eclipse.zest.layout.interfaces.SubgraphLayout;
import org.mati.zest.core.widgets.internal.ZestRootLayer;

public class ExperimentalSubgraphLayout extends InternalSubgraphLayout {

	public final static SubgraphFactory FACTORY = new SubgraphFactory() {
		public SubgraphLayout createSubgraph(NodeLayout[] nodes, InternalLayoutContext context) {
			return new ExperimentalSubgraphLayout(nodes, context);
		}
	};

	private Label figure;
	private DisplayIndependentPoint location;

	protected void createFigure() {
		figure = new GraphLabel("HI!", false);
		figure.setLocation(new Point(20, 20));
		figure.setForegroundColor(ColorConstants.black);
		figure.setBackgroundColor(ColorConstants.yellow);
		updateFigure();
	}

	protected void updateFigure() {
		if (figure != null) {
			if (disposed)
				figure.setText("##");
			else
				figure.setText("" + nodes.size());
		}
	}

	protected ExperimentalSubgraphLayout(NodeLayout[] nodes, InternalLayoutContext context) {
		super(context);
		addNodes(nodes);
		createFigure();
		context.container.addFigure(figure);
	}

	public void addNodes(NodeLayout[] nodes) {
		super.addNodes(nodes);
		updateFigure();
		if (location != null)
			for (int i = 0; i < nodes.length; i++) {
				nodes[i].setLocation(location.x, location.y);
			}
	}

	public void removeNodes(NodeLayout[] nodes) {
		super.removeNodes(nodes);
		updateFigure();
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
			if (parent instanceof ZestRootLayer) {
				((ZestRootLayer) parent).removeSubgraph(figure);
			} else {
				parent.remove(figure);
			}
		}
	}

	protected void applyLayoutChanges() {
		if (location != null) {
			Dimension size = figure.getSize();
			figure.setLocation(new Point(location.x - size.width / 2, location.y - size.height / 2));
		}
	}

}
