/*******************************************************************************
 * Copyright (c) 2000, 2009 IBM Corporation and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.zest.core.viewers;

/**
 * Listens to zoom level changes.
 * 
 * @author Eric Bordeau
 * @since 2.0
 */
public interface ZoomListener {

	/**
	 * Called whenever the ZoomManager's zoom level changes.
	 * 
	 * @param zoom
	 *            the new zoom level.
	 */
	void zoomChanged(double zoom);

}
