package org.eclipse.birt.chart.model.newtype.render;

import java.awt.List;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;

import org.eclipse.birt.chart.computation.BoundingBox;
import org.eclipse.birt.chart.computation.DataPointHints;
import org.eclipse.birt.chart.computation.GObjectFactory;
import org.eclipse.birt.chart.computation.IConstants;
import org.eclipse.birt.chart.computation.IGObjectFactory;
import org.eclipse.birt.chart.computation.withoutaxes.SeriesRenderingHints;
import org.eclipse.birt.chart.device.IDeviceRenderer;
import org.eclipse.birt.chart.device.IDisplayServer;
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
import org.eclipse.birt.chart.model.attribute.LeaderLineStyle;
import org.eclipse.birt.chart.model.attribute.LineAttributes;
import org.eclipse.birt.chart.model.attribute.LineStyle;
import org.eclipse.birt.chart.model.attribute.Location;
import org.eclipse.birt.chart.model.attribute.Palette;
import org.eclipse.birt.chart.model.attribute.Position;
import org.eclipse.birt.chart.model.attribute.impl.BoundsImpl;
import org.eclipse.birt.chart.model.attribute.impl.ColorDefinitionImpl;
import org.eclipse.birt.chart.model.attribute.impl.DataPointImpl;
import org.eclipse.birt.chart.model.attribute.impl.LineAttributesImpl;
import org.eclipse.birt.chart.model.component.Label;
import org.eclipse.birt.chart.model.component.Series;
import org.eclipse.birt.chart.model.data.DataSet;
import org.eclipse.birt.chart.model.data.NumberDataSet;
import org.eclipse.birt.chart.model.data.SeriesDefinition;
import org.eclipse.birt.chart.model.layout.ClientArea;
import org.eclipse.birt.chart.model.layout.Legend;
import org.eclipse.birt.chart.model.layout.Plot;
import org.eclipse.birt.chart.model.newtype.VennSeries;
import org.eclipse.birt.chart.model.newtype.data.VennDataSet;
import org.eclipse.birt.chart.model.newtype.impl.IntersectionColorType;
import org.eclipse.birt.chart.plugin.ChartEngineExtensionPlugin;
import org.eclipse.birt.chart.render.BaseRenderer;
import org.eclipse.birt.chart.render.ISeriesRenderingHints;

public class Venn extends BaseRenderer {

	private DataPointHints dph = null;

	protected static final IGObjectFactory goFactory = GObjectFactory
			.instance();
	private VennSeries vennseries;
	private IDeviceRenderer idr = null;
	private LineAttributes leaderLineAttributes;
	private LeaderLineStyle leaderLineStyle;
	private double leaderLineLength;
	private Label titleLabel;

	private Bounds titleContainerBounds;

	private Position titlePosition;

	private IDisplayServer idserver;

	private Bounds bounds = null;

	private ArrayList<Circle> circleList = new ArrayList<Circle>();

	private Palette seriesPalette;

	private ArrayList<Intersection> interSectionList = new ArrayList<Intersection>();

	private IntersectionColorType intersectionColorType;

	private static int debugCompute = 0;
	private static int debugRenderSeries = 0;

	@Override
	public void compute(Bounds bo, Plot p, ISeriesRenderingHints isrh)
			throws ChartException {

		debugCompute++;

		vennseries = (VennSeries) getSeries();
		this.leaderLineAttributes = vennseries.getLeaderLineAttributes();
		this.leaderLineStyle = vennseries.getLeaderLineStyle();
		this.leaderLineLength = vennseries.getLeaderLineLength();
		this.titleLabel = vennseries.getTitle();
		this.titlePosition = vennseries.getTitlePosition();
		this.seriesPalette = getSeriesDefinition().getSeriesPalette();
		this.intersectionColorType = vennseries.getIntersectionColorType();

		this.idserver = getXServer();

		Bounds cellBounds = getCellBounds();
		computeTitleArea(cellBounds);
		bounds = cellBounds;

		/*
		 * For debugging reasons
		 */
		Double[] dataSetOne = new Double[] { 1.0,2.0,3.0 };
		Double[] dataSetTwo = new Double[] { 1.0,2.0,4.0 };
		Double[] dataSetThree = new Double[] { 1.0,3.0,4.0 };

		// String[] dataSetOne = new String[] { "abc", "cde", "fgh", "asdsada",
		// "dasdsadsa" };
		// String[] dataSetTwo = new String[] { "abc", "cde", "zyu" };
		// String[] dataSetThree = new String[] { "ikl", "da", "fdas" };
		circleList.add(new Circle(dataSetThree));
		circleList.add(new Circle(dataSetTwo));
		circleList.add(new Circle(dataSetOne));
		/*
		 * End debugging
		 */
		initializeCircles();
	}

	private void initializeCircles() throws ChartException {
		if (circleList.size() == 1) {
			computeCircleWithOneDataSet();
		} else if (circleList.size() == 2) {
			computeCirclesWithTwoDataSets();
		} else if (circleList.size() == 3) {
			computeCirclesWithThreeDataSets();
		}
	}

	private void computeCircleWithOneDataSet() {

		Circle circleOne = circleList.get(0);
		Object[] dataSetOne = (Object[]) circleOne.getDataSet();

		double radUnit;
		if (bounds.getHeight() > bounds.getWidth()) {
			radUnit = bounds.getWidth() / dataSetOne.length;
		} else {
			radUnit = bounds.getHeight() / dataSetOne.length;
		}
		double radius = (radUnit * (dataSetOne.length - 1)) / 2;
		double xm = bounds.getLeft() + bounds.getWidth() / 2;
		double ym = bounds.getTop() + bounds.getHeight() / 2;

		circleOne.setXm(xm);
		circleOne.setYm(ym);
		circleOne.setRad(radius);
		circleOne.setBackGroundColor((seriesPalette != null) ? seriesPalette
				.getEntries().get(0) : ColorDefinitionImpl.RED());
	}

