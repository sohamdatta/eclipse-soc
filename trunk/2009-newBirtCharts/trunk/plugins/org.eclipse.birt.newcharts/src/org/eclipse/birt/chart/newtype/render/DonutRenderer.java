package org.eclipse.birt.chart.newtype.render;

import org.eclipse.birt.chart.computation.DataPointHints;
import org.eclipse.birt.chart.computation.GObjectFactory;
import org.eclipse.birt.chart.computation.IGObjectFactory;
import org.eclipse.birt.chart.device.IDeviceRenderer;
import org.eclipse.birt.chart.event.ArcRenderEvent;
import org.eclipse.birt.chart.event.WrappedStructureSource;
import org.eclipse.birt.chart.exception.ChartException;
import org.eclipse.birt.chart.model.ChartWithoutAxes;
import org.eclipse.birt.chart.model.attribute.Fill;
import org.eclipse.birt.chart.model.attribute.Location;
import org.eclipse.birt.chart.model.attribute.Palette;
import org.eclipse.birt.chart.model.layout.Plot;
import org.eclipse.birt.chart.newtype.DonutSeries;

public class DonutRenderer {

	private Slice[] sliceList;
	private DonutSeries donutseries;
	private int exploration;
	private int rotation;
	private int thickness;
	private double deviceScale;
	private Palette seriesPalette;
	private Location loc;
	private double donutwidth;

	public DonutRenderer(ChartWithoutAxes cwoa, Donut donut,
			DataPointHints[] dataPoints, double[] asPrimitiveDoubleValues,
			Palette seriesPalette) {

		this.donutseries = (DonutSeries) donut.getSeries();
		this.deviceScale = donut.getDeviceScale();
		this.seriesPalette = seriesPalette;
		setDonutProperties();

		int sliceCount = dataPoints.length;
		this.sliceList = new Slice[sliceCount];
		computeSlices(asPrimitiveDoubleValues, dataPoints);

	}

	private void computeSlices(double[] primitiveDoubleValues,
			DataPointHints[] dataPoints) {
		double total = 0;
		for (int i = 0; i < primitiveDoubleValues.length; i++) {
			total += primitiveDoubleValues[i];
		}
		double lastAngle = this.rotation;
		for (int i = 0; i < dataPoints.length; i++) {
			DataPointHints dph = dataPoints[i];
			Fill fillColor = this.seriesPalette.getEntries().get(i);

			// TODO Check pixel
			double startAngle = lastAngle;
			double angleExtent = (360d / total) * primitiveDoubleValues[i];

			sliceList[i] = new Slice(startAngle, angleExtent, fillColor, dph);
			lastAngle = lastAngle + angleExtent;
		}
	}

	private void setDonutProperties() {
		this.exploration = (donutseries.isSetExplosion() ? donutseries
				.getExplosion() : 0);
		this.exploration *= this.deviceScale;
		this.rotation = (donutseries.isRotated() ? donutseries.getRotation()
				: 0);
		this.rotation *= this.deviceScale;
		this.thickness = (donutseries.getThickness() == 0
				|| donutseries.getThickness() < 0 ? 0 : donutseries
				.getThickness());
		this.thickness *= this.deviceScale;
	}

	public void render(IDeviceRenderer idr, Fill bgcolor) throws ChartException {

		for (int i = 0; i < sliceList.length; i++) {

			Slice slice = sliceList[i];
			DataPointHints dph = slice.getDataPoint();
			ArcRenderEvent coloredarc = new ArcRenderEvent(
					WrappedStructureSource.createSeriesDataPoint(donutseries,
							dph));
			Fill fPaletteEntry = slice.getFillColor();
			coloredarc.setBackground(fPaletteEntry);
			coloredarc.setStartAngle(slice.getStartAngle());
			// TODO Check pixel
			coloredarc.setAngleExtent(slice.getAngleExtent());
			coloredarc.setTopLeft(loc);
			coloredarc.setWidth(donutwidth);
			coloredarc.setHeight(donutwidth);
			coloredarc.setStyle(ArcRenderEvent.SECTOR);
			idr.fillArc(coloredarc);

			// Apply Thickness
			ArcRenderEvent negativarc = new ArcRenderEvent(
					WrappedStructureSource.createSeriesDataPoint(donutseries,
							dph));
			negativarc.setBackground(bgcolor);
			negativarc.setStartAngle(slice.getStartAngle()-5);
			negativarc.setAngleExtent(slice.getAngleExtent()+5);
			double x = loc.getX();
			double y = loc.getY();
			Location negativeLoc = loc;
			negativeLoc.set(x + thickness, y + thickness);
			negativarc.setTopLeft(negativeLoc);

			negativarc.setWidth(donutwidth - 2 * thickness);
			negativarc.setHeight(donutwidth - 2 * thickness);
			negativarc.setStyle(ArcRenderEvent.SECTOR);
			idr.fillArc(negativarc);

			
		}

	}

	public void computeFrame(Plot p) {
		// TODO RELATIVE WIDTH FOR DONUT

		double plotwidth = p.getBounds().getWidth();
		double plotheight = p.getBounds().getHeight();
		donutwidth = 300;
		double x = plotwidth - donutwidth * 0.75;
		double y = plotheight - donutwidth * 0.75;

		IGObjectFactory goFactory = GObjectFactory.instance();
		loc = goFactory.createLocation(x, y);
	}

}

class Slice {

	private DataPointHints dataPoint;
	private Fill fillColor;
	private double angleExtent;
	private double startAngle;

	public Slice(double startAngle, double angleExtent, Fill fillColor,
			DataPointHints dph) {

		this.setStartAngle(startAngle);
		this.setAngleExtent(angleExtent);
		this.setFillColor(fillColor);
		this.setDataPoint(dph);
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

}
