package org.mati.zest.core.widgets;

import java.util.Arrays;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.zest.core.widgets.internal.GraphLabel;
import org.eclipse.zest.layout.dataStructures.DisplayIndependentDimension;
import org.eclipse.zest.layout.dataStructures.DisplayIndependentPoint;
import org.eclipse.zest.layout.interfaces.NodeLayout;
import org.eclipse.zest.layout.interfaces.SubgraphLayout;

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
	}

	protected void updateFigure() {

	}

	protected ExperimentalSubgraphLayout(NodeLayout[] nodes, InternalLayoutContext context) {
		super(context);
		System.out.println("" + this.hashCode() + " creating a subgraph for: " + Arrays.asList(nodes).toString());
		addNodes(nodes);
		createFigure();
		context.container.addFigure(figure);
	}

	public void addNodes(NodeLayout[] nodes) {
		super.addNodes(nodes);
		System.out.println("" + this.hashCode() + " adding nodes: " + Arrays.asList(nodes).toString());
	}

	public void removeNodes(NodeLayout[] nodes) {
		super.removeNodes(nodes);
		System.out.println("" + this.hashCode() + " removing nodes: " + Arrays.asList(nodes).toString());
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
		if (location != null) {
			location.x = x;
			location.y = y;
		} else {
			location = new DisplayIndependentPoint(x, y);
		}
	}

	public boolean isGraphEntity() {
		return true;
	}

	protected void dispose() {
		super.dispose();
		System.out.println("" + this.hashCode() + " disposing");
		figure.getParent().remove(figure);
	}

	protected void applyLayoutChanges() {
		if (location != null) {
			Dimension size = figure.getSize();
			figure.setLocation(new Point(location.x - size.width / 2, location.y - size.height / 2));
		}
	}

}
