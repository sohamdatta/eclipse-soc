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

package org.eclipse.birt.chart.datafeed;

import org.eclipse.birt.chart.computation.ValueFormatter;
import org.eclipse.birt.chart.exception.ChartException;
import org.eclipse.birt.chart.log.Logger;
import org.eclipse.birt.chart.model.attribute.FormatSpecifier;

import com.ibm.icu.util.ULocale;

/**
 * StockEntry
 */
public final class StockEntry implements IDataPointEntry
{

	private double dOpen;

	private double dLow;

	private double dHigh;

	private double dClose;

	/**
	 * 
	 * @param dOpen
	 * @param dLow
	 * @param dHigh
	 * @param dClose
	 */
	public StockEntry( double dOpen, double dLow, double dHigh, double dClose )
	{
		this.dOpen = dOpen;
		this.dLow = dLow;
		this.dHigh = dHigh;
		this.dClose = dClose;
	}

	/**
	 * 
	 * @param oaFourComponents
	 */
	public StockEntry( Object[] oaFourComponents )
	{
		assert oaFourComponents.length == 4;
		this.dHigh = ( oaFourComponents[0] instanceof Number )
				? ( (Number) oaFourComponents[0] ).doubleValue( ) : Double.NaN;
		this.dLow = ( oaFourComponents[1] instanceof Number )
				? ( (Number) oaFourComponents[1] ).doubleValue( ) : Double.NaN;
		this.dOpen = ( oaFourComponents[2] instanceof Number )
				? ( (Number) oaFourComponents[2] ).doubleValue( ) : Double.NaN;
		this.dClose = ( oaFourComponents[3] instanceof Number )
				? ( (Number) oaFourComponents[3] ).doubleValue( ) : Double.NaN;
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
		return "H" + getDoubleStr( dHigh ) + " L" + getDoubleStr( dLow ) + " O" + getDoubleStr( dOpen ) + " C" + getDoubleStr( dClose ); //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$ //$NON-NLS-4$
	}

	/**
	 * @return Returns the close.
	 */
	public final double getClose( )
	{
		return dClose;
	}

	/**
	 * @param close
	 *            The close to set.
	 */
	public final void setClose( double close )
	{
		this.dClose = close;
	}

	/**
	 * @return Returns the high.
	 */
	public final double getHigh( )
	{
		return dHigh;
	}

	/**
	 * @param high
	 *            The high to set.
	 */
	public final void setHigh( double high )
	{
		this.dHigh = high;
	}

	/**
	 * @return Returns the low.
	 */
	public final double getLow( )
	{
		return dLow;
	}

	/**
	 * @param low
	 *            The low to set.
	 */
	public final void setLow( double low )
	{
		this.dLow = low;
	}

	/**
	 * @return Returns the open.
	 */
	public final double getOpen( )
	{
		return dOpen;
	}

	/**
	 * @param open
	 *            The open to set.
	 */
	public final void setOpen( double open )
	{
		this.dOpen = open;
	}

	public String getFormattedString( String type, FormatSpecifier formatter,
			ULocale locale )
	{
		String str = null;
		try
		{
			if ( StockDataPointDefinition.TYPE_HIGH.equals( type ) )
			{
				str = ValueFormatter.format( new Double( dHigh ),
						formatter,
						locale,
						null );
			}
			else if ( StockDataPointDefinition.TYPE_LOW.equals( type ) )
			{
				str = ValueFormatter.format( new Double( dLow ),
						formatter,
						locale,
						null );
			}
			else if ( StockDataPointDefinition.TYPE_OPEN.equals( type ) )
			{
				str = ValueFormatter.format( new Double( dOpen ),
						formatter,
						locale,
						null );
			}
			else if ( StockDataPointDefinition.TYPE_CLOSE.equals( type ) )
			{
				str = ValueFormatter.format( new Double( dClose ),
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
		return ( !( Double.isNaN( dHigh )
				|| Double.isNaN( dLow )
				|| Double.isNaN( dClose ) || Double.isNaN( dOpen ) ) );
	}
}