	private void computeCirclesWithTwoDataSets() {
		Circle circleOne = circleList.get(0);
		Object[] dataSetOne = (Object[]) circleOne.getDataSet();

		Circle circleTwo = circleList.get(1);
		Object[] dataSetTwo = (Object[]) circleTwo.getDataSet();

		Object[] duplicates = computeIntersectionData(dataSetOne, dataSetTwo);

		double radiusOne = 0;
		double xmOne = 0;
		double ymOne = 0;

		double radiusTwo = 0;
		double xmTwo = 0;
		double ymTwo = 0;

		// DataSetOne is completely in DataSetTwo
		if (duplicates.length >= dataSetOne.length) {
			double radUnit;
			if (bounds.getHeight() > bounds.getWidth()) {
				radUnit = bounds.getWidth() / dataSetTwo.length;
			} else {
				radUnit = bounds.getHeight() / dataSetTwo.length;
			}

			radiusTwo = (radUnit * (dataSetTwo.length - 1)) / 2;
			xmTwo = bounds.getLeft() + bounds.getWidth() / 2;
			ymTwo = bounds.getTop() + bounds.getHeight() / 2;

			radiusOne = (radUnit * (dataSetOne.length - 1)) / 2;
			xmOne = bounds.getLeft() + bounds.getWidth() / 2
					+ (radUnit * duplicates.length / 3);
			ymOne = bounds.getTop() + bounds.getHeight() / 2;
		}
		// DataSetTwo is completely in DataSetOne
		else if (duplicates.length >= dataSetTwo.length) {
			double radUnit;
			if (bounds.getHeight() > bounds.getWidth()) {
				radUnit = bounds.getWidth() / dataSetOne.length;
			} else {
				radUnit = bounds.getHeight() / dataSetOne.length;
			}

			radiusTwo = (radUnit * (dataSetTwo.length - 1)) / 2;
			xmTwo = bounds.getLeft() + bounds.getWidth() / 2
					+ (radUnit * duplicates.length / 3);
			ymTwo = bounds.getTop() + bounds.getHeight() / 2;

			radiusOne = (radUnit * (dataSetOne.length - 1)) / 2;
			xmOne = bounds.getLeft() + bounds.getWidth() / 2;
			ymOne = bounds.getTop() + bounds.getHeight() / 2;
		}
		// If there are no intersections
		else if (duplicates.length == 0) {
			double radUnit;
			radUnit = bounds.getWidth()
					/ (dataSetOne.length + dataSetTwo.length + 1);

			radiusOne = (radUnit * dataSetOne.length) / 2;
			xmOne = bounds.getLeft() + radiusOne;
			ymOne = bounds.getTop() + bounds.getHeight() / 2;

			radiusTwo = (radUnit * dataSetTwo.length) / 2;
			xmTwo = bounds.getLeft() + 2 * radiusOne + radUnit + radiusTwo;
			ymTwo = bounds.getTop() + bounds.getHeight() / 2;
		}
		// If there are at least one intersection
		else {
			double radUnit;
			if (bounds.getHeight() > bounds.getWidth()) {
				radUnit = bounds.getWidth()
						/ (dataSetOne.length + dataSetTwo.length - duplicates.length);
			} else {
				radUnit = bounds.getHeight()
						/ (dataSetOne.length + dataSetTwo.length - duplicates.length);
			}

			radiusOne = (radUnit * dataSetOne.length) / 2;
			radiusTwo = (radUnit * dataSetTwo.length) / 2;

			double offset = (bounds.getWidth() - (2 * radiusOne + 2 * radiusTwo - duplicates.length
					* radUnit)) / 2;
			xmOne = bounds.getLeft() + radiusOne + offset;
			ymOne = (bounds.getTop() + bounds.getHeight()) / 2;

			xmTwo = bounds.getLeft() + bounds.getWidth() - offset - radiusTwo;
			ymTwo = (bounds.getTop() + bounds.getHeight()) / 2;

			Intersection intersectionOne = new Intersection();
			intersectionOne.computeLocationPoints(xmOne, ymOne, radiusOne,
					xmTwo, ymTwo, radiusTwo);

			interSectionList.add(intersectionOne);
		}
		circleOne.setBackGroundColor(this.seriesPalette.getEntries().get(0));
		circleOne.setXm(xmOne);
		circleOne.setYm(ymOne);
		circleOne.setRad(radiusOne);

		circleTwo.setXm(xmTwo);
		circleTwo.setYm(ymTwo);
		circleTwo.setRad(radiusTwo);
		circleTwo.setBackGroundColor(this.seriesPalette.getEntries().get(1));
	}

	private void computeCirclesWithThreeDataSets() throws ChartException {

		Circle circleOne = circleList.get(0);
		Object[] dataSetOne = (Object[]) circleOne.getDataSet();

		Circle circleTwo = circleList.get(1);
		Object[] dataSetTwo = (Object[]) circleTwo.getDataSet();

		Circle circleThree = circleList.get(2);
		Object[] dataSetThree = (Object[]) circleThree.getDataSet();

		Object[] duplicatesCircleOneCircleTwo = computeIntersectionData(
				dataSetOne, dataSetTwo);
		Object[] duplicatesCircleOneCircleThree = computeIntersectionData(
				dataSetOne, dataSetThree);
		Object[] duplicatesCircleTwoCircleThree = computeIntersectionData(
				dataSetTwo, dataSetThree);

		int intersectionCount = computerIntersectionCount(
				duplicatesCircleOneCircleThree, duplicatesCircleOneCircleTwo,
				duplicatesCircleTwoCircleThree);

		// There are no intersections
		if (0 == intersectionCount) {
			computeThreeCirclesWithoutIntersection(circleOne, circleTwo,
					circleThree, duplicatesCircleOneCircleTwo,
					duplicatesCircleOneCircleThree,
					duplicatesCircleTwoCircleThree);
		}
		// There are only one intersection
		if (1 <= intersectionCount && 4 > intersectionCount) {
			computeThreeCirclesWithOneIntersection(circleOne, circleTwo,
					circleThree, duplicatesCircleOneCircleTwo,
					duplicatesCircleOneCircleThree,
					duplicatesCircleTwoCircleThree, intersectionCount);
		}

		// There are only two intersection
		if (4 <= intersectionCount && 7 > intersectionCount) {
			computeThreeCirclesWithTwoIntersection(circleOne, circleTwo,
					circleThree, duplicatesCircleOneCircleTwo,
					duplicatesCircleOneCircleThree,
					duplicatesCircleTwoCircleThree, intersectionCount);
		}

		// There are three intersection
		if (7 == intersectionCount) {
			computeThreeCirclesWithThreeIntersection(circleOne, circleTwo,
					circleThree, duplicatesCircleOneCircleTwo,
					duplicatesCircleOneCircleThree,
					duplicatesCircleTwoCircleThree, intersectionCount);
		}
		circleOne.setBackGroundColor(seriesPalette.getEntries().get(0));
		circleTwo.setBackGroundColor(seriesPalette.getEntries().get(1));
		circleThree.setBackGroundColor(seriesPalette.getEntries().get(2));
	}

