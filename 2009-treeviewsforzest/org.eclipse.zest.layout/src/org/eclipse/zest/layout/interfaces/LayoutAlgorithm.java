package org.eclipse.zest.layout.interfaces;

/**
 * An interface for all layout algorithms.
 * 
 * 
 */
public interface LayoutAlgorithm {

	/**
	 * Sets the layout context for this algorithm. The receiver will unregister
	 * from its previous layout context and register to the new one
	 * (registration means for example adding listeners). After a call to this
	 * method, the receiving algorithm can compute and cache internal data
	 * related to given context and perform an initial layout.
	 * 
	 * @param context
	 *            a new layout context or null if this algorithm should not
	 *            perform any layout
	 */
	public void setLayoutContext(LayoutContext context);

	/**
	 * Makes this algorithm perform layout computation and apply it to its
	 * context. The receiver should assume that the layout context has changed
	 * significantly and recompute the layout even if it keeps track of changes
	 * with listeners.
	 */
	public void applyLayout();

}
