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

package org.eclipse.birt.chart.computation.withaxes;

import org.eclipse.birt.chart.computation.DataPointHints;
import org.eclipse.birt.chart.computation.DataSetIterator;
import org.eclipse.birt.chart.computation.GObjectFactory;
import org.eclipse.birt.chart.computation.IConstants;
import org.eclipse.birt.chart.computation.IGObjectFactory;
import org.eclipse.birt.chart.computation.Methods;
import org.eclipse.birt.chart.exception.ChartException;
import org.eclipse.birt.chart.model.attribute.Bounds;
import org.eclipse.birt.chart.model.attribute.Position;
import org.eclipse.birt.chart.model.component.Label;
import org.eclipse.birt.chart.model.component.Series;
import org.eclipse.birt.chart.render.ISeriesRenderingHints;

/**
 * SeriesRenderingHints
 */
public final class SeriesRenderingHints implements ISeriesRenderingHints
{

	private int iDataSetStructure = UNDEFINED;

	private final double dAxisLocation;

	private final double dZeroLocation;

	private final double dSeriesThickness;

	private final double dPlotBaseLocation;

	private final AxisTickCoordinates daTickCoordinates;

	private final DataPointHints[] dpa;

	private final AutoScale scBase;

	private final AutoScale scOrthogonal;

	private final StackedSeriesLookup ssl;

	private final PlotWith2DAxes pwa;

	private final DataSetIterator dsiBase;

	private final DataSetIterator dsiOrthogonal;

	private static final IGObjectFactory goFactory = GObjectFactory.instance( );

	/**
	 * 
	 * @param _dAxisLocation
	 * @param _dZeroLocation
	 * @param _daTickCoordinates
	 * @param _dpa
	 */
	public SeriesRenderingHints( PlotWith2DAxes _pwa, double _dAxisLocation,
			double _dPlotBaseLocation, double _dZeroLocation,
			double _dSeriesThickness, AxisTickCoordinates _daTickCoordinates,
			DataPointHints[] _dpa, AutoScale _scBase, AutoScale _scOrthogonal,
			StackedSeriesLookup _ssl, DataSetIterator _dsiBase,
			DataSetIterator _dsiOrthogonal )
	{
		pwa = _pwa;
		dAxisLocation = _dAxisLocation;
		dZeroLocation = _dZeroLocation;
		dPlotBaseLocation = _dPlotBaseLocation;
		dSeriesThickness = _dSeriesThickness;
		daTickCoordinates = _daTickCoordinates;
		dpa = _dpa;
		scBase = _scBase;
		scOrthogonal = _scOrthogonal;
		ssl = _ssl;
		dsiBase = _dsiBase;
		dsiOrthogonal = _dsiOrthogonal;

		// DEFINE THE DATA SET STRUCTURES
		if ( dsiBase.size( ) != dsiOrthogonal.size( ) )
		{
			iDataSetStructure |= BASE_ORTHOGONAL_OUT_OF_SYNC;
		}
		else
		{
			iDataSetStructure = BASE_ORTHOGONAL_IN_SYNC;
		}
		if ( dsiBase.isEmpty( ) )
		{
			iDataSetStructure |= BASE_EMPTY;
		}
		if ( dsiOrthogonal.isEmpty( ) )
		{
			iDataSetStructure |= ORTHOGONAL_EMPTY;
		}
	}

	/**
	 * 
	 * @return The location (if vertical, then horizontal co-ordinate; if
	 *         horizontal, then vertical co-ordinate) of the category axis used
	 *         in the plot
	 */
	public final double getAxisLocation( )
	{
		return dAxisLocation;
	}

	/**
	 * 
	 * @return The location (if vertical, then horizontal co-ordinate; if
	 *         horizontal, then vertical co-ordinate) of zero along the primary
	 *         orthogonal (value) axis used in the plot
	 */
	public final double getZeroLocation( )
	{
		return dZeroLocation;
	}

