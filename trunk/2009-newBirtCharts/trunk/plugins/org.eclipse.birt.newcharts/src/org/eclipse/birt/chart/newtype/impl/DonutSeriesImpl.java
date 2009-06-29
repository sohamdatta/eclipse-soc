package org.eclipse.birt.chart.newtype.impl;

import org.eclipse.birt.chart.model.attribute.Position;
import org.eclipse.birt.chart.model.component.Series;
import org.eclipse.birt.chart.model.component.impl.SeriesImpl;
import org.eclipse.birt.chart.newtype.DonutSeries;

public class DonutSeriesImpl extends SeriesImpl implements DonutSeries{

	
	private static final int _EXPLOSION_DEFAULT = 0;
	private static final int _ROTATION_DEFAULT = 0;
	private static final int _THICKNESS_DEFAULT = 0;
	private String _text;
	private int _explosion;
	private int _rotation;
	private int _thickness;
	private boolean _isExploded = false;
	private boolean _isRotated = false;
	
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
	public int getExplosion() {
		return _explosion;
	}

	@Override
	public int getRotation() {
		return _rotation;
	}

	@Override
	public int getThickness() {
		return _thickness;
	}

	@Override
	public boolean isRotatet() {
		return _isRotated;
	}

	@Override
	public boolean isSetExplosion() {
		return _isExploded;
	}

	@Override
	public void setExplosion(int pExplosion) {
		_explosion = pExplosion;
		_isExploded = true;
	}

	@Override
	public void setRotation(int pRotationInDegree) {
		_rotation = pRotationInDegree;
		_isRotated = true;
	}

	@Override
	public void setThickness(int pThickness) {
		_thickness = pThickness;
	}

	@Override
	public void unsetExplosion() {
		_explosion = _EXPLOSION_DEFAULT;
		_isExploded = false;
	}

	@Override
	public void unsetRotation() {
		_rotation = _ROTATION_DEFAULT;
		_isRotated = false;
	}

	@Override
	public void unsetThickness() {
		_thickness = _THICKNESS_DEFAULT;
	}

}
