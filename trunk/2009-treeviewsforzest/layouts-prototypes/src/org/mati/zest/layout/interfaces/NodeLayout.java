package org.mati.zest.layout.interfaces;


public interface NodeLayout extends EntityLayout {

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
	 * Returns all nodes that are direct successors of this node. Nodes
	 * connected with this node with a bidirectional connection are considered
	 * both successors and predecessors. Any subsequent changes to the returned
	 * array do not affect this node.
	 * 
	 * @return array of successors of this node
	 */
	public NodeLayout[] getDirectSuccessors();

	/**
	 * Returns all nodes that are direct predecessors of this node. Nodes
	 * connected with this node with a bidirectional connection are considered
	 * both successors and predecessors. Any subsequent changes to the returned
	 * array do not affect this node.
	 * 
	 * @return array of predecessors of this node
	 */
	public NodeLayout[] getDirectPredecessors();

	/**
	 * Returns all connections that have this node as a target. All connections
	 * that are bidirectional and are adjacent to this node will be also
	 * included in the result. Any subsequent changes to the returned array do
	 * not affect this node.
	 * 
	 * @return array of connections entering this node
	 */
	public ConnectionLayout[] getIncomingConnections();

	/**
	 * Returns all connections that have this node as a source. All connections
	 * that are bidirectional and are adjacent to this node will be also
	 * included in the result. Any subsequent changes to the returned array do
	 * not affect this node.
	 * 
	 * @return array of connections leaving this node
	 */
	public ConnectionLayout[] getOutgoingConnections();

}
