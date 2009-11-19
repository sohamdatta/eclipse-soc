/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.birt.chart.model.donut.series;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * @see org.eclipse.birt.chart.model.donut.series.SeriesPackage
 * @generated
 */
public interface SeriesFactory extends EFactory {
	/**
	 * The singleton instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	SeriesFactory eINSTANCE = org.eclipse.birt.chart.model.donut.series.impl.SeriesFactoryImpl.init();

	/**
	 * Returns a new object of class '<em>Donut Series</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Donut Series</em>'.
	 * @generated
	 */
	DonutSeries createDonutSeries();

	/**
	 * Returns the package supported by this factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the package supported by this factory.
	 * @generated
	 */
	SeriesPackage getSeriesPackage();

} //SeriesFactory
