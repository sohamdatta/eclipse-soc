package org.mati.zest.layout.interfaces;

import org.eclipse.zest.layouts.dataStructures.DisplayIndependentDimension;
import org.eclipse.zest.layouts.dataStructures.DisplayIndependentPoint;

public interface NodeLayout {

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

	public boolean isResizable();

	public boolean isMovable();

	public boolean isPrunable();

	public boolean isPruned();

	/**
	 * 
	 * @return a subgraph this node belongs to or null if this node is not
	 *         pruned
	 */
	public SubgraphLayout getSubgraph();

	/**
	 * 
	 * @param subgraph
	 *            a subgraph this node should belong to or null if this node
	 *            should not be pruned
	 */
	public void prune(SubgraphLayout subgraph);

	/**
	 * Returns all connections that have this node as their target. Any
	 * subsequent changes to the returned array do not affect this node.
	 * 
	 * @return array of connections entering this node
	 */
	public ConnectionLayout[] getIncomingConnections();

	/**
	 * Returns all connections that have this node as their source. Any
	 * subsequent changes to the returned array do not affect this node.
	 * 
	 * @return array of connections starting at this node
	 */
	public ConnectionLayout[] getOutgoingConnections();

}
