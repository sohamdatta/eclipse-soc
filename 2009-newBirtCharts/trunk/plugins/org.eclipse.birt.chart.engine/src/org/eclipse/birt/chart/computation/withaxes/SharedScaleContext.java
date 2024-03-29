/*******************************************************************************
 * Copyright (c) 2008 Actuate Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *  Actuate Corporation  - initial API and implementation
 *******************************************************************************/

package org.eclipse.birt.chart.computation.withaxes;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.birt.chart.computation.DataSetIterator;
import org.eclipse.birt.chart.exception.ChartException;

/**
 * We use this class to store shared scale context of chart in cross-tab.
 * 
 * @since 2.5
 */

public final class SharedScaleContext
{

	private ScaleContext scaleContext;
	private List<Object> alMinmax = new ArrayList<Object>( 2 );
	private boolean bShared = false;

	public SharedScaleContext( ScaleContext scaleContext, Object realMin,
			Object realMax )
	{
		super( );
		this.scaleContext = scaleContext;
		alMinmax.add( realMin );
		alMinmax.add( realMax );
	}

	/**
	 * 
	 * @param oMin
	 * @param oMax
	 * @return
	 */
	public static final SharedScaleContext createInstance( Object oMin,
			Object oMax )
	{
		ScaleContext sct = ScaleContext.createSimpleScale( oMin, oMax );
		return new SharedScaleContext( sct, sct.getMin( ), sct.getMax( ) );
	}

	/**
	 * @return Returns the scaleContext.
	 */
	public final ScaleContext getScaleContext( )
	{
		return scaleContext;
	}

	/**
	 * @param scaleContext
	 *            The scaleContext to set.
	 */
	public final void setScaleContext( ScaleContext scaleContext )
	{
		this.scaleContext = scaleContext;
	}

	/**
	 * Returns if the scale will be shared among multiple chart instances
	 * 
	 * @return shared or not
	 * @since 2.5
	 */
	public final boolean isShared( )
	{
		return bShared;
	}

	/**
	 * @param shared
	 * @since 2.5
	 * 
	 */
	public final void setShared( boolean shared )
	{
		bShared = shared;
	}

	/**
	 * Create a DataSetIterator with the min/max value, which can be used by
	 * AutoScale.
	 * 
	 * @param iDataType
	 * @return
	 * @throws ChartException
	 * @throws IllegalArgumentException
	 */
	public final DataSetIterator createDataSetIterator( int iDataType )
			throws ChartException, IllegalArgumentException
	{
		return new DataSetIterator( alMinmax, iDataType );
	}

}
