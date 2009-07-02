package org.eclipse.birt.chart.model.newtype.render;

import org.eclipse.birt.chart.computation.DataPointHints;
import org.eclipse.birt.chart.model.attribute.Bounds;
import org.eclipse.birt.chart.model.attribute.Fill;

public class DonutSlice {

	private DataPointHints dataPoint;
	private Fill fillColor;
	private double angleExtent;
	private double startAngle;
	private int exploration;
	private Bounds bounds;
	private double width;
	private double thickness;
	private double height;
	private double xc;
	private double yc;

	public DonutSlice(double startAngle, double angleExtent, Fill fillColor,
			DataPointHints dph, double thickness) {

		this.setStartAngle(startAngle);
		this.setAngleExtent(angleExtent);
		this.setFillColor(fillColor);
		this.setDataPoint(dph);
		this.setThickness(thickness);
	}

	private void setThickness(double thickness) {
		this.thickness = thickness;
	}

	public double getThickness() {
		return thickness;
	}

	public void setExploded(int exploration) {
		this.exploration = exploration;
	}

	public void setDataPoint(DataPointHints dataPoint) {
		this.dataPoint = dataPoint;
	}

	public DataPointHints getDataPoint() {
		return dataPoint;
	}

	public void setFillColor(Fill fillColor) {
		this.fillColor = fillColor;
	}

	public Fill getFillColor() {
		return fillColor;
	}

	public void setAngleExtent(double angleExtent) {
		this.angleExtent = angleExtent;
	}

	public double getAngleExtent() {
		return angleExtent;
	}

	public void setStartAngle(double startAngle) {
		this.startAngle = startAngle;
	}

	public double getStartAngle() {
		return startAngle;
	}

	public void setBounds(Bounds cellBounds) {
		bounds = cellBounds;
		setWidth(bounds.getWidth() / 2 - exploration);
		setHeight(bounds.getHeight() / 2 - exploration - thickness / 2);
		setXc(bounds.getLeft() + getWidth() + exploration);
		setYc(bounds.getTop() + getHeight() + exploration + thickness / 2);

		// if ( ratio > 0 && width > 0 )
		// {
		// if ( height / width > ratio )
		// {
		// height = width * ratio;
		// }
		// else if ( height / width < ratio )
		// {
		// width = height / ratio;
		// }
		// }

		// detect invalid size.
		if (getWidth() <= 0 || getHeight() <= 0) {
			setWidth(setHeight(1));
		}
	}

	public void computeLabelBoundInside() {
//		TODO
	}

	public void setXc(double xc) {
		this.xc = xc;
	}

	public double getXc() {
		return xc;
	}

	public void setYc(double yc) {
		this.yc = yc;
	}

	public double getYc() {
		return yc;
	}

	public void setWidth(double width) {
		this.width = width;
	}

	public double getWidth() {
		return width;
	}

	public double setHeight(double height) {
		this.height = height;
		return height;
	}

	public double getHeight() {
		return height;
	}
}
