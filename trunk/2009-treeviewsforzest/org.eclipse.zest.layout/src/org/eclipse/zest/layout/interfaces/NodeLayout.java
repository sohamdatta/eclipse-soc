package org.eclipse.zest.layout.interfaces;


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
	 * connected with this node by a bidirectional connection are considered
	 * both successors and predecessors. Any subsequent changes to the returned
	 * array do not affect this node.
	 * 
	 * @return array of successors of this node
	 */
	public NodeLayout[] getSuccessingNodes();

	/**
	 * Returns all nodes that are direct predecessors of this node. Nodes
	 * connected with this node by a bidirectional connection are considered
	 * both successors and predecessors. Any subsequent changes to the returned
	 * array do not affect this node.
	 * 
	 * @return array of predecessors of this node
	 */
	public NodeLayout[] getPredecessingNodes();

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

	/**
	 * Sets the minimized state of this node. Node that is minimized resizes its
	 * figure to (0, 0). When it is unminimized, it resizes back to previous
	 * dimension. The node's size property is not affected by minimized state,
	 * so a node can be minimized even if it's not resizable.
	 * 
	 * @param minimized
	 *            new minimized state
	 */
	public void setMinimized(boolean minimized);

	/**
	 * @see #setMinimized(boolean)
	 * 
	 * @return true if this node is minimized
	 */
	public boolean isMinimized();

}
