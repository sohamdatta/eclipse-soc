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

package org.eclipse.birt.chart.model.attribute;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Location</b></em>'. <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * 
 * 			This type defines the location of an element.
 * 			
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.birt.chart.model.attribute.Location#getX <em>X</em>}</li>
 *   <li>{@link org.eclipse.birt.chart.model.attribute.Location#getY <em>Y</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.birt.chart.model.attribute.AttributePackage#getLocation()
 * @model extendedMetaData="name='Location' kind='elementOnly'"
 * @generated
 */
public interface Location extends EObject
{

	/**
	 * Returns the value of the '<em><b>X</b></em>' attribute.
	 * <!-- begin-user-doc --> Returns the 'X' co-ordinate
	 * component of the location. <!-- end-user-doc -->
	 * @return the value of the '<em>X</em>' attribute.
	 * @see #isSetX()
	 * @see #unsetX()
	 * @see #setX(double)
	 * @see org.eclipse.birt.chart.model.attribute.AttributePackage#getLocation_X()
	 * @model unsettable="true" dataType="org.eclipse.emf.ecore.xml.type.Double" required="true"
	 *        extendedMetaData="kind='element' name='x'"
	 * @generated
	 */
	double getX( );

	/**
	 * Sets the value of the '{@link org.eclipse.birt.chart.model.attribute.Location#getX <em>X</em>}' attribute. <!--
	 * begin-user-doc --> Sets the 'X' co-ordinate component of the location. <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>X</em>' attribute.
	 * @see #isSetX()
	 * @see #unsetX()
	 * @see #getX()
	 * @generated
	 */
	void setX( double value );

	/**
	 * Unsets the value of the '{@link org.eclipse.birt.chart.model.attribute.Location#getX <em>X</em>}' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @see #isSetX()
	 * @see #getX()
	 * @see #setX(double)
	 * @generated
	 */
	void unsetX( );

	/**
	 * Returns whether the value of the '{@link org.eclipse.birt.chart.model.attribute.Location#getX <em>X</em>}' attribute is set.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @return whether the value of the '<em>X</em>' attribute is set.
	 * @see #unsetX()
	 * @see #getX()
	 * @see #setX(double)
	 * @generated
	 */
	boolean isSetX( );

	/**
	 * Returns the value of the '<em><b>Y</b></em>' attribute.
	 * <!-- begin-user-doc --> Returns the 'Y' co-ordinate
	 * component of the location. <!-- end-user-doc -->
	 * @return the value of the '<em>Y</em>' attribute.
	 * @see #isSetY()
	 * @see #unsetY()
	 * @see #setY(double)
	 * @see org.eclipse.birt.chart.model.attribute.AttributePackage#getLocation_Y()
	 * @model unsettable="true" dataType="org.eclipse.emf.ecore.xml.type.Double" required="true"
	 *        extendedMetaData="kind='element' name='y'"
	 * @generated
	 */
	double getY( );

	/**
	 * Sets the value of the '{@link org.eclipse.birt.chart.model.attribute.Location#getY <em>Y</em>}' attribute. <!--
	 * begin-user-doc --> Sets the 'Y' co-ordinate component of the location. <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Y</em>' attribute.
	 * @see #isSetY()
	 * @see #unsetY()
	 * @see #getY()
	 * @generated
	 */
	void setY( double value );

	/**
	 * Unsets the value of the '{@link org.eclipse.birt.chart.model.attribute.Location#getY <em>Y</em>}' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @see #isSetY()
	 * @see #getY()
	 * @see #setY(double)
	 * @generated
	 */
	void unsetY( );

	/**
	 * Returns whether the value of the '{@link org.eclipse.birt.chart.model.attribute.Location#getY <em>Y</em>}' attribute is set.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @return whether the value of the '<em>Y</em>' attribute is set.
	 * @see #unsetY()
	 * @see #getY()
	 * @see #setY(double)
	 * @generated
	 */
	boolean isSetY( );

	/**
	 * A convenience method for defining member variables
	 * 
	 * NOTE: Manually created
	 * 
	 * @param dX
	 * @param dY
	 */
	void set( double dX, double dY );

	/**
	 * Causes the internal (x,y) values to be translated by a relative value of (dTranslateX, dTranslateY)
	 * 
	 * @param dX
	 * @param dY
	 */
	void translate( double dTranslateX, double dTranslateY );

	/**
	 * Causes the internal (x,y) values to be scaled by a relative (dScale) value
	 * 
	 * @param dScale
	 */
	void scale( double dScale );

	/**
	 * A convenient method to get an instance copy. This is much faster than the
	 * ECoreUtil.copy().
	 */
	Location copyInstance( );

} // Location
