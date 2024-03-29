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
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Base Sample Data</b></em>'. <!--
 * end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * 
 * 			This type sample data for a base series.
 * 			
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.birt.chart.model.data.BaseSampleData#getDataSetRepresentation <em>Data Set Representation</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.birt.chart.model.data.DataPackage#getBaseSampleData()
 * @model extendedMetaData="name='BaseSampleData' kind='elementOnly'"
 * @generated
 */
public interface BaseSampleData extends EObject
{

	/**
	 * Returns the value of the '<em><b>Data Set Representation</b></em>' attribute. <!-- begin-user-doc --> <!--
	 * end-user-doc --> <!-- begin-model-doc -->
	 * 
	 * Holds the sample data for a single data set as a string in the form expected by the DataSetProcessor for the
	 * series.
	 * 
	 * <!-- end-model-doc -->
	 * 
	 * @return the value of the '<em>Data Set Representation</em>' attribute.
	 * @see #setDataSetRepresentation(String)
	 * @see org.eclipse.birt.chart.model.data.DataPackage#getBaseSampleData_DataSetRepresentation()
	 * @model unique="false" dataType="org.eclipse.emf.ecore.xml.type.String" required="true"
	 * @generated
	 */
	String getDataSetRepresentation( );

	/**
	 * Sets the value of the '{@link org.eclipse.birt.chart.model.data.BaseSampleData#getDataSetRepresentation <em>Data Set Representation</em>}' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @param value the new value of the '<em>Data Set Representation</em>' attribute.
	 * @see #getDataSetRepresentation()
	 * @generated
	 */
	void setDataSetRepresentation( String value );

	/**
	 * A convenient method to get an instance copy. This is much faster than the
	 * ECoreUtil.copy().
	 */
	BaseSampleData copyInstance( );

} // BaseSampleData
