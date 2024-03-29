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

import java.util.Date;

import org.eclipse.birt.chart.computation.ValueFormatter;
import org.eclipse.birt.chart.exception.ChartException;
import org.eclipse.birt.chart.log.Logger;
import org.eclipse.birt.chart.model.attribute.FormatSpecifier;
import org.eclipse.birt.chart.util.CDateTime;

import com.ibm.icu.util.Calendar;
import com.ibm.icu.util.ULocale;

/**
 * GanttEntry
 */
public final class GanttEntry implements IDataPointEntry
{

	private CDateTime dateStart;

	private CDateTime dateEnd;

	private String strLabel;

	/**
	 * The constructor.
	 * 
	 * @param dateStart
	 * @param dateEnd
	 * @param strLabel
	 */
	public GanttEntry( Calendar dateStart, Calendar dateEnd, String strLabel )
	{
		this.dateStart = new CDateTime( dateStart );
		this.dateEnd = new CDateTime( dateEnd );
		this.strLabel = strLabel;
	}

	/**
	 * The constructor.
	 * 
	 * @param dateStart
	 * @param dateEnd
	 * @param strLabel
	 */
	public GanttEntry( Date dateStart, Date dateEnd, String strLabel )
	{
		this.dateStart = new CDateTime( dateStart );
		this.dateEnd = new CDateTime( dateEnd );
		this.strLabel = strLabel;
	}

	/**
	 * The constructor.
	 * 
	 * @param dateStart
	 * @param dateEnd
	 * @param strLabel
	 */
	public GanttEntry( CDateTime dateStart, CDateTime dateEnd, String strLabel )
	{
		this.dateStart = dateStart;
		this.dateEnd = dateEnd;
		this.strLabel = strLabel;
	}

	/**
	 * The constructor.
	 * 
	 * @param oaThreeComponents
	 */
	GanttEntry( Object[] oaThreeComponents )
	{
		if ( oaThreeComponents[0] instanceof CDateTime )
		{
			this.dateStart = (CDateTime) oaThreeComponents[0];
		}
		else
		{
			this.dateStart = null;
		}

		if ( oaThreeComponents[1] instanceof CDateTime )
		{
			this.dateEnd = (CDateTime) oaThreeComponents[1];
		}
		else
		{
			this.dateEnd = null;
		}

		if ( oaThreeComponents[2] != null )
		{
			this.strLabel = String.valueOf( oaThreeComponents[2] );
		}
		else
		{
			this.strLabel = null;
		}
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
	 * @return Returns the start datetime.
	 */
	public final CDateTime getStart( )
	{
		return dateStart;
	}

	/**
	 * @param start
	 *            The start datetime to set.
	 */
	public final void setStart( CDateTime start )
	{
		this.dateStart = start;
	}

	/**
	 * @return Returns the end datetime.
	 */
	public final CDateTime getEnd( )
	{
		return dateEnd;
	}

	/**
	 * @param end
	 *            The end datetime to set.
	 */
	public final void setEnd( CDateTime end )
	{
		this.dateEnd = end;
	}

	/**
	 * @return Returns the label.
	 */
	public final String getLabel( )
	{
		return strLabel;
	}

	/**
	 * @param end
	 *            The label to set.
	 */
	public final void setLabel( String strLabel )
	{
		this.strLabel = strLabel;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.chart.datafeed.IFormattable#getFormattedString(java.lang.Object,
	 *      com.ibm.icu.util.ULocale)
	 */
	public String getFormattedString( Object formatter, ULocale locale )
	{
		String strStart = ""; //$NON-NLS-1$
		String strEnd = ""; //$NON-NLS-1$
		try
		{
			strStart = ValueFormatter.format( dateStart,
					null,
					locale,
					formatter );
			strEnd = ValueFormatter.format( dateEnd, null, locale, formatter );
		}
		catch ( ChartException e )
		{
			Logger.getLogger( "org.eclipse.birt.chart.engine.extension/render" ) //$NON-NLS-1$
					.log( e );
		}
		return "S" + strStart + " E" + strEnd + " " + strLabel; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	public String getFormattedString( String type, FormatSpecifier formatter,
			ULocale locale )
	{
		String str = null;
		try
		{
			if ( GanttDataPointDefinition.TYPE_START_DATE.equals( type ) )
			{
				str = ValueFormatter.format( dateStart, formatter, locale, null );
			}
			else if ( GanttDataPointDefinition.TYPE_END_DATE.equals( type ) )
			{
				str = ValueFormatter.format( dateEnd, formatter, locale, null );
			}
			else if ( GanttDataPointDefinition.TYPE_DECORATION_LABEL.equals( type ) )
			{
				str = ValueFormatter.format( strLabel, formatter, locale, null );
			}
		}
		catch ( ChartException e )
		{
			Logger.getLogger( "org.eclipse.birt.chart.engine/exception" ) //$NON-NLS-1$
					.log( e );
		}
		return str;
	}

	public boolean isValid( )
	{
		return ( dateStart != null || dateEnd != null );
	}

}