	private void computeThreeCirclesWithThreeIntersection(Circle circleOne,
			Circle circleTwo, Circle circleThree,
			Object[] duplicatesCircleOneCircleTwo,
			Object[] duplicatesCircleOneCircleThree,
			Object[] duplicatesCircleTwoCircleThree, int intersectionCount) {

		Object[] dataSetOne = (Object[]) circleOne.getDataSet();
		Object[] dataSetTwo = (Object[]) circleTwo.getDataSet();
		Object[] dataSetThree = (Object[]) circleThree.getDataSet();

		int maxHeight = ((dataSetOne.length+dataSetThree.length-duplicatesCircleOneCircleThree.length)
				>(dataSetTwo.length+dataSetThree.length-duplicatesCircleTwoCircleThree.length))?
						(dataSetOne.length+dataSetThree.length-duplicatesCircleOneCircleThree.length):
							(dataSetTwo.length+dataSetThree.length-duplicatesCircleTwoCircleThree.length);

		int maxWidth = ((dataSetOne.length+dataSetTwo.length-duplicatesCircleOneCircleThree.length)
				>(dataSetTwo.length+dataSetThree.length-duplicatesCircleTwoCircleThree.length))?
						(dataSetOne.length+dataSetTwo.length-duplicatesCircleOneCircleThree.length):
							(dataSetTwo.length+dataSetThree.length-duplicatesCircleTwoCircleThree.length);

		double radUnit = 0.0;
		if (bounds.getHeight() > bounds.getWidth()) {
			if (maxHeight > maxWidth) {
				radUnit = bounds.getWidth() / maxHeight;
			} else {
				radUnit = bounds.getWidth() / maxWidth;
			}
		} else {
			if (maxHeight > maxWidth) {
				radUnit = bounds.getHeight() / maxHeight;
			} else {
				radUnit = bounds.getHeight() / maxWidth;
			}
		}

		double offsetX = (bounds.getWidth()-maxWidth*radUnit)/2;
		double offsetY = (bounds.getHeight()-maxHeight*radUnit)/2;

		double radiusOne = (dataSetOne.length*radUnit)/2;
		double radiusTwo = (dataSetTwo.length*radUnit)/2;
		double radiusThree = (dataSetThree.length*radUnit)/2;

		double xmOne = bounds.getLeft()+offsetX+radiusOne;
		double ymOne = bounds.getTop()+bounds.getHeight()-offsetY-radiusOne;;

		double xmTwo = xmOne-duplicatesCircleOneCircleTwo.length*radUnit+radiusTwo+radiusOne;
		double ymTwo = ymOne-radiusOne+duplicatesCircleOneCircleTwo.length*radUnit-radiusTwo;

		double xmThree = xmOne+radiusOne-duplicatesCircleOneCircleThree.length*radUnit;
		double ymThree = ymOne-radiusOne+duplicatesCircleOneCircleThree.length*radUnit-radiusThree;

		circleOne.setXm(xmOne);
		circleOne.setYm(ymOne);
		circleOne.setRad(radiusOne);

		circleTwo.setXm(xmTwo);
		circleTwo.setYm(ymTwo);
		circleTwo.setRad(radiusTwo);

		circleThree.setXm(xmThree);
		circleThree.setYm(ymThree);
		circleThree.setRad(radiusThree);
	}

	private void computeThreeCirclesWithoutIntersection(Circle circleOne,
			Circle circleTwo, Circle circleThree,
			Object[] duplicatesCircleOneCircleTwo,
			Object[] duplicatesCircleOneCircleThree,
			Object[] duplicatesCircleTwoCircleThree) {

		Object[] dataSetOne = (Object[]) circleOne.getDataSet();
		Object[] dataSetTwo = (Object[]) circleTwo.getDataSet();
		Object[] dataSetThree = (Object[]) circleThree.getDataSet();

		double offsetX = 0;
		double offsetY = 0;

		double radiusOne = 0;
		double xmOne = 0;
		double ymOne = 0;

		double radiusTwo = 0;
		double xmTwo = 0;
		double ymTwo = 0;

		double radiusThree = 0;
		double xmThree = 0;
		double ymThree = 0;
		double radUnit = 0;

		int maxHeight = (dataSetOne.length > dataSetTwo.length) ? dataSetOne.length
				+ dataSetThree.length - duplicatesCircleOneCircleThree.length
				: dataSetTwo.length + dataSetThree.length
						- duplicatesCircleTwoCircleThree.length;

		int maxWidth = dataSetOne.length + dataSetTwo.length
				- duplicatesCircleOneCircleTwo.length;

		if (bounds.getHeight() > bounds.getWidth()) {
			if (maxHeight > maxWidth) {
				radUnit = bounds.getWidth() / maxHeight;
			} else {
				radUnit = bounds.getWidth() / maxWidth;
			}
		} else {
			if (maxHeight > maxWidth) {
				radUnit = bounds.getHeight() / maxHeight;
			} else {
				radUnit = bounds.getHeight() / maxWidth;
			}
		}

		offsetX = (bounds.getWidth() - maxWidth * radUnit) / 2;
		offsetY = (bounds.getHeight() - maxHeight * radUnit) / 2;

		radiusOne = (dataSetOne.length * radUnit) / 2;
		xmOne = offsetX + radiusOne;
		ymOne = bounds.getTop() + bounds.getHeight() - offsetY - radiusOne;

		radiusTwo = (dataSetTwo.length * radUnit) / 2;
		xmTwo = bounds.getLeft() + bounds.getWidth() - offsetX - radiusTwo;
		ymTwo = bounds.getTop() + bounds.getHeight() - offsetY - radiusTwo;

		radiusThree = (dataSetThree.length * radUnit) / 2;
		xmThree = bounds.getLeft() + bounds.getWidth() / 2;
		ymThree = bounds.getTop() + offsetY + radiusThree;

		circleOne.setXm(xmOne);
		circleOne.setYm(ymOne);
		circleOne.setRad(radiusOne);

		circleTwo.setXm(xmTwo);
		circleTwo.setYm(ymTwo);
		circleTwo.setRad(radiusTwo);

		circleThree.setXm(xmThree);
		circleThree.setYm(ymThree);
		circleThree.setRad(radiusThree);
	}

