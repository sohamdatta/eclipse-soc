package org.eclipse.zest.layout.interfaces;


/**
 * An interface for subgraphs in layout. A subgraph is a set of pruned nodes
 * that will be displayed as one element. A subgraph must contain at least one
 * node (empty subgraphs will be removed from its context). Every node can
 * belong to at most one subgraph.
 */
public interface SubgraphLayout extends EntityLayout {

	/**
	 * Returns all the nodes belonging to this subgraph. Replacing elements in
	 * the returned array does not affect this subgraph.
	 * 
	 * @return array of nodes
	 */
	public NodeLayout[] getNodes();

	/**
	 * 
	 * @return number of nodes pruned into this subgraph
	 */
	public int countNodes();

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
	 * Returns true if this subgraph is visualized as a particular object on the
	 * graph. If this method returns false, it means that this subgraph will not
	 * be visible so all methods related to location and size should be ignored.
	 * 
	 * @return whether or not this subgraph is a graph entity that should be
	 *         laid out.
	 */
	public boolean isGraphEntity();

}
