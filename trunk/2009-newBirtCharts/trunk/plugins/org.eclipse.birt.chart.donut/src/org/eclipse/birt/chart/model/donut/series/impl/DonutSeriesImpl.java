/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.birt.chart.model.donut.series.impl;

import org.eclipse.birt.chart.engine.i18n.Messages;
import org.eclipse.birt.chart.model.component.Series;
import org.eclipse.birt.chart.model.donut.series.DonutSeries;
import org.eclipse.birt.chart.model.donut.series.SeriesFactory;
import org.eclipse.birt.chart.model.donut.series.SeriesPackage;
import org.eclipse.birt.chart.model.type.impl.PieSeriesImpl;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Donut Series</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.birt.chart.model.donut.series.impl.DonutSeriesImpl#getThickness <em>Thickness</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class DonutSeriesImpl extends PieSeriesImpl implements DonutSeries {
	/**
	 * The default value of the '{@link #getThickness() <em>Thickness</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getThickness()
	 * @generated
	 * @ordered
	 */
	protected static final int THICKNESS_EDEFAULT = 0;

	/**
	 * The cached value of the '{@link #getThickness() <em>Thickness</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getThickness()
	 * @generated
	 * @ordered
	 */
	protected int thickness = THICKNESS_EDEFAULT;

	/**
	 * This is true if the Thickness attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean thicknessESet;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected DonutSeriesImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return SeriesPackage.Literals.DONUT_SERIES;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public int getThickness() {
		return thickness;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setThickness(int newThickness) {
		int oldThickness = thickness;
		thickness = newThickness;
		boolean oldThicknessESet = thicknessESet;
		thicknessESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, SeriesPackage.DONUT_SERIES__THICKNESS, oldThickness, thickness, !oldThicknessESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void unsetThickness() {
		int oldThickness = thickness;
		boolean oldThicknessESet = thicknessESet;
		thickness = THICKNESS_EDEFAULT;
		thicknessESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, SeriesPackage.DONUT_SERIES__THICKNESS, oldThickness, THICKNESS_EDEFAULT, oldThicknessESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isSetThickness() {
		return thicknessESet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case SeriesPackage.DONUT_SERIES__THICKNESS:
				return getThickness();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case SeriesPackage.DONUT_SERIES__THICKNESS:
				setThickness((Integer)newValue);
				return;
		}
		super.eSet(featureID, newValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eUnset(int featureID) {
		switch (featureID) {
			case SeriesPackage.DONUT_SERIES__THICKNESS:
				unsetThickness();
				return;
		}
		super.eUnset(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean eIsSet(int featureID) {
		switch (featureID) {
			case SeriesPackage.DONUT_SERIES__THICKNESS:
				return isSetThickness();
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String toString() {
		if (eIsProxy()) return super.toString();

		StringBuffer result = new StringBuffer(super.toString());
		result.append(" (thickness: ");
		if (thicknessESet) result.append(thickness); else result.append("<unset>");
		result.append(')');
		return result.toString();
	}
	
	
	/**
	 * A convenience method to create an initialized 'Series' instance
	 * 
	 * @return
	 */
	public static Series create() {
		final DonutSeries se = SeriesFactory.eINSTANCE.createDonutSeries();
		((DonutSeriesImpl) se).initialize();
		return se;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.chart.model.component.Series#initialize()
	 */
	protected void initialize() {
		super.initialize();
		setThickness(THICKNESS_EDEFAULT);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.chart.model.component.Series#getDisplayName()
	 */
	public String getDisplayName() {
		return Messages.getString("DonutSeriesImpl.displayName"); //$NON-NLS-1$
	}

	/**
	 * A convenient method to get an instance copy. This is much faster than the
	 * ECoreUtil.copy().
	 */
	public DonutSeries copyInstance() {
		DonutSeriesImpl dest = new DonutSeriesImpl();
		dest.set(this);
		return dest;
	}

	protected void set(DonutSeries src) {
		super.set(src);

		thickness = src.getThickness();
		if (thickness != 0)
			thicknessESet = true;
	}

	public static DonutSeries create(EObject parent, EReference ref) {
		return new DonutSeriesImpl();
	}

} //DonutSeriesImpl
