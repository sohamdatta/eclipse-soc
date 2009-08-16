package org.eclipse.birt.chart.model.newtype.render;

import java.util.ArrayList;

import javax.jws.soap.SOAPBinding.Style;

import org.eclipse.birt.chart.device.IDeviceRenderer;
import org.eclipse.birt.chart.event.ArcRenderEvent;
import org.eclipse.birt.chart.event.EventObjectCache;
import org.eclipse.birt.chart.event.LineRenderEvent;
import org.eclipse.birt.chart.event.PolygonRenderEvent;
import org.eclipse.birt.chart.event.PrimitiveRenderEvent;
import org.eclipse.birt.chart.event.RectangleRenderEvent;
import org.eclipse.birt.chart.event.WrappedStructureSource;
import org.eclipse.birt.chart.exception.ChartException;
import org.eclipse.birt.chart.model.attribute.ColorDefinition;
import org.eclipse.birt.chart.model.attribute.Fill;
import org.eclipse.birt.chart.model.attribute.LeaderLineStyle;
import org.eclipse.birt.chart.model.attribute.LineStyle;
import org.eclipse.birt.chart.model.attribute.Location;
import org.eclipse.birt.chart.model.attribute.impl.BoundsImpl;
import org.eclipse.birt.chart.model.attribute.impl.ColorDefinitionImpl;
import org.eclipse.birt.chart.model.attribute.impl.LineAttributesImpl;
import org.eclipse.birt.chart.model.attribute.impl.LocationImpl;
import org.eclipse.birt.chart.model.component.Series;

public class Intersection {

	private Location[] intersectionsPoints;
	private Location[] locationPoints;
	private Fill backGroundColor;
	private double[][] circleInfo;

	double angleExtentOne = 0;
	double startAngleOne = 0;
	double endAngleOne = 0;

	double angleExtentTwo = 0;
	double startAngleTwo = 0;
	double endAngleTwo = 0;
	private double xmOne;
	private double ymOne;
	private double radiusOne;
	private double xmTwo;
	private double ymTwo;
	private double radiusTwo;

	public void renderIntersectionPoints(IDeviceRenderer idr, Series vennseries)
			throws ChartException {
		RectangleRenderEvent rect1 = (RectangleRenderEvent) ((EventObjectCache) idr)
				.getEventObject(
						WrappedStructureSource.createSeries(vennseries),
						RectangleRenderEvent.class);

		rect1.setBackground(ColorDefinitionImpl.BLUE());
		rect1.setBounds(BoundsImpl.create(intersectionsPoints[0].getX(),
				intersectionsPoints[0].getY(), 10, 10));

		RectangleRenderEvent rect2 = (RectangleRenderEvent) rect1.copy();
		rect2.setBounds(BoundsImpl.create(intersectionsPoints[1].getX(),
				intersectionsPoints[1].getY(), 10, 10));

		idr.fillRectangle(rect1);
		// idr.fillRectangle(rect2);
	}

	public void renderInterSection(IDeviceRenderer idr, Series vennseries)
			throws ChartException {
		if (locationPoints == null) {
			ArcRenderEvent arcOne = (ArcRenderEvent) ((EventObjectCache) idr)
					.getEventObject(WrappedStructureSource
							.createSeries(vennseries), ArcRenderEvent.class);

			angleExtentOne = endAngleOne - startAngleOne;
			angleExtentTwo = endAngleTwo - startAngleTwo;

			arcOne.setTopLeft(LocationImpl.create(this.xmOne - radiusOne,
					this.ymOne - radiusOne));
			// arcOne.setOuterRadius(this.radiusOne);
			arcOne.setStartAngle(this.startAngleOne);
			arcOne.setAngleExtent(this.angleExtentOne);
			arcOne.setStyle(ArcRenderEvent.CLOSED);
			arcOne.setOutline(LineAttributesImpl.create(
					(ColorDefinition) getBackground(), LineStyle.SOLID_LITERAL,
					2));
			arcOne.setHeight(this.radiusOne * 2);
			arcOne.setWidth(this.radiusOne * 2);
			arcOne.setBackground(this.getBackground());

			ArcRenderEvent arcTwo = (ArcRenderEvent) arcOne.copy();

			arcTwo.setTopLeft(LocationImpl.create(this.xmTwo - radiusTwo,
					this.ymTwo - radiusTwo));
			// arcTwo.setOuterRadius(this.radiusTwo);
			arcTwo.setStartAngle(this.startAngleTwo);
			arcTwo.setAngleExtent(this.angleExtentTwo);
			arcTwo.setStyle(ArcRenderEvent.CLOSED);
			arcTwo.setBackground(this.getBackground());
			arcTwo.setHeight(this.radiusTwo * 2);
			arcTwo.setWidth(this.radiusTwo * 2);

			LineRenderEvent lre = (LineRenderEvent) ((EventObjectCache) idr)
					.getEventObject(WrappedStructureSource
							.createSeries(vennseries), LineRenderEvent.class);
			lre.setStart(this.intersectionsPoints[0]);
			lre.setEnd(this.intersectionsPoints[1]);
			lre.setLineAttributes(LineAttributesImpl.create(
					(ColorDefinition) getBackground(), LineStyle.SOLID_LITERAL,
					2));

			idr.drawArc(arcOne);
			arcOne.fill(idr);
			idr.fillArc(arcTwo);
			idr.drawArc(arcTwo);
			idr.drawLine(lre);
		} else {
			PolygonRenderEvent poly = (PolygonRenderEvent) ((EventObjectCache) idr)
					.getEventObject(WrappedStructureSource
							.createSeries(vennseries), PolygonRenderEvent.class);

			poly.setBackground(getBackground());
			poly.setPoints(locationPoints);
			poly.setOutline(LineAttributesImpl.create(
					(ColorDefinition) getBackground(), LineStyle.SOLID_LITERAL,
					1));
			poly.setOutline(LineAttributesImpl.create(ColorDefinitionImpl
					.WHITE(), LineStyle.SOLID_LITERAL, 1));
			idr.fillPolygon(poly);
			idr.drawPolygon(poly);
		}

	}

