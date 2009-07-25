package org.eclipse.birt.chart.model.newtype.render;

import java.util.ArrayList;
import java.util.Hashtable;

import org.eclipse.birt.chart.computation.DataPointHints;
import org.eclipse.birt.chart.computation.GObjectFactory;
import org.eclipse.birt.chart.computation.IGObjectFactory;
import org.eclipse.birt.chart.computation.withoutaxes.SeriesRenderingHints;
import org.eclipse.birt.chart.device.IDeviceRenderer;
import org.eclipse.birt.chart.device.IPrimitiveRenderer;
import org.eclipse.birt.chart.event.ArcRenderEvent;
import org.eclipse.birt.chart.event.EventObjectCache;
import org.eclipse.birt.chart.event.LineRenderEvent;
import org.eclipse.birt.chart.event.PolygonRenderEvent;
import org.eclipse.birt.chart.event.RectangleRenderEvent;
import org.eclipse.birt.chart.event.TextRenderEvent;
import org.eclipse.birt.chart.event.WrappedStructureSource;
import org.eclipse.birt.chart.exception.ChartException;
import org.eclipse.birt.chart.model.ChartWithoutAxes;
import org.eclipse.birt.chart.model.attribute.Bounds;
import org.eclipse.birt.chart.model.attribute.ColorDefinition;
import org.eclipse.birt.chart.model.attribute.Fill;
import org.eclipse.birt.chart.model.attribute.LineAttributes;
import org.eclipse.birt.chart.model.attribute.LineStyle;
import org.eclipse.birt.chart.model.attribute.Location;
import org.eclipse.birt.chart.model.attribute.impl.BoundsImpl;
import org.eclipse.birt.chart.model.attribute.impl.ColorDefinitionImpl;
import org.eclipse.birt.chart.model.attribute.impl.DataPointImpl;
import org.eclipse.birt.chart.model.attribute.impl.LineAttributesImpl;
import org.eclipse.birt.chart.model.component.Series;
import org.eclipse.birt.chart.model.data.SeriesDefinition;
import org.eclipse.birt.chart.model.layout.ClientArea;
import org.eclipse.birt.chart.model.layout.Legend;
import org.eclipse.birt.chart.model.layout.Plot;
import org.eclipse.birt.chart.model.newtype.VennSeries;
import org.eclipse.birt.chart.render.BaseRenderer;
import org.eclipse.birt.chart.render.ISeriesRenderingHints;

public class Venn extends BaseRenderer {

	ArrayList<double[]> assignedSeriesDefinition;
	Bounds bounds;
	private Hashtable<Double, Double> duplicateValues;
	private DataPointHints dph = null;

	protected static final IGObjectFactory goFactory = GObjectFactory
			.instance();
	private VennSeries vennseries;
	private IDeviceRenderer idr = null;

	@Override
	public void compute(Bounds bo, Plot p, ISeriesRenderingHints isrh)
			throws ChartException {

		final SeriesRenderingHints srh = (SeriesRenderingHints) isrh;

		vennseries = (VennSeries) getSeries();

		bounds = getCellBounds()
				.adjustedInstance(p.getClientArea().getInsets());
		final SeriesDefinition sd = getSeriesDefinition();

		assignedSeriesDefinition = new ArrayList<double[]>();

		for (SeriesDefinition sdtmp : sd.getSeriesDefinitions()) {
			for (Series series : sdtmp.getSeries()) {
				assignedSeriesDefinition.add((double[]) series.getDataSet()
						.getValues());
			}
		}

		duplicateValues = new Hashtable<Double, Double>();

		for (double d1 : assignedSeriesDefinition.get(0)) {
			for (double d2 : assignedSeriesDefinition.get(1)) {
				if (d1 == d2) {
					duplicateValues.put(d1, d2);
				}
			}
		}
	}

	@Override
	public void renderLegendGraphic(IPrimitiveRenderer ipr, Legend lg,
			Fill fPaletteEntry, Bounds bo) throws ChartException {
		// TODO Auto-generated method stub

	}

