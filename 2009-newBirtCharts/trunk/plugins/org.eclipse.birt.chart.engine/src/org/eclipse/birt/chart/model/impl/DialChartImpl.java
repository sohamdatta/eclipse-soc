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

package org.eclipse.birt.chart.model.impl;

import org.eclipse.birt.chart.model.ChartWithoutAxes;
import org.eclipse.birt.chart.model.DialChart;
import org.eclipse.birt.chart.model.ModelFactory;
import org.eclipse.birt.chart.model.ModelPackage;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.impl.ENotificationImpl;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Dial Chart</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.birt.chart.model.impl.DialChartImpl#isDialSuperimposition <em>Dial Superimposition</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class DialChartImpl extends ChartWithoutAxesImpl implements DialChart
{

	/**
	 * The default value of the '{@link #isDialSuperimposition() <em>Dial Superimposition</em>}' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @see #isDialSuperimposition()
	 * @generated
	 * @ordered
	 */
	protected static final boolean DIAL_SUPERIMPOSITION_EDEFAULT = true;

	/**
	 * The cached value of the '{@link #isDialSuperimposition() <em>Dial Superimposition</em>}' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @see #isDialSuperimposition()
	 * @generated
	 * @ordered
	 */
	protected boolean dialSuperimposition = DIAL_SUPERIMPOSITION_EDEFAULT;

	/**
	 * This is true if the Dial Superimposition attribute has been set. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	protected boolean dialSuperimpositionESet;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	protected DialChartImpl( )
	{
		super( );
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass( )
	{
		return ModelPackage.Literals.DIAL_CHART;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isDialSuperimposition( )
	{
		return dialSuperimposition;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public void setDialSuperimposition( boolean newDialSuperimposition )
	{
		boolean oldDialSuperimposition = dialSuperimposition;
		dialSuperimposition = newDialSuperimposition;
		boolean oldDialSuperimpositionESet = dialSuperimpositionESet;
		dialSuperimpositionESet = true;
		if ( eNotificationRequired( ) )
			eNotify( new ENotificationImpl( this,
					Notification.SET,
					ModelPackage.DIAL_CHART__DIAL_SUPERIMPOSITION,
					oldDialSuperimposition,
					dialSuperimposition,
					!oldDialSuperimpositionESet ) );
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public void unsetDialSuperimposition( )
	{
		boolean oldDialSuperimposition = dialSuperimposition;
		boolean oldDialSuperimpositionESet = dialSuperimpositionESet;
		dialSuperimposition = DIAL_SUPERIMPOSITION_EDEFAULT;
		dialSuperimpositionESet = false;
		if ( eNotificationRequired( ) )
			eNotify( new ENotificationImpl( this,
					Notification.UNSET,
					ModelPackage.DIAL_CHART__DIAL_SUPERIMPOSITION,
					oldDialSuperimposition,
					DIAL_SUPERIMPOSITION_EDEFAULT,
					oldDialSuperimpositionESet ) );
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isSetDialSuperimposition( )
	{
		return dialSuperimpositionESet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet( int featureID, boolean resolve, boolean coreType )
	{
		switch ( featureID )
		{
			case ModelPackage.DIAL_CHART__DIAL_SUPERIMPOSITION :
				return isDialSuperimposition( ) ? Boolean.TRUE : Boolean.FALSE;
		}
		return super.eGet( featureID, resolve, coreType );
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eSet( int featureID, Object newValue )
	{
		switch ( featureID )
		{
			case ModelPackage.DIAL_CHART__DIAL_SUPERIMPOSITION :
				setDialSuperimposition( ( (Boolean) newValue ).booleanValue( ) );
				return;
		}
		super.eSet( featureID, newValue );
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eUnset( int featureID )
	{
		switch ( featureID )
		{
			case ModelPackage.DIAL_CHART__DIAL_SUPERIMPOSITION :
				unsetDialSuperimposition( );
				return;
		}
		super.eUnset( featureID );
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean eIsSet( int featureID )
	{
		switch ( featureID )
		{
			case ModelPackage.DIAL_CHART__DIAL_SUPERIMPOSITION :
				return isSetDialSuperimposition( );
		}
		return super.eIsSet( featureID );
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String toString( )
	{
		if ( eIsProxy( ) )
			return super.toString( );

		StringBuffer result = new StringBuffer( super.toString( ) );
		result.append( " (dialSuperimposition: " ); //$NON-NLS-1$
		if ( dialSuperimpositionESet )
			result.append( dialSuperimposition );
		else
			result.append( "<unset>" ); //$NON-NLS-1$
		result.append( ')' );
		return result.toString( );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.chart.model.impl.ChartWithoutAxesImpl#create()
	 */
	public static final ChartWithoutAxes create( )
	{
		final DialChart dc = ModelFactory.eINSTANCE.createDialChart( );
		( (DialChartImpl) dc ).initialize( );
		return dc;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.birt.chart.model.impl.ChartImpl#initialize()
	 */
	protected void initialize( )
	{
		super.initialize( );
	}

	/**
	 * A convenient method to get an instance copy. This is much faster than the
	 * ECoreUtil.copy().
	 */
	public DialChart copyInstance( )
	{
		DialChartImpl dest = new DialChartImpl( );
		dest.set( this );
		return dest;
	}

	protected void set( DialChart src )
	{
		super.set( src );

		minSlice = src.getMinSlice( );
		minSliceESet = src.isSetMinSlice( );
		minSlicePercent = src.isMinSlicePercent( );
		minSlicePercentESet = src.isSetMinSlicePercent( );
		minSliceLabel = src.getMinSliceLabel( );
		coverage = src.getCoverage( );
		coverageESet = src.isSetCoverage( );
		dialSuperimposition = src.isDialSuperimposition( );
		dialSuperimpositionESet = src.isSetDialSuperimposition( );
	}

	public static DialChart create( EObject parent, EReference ref )
	{
		return new DialChartImpl( );
	}

} // DialChartImpl
