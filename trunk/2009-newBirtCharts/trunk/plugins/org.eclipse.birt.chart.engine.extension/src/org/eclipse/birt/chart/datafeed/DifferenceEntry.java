/*******************************************************************************
 * Copyright (c) 2004 Actuate Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *  Actuate Corporation  - initial API and implementation
 *******************************************************************************/

package org.eclipse.birt.chart.datafeed;

import org.eclipse.birt.chart.computation.ValueFormatter;
import org.eclipse.birt.chart.exception.ChartException;
import org.eclipse.birt.chart.log.Logger;
import org.eclipse.birt.chart.model.attribute.FormatSpecifier;

import com.ibm.icu.util.ULocale;

/**
 * DifferenceEntry
 */
public final class DifferenceEntry implements IDataPointEntry
{

	private double dPosValue;

	private double dNegValue;

	/**
	 * The constructor.
	 */
	public DifferenceEntry( double dPositiveValue, double dNegativeValue )
	{
		this.dPosValue = dPositiveValue;
		this.dNegValue = dNegativeValue;
	}

	/**
	 * The constructor.
	 * 
	 * @param oaTwoComponents
	 */
	public DifferenceEntry( Object[] oaTwoComponents )
	{
		assert oaTwoComponents.length == 2;

		this.dPosValue = ( oaTwoComponents[0] instanceof Number )
				? ( (Number) oaTwoComponents[0] ).doubleValue( ) : Double.NaN;
		this.dNegValue = ( oaTwoComponents[1] instanceof Number )
				? ( (Number) oaTwoComponents[1] ).doubleValue( ) : Double.NaN;
	}

	private static String getDoubleStr( double d )
	{
		if ( Double.isNaN( d ) )
		{
			return "null"; //$NON-NLS-1$
		}
		else
		{
			return Double.toString( d );
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString( )
	{
		return "P" + getDoubleStr( dPosValue ) + " N" + getDoubleStr( dNegValue ); //$NON-NLS-1$//$NON-NLS-2$
	}

	/**
	 * @return Returns the positive value.
	 */
	public final double getPositiveValue( )
	{
		return dPosValue;
	}

	/**
	 * @param value
	 *            The positive value to set.
	 */
	public final void setPositiveValue( double value )
	{
		this.dPosValue = value;
	}

	/**
	 * @return Returns the negative value.
	 */
	public final double getNegativeValue( )
	{
		return dNegValue;
	}

	/**
	 * @param end
	 *            The negative value to set.
	 */
	public final void setNegativeValue( double value )
	{
		this.dNegValue = value;
	}

	public String getFormattedString( String type, FormatSpecifier formatter,
			ULocale locale )
	{
		String str = null;
		try
		{
			if ( DifferenceDataPointDefinition.TYPE_POSITIVE_VALUE.equals( type ) )
			{
				str = ValueFormatter.format( new Double( dPosValue ),
						formatter,
						locale,
						null );
			}
			else if ( DifferenceDataPointDefinition.TYPE_NEGATIVE_VALUE.equals( type ) )
			{
				str = ValueFormatter.format( new Double( dNegValue ),
						formatter,
						locale,
						null );
			}
		}
		catch ( ChartException e )
		{
			Logger.getLogger( "org.eclipse.birt.chart.engine/exception" ) //$NON-NLS-1$
					.log( e );
		}
		return str;
	}

	public String getFormattedString( Object formatter, ULocale locale )
	{
		return toString( );
	}

	public boolean isValid( )
	{
		return !Double.isNaN( dNegValue ) && !Double.isNaN( dPosValue );
	}

}
