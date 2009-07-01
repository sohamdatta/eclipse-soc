/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.birt.chart.model.newtype;

import org.eclipse.birt.chart.model.component.ComponentPackage;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

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
 * @see org.eclipse.birt.chart.model.newtype.NewtypeFactory
 * @model kind="package"
 * @generated
 */
public interface NewtypePackage extends EPackage {
	/**
	 * The package name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNAME = "newtype";

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_URI = "http://www.birt.eclipse.org/ChartModelNewtype";

	/**
	 * The package namespace name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_PREFIX = "newtype";

	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	NewtypePackage eINSTANCE = org.eclipse.birt.chart.model.newtype.impl.NewtypePackageImpl.init();

	/**
	 * The meta object id for the '{@link org.eclipse.birt.chart.model.newtype.impl.DonutSeriesImpl <em>Donut Series</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.birt.chart.model.newtype.impl.DonutSeriesImpl
	 * @see org.eclipse.birt.chart.model.newtype.impl.NewtypePackageImpl#getDonutSeries()
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
	int DONUT_SERIES__VISIBLE = ComponentPackage.SERIES__VISIBLE;

	/**
	 * The feature id for the '<em><b>Label</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DONUT_SERIES__LABEL = ComponentPackage.SERIES__LABEL;

	/**
	 * The feature id for the '<em><b>Data Definition</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DONUT_SERIES__DATA_DEFINITION = ComponentPackage.SERIES__DATA_DEFINITION;

	/**
	 * The feature id for the '<em><b>Series Identifier</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DONUT_SERIES__SERIES_IDENTIFIER = ComponentPackage.SERIES__SERIES_IDENTIFIER;

	/**
	 * The feature id for the '<em><b>Data Point</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DONUT_SERIES__DATA_POINT = ComponentPackage.SERIES__DATA_POINT;

	/**
	 * The feature id for the '<em><b>Data Sets</b></em>' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DONUT_SERIES__DATA_SETS = ComponentPackage.SERIES__DATA_SETS;

	/**
	 * The feature id for the '<em><b>Label Position</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DONUT_SERIES__LABEL_POSITION = ComponentPackage.SERIES__LABEL_POSITION;

	/**
	 * The feature id for the '<em><b>Stacked</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DONUT_SERIES__STACKED = ComponentPackage.SERIES__STACKED;

	/**
	 * The feature id for the '<em><b>Triggers</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DONUT_SERIES__TRIGGERS = ComponentPackage.SERIES__TRIGGERS;

	/**
	 * The feature id for the '<em><b>Translucent</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DONUT_SERIES__TRANSLUCENT = ComponentPackage.SERIES__TRANSLUCENT;

	/**
	 * The feature id for the '<em><b>Curve Fitting</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DONUT_SERIES__CURVE_FITTING = ComponentPackage.SERIES__CURVE_FITTING;

	/**
	 * The feature id for the '<em><b>Cursor</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DONUT_SERIES__CURSOR = ComponentPackage.SERIES__CURSOR;

	/**
	 * The feature id for the '<em><b>Explosion</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DONUT_SERIES__EXPLOSION = ComponentPackage.SERIES_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Thickness</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DONUT_SERIES__THICKNESS = ComponentPackage.SERIES_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Rotation</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DONUT_SERIES__ROTATION = ComponentPackage.SERIES_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Show Donut Labels</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DONUT_SERIES__SHOW_DONUT_LABELS = ComponentPackage.SERIES_FEATURE_COUNT + 3;

	/**
	 * The feature id for the '<em><b>Leader Line Style</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DONUT_SERIES__LEADER_LINE_STYLE = ComponentPackage.SERIES_FEATURE_COUNT + 4;

	/**
	 * The feature id for the '<em><b>Leader Line Length</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DONUT_SERIES__LEADER_LINE_LENGTH = ComponentPackage.SERIES_FEATURE_COUNT + 5;

	/**
	 * The feature id for the '<em><b>Title</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DONUT_SERIES__TITLE = ComponentPackage.SERIES_FEATURE_COUNT + 6;

	/**
	 * The feature id for the '<em><b>Title Position</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DONUT_SERIES__TITLE_POSITION = ComponentPackage.SERIES_FEATURE_COUNT + 7;

	/**
	 * The number of structural features of the '<em>Donut Series</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DONUT_SERIES_FEATURE_COUNT = ComponentPackage.SERIES_FEATURE_COUNT + 8;


	/**
	 * Returns the meta object for class '{@link org.eclipse.birt.chart.model.newtype.DonutSeries <em>Donut Series</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Donut Series</em>'.
	 * @see org.eclipse.birt.chart.model.newtype.DonutSeries
	 * @generated
	 */
	EClass getDonutSeries();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.birt.chart.model.newtype.DonutSeries#getExplosion <em>Explosion</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Explosion</em>'.
	 * @see org.eclipse.birt.chart.model.newtype.DonutSeries#getExplosion()
	 * @see #getDonutSeries()
	 * @generated
	 */
	EAttribute getDonutSeries_Explosion();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.birt.chart.model.newtype.DonutSeries#getThickness <em>Thickness</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Thickness</em>'.
	 * @see org.eclipse.birt.chart.model.newtype.DonutSeries#getThickness()
	 * @see #getDonutSeries()
	 * @generated
	 */
	EAttribute getDonutSeries_Thickness();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.birt.chart.model.newtype.DonutSeries#getRotation <em>Rotation</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Rotation</em>'.
	 * @see org.eclipse.birt.chart.model.newtype.DonutSeries#getRotation()
	 * @see #getDonutSeries()
	 * @generated
	 */
	EAttribute getDonutSeries_Rotation();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.birt.chart.model.newtype.DonutSeries#isShowDonutLabels <em>Show Donut Labels</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Show Donut Labels</em>'.
	 * @see org.eclipse.birt.chart.model.newtype.DonutSeries#isShowDonutLabels()
	 * @see #getDonutSeries()
	 * @generated
	 */
	EAttribute getDonutSeries_ShowDonutLabels();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.birt.chart.model.newtype.DonutSeries#getLeaderLineStyle <em>Leader Line Style</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Leader Line Style</em>'.
	 * @see org.eclipse.birt.chart.model.newtype.DonutSeries#getLeaderLineStyle()
	 * @see #getDonutSeries()
	 * @generated
	 */
	EAttribute getDonutSeries_LeaderLineStyle();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.birt.chart.model.newtype.DonutSeries#getLeaderLineLength <em>Leader Line Length</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Leader Line Length</em>'.
	 * @see org.eclipse.birt.chart.model.newtype.DonutSeries#getLeaderLineLength()
	 * @see #getDonutSeries()
	 * @generated
	 */
	EAttribute getDonutSeries_LeaderLineLength();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.birt.chart.model.newtype.DonutSeries#getTitle <em>Title</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Title</em>'.
	 * @see org.eclipse.birt.chart.model.newtype.DonutSeries#getTitle()
	 * @see #getDonutSeries()
	 * @generated
	 */
	EReference getDonutSeries_Title();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.birt.chart.model.newtype.DonutSeries#getTitlePosition <em>Title Position</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Title Position</em>'.
	 * @see org.eclipse.birt.chart.model.newtype.DonutSeries#getTitlePosition()
	 * @see #getDonutSeries()
	 * @generated
	 */
	EAttribute getDonutSeries_TitlePosition();

