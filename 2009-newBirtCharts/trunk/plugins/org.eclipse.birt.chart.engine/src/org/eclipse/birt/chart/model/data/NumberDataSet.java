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

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Number Data Set</b></em>'. <!--
 * end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * 
 * 			This type holds the numeric data associated with a series.
 * 			
 * <!-- end-model-doc -->
 *
 *
 * @see org.eclipse.birt.chart.model.data.DataPackage#getNumberDataSet()
 * @model extendedMetaData="name='NumberDataSet' kind='elementOnly'"
 * @generated
 */
public interface NumberDataSet extends DataSet
{

	/**
	 * A convenient method to get an instance copy. This is much faster than the
	 * ECoreUtil.copy().
	 */
	NumberDataSet copyInstance( );

} // NumberDataSet