	/**
	 * @param oValue
	 *            The value for which a rendering co-ordinate is being requested
	 *            for
	 * @return The co-ordinate on the scale that corresponds to the requested
	 *         value
	 * 
	 */
	public final double getLocationOnOrthogonal( Object oValue )
			throws ChartException, IllegalArgumentException
	{
		return Methods.getLocation( scOrthogonal, oValue );
	}

	/**
	 * 
	 * @return
	 */
	public final double getPlotBaseLocation( )
	{
		return dPlotBaseLocation;
	}

	/**
	 * 
	 * @return The thickness of the series element to be rendered in a 2.5D or
	 *         3D plot
	 */
	public final double getSeriesThickness( )
	{
		return dSeriesThickness;
	}

	/**
	 * 
	 * @return The ticks' co-ordinates specified as a values along a horizontal
	 *         or vertical category axis. The other fixed co-ordinate is
	 *         obtained via the axis location.
	 */
	public final AxisTickCoordinates getTickCoordinates( )
	{
		return daTickCoordinates;
	}

	/**
	 * 
	 * @return Detailed plotting information for the data points represented by
	 *         the series rendering
	 */
	public final DataPointHints[] getDataPoints( )
	{
		return dpa;
	}

	/**
	 * 
	 * @return
	 */
	public final boolean isCategoryScale( )
	{
		return ( scBase.getType( ) == IConstants.TEXT || scBase.isCategoryScale( ) );
	}

	/**
	 * 
	 * @return
	 */
	public final StackedSeriesLookup getStackedSeriesLookup( )
	{
		return ssl;
	}

	/**
	 * 
	 * @param se
	 * @return
	 * @throws ChartException
	 * @throws IllegalArgumentException
	 */
	public final Position getLabelPosition( Series se ) throws ChartException,
			IllegalArgumentException
	{
		final int iTransposed = pwa.transposeLabelPosition( IConstants.ORTHOGONAL,
				Methods.getLabelPosition( se.getLabelPosition( ) ) );
		Position p = null;
		switch ( iTransposed )
		{
			case IConstants.LEFT :
				p = Position.LEFT_LITERAL;
				break;
			case IConstants.RIGHT :
				p = Position.RIGHT_LITERAL;
				break;
			case IConstants.ABOVE :
				p = Position.ABOVE_LITERAL;
				break;
			case IConstants.BELOW :
				p = Position.BELOW_LITERAL;
				break;
			case IConstants.OUTSIDE :
				p = Position.OUTSIDE_LITERAL;
				break;
			case IConstants.INSIDE :
				p = Position.INSIDE_LITERAL;
				break;
		}
		return p;
	}

	/**
	 * 
	 * @param se
	 * @return
	 * @throws IllegalArgumentException
	 */
	public final Label getLabelAttributes( Series se )
			throws IllegalArgumentException
	{
		return goFactory.copyOf( se.getLabel( ) );
	}

	/**
	 * 
	 * @param bReduceByInsets
	 * @return
	 */
	public final Bounds getClientAreaBounds( boolean bReduceByInsets )
	{
		final Bounds boClientArea = goFactory.copyOf( pwa.getPlotBounds( ) );
		if ( bReduceByInsets )
		{
			boClientArea.adjust( pwa.getPlotInsets( ) );
		}
		return boClientArea;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.chart.render.ISeriesRenderingHints#getDataSetStructure()
	 */
	public int getDataSetStructure( )
	{
		return iDataSetStructure;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.chart.render.ISeriesRenderingHints#getBaseDataSet()
	 */
	public DataSetIterator getBaseDataSet( )
	{
		return dsiBase;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.chart.render.ISeriesRenderingHints#getOrthogonalDataSet()
	 */
	public DataSetIterator getOrthogonalDataSet( )
	{
		return dsiOrthogonal;
	}
	
	/**
	 * Returns Orthogonal axis's AutoScale object.
	 * 
	 * @return
	 * @since 2.5
	 */
	public AutoScale getOrthogonalScale( )
	{
		return scOrthogonal;
	}
}