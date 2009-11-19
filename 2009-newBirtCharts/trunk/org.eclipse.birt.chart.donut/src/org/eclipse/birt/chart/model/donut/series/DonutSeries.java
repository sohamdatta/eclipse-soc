/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.birt.chart.model.donut.series;

import org.eclipse.birt.chart.model.type.PieSeries;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Donut Series</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * 
 * 
 * 			
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.birt.chart.model.donut.series.DonutSeries#getThickness <em>Thickness</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.birt.chart.model.donut.series.SeriesPackage#getDonutSeries()
 * @model extendedMetaData="name='DonutSeries' kind='elementOnly'"
 * @generated
 */
public interface DonutSeries extends PieSeries {
	/**
	 * Returns the value of the '<em><b>Thickness</b></em>' attribute.
	 * The default value is <code>"0"</code>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * 
	 * 								Specifies the
	 * 								thickness of the donut.
	 *               				
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Thickness</em>' attribute.
	 * @see #isSetThickness()
	 * @see #unsetThickness()
	 * @see #setThickness(int)
	 * @see org.eclipse.birt.chart.model.donut.series.SeriesPackage#getDonutSeries_Thickness()
	 * @model default="0" unsettable="true" dataType="org.eclipse.emf.ecore.xml.type.Int"
	 *        extendedMetaData="kind='element' name='Thickness'"
	 * @generated
	 */
	int getThickness();

	/**
	 * Sets the value of the '{@link org.eclipse.birt.chart.model.donut.series.DonutSeries#getThickness <em>Thickness</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Thickness</em>' attribute.
	 * @see #isSetThickness()
	 * @see #unsetThickness()
	 * @see #getThickness()
	 * @generated
	 */
	void setThickness(int value);

	/**
	 * Unsets the value of the '{@link org.eclipse.birt.chart.model.donut.series.DonutSeries#getThickness <em>Thickness</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetThickness()
	 * @see #getThickness()
	 * @see #setThickness(int)
	 * @generated
	 */
	void unsetThickness();

	/**
	 * Returns whether the value of the '{@link org.eclipse.birt.chart.model.donut.series.DonutSeries#getThickness <em>Thickness</em>}' attribute is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Thickness</em>' attribute is set.
	 * @see #unsetThickness()
	 * @see #getThickness()
	 * @see #setThickness(int)
	 * @generated
	 */
	boolean isSetThickness();

} // DonutSeries