	private void computeThreeCirclesWithOneIntersection(Circle circleOne,
			Circle circleTwo, Circle circleThree,
			Object[] duplicatesCircleOneCircleTwo,
			Object[] duplicatesCircleOneCircleThree,
			Object[] duplicatesCircleTwoCircleThree, int intersectionCount)
			throws ChartException {

		Object[] dataSetOne = (Object[]) circleOne.getDataSet();
		Object[] dataSetTwo = (Object[]) circleTwo.getDataSet();
		Object[] dataSetThree = (Object[]) circleThree.getDataSet();

		double offsetX = 0;
		double offsetY = 0;

		double radiusOne = 0;
		double xmOne = 0;
		double ymOne = 0;

		double radiusTwo = 0;
		double xmTwo = 0;
		double ymTwo = 0;

		double radiusThree = 0;
		double xmThree = 0;
		double ymThree = 0;
		double radUnit = 0;

		Intersection intersection = new Intersection();

		// Intersection between circleOne and circleThree
		if (1 == intersectionCount) {
			int maxWidth = (dataSetOne.length > dataSetThree.length) ? dataSetOne.length
					+ dataSetTwo.length + 1
					: dataSetThree.length + dataSetTwo.length + 1;
			int maxHeight = ((dataSetOne.length + dataSetThree.length - duplicatesCircleOneCircleThree.length) > dataSetTwo.length) ? (dataSetOne.length
					+ dataSetThree.length - duplicatesCircleOneCircleThree.length)
					: dataSetTwo.length;
			if (bounds.getHeight() > bounds.getWidth()) {
				if (maxHeight > maxWidth) {
					radUnit = bounds.getWidth() / (maxHeight + 1);
				} else {
					radUnit = bounds.getWidth() / (maxWidth + 1);
				}
			} else {
				if (maxHeight > maxWidth) {
					radUnit = bounds.getHeight() / (maxHeight + 1);
				} else {
					radUnit = bounds.getHeight() / (maxWidth + 1);
				}
			}

			offsetX = (bounds.getWidth() - maxWidth * radUnit) / 2;
			offsetY = (bounds.getHeight() - (dataSetOne.length
					+ dataSetThree.length - duplicatesCircleOneCircleThree.length)
					* radUnit) / 2;

			radiusOne = dataSetOne.length * radUnit / 2;
			radiusTwo = dataSetTwo.length * radUnit / 2;
			radiusThree = dataSetThree.length * radUnit / 2;

			xmOne = (radiusOne > radiusThree) ? bounds.getLeft() + offsetX
					+ radiusOne : bounds.getLeft() + offsetX + radiusThree;
			xmTwo = bounds.getLeft() + bounds.getWidth() - offsetX - radiusTwo;
			xmThree = xmOne;

			ymOne = bounds.getTop() - offsetY + bounds.getHeight() - radiusOne;
			ymThree = bounds.getTop() + offsetY + radiusThree;
			ymTwo = bounds.getTop() + bounds.getHeight() / 2;

			// intersection.computeLocationPoints(xmThree, ymThree, radiusThree,
			// xmOne, ymOne, radiusOne);
			// intersection.setBackGround(createColor(
			// (ColorDefinition) seriesPalette.getEntries().get(0),
			// (ColorDefinition) seriesPalette.getEntries().get(2)));

		}
		// Intersection between circleOne and circleTwo
		if (2 == intersectionCount) {
			int maxWidth = ((dataSetOne.length + dataSetTwo.length - duplicatesCircleOneCircleTwo.length) > dataSetThree.length) ? (dataSetOne.length
					+ dataSetTwo.length - duplicatesCircleOneCircleTwo.length)
					: dataSetThree.length;
			int maxHeight = (dataSetOne.length > dataSetTwo.length) ? dataSetOne.length
					+ dataSetThree.length + 1
					: dataSetTwo.length + dataSetThree.length + 1;
			if (bounds.getHeight() > bounds.getWidth()) {
				if (maxHeight > maxWidth) {
					radUnit = bounds.getWidth() / maxHeight;
				} else {
					radUnit = bounds.getWidth() / maxWidth;
				}
			} else {
				if (maxHeight > maxWidth) {
					radUnit = bounds.getHeight() / maxHeight;
				} else {
					radUnit = bounds.getHeight() / maxWidth;
				}
			}

			offsetX = (bounds.getWidth() - (dataSetOne.length
					+ dataSetTwo.length - duplicatesCircleOneCircleTwo.length)
					* radUnit) / 2;
			offsetY = (bounds.getHeight() - maxHeight * radUnit) / 2;

			radiusOne = (dataSetOne.length * radUnit) / 2;
			radiusThree = (dataSetThree.length * radUnit) / 2;
			radiusTwo = (dataSetTwo.length * radUnit) / 2;

			xmOne = offsetX + bounds.getLeft() + radiusOne;
			xmTwo = bounds.getLeft() + bounds.getWidth() - offsetX - radiusTwo;
			xmThree = bounds.getLeft() + bounds.getWidth() / 2;

			ymOne = (radiusOne > radiusTwo) ? bounds.getTop()
					+ bounds.getHeight() - offsetY - radiusOne : bounds
					.getTop()
					+ bounds.getHeight() - offsetY - radiusTwo;
			ymTwo = ymOne;
			ymThree = bounds.getTop() + offsetY + radiusThree;

			// intersection.computeLocationPoints(xmOne, ymOne, radiusOne,
			// xmTwo,
			// ymTwo, radiusTwo);
			//
			// intersection.setBackGround(createColor(
			// (ColorDefinition) seriesPalette.getEntries().get(0),
			// (ColorDefinition) seriesPalette.getEntries().get(1)));
		}
		// Intersection between circleTwo and circleThree
		if (3 == intersectionCount) {
			int maxWidth = (dataSetTwo.length > dataSetThree.length) ? dataSetTwo.length
					+ dataSetOne.length + 1
					: dataSetThree.length + dataSetOne.length + 1;
			int maxHeight = ((dataSetTwo.length + dataSetThree.length - duplicatesCircleTwoCircleThree.length) > dataSetOne.length) ? (dataSetTwo.length
					+ dataSetThree.length - duplicatesCircleTwoCircleThree.length)
					: dataSetOne.length;
			if (bounds.getHeight() > bounds.getWidth()) {
				if (maxHeight > maxWidth) {
					radUnit = bounds.getWidth() / maxHeight;
				} else {
					radUnit = bounds.getWidth() / maxWidth;
				}
			} else {
				if (maxHeight > maxWidth) {
					radUnit = bounds.getHeight() / maxHeight;
				} else {
					radUnit = bounds.getHeight() / maxWidth;
				}
			}

			offsetX = (bounds.getWidth() - maxWidth * radUnit) / 2;
			offsetY = (bounds.getHeight() - (dataSetTwo.length
					+ dataSetThree.length - duplicatesCircleTwoCircleThree.length)
					* radUnit) / 2;

			radiusOne = dataSetOne.length * radUnit / 2;
			radiusTwo = dataSetTwo.length * radUnit / 2;
			radiusThree = dataSetThree.length * radUnit / 2;

			xmOne = bounds.getLeft() + offsetX + radiusOne;
			xmTwo = (radiusTwo > radiusThree) ? bounds.getLeft()
					+ bounds.getWidth() - offsetX - radiusTwo : bounds
					.getLeft()
					+ bounds.getWidth() - radiusThree;
			xmThree = xmTwo;

			ymOne = bounds.getTop() + bounds.getHeight() / 2;
			ymTwo = bounds.getTop() - offsetY + bounds.getHeight() - radiusTwo;
			ymThree = bounds.getTop() + offsetY + radiusThree;

			// intersection.computeLocationPoints(xmTwo, ymTwo, radiusTwo,
			// xmThree, ymThree, radiusThree);
			// intersection.setBackGround(createColor(
			// (ColorDefinition) seriesPalette.getEntries().get(0),
			// (ColorDefinition) seriesPalette.getEntries().get(2)));
		}

		circleOne.setXm(xmOne);
		circleOne.setYm(ymOne);
		circleOne.setRad(radiusOne);

		circleTwo.setXm(xmTwo);
		circleTwo.setYm(ymTwo);
		circleTwo.setRad(radiusTwo);

		circleThree.setXm(xmThree);
		circleThree.setYm(ymThree);
		circleThree.setRad(radiusThree);

		interSectionList.add(intersection);
	}

