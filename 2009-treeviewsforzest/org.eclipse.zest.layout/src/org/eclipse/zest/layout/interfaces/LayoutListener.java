package org.eclipse.zest.layout.interfaces;

public interface LayoutListener {

	/**
	 * This method is called whenever location of a particular node is changed
	 * within observed context. If true is returned, it means that the receiving
	 * listener has intercepted this event. Intercepted events will not be
	 * passed to the rest of the listeners. If the event is not intercepted by
	 * any listener, {@link LayoutAlgorithm#applyLayout() applyLayout()} will be
	 * called on the context's main algorithm.
	 * 
	 * @param context
	 *            the layout context that fired the event
	 * @param node
	 *            the node that has moved
	 * @return true if no further operations after this event are required
	 */
	public boolean nodeMoved(LayoutContext context, NodeLayout node);

	/**
	 * This method is called whenever size of a particular node is changed
	 * within observed context. If true is returned, it means that the receiving
	 * listener has intercepted this event. Intercepted events will not be
	 * passed to the rest of the listeners. If the event is not intercepted by
	 * any listener, {@link LayoutAlgorithm#applyLayout() applyLayout()} will be
	 * called on the context's main algorithm.
	 * 
	 * @param context
	 *            the layout context that fired the event
	 * @param node
	 *            the node that was resized
	 * @return true if no further operations after this event are required
	 */
	public boolean nodeResized(LayoutContext context, NodeLayout node);

	/**
	 * This method is called whenever location of a particular subgraph is
	 * changed within observed context. If true is returned, it means that the
	 * receiving listener has intercepted this event. Intercepted events will
	 * not be passed to the rest of the listeners. If the event is not
	 * intercepted by any listener, {@link LayoutAlgorithm#applyLayout()
	 * applyLayout()} will be called on the context's main algorithm.
	 * 
	 * @param context
	 *            the layout context that fired the event
	 * @param node
	 *            the node that has moved
	 * @return true if no further operations after this event are required
	 */
	public boolean subgraphMoved(LayoutContext context, SubgraphLayout node);

	/**
	 * This method is called whenever size of a particular subgraph is changed
	 * within observed context. If true is returned, it means that the receiving
	 * listener has intercepted this event. Intercepted events will not be
	 * passed to the rest of the listeners. If the event is not intercepted by
	 * any listener, {@link LayoutAlgorithm#applyLayout() applyLayout()} will be
	 * called on the context's main algorithm.
	 * 
	 * @param context
	 *            the layout context that fired the event
	 * @param node
	 *            the node that was resized
	 * @return true if no further operations after this event are required
	 */
	public boolean subgraphResized(LayoutContext context, SubgraphLayout node);
}