	public Fill getBackground() {
		return this.backGroundColor;
	}

	public void setBackGround(Fill color) {
		this.backGroundColor = color;
	}

	public void computeLocationPoints(double xmOne, double ymOne,
			double radiusOne, double xmTwo, double ymTwo, double radiusTwo) {

		intersectionsPoints = computeCircleIntersections(xmOne, ymOne,
				radiusOne, xmTwo, ymTwo, radiusTwo);

		this.xmOne = xmOne;
		this.ymOne = ymOne;
		this.radiusOne = radiusOne;

		this.xmTwo = xmTwo;
		this.ymTwo = ymTwo;
		this.radiusTwo = radiusTwo;
		if (intersectionsPoints != null) {

			// compute location relationship between the two circles
			// circleOne is left to circleTwo

			if (xmOne <= xmTwo) {
				double dY01 = intersectionsPoints[0].getY() - ymOne;
				double dX01 = intersectionsPoints[0].getX() - xmOne;
				double dY02 = intersectionsPoints[0].getY() - ymTwo;
				double dX02 = intersectionsPoints[0].getX() - xmTwo;

				double dY11 = intersectionsPoints[1].getY() - ymOne;
				double dX11 = intersectionsPoints[1].getX() - xmOne;
				double dY12 = intersectionsPoints[1].getY() - ymTwo;
				double dX12 = intersectionsPoints[1].getX() - xmTwo;

				// Second circle is above from first circle
				if (ymOne >= ymTwo) {
					/*
					 * startAngle is alywas the lower intersection point
					 */
					if (dY01 >= dY11) {
						if (dX01 >= 0)
							startAngleOne = Math.toDegrees(Math.atan(-dY01
									/ dX01));
						else
							startAngleOne = -180
									+ Math.toDegrees(Math.atan(-dY01 / dX01));
						// Second Point is on first or fourth quader
						if (dX11 > 0)
							endAngleOne = Math.toDegrees(Math
									.atan(-dY11 / dX11));
						else
							endAngleOne = 180 + Math.toDegrees(Math.atan(-dY11
									/ dX11));

						startAngleTwo = 180 + Math.toDegrees(Math.atan(-dY12
								/ dX12));
						if (dX02 > 0)
							endAngleTwo = 360 + Math.toDegrees(Math.atan(-dY02
									/ dX02));
						else
							endAngleTwo = 180 + Math.toDegrees(Math.atan(-dY02
									/ dX02));
					} else {
						startAngleOne = Math.toDegrees(Math
								.atan((-dY11 / dX11)));

						if (dX01 > 0)
							endAngleOne = Math.toDegrees(Math
									.atan(-dY01 / dX01));
						else
							endAngleOne = 180 + Math.toDegrees(Math.atan(-dY01
									/ dX01));
						endAngleOne = Math.toDegrees(Math.atan(-dY01 / dX01));
					}
				} else {

					if (dY01 >= dY11) {
						if (dX01 <= 0)
							startAngleOne = -180
									+ Math.toDegrees(Math.atan((-dY01 / dX01)));
						else
							startAngleOne = 0 + Math.toDegrees(Math
									.atan((-dY01 / dX01)));
						endAngleOne = Math.toDegrees(Math.atan(-dY11 / dX11));

						if (dX12 <= 0)
							startAngleTwo = 180 + Math.toDegrees(Math
									.atan((-dY12 / dX12)));
						else
							startAngleTwo = Math.toDegrees(Math
									.atan((-dY12 / dX12)));
						if (dY02 >= 0)
							endAngleTwo = 180 + Math.toDegrees(Math
									.atan((-dY02 / dX02)));
					} else {
						startAngleOne = Math.toDegrees(Math
								.atan((-dY01 / dX01)));
						endAngleOne = Math.toDegrees(Math.atan(-dY11 / dX11));
					}
				}
			}
		}
		// // compute quadrant of angle
		// // startAngle is in fourth quadrant && endAngle is in first
		// // quadrant
		// // if (dYOne >= 0 && dXOne >= 0 && dYTwo <= 0 && dXTwo >= 0) {

		// this.startAngleOne =
		// this.endAngleOne = Math.toDegrees(Math.atan(-dY11 / dX11));
		// // }
		// this.angleExtentOne = endAngleOne - startAngleOne;
		//
		// System.out.println("startAngle" + startAngleOne);
		// System.out.println("endAngle" + endAngleOne);
		// System.out.println("angleextent" + angleExtentOne);
		//
		// dY01 = intersectionsPoints[1].getY() - ymTwo;
		// dX01 = intersectionsPoints[1].getX() - xmTwo;
		//
		// dY11 = intersectionsPoints[0].getY() - ymTwo;
		// dX11 = intersectionsPoints[0].getX() - xmTwo;
		//
		// this.xmTwo = xmTwo;
		// this.ymTwo = ymTwo;
		// this.radiusTwo = radiusTwo;
		// this.startAngleTwo = 180 + Math.toDegrees(Math
		// .atan((-dY01 / dX01)));
		// this.endAngleTwo = 180 + Math.toDegrees(Math.atan(-dY11 / dX11));
		// // }
		// this.angleExtentTwo = endAngleTwo - startAngleTwo;
		//
		// }
		// else{
		//				
		// }
		// ArrayList<Location> allLocs = new ArrayList<Location>();
		// createLocationPoints(radiusOne, xmOne, ymOne, startAngleOne,
		// angleExtentOne, allLocs);
		//
		// createLocationPoints(radiusTwo, xmTwo, ymTwo, startAngleTwo,
		// angleExtentTwo, allLocs);
		//
		// this.locationPoints = allLocs.toArray(new Location[] {});
		// /*
		// * Try*
		// */
		// // xm ym rad start extend
		// // this.circleInfo = new double[2][4];
		// // circleInfo[0] = new double[] { xmOne, ymOne, radiusOne,
		// // startAngleOne, angleExtent };
		//
		// // if (dYOne <= 0 && dXOne <= 0 && dYTwo >= 0 && dXTwo <= 0) {
		//
		// // circleInfo[1] = new double[] { xmTwo, ymTwo, radiusTwo,
		// // startAngleOne, angleExtent };
		// //
		// // System.out.println("startAngle" + startAngleOne);
		// // System.out.println("endAngle" + endAngleOne);
		// // System.out.println("angleextent" + angleExtent);
		// } else if (xmOne == xmTwo) {
		//
		// // CircleOne is beneath CircleTwo
		// if (ymOne > ymTwo) {
		// double dYOne = intersectionsPoints[0].getY() - ymOne;
		// double dXOne = intersectionsPoints[0].getX() - xmOne;
		//
		// double dYTwo = intersectionsPoints[1].getY() - ymOne;
		// double dXTwo = intersectionsPoints[1].getX() - xmOne;
		//
		// double startAngleOne = 0;
		// double endAngle = 0;
		// if (dYOne <= 0 && dXOne >= 0 && dYTwo <= 0 && dXTwo <= 0) {
		// startAngleOne = Math.toDegrees(Math.atan((-dYOne / dXOne)));
		// endAngle = 180 + Math.toDegrees(Math.atan(-dYTwo / dXTwo));
		// }
		// double angleExtent = endAngle - startAngleOne;
		//
		// this.circleInfo = new double[2][4];
		// circleInfo[0] = new double[] { xmOne, ymOne, radiusOne,
		// startAngleOne, angleExtent };
		// ArrayList<Location> allLocs = new ArrayList<Location>();
		// createLocationPoints(radiusOne, xmOne, ymOne, startAngleOne,
		// angleExtent, allLocs);
		//
		// dYOne = intersectionsPoints[1].getY() - ymTwo;
		// dXOne = intersectionsPoints[1].getX() - xmTwo;
		//
		// dYTwo = intersectionsPoints[0].getY() - ymTwo;
		// dXTwo = intersectionsPoints[0].getX() - xmTwo;
		//
		// if (dYOne >= 0 && dXOne <= 0 && dYTwo >= 0 && dXTwo >= 0) {
		// startAngleOne = 180 + Math.toDegrees(Math.atan(-dYOne
		// / dXOne));
		// endAngle = 360 + Math.toDegrees((-dYTwo / dXTwo));
		// }
		// angleExtent = endAngle - startAngleOne;
		// circleInfo[1] = new double[] { xmTwo, ymTwo, radiusTwo,
		// startAngleOne, angleExtent };
		// createLocationPoints(radiusTwo, xmTwo, ymTwo, startAngleOne,
		// angleExtent, allLocs);
		// this.locationPoints = allLocs.toArray(new Location[] {});
		// }
		// }
	}