	private void computeThreeCirclesWithTwoIntersection(Circle circleOne,
			Circle circleTwo, Circle circleThree,
			Object[] duplicatesCircleOneCircleTwo,
			Object[] duplicatesCircleOneCircleThree,
			Object[] duplicatesCircleTwoCircleThree, int intersectionCount) {

		Object[] dataSetOne = (Object[]) circleOne.getDataSet();
		Object[] dataSetTwo = (Object[]) circleTwo.getDataSet();
		Object[] dataSetThree = (Object[]) circleThree.getDataSet();

		double maxWidth = dataSetOne.length + dataSetTwo.length
				+ dataSetThree.length - duplicatesCircleOneCircleThree.length
				- duplicatesCircleOneCircleTwo.length
				- duplicatesCircleTwoCircleThree.length;

		double maxHeight = (dataSetOne.length > dataSetTwo.length) ? ((dataSetOne.length > dataSetThree.length) ? dataSetOne.length
				: dataSetThree.length)
				: ((dataSetTwo.length > dataSetThree.length) ? dataSetTwo.length
						: dataSetThree.length);

		double radUnit = 0.0;

		if (bounds.getHeight() > bounds.getWidth()) {
			if (maxHeight > maxWidth) {
				radUnit = bounds.getWidth() / maxHeight;
			} else {
				radUnit = bounds.getWidth() / maxWidth;
			}
		} else {
			if (maxHeight > maxWidth) {
				radUnit = bounds.getHeight() / maxHeight;
			} else {
				radUnit = bounds.getHeight() / maxWidth;
			}
		}

		double offsetX = (bounds.getWidth() - maxWidth * radUnit) / 2;

		double radiusOne = (dataSetOne.length * radUnit) / 2;
		double radiusTwo = (dataSetTwo.length * radUnit) / 2;
		double radiusThree = (dataSetThree.length * radUnit) / 2;

		double xmOne = 0.0;

		double xmTwo = 0;

		double xmThree = 0;

		double ymOne = bounds.getTop() + bounds.getHeight() / 2;
		double ymTwo = ymOne;
		double ymThree = ymOne;

		// intersection 1+3 & 1+2
		if (4 == intersectionCount) {
			xmOne = bounds.getLeft() + offsetX + 2 * radiusThree + radiusOne
					- duplicatesCircleOneCircleThree.length * radUnit;
			xmTwo = bounds.getLeft() + bounds.getWidth() - offsetX - radiusTwo;
			xmThree = bounds.getLeft() + offsetX + radiusThree;
		}
		// intersection 1+3 & 2+3
		if (5 == intersectionCount) {
			xmOne = bounds.getLeft() + offsetX + radiusOne;
			xmTwo = bounds.getWidth() + bounds.getLeft() - offsetX - radiusTwo;
			xmThree = bounds.getLeft() + 2 * radiusOne + offsetX
					- duplicatesCircleOneCircleThree.length * radUnit
					+ radiusThree;
		}
		// intersection 1+2 & 2+3
		if (6 == intersectionCount) {
			xmOne = bounds.getLeft() + offsetX + radiusOne;
			xmTwo = bounds.getLeft() + offsetX + 2 * radiusOne
					- duplicatesCircleOneCircleTwo.length * radUnit + radiusTwo;
			xmThree = bounds.getLeft() + bounds.getWidth() - offsetX
					- radiusThree;
		}

		circleOne.setXm(xmOne);
		circleOne.setYm(ymOne);
		circleOne.setRad(radiusOne);
		circleOne.setBackGroundColor(seriesPalette.getEntries().get(0));

		circleTwo.setXm(xmTwo);
		circleTwo.setYm(ymTwo);
		circleTwo.setRad(radiusTwo);
		circleTwo.setBackGroundColor(seriesPalette.getEntries().get(1));

		circleThree.setXm(xmThree);
		circleThree.setYm(ymThree);
		circleThree.setRad(radiusThree);
		circleThree.setBackGroundColor(seriesPalette.getEntries().get(2));

	}

	private Fill createColor(ColorDefinition fill1, ColorDefinition fill2)
			throws ChartException {

		if (this.intersectionColorType == IntersectionColorType.ADDITIVE_COLOR) {
			int r = (Math.abs(fill1.getRed() + fill2.getRed()) > 255) ? 255
					: Math.abs(fill1.getRed() + fill2.getRed());
			int g = (Math.abs(fill1.getGreen() + fill2.getGreen()) > 255) ? 255
					: Math.abs(fill1.getGreen() + fill2.getGreen());
			int b = (Math.abs(fill1.getBlue() + fill2.getBlue()) > 255) ? 255
					: Math.abs(fill1.getBlue() + fill2.getBlue());
			return ColorDefinitionImpl.create(r, g, b);
		} else if (this.intersectionColorType == IntersectionColorType.ADDITIVE_COLOR) {
			int r = (Math.abs(fill1.getRed() - fill2.getRed()) < 0) ? 0 : Math
					.abs(fill1.getRed() - fill2.getRed());
			int g = (Math.abs(fill1.getGreen() - fill2.getGreen()) < 0) ? 0
					: Math.abs(fill1.getGreen() - fill2.getGreen());
			int b = (Math.abs(fill1.getBlue() - fill2.getBlue()) < 0) ? 0
					: Math.abs(fill1.getBlue() - fill2.getBlue());
			return ColorDefinitionImpl.create(r, g, b);
		} else {
			throw new ChartException(
					ChartException.UNDEFINED_VALUE,
					"intersection color type has to be a supported value of enum IntersectionColorType");
		}
	}

	private int computerIntersectionCount(
			Object[] duplicatesCircleOneCircleThree,
			Object[] duplicatesCircleOneCircleTwo,
			Object[] duplicatesCircleTwoCircleThree) {

		if (0 == duplicatesCircleOneCircleThree.length
				&& duplicatesCircleOneCircleTwo.length == 0
				&& duplicatesCircleTwoCircleThree.length == 0) {
			return 0;
		}

		else if (0 < duplicatesCircleOneCircleThree.length
				&& duplicatesCircleOneCircleTwo.length == 0
				&& duplicatesCircleTwoCircleThree.length == 0) {
			return 1;
		} else if (0 == duplicatesCircleOneCircleThree.length
				&& 0 < duplicatesCircleOneCircleTwo.length
				&& duplicatesCircleTwoCircleThree.length == 0) {
			return 2;
		} else if (0 == duplicatesCircleOneCircleThree.length
				&& duplicatesCircleOneCircleTwo.length == 0
				&& duplicatesCircleTwoCircleThree.length > 0) {
			return 3;
		}

		else if (0 < duplicatesCircleOneCircleThree.length
				&& duplicatesCircleOneCircleTwo.length > 0
				&& duplicatesCircleTwoCircleThree.length == 0) {
			return 4;
		} else if (0 < duplicatesCircleOneCircleThree.length
				&& 0 == duplicatesCircleOneCircleTwo.length
				&& duplicatesCircleTwoCircleThree.length > 0) {
			return 5;
		} else if (0 == duplicatesCircleOneCircleThree.length
				&& duplicatesCircleOneCircleTwo.length > 0
				&& duplicatesCircleTwoCircleThree.length > 0) {
			return 6;
		}

		else if (0 < duplicatesCircleOneCircleThree.length
				&& duplicatesCircleOneCircleTwo.length > 0
				&& duplicatesCircleTwoCircleThree.length > 0) {
			return 7;
		}

		else {
			return -1;
		}
	}

	private Object[] computeIntersectionData(Object[] dataSetOne,
			Object[] dataSetTwo) {

		HashSet<Object> duplicates = new HashSet<Object>();

		HashSet<Object> dataSetOneAsHash = new HashSet<Object>();
		for (Object d : dataSetOne) {
			dataSetOneAsHash.add(d);
		}
		for (Object d : dataSetTwo) {
			if (dataSetOneAsHash.contains(d)) {
				duplicates.add(d);
			}
		}
		return duplicates.toArray(new Object[] {});
	}

	private void computeTitleArea(Bounds cellBounds) throws ChartException {
		titleContainerBounds = null;
		if (titleLabel.isSetVisible()) {
			if (titlePosition == null) {
				throw new ChartException(ChartEngineExtensionPlugin.ID,
						ChartException.UNDEFINED_VALUE,
						"exception.unspecified.visible.series.title");
			}

			// Compute the bounding box ( location and size ) of a label.
			final BoundingBox bb = cComp.computeBox(idserver, IConstants.BELOW,
					titleLabel, 0, 0);

			titleContainerBounds = goFactory.createBounds(0, 0, 0, 0);

			switch (titlePosition.getValue()) {
			case Position.BELOW:
				cellBounds.setHeight(cellBounds.getHeight() - bb.getHeight());
				titleContainerBounds.set(cellBounds.getLeft(), cellBounds
						.getTop()
						+ cellBounds.getHeight(), cellBounds.getWidth(),
						cellBounds.getHeight());
				break;
			case Position.ABOVE:
				titleContainerBounds.set(cellBounds.getLeft(), cellBounds
						.getTop(), cellBounds.getWidth(), bb.getHeight());
				cellBounds.setTop(cellBounds.getTop() + bb.getHeight());
				cellBounds.setHeight(cellBounds.getHeight() - bb.getHeight());
				break;
			case Position.LEFT:
				cellBounds.setWidth(cellBounds.getWidth() - bb.getWidth());
				titleContainerBounds.set(cellBounds.getLeft(), cellBounds
						.getTop(), bb.getWidth(), cellBounds.getHeight());
				cellBounds.setLeft(cellBounds.getLeft() + bb.getWidth());
				break;
			case Position.RIGHT:
				cellBounds.setWidth(cellBounds.getWidth() - bb.getWidth());
				titleContainerBounds.set(cellBounds.getLeft()
						+ cellBounds.getWidth(), cellBounds.getTop(), bb
						.getWidth(), cellBounds.getHeight());
				break;
			default:
				throw new IllegalArgumentException(
						"exception.illegal.pie.series.title.position");
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

		debugRenderSeries++;

		if (idr == null)
			idr = getDevice();

		for (Circle circle : circleList) {
			circle.render(idr, vennseries);
		}
		// circleList.get(2).render(idr, vennseries);

		for (Intersection intersection : interSectionList) {
			// intersection.renderIntersectionPoints(idr, vennseries);
			intersection.renderInterSection(idr, vennseries);
		}

		// RectangleRenderEvent lrec1c20 = new RectangleRenderEvent(
		// ((EventObjectCache) idr).getEventObject(WrappedStructureSource
		// .createSeries(vennseries), RectangleRenderEvent.class));
		// RectangleRenderEvent plotarea = (RectangleRenderEvent)
		// lrec1c20.copy();
		// plotarea.setBounds(bounds);
		// plotarea.setBackground(ColorDefinitionImpl.BLACK());
		// idr.fillRectangle(plotarea);
		//
		// RectangleRenderEvent titleareat = (RectangleRenderEvent) lrec1c20
		// .copy();
		// titleareat.setBounds(titleContainerBounds);
		// titleareat.setBackground(ColorDefinitionImpl.BLUE());
		// idr.fillRectangle(titleareat);
		//		

		// circleList.get(1).render(idr, vennseries);
		// circleList.get(0).render(idr, vennseries);

		/*
		 * Location plotLocation = goFactory.createLocation(150, 75); double
		 * plotWidth = 400; double plotHeight = 400;
		 * 
		 * double r1 = plotWidth / 3; double xm1 = plotLocation.getX() + r1;
		 * double ym1 = plotLocation.getY() + 2 * r1;
		 * 
		 * double r2 = plotWidth / 4; double xm2 = plotLocation.getX() + 2 * r2;
		 * double ym2 = plotLocation.getY() + 2 * r2;
		 * 
		 * double r3 = plotWidth / 3; double xm3 = (xm2 - xm1) / 2 + xm1; double
		 * ym3 = plotLocation.getY() + r3;
		 * 
		 * ArcRenderEvent circle1 = (ArcRenderEvent) ((EventObjectCache) idr)
		 * .getEventObject( WrappedStructureSource.createSeries(vennseries),
		 * ArcRenderEvent.class);
		 * circle1.setOutline(LineAttributesImpl.create(ColorDefinitionImpl
		 * .BLACK(), LineStyle.SOLID_LITERAL, 3));
		 * 
		 * circle1.setTopLeft(goFactory.createLocation(xm1 - r1, ym1 - r1));
		 * circle1.setWidth(2 * r1); circle1.setHeight(2 * r1);
		 * circle1.setStartAngle(0); circle1.setAngleExtent(360);
		 * circle1.setBackground(ColorDefinitionImpl.GREEN().brighter());
		 * 
		 * ArcRenderEvent circle2 = (ArcRenderEvent) circle1.copy();
		 * 
		 * circle2.setTopLeft(goFactory.createLocation(xm2 - r2, ym2 - r2));
		 * circle2.setWidth(2 * r2); circle2.setHeight(2 * r2);
		 * circle2.setStartAngle(0); circle2.setAngleExtent(360);
		 * circle2.setBackground(ColorDefinitionImpl.BLUE().darker());
		 * 
		 * ArcRenderEvent circle3 = (ArcRenderEvent) circle1.copy();
		 * 
		 * circle3.setTopLeft(goFactory.createLocation(xm3 - r3, ym3 - r3));
		 * circle3.setWidth(2 * r2); circle3.setHeight(2 * r2);
		 * circle3.setStartAngle(0); circle3.setAngleExtent(360);
		 * circle3.setBackground(ColorDefinitionImpl.RED().brighter());
		 * 
		 * ArrayList<Location> allLocs = new ArrayList<Location>();
		 * 
		 * computePolygonLocationsC1C2(xm1, ym1, r1, xm2, ym2, r2, allLocs);
		 * 
		 * Location[] allPoints = (Location[]) allLocs.toArray(new Location[]
		 * {}); PolygonRenderEvent poly1 = (PolygonRenderEvent)
		 * ((EventObjectCache) idr) .getEventObject(
		 * WrappedStructureSource.createSeries(vennseries),
		 * PolygonRenderEvent.class);
		 * 
		 * poly1.setPoints(allPoints);
		 * poly1.setBackground(ColorDefinitionImpl.CYAN().darker());
		 * 
		 * allLocs = new ArrayList<Location>();
		 * 
		 * computePolygonLocationsC2C3(xm2, ym2, r2, xm3, ym3, r3, allLocs);
		 * 
		 * Location[] allPoints2 = (Location[]) allLocs.toArray(new Location[]
		 * {}); PolygonRenderEvent poly2 = (PolygonRenderEvent) poly1.copy();
		 * 
		 * poly2.setPoints(allPoints2);
		 * poly2.setBackground(ColorDefinitionImpl.PINK());
		 * 
		 * allLocs = new ArrayList<Location>();
		 * 
		 * computePolygonLocationsC1C3(xm1, ym1, r1, xm3, ym3, r3, allLocs);
		 * 
		 * Location[] allPoints3 = (Location[]) allLocs.toArray(new Location[]
		 * {}); PolygonRenderEvent poly3 = (PolygonRenderEvent) poly1.copy();
		 * poly3.setPoints(allPoints3);
		 * poly3.setBackground(ColorDefinitionImpl.YELLOW());
		 * 
		 * allLocs = new ArrayList<Location>();
		 * 
		 * computePolygonLocationC1C2C3(xm1, ym1, r1, xm2, ym2, r2, xm3, ym3,
		 * r3, allLocs); Location[] allPoints4 = (Location[])
		 * allLocs.toArray(new Location[] {}); PolygonRenderEvent poly4 =
		 * (PolygonRenderEvent) poly1.copy(); poly4.setPoints(allPoints4);
		 * poly4.setBackground(ColorDefinitionImpl.WHITE());
		 * 
		 * // poly2.setPoints(allPoints3); //
		 * poly2.setBackground(ColorDefinitionImpl.PINK());
		 * 
		 * RectangleRenderEvent lrec1c20 = new RectangleRenderEvent(
		 * ((EventObjectCache) idr).getEventObject(WrappedStructureSource
		 * .createSeries(vennseries), RectangleRenderEvent.class));
		 * 
		 * // CIRCLE C1 - C2 Location[] c1c2 =
		 * computeCircleIntersectionsC1C2(xm1, xm2, ym1, ym2, r1, r2);
		 * lrec1c20.setBounds(BoundsImpl.create(c1c2[0].getX(), c1c2[0].getY(),
		 * 10, 10)); lrec1c20.setBackground(ColorDefinitionImpl.BLACK());
		 * 
		 * RectangleRenderEvent lrec1c21 = (RectangleRenderEvent)
		 * lrec1c20.copy(); lrec1c21.setBounds(BoundsImpl.create(c1c2[1].getX(),
		 * c1c2[1].getY(), 10, 10));
		 * 
		 * Location[] c1c3 = computeCircleIntersectionsC1C3(xm1, xm3, ym1, ym3,
		 * r1, r3); RectangleRenderEvent lrec1c30 = (RectangleRenderEvent)
		 * lrec1c20.copy(); lrec1c30.setBounds(BoundsImpl.create(c1c3[0].getX(),
		 * c1c3[0].getY(), 10, 10));
		 * lrec1c30.setBackground(ColorDefinitionImpl.BLACK());
		 * 
		 * RectangleRenderEvent lrec1c31 = (RectangleRenderEvent)
		 * lrec1c20.copy(); lrec1c31.setBounds(BoundsImpl.create(c1c3[1].getX(),
		 * c1c3[1].getY(), 10, 10));
		 * 
		 * Location[] c2c3 = computeCircleIntersectionsC2C3(xm2, xm3, ym2, ym3,
		 * r2, r3); RectangleRenderEvent lrec2c30 = (RectangleRenderEvent)
		 * lrec1c20.copy(); lrec2c30.setBounds(BoundsImpl.create(c2c3[0].getX(),
		 * c2c3[0].getY(), 10, 10));
		 * lrec2c30.setBackground(ColorDefinitionImpl.BLACK());
		 * 
		 * RectangleRenderEvent lrec2c31 = (RectangleRenderEvent)
		 * lrec1c20.copy(); lrec2c31.setBounds(BoundsImpl.create(c2c3[1].getX(),
		 * c2c3[1].getY(), 10, 10));
		 * 
		 * System.out.println("c1c2"); System.out.println("0x=" +
		 * c1c2[0].getX()); System.out.println("0y=" + c1c2[0].getY());
		 * System.out.println("1x=" + c1c2[1].getX()); System.out.println("1y="
		 * + c1c2[1].getY());
		 */

		/*
		 * // idr.fillArc(circle1); idr.fillArc(circle2); idr.fillArc(circle3);
		 * // idr.fillPolygon(poly1); // idr.fillPolygon(poly2); //
		 * idr.fillPolygon(poly3); // idr.fillPolygon(poly4); //
		 * idr.fillRectangle(lrec1c20); // idr.fillRectangle(lrec1c21); //
		 * idr.fillRectangle(lrec1c30); // idr.fillRectangle(lrec1c31);
		 * idr.fillRectangle(lrec2c31); idr.fillRectangle(lrec2c31);
		 */
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
		 * 
		 * 
		 * 
		 * 
		 * 
		 * 
		 * 
		 * 
		 * 
		 * 
		 * 
		 * 
		 * 
		 * 
		 * 
		 * 
		 * 
		 * 
		 * 
		 * 
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

		Location loc0 = computeCircleIntersectionsC1C2(xm1, xm2, ym1, ym2, r1,
				r2)[1];
		Location loc1 = computeCircleIntersectionsC1C3(xm1, xm3, ym1, ym3, r1,
				r3)[0];
		Location loc2 = computeCircleIntersectionsC2C3(xm2, xm3, ym2, ym3, r1,
				r3)[0];

		double y00 = ym1 - loc2.getY();
		double startAngle0 = Math.toDegrees(Math.asin(y00 / r1));

		double y01 = ym1 - loc0.getY();
		double endAngle0 = Math.toDegrees(Math.asin(y01 / r1));

		double angleExtent0 = endAngle0 - startAngle0;

		System.out.println("startAngle" + startAngle0);
		System.out.println("endAngle" + endAngle0);
		System.out.println("angleextent" + angleExtent0);
		createLocationPoints(r1, xm1, ym1, startAngle0, angleExtent0, allLocs);

		double y10 = ym2 - loc0.getY();
		double startAngle1 = 90 + Math.toDegrees(Math.acos(y10 / r2));

		double y11 = ym2 - loc1.getY();
		double endAngle1 = 180 - Math.toDegrees(Math.asin(y11 / r2));

		double angleExtent1 = endAngle1 - startAngle1;

		createLocationPoints(r2, xm2, ym2, startAngle1, angleExtent1, allLocs);

		double y20 = ym3 - loc1.getY();
		double startAngle2 = 180 + Math.toDegrees(Math.asin(y20 / -r3));
		System.out.println("start" + startAngle2);
		double y21 = ym3 - loc2.getY();
		double endAngle2 = 360 - Math.toDegrees(Math.asin(y21 / -r3));
		System.out.println("end" + endAngle2);
		double angleExtent2 = endAngle2 - startAngle2;

		createLocationPoints(r3, xm3, ym3, startAngle2, angleExtent2, allLocs);
	}

	private void computePolygonLocationsC1C3(double xm1, double ym1, double r1,
			double xm3, double ym3, double r3, ArrayList<Location> allLocs) {

		Location[] intersectionC1C3 = computeCircleIntersectionsC1C3(xm1, xm3,
				ym1, ym3, r1, r3);
		System.out.println("IntersectionC1C3");
		for (int i = 0; i < intersectionC1C3.length; i++) {
			System.out.println(i + "x=" + intersectionC1C3[i].getX());
			System.out.println(i + "y=" + intersectionC1C3[i].getY());
		}

		double y1 = ym1 - intersectionC1C3[0].getY();
		double startAngle1 = Math.toDegrees(Math.asin(y1 / r1));
		double dY = ym1 - intersectionC1C3[1].getY();
		double endAngle1 = 90 + Math.toDegrees(Math.acos(dY / r1));
		double angleExtent1 = startAngle1 - endAngle1;
		System.out.println("startAngle:" + startAngle1);
		System.out.println("angleExtent:" + angleExtent1);

		double y2 = ym3 - intersectionC1C3[1].getY();
		double startAngle2 = 180 - Math.toDegrees(Math.asin(y2 / r3));
		double dY2 = ym3 - intersectionC1C3[0].getY();
		double endAngle2 = 360 + Math.toDegrees(Math.asin(dY2 / r3));
		double angleExtent2 = startAngle2 - endAngle2;
		System.out.println("startAngle:" + startAngle2);
		System.out.println("endAngle:" + endAngle2);

		createLocationPoints(r1, xm1, ym1, startAngle1, Math.abs(angleExtent1),
				allLocs);
		createLocationPoints(r3, xm3, ym3, startAngle2, Math.abs(angleExtent2),
				allLocs);

		System.out.println("Punkte" + allLocs.size());

	}

	private void computePolygonLocationsC2C3(double xm2, double ym2, double r2,
			double xm3, double ym3, double r3, ArrayList<Location> allLocs) {

		Location[] intersectionC2C3 = computeCircleIntersectionsC2C3(xm2, xm3,
				ym2, ym3, r2, r3);
		System.out.println("IntersectionC2C3");
		for (int i = 0; i < intersectionC2C3.length; i++) {
			System.out.println(i + "x=" + intersectionC2C3[i].getX());
			System.out.println(i + "y=" + intersectionC2C3[i].getY());
		}

		double y1 = ym3 - intersectionC2C3[0].getY();
		double startAngle1 = -180 - Math.toDegrees(Math.asin(y1 / r3));
		double dY = ym3 - intersectionC2C3[1].getY();
		double endAngle1 = Math.toDegrees(Math.asin(dY / r3));
		double angleExtent1 = startAngle1 - endAngle1;
		System.out.println("startAngle:" + startAngle1);
		System.out.println("angleExtent:" + angleExtent1);

		double y2 = ym2 - intersectionC2C3[1].getY();
		double startAngle2 = Math.toDegrees(Math.asin(y2 / r2));
		double dY2 = ym2 - intersectionC2C3[0].getY();
		double endAngle2 = 180 - Math.toDegrees(Math.asin(dY2 / r2));
		double angleExtent2 = startAngle2 - endAngle2;
		System.out.println("startAngle:" + startAngle2);
		System.out.println("endAngle:" + endAngle2);

		createLocationPoints(r3, xm3, ym3, startAngle1, Math.abs(angleExtent1),
				allLocs);

		System.out.println("Punkte" + allLocs.size());
		createLocationPoints(r2, xm2, ym2, startAngle2, Math.abs(angleExtent2),
				allLocs);

	}

	private void computePolygonLocationsC1C2(double xm1, double ym1, double r1,
			double xm2, double ym2, double r2, ArrayList<Location> allLocs) {
		Location[] intersectionC1C2 = computeCircleIntersectionsC1C2(xm1, xm2,
				ym1, ym2, r1, r2);
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
