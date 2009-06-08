package org.mati.zest.layout.interfaces;

import org.eclipse.zest.layouts.dataStructures.DisplayIndependentRectangle;

/**
 * Objects implementing LayoutContext interface are used for exchanging of
 * information between layout algorithms and graphical objects displaying
 * graphs.
 */
public interface LayoutContext {

	/**
	 * Returns all the nodes that should be laid out. Replacing elements in the
	 * returned array does not affect this context.
	 * 
	 * @return array of nodes to lay out
	 */
	public NodeLayout[] getNodes();

	/**
	 * Returns all the connections between nodes that should be laid out.
	 * Replacing elements in the returned array does not affect this context.
	 * 
	 * @return array of connections between nodes
	 */
	public ConnectionLayout[] getConnections();

	/**
	 * 
	 * @return bounds in which the graph elements can be placed
	 */
	public DisplayIndependentRectangle getBounds();

	/**
	 * 
	 * @return true if a layout algorithm is allowed to place graph elements
	 *         outside of suggested bounds
	 */
	public boolean isBoundsExpandable();

	/**
	 * Returns all the subgraphs this context's nodes were pruned to. Replacing
	 * elements in the returned array does not affect this context.
	 * 
	 * @return array of subgraphs (may be empty)
	 */
	public SubgraphLayout[] getSubgraphs();

	/**
	 * Creates a subgraph containing given nodes and adds it to this context. If
	 * given nodes already belong to another subgraphs, they are removed from
	 * them prior to adding to the new subgraph.
	 * 
	 * @param nodes
	 *            nodes to add to the new subgraph
	 */
	public SubgraphLayout addSubgraph(NodeLayout[] nodes);

	/**
	 * 
	 * @return true if this layout context allows pruning nodes into subgraphs
	 */
	public boolean isPruningEnabled();

	/**
	 * 
	 * @return true if this layout context allows layout algorithm to work
	 *         continuously in background and change the layout with time or in
	 *         reaction to some events.
	 */
	public boolean isContinuousLayoutEnabled();

	/**
	 * Sets the mail layout algorithm for this context. Main algorithm will be
	 * used to relayout graph items using {@link LayoutAlgorithm#applyLayout()}
	 * after every event that is not intercepted by any listener.
	 * 
	 * @param algorithm
	 */
	public void setMainLayoutAlgorithm(LayoutAlgorithm algorithm);

	/**
	 * 
	 * @return the main algorithm of this context (see
	 *         {@link #setMainLayoutAlgorithm(LayoutAlgorithm)} for details)
	 */
	public LayoutAlgorithm getMainLayoutAlgorithm();

	/**
	 * Adds a listener to the context that will be notified about changes in
	 * this context's layout, that is movement and resizing of nodes /
	 * subgraphs. The notifications will not include changes made with API
	 * included in layout related interfaces, so that layout algorithms won't be
	 * notified about changes they invoke. Only internal changes of the system
	 * will fire events.
	 * 
	 * @param listener
	 *            listener to add
	 */
	public void addLayoutListener(LayoutListener listener);

	/**
	 * Removes a layout listener from this context.
	 * 
	 * @param listener
	 *            listener to remove
	 */
	public void removeLayoutListener(LayoutListener listener);

	/**
	 * Adds a listener to the context that will be notified about changes in
	 * graph structure, that is addition and removal of nodes and connections.
	 * The notifications will not include changes made with API included in
	 * layout related interfaces, so that layout algorithms won't be notified
	 * about changes they invoke. Only internal changes of the system will fire
	 * events.
	 * 
	 * @param listener
	 *            listener to add
	 */
	public void addGraphStructureListener(GraphStructureListener listener);

	/**
	 * Removes a graph structure listener from this context.
	 * 
	 * @param listener
	 *            listener to remove
	 */
	public void removeGraphStructureListener(GraphStructureListener listener);

	/**
	 * Adds a listener to the context that will be notified about changes
	 * related to its configuration.
	 * 
	 * @param listener
	 *            listener to add
	 */
	public void addContextListener(ContextListener listener);

	/**
	 * Removes a context listener from this context.
	 * 
	 * @param listener
	 *            listener to remove
	 */
	public void removeContextListener(ContextListener listener);

	/**
	 * Adds a listener to the context that will be notified about changes in
	 * graph pruning, that is hiding and showing of nodes. The notifications
	 * will not include changes made with API included in layout related
	 * interfaces, so that layout algorithms won't be notified about changes
	 * they invoke. Only internal changes of the system will fire events.
	 * 
	 * @param listener
	 *            listener to add
	 */
	public void addPruningListener(PruningListener listener);

	/**
	 * Removes a pruning structure listener from this context.
	 * 
	 * @param listener
	 *            listener to remove
	 */
	public void removePruningListener(PruningListener listener);

	/**
	 * Causes all the changes made to elements in this context to affect the
	 * display.
	 * 
	 * @param animationHint
	 *            a hint for display mechanism indicating whether changes are
	 *            major and should be animated (if true) or not.
	 */
	public void flushChanges(boolean animationHint);
}