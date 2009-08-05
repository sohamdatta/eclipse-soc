package org.eclipse.zest.core.widgets;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Label;
import org.eclipse.swt.graphics.Color;
import org.eclipse.zest.core.widgets.internal.GraphLabel;
import org.eclipse.zest.layout.interfaces.LayoutContext;
import org.eclipse.zest.layout.interfaces.NodeLayout;
import org.eclipse.zest.layout.interfaces.SubgraphLayout;

/**
 * A subgraph layout that displays a label showing number of items pruned within
 * it.
 */
public class LabelSubgraph extends FigureSubgraph {

	public static class Factory implements SubgraphFactory {
		private Color defaultForegroundColor = ColorConstants.black;
		private Color defaultBackgroundColor = ColorConstants.yellow;

		/**
		 * Changes the default foreground color for newly created subgraphs.
		 * 
		 * @param c
		 *            color to use
		 */
		public void setDefualtForegroundColor(Color c) {
			defaultForegroundColor = c;
		}

		/**
		 * Changes the default background color for newly created subgraphs.
		 * 
		 * @param c
		 *            color to use
		 */
		public void setDefaultBackgroundColor(Color c) {
			defaultBackgroundColor = c;
		}

		public SubgraphLayout createSubgraph(NodeLayout[] nodes, LayoutContext context) {
			return new LabelSubgraph(nodes, context, defaultForegroundColor, defaultBackgroundColor);
		}
	};

	private Color backgroundColor;
	private Color foregroundColor;

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
		figure.setForegroundColor(foregroundColor);
		figure.setBackgroundColor(backgroundColor);
		updateFigure();
	}

	protected void updateFigure() {
		((Label) figure).setText("" + nodes.size());
	}

	protected LabelSubgraph(NodeLayout[] nodes, LayoutContext context, Color foregroundColor, Color backgroundColor) {
		super(nodes, context);
		this.foregroundColor = foregroundColor;
		this.backgroundColor = backgroundColor;
	}
}