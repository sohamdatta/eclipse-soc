/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.birt.chart.model.newtype.impl;

import org.eclipse.birt.chart.engine.i18n.Messages;
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

import org.eclipse.birt.chart.model.newtype.DonutSeries;
import org.eclipse.birt.chart.model.newtype.NewtypeFactory;
import org.eclipse.birt.chart.model.newtype.NewtypePackage;
import org.eclipse.birt.chart.model.type.PieSeries;
import org.eclipse.birt.chart.model.type.TypeFactory;
import org.eclipse.birt.chart.model.type.TypePackage;
import org.eclipse.birt.chart.model.type.impl.PieSeriesImpl;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Donut Series</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.birt.chart.model.newtype.impl.DonutSeriesImpl#getExplosion <em>Explosion</em>}</li>
 *   <li>{@link org.eclipse.birt.chart.model.newtype.impl.DonutSeriesImpl#getThickness <em>Thickness</em>}</li>
 *   <li>{@link org.eclipse.birt.chart.model.newtype.impl.DonutSeriesImpl#getRotation <em>Rotation</em>}</li>
 *   <li>{@link org.eclipse.birt.chart.model.newtype.impl.DonutSeriesImpl#isShowDonutLabels <em>Show Donut Labels</em>}</li>
 *   <li>{@link org.eclipse.birt.chart.model.newtype.impl.DonutSeriesImpl#getLeaderLineStyle <em>Leader Line Style</em>}</li>
 *   <li>{@link org.eclipse.birt.chart.model.newtype.impl.DonutSeriesImpl#getLeaderLineLength <em>Leader Line Length</em>}</li>
 *   <li>{@link org.eclipse.birt.chart.model.newtype.impl.DonutSeriesImpl#getTitle <em>Title</em>}</li>
 *   <li>{@link org.eclipse.birt.chart.model.newtype.impl.DonutSeriesImpl#getTitlePosition <em>Title Position</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class DonutSeriesImpl extends SeriesImpl implements DonutSeries {
	
	/**
	 * The cached value of the '{@link #getLeaderLineAttributes() <em>Leader Line Attributes</em>}' containment reference.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @see #getLeaderLineAttributes()
	 * @generated
	 * @ordered
	 */
	protected LineAttributes leaderLineAttributes;
	
	
	/**
	 * The default value of the '{@link #getExplosion() <em>Explosion</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getExplosion()
	 * @generated
	 * @ordered
	 */
	protected static final int EXPLOSION_EDEFAULT = 0;

	/**
	 * The cached value of the '{@link #getExplosion() <em>Explosion</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getExplosion()
	 * @generated
	 * @ordered
	 */
	protected int explosion = EXPLOSION_EDEFAULT;

	/**
	 * This is true if the Explosion attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean explosionESet;

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
	 * The default value of the '{@link #getRotation() <em>Rotation</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getRotation()
	 * @generated
	 * @ordered
	 */
	protected static final int ROTATION_EDEFAULT = 0;

	/**
	 * The cached value of the '{@link #getRotation() <em>Rotation</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getRotation()
	 * @generated
	 * @ordered
	 */
	protected int rotation = ROTATION_EDEFAULT;

	/**
	 * This is true if the Rotation attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean rotationESet;

	/**
	 * The default value of the '{@link #isShowDonutLabels() <em>Show Donut Labels</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isShowDonutLabels()
	 * @generated
	 * @ordered
	 */
	protected static final boolean SHOW_DONUT_LABELS_EDEFAULT = false;

	/**
	 * The cached value of the '{@link #isShowDonutLabels() <em>Show Donut Labels</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isShowDonutLabels()
	 * @generated
	 * @ordered
	 */
	protected boolean showDonutLabels = SHOW_DONUT_LABELS_EDEFAULT;

	/**
	 * This is true if the Show Donut Labels attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean showDonutLabelsESet;

	/**
	 * The default value of the '{@link #getLeaderLineStyle() <em>Leader Line Style</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getLeaderLineStyle()
	 * @generated
	 * @ordered
	 */
	protected static final LeaderLineStyle LEADER_LINE_STYLE_EDEFAULT = LeaderLineStyle.FIXED_LENGTH_LITERAL;

	/**
	 * The cached value of the '{@link #getLeaderLineStyle() <em>Leader Line Style</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getLeaderLineStyle()
	 * @generated
	 * @ordered
	 */
	protected LeaderLineStyle leaderLineStyle = LEADER_LINE_STYLE_EDEFAULT;

	/**
	 * This is true if the Leader Line Style attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean leaderLineStyleESet;

	/**
	 * The default value of the '{@link #getLeaderLineLength() <em>Leader Line Length</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getLeaderLineLength()
	 * @generated
	 * @ordered
	 */
	protected static final double LEADER_LINE_LENGTH_EDEFAULT = 0.0;

	/**
	 * The cached value of the '{@link #getLeaderLineLength() <em>Leader Line Length</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getLeaderLineLength()
	 * @generated
	 * @ordered
	 */
	protected double leaderLineLength = LEADER_LINE_LENGTH_EDEFAULT;

	/**
	 * This is true if the Leader Line Length attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean leaderLineLengthESet;

	/**
	 * The cached value of the '{@link #getTitle() <em>Title</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTitle()
	 * @generated
	 * @ordered
	 */
	protected Label title;

	/**
	 * The default value of the '{@link #getTitlePosition() <em>Title Position</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTitlePosition()
	 * @generated
	 * @ordered
	 */
	protected static final Position TITLE_POSITION_EDEFAULT = Position.ABOVE_LITERAL;


	private static final double RATIO_EDEFAULT = 0;

	/**
	 * The cached value of the '{@link #getTitlePosition() <em>Title Position</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTitlePosition()
	 * @generated
	 * @ordered
	 */
	protected Position titlePosition = TITLE_POSITION_EDEFAULT;

	/**
	 * This is true if the Title Position attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean titlePositionESet;


	private double ratio;


	private boolean ratioESet;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected DonutSeriesImpl() {
		super();
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
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return NewtypePackage.Literals.DONUT_SERIES;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public int getExplosion() {
		return explosion;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setExplosion(int newExplosion) {
		int oldExplosion = explosion;
		explosion = newExplosion;
		boolean oldExplosionESet = explosionESet;
		explosionESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, NewtypePackage.DONUT_SERIES__EXPLOSION, oldExplosion, explosion, !oldExplosionESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void unsetExplosion() {
		int oldExplosion = explosion;
		boolean oldExplosionESet = explosionESet;
		explosion = EXPLOSION_EDEFAULT;
		explosionESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, NewtypePackage.DONUT_SERIES__EXPLOSION, oldExplosion, EXPLOSION_EDEFAULT, oldExplosionESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isSetExplosion() {
		return explosionESet;
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
			eNotify(new ENotificationImpl(this, Notification.SET, NewtypePackage.DONUT_SERIES__THICKNESS, oldThickness, thickness, !oldThicknessESet));
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
			eNotify(new ENotificationImpl(this, Notification.UNSET, NewtypePackage.DONUT_SERIES__THICKNESS, oldThickness, THICKNESS_EDEFAULT, oldThicknessESet));
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
	public int getRotation() {
		return rotation;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setRotation(int newRotation) {
		int oldRotation = rotation;
		rotation = newRotation;
		boolean oldRotationESet = rotationESet;
		rotationESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, NewtypePackage.DONUT_SERIES__ROTATION, oldRotation, rotation, !oldRotationESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void unsetRotation() {
		int oldRotation = rotation;
		boolean oldRotationESet = rotationESet;
		rotation = ROTATION_EDEFAULT;
		rotationESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, NewtypePackage.DONUT_SERIES__ROTATION, oldRotation, ROTATION_EDEFAULT, oldRotationESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isSetRotation() {
		return rotationESet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isShowDonutLabels() {
		return showDonutLabels;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setShowDonutLabels(boolean newShowDonutLabels) {
		boolean oldShowDonutLabels = showDonutLabels;
		showDonutLabels = newShowDonutLabels;
		boolean oldShowDonutLabelsESet = showDonutLabelsESet;
		showDonutLabelsESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, NewtypePackage.DONUT_SERIES__SHOW_DONUT_LABELS, oldShowDonutLabels, showDonutLabels, !oldShowDonutLabelsESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void unsetShowDonutLabels() {
		boolean oldShowDonutLabels = showDonutLabels;
		boolean oldShowDonutLabelsESet = showDonutLabelsESet;
		showDonutLabels = SHOW_DONUT_LABELS_EDEFAULT;
		showDonutLabelsESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, NewtypePackage.DONUT_SERIES__SHOW_DONUT_LABELS, oldShowDonutLabels, SHOW_DONUT_LABELS_EDEFAULT, oldShowDonutLabelsESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isSetShowDonutLabels() {
		return showDonutLabelsESet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public LeaderLineStyle getLeaderLineStyle() {
		return leaderLineStyle;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setLeaderLineStyle(LeaderLineStyle newLeaderLineStyle) {
		LeaderLineStyle oldLeaderLineStyle = leaderLineStyle;
		leaderLineStyle = newLeaderLineStyle == null ? LEADER_LINE_STYLE_EDEFAULT : newLeaderLineStyle;
		boolean oldLeaderLineStyleESet = leaderLineStyleESet;
		leaderLineStyleESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, NewtypePackage.DONUT_SERIES__LEADER_LINE_STYLE, oldLeaderLineStyle, leaderLineStyle, !oldLeaderLineStyleESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void unsetLeaderLineStyle() {
		LeaderLineStyle oldLeaderLineStyle = leaderLineStyle;
		boolean oldLeaderLineStyleESet = leaderLineStyleESet;
		leaderLineStyle = LEADER_LINE_STYLE_EDEFAULT;
		leaderLineStyleESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, NewtypePackage.DONUT_SERIES__LEADER_LINE_STYLE, oldLeaderLineStyle, LEADER_LINE_STYLE_EDEFAULT, oldLeaderLineStyleESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isSetLeaderLineStyle() {
		return leaderLineStyleESet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public double getLeaderLineLength() {
		return leaderLineLength;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setLeaderLineLength(double newLeaderLineLength) {
		double oldLeaderLineLength = leaderLineLength;
		leaderLineLength = newLeaderLineLength;
		boolean oldLeaderLineLengthESet = leaderLineLengthESet;
		leaderLineLengthESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, NewtypePackage.DONUT_SERIES__LEADER_LINE_LENGTH, oldLeaderLineLength, leaderLineLength, !oldLeaderLineLengthESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void unsetLeaderLineLength() {
		double oldLeaderLineLength = leaderLineLength;
		boolean oldLeaderLineLengthESet = leaderLineLengthESet;
		leaderLineLength = LEADER_LINE_LENGTH_EDEFAULT;
		leaderLineLengthESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, NewtypePackage.DONUT_SERIES__LEADER_LINE_LENGTH, oldLeaderLineLength, LEADER_LINE_LENGTH_EDEFAULT, oldLeaderLineLengthESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isSetLeaderLineLength() {
		return leaderLineLengthESet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Label getTitle() {
		return title;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetTitle(Label newTitle, NotificationChain msgs) {
		Label oldTitle = title;
		title = newTitle;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, NewtypePackage.DONUT_SERIES__TITLE, oldTitle, newTitle);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setTitle(Label newTitle) {
		if (newTitle != title) {
			NotificationChain msgs = null;
			if (title != null)
				msgs = ((InternalEObject)title).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - NewtypePackage.DONUT_SERIES__TITLE, null, msgs);
			if (newTitle != null)
				msgs = ((InternalEObject)newTitle).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - NewtypePackage.DONUT_SERIES__TITLE, null, msgs);
			msgs = basicSetTitle(newTitle, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, NewtypePackage.DONUT_SERIES__TITLE, newTitle, newTitle));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Position getTitlePosition() {
		return titlePosition;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setTitlePosition(Position newTitlePosition) {
		Position oldTitlePosition = titlePosition;
		titlePosition = newTitlePosition == null ? TITLE_POSITION_EDEFAULT : newTitlePosition;
		boolean oldTitlePositionESet = titlePositionESet;
		titlePositionESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, NewtypePackage.DONUT_SERIES__TITLE_POSITION, oldTitlePosition, titlePosition, !oldTitlePositionESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void unsetTitlePosition() {
		Position oldTitlePosition = titlePosition;
		boolean oldTitlePositionESet = titlePositionESet;
		titlePosition = TITLE_POSITION_EDEFAULT;
		titlePositionESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, NewtypePackage.DONUT_SERIES__TITLE_POSITION, oldTitlePosition, TITLE_POSITION_EDEFAULT, oldTitlePositionESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isSetTitlePosition() {
		return titlePositionESet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case NewtypePackage.DONUT_SERIES__TITLE:
				return basicSetTitle(null, msgs);
		}
		return super.eInverseRemove(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case NewtypePackage.DONUT_SERIES__EXPLOSION:
				return getExplosion();
			case NewtypePackage.DONUT_SERIES__THICKNESS:
				return getThickness();
			case NewtypePackage.DONUT_SERIES__ROTATION:
				return getRotation();
			case NewtypePackage.DONUT_SERIES__SHOW_DONUT_LABELS:
				return isShowDonutLabels();
			case NewtypePackage.DONUT_SERIES__LEADER_LINE_STYLE:
				return getLeaderLineStyle();
			case NewtypePackage.DONUT_SERIES__LEADER_LINE_LENGTH:
				return getLeaderLineLength();
			case NewtypePackage.DONUT_SERIES__TITLE:
				return getTitle();
			case NewtypePackage.DONUT_SERIES__TITLE_POSITION:
				return getTitlePosition();
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
			case NewtypePackage.DONUT_SERIES__EXPLOSION:
				setExplosion((Integer)newValue);
				return;
			case NewtypePackage.DONUT_SERIES__THICKNESS:
				setThickness((Integer)newValue);
				return;
			case NewtypePackage.DONUT_SERIES__ROTATION:
				setRotation((Integer)newValue);
				return;
			case NewtypePackage.DONUT_SERIES__SHOW_DONUT_LABELS:
				setShowDonutLabels((Boolean)newValue);
				return;
			case NewtypePackage.DONUT_SERIES__LEADER_LINE_STYLE:
				setLeaderLineStyle((LeaderLineStyle)newValue);
				return;
			case NewtypePackage.DONUT_SERIES__LEADER_LINE_LENGTH:
				setLeaderLineLength((Double)newValue);
				return;
			case NewtypePackage.DONUT_SERIES__TITLE:
				setTitle((Label)newValue);
				return;
			case NewtypePackage.DONUT_SERIES__TITLE_POSITION:
				setTitlePosition((Position)newValue);
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
			case NewtypePackage.DONUT_SERIES__EXPLOSION:
				unsetExplosion();
				return;
			case NewtypePackage.DONUT_SERIES__THICKNESS:
				unsetThickness();
				return;
			case NewtypePackage.DONUT_SERIES__ROTATION:
				unsetRotation();
				return;
			case NewtypePackage.DONUT_SERIES__SHOW_DONUT_LABELS:
				unsetShowDonutLabels();
				return;
			case NewtypePackage.DONUT_SERIES__LEADER_LINE_STYLE:
				unsetLeaderLineStyle();
				return;
			case NewtypePackage.DONUT_SERIES__LEADER_LINE_LENGTH:
				unsetLeaderLineLength();
				return;
			case NewtypePackage.DONUT_SERIES__TITLE:
				setTitle((Label)null);
				return;
			case NewtypePackage.DONUT_SERIES__TITLE_POSITION:
				unsetTitlePosition();
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
			case NewtypePackage.DONUT_SERIES__EXPLOSION:
				return isSetExplosion();
			case NewtypePackage.DONUT_SERIES__THICKNESS:
				return isSetThickness();
			case NewtypePackage.DONUT_SERIES__ROTATION:
				return isSetRotation();
			case NewtypePackage.DONUT_SERIES__SHOW_DONUT_LABELS:
				return isSetShowDonutLabels();
			case NewtypePackage.DONUT_SERIES__LEADER_LINE_STYLE:
				return isSetLeaderLineStyle();
			case NewtypePackage.DONUT_SERIES__LEADER_LINE_LENGTH:
				return isSetLeaderLineLength();
			case NewtypePackage.DONUT_SERIES__TITLE:
				return title != null;
			case NewtypePackage.DONUT_SERIES__TITLE_POSITION:
				return isSetTitlePosition();
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
		result.append(" (explosion: ");
		if (explosionESet) result.append(explosion); else result.append("<unset>");
		result.append(", thickness: ");
		if (thicknessESet) result.append(thickness); else result.append("<unset>");
		result.append(", rotation: ");
		if (rotationESet) result.append(rotation); else result.append("<unset>");
		result.append(", showDonutLabels: ");
		if (showDonutLabelsESet) result.append(showDonutLabels); else result.append("<unset>");
		result.append(", leaderLineStyle: ");
		if (leaderLineStyleESet) result.append(leaderLineStyle); else result.append("<unset>");
		result.append(", leaderLineLength: ");
		if (leaderLineLengthESet) result.append(leaderLineLength); else result.append("<unset>");
		result.append(", titlePosition: ");
		if (titlePositionESet) result.append(titlePosition); else result.append("<unset>");
		result.append(')');
		return result.toString();
	}
	
	
	
	/**
	 * A convenience method to create an initialized 'Series' instance
	 * 
	 * @return
	 */
	public static final Series create( )
	{
		final DonutSeries se = NewtypeFactory.eINSTANCE.createDonutSeries();
		( (DonutSeriesImpl) se ).initialize( );
		return se;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.chart.model.component.Series#initialize()
	 */
	protected final void initialize( )
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
	public DonutSeries copyInstance( )
	{
		DonutSeriesImpl dest = new DonutSeriesImpl( );
		dest.set( this );
		return dest;
	}

	protected void set( DonutSeries src )
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

//		if ( src.getSliceOutline( ) != null )
//		{
//			setSliceOutline( src.getSliceOutline( ).copyInstance( ) );
//		}

		explosion = src.getExplosion( );
		explosionESet = src.isSetExplosion( );
//		explosionExpression = src.getExplosionExpression( );
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
		
		thickness = src.getThickness();
		if (thickness != 0)
			thicknessESet = true;
	}

	public static DonutSeries create( EObject parent, EReference ref )
	{
		return new DonutSeriesImpl( );
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


} //DonutSeriesImpl
