/***********************************************************************
 * Copyright (c) 2008 Actuate Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Actuate Corporation - initial API and implementation
 ***********************************************************************/

package org.eclipse.birt.chart.aggregate;

import org.eclipse.birt.chart.engine.extension.i18n.Messages;


/**
 * @since BIRT 2.3
 */
public class TopPercent extends AggregateFunctionAdapter
{
	/* (non-Javadoc)
	 * @see org.eclipse.birt.chart.aggregate.IAggregateFunction#getDisplayParameters()
	 */
	public String[] getDisplayParameters( )
	{
		return new String[]{Messages.getString("TopPercent.AggregateFunction.Parameters.Label.Percentage")}; //$NON-NLS-1$
	}

	/* (non-Javadoc)
	 * @see org.eclipse.birt.chart.aggregate.IAggregateFunction#getParametersCount()
	 */
	public int getParametersCount( )
	{
		return 1;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.birt.chart.aggregate.AggregateFunctionAdapter#getType()
	 */
	public int getType( )
	{
		return RUNNING_AGGR;
	}
}
