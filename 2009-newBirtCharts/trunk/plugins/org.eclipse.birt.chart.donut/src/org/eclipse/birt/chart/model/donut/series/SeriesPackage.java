/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.birt.chart.model.donut.series;

import org.eclipse.birt.chart.model.type.TypePackage;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;

/**
 * <!-- begin-user-doc -->
 * The <b>Package</b> for the model.
 * It contains accessors for the meta objects to represent
 * <ul>
 *   <li>each class,</li>
 *   <li>each feature of each class,</li>
 *   <li>each enum,</li>
 *   <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * <!-- begin-model-doc -->
 * 
 * 		Schema file for the chart.model package.
 * 		
 * <!-- end-model-doc -->
 * @see org.eclipse.birt.chart.model.donut.series.SeriesFactory
 * @model kind="package"
 * @generated
 */
public interface SeriesPackage extends EPackage {
	/**
	 * The package name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNAME = "series";

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_URI = "http://www.birt.eclipse.org/ChartModelDonutSeries";

	/**
	 * The package namespace name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_PREFIX = "series";

	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	SeriesPackage eINSTANCE = org.eclipse.birt.chart.model.donut.series.impl.SeriesPackageImpl.init();

	/**
	 * The meta object id for the '{@link org.eclipse.birt.chart.model.donut.series.impl.DonutSeriesImpl <em>Donut Series</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.birt.chart.model.donut.series.impl.DonutSeriesImpl
	 * @see org.eclipse.birt.chart.model.donut.series.impl.SeriesPackageImpl#getDonutSeries()
	 * @generated
	 */
	int DONUT_SERIES = 0;

	/**
	 * The feature id for the '<em><b>Visible</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DONUT_SERIES__VISIBLE = TypePackage.PIE_SERIES__VISIBLE;

	/**
	 * The feature id for the '<em><b>Label</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DONUT_SERIES__LABEL = TypePackage.PIE_SERIES__LABEL;

	/**
	 * The feature id for the '<em><b>Data Definition</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DONUT_SERIES__DATA_DEFINITION = TypePackage.PIE_SERIES__DATA_DEFINITION;

	/**
	 * The feature id for the '<em><b>Series Identifier</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DONUT_SERIES__SERIES_IDENTIFIER = TypePackage.PIE_SERIES__SERIES_IDENTIFIER;

	/**
	 * The feature id for the '<em><b>Data Point</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DONUT_SERIES__DATA_POINT = TypePackage.PIE_SERIES__DATA_POINT;

	/**
	 * The feature id for the '<em><b>Data Sets</b></em>' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DONUT_SERIES__DATA_SETS = TypePackage.PIE_SERIES__DATA_SETS;

	/**
	 * The feature id for the '<em><b>Label Position</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DONUT_SERIES__LABEL_POSITION = TypePackage.PIE_SERIES__LABEL_POSITION;

	/**
	 * The feature id for the '<em><b>Stacked</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DONUT_SERIES__STACKED = TypePackage.PIE_SERIES__STACKED;

	/**
	 * The feature id for the '<em><b>Triggers</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DONUT_SERIES__TRIGGERS = TypePackage.PIE_SERIES__TRIGGERS;

	/**
	 * The feature id for the '<em><b>Translucent</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DONUT_SERIES__TRANSLUCENT = TypePackage.PIE_SERIES__TRANSLUCENT;

	/**
	 * The feature id for the '<em><b>Curve Fitting</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DONUT_SERIES__CURVE_FITTING = TypePackage.PIE_SERIES__CURVE_FITTING;

	/**
	 * The feature id for the '<em><b>Cursor</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
//	int DONUT_SERIES__CURSOR = TypePackage.PIE_SERIES__CURSOR;

	/**
	 * The feature id for the '<em><b>Explosion</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DONUT_SERIES__EXPLOSION = TypePackage.PIE_SERIES__EXPLOSION;

	/**
	 * The feature id for the '<em><b>Explosion Expression</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DONUT_SERIES__EXPLOSION_EXPRESSION = TypePackage.PIE_SERIES__EXPLOSION_EXPRESSION;

	/**
	 * The feature id for the '<em><b>Title</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DONUT_SERIES__TITLE = TypePackage.PIE_SERIES__TITLE;

	/**
	 * The feature id for the '<em><b>Title Position</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DONUT_SERIES__TITLE_POSITION = TypePackage.PIE_SERIES__TITLE_POSITION;

	/**
	 * The feature id for the '<em><b>Leader Line Attributes</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DONUT_SERIES__LEADER_LINE_ATTRIBUTES = TypePackage.PIE_SERIES__LEADER_LINE_ATTRIBUTES;

	/**
	 * The feature id for the '<em><b>Leader Line Style</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DONUT_SERIES__LEADER_LINE_STYLE = TypePackage.PIE_SERIES__LEADER_LINE_STYLE;

	/**
	 * The feature id for the '<em><b>Leader Line Length</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DONUT_SERIES__LEADER_LINE_LENGTH = TypePackage.PIE_SERIES__LEADER_LINE_LENGTH;

	/**
	 * The feature id for the '<em><b>Slice Outline</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DONUT_SERIES__SLICE_OUTLINE = TypePackage.PIE_SERIES__SLICE_OUTLINE;

	/**
	 * The feature id for the '<em><b>Ratio</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DONUT_SERIES__RATIO = TypePackage.PIE_SERIES__RATIO;

	/**
	 * The feature id for the '<em><b>Rotation</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DONUT_SERIES__ROTATION = TypePackage.PIE_SERIES__ROTATION;

	/**
	 * The feature id for the '<em><b>Thickness</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DONUT_SERIES__THICKNESS = TypePackage.PIE_SERIES_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Donut Series</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DONUT_SERIES_FEATURE_COUNT = TypePackage.PIE_SERIES_FEATURE_COUNT + 1;


	/**
	 * Returns the meta object for class '{@link org.eclipse.birt.chart.model.donut.series.DonutSeries <em>Donut Series</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Donut Series</em>'.
	 * @see org.eclipse.birt.chart.model.donut.series.DonutSeries
	 * @generated
	 */
	EClass getDonutSeries();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.birt.chart.model.donut.series.DonutSeries#getThickness <em>Thickness</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Thickness</em>'.
	 * @see org.eclipse.birt.chart.model.donut.series.DonutSeries#getThickness()
	 * @see #getDonutSeries()
	 * @generated
	 */
	EAttribute getDonutSeries_Thickness();

	/**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	SeriesFactory getSeriesFactory();

	/**
	 * <!-- begin-user-doc -->
	 * Defines literals for the meta objects that represent
	 * <ul>
	 *   <li>each class,</li>
	 *   <li>each feature of each class,</li>
	 *   <li>each enum,</li>
	 *   <li>and each data type</li>
	 * </ul>
	 * <!-- end-user-doc -->
	 * @generated
	 */
	interface Literals {
		/**
		 * The meta object literal for the '{@link org.eclipse.birt.chart.model.donut.series.impl.DonutSeriesImpl <em>Donut Series</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.birt.chart.model.donut.series.impl.DonutSeriesImpl
		 * @see org.eclipse.birt.chart.model.donut.series.impl.SeriesPackageImpl#getDonutSeries()
		 * @generated
		 */
		EClass DONUT_SERIES = eINSTANCE.getDonutSeries();

		/**
		 * The meta object literal for the '<em><b>Thickness</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute DONUT_SERIES__THICKNESS = eINSTANCE.getDonutSeries_Thickness();

	}

} //SeriesPackage
