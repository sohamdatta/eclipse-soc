/*******************************************************************************
 * Copyright (c) 2006 Actuate Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *  Actuate Corporation  - initial API and implementation
 *******************************************************************************/

package org.eclipse.birt.chart.datafeed;

import org.eclipse.birt.chart.computation.IConstants;
import org.eclipse.birt.chart.engine.extension.i18n.Messages;

/**
 * 
 */

public class DifferenceDataPointDefinition extends AbstractDataPointDefinition
{

	public final static String TYPE_POSITIVE_VALUE = "difference_positive"; //$NON-NLS-1$

	public final static String TYPE_NEGATIVE_VALUE = "difference_negative"; //$NON-NLS-1$

	private final String[] saTypeNames = {
			TYPE_POSITIVE_VALUE, TYPE_NEGATIVE_VALUE
	};

	private final int[] iaTypeCompatibles = {
			IConstants.NUMERICAL, IConstants.NUMERICAL
	};

	public String[] getDataPointTypes( )
	{
		return new String[]{
				TYPE_POSITIVE_VALUE, TYPE_NEGATIVE_VALUE
		};
	}

	public String getDisplayText( String type )
	{
		if ( TYPE_POSITIVE_VALUE.equals( type ) )
		{
			return Messages.getString( "info.datapoint.PositiveValue" ); //$NON-NLS-1$
		}
		else if ( TYPE_NEGATIVE_VALUE.equals( type ) )
		{
			return Messages.getString( "info.datapoint.NegativeValue" ); //$NON-NLS-1$
		}
		return null;
	}
	
	public int getCompatibleDataType( String type )
	{
		for ( int i = 0; i < saTypeNames.length; i++ )
		{
			if ( saTypeNames[i].equals( type ) )
			{
				return this.iaTypeCompatibles[i];
			}
		}

		// no match, return the default value
		return 0;
	}
	
}
