/**
 * <copyright>
 * </copyright>
 *
 * $Id: MultiURLValues.java,v 1.2 2009/03/12 09:24:17 ywang1 Exp $
 */

package org.eclipse.birt.chart.model.attribute;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.EMap;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Multi URL Values</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * The type is defined to store multiple hyperlink values for 'url_redirection' action, it enables multiple options for an 'url-redirection' action.
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.birt.chart.model.attribute.MultiURLValues#getURLValues <em>URL Values</em>}</li>
 *   <li>{@link org.eclipse.birt.chart.model.attribute.MultiURLValues#getTooltip <em>Tooltip</em>}</li>
 *   <li>{@link org.eclipse.birt.chart.model.attribute.MultiURLValues#getPropertiesMap <em>Properties Map</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.birt.chart.model.attribute.AttributePackage#getMultiURLValues()
 * @model extendedMetaData="name='MultiURLValues' kind='elementOnly'"
 * @generated
 */
public interface MultiURLValues extends ActionValue
{

	/**
	 * Returns the value of the '<em><b>URL Values</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.birt.chart.model.attribute.URLValue}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>URL Values</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>URL Values</em>' containment reference list.
	 * @see org.eclipse.birt.chart.model.attribute.AttributePackage#getMultiURLValues_URLValues()
	 * @model containment="true"
	 *        extendedMetaData="kind='element' name='URLValues'"
	 * @generated
	 */
	EList<URLValue> getURLValues( );

	/**
	 * Returns the value of the '<em><b>Tooltip</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Tooltip</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Tooltip</em>' attribute.
	 * @see #setTooltip(String)
	 * @see org.eclipse.birt.chart.model.attribute.AttributePackage#getMultiURLValues_Tooltip()
	 * @model dataType="org.eclipse.emf.ecore.xml.type.String" required="true"
	 *        extendedMetaData="kind='element' name='Tooltip'"
	 * @generated
	 */
	String getTooltip( );

	/**
	 * Sets the value of the '{@link org.eclipse.birt.chart.model.attribute.MultiURLValues#getTooltip <em>Tooltip</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Tooltip</em>' attribute.
	 * @see #getTooltip()
	 * @generated
	 */
	void setTooltip( String value );

	/**
	 * Returns the value of the '<em><b>Properties Map</b></em>' map.
	 * The key is of type {@link java.lang.String},
	 * and the value is of type {@link java.lang.String},
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * The map is used to store styles properties for hyperlink menu.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Properties Map</em>' map.
	 * @see org.eclipse.birt.chart.model.attribute.AttributePackage#getMultiURLValues_PropertiesMap()
	 * @model mapType="org.eclipse.birt.chart.model.attribute.EStringToStringMapEntry<org.eclipse.emf.ecore.xml.type.String, org.eclipse.emf.ecore.xml.type.String>"
	 *        extendedMetaData="kind='element' name='PropertiesMap'"
	 * @generated
	 */
	EMap<String, String> getPropertiesMap( );

	/**
	 * A convenient method to get an instance copy. This is much faster than the
	 * ECoreUtil.copy().
	 */
	MultiURLValues copyInstance( );

} // MultiURLValues
