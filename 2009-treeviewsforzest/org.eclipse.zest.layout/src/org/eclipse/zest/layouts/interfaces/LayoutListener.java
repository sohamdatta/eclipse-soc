/*******************************************************************************
 * Copyright (c) 2009 Mateusz Matela and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Mateusz Matela - initial API and implementation
 *               Ian Bull
 ******************************************************************************/
package org.eclipse.zest.layouts.interfaces;

import org.eclipse.zest.layouts.LayoutAlgorithm;

/**
 * A listener added to {@link LayoutContext} that is notified about changes in
 * this layout.
 */
public interface LayoutListener {

	/**
	 * This method is called whenever location of a particular entity (node or
	 * subgraph) is changed within observed context. If true is returned, it
	 * means that the receiving listener has intercepted this event. Intercepted
	 * events will not be passed to the rest of the listeners. If the event is
	 * not intercepted by any listener, {@link LayoutAlgorithm#applyLayout()
	 * applyLayout()} will be called on the context's main algorithm.
	 * 
	 * @param context
	 *            the layout context that fired the event
	 * @param entity
	 *            the entity that has moved
	 * @return true if no further operations after this event are required
	 */
	public boolean entityMoved(LayoutContext context, EntityLayout entity);

	/**
	 * This method is called whenever size of a particular entity (node or
	 * subgraph) is changed within observed context. This usually implicates
	 * change of position (the center of the entity) and the receiver should be
	 * aware of it (no additional
	 * {@link #entityMoved(LayoutContext, EntityLayout)} event will be fired).
	 * 
	 * If true is returned, it means that the receiving listener has intercepted
	 * this event. Intercepted events will not be passed to the rest of the
	 * listeners. If the event is not intercepted by any listener,
	 * {@link LayoutAlgorithm#applyLayout() applyLayout()} will be called on the
	 * context's main algorithm.
	 * 
	 * @param context
	 *            the layout context that fired the event
	 * @param entity
	 *            the entity that was resized
	 * @return true if no further operations after this event are required
	 */
	public boolean entityResized(LayoutContext context, EntityLayout entity);
}
