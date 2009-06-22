package org.mati.zest.layout.interfaces;


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

}
