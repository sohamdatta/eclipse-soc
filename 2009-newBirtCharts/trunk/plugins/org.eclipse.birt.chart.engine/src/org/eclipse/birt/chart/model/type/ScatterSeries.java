/***********************************************************************
 * Copyright (c) 2004 Actuate Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Actuate Corporation - initial API and implementation
 ***********************************************************************/

package org.eclipse.birt.chart.model.type;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Scatter Series</b></em>'. <!-- end-user-doc
 * -->
 * 
 * <!-- begin-model-doc -->
 * 
 * This is a Series type that holds data for Scatter Charts.
 * 
 * <!-- end-model-doc -->
 * 
 * 
 * @see org.eclipse.birt.chart.model.type.TypePackage#getScatterSeries()
 * @model
 * @generated
 */
public interface ScatterSeries extends LineSeries
{

	/**
	 * A convenient method to get an instance copy. This is much faster than the
	 * ECoreUtil.copy().
	 */
	ScatterSeries copyInstance( );

} // ScatterSeries
