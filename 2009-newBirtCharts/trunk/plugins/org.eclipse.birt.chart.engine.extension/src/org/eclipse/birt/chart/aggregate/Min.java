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

package org.eclipse.birt.chart.aggregate;

/**
 * 
 */

public class Min extends AggregateFunctionAdapter
{

	private Object min;

	public void accumulate( Object oValue ) throws IllegalArgumentException
	{
		if ( min == null )
		{
			min = oValue;
		}
		else if ( oValue instanceof Comparable )
		{
			min = ((Comparable)oValue ).compareTo( min ) <= 0 ? oValue : min;
		}
		
	}

	public Object getAggregatedValue( )
	{
		return min;
	}

	public void initialize( )
	{
		min = null;
	}
}

