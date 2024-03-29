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

package org.eclipse.birt.chart.model.type.impl;

import org.eclipse.birt.chart.engine.i18n.Messages;
import org.eclipse.birt.chart.model.attribute.ColorDefinition;
import org.eclipse.birt.chart.model.attribute.LeaderLineStyle;
import org.eclipse.birt.chart.model.attribute.LineAttributes;
import org.eclipse.birt.chart.model.attribute.LineStyle;
import org.eclipse.birt.chart.model.attribute.Position;
import org.eclipse.birt.chart.model.attribute.impl.ColorDefinitionImpl;
import org.eclipse.birt.chart.model.attribute.impl.LineAttributesImpl;
import org.eclipse.birt.chart.model.component.Label;
import org.eclipse.birt.chart.model.component.Series;
import org.eclipse.birt.chart.model.component.impl.LabelImpl;
import org.eclipse.birt.chart.model.component.impl.SeriesImpl;
import org.eclipse.birt.chart.model.type.PieSeries;
import org.eclipse.birt.chart.model.type.TypeFactory;
import org.eclipse.birt.chart.model.type.TypePackage;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;

/**
 * <!-- begin-user-doc --> An implementation of the model object '
 * <em><b>Pie Series</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.birt.chart.model.type.impl.PieSeriesImpl#getExplosion <em>Explosion</em>}</li>
 *   <li>{@link org.eclipse.birt.chart.model.type.impl.PieSeriesImpl#getExplosionExpression <em>Explosion Expression</em>}</li>
 *   <li>{@link org.eclipse.birt.chart.model.type.impl.PieSeriesImpl#getTitle <em>Title</em>}</li>
 *   <li>{@link org.eclipse.birt.chart.model.type.impl.PieSeriesImpl#getTitlePosition <em>Title Position</em>}</li>
 *   <li>{@link org.eclipse.birt.chart.model.type.impl.PieSeriesImpl#getLeaderLineAttributes <em>Leader Line Attributes</em>}</li>
 *   <li>{@link org.eclipse.birt.chart.model.type.impl.PieSeriesImpl#getLeaderLineStyle <em>Leader Line Style</em>}</li>
 *   <li>{@link org.eclipse.birt.chart.model.type.impl.PieSeriesImpl#getLeaderLineLength <em>Leader Line Length</em>}</li>
 *   <li>{@link org.eclipse.birt.chart.model.type.impl.PieSeriesImpl#getSliceOutline <em>Slice Outline</em>}</li>
 *   <li>{@link org.eclipse.birt.chart.model.type.impl.PieSeriesImpl#getRatio <em>Ratio</em>}</li>
 *   <li>{@link org.eclipse.birt.chart.model.type.impl.PieSeriesImpl#getRotation <em>Rotation</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class PieSeriesImpl extends SeriesImpl implements PieSeries
{

	/**
	 * The default value of the '{@link #getExplosion() <em>Explosion</em>}' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @see #getExplosion()
	 * @generated
	 * @ordered
	 */
	protected static final int EXPLOSION_EDEFAULT = 0;

	/**
	 * The cached value of the '{@link #getExplosion() <em>Explosion</em>}' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @see #getExplosion()
	 * @generated
	 * @ordered
	 */
	protected int explosion = EXPLOSION_EDEFAULT;

	/**
	 * This is true if the Explosion attribute has been set.
	 * <!-- begin-user-doc
	 * --> <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean explosionESet;

	/**
	 * The default value of the '{@link #getExplosionExpression() <em>Explosion Expression</em>}' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @see #getExplosionExpression()
	 * @generated
	 * @ordered
	 */
	protected static final String EXPLOSION_EXPRESSION_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getExplosionExpression() <em>Explosion Expression</em>}' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @see #getExplosionExpression()
	 * @generated
	 * @ordered
	 */
	protected String explosionExpression = EXPLOSION_EXPRESSION_EDEFAULT;

	/**
	 * The cached value of the '{@link #getTitle() <em>Title</em>}' containment reference.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @see #getTitle()
	 * @generated
	 * @ordered
	 */
	protected Label title;

	/**
	 * The default value of the '
	 * {@link #getTitlePosition() <em>Title Position</em>}' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getTitlePosition()
	 * @generated
	 * @ordered
	 */
	protected static final Position TITLE_POSITION_EDEFAULT = Position.ABOVE_LITERAL;

	/**
	 * The cached value of the '
	 * {@link #getTitlePosition() <em>Title Position</em>}' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getTitlePosition()
	 * @generated
	 * @ordered
	 */
	protected Position titlePosition = TITLE_POSITION_EDEFAULT;

	/**
	 * This is true if the Title Position attribute has been set. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	protected boolean titlePositionESet;

	/**
	 * The cached value of the '{@link #getLeaderLineAttributes() <em>Leader Line Attributes</em>}' containment reference.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @see #getLeaderLineAttributes()
	 * @generated
	 * @ordered
	 */
	protected LineAttributes leaderLineAttributes;

	/**
	 * The default value of the '{@link #getLeaderLineStyle() <em>Leader Line Style</em>}' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @see #getLeaderLineStyle()
	 * @generated
	 * @ordered
	 */
	protected static final LeaderLineStyle LEADER_LINE_STYLE_EDEFAULT = LeaderLineStyle.FIXED_LENGTH_LITERAL;

	/**
	 * The cached value of the '{@link #getLeaderLineStyle() <em>Leader Line Style</em>}' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @see #getLeaderLineStyle()
	 * @generated
	 * @ordered
	 */
	protected LeaderLineStyle leaderLineStyle = LEADER_LINE_STYLE_EDEFAULT;

	/**
	 * This is true if the Leader Line Style attribute has been set. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	protected boolean leaderLineStyleESet;

	/**
	 * The default value of the '{@link #getLeaderLineLength() <em>Leader Line Length</em>}' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @see #getLeaderLineLength()
	 * @generated
	 * @ordered
	 */
	protected static final double LEADER_LINE_LENGTH_EDEFAULT = 0.0;

	/**
	 * The cached value of the '{@link #getLeaderLineLength() <em>Leader Line Length</em>}' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @see #getLeaderLineLength()
	 * @generated
	 * @ordered
	 */
	protected double leaderLineLength = LEADER_LINE_LENGTH_EDEFAULT;

	/**
	 * This is true if the Leader Line Length attribute has been set. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	protected boolean leaderLineLengthESet;

	/**
	 * The cached value of the '{@link #getSliceOutline() <em>Slice Outline</em>}' containment reference.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @see #getSliceOutline()
	 * @generated
	 * @ordered
	 */
	protected ColorDefinition sliceOutline;

	/**
	 * The default value of the '{@link #getRatio() <em>Ratio</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getRatio()
	 * @generated
	 * @ordered
	 */
	protected static final double RATIO_EDEFAULT = 1.0;

	/**
	 * The cached value of the '{@link #getRatio() <em>Ratio</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getRatio()
	 * @generated
	 * @ordered
	 */
	protected double ratio = RATIO_EDEFAULT;

	/**
	 * This is true if the Ratio attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean ratioESet;

	/**
	 * The default value of the '{@link #getRotation() <em>Rotation</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getRotation()
	 * @generated
	 * @ordered
	 */
	protected static final double ROTATION_EDEFAULT = 0.0;

	/**
	 * The cached value of the '{@link #getRotation() <em>Rotation</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getRotation()
	 * @generated
	 * @ordered
	 */
	protected double rotation = ROTATION_EDEFAULT;

	/**
	 * This is true if the Rotation attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean rotationESet;


	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	protected PieSeriesImpl( )
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
		return TypePackage.Literals.PIE_SERIES;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public int getExplosion( )
	{
		return explosion;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public void setExplosion( int newExplosion )
	{
		int oldExplosion = explosion;
		explosion = newExplosion;
		boolean oldExplosionESet = explosionESet;
		explosionESet = true;
		if ( eNotificationRequired( ) )
			eNotify( new ENotificationImpl( this,
					Notification.SET,
					TypePackage.PIE_SERIES__EXPLOSION,
					oldExplosion,
					explosion,
					!oldExplosionESet ) );
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public void unsetExplosion( )
	{
		int oldExplosion = explosion;
		boolean oldExplosionESet = explosionESet;
		explosion = EXPLOSION_EDEFAULT;
		explosionESet = false;
		if ( eNotificationRequired( ) )
			eNotify( new ENotificationImpl( this,
					Notification.UNSET,
					TypePackage.PIE_SERIES__EXPLOSION,
					oldExplosion,
					EXPLOSION_EDEFAULT,
					oldExplosionESet ) );
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isSetExplosion( )
	{
		return explosionESet;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public String getExplosionExpression( )
	{
		return explosionExpression;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public void setExplosionExpression( String newExplosionExpression )
	{
		String oldExplosionExpression = explosionExpression;
		explosionExpression = newExplosionExpression;
		if ( eNotificationRequired( ) )
			eNotify( new ENotificationImpl( this,
					Notification.SET,
					TypePackage.PIE_SERIES__EXPLOSION_EXPRESSION,
					oldExplosionExpression,
					explosionExpression ) );
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public Label getTitle( )
	{
		return title;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetTitle( Label newTitle,
			NotificationChain msgs )
	{
		Label oldTitle = title;
		title = newTitle;
		if ( eNotificationRequired( ) )
		{
			ENotificationImpl notification = new ENotificationImpl( this,
					Notification.SET,
					TypePackage.PIE_SERIES__TITLE,
					oldTitle,
					newTitle );
			if ( msgs == null )
				msgs = notification;
			else
				msgs.add( notification );
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public void setTitle( Label newTitle )
	{
		if ( newTitle != title )
		{
			NotificationChain msgs = null;
			if ( title != null )
				msgs = ( (InternalEObject) title ).eInverseRemove( this,
						EOPPOSITE_FEATURE_BASE - TypePackage.PIE_SERIES__TITLE,
						null,
						msgs );
			if ( newTitle != null )
				msgs = ( (InternalEObject) newTitle ).eInverseAdd( this,
						EOPPOSITE_FEATURE_BASE - TypePackage.PIE_SERIES__TITLE,
						null,
						msgs );
			msgs = basicSetTitle( newTitle, msgs );
			if ( msgs != null )
				msgs.dispatch( );
		}
		else if ( eNotificationRequired( ) )
			eNotify( new ENotificationImpl( this,
					Notification.SET,
					TypePackage.PIE_SERIES__TITLE,
					newTitle,
					newTitle ) );
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public Position getTitlePosition( )
	{
		return titlePosition;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public void setTitlePosition( Position newTitlePosition )
	{
		Position oldTitlePosition = titlePosition;
		titlePosition = newTitlePosition == null ? TITLE_POSITION_EDEFAULT
				: newTitlePosition;
		boolean oldTitlePositionESet = titlePositionESet;
		titlePositionESet = true;
		if ( eNotificationRequired( ) )
			eNotify( new ENotificationImpl( this,
					Notification.SET,
					TypePackage.PIE_SERIES__TITLE_POSITION,
					oldTitlePosition,
					titlePosition,
					!oldTitlePositionESet ) );
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public void unsetTitlePosition( )
	{
		Position oldTitlePosition = titlePosition;
		boolean oldTitlePositionESet = titlePositionESet;
		titlePosition = TITLE_POSITION_EDEFAULT;
		titlePositionESet = false;
		if ( eNotificationRequired( ) )
			eNotify( new ENotificationImpl( this,
					Notification.UNSET,
					TypePackage.PIE_SERIES__TITLE_POSITION,
					oldTitlePosition,
					TITLE_POSITION_EDEFAULT,
					oldTitlePositionESet ) );
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isSetTitlePosition( )
	{
		return titlePositionESet;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public LineAttributes getLeaderLineAttributes( )
	{
		return leaderLineAttributes;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetLeaderLineAttributes(
			LineAttributes newLeaderLineAttributes, NotificationChain msgs )
	{
		LineAttributes oldLeaderLineAttributes = leaderLineAttributes;
		leaderLineAttributes = newLeaderLineAttributes;
		if ( eNotificationRequired( ) )
		{
			ENotificationImpl notification = new ENotificationImpl( this,
					Notification.SET,
					TypePackage.PIE_SERIES__LEADER_LINE_ATTRIBUTES,
					oldLeaderLineAttributes,
					newLeaderLineAttributes );
			if ( msgs == null )
				msgs = notification;
			else
				msgs.add( notification );
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public void setLeaderLineAttributes( LineAttributes newLeaderLineAttributes )
	{
		if ( newLeaderLineAttributes != leaderLineAttributes )
		{
			NotificationChain msgs = null;
			if ( leaderLineAttributes != null )
				msgs = ( (InternalEObject) leaderLineAttributes ).eInverseRemove( this,
						EOPPOSITE_FEATURE_BASE
								- TypePackage.PIE_SERIES__LEADER_LINE_ATTRIBUTES,
						null,
						msgs );
			if ( newLeaderLineAttributes != null )
				msgs = ( (InternalEObject) newLeaderLineAttributes ).eInverseAdd( this,
						EOPPOSITE_FEATURE_BASE
								- TypePackage.PIE_SERIES__LEADER_LINE_ATTRIBUTES,
						null,
						msgs );
			msgs = basicSetLeaderLineAttributes( newLeaderLineAttributes, msgs );
			if ( msgs != null )
				msgs.dispatch( );
		}
		else if ( eNotificationRequired( ) )
			eNotify( new ENotificationImpl( this,
					Notification.SET,
					TypePackage.PIE_SERIES__LEADER_LINE_ATTRIBUTES,
					newLeaderLineAttributes,
					newLeaderLineAttributes ) );
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public LeaderLineStyle getLeaderLineStyle( )
	{
		return leaderLineStyle;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public void setLeaderLineStyle( LeaderLineStyle newLeaderLineStyle )
	{
		LeaderLineStyle oldLeaderLineStyle = leaderLineStyle;
		leaderLineStyle = newLeaderLineStyle == null ? LEADER_LINE_STYLE_EDEFAULT
				: newLeaderLineStyle;
		boolean oldLeaderLineStyleESet = leaderLineStyleESet;
		leaderLineStyleESet = true;
		if ( eNotificationRequired( ) )
			eNotify( new ENotificationImpl( this,
					Notification.SET,
					TypePackage.PIE_SERIES__LEADER_LINE_STYLE,
					oldLeaderLineStyle,
					leaderLineStyle,
					!oldLeaderLineStyleESet ) );
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public void unsetLeaderLineStyle( )
	{
		LeaderLineStyle oldLeaderLineStyle = leaderLineStyle;
		boolean oldLeaderLineStyleESet = leaderLineStyleESet;
		leaderLineStyle = LEADER_LINE_STYLE_EDEFAULT;
		leaderLineStyleESet = false;
		if ( eNotificationRequired( ) )
			eNotify( new ENotificationImpl( this,
					Notification.UNSET,
					TypePackage.PIE_SERIES__LEADER_LINE_STYLE,
					oldLeaderLineStyle,
					LEADER_LINE_STYLE_EDEFAULT,
					oldLeaderLineStyleESet ) );
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isSetLeaderLineStyle( )
	{
		return leaderLineStyleESet;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public double getLeaderLineLength( )
	{
		return leaderLineLength;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public void setLeaderLineLength( double newLeaderLineLength )
	{
		double oldLeaderLineLength = leaderLineLength;
		leaderLineLength = newLeaderLineLength;
		boolean oldLeaderLineLengthESet = leaderLineLengthESet;
		leaderLineLengthESet = true;
		if ( eNotificationRequired( ) )
			eNotify( new ENotificationImpl( this,
					Notification.SET,
					TypePackage.PIE_SERIES__LEADER_LINE_LENGTH,
					oldLeaderLineLength,
					leaderLineLength,
					!oldLeaderLineLengthESet ) );
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public void unsetLeaderLineLength( )
	{
		double oldLeaderLineLength = leaderLineLength;
		boolean oldLeaderLineLengthESet = leaderLineLengthESet;
		leaderLineLength = LEADER_LINE_LENGTH_EDEFAULT;
		leaderLineLengthESet = false;
		if ( eNotificationRequired( ) )
			eNotify( new ENotificationImpl( this,
					Notification.UNSET,
					TypePackage.PIE_SERIES__LEADER_LINE_LENGTH,
					oldLeaderLineLength,
					LEADER_LINE_LENGTH_EDEFAULT,
					oldLeaderLineLengthESet ) );
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isSetLeaderLineLength( )
	{
		return leaderLineLengthESet;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public ColorDefinition getSliceOutline( )
	{
		return sliceOutline;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetSliceOutline(
			ColorDefinition newSliceOutline, NotificationChain msgs )
	{
		ColorDefinition oldSliceOutline = sliceOutline;
		sliceOutline = newSliceOutline;
		if ( eNotificationRequired( ) )
		{
			ENotificationImpl notification = new ENotificationImpl( this,
					Notification.SET,
					TypePackage.PIE_SERIES__SLICE_OUTLINE,
					oldSliceOutline,
					newSliceOutline );
			if ( msgs == null )
				msgs = notification;
			else
				msgs.add( notification );
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public void setSliceOutline( ColorDefinition newSliceOutline )
	{
		if ( newSliceOutline != sliceOutline )
		{
			NotificationChain msgs = null;
			if ( sliceOutline != null )
				msgs = ( (InternalEObject) sliceOutline ).eInverseRemove( this,
						EOPPOSITE_FEATURE_BASE
								- TypePackage.PIE_SERIES__SLICE_OUTLINE,
						null,
						msgs );
			if ( newSliceOutline != null )
				msgs = ( (InternalEObject) newSliceOutline ).eInverseAdd( this,
						EOPPOSITE_FEATURE_BASE
								- TypePackage.PIE_SERIES__SLICE_OUTLINE,
						null,
						msgs );
			msgs = basicSetSliceOutline( newSliceOutline, msgs );
			if ( msgs != null )
				msgs.dispatch( );
		}
		else if ( eNotificationRequired( ) )
			eNotify( new ENotificationImpl( this,
					Notification.SET,
					TypePackage.PIE_SERIES__SLICE_OUTLINE,
					newSliceOutline,
					newSliceOutline ) );
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public double getRatio( )
	{
		return ratio;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setRatio( double newRatio )
	{
		double oldRatio = ratio;
		ratio = newRatio;
		boolean oldRatioESet = ratioESet;
		ratioESet = true;
		if ( eNotificationRequired( ) )
			eNotify( new ENotificationImpl( this,
					Notification.SET,
					TypePackage.PIE_SERIES__RATIO,
					oldRatio,
					ratio,
					!oldRatioESet ) );
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void unsetRatio( )
	{
		double oldRatio = ratio;
		boolean oldRatioESet = ratioESet;
		ratio = RATIO_EDEFAULT;
		ratioESet = false;
		if ( eNotificationRequired( ) )
			eNotify( new ENotificationImpl( this,
					Notification.UNSET,
					TypePackage.PIE_SERIES__RATIO,
					oldRatio,
					RATIO_EDEFAULT,
					oldRatioESet ) );
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isSetRatio( )
	{
		return ratioESet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public double getRotation( )
	{
		return rotation;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setRotation( double newRotation )
	{
		double oldRotation = rotation;
		rotation = newRotation;
		boolean oldRotationESet = rotationESet;
		rotationESet = true;
		if ( eNotificationRequired( ) )
			eNotify( new ENotificationImpl( this,
					Notification.SET,
					TypePackage.PIE_SERIES__ROTATION,
					oldRotation,
					rotation,
					!oldRotationESet ) );
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void unsetRotation( )
	{
		double oldRotation = rotation;
		boolean oldRotationESet = rotationESet;
		rotation = ROTATION_EDEFAULT;
		rotationESet = false;
		if ( eNotificationRequired( ) )
			eNotify( new ENotificationImpl( this,
					Notification.UNSET,
					TypePackage.PIE_SERIES__ROTATION,
					oldRotation,
					ROTATION_EDEFAULT,
					oldRotationESet ) );
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isSetRotation( )
	{
		return rotationESet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove( InternalEObject otherEnd,
			int featureID, NotificationChain msgs )
	{
		switch ( featureID )
		{
			case TypePackage.PIE_SERIES__TITLE :
				return basicSetTitle( null, msgs );
			case TypePackage.PIE_SERIES__LEADER_LINE_ATTRIBUTES :
				return basicSetLeaderLineAttributes( null, msgs );
			case TypePackage.PIE_SERIES__SLICE_OUTLINE :
				return basicSetSliceOutline( null, msgs );
		}
		return super.eInverseRemove( otherEnd, featureID, msgs );
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
			case TypePackage.PIE_SERIES__EXPLOSION :
				return new Integer( getExplosion( ) );
			case TypePackage.PIE_SERIES__EXPLOSION_EXPRESSION :
				return getExplosionExpression( );
			case TypePackage.PIE_SERIES__TITLE :
				return getTitle( );
			case TypePackage.PIE_SERIES__TITLE_POSITION :
				return getTitlePosition( );
			case TypePackage.PIE_SERIES__LEADER_LINE_ATTRIBUTES :
				return getLeaderLineAttributes( );
			case TypePackage.PIE_SERIES__LEADER_LINE_STYLE :
				return getLeaderLineStyle( );
			case TypePackage.PIE_SERIES__LEADER_LINE_LENGTH :
				return new Double( getLeaderLineLength( ) );
			case TypePackage.PIE_SERIES__SLICE_OUTLINE :
				return getSliceOutline( );
			case TypePackage.PIE_SERIES__RATIO :
				return new Double( getRatio( ) );
			case TypePackage.PIE_SERIES__ROTATION :
				return new Double( getRotation( ) );
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
			case TypePackage.PIE_SERIES__EXPLOSION :
				setExplosion( ( (Integer) newValue ).intValue( ) );
				return;
			case TypePackage.PIE_SERIES__EXPLOSION_EXPRESSION :
				setExplosionExpression( (String) newValue );
				return;
			case TypePackage.PIE_SERIES__TITLE :
				setTitle( (Label) newValue );
				return;
			case TypePackage.PIE_SERIES__TITLE_POSITION :
				setTitlePosition( (Position) newValue );
				return;
			case TypePackage.PIE_SERIES__LEADER_LINE_ATTRIBUTES :
				setLeaderLineAttributes( (LineAttributes) newValue );
				return;
			case TypePackage.PIE_SERIES__LEADER_LINE_STYLE :
				setLeaderLineStyle( (LeaderLineStyle) newValue );
				return;
			case TypePackage.PIE_SERIES__LEADER_LINE_LENGTH :
				setLeaderLineLength( ( (Double) newValue ).doubleValue( ) );
				return;
			case TypePackage.PIE_SERIES__SLICE_OUTLINE :
				setSliceOutline( (ColorDefinition) newValue );
				return;
			case TypePackage.PIE_SERIES__RATIO :
				setRatio( ( (Double) newValue ).doubleValue( ) );
				return;
			case TypePackage.PIE_SERIES__ROTATION :
				setRotation( ( (Double) newValue ).doubleValue( ) );
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
			case TypePackage.PIE_SERIES__EXPLOSION :
				unsetExplosion( );
				return;
			case TypePackage.PIE_SERIES__EXPLOSION_EXPRESSION :
				setExplosionExpression( EXPLOSION_EXPRESSION_EDEFAULT );
				return;
			case TypePackage.PIE_SERIES__TITLE :
				setTitle( (Label) null );
				return;
			case TypePackage.PIE_SERIES__TITLE_POSITION :
				unsetTitlePosition( );
				return;
			case TypePackage.PIE_SERIES__LEADER_LINE_ATTRIBUTES :
				setLeaderLineAttributes( (LineAttributes) null );
				return;
			case TypePackage.PIE_SERIES__LEADER_LINE_STYLE :
				unsetLeaderLineStyle( );
				return;
			case TypePackage.PIE_SERIES__LEADER_LINE_LENGTH :
				unsetLeaderLineLength( );
				return;
			case TypePackage.PIE_SERIES__SLICE_OUTLINE :
				setSliceOutline( (ColorDefinition) null );
				return;
			case TypePackage.PIE_SERIES__RATIO :
				unsetRatio( );
				return;
			case TypePackage.PIE_SERIES__ROTATION :
				unsetRotation( );
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
			case TypePackage.PIE_SERIES__EXPLOSION :
				return isSetExplosion( );
			case TypePackage.PIE_SERIES__EXPLOSION_EXPRESSION :
				return EXPLOSION_EXPRESSION_EDEFAULT == null ? explosionExpression != null
						: !EXPLOSION_EXPRESSION_EDEFAULT.equals( explosionExpression );
			case TypePackage.PIE_SERIES__TITLE :
				return title != null;
			case TypePackage.PIE_SERIES__TITLE_POSITION :
				return isSetTitlePosition( );
			case TypePackage.PIE_SERIES__LEADER_LINE_ATTRIBUTES :
				return leaderLineAttributes != null;
			case TypePackage.PIE_SERIES__LEADER_LINE_STYLE :
				return isSetLeaderLineStyle( );
			case TypePackage.PIE_SERIES__LEADER_LINE_LENGTH :
				return isSetLeaderLineLength( );
			case TypePackage.PIE_SERIES__SLICE_OUTLINE :
				return sliceOutline != null;
			case TypePackage.PIE_SERIES__RATIO :
				return isSetRatio( );
			case TypePackage.PIE_SERIES__ROTATION :
				return isSetRotation( );
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
		result.append( " (explosion: " ); //$NON-NLS-1$
		if ( explosionESet )
			result.append( explosion );
		else
			result.append( "<unset>" ); //$NON-NLS-1$
		result.append( ", explosionExpression: " ); //$NON-NLS-1$
		result.append( explosionExpression );
		result.append( ", titlePosition: " ); //$NON-NLS-1$
		if ( titlePositionESet )
			result.append( titlePosition );
		else
			result.append( "<unset>" ); //$NON-NLS-1$
		result.append( ", leaderLineStyle: " ); //$NON-NLS-1$
		if ( leaderLineStyleESet )
			result.append( leaderLineStyle );
		else
			result.append( "<unset>" ); //$NON-NLS-1$
		result.append( ", leaderLineLength: " ); //$NON-NLS-1$
		if ( leaderLineLengthESet )
			result.append( leaderLineLength );
		else
			result.append( "<unset>" ); //$NON-NLS-1$
		result.append( ", ratio: " ); //$NON-NLS-1$
		if ( ratioESet )
			result.append( ratio );
		else
			result.append( "<unset>" ); //$NON-NLS-1$
		result.append( ", rotation: " ); //$NON-NLS-1$
		if ( rotationESet )
			result.append( rotation );
		else
			result.append( "<unset>" ); //$NON-NLS-1$
		result.append( ')' );
		return result.toString( );
	}

	/**
	 * A convenience method to create an initialized 'Series' instance
	 * 
	 * @return
	 */
	public static Series create( )
	{
		final PieSeries se = TypeFactory.eINSTANCE.createPieSeries( );
		( (PieSeriesImpl) se ).initialize( );
		return se;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.chart.model.component.Series#initialize()
	 */
	protected void initialize( )
	{
		super.initialize( );
		setExplosion( 20 );
		setLabelPosition( Position.OUTSIDE_LITERAL );
		setLeaderLineAttributes( LineAttributesImpl.create( ColorDefinitionImpl.BLACK( ),
				LineStyle.SOLID_LITERAL,
				1 ) );
		setLeaderLineLength( 40 );
		setLeaderLineStyle( LeaderLineStyle.STRETCH_TO_SIDE_LITERAL );
		// setSliceOutline(ColorDefinitionImpl.BLACK()); // UNDEFINED SUGGESTS
		// THAT OUTLINE IS RENDERED IN DARKER SLICE FILL COLOR
		getLabel( ).setVisible( true );
		final Label la = LabelImpl.create( );
		la.getCaption( ).getFont( ).setSize( 16 );
		la.getCaption( ).getFont( ).setBold( true );
		setTitle( la );
		setTitlePosition( Position.BELOW_LITERAL );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.chart.model.component.Series#getDisplayName()
	 */
	public String getDisplayName( )
	{
		return Messages.getString( "PieSeriesImpl.displayName" ); //$NON-NLS-1$
	}

	/**
	 * A convenient method to get an instance copy. This is much faster than the
	 * ECoreUtil.copy().
	 */
	public PieSeries copyInstance( )
	{
		PieSeriesImpl dest = new PieSeriesImpl( );
		dest.set( this );
		return dest;
	}

	protected void set( PieSeries src )
	{
		super.set( src );

		if ( src.getTitle( ) != null )
		{
			setTitle( src.getTitle( ).copyInstance( ) );
		}

		if ( src.getLeaderLineAttributes( ) != null )
		{
			setLeaderLineAttributes( src.getLeaderLineAttributes( )
					.copyInstance( ) );
		}

		if ( src.getSliceOutline( ) != null )
		{
			setSliceOutline( src.getSliceOutline( ).copyInstance( ) );
		}

		explosion = src.getExplosion( );
		explosionESet = src.isSetExplosion( );
		explosionExpression = src.getExplosionExpression( );
		titlePosition = src.getTitlePosition( );
		titlePositionESet = src.isSetTitlePosition( );
		leaderLineStyle = src.getLeaderLineStyle( );
		leaderLineStyleESet = src.isSetLeaderLineStyle( );
		leaderLineLength = src.getLeaderLineLength( );
		leaderLineLengthESet = src.isSetLeaderLineLength( );
		ratio = src.getRatio( );
		ratioESet = src.isSetRatio( );
		rotation = src.getRotation( );
		rotationESet = src.isSetRotation( );
		
	}

	public static PieSeries create( EObject parent, EReference ref )
	{
		return new PieSeriesImpl( );
	}

} // PieSeriesImpl