	@Override
	public void renderSeries(IPrimitiveRenderer ipr, Plot p,
			ISeriesRenderingHints isrh) throws ChartException {

		if (idr == null)
			idr = getDevice();

		double[] m1 = new double[] { 1, 3, 5 };
		double[] m2 = new double[] { 1, 4, 5 };

		int duplicates = 2;

		Location plotLocation = goFactory.createLocation(100, 100);
		double plotWidth = 200;
		double plotHeight = 200;
		double radUnit = plotWidth / (m1.length + m2.length - duplicates);

		double r1 = m1.length * radUnit;
		double xm1 = plotLocation.getX() + r1;
		double ym1 = plotLocation.getY() + r1;

		ArcRenderEvent circle1 = (ArcRenderEvent) ((EventObjectCache) idr)
				.getEventObject(
						WrappedStructureSource.createSeries(vennseries),
						ArcRenderEvent.class);

		circle1.setStartAngle(0);
		circle1.setAngleExtent(360);
		circle1.setBackground(ColorDefinitionImpl.BLUE());
		circle1.setTopLeft(plotLocation);
		circle1.setWidth(2 * r1);
		circle1.setHeight(2 * r1);

		Location circleLoc2 = circle1.getTopLeft().copyInstance();
		circleLoc2.translate(circle1.getWidth() - duplicates * radUnit, 0);

		double r2 = m2.length * radUnit;
		double xm2 = circleLoc2.getX() + r2;
		double ym2 = circleLoc2.getY() + r2;

		ArcRenderEvent circle2 = (ArcRenderEvent) circle1.copy();
		circle2.setBackground(ColorDefinitionImpl.ORANGE());
		circle2.setTopLeft(circleLoc2);
		circle2.setWidth(2 * r2);
		circle2.setHeight(2 * r2);

		idr.fillArc(circle1);
		idr.fillArc(circle2);

		double delta_mx1r1 = xm1 + r1;
		double delta_mx2r2 = xm2 - r2;
		double middleX = xm1 + r1 - (delta_mx1r1 - delta_mx2r2) / 2;

		double h = Math.sqrt(Math.pow(r1, 2) - Math.pow(middleX - xm1, 2));

		double middleY1 = circle1.getTopLeft().getY() + r1 - h;
		double middleY2 = circle1.getTopLeft().getY() + r1 + h;

		LineRenderEvent middleLine = (LineRenderEvent) ((EventObjectCache) idr)
				.getEventObject(
						WrappedStructureSource.createSeries(vennseries),
						LineRenderEvent.class);

		middleLine.setLineAttributes(LineAttributesImpl.create(
				ColorDefinitionImpl.BLACK(), LineStyle.SOLID_LITERAL, 2));

		middleLine.setStart(goFactory.createLocation(middleX, middleY1));
		middleLine.setEnd(goFactory.createLocation(middleX, middleY2));

//		idr.drawLine(middleLine);

		ArrayList<Location> allLocs = new ArrayList<Location>();

		double startAngle1 = -1 * Math.toDegrees(Math.asin(h / r1));
		double angleExtent1 = Math.abs(2 * startAngle1);

		// double startAngle1 = 0;
		// double angleExtent1 = 90;

		for (int i = 0; i < angleExtent1 + 1; i++) {

			double u;
			double v;

			u = xm1 + Math.cos(Math.toRadians(startAngle1 + i)) * r1;
			v = ym1 - Math.sin(Math.toRadians(startAngle1 + i)) * r1;

			Location loc = goFactory.createLocation(u, v);
			allLocs.add(loc);
		}

		 double startAngle2 = Math.toDegrees(Math.asin(h / -r2));
		 double angleExtent2 = Math.abs(2 * startAngle2);
		
		 for (int i = 0; i < angleExtent2; i++) {
		
		 double u;
		 double v;
		
			u = xm2 + Math.cos(Math.toRadians(startAngle2 + i)) * -r2;
			v = ym2 - Math.sin(Math.toRadians(startAngle2 + i)) * -r2;
		
		 Location loc = goFactory.createLocation(u, v);
		 allLocs.add(loc);
		 }

		 allLocs.add(allLocs.get(0));
		 
		Location[] allPoints = (Location[]) allLocs.toArray(new Location[] {});
		PolygonRenderEvent poly = (PolygonRenderEvent) ((EventObjectCache) idr)
				.getEventObject(
						WrappedStructureSource.createSeries(vennseries),
						PolygonRenderEvent.class);

		poly.setOutline(LineAttributesImpl.create(ColorDefinitionImpl.BLACK(),
				LineStyle.SOLID_LITERAL, 2));
		poly.setBackground(ColorDefinitionImpl.GREEN().darker());

		poly.setPoints(allPoints);

		idr.fillPolygon(poly);

	}
}
