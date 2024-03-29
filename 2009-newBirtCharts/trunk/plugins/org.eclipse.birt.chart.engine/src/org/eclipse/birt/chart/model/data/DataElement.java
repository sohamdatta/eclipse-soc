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

package org.eclipse.birt.chart.model.data;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Element</b></em>'. <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * 
 * 			This type defines a single element of data to be plotted in a chart. A data element can hold a scalar or a multi-dimensional value.
 * 			
 * <!-- end-model-doc -->
 *
 *
 * @see org.eclipse.birt.chart.model.data.DataPackage#getDataElement()
 * @model extendedMetaData="name='DataElement' kind='empty'"
 * @generated
 */
public interface DataElement extends EObject
{

	/**
	 * A convenient method to get an instance copy. This is much faster than the
	 * ECoreUtil.copy().
	 */
	DataElement copyInstance( );

} // DataElement
