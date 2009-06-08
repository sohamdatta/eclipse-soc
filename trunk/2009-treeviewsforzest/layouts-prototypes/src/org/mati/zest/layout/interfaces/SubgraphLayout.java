package org.mati.zest.layout.interfaces;

import org.eclipse.zest.layouts.dataStructures.DisplayIndependentDimension;
import org.eclipse.zest.layouts.dataStructures.DisplayIndependentPoint;

/**
 * An interface for subgraphs in layout. A subgraph is a set of pruned nodes
 * that will be displayed as one element. A subgraph must contain at least one
 * node (empty subgraphs will be removed from its context). Every node can
 * belong to at most one subgraph.
 */
public interface SubgraphLayout {

	/**
	 * Returns all the nodes belonging to this subgraph. Replacing elements in
	 * the returned array does not affect this subgraph.
	 * 
	 * @return array of nodes
	 */
	public NodeLayout[] getNodes();

	/**
	 * Adds nodes to this subgraph. If given nodes already belong to another
	 * subgraph, they are first removed from them.
	 * 
	 * @param nodes
	 *            array of nodes to add
	 */
	public void addNodes(NodeLayout[] nodes);

	/**
	 * Removes nodes from this subgraph.
	 * 
	 * @param nodes
	 *            array of nodes to remove
	 */
	public void removeNodes(NodeLayout[] nodes);

	/**
	 * Returns a point laying in the center of this subgraph. Any subsequent
	 * changes to the returned point won't affect this subgraph.
	 * 
	 * @return position of the center of this node
	 */
	public DisplayIndependentPoint getLocation();

	/**
	 * Sets the position of this subgraph. The subgraph will be moved so that
	 * it's center is located in the given point.
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
}
