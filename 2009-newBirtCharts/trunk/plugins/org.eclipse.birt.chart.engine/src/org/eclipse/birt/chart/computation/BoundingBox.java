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

package org.eclipse.birt.chart.computation;

/**
 * This class ...
 * 
 * @author Actuate Corporation
 */
public final class BoundingBox implements Cloneable
{

	private double dX, dY;

	private double dWidth, dHeight;

	private double dHotPoint;

	public BoundingBox( int _iLabelLocation, double _dX, double _dY,
			double _dWidth, double _dHeight, double _dHotPoint )
	{
		dX = _dX;
		dY = _dY;
		dWidth = _dWidth;
		dHeight = _dHeight;
		dHotPoint = _dHotPoint;
	}
	
	public BoundingBox clone( )
	{
		return new BoundingBox( 0, dX, dY, dWidth, dHeight, dHotPoint );
	}

	public final double getHotPoint( )
	{
		return dHotPoint;
	}

	public final double getTop( )
	{
		return dY;
	}

	public final double getLeft( )
	{
		return dX;
	}

	public final double getWidth( )
	{
		return dWidth;
	}

	public final double getHeight( )
	{
		return dHeight;
	}

	public final void setLeft( double _dX )
	{
		dX = _dX;
	}

	public final void setTop( double _dY )
	{
		dY = _dY;
	}

	public final void scale( double dScale )
	{
		dX *= dScale;
		dY *= dScale;
		dWidth *= dScale;
		dHeight *= dScale;
		dHotPoint *= dScale;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode( )
	{
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits( dHeight );
		result = prime * result + (int) ( temp ^ ( temp >>> 32 ) );
		temp = Double.doubleToLongBits( dHotPoint );
		result = prime * result + (int) ( temp ^ ( temp >>> 32 ) );
		temp = Double.doubleToLongBits( dWidth );
		result = prime * result + (int) ( temp ^ ( temp >>> 32 ) );
		temp = Double.doubleToLongBits( dX );
		result = prime * result + (int) ( temp ^ ( temp >>> 32 ) );
		temp = Double.doubleToLongBits( dY );
		result = prime * result + (int) ( temp ^ ( temp >>> 32 ) );
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals( Object obj )
	{
		if ( this == obj )
			return true;
		if ( obj == null )
			return false;
		if ( getClass( ) != obj.getClass( ) )
			return false;
		BoundingBox other = (BoundingBox) obj;
		if ( Double.doubleToLongBits( dHeight ) != Double.doubleToLongBits( other.dHeight ) )
			return false;
		if ( Double.doubleToLongBits( dHotPoint ) != Double.doubleToLongBits( other.dHotPoint ) )
			return false;
		if ( Double.doubleToLongBits( dWidth ) != Double.doubleToLongBits( other.dWidth ) )
			return false;
		if ( Double.doubleToLongBits( dX ) != Double.doubleToLongBits( other.dX ) )
			return false;
		if ( Double.doubleToLongBits( dY ) != Double.doubleToLongBits( other.dY ) )
			return false;
		return true;
	}
}