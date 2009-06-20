package org.eclipse.birt.chart.newtype.impl;

import org.eclipse.birt.chart.model.attribute.Position;
import org.eclipse.birt.chart.model.component.Series;
import org.eclipse.birt.chart.model.component.impl.SeriesImpl;
import org.eclipse.birt.chart.newtype.DonutSeries;

public class DonutSeriesImpl extends SeriesImpl implements DonutSeries{

	
	private static final double _RADIUS_DEFAULT = 1.0;
	private String _text;
	private double _radius = _RADIUS_DEFAULT;
	
	@Override
	public void setText(String text) {
		_text = text;
	}

	public String getText() {
		return _text;
	}

	public static final Series create( )
	{
		final DonutSeries donutSeries = new DonutSeriesImpl();
		( (DonutSeriesImpl) donutSeries ).initialize( );
		return donutSeries;
	}
	
	protected final void initialize( )
	{
		super.initialize( );
		setLabelPosition( Position.OUTSIDE_LITERAL );
		getLabel( ).setVisible( true );
	}

	@Override
	public double getRadius() {
		return _radius;
	}

	@Override
	public void setRadius(double rad) {
		_radius = rad;
	}

}
