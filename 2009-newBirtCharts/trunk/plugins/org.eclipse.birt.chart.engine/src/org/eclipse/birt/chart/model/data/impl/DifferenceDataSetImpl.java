/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */

package org.eclipse.birt.chart.model.data.impl;

import org.eclipse.birt.chart.model.data.DataFactory;
import org.eclipse.birt.chart.model.data.DataPackage;
import org.eclipse.birt.chart.model.data.DifferenceDataSet;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Difference Data Set</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * </p>
 *
 * @generated
 */
public class DifferenceDataSetImpl extends DataSetImpl implements
		DifferenceDataSet
{

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	protected DifferenceDataSetImpl( )
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
		return DataPackage.Literals.DIFFERENCE_DATA_SET;
	}

	/**
	 * A convenience method to create an initialized 'DifferenceDataSet'
	 * instance
	 * 
	 * @param oValues
	 *            The Collection (of DifferenceEntry) or DifferenceEntry[] of
	 *            values associated with this dataset
	 * 
	 * @return
	 */
	public static final DifferenceDataSet create( Object oValues )
	{
		final DifferenceDataSet dds = DataFactory.eINSTANCE.createDifferenceDataSet( );
		( (DifferenceDataSetImpl) dds ).initialize( );
		dds.setValues( oValues );
		return dds;
	}

	/**
	 * This method performs any initialization of the instance when created
	 * 
	 * Note: Manually written
	 */
	protected void initialize( )
	{
	}

	/**
	 * A convenient method to get an instance copy. This is much faster than the
	 * ECoreUtil.copy().
	 */
	public DifferenceDataSet copyInstance( )
	{
		DifferenceDataSetImpl dest = new DifferenceDataSetImpl( );
		dest.set( this );
		return dest;
	}

	protected void set( DifferenceDataSet src )
	{
		super.set( src );

	}

	public static DifferenceDataSet create( EObject parent, EReference ref )
	{
		return new DifferenceDataSetImpl( );
	}

} // DifferenceDataSetImpl
