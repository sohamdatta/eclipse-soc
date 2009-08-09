package org.eclipse.birt.chart.model.newtype.render;

import java.util.ArrayList;

import org.eclipse.birt.chart.device.IDeviceRenderer;
import org.eclipse.birt.chart.event.ArcRenderEvent;
import org.eclipse.birt.chart.event.EventObjectCache;
import org.eclipse.birt.chart.event.PolygonRenderEvent;
import org.eclipse.birt.chart.event.PrimitiveRenderEvent;
import org.eclipse.birt.chart.event.RectangleRenderEvent;
import org.eclipse.birt.chart.event.WrappedStructureSource;
import org.eclipse.birt.chart.exception.ChartException;
import org.eclipse.birt.chart.model.attribute.Location;
import org.eclipse.birt.chart.model.attribute.impl.BoundsImpl;
import org.eclipse.birt.chart.model.attribute.impl.ColorDefinitionImpl;
import org.eclipse.birt.chart.model.attribute.impl.LocationImpl;
import org.eclipse.birt.chart.model.component.Series;

public class Intersection {

	private Location[] intersectionsPoints;
	private Location[] locationPoints;

	public void renderIntersectionPoints(IDeviceRenderer idr, Series vennseries) throws ChartException{
		RectangleRenderEvent rect1 = (RectangleRenderEvent) ((EventObjectCache) idr)
		.getEventObject(WrappedStructureSource.createSeries(vennseries), RectangleRenderEvent.class);
		
		rect1.setBackground(ColorDefinitionImpl.BLUE());
		rect1.setBounds(BoundsImpl.create(intersectionsPoints[0].getX(), intersectionsPoints[0].getY(), 10, 10));
		
		RectangleRenderEvent rect2 = (RectangleRenderEvent) rect1.copy();
		rect2.setBounds(BoundsImpl.create(intersectionsPoints[1].getX(), intersectionsPoints[1].getY(), 10, 10));
		
		idr.fillRectangle(rect1);
//		idr.fillRectangle(rect2);
	}
	
	public void renderInterSection(IDeviceRenderer idr, Series vennseries) throws ChartException{
		PolygonRenderEvent poly = (PolygonRenderEvent) ((EventObjectCache) idr)
		.getEventObject(WrappedStructureSource.createSeries(vennseries), PolygonRenderEvent.class);
		
		poly.setBackground(ColorDefinitionImpl.GREEN().darker());
		poly.setPoints(locationPoints);
		idr.fillPolygon(poly);
		
	}
	
	public void computeLocationPoints(double xmOne, double ymOne,
			double radiusOne, double xmTwo, double ymTwo, double radiusTwo) {

		intersectionsPoints = computeCircleIntersections(xmOne,ymOne,radiusOne,xmTwo,ymTwo,radiusTwo);
		
		double y00 =  intersectionsPoints[0].getY()-ymOne;
		double startAngle0 = Math.toDegrees(Math.asin(-y00 / radiusOne));

		double y01 = ymOne - intersectionsPoints[1].getY();
		double endAngle0 = Math.toDegrees(Math.asin(y01 / radiusOne));

		double angleExtent0 = endAngle0 - startAngle0;

		System.out.println("startAngle" + startAngle0);
		System.out.println("endAngle" + endAngle0);
		System.out.println("angleextent" + angleExtent0);
		
		ArrayList<Location> allLocs = new ArrayList<Location>();
		createLocationPoints(radiusOne, xmOne, ymOne, startAngle0, angleExtent0, allLocs);

		double y10 = ymTwo - intersectionsPoints[1].getY();
		double startAngle1 = 90 + Math.toDegrees(Math.acos(y10 / radiusTwo));

		double y11 = ymTwo - intersectionsPoints[0].getY();
		double endAngle1 = 180 - Math.toDegrees(Math.asin(y11 / radiusTwo));

		double angleExtent1 = endAngle1 - startAngle1;

		createLocationPoints(radiusTwo, xmTwo, ymTwo, startAngle1, angleExtent1, allLocs);
		this.locationPoints = allLocs.toArray(new Location[]{});
	}

	private void createLocationPoints(double radius, double xm,
			double ym, double startAngle, double angleExtent,
			ArrayList<Location> allLocs) {
		for (int i = 0; i < angleExtent + 1; i++) {

			double u;
			double v;

			u = xm + Math.cos(Math.toRadians(startAngle + i)) * radius;
			v = ym - Math.sin(Math.toRadians(startAngle + i)) * radius;

			Location loc = LocationImpl.create(u, v);
			allLocs.add(loc);
		}
		
	}

	private Location[] computeCircleIntersections(double xmOne, double ymOne,
			double radiusOne, double xmTwo, double ymTwo, double radiusTwo) {

		double a = (-2 * xmOne + 2 * xmTwo);
		double b = (Math.pow(ymTwo, 2) - Math.pow(ymOne, 2) - Math.pow(radiusTwo, 2)
				+ Math.pow(radiusOne, 2) + Math.pow(xmTwo, 2) - Math.pow(xmOne, 2))
				/ a;
		double c = (-2 * ymTwo + 2 * ymOne) / a;

		double p = (2 * (c * b - c * xmOne - ymOne)) / (Math.pow(c, 2) + 1);
		double q = (Math.pow(b - xmOne, 2) + Math.pow(ymOne, 2) - Math.pow(radiusOne, 2))
				/ (Math.pow(c, 2) + 1);

		double y1 = -p / 2 + Math.sqrt(Math.pow(p / 2, 2) - q);
		double y2 = -p / 2 - Math.sqrt(Math.pow(p / 2, 2) - q);

		double x1 = xmOne + Math.sqrt(Math.pow(radiusOne, 2) - Math.pow((y1 - ymOne), 2));
		double x2 = xmOne + Math.sqrt(Math.pow(radiusOne, 2) - Math.pow((y2 - ymOne), 2));

		if (y1 == y2 && x1 == x2) {
			return new Location[] { LocationImpl.create(x1, y1) };
		} else {
			return new Location[] { LocationImpl.create(x1, y1),
					LocationImpl.create(x2, y2) };
		}
		
	}

}