	private void createLocationPoints(double radius, double xm, double ym,
			double startAngle, double angleExtent, ArrayList<Location> allLocs) {
		for (double i = 0; i < angleExtent + 1; i = i + 0.001) {

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

		if (xmOne != xmTwo) {
			return computeCircleIntersectionsPoints(xmOne, ymOne, radiusOne,
					xmTwo, ymTwo, radiusTwo);
		} else if (ymOne != ymTwo) {
			Location[] locPoints = computeCircleIntersectionsPoints(ymOne,
					xmOne, radiusOne, ymTwo, xmTwo, radiusTwo);
			swapLocation(locPoints);
			return locPoints;
		}
		return null;
	}

	private void swapLocation(Location[] locPoints) {
		for (int i = 0; i < locPoints.length; i++) {
			double x = locPoints[i].getX();
			double y = locPoints[i].getY();
			locPoints[i].set(y, x);
		}
	}

	private Location[] computeCircleIntersectionsPoints(double xmOne,
			double ymOne, double radiusOne, double xmTwo, double ymTwo,
			double radiusTwo) {
		double a = (-2 * xmOne + 2 * xmTwo);
		double b = (Math.pow(ymTwo, 2) - Math.pow(ymOne, 2)
				- Math.pow(radiusTwo, 2) + Math.pow(radiusOne, 2)
				+ Math.pow(xmTwo, 2) - Math.pow(xmOne, 2))
				/ a;
		double c = (-2 * ymTwo + 2 * ymOne) / a;

		double p1 = (2 * (c * b - c * xmOne - ymOne)) / (Math.pow(c, 2) + 1);
		double q1 = (Math.pow(b - xmOne, 2) + Math.pow(ymOne, 2) - Math.pow(
				radiusOne, 2))
				/ (Math.pow(c, 2) + 1);

		double y11 = -p1 / 2 + Math.sqrt(Math.pow(p1 / 2, 2) - q1);
		double y21 = -p1 / 2 - Math.sqrt(Math.pow(p1 / 2, 2) - q1);

		double x11 = c * y11 + b;
		double x21 = c * y21 + b;

		// double x11 = xmOne + Math.sqrt(Math.pow(radiusOne, 2) - Math.pow((y11
		// - ymOne), 2));
		// double x21 = xmOne - Math.sqrt(Math.pow(radiusOne, 2) - Math.pow((y11
		// - ymOne), 2));
		// double x31 = xmOne + Math.sqrt(Math.pow(radiusOne, 2) - Math.pow((y21
		// - ymOne), 2));
		// double x41 = xmOne - Math.sqrt(Math.pow(radiusOne, 2) - Math.pow((y21
		// - ymOne), 2));
		//
		// double p2 = (2 * (c * b - c * xmTwo - ymTwo)) / (Math.pow(c, 2) + 1);
		// double q2 = (Math.pow(b - xmTwo, 2) + Math.pow(ymTwo, 2) -
		// Math.pow(radiusTwo, 2))
		// / (Math.pow(c, 2) + 1);
		//
		// double y12 = -p2 / 2 + Math.sqrt(Math.pow(p2 / 2, 2) - q2);
		// double y22 = -p2 / 2 - Math.sqrt(Math.pow(p2 / 2, 2) - q2);
		//
		// double x12 = xmTwo + Math.sqrt(Math.pow(radiusTwo, 2) - Math.pow((y12
		// - ymTwo), 2));
		// double x22 = xmTwo + Math.sqrt(Math.pow(radiusTwo, 2) - Math.pow((y22
		// - ymTwo), 2));

		if (y11 == y21 && x11 == x21) {
			return new Location[] { LocationImpl.create(x11, y11) };
		} else {
			return new Location[] { LocationImpl.create(x11, y11),
					LocationImpl.create(x21, y21) };
		}
	}

	public void computeLocationPoints(double xmOne, double ymOne,
			double radiusOne, double xmTwo, double ymTwo, double radiusTwo,
			double xmThree, double ymThree, double radiusThree) {

		Location[] intersectionsPointsOneTwo = computeCircleIntersections(
				xmOne, ymOne, radiusOne, xmTwo, ymTwo, radiusTwo);
		Location[] intersectionsPointsOneThree = computeCircleIntersections(
				xmOne, ymOne, radiusOne, xmThree, ymThree, radiusThree);
		Location[] intersectionsPointsTwoThree = computeCircleIntersections(
				xmTwo, ymTwo, radiusTwo, xmThree, ymThree, radiusThree);

		double startAngle = 0.0;
		double endAngle = 0.0;
		double angleExtent = 0.0;

		double dY01 = intersectionsPointsOneTwo[0].getY() - ymOne;
		double dX01 = intersectionsPointsOneTwo[0].getX() - xmOne;
		double dY11 = intersectionsPointsOneTwo[1].getY() - ymOne;
		double dX11 = intersectionsPointsOneTwo[1].getX() - xmOne;

		double dY02 = intersectionsPointsOneTwo[1].getY() - ymTwo;
		double dX02 = intersectionsPointsOneTwo[1].getX() - xmTwo;
		double dY12 = intersectionsPointsOneTwo[0].getY() - ymOne;
		double dX12 = intersectionsPointsOneTwo[0].getX() - xmOne;

		double dY03 = intersectionsPointsOneTwo[1].getY() - ymTwo;
		double dX03 = intersectionsPointsOneTwo[1].getX() - xmTwo;
		double dY13 = intersectionsPointsOneTwo[0].getY() - ymOne;
		double dX13 = intersectionsPointsOneTwo[0].getX() - xmOne;

		// if (dY01 <= 0) {
		// if (dX01 >= 0) {
		// startAngle = Math.toDegrees(Math.atan(-dY01 / dX01));
		// // }
		// // }
		// // if (dY01 <= 0) {
		// // if (dX01 >= 0) {
		// endAngle = Math.toDegrees(Math.atan(-dY11 / dX11));
		// // }
		// // }
		// angleExtent = endAngle - startAngle;
		ArrayList<Location> allLocs = new ArrayList<Location>();
		// createLocationPoints(radiusOne, xmOne, ymOne, startAngle,
		// angleExtent,
		// allLocs);

		// if (dY01 <= 0) {
		// if (dX01 <= 0) {
		startAngle = Math.toDegrees(Math.atan(-(intersectionsPointsOneThree[0]
				.getY() - ymOne)
				/ (intersectionsPointsOneThree[0].getX() - xmOne)));
		endAngle = 180 + Math.toDegrees(Math
				.atan(-(intersectionsPointsOneTwo[1].getY() - ymOne)
						/ (intersectionsPointsOneTwo[1].getX() - xmOne)));
		angleExtent = endAngle - startAngle;
		createLocationPoints(radiusOne, xmOne, ymOne, startAngle, angleExtent,
				allLocs);
		//
		startAngle = 180 + Math.toDegrees(Math
				.atan(-(intersectionsPointsOneTwo[1].getY() - ymTwo)
						/ (intersectionsPointsOneTwo[1].getX() - xmTwo)));
		endAngle = 180 + Math.toDegrees(Math
				.atan(-(intersectionsPointsTwoThree[0].getY() - ymTwo)
						/ (intersectionsPointsTwoThree[0].getX() - xmTwo)));
		angleExtent = endAngle - startAngle;
		createLocationPoints(radiusTwo, xmTwo, ymTwo, startAngle, angleExtent,
				allLocs);
//
		startAngle = 180 + Math.toDegrees(Math
				.atan(-(intersectionsPointsTwoThree[0].getY() - ymThree)
						/ (intersectionsPointsTwoThree[0].getX() - xmThree)));
		endAngle =360 +Math.toDegrees(Math
				.atan(-(intersectionsPointsOneThree[0].getY() - ymThree)
						/ (intersectionsPointsOneThree[0].getX() - xmThree)));
		angleExtent = endAngle - startAngle;
		createLocationPoints(radiusThree, xmThree, ymThree, startAngle, angleExtent,
				allLocs);

		allLocs.add(allLocs.get(0));
		this.locationPoints = allLocs.toArray(new Location[] {});

	}

}
