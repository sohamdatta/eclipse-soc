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
 * BubbleEntry
 */
public final class BubbleEntry implements IDataPointEntry
{

	private Object oValue;

	private double dSize;

	/** Index for category value, starting with 1. Default value is 0 */
	private int index = 0;

	/**
	 * The constructor.
	 * 
	 * @param value
	 *            value could be any type or null. Value will represent a
	 *            category entry with the specified index.
	 * @param size
	 *            size could be Number or null. Null means this entry will be
	 *            omitted
	 * @param index
	 *            index for category value. Starting with 1
	 */
	public BubbleEntry( Object value, Object size, int index )
	{
		this( value, size );
		this.index = index;
	}

	/**
	 * The constructor.
	 * 
	 * @param value
	 *            value could be Number, String, CDateTime or null. Null means
	 *            this entry will be omitted.
	 * @param size
	 *            size could be Number or null. Null means this entry will be
	 *            omitted
	 */
	public BubbleEntry( Object value, Object size )
	{
		this.oValue = value;
		if ( value instanceof Double && ( (Double) value ).isNaN( ) )
		{
			// Handle NaN as null
			this.oValue = null;
		}
		// Invalid size is set to 0 by default
		this.dSize = ( size instanceof Number )
				? ( (Number) size ).doubleValue( ) : 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString( )
	{
		return getFormattedString( null, ULocale.getDefault( ) );
	}

	/**
	 * @return Returns the Object value.
	 */
	public final Object getValue( )
	{
		if ( index > 0 )
		{
			return Integer.valueOf( index );
		}
		return this.oValue;
	}

	/**
	 * @param value
	 *            The value to set.
	 */
	public final void setValue( Object value )
	{
		this.oValue = value;
	}

	/**
	 * @return Returns the size.
	 */
	public final double getSize( )
	{
		return dSize;
	}

	/**
	 * @param end
	 *            The size to set.
	 */
	public final void setSize( double dSize )
	{
		this.dSize = dSize;
	}

	public String getFormattedString( String type, FormatSpecifier formatter,
			ULocale locale )
	{
		String str = null;
		try
		{
			if ( BubbleDataPointDefinition.TYPE_VALUE.equals( type ) )
			{
				str = ValueFormatter.format( oValue, formatter, locale, null );
			}
			else if ( BubbleDataPointDefinition.TYPE_SIZE.equals( type ) )
			{
				str = ValueFormatter.format( new Double( dSize ),
						formatter,
						locale,
						null );
			}
		}
		catch ( ChartException e )
		{
			Logger.getLogger( "org.eclipse.birt.chart.engine.extension/render" ) //$NON-NLS-1$
					.log( e );
		}
		return str;
	}

	public String getFormattedString( Object formatter, ULocale locale )
	{
		if ( oValue == null )
		{
			return "S" + String.valueOf( dSize ); //$NON-NLS-1$
		}
		String strFormat = ""; //$NON-NLS-1$
		try
		{
			strFormat = ValueFormatter.format( oValue,
					formatter instanceof FormatSpecifier ? (FormatSpecifier) formatter
							: null,
					locale,
					null );
		}
		catch ( ChartException e )
		{
			Logger.getLogger( "org.eclipse.birt.chart.engine.extension/render" ) //$NON-NLS-1$
					.log( e );
		}
		return "Y" + strFormat + " S" + dSize; //$NON-NLS-1$//$NON-NLS-2$
	}

	public boolean isValid( )
	{
		return getValue( ) != null && !Double.isNaN( dSize ) && dSize != 0;
	}
}
