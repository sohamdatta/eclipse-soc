package org.eclipse.zest.core.widgets;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Label;
import org.eclipse.swt.graphics.Color;
import org.eclipse.zest.core.widgets.internal.GraphLabel;
import org.eclipse.zest.layout.interfaces.NodeLayout;
import org.eclipse.zest.layout.interfaces.SubgraphLayout;

/**
 * A subgraph layout that displays a label showing number of items pruned within
 * it.
 */
public class LabelSubgraphLayout extends FigureSubgraphLayout {

	public final static SubgraphFactory FACTORY = new SubgraphFactory() {
		public SubgraphLayout createSubgraph(InternalNodeLayout[] nodes, InternalLayoutContext context) {
			return new LabelSubgraphLayout(nodes, context);
		}
	};

	private static Color defaultForegroundColor = ColorConstants.black;
	private static Color defaultBackgroundColor = ColorConstants.yellow;

	/**
	 * Changes the default foreground color for newly created subgraphs.
	 * 
	 * @param c
	 *            color to use
	 */
	public static void setDefualtForegroundColor(Color c) {
		defaultForegroundColor = c;
	}

	/**
	 * Changes the default background color for newly created subgraphs.
	 * 
	 * @param c
	 *            color to use
	 */
	public static void setDefaultBackgroundColor(Color c) {
		defaultBackgroundColor = c;
	}

	/**
	 * Sets the foreground color of this subgraph (that is color of the text on
	 * the label).
	 * 
	 * @param color
	 *            color to set
	 */
	public void setForegroundColor(Color color) {
		figure.setForegroundColor(color);
	}

	/**
	 * Sets the background color of this subgraph's label.
	 * 
	 * @param color
	 *            color to set
	 */
	public void setBackgroundColor(Color color) {
		figure.setBackgroundColor(color);
	}

	protected void createFigure() {
		figure = new GraphLabel(false);
		figure.setForegroundColor(defaultForegroundColor);
		figure.setBackgroundColor(defaultBackgroundColor);
		updateFigure();
	}

	protected void updateFigure() {
		((Label) figure).setText("" + nodes.size());
	}

	protected LabelSubgraphLayout(NodeLayout[] nodes, InternalLayoutContext context) {
		super(nodes, context);
	}
}