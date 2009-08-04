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
import org.eclipse.birt.chart.event.PrimitiveRenderEvent;
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
import org.eclipse.birt.chart.model.data.DataSet;
import org.eclipse.birt.chart.model.data.NumberDataSet;
import org.eclipse.birt.chart.model.data.SeriesDefinition;
import org.eclipse.birt.chart.model.layout.ClientArea;
import org.eclipse.birt.chart.model.layout.Legend;
import org.eclipse.birt.chart.model.layout.Plot;
import org.eclipse.birt.chart.model.newtype.VennSeries;
import org.eclipse.birt.chart.model.newtype.data.VennDataSet;
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

		assignedSeriesDefinition = new ArrayList<double[]>();
		NumberDataSet allVennDataSets = (NumberDataSet) vennseries.getDataSet();

		bounds = getCellBounds()
				.adjustedInstance(p.getClientArea().getInsets());
		final SeriesDefinition sd = getSeriesDefinition();

		duplicateValues = new Hashtable<Double, Double>();

		// for (double d1 : assignedSeriesDefinition.get(0)) {
		// for (double d2 : assignedSeriesDefinition.get(1)) {
		// if (d1 == d2) {
		// duplicateValues.put(d1, d2);
		// }
		// }
		// }
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

		Location plotLocation = goFactory.createLocation(150, 75);
		double plotWidth = 400;
		double plotHeight = 400;

		double r1 = plotWidth / 3;
		double xm1 = plotLocation.getX() + r1;
		double ym1 = plotLocation.getY() + 2 * r1;

		double r2 = plotWidth / 3;
		double xm2 = plotLocation.getX() + 2 * r2;
		double ym2 = plotLocation.getY() + 2 * r2;

		double r3 = plotWidth / 3;
		double xm3 = (xm2 - xm1) / 2 + xm1;
		double ym3 = plotLocation.getY() + r3;

		ArcRenderEvent circle1 = (ArcRenderEvent) ((EventObjectCache) idr)
				.getEventObject(
						WrappedStructureSource.createSeries(vennseries),
						ArcRenderEvent.class);
		circle1.setOutline(LineAttributesImpl.create(ColorDefinitionImpl.BLACK(), LineStyle.SOLID_LITERAL, 3));

		circle1.setTopLeft(goFactory.createLocation(xm1 - r1, ym1 - r1));
		circle1.setWidth(2 * r1);
		circle1.setHeight(2 * r1);
		circle1.setStartAngle(0);
		circle1.setAngleExtent(360);
		circle1.setBackground(ColorDefinitionImpl.GREEN().brighter());

		ArcRenderEvent circle2 = (ArcRenderEvent) circle1.copy();

		circle2.setTopLeft(goFactory.createLocation(xm2 - r2, ym2 - r2));
		circle2.setWidth(2 * r2);
		circle2.setHeight(2 * r2);
		circle2.setStartAngle(0);
		circle2.setAngleExtent(360);
		circle2.setBackground(ColorDefinitionImpl.BLUE().darker());

		ArcRenderEvent circle3 = (ArcRenderEvent) circle1.copy();

		circle3.setTopLeft(goFactory.createLocation(xm3 - r3, ym3 - r3));
		circle3.setWidth(2 * r2);
		circle3.setHeight(2 * r2);
		circle3.setStartAngle(0);
		circle3.setAngleExtent(360);
		circle3.setBackground(ColorDefinitionImpl.RED().brighter());

		ArrayList<Location> allLocs = new ArrayList<Location>();

		computePolygonLocationsC1C2(xm1,ym1,r1,xm2,ym2,r2,allLocs);

		Location[] allPoints = (Location[]) allLocs.toArray(new Location[] {});
		PolygonRenderEvent poly1 = (PolygonRenderEvent) ((EventObjectCache) idr)
				.getEventObject(
						WrappedStructureSource.createSeries(vennseries),
						PolygonRenderEvent.class);

		poly1.setPoints(allPoints);
		poly1.setBackground(ColorDefinitionImpl.CYAN().darker());
		
		
		
		allLocs = new ArrayList<Location>();

		computePolygonLocationsC2C3(xm2, ym2, r2, xm3, ym3, r3, allLocs);

		Location[] allPoints2 = (Location[]) allLocs.toArray(new Location[] {});
		PolygonRenderEvent poly2 = (PolygonRenderEvent) poly1.copy();

		poly2.setPoints(allPoints2);
		poly2.setBackground(ColorDefinitionImpl.PINK());

		
		
		
		
		allLocs = new ArrayList<Location>();

		computePolygonLocationsC1C3(xm1,ym1,r1,xm3,ym3,r3,allLocs);

		Location[] allPoints3 = (Location[]) allLocs.toArray(new Location[] {});
		PolygonRenderEvent poly3 = (PolygonRenderEvent) poly1.copy();
		poly3.setPoints(allPoints3);
		poly3.setBackground(ColorDefinitionImpl.YELLOW());
		
		allLocs = new ArrayList<Location>();
		
		computePolygonLocationC1C2C3(xm1,ym1,r1,xm2,ym2,r2,xm3,ym3,r3,allLocs);
		Location[] allPoints4 = (Location[]) allLocs.toArray(new Location[]{});
		PolygonRenderEvent poly4 = (PolygonRenderEvent) poly1.copy();
		poly4.setPoints(allPoints4);
		poly4.setBackground(ColorDefinitionImpl.WHITE());
		
		
