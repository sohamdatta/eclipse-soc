/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.birt.chart.model.newtype.impl;

import org.eclipse.birt.chart.model.ModelPackage;

import org.eclipse.birt.chart.model.attribute.AttributePackage;

import org.eclipse.birt.chart.model.attribute.impl.AttributePackageImpl;

import org.eclipse.birt.chart.model.component.ComponentPackage;

import org.eclipse.birt.chart.model.component.impl.ComponentPackageImpl;

import org.eclipse.birt.chart.model.data.DataPackage;

import org.eclipse.birt.chart.model.data.impl.DataPackageImpl;

import org.eclipse.birt.chart.model.impl.ModelPackageImpl;

import org.eclipse.birt.chart.model.layout.LayoutPackage;

import org.eclipse.birt.chart.model.layout.impl.LayoutPackageImpl;

import org.eclipse.birt.chart.model.newtype.DonutSeries;
import org.eclipse.birt.chart.model.newtype.NewtypeFactory;
import org.eclipse.birt.chart.model.newtype.NewtypePackage;

import org.eclipse.birt.chart.model.type.TypePackage;

import org.eclipse.birt.chart.model.type.impl.TypePackageImpl;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

import org.eclipse.emf.ecore.impl.EPackageImpl;

import org.eclipse.emf.ecore.xml.type.XMLTypePackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class NewtypePackageImpl extends EPackageImpl implements NewtypePackage {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass donutSeriesEClass = null;

	/**
	 * Creates an instance of the model <b>Package</b>, registered with
	 * {@link org.eclipse.emf.ecore.EPackage.Registry EPackage.Registry} by the package
	 * package URI value.
	 * <p>Note: the correct way to create the package is via the static
	 * factory method {@link #init init()}, which also performs
	 * initialization of the package, or returns the registered package,
	 * if one already exists.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.emf.ecore.EPackage.Registry
	 * @see org.eclipse.birt.chart.model.newtype.NewtypePackage#eNS_URI
	 * @see #init()
	 * @generated
	 */
	private NewtypePackageImpl() {
		super(eNS_URI, NewtypeFactory.eINSTANCE);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private static boolean isInited = false;

	/**
	 * Creates, registers, and initializes the <b>Package</b> for this model, and for any others upon which it depends.
	 * 
	 * <p>This method is used to initialize {@link NewtypePackage#eINSTANCE} when that field is accessed.
	 * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #eNS_URI
	 * @see #createPackageContents()
	 * @see #initializePackageContents()
	 * @generated
	 */
	public static NewtypePackage init() {
		if (isInited) return (NewtypePackage)EPackage.Registry.INSTANCE.getEPackage(NewtypePackage.eNS_URI);

		// Obtain or create and register package
		NewtypePackageImpl theNewtypePackage = (NewtypePackageImpl)(EPackage.Registry.INSTANCE.get(eNS_URI) instanceof NewtypePackageImpl ? EPackage.Registry.INSTANCE.get(eNS_URI) : new NewtypePackageImpl());

		isInited = true;

		// Initialize simple dependencies
		XMLTypePackage.eINSTANCE.eClass();

		// Obtain or create and register interdependencies
		AttributePackageImpl theAttributePackage = (AttributePackageImpl)(EPackage.Registry.INSTANCE.getEPackage(AttributePackage.eNS_URI) instanceof AttributePackageImpl ? EPackage.Registry.INSTANCE.getEPackage(AttributePackage.eNS_URI) : AttributePackage.eINSTANCE);
		DataPackageImpl theDataPackage = (DataPackageImpl)(EPackage.Registry.INSTANCE.getEPackage(DataPackage.eNS_URI) instanceof DataPackageImpl ? EPackage.Registry.INSTANCE.getEPackage(DataPackage.eNS_URI) : DataPackage.eINSTANCE);
		TypePackageImpl theTypePackage = (TypePackageImpl)(EPackage.Registry.INSTANCE.getEPackage(TypePackage.eNS_URI) instanceof TypePackageImpl ? EPackage.Registry.INSTANCE.getEPackage(TypePackage.eNS_URI) : TypePackage.eINSTANCE);
		ComponentPackageImpl theComponentPackage = (ComponentPackageImpl)(EPackage.Registry.INSTANCE.getEPackage(ComponentPackage.eNS_URI) instanceof ComponentPackageImpl ? EPackage.Registry.INSTANCE.getEPackage(ComponentPackage.eNS_URI) : ComponentPackage.eINSTANCE);
		LayoutPackageImpl theLayoutPackage = (LayoutPackageImpl)(EPackage.Registry.INSTANCE.getEPackage(LayoutPackage.eNS_URI) instanceof LayoutPackageImpl ? EPackage.Registry.INSTANCE.getEPackage(LayoutPackage.eNS_URI) : LayoutPackage.eINSTANCE);
		ModelPackageImpl theModelPackage = (ModelPackageImpl)(EPackage.Registry.INSTANCE.getEPackage(ModelPackage.eNS_URI) instanceof ModelPackageImpl ? EPackage.Registry.INSTANCE.getEPackage(ModelPackage.eNS_URI) : ModelPackage.eINSTANCE);

		// Create package meta-data objects
		theNewtypePackage.createPackageContents();
		theAttributePackage.createPackageContents();
		theDataPackage.createPackageContents();
		theTypePackage.createPackageContents();
		theComponentPackage.createPackageContents();
		theLayoutPackage.createPackageContents();
		theModelPackage.createPackageContents();

		// Initialize created meta-data
		theNewtypePackage.initializePackageContents();
		theAttributePackage.initializePackageContents();
		theDataPackage.initializePackageContents();
		theTypePackage.initializePackageContents();
		theComponentPackage.initializePackageContents();
		theLayoutPackage.initializePackageContents();
		theModelPackage.initializePackageContents();

		// Mark meta-data to indicate it can't be changed
		theNewtypePackage.freeze();

  
		// Update the registry and return the package
		EPackage.Registry.INSTANCE.put(NewtypePackage.eNS_URI, theNewtypePackage);
		return theNewtypePackage;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getDonutSeries() {
		return donutSeriesEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getDonutSeries_Explosion() {
		return (EAttribute)donutSeriesEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getDonutSeries_Thickness() {
		return (EAttribute)donutSeriesEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getDonutSeries_Rotation() {
		return (EAttribute)donutSeriesEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getDonutSeries_ShowDonutLabels() {
		return (EAttribute)donutSeriesEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getDonutSeries_LeaderLineStyle() {
		return (EAttribute)donutSeriesEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getDonutSeries_LeaderLineLength() {
		return (EAttribute)donutSeriesEClass.getEStructuralFeatures().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getDonutSeries_Title() {
		return (EReference)donutSeriesEClass.getEStructuralFeatures().get(6);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getDonutSeries_TitlePosition() {
		return (EAttribute)donutSeriesEClass.getEStructuralFeatures().get(7);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NewtypeFactory getNewtypeFactory() {
		return (NewtypeFactory)getEFactoryInstance();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private boolean isCreated = false;

	/**
	 * Creates the meta-model objects for the package.  This method is
	 * guarded to have no affect on any invocation but its first.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void createPackageContents() {
		if (isCreated) return;
		isCreated = true;

		// Create classes and their features
		donutSeriesEClass = createEClass(DONUT_SERIES);
		createEAttribute(donutSeriesEClass, DONUT_SERIES__EXPLOSION);
		createEAttribute(donutSeriesEClass, DONUT_SERIES__THICKNESS);
		createEAttribute(donutSeriesEClass, DONUT_SERIES__ROTATION);
		createEAttribute(donutSeriesEClass, DONUT_SERIES__SHOW_DONUT_LABELS);
		createEAttribute(donutSeriesEClass, DONUT_SERIES__LEADER_LINE_STYLE);
		createEAttribute(donutSeriesEClass, DONUT_SERIES__LEADER_LINE_LENGTH);
		createEReference(donutSeriesEClass, DONUT_SERIES__TITLE);
		createEAttribute(donutSeriesEClass, DONUT_SERIES__TITLE_POSITION);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private boolean isInitialized = false;

	/**
	 * Complete the initialization of the package and its meta-model.  This
	 * method is guarded to have no affect on any invocation but its first.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void initializePackageContents() {
		if (isInitialized) return;
		isInitialized = true;

		// Initialize package
		setName(eNAME);
		setNsPrefix(eNS_PREFIX);
		setNsURI(eNS_URI);

		// Obtain other dependent packages
		ComponentPackage theComponentPackage = (ComponentPackage)EPackage.Registry.INSTANCE.getEPackage(ComponentPackage.eNS_URI);
		XMLTypePackage theXMLTypePackage = (XMLTypePackage)EPackage.Registry.INSTANCE.getEPackage(XMLTypePackage.eNS_URI);
		AttributePackage theAttributePackage = (AttributePackage)EPackage.Registry.INSTANCE.getEPackage(AttributePackage.eNS_URI);

		// Create type parameters

		// Set bounds for type parameters

		// Add supertypes to classes
		donutSeriesEClass.getESuperTypes().add(theComponentPackage.getSeries());

		// Initialize classes and features; add operations and parameters
		initEClass(donutSeriesEClass, DonutSeries.class, "DonutSeries", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getDonutSeries_Explosion(), theXMLTypePackage.getInt(), "explosion", "0", 1, 1, DonutSeries.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getDonutSeries_Thickness(), theXMLTypePackage.getInt(), "thickness", "0", 0, 1, DonutSeries.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getDonutSeries_Rotation(), theXMLTypePackage.getInt(), "rotation", "0", 0, 1, DonutSeries.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getDonutSeries_ShowDonutLabels(), theXMLTypePackage.getBoolean(), "showDonutLabels", "false", 0, 1, DonutSeries.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getDonutSeries_LeaderLineStyle(), theAttributePackage.getLeaderLineStyle(), "leaderLineStyle", null, 1, 1, DonutSeries.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getDonutSeries_LeaderLineLength(), theXMLTypePackage.getDouble(), "leaderLineLength", "0.0", 1, 1, DonutSeries.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getDonutSeries_Title(), theComponentPackage.getLabel(), null, "title", null, 1, 1, DonutSeries.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getDonutSeries_TitlePosition(), theAttributePackage.getPosition(), "titlePosition", null, 1, 1, DonutSeries.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		// Create resource
		createResource(eNS_URI);

		// Create annotations
		// http:///org/eclipse/emf/ecore/util/ExtendedMetaData
		createExtendedMetaDataAnnotations();
	}

	/**
	 * Initializes the annotations for <b>http:///org/eclipse/emf/ecore/util/ExtendedMetaData</b>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void createExtendedMetaDataAnnotations() {
		String source = "http:///org/eclipse/emf/ecore/util/ExtendedMetaData";				
		addAnnotation
		  (donutSeriesEClass, 
		   source, 
		   new String[] {
			 "name", "DonutSeries",
			 "kind", "elementOnly"
		   });			
		addAnnotation
		  (getDonutSeries_Explosion(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "Explosion"
		   });			
		addAnnotation
		  (getDonutSeries_Thickness(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "Thickness"
		   });			
		addAnnotation
		  (getDonutSeries_Rotation(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "Rotation"
		   });			
		addAnnotation
		  (getDonutSeries_ShowDonutLabels(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "ShowDonutLabels"
		   });		
		addAnnotation
		  (getDonutSeries_LeaderLineStyle(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "LeaderLineStyle"
		   });		
		addAnnotation
		  (getDonutSeries_LeaderLineLength(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "LeaderLineLength"
		   });		
		addAnnotation
		  (getDonutSeries_Title(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "Title"
		   });		
		addAnnotation
		  (getDonutSeries_TitlePosition(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "TitlePosition"
		   });
	}

} //NewtypePackageImpl