	/**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	NewtypeFactory getNewtypeFactory();

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
		 * The meta object literal for the '{@link org.eclipse.birt.chart.model.newtype.impl.DonutSeriesImpl <em>Donut Series</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.birt.chart.model.newtype.impl.DonutSeriesImpl
		 * @see org.eclipse.birt.chart.model.newtype.impl.NewtypePackageImpl#getDonutSeries()
		 * @generated
		 */
		EClass DONUT_SERIES = eINSTANCE.getDonutSeries();

		/**
		 * The meta object literal for the '<em><b>Explosion</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute DONUT_SERIES__EXPLOSION = eINSTANCE.getDonutSeries_Explosion();

		/**
		 * The meta object literal for the '<em><b>Thickness</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute DONUT_SERIES__THICKNESS = eINSTANCE.getDonutSeries_Thickness();

		/**
		 * The meta object literal for the '<em><b>Rotation</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute DONUT_SERIES__ROTATION = eINSTANCE.getDonutSeries_Rotation();

		/**
		 * The meta object literal for the '<em><b>Show Donut Labels</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute DONUT_SERIES__SHOW_DONUT_LABELS = eINSTANCE.getDonutSeries_ShowDonutLabels();

		/**
		 * The meta object literal for the '<em><b>Leader Line Style</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute DONUT_SERIES__LEADER_LINE_STYLE = eINSTANCE.getDonutSeries_LeaderLineStyle();

		/**
		 * The meta object literal for the '<em><b>Leader Line Length</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute DONUT_SERIES__LEADER_LINE_LENGTH = eINSTANCE.getDonutSeries_LeaderLineLength();

		/**
		 * The meta object literal for the '<em><b>Title</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference DONUT_SERIES__TITLE = eINSTANCE.getDonutSeries_Title();

		/**
		 * The meta object literal for the '<em><b>Title Position</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute DONUT_SERIES__TITLE_POSITION = eINSTANCE.getDonutSeries_TitlePosition();

	}

} //NewtypePackage
