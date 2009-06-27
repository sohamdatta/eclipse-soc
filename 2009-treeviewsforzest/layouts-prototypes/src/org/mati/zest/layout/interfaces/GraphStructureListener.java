package org.mati.zest.layout.interfaces;

public interface GraphStructureListener {

	public class Stub implements GraphStructureListener {

		public boolean nodeAdded(LayoutContext context, NodeLayout node) {
			return false;
		}

		public boolean nodeRemoved(LayoutContext context, NodeLayout node) {
			return false;
		}

		public boolean connectionAdded(LayoutContext context, ConnectionLayout connection) {
			return false;
		}

		public boolean connectionRemoved(LayoutContext context, ConnectionLayout connection) {
			return false;
		}
	}

	/**
	 * This method is called whenever a node is added to a context. No separate
	 * events will be fired for eventual connections adjacent to the added node.
	 * 
	 * If true is returned, it means that the receiving listener has intercepted
	 * this event. Intercepted events will not be passed to the rest of the
	 * listeners. If the event is not intercepted by any listener,
	 * {@link LayoutAlgorithm#applyLayout() applyLayout()} will be called on the
	 * context's main algorithm.
	 * 
	 * @param context
	 *            the layout context that fired the event
	 * @param node
	 *            the added node
	 * @return true if no further operations after this event are required
	 */
	public boolean nodeAdded(LayoutContext context, NodeLayout node);

	/**
	 * This method is called whenever a node is removed from a context. No
	 * separate events will be fired for eventual connections adjacent to the
	 * removed node.
	 * 
	 * If true is returned, it means that the receiving listener has intercepted
	 * this event. Intercepted events will not be passed to the rest of the
	 * listeners. If the event is not intercepted by any listener,
	 * {@link LayoutAlgorithm#applyLayout() applyLayout()} will be called on the
	 * context's main algorithm.
	 * 
	 * @param context
	 *            the context that fired the event
	 * @param node
	 *            the removed node
	 * @return true if no further operations after this event are required
	 */
	public boolean nodeRemoved(LayoutContext context, NodeLayout node);

	/**
	 * This method is called whenever a connection is added to a context. It can
	 * be assumed that both source and target nodes of the added connection
	 * already exist in the context.
	 * 
	 * If true is returned, it means that the receiving listener has intercepted
	 * this event. Intercepted events will not be passed to the rest of the
	 * listeners. If the event is not intercepted by any listener,
	 * {@link LayoutAlgorithm#applyLayout() applyLayout()} will be called on the
	 * context's main algorithm.
	 * 
	 * @param context
	 *            the context that fired the event
	 * @param connection
	 *            the added connection
	 * @return true if no further operations after this event are required
	 */
	public boolean connectionAdded(LayoutContext context, ConnectionLayout connection);

	/**
	 * This method is called whenever a connection is removed from a context. It
	 * can be assumed that both source and target nodes of the removed
	 * connection still exist in the context and will not be removed along with
	 * it.
	 * 
	 * If true is returned, it means that the receiving listener has intercepted
	 * this event. Intercepted events will not be passed to the rest of the
	 * listeners. If the event is not intercepted by any listener,
	 * {@link LayoutAlgorithm#applyLayout() applyLayout()} will be called on the
	 * context's main algorithm.
	 * 
	 * @param context
	 *            the context that fired the event
	 * @param connection
	 *            the added connection
	 * @return true if no further operations after this event are required
	 */
	public boolean connectionRemoved(LayoutContext context, ConnectionLayout connection);

}