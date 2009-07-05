package org.eclipse.birt.chart.model.newtype.render;

import org.eclipse.birt.chart.computation.DataPointHints;
import org.eclipse.birt.chart.exception.ChartException;
import org.eclipse.birt.chart.model.attribute.Bounds;
import org.eclipse.birt.chart.model.attribute.Fill;
import org.eclipse.birt.chart.model.attribute.LeaderLineStyle;

public class DonutSlice {

	private DataPointHints dataPoint;
	private Fill fillColor;
	private double angleExtent;
	private double startAngle;
	private int explosion;
	private Bounds bounds;
	private double width;
	private double sliceDepth;
	private double height;
	private double xc;
	private double yc;
	private double labelPositionX;
	private double frameThickness;
	double MIN_FRAMETHICKNESS = 80;

	private int quadrant;

	private LeaderLineStyle leaderLineStyle;

	public DonutSlice(double startAngle, double angleExtent, Fill fillColor,
			DataPointHints dph, double sliceDepth, double frameThickness) {

		this.setStartAngle(startAngle);
		this.setAngleExtent(angleExtent);
		this.setFillColor(fillColor);
		this.setDataPoint(dph);
		this.setSliceDepth(sliceDepth);
		this.setFrameThickness(frameThickness);
	}

	private void setSliceDepth(double depth) {
		this.sliceDepth = depth;
	}

	public double getDepth() {
		return sliceDepth;
	}

	public void setExplosion(int explosion) {
		this.explosion = explosion;
	}
	
	public double getExplosion(){
		return explosion;
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
		this.bounds = cellBounds;
		bounds.setHeight(cellBounds.getHeight());

		if (cellBounds.getHeight() < cellBounds.getWidth()) {
			// SET RADIUS
			// IF 3D HEIGHT != WIDTH
			if (sliceDepth != 0)
				setHeight(bounds.getHeight() - sliceDepth);
			else
				setHeight(bounds.getHeight());
			setWidth(getHeight());

			// SETS THE FIX POINT TO THE TOP & LEFT OF THE DONUT
			setXc(bounds.getLeft() + (bounds.getWidth() - getWidth()) / 2);
			setYc(bounds.getTop() + sliceDepth + 20);
		} else {
			// SET RADIUS
			// IF 3D HEIGHT != WIDTH
			setWidth(bounds.getWidth());
			if (sliceDepth != 0)
				setHeight(bounds.getWidth() - sliceDepth);
			else
				setHeight(bounds.getWidth());

			// SETS THE FIX POINT TO THE TOP & LEFT OF THE DONUT
			setXc(bounds.getLeft());
			setYc(bounds.getTop() + (bounds.getHeight() - getHeight()) / 2
					+ sliceDepth);
		}

		// // SETS THE FIX POINT TO THE TOP & LEFT OF THE DONUT
		// setXc(bounds.getLeft()+(bounds.getWidth()-getWidth())/2);
		// setYc(bounds.getTop() + sliceDepth);
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

	public double getMiddleAngle(){
		return (getStartAngle()+getStartAngle()+getAngleExtent())/2;
	}
	
	public void setQuadrant(int i){
		/*
		 * 2 | 1
		 * -----
		 * 3 | 4
		 */
		this.quadrant = i;
	}
	
	public int getQuadrant(){
		return quadrant;
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

	public double getLabelBound() {
		return labelPositionX;
	}

	public void setLabelBound(double labelPositionX) {
		this.labelPositionX = labelPositionX;
	}

	public <OutsideLabelBoundCache> void computeLabelBoundOutside(
			LeaderLineStyle leaderLineStyle, double leaderLinesLength,
			OutsideLabelBoundCache bbCache,double offset) throws ChartException {

		if (getQuadrant()==1 || getQuadrant()==4){
			setLabelBound(offset+2*leaderLinesLength+getWidth());
		}
		if (getQuadrant()==2 || getQuadrant()==3){
			setLabelBound(offset);
		}
		
		setLeaderLineStyle(leaderLineStyle);
	}

	private void setLeaderLineStyle(LeaderLineStyle leaderLineStyle) {
		this.leaderLineStyle = leaderLineStyle;
	}
	
	public LeaderLineStyle getLeaderLineStyle(){
		return leaderLineStyle;
	}

	public void setFrameThickness(double frameThickness) {
		if (frameThickness < 0) {
			this.frameThickness = 0;
		} else {
			this.frameThickness = frameThickness;
		}
	}

	public double getFrameThickness() {
		if (frameThickness > width/2) {
			return width/2 - MIN_FRAMETHICKNESS;
		} else {
			return frameThickness;
		}
	}
}
