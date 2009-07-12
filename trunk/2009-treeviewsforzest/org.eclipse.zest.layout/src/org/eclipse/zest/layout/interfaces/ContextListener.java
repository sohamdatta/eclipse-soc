package org.eclipse.zest.layout.interfaces;

public interface ContextListener {
	public class Stub implements ContextListener {

		public boolean boundsChanged(LayoutContext context) {
			return false;
		}

		public void continuousLayoutChanged(LayoutContext context) {
			// do nothing
		}

		public boolean pruningEnablementChanged(LayoutContext context) {
			return false;
		}

	}

	/**
	 * This method is called whenever the bounds available in a layout context
	 * change.
	 * 
	 * If true is returned, it means that the receiving listener has intercepted
	 * this event. Intercepted events will not be passed to the rest of the
	 * listeners. If the event is not intercepted by any listener,
	 * {@link LayoutAlgorithm#applyLayout() applyLayout()} will be called on the
	 * context's main algorithm.
	 * 
	 * @param context
	 *            the layout context that fired the event
	 * @return true if no further operations after this event are required
	 */
	public boolean boundsChanged(LayoutContext context);

	/**
	 * This method is called whenever graph pruning is enabled or disabled in a
	 * layout context.
	 * 
	 * If true is returned, it means that the receiving listener has intercepted
	 * this event. Intercepted events will not be passed to the rest of the
	 * listeners. If the event is not intercepted by any listener,
	 * {@link LayoutAlgorithm#applyLayout() applyLayout()} will be called on the
	 * context's main algorithm.
	 * 
	 * @param context
	 *            the layout context that fired the event
	 * @return true if no further operations after this event are required
	 */
	public boolean pruningEnablementChanged(LayoutContext context);

	/**
	 * This method is called whenever continuous layout is enabled or disabled
	 * in a layout context. If the receiving listener is related to a layout
	 * algorithm that supports continuous layout, it should react accordingly by
	 * starting or stopping its thread.
	 * 
	 * @param context
	 *            the layout context that fired the event
	 */
	public void continuousLayoutChanged(LayoutContext context);
}