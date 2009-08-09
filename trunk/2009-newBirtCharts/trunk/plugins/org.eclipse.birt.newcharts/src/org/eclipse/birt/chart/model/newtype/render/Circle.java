package org.eclipse.birt.chart.model.newtype.render;


import org.eclipse.birt.chart.device.IDeviceRenderer;
import org.eclipse.birt.chart.event.ArcRenderEvent;
import org.eclipse.birt.chart.event.EventObjectCache;
import org.eclipse.birt.chart.event.WrappedStructureSource;
import org.eclipse.birt.chart.exception.ChartException;
import org.eclipse.birt.chart.model.attribute.Fill;
import org.eclipse.birt.chart.model.attribute.impl.ColorDefinitionImpl;
import org.eclipse.birt.chart.model.attribute.impl.LocationImpl;
import org.eclipse.birt.chart.model.component.Series;


public class Circle {

	private Object dataSet;
	private double xm;
	private double rad;
	double ym;
	private Fill backgroundColor;
	
	public Object getDataSet() {
		return dataSet;
	}

	public double getXm() {
		return xm;
	}

	public double getRad() {
		return rad;
	}

	public double getYm() {
		return ym;
	}

	public ColorDefinitionImpl getBackGroundColor(){
		return (ColorDefinitionImpl) this.backgroundColor;
	}

	public Circle(Object[] d) {
		setDataSet(d);
	}

	private void setDataSet(Object  d) {
		if (d instanceof Double[]){
			this.dataSet = d;
		}
		else if (d instanceof String[]){
			this.dataSet = d;
		}
	}

	 public void setXm(double xm) {
		 this.xm = xm;
	}

	public void setYm(double ym) {
		this.ym = ym;
	}

	public void setRad(double radius) {
		this.rad = radius;
	}

	public void setBackGroundColor(Fill fill) {
		this.backgroundColor = fill;
	}

	public void render(IDeviceRenderer idr, Series vennseries) throws ChartException {
		ArcRenderEvent circle = (ArcRenderEvent) ((EventObjectCache) idr)
		.getEventObject(WrappedStructureSource.createSeries(vennseries), ArcRenderEvent.class);
		
		circle.setOuterRadius(getRad());
		circle.setStartAngle(0);
		circle.setAngleExtent(360);
		
		circle.setBackground(getBackGroundColor());
		
		circle.setTopLeft(LocationImpl.create(xm, ym));
		idr.fillArc(circle);
	}
	

}
