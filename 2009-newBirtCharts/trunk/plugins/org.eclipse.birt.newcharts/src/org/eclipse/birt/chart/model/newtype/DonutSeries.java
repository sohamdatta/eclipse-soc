/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.birt.chart.model.newtype;

import org.eclipse.birt.chart.model.attribute.LeaderLineStyle;
import org.eclipse.birt.chart.model.attribute.LineAttributes;
import org.eclipse.birt.chart.model.attribute.Position;

import org.eclipse.birt.chart.model.component.Label;
import org.eclipse.birt.chart.model.component.Series;
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
 *   <li>{@link org.eclipse.birt.chart.model.newtype.DonutSeries#getExplosion <em>Explosion</em>}</li>
 *   <li>{@link org.eclipse.birt.chart.model.newtype.DonutSeries#getThickness <em>Thickness</em>}</li>
 *   <li>{@link org.eclipse.birt.chart.model.newtype.DonutSeries#getRotation <em>Rotation</em>}</li>
 *   <li>{@link org.eclipse.birt.chart.model.newtype.DonutSeries#isShowDonutLabels <em>Show Donut Labels</em>}</li>
 *   <li>{@link org.eclipse.birt.chart.model.newtype.DonutSeries#getLeaderLineStyle <em>Leader Line Style</em>}</li>
 *   <li>{@link org.eclipse.birt.chart.model.newtype.DonutSeries#getLeaderLineLength <em>Leader Line Length</em>}</li>
 *   <li>{@link org.eclipse.birt.chart.model.newtype.DonutSeries#getTitle <em>Title</em>}</li>
 *   <li>{@link org.eclipse.birt.chart.model.newtype.DonutSeries#getTitlePosition <em>Title Position</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.birt.chart.model.newtype.NewtypePackage#getDonutSeries()
 * @model extendedMetaData="name='DonutSeries' kind='elementOnly'"
 * @generated
 */
public interface DonutSeries extends Series {
	
	LineAttributes getLeaderLineAttributes( );

	/**
	 * Sets the value of the '{@link org.eclipse.birt.chart.model.type.PieSeries#getLeaderLineAttributes <em>Leader Line Attributes</em>}' containment reference.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @param value the new value of the '<em>Leader Line Attributes</em>' containment reference.
	 * @see #getLeaderLineAttributes()
	 * @generated
	 */
	void setLeaderLineAttributes( LineAttributes value );
	
	
	
	/**
	 * Returns the value of the '<em><b>Explosion</b></em>' attribute.
	 * The default value is <code>"0"</code>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * 
	 * 								Specifies the explosion of the donut slices.
	 *               				
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Explosion</em>' attribute.
	 * @see #isSetExplosion()
	 * @see #unsetExplosion()
	 * @see #setExplosion(int)
	 * @see org.eclipse.birt.chart.model.newtype.NewtypePackage#getDonutSeries_Explosion()
	 * @model default="0" unsettable="true" dataType="org.eclipse.emf.ecore.xml.type.Int" required="true"
	 *        extendedMetaData="kind='element' name='Explosion'"
	 * @generated
	 */
	int getExplosion();

	/**
	 * Sets the value of the '{@link org.eclipse.birt.chart.model.newtype.DonutSeries#getExplosion <em>Explosion</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Explosion</em>' attribute.
	 * @see #isSetExplosion()
	 * @see #unsetExplosion()
	 * @see #getExplosion()
	 * @generated
	 */
	void setExplosion(int value);

	/**
	 * Unsets the value of the '{@link org.eclipse.birt.chart.model.newtype.DonutSeries#getExplosion <em>Explosion</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetExplosion()
	 * @see #getExplosion()
	 * @see #setExplosion(int)
	 * @generated
	 */
	void unsetExplosion();

	/**
	 * Returns whether the value of the '{@link org.eclipse.birt.chart.model.newtype.DonutSeries#getExplosion <em>Explosion</em>}' attribute is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Explosion</em>' attribute is set.
	 * @see #unsetExplosion()
	 * @see #getExplosion()
	 * @see #setExplosion(int)
	 * @generated
	 */
	boolean isSetExplosion();

	
	
	/**
	 * Returns the value of the '<em><b>Thickness</b></em>' attribute.
	 * The default value is <code>"0"</code>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * 
	 * 								Specifies the thickness of the donut.
	 *               				
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Thickness</em>' attribute.
	 * @see #isSetThickness()
	 * @see #unsetThickness()
	 * @see #setThickness(int)
	 * @see org.eclipse.birt.chart.model.newtype.NewtypePackage#getDonutSeries_Thickness()
	 * @model default="0" unsettable="true" dataType="org.eclipse.emf.ecore.xml.type.Int"
	 *        extendedMetaData="kind='element' name='Thickness'"
	 * @generated
	 */
	int getThickness();

	/**
	 * Sets the value of the '{@link org.eclipse.birt.chart.model.newtype.DonutSeries#getThickness <em>Thickness</em>}' attribute.
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
	 * Unsets the value of the '{@link org.eclipse.birt.chart.model.newtype.DonutSeries#getThickness <em>Thickness</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetThickness()
	 * @see #getThickness()
	 * @see #setThickness(int)
	 * @generated
	 */
	void unsetThickness();

	/**
	 * Returns whether the value of the '{@link org.eclipse.birt.chart.model.newtype.DonutSeries#getThickness <em>Thickness</em>}' attribute is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Thickness</em>' attribute is set.
	 * @see #unsetThickness()
	 * @see #getThickness()
	 * @see #setThickness(int)
	 * @generated
	 */
	boolean isSetThickness();

	/**
	 * Returns the value of the '<em><b>Rotation</b></em>' attribute.
	 * The default value is <code>"0"</code>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * 
	 * 								Specifies the rotation of the donut.
	 *               				
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Rotation</em>' attribute.
	 * @see #isSetRotation()
	 * @see #unsetRotation()
	 * @see #setRotation(int)
	 * @see org.eclipse.birt.chart.model.newtype.NewtypePackage#getDonutSeries_Rotation()
	 * @model default="0" unsettable="true" dataType="org.eclipse.emf.ecore.xml.type.Int"
	 *        extendedMetaData="kind='element' name='Rotation'"
	 * @generated
	 */
	int getRotation();

	/**
	 * Sets the value of the '{@link org.eclipse.birt.chart.model.newtype.DonutSeries#getRotation <em>Rotation</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Rotation</em>' attribute.
	 * @see #isSetRotation()
	 * @see #unsetRotation()
	 * @see #getRotation()
	 * @generated
	 */
	void setRotation(int value);

	/**
	 * Unsets the value of the '{@link org.eclipse.birt.chart.model.newtype.DonutSeries#getRotation <em>Rotation</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetRotation()
	 * @see #getRotation()
	 * @see #setRotation(int)
	 * @generated
	 */
	void unsetRotation();

	/**
	 * Returns whether the value of the '{@link org.eclipse.birt.chart.model.newtype.DonutSeries#getRotation <em>Rotation</em>}' attribute is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Rotation</em>' attribute is set.
	 * @see #unsetRotation()
	 * @see #getRotation()
	 * @see #setRotation(int)
	 * @generated
	 */
	boolean isSetRotation();

	/**
	 * Returns the value of the '<em><b>Show Donut Labels</b></em>' attribute.
	 * The default value is <code>"false"</code>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * 
	 * 								Specifies whether to show the donut slice
	 * 								labels.
	 *            				  	
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Show Donut Labels</em>' attribute.
	 * @see #isSetShowDonutLabels()
	 * @see #unsetShowDonutLabels()
	 * @see #setShowDonutLabels(boolean)
	 * @see org.eclipse.birt.chart.model.newtype.NewtypePackage#getDonutSeries_ShowDonutLabels()
	 * @model default="false" unsettable="true" dataType="org.eclipse.emf.ecore.xml.type.Boolean"
	 *        extendedMetaData="kind='element' name='ShowDonutLabels'"
	 * @generated
	 */
	boolean isShowDonutLabels();

	/**
	 * Sets the value of the '{@link org.eclipse.birt.chart.model.newtype.DonutSeries#isShowDonutLabels <em>Show Donut Labels</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Show Donut Labels</em>' attribute.
	 * @see #isSetShowDonutLabels()
	 * @see #unsetShowDonutLabels()
	 * @see #isShowDonutLabels()
	 * @generated
	 */
	void setShowDonutLabels(boolean value);

	/**
	 * Unsets the value of the '{@link org.eclipse.birt.chart.model.newtype.DonutSeries#isShowDonutLabels <em>Show Donut Labels</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetShowDonutLabels()
	 * @see #isShowDonutLabels()
	 * @see #setShowDonutLabels(boolean)
	 * @generated
	 */
	void unsetShowDonutLabels();

	/**
	 * Returns whether the value of the '{@link org.eclipse.birt.chart.model.newtype.DonutSeries#isShowDonutLabels <em>Show Donut Labels</em>}' attribute is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Show Donut Labels</em>' attribute is set.
	 * @see #unsetShowDonutLabels()
	 * @see #isShowDonutLabels()
	 * @see #setShowDonutLabels(boolean)
	 * @generated
	 */
	boolean isSetShowDonutLabels();

	/**
	 * Returns the value of the '<em><b>Leader Line Style</b></em>' attribute.
	 * The literals are from the enumeration {@link org.eclipse.birt.chart.model.attribute.LeaderLineStyle}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Leader Line Style</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Leader Line Style</em>' attribute.
	 * @see org.eclipse.birt.chart.model.attribute.LeaderLineStyle
	 * @see #isSetLeaderLineStyle()
	 * @see #unsetLeaderLineStyle()
	 * @see #setLeaderLineStyle(LeaderLineStyle)
	 * @see org.eclipse.birt.chart.model.newtype.NewtypePackage#getDonutSeries_LeaderLineStyle()
	 * @model unsettable="true" required="true"
	 *        extendedMetaData="kind='element' name='LeaderLineStyle'"
	 * @generated
	 */
	LeaderLineStyle getLeaderLineStyle();

	/**
	 * Sets the value of the '{@link org.eclipse.birt.chart.model.newtype.DonutSeries#getLeaderLineStyle <em>Leader Line Style</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Leader Line Style</em>' attribute.
	 * @see org.eclipse.birt.chart.model.attribute.LeaderLineStyle
	 * @see #isSetLeaderLineStyle()
	 * @see #unsetLeaderLineStyle()
	 * @see #getLeaderLineStyle()
	 * @generated
	 */
	void setLeaderLineStyle(LeaderLineStyle value);

	/**
	 * Unsets the value of the '{@link org.eclipse.birt.chart.model.newtype.DonutSeries#getLeaderLineStyle <em>Leader Line Style</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetLeaderLineStyle()
	 * @see #getLeaderLineStyle()
	 * @see #setLeaderLineStyle(LeaderLineStyle)
	 * @generated
	 */
	void unsetLeaderLineStyle();

	/**
	 * Returns whether the value of the '{@link org.eclipse.birt.chart.model.newtype.DonutSeries#getLeaderLineStyle <em>Leader Line Style</em>}' attribute is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Leader Line Style</em>' attribute is set.
	 * @see #unsetLeaderLineStyle()
	 * @see #getLeaderLineStyle()
	 * @see #setLeaderLineStyle(LeaderLineStyle)
	 * @generated
	 */
	boolean isSetLeaderLineStyle();

	/**
	 * Returns the value of the '<em><b>Leader Line Length</b></em>' attribute.
	 * The default value is <code>"0.0"</code>.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Leader Line Length</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Leader Line Length</em>' attribute.
	 * @see #isSetLeaderLineLength()
	 * @see #unsetLeaderLineLength()
	 * @see #setLeaderLineLength(double)
	 * @see org.eclipse.birt.chart.model.newtype.NewtypePackage#getDonutSeries_LeaderLineLength()
	 * @model default="0.0" unsettable="true" dataType="org.eclipse.emf.ecore.xml.type.Double" required="true"
	 *        extendedMetaData="kind='element' name='LeaderLineLength'"
	 * @generated
	 */
	double getLeaderLineLength();

	/**
	 * Sets the value of the '{@link org.eclipse.birt.chart.model.newtype.DonutSeries#getLeaderLineLength <em>Leader Line Length</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Leader Line Length</em>' attribute.
	 * @see #isSetLeaderLineLength()
	 * @see #unsetLeaderLineLength()
	 * @see #getLeaderLineLength()
	 * @generated
	 */
	void setLeaderLineLength(double value);

	/**
	 * Unsets the value of the '{@link org.eclipse.birt.chart.model.newtype.DonutSeries#getLeaderLineLength <em>Leader Line Length</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetLeaderLineLength()
	 * @see #getLeaderLineLength()
	 * @see #setLeaderLineLength(double)
	 * @generated
	 */
	void unsetLeaderLineLength();

	/**
	 * Returns whether the value of the '{@link org.eclipse.birt.chart.model.newtype.DonutSeries#getLeaderLineLength <em>Leader Line Length</em>}' attribute is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Leader Line Length</em>' attribute is set.
	 * @see #unsetLeaderLineLength()
	 * @see #getLeaderLineLength()
	 * @see #setLeaderLineLength(double)
	 * @generated
	 */
	boolean isSetLeaderLineLength();

	/**
	 * Returns the value of the '<em><b>Title</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Title</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Title</em>' containment reference.
	 * @see #setTitle(Label)
	 * @see org.eclipse.birt.chart.model.newtype.NewtypePackage#getDonutSeries_Title()
	 * @model containment="true" required="true"
	 *        extendedMetaData="kind='element' name='Title'"
	 * @generated
	 */
	Label getTitle();

	/**
	 * Sets the value of the '{@link org.eclipse.birt.chart.model.newtype.DonutSeries#getTitle <em>Title</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Title</em>' containment reference.
	 * @see #getTitle()
	 * @generated
	 */
	void setTitle(Label value);

	/**
	 * Returns the value of the '<em><b>Title Position</b></em>' attribute.
	 * The literals are from the enumeration {@link org.eclipse.birt.chart.model.attribute.Position}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Title Position</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Title Position</em>' attribute.
	 * @see org.eclipse.birt.chart.model.attribute.Position
	 * @see #isSetTitlePosition()
	 * @see #unsetTitlePosition()
	 * @see #setTitlePosition(Position)
	 * @see org.eclipse.birt.chart.model.newtype.NewtypePackage#getDonutSeries_TitlePosition()
	 * @model unsettable="true" required="true"
	 *        extendedMetaData="kind='element' name='TitlePosition'"
	 * @generated
	 */
	Position getTitlePosition();

	/**
	 * Sets the value of the '{@link org.eclipse.birt.chart.model.newtype.DonutSeries#getTitlePosition <em>Title Position</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Title Position</em>' attribute.
	 * @see org.eclipse.birt.chart.model.attribute.Position
	 * @see #isSetTitlePosition()
	 * @see #unsetTitlePosition()
	 * @see #getTitlePosition()
	 * @generated
	 */
	void setTitlePosition(Position value);

	/**
	 * Unsets the value of the '{@link org.eclipse.birt.chart.model.newtype.DonutSeries#getTitlePosition <em>Title Position</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetTitlePosition()
	 * @see #getTitlePosition()
	 * @see #setTitlePosition(Position)
	 * @generated
	 */
	void unsetTitlePosition();

	/**
	 * Returns whether the value of the '{@link org.eclipse.birt.chart.model.newtype.DonutSeries#getTitlePosition <em>Title Position</em>}' attribute is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Title Position</em>' attribute is set.
	 * @see #unsetTitlePosition()
	 * @see #getTitlePosition()
	 * @see #setTitlePosition(Position)
	 * @generated
	 */
	boolean isSetTitlePosition();

	void setRatio(double i);

	double getRatio();

	boolean isSetRatio();

} // DonutSeries