//		poly2.setPoints(allPoints3);
//		poly2.setBackground(ColorDefinitionImpl.PINK());

				
		RectangleRenderEvent lre1 = new RectangleRenderEvent(((EventObjectCache) idr)
				.getEventObject(WrappedStructureSource.createSeries(vennseries),
						RectangleRenderEvent.class));
		
		// CIRCLE C1 - C2
		Location[] c1c2 = computeCircleIntersectionsC1C2(xm1, xm2, ym1, ym2, r1, r2);
		lre1.setBounds(BoundsImpl.create(c1c2[0].getX(), c1c2[0].getY(), 10, 10));
		lre1.setBackground(ColorDefinitionImpl.BLACK());
		
		RectangleRenderEvent lre2 = (RectangleRenderEvent) lre1.copy();
		lre2.setBounds(BoundsImpl.create(c1c2[1].getX(), c1c2[1].getY(), 10, 10));

		
		Location[] c1c3 = computeCircleIntersectionsC1C3(xm1, xm3, ym1, ym3, r1, r3);
		RectangleRenderEvent lre3 = (RectangleRenderEvent) lre1.copy();
		lre3.setBounds(BoundsImpl.create(c1c3[0].getX(), c1c3[0].getY(), 10, 10));
		lre3.setBackground(ColorDefinitionImpl.BLACK());
		
		RectangleRenderEvent lre4 = (RectangleRenderEvent) lre1.copy();
		lre4.setBounds(BoundsImpl.create(c1c3[1].getX(), c1c3[1].getY(), 10, 10));
		
		Location[] c2c3 = computeCircleIntersectionsC2C3(xm2, xm3, ym2, ym3, r2, r3);
		RectangleRenderEvent lre5 = (RectangleRenderEvent) lre1.copy();
		lre5.setBounds(BoundsImpl.create(c2c3[0].getX(), c2c3[0].getY(), 10, 10));
		lre5.setBackground(ColorDefinitionImpl.BLACK());
		
		
		RectangleRenderEvent lre6 = (RectangleRenderEvent) lre1.copy();
		lre6.setBounds(BoundsImpl.create(c2c3[1].getX(), c2c3[1].getY(), 10, 10));
		
		System.out.println("c1c2");
		System.out.println("0x="+c1c2[0].getX());
		System.out.println("0y="+c1c2[0].getY());
		System.out.println("1x="+c1c2[1].getX());
		System.out.println("1y="+c1c2[1].getY());
		


		idr.fillArc(circle1);
		idr.fillArc(circle2);
		idr.fillArc(circle3);
		idr.fillPolygon(poly1);
		idr.fillPolygon(poly2);
		idr.fillPolygon(poly3);
		idr.fillPolygon(poly4);
