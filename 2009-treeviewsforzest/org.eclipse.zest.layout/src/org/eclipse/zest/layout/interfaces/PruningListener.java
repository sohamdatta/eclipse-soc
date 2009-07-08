package org.eclipse.zest.layout.interfaces;

public interface PruningListener {

	/**
	 * This method is called when some nodes are pruned in a layout context.
	 * 
	 * If true is returned, it means that the receiving listener has intercepted
	 * this event. Intercepted events will not be passed to the rest of the
	 * listeners. If the event is not intercepted by any listener,
	 * {@link LayoutAlgorithm#applyLayout() applyLayout()} will be called on the
	 * context's main algorithm.
	 * 
	 * @param context
	 *            the layout context that fired the event
	 * @param subgraph
	 *            subgraphs that have been created or had nodes added
	 * @return true if no further operations after this event are required
	 */
	public boolean nodesPruned(LayoutContext context, SubgraphLayout[] subgraph);

	/**
	 * This method is called when some nodes are unpruned in a layout context,
	 * that is they are no longer part of a subgraph.
	 * 
	 * If true is returned, it means that the receiving listener has intercepted
	 * this event. Intercepted events will not be passed to the rest of the
	 * listeners. If the event is not intercepted by any listener,
	 * {@link LayoutAlgorithm#applyLayout() applyLayout()} will be called on the
	 * context's main algorithm.
	 * 
	 * @param context
	 *            the layout context that fired the event
	 * @param nodes
	 *            nodes that have been unpruned
	 * @return true if no further operations after this event are required
	 */
	public boolean nodesUnpruned(LayoutContext context, NodeLayout[] nodes);

}
