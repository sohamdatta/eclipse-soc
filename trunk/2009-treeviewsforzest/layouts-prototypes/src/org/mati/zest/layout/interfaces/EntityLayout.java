package org.mati.zest.layout.interfaces;

import org.eclipse.zest.layouts.dataStructures.DisplayIndependentDimension;
import org.eclipse.zest.layouts.dataStructures.DisplayIndependentPoint;

/**
 * A common interface for entities that are displayed on a graph, that is
 * {@link NodeLayout nodes} and {@link SubgraphLayout subgraphs}.
 */
public interface EntityLayout {

	/**
	 * Returns a point laying in the center of this node. Any subsequent changes
	 * to the returned point won't affect this node.
	 * 
	 * @return position of the center of this node
	 */
	public DisplayIndependentPoint getLocation();

	/**
	 * Sets the position of this node. The node will be moved so that it's
	 * center is located in the given point.
	 * 
	 * @param x
	 *            the x-position
	 * @param y
	 *            the y-position
	 */
	public void setLocation(double x, double y);

	public DisplayIndependentDimension getSize();

	public void setSize(double width, double height);

	/**
	 * Returns aspect ratio that is preferred for this node. Can be 0 if this
	 * node can't be resized anyway or it doesn't care about about its ratio.
	 * 
	 * @return aspect ratio (width / height)
	 */
	public double getPreferredAspectRatio();

	public boolean isResizable();

	public boolean isMovable();
}