//		idr.fillRectangle(lre1);
//		idr.fillRectangle(lre2);
//		idr.fillRectangle(lre3);
//		idr.fillRectangle(lre4);
//		idr.fillRectangle(lre5);
//		idr.fillRectangle(lre6);
		/*
		 * double[] m1 = new double[] { 1, 3, 5 }; double[] m2 = new double[] {
		 * 1, 4, 5 };
		 * 
		 * int duplicates = 2;
		 * 
		 * Location plotLocation = goFactory.createLocation(100, 100); double
		 * plotWidth = 200; double plotHeight = 200; double radUnit = plotWidth
		 * / (m1.length + m2.length - duplicates);
		 * 
		 * double r1 = m1.length * radUnit; double xm1 = plotLocation.getX() +
		 * r1; double ym1 = plotLocation.getY() + r1;
		 * 
		 * ArcRenderEvent circle1 = (ArcRenderEvent) ((EventObjectCache) idr)
		 * .getEventObject( WrappedStructureSource.createSeries(vennseries),
		 * ArcRenderEvent.class);
		 * 
		 * circle1.setStartAngle(0); circle1.setAngleExtent(360);
		 * circle1.setBackground(ColorDefinitionImpl.BLUE());
		 * circle1.setTopLeft(plotLocation); circle1.setWidth(2 * r1);
		 * circle1.setHeight(2 * r1);
		 * 
		 * Location circleLoc2 = circle1.getTopLeft().copyInstance();
		 * circleLoc2.translate(circle1.getWidth() - duplicates * radUnit, 0);
		 * 
		 * double r2 = m2.length * radUnit; double xm2 = circleLoc2.getX() + r2;
		 * double ym2 = circleLoc2.getY() + r2;
		 * 
		 * ArcRenderEvent circle2 = (ArcRenderEvent) circle1.copy();
		 * circle2.setBackground(ColorDefinitionImpl.ORANGE());
		 * circle2.setTopLeft(circleLoc2); circle2.setWidth(2 * r2);
		 * circle2.setHeight(2 * r2);
		 * 
		 * // computeCircleIntersections(xm1, xm2, ym1, ym2, r1, r2);
		 * computeCircleIntersections(3, 7, 3, 3, 2, 2);
		 * 
		 * double delta_mx1r1 = xm1 + r1; double delta_mx2r2 = xm2 - r2; double
		 * middleX = xm1 + r1 - (delta_mx1r1 - delta_mx2r2) / 2;
		 * 
		 * double h = Math.sqrt(Math.pow(r1, 2) - Math.pow(middleX - xm1, 2));
		 * 
		 * double middleY1 = circle1.getTopLeft().getY() + r1 - h; double
		 * middleY2 = circle1.getTopLeft().getY() + r1 + h;
		 * 
		 * LineRenderEvent middleLine = (LineRenderEvent) ((EventObjectCache)
		 * idr) .getEventObject(
		 * WrappedStructureSource.createSeries(vennseries),
		 * LineRenderEvent.class);
		 * 
		 * middleLine.setLineAttributes(LineAttributesImpl.create(
		 * ColorDefinitionImpl.BLACK(), LineStyle.SOLID_LITERAL, 2));
		 * 
		 * middleLine.setStart(goFactory.createLocation(middleX, middleY1));
		 * middleLine.setEnd(goFactory.createLocation(middleX, middleY2));
		 * 
		 * // idr.drawLine(middleLine);
		 * 
		 * double startAngle1 = -1 * Math.toDegrees(Math.asin(h / r1)); double
		 * angleExtent1 = Math.abs(2 * startAngle1);
		 * 
		 * // double startAngle1 = 0; // double angleExtent1 = 90;
		 * 
		 * ArrayList<Location> allLocs = new ArrayList<Location>();
		 * createLocationPoints(r1, xm1, ym1, startAngle1, angleExtent1,
		 * allLocs);
		 * 
		 * double startAngle2 = Math.toDegrees(Math.asin(h / -r2)); double
		 * angleExtent2 = Math.abs(2 * startAngle2);
		 * 
		 * createLocationPoints(-r2, xm2, ym2, startAngle2, angleExtent2,
		 * allLocs);
		 * 
		 * allLocs.add(allLocs.get(0));
		 * 
		 * Location[] allPoints = (Location[]) allLocs.toArray(new Location[]
		 * {}); PolygonRenderEvent poly = (PolygonRenderEvent)
		 * ((EventObjectCache) idr) .getEventObject(
		 * WrappedStructureSource.createSeries(vennseries),
		 * PolygonRenderEvent.class);
		 * 
		 * 
		 * 
		 * 
		 * 
		 * 
		 * 
		 * poly.setOutline(LineAttributesImpl.create(ColorDefinitionImpl.BLACK(),
		 * LineStyle.SOLID_LITERAL, 2));
		 * poly.setBackground(ColorDefinitionImpl.GREEN().darker());
		 * 
		 * poly.setPoints(allPoints);
		 * 
		 * ArcRenderEvent circle1Low = (ArcRenderEvent) circle1.copy();
		 * circle1Low.getTopLeft().translate(10, 10);
		 * circle1Low.setBackground(((ColorDefinitionImpl) circle1Low
		 * .getBackground()).darker());
		 * 
		 * ArcRenderEvent circle2Low = (ArcRenderEvent) circle2.copy();
		 * circle2Low.getTopLeft().translate(10, 10);
		 * circle2Low.setBackground(((ColorDefinitionImpl) circle2Low
		 * .getBackground()).darker());
		 * 
		 * PolygonRenderEvent polyLow = (PolygonRenderEvent) poly.copy(); for
		 * (Location location : polyLow.getPoints()) { location.translate(10,
		 * 10); } polyLow.setBackground(((ColorDefinitionImpl)
		 * polyLow.getBackground()) .darker());
		 * 
		 * idr.fillArc(circle1Low); idr.fillArc(circle2Low);
		 * idr.fillPolygon(polyLow);
		 * 
		 * idr.fillArc(circle1); idr.fillArc(circle2); idr.fillPolygon(poly);
		 */
	}


	private void computePolygonLocationC1C2C3(double xm1, double ym1,
			double r1, double xm2, double ym2, double r2, double xm3,
			double ym3, double r3, ArrayList<Location> allLocs) {

		Location loc0 = computeCircleIntersectionsC1C2(xm1, xm2, ym1, ym2, r1, r2)[1];
		Location loc1 = computeCircleIntersectionsC1C3(xm1, xm3, ym1, ym3, r1, r3)[0];
		Location loc2 = computeCircleIntersectionsC2C3(xm2, xm3, ym2, ym3, r1, r3)[0];
		
		
		double y00 = ym1-loc2.getY();
		double startAngle0 = Math.toDegrees(Math.asin(y00/r1));
		
		double y01 = ym1-loc0.getY();
		double endAngle0 = Math.toDegrees(Math.asin(y01/r1));
		
		double angleExtent0 = endAngle0-startAngle0;
		
		System.out.println("startAngle"+startAngle0);
		System.out.println("endAngle"+endAngle0);
		System.out.println("angleextent"+angleExtent0);
		createLocationPoints(r1, xm1, ym1, startAngle0, angleExtent0, allLocs);

		
		double y10 = ym2-loc0.getY();
		double startAngle1 = 90+Math.toDegrees(Math.acos(y10/r2));
		
		double y11 = ym2-loc1.getY();
		double endAngle1 = 180-Math.toDegrees(Math.asin(y11/r2));
		
		double angleExtent1 = endAngle1-startAngle1;
		
		createLocationPoints(r2, xm2, ym2, startAngle1, angleExtent1, allLocs);
		
		double y20 = ym3 - loc1.getY();
		double startAngle2 = 180+Math.toDegrees(Math.asin(y20/-r3));
		System.out.println("start"+startAngle2);
		double y21 = ym3 - loc2.getY();
		double endAngle2 = 360-Math.toDegrees(Math.asin(y21/-r3));
		System.out.println("end"+endAngle2);
		double angleExtent2 = endAngle2-startAngle2;
		
		createLocationPoints(r3, xm3, ym3, startAngle2, angleExtent2, allLocs);
	}

	private void computePolygonLocationsC1C3(double xm1, double ym1, double r1,
			double xm3, double ym3, double r3, ArrayList<Location> allLocs) {

		Location[] intersectionC1C3 = computeCircleIntersectionsC1C3(xm1, xm3, ym1, ym3, r1, r3);
		System.out.println("IntersectionC1C3");
		for (int i = 0; i < intersectionC1C3.length; i++) {
			System.out.println(i + "x=" + intersectionC1C3[i].getX());
			System.out.println(i + "y=" + intersectionC1C3[i].getY());
		}

		double y1 = ym1 - intersectionC1C3[0].getY();
		double startAngle1 = Math.toDegrees(Math.asin(y1 / r1));
		double dY = ym1 - intersectionC1C3[1].getY();
		double endAngle1 = 90+Math.toDegrees(Math.acos(dY / r1));
		double angleExtent1 = startAngle1 - endAngle1;
		System.out.println("startAngle:" + startAngle1);
		System.out.println("angleExtent:" + angleExtent1);

		double y2 = ym3 - intersectionC1C3[1].getY();
		double startAngle2 = 180+Math.toDegrees(Math.asin(y2 / r3));
		double dY2 = ym3 - intersectionC1C3[0].getY();
		double endAngle2 = 0-Math.toDegrees(Math.asin(dY2 / r3));
		double angleExtent2 = startAngle2 - endAngle2;
		System.out.println("startAngle:" + startAngle2);
		System.out.println("endAngle:"+endAngle2);

		
		createLocationPoints(r1, xm1, ym1, startAngle1, Math.abs(angleExtent1),
				allLocs);
		createLocationPoints(r3, xm3, ym3, startAngle2, Math.abs(angleExtent2),
				allLocs);
		
		System.out.println("Punkte"+allLocs.size());

	}

	private void computePolygonLocationsC2C3(double xm2, double ym2, double r2,
			double xm3, double ym3, double r3, ArrayList<Location> allLocs) {

		Location[] intersectionC2C3 = computeCircleIntersectionsC2C3(xm2, xm3, ym2, ym3, r2, r3);
		System.out.println("IntersectionC2C3");
		for (int i = 0; i < intersectionC2C3.length; i++) {
			System.out.println(i + "x=" + intersectionC2C3[i].getX());
			System.out.println(i + "y=" + intersectionC2C3[i].getY());
		}

		double y1 = ym3 - intersectionC2C3[0].getY();
		double startAngle1 = -180-Math.toDegrees(Math.asin(y1 / r3));
		double dY = ym3 - intersectionC2C3[1].getY();
		double endAngle1 = Math.toDegrees(Math.asin(dY / r3));
		double angleExtent1 = startAngle1 - endAngle1;
		System.out.println("startAngle:" + startAngle1);
		System.out.println("angleExtent:" + angleExtent1);

		double y2 = ym2 - intersectionC2C3[1].getY();
		double startAngle2 = Math.toDegrees(Math.asin(y2 / r2));
		double dY2 = ym2 - intersectionC2C3[0].getY();
		double endAngle2 = 180-Math.toDegrees(Math.asin(dY2 / r2));
		double angleExtent2 = startAngle2 - endAngle2;
		System.out.println("startAngle:" + startAngle2);
		System.out.println("endAngle:"+endAngle2);

		
		createLocationPoints(r3, xm3, ym3, startAngle1, Math.abs(angleExtent1),
				allLocs);
		
		System.out.println("Punkte"+allLocs.size());
		createLocationPoints(r2, xm2, ym2, startAngle2, Math.abs(angleExtent2),
				allLocs);

	}

	private void computePolygonLocationsC1C2(double xm1, double ym1, double r1,
			double xm2, double ym2, double r2, ArrayList<Location> allLocs) {
		Location[] intersectionC1C2 = computeCircleIntersectionsC1C2(xm1, xm2, ym1,
				ym2, r1, r2);
		double y1 = ym1 - intersectionC1C2[0].getY();
		double startAngle1 = Math.toDegrees(Math.asin(y1 / r1));

		double dY = ym1 - intersectionC1C2[1].getY();
		double endAngle1 = Math.toDegrees(Math.asin(dY / r1));
		double angleExtent1 = startAngle1 - endAngle1;

		double y2 = ym2 - intersectionC1C2[1].getY();
		double startAngle2 = Math.toDegrees(Math.asin(y2 / -r2));
		double dY2 = ym2 - intersectionC1C2[0].getY();
		double endAngle2 = Math.toDegrees(Math.asin(dY2 / -r2));
		double angleExtent2 = startAngle2 - endAngle2;

		createLocationPoints(r1, xm1, ym1, startAngle1, Math.abs(angleExtent1),
				allLocs);
		createLocationPoints(-r2, xm2, ym2, startAngle2,
				Math.abs(angleExtent2), allLocs);

	}

	private void createLocationPoints(double r, double xm, double ym,
			double startAngle, double angleExtent, ArrayList<Location> allLocs) {
		for (int i = 0; i < angleExtent + 1; i++) {

			double u;
			double v;

			u = xm + Math.cos(Math.toRadians(startAngle + i)) * r;
			v = ym - Math.sin(Math.toRadians(startAngle + i)) * r;

			Location loc = goFactory.createLocation(u, v);
			allLocs.add(loc);
		}
	}

	private Location[] computeCircleIntersectionsC1C2(double xm1, double xm2,
			double ym1, double ym2, double r1, double r2) {

		double a = (-2 * xm1 + 2 * xm2);
		double b = (Math.pow(ym2, 2) - Math.pow(ym1, 2) - Math.pow(r2, 2)
				+ Math.pow(r1, 2) + Math.pow(xm2, 2) - Math.pow(xm1, 2))
				/ a;
		double c = (-2 * ym2 + 2 * ym1) / a;

		double p = (2 * (c * b - c * xm1 - ym1)) / (Math.pow(c, 2) + 1);
		double q = (Math.pow(b - xm1, 2) + Math.pow(ym1, 2) - Math.pow(r1, 2))
				/ (Math.pow(c, 2) + 1);

		double y1 = -p / 2 + Math.sqrt(Math.pow(p / 2, 2) - q);
		double y2 = -p / 2 - Math.sqrt(Math.pow(p / 2, 2) - q);

		double x1 = xm1 + Math.sqrt(Math.pow(r1, 2) - Math.pow((y1 - ym1), 2));
		double x2 = xm1 + Math.sqrt(Math.pow(r1, 2) - Math.pow((y2 - ym1), 2));

		if (y1 == y2 && x1 == x2) {
			return new Location[] { goFactory.createLocation(x1, y1) };
		} else {
			return new Location[] { goFactory.createLocation(x1, y1),
					goFactory.createLocation(x2, y2) };
		}
	}
	
	private Location[] computeCircleIntersectionsC1C3(double xm1, double xm3,
			double ym1, double ym3, double r1, double r3) {
		double a = (-2 * xm1 + 2 * xm3);
		double b = (Math.pow(ym3, 2) - Math.pow(ym1, 2) - Math.pow(r3, 2)
				+ Math.pow(r1, 2) + Math.pow(xm3, 2) - Math.pow(xm1, 2))
				/ a;
		double c = (-2 * ym3 + 2 * ym1) / a;

		double p = (2 * (c * b - c * xm1 - ym1)) / (Math.pow(c, 2) + 1);
		double q = (Math.pow(b - xm1, 2) + Math.pow(ym1, 2) - Math.pow(r1, 2))
				/ (Math.pow(c, 2) + 1);

		double y1 = -p / 2 + Math.sqrt(Math.pow(p / 2, 2) - q);
		double y2 = -p / 2 - Math.sqrt(Math.pow(p / 2, 2) - q);

		double x1 = xm1 + Math.sqrt(Math.pow(r1, 2) - Math.pow((y1 - ym1), 2));
		double x2 = xm1 - Math.sqrt(Math.pow(r1, 2) - Math.pow((y2 - ym1), 2));

		if (y1 == y2 && x1 == x2) {
			return new Location[] { goFactory.createLocation(x1, y1) };
		} else {
			return new Location[] { goFactory.createLocation(x1, y1),
					goFactory.createLocation(x2, y2) };
		}
	}
	
	private Location[] computeCircleIntersectionsC2C3(double xm2, double xm3,
			double ym2, double ym3, double r1, double r3) {
		double a = (-2 * xm2 + 2 * xm3);
		double b = (Math.pow(ym3, 2) - Math.pow(ym2, 2) - Math.pow(r1, 2)
				+ Math.pow(r1, 2) + Math.pow(xm3, 2) - Math.pow(xm2, 2))
				/ a;
		double c = (-2 * ym3 + 2 * ym2) / a;

		double p = (2 * (c * b - c * xm2 - ym2)) / (Math.pow(c, 2) + 1);
		double q = (Math.pow(b - xm2, 2) + Math.pow(ym2, 2) - Math.pow(r1, 2))
				/ (Math.pow(c, 2) + 1);

		double y1 = -p / 2 + Math.sqrt(Math.pow(p / 2, 2) - q);
		double y2 = -p / 2 - Math.sqrt(Math.pow(p / 2, 2) - q);

		double x1 = xm2 - Math.sqrt(Math.pow(r1, 2) - Math.pow((y1 - ym2), 2));
		double x2 = xm2 + Math.sqrt(Math.pow(r1, 2) - Math.pow((y2 - ym2), 2));

		if (y1 == y2 && x1 == x2) {
			return new Location[] { goFactory.createLocation(x1, y1) };
		} else {
			return new Location[] { goFactory.createLocation(x1, y1),
					goFactory.createLocation(x2, y2) };
		}
	}

}
