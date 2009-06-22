package org.mati.zest.layout.interfaces;


public interface ConnectionLayout {

	public NodeLayout getSource();

	public NodeLayout getTarget();

	/**
	 * 
	 * @return weight assigned to this connection
	 */
	public double getWeight();

	/**
	 * Checks if this connection is directed. For undirected connections, source
	 * and target nodes should be considered just adjacent nodes without
	 * dividing to source/target.
	 * 
	 * @return true if this connection is directed
	 */
	public boolean isDirected();
}
