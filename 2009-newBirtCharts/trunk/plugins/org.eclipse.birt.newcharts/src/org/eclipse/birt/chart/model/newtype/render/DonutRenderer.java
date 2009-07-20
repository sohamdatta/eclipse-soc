package org.eclipse.birt.chart.model.newtype.render;

import java.util.ArrayList;

import javax.jws.soap.SOAPBinding.Style;

import org.eclipse.birt.chart.computation.BoundingBox;
import org.eclipse.birt.chart.computation.DataPointHints;
import org.eclipse.birt.chart.computation.GObjectFactory;
import org.eclipse.birt.chart.computation.IChartComputation;
import org.eclipse.birt.chart.computation.IConstants;
import org.eclipse.birt.chart.computation.IGObjectFactory;
import org.eclipse.birt.chart.device.IDeviceRenderer;
import org.eclipse.birt.chart.device.IDisplayServer;
import org.eclipse.birt.chart.device.IStructureDefinitionListener;
import org.eclipse.birt.chart.event.Arc3DRenderEvent;
import org.eclipse.birt.chart.event.ArcRenderEvent;
import org.eclipse.birt.chart.event.AreaRenderEvent;
import org.eclipse.birt.chart.event.ClipRenderEvent;
import org.eclipse.birt.chart.event.EventObjectCache;
import org.eclipse.birt.chart.event.LineRenderEvent;
import org.eclipse.birt.chart.event.PolygonRenderEvent;
import org.eclipse.birt.chart.event.RectangleRenderEvent;
import org.eclipse.birt.chart.event.TextRenderEvent;
import org.eclipse.birt.chart.event.WrappedStructureSource;
import org.eclipse.birt.chart.exception.ChartException;
import org.eclipse.birt.chart.factory.RunTimeContext.StateKey;
import org.eclipse.birt.chart.model.ChartWithoutAxes;
import org.eclipse.birt.chart.model.attribute.Bounds;
import org.eclipse.birt.chart.model.attribute.ChartDimension;
import org.eclipse.birt.chart.model.attribute.ColorDefinition;
import org.eclipse.birt.chart.model.attribute.Fill;
import org.eclipse.birt.chart.model.attribute.Insets;
import org.eclipse.birt.chart.model.attribute.LeaderLineStyle;
import org.eclipse.birt.chart.model.attribute.LineAttributes;
import org.eclipse.birt.chart.model.attribute.LineStyle;
import org.eclipse.birt.chart.model.attribute.Location;
import org.eclipse.birt.chart.model.attribute.Location3D;
import org.eclipse.birt.chart.model.attribute.Palette;
import org.eclipse.birt.chart.model.attribute.Position;
import org.eclipse.birt.chart.model.attribute.StyledComponent;
import org.eclipse.birt.chart.model.attribute.Text;
import org.eclipse.birt.chart.model.attribute.impl.BoundsImpl;
import org.eclipse.birt.chart.model.attribute.impl.ColorDefinitionImpl;
import org.eclipse.birt.chart.model.attribute.impl.FontDefinitionImpl;
import org.eclipse.birt.chart.model.attribute.impl.LineAttributesImpl;
import org.eclipse.birt.chart.model.attribute.impl.StyleImpl;
import org.eclipse.birt.chart.model.attribute.impl.TextAlignmentImpl;
import org.eclipse.birt.chart.model.attribute.impl.TextImpl;
import org.eclipse.birt.chart.model.component.Label;
import org.eclipse.birt.chart.model.component.impl.LabelImpl;
import org.eclipse.birt.chart.model.newtype.DonutSeries;
import org.eclipse.birt.chart.plugin.ChartEngineExtensionPlugin;
import org.eclipse.birt.chart.script.ScriptHandler;

public class DonutRenderer {

	protected static final IGObjectFactory goFactory = GObjectFactory
			.instance();

	private Donut donut;
	private DonutSlice[] sliceList;
	private DonutSeries donutseries;
	private int explosion;
	private int rotation;
	private double ratio;
	private double deviceScale;
	private Palette seriesPalette;
	private Location loc;
	private double donutwidth;
	private Position titleLabelPos;
	private Label titleLabelText;
	private LeaderLineStyle leaderLineStyle;
	private LineAttributes leaderLineAttributes;
	private double leaderLinesLength;
	private boolean isMinSliceDefined;
	private double minSliceValue;
	private boolean isPercentageMinSlice;

	/**
	 * The constant variable is used to adjust start angle of plane for getting
	 * correct rendering order of planes.
	 * <p>
	 * 
	 * Note: Since its value is very little, it will not affect computing the
	 * coordinates of pie slice.
	 */
	private final double MIN_DOUBLE = 0.0000000001d;
	private IDisplayServer idserver;
	private IChartComputation cComp;
	private Bounds titleContainerBounds;

	private Insets chartContainer;

	private Position labelPos;

	private Label labelText;

	private Bounds boundsBeforeComputation;

	private Bounds boundsAfterTitleComp;

	private boolean boundsChanged;

	private double sliceDepth;

	private double frameThickness;

	public DonutRenderer(ChartWithoutAxes cwoa, Donut donut,
			DataPointHints[] dataPoints, double[] asPrimitiveDoubleValues,
			Palette seriesPalette) throws ChartException {

		this.donut = donut;
		// CHECK THIS FUNCTIONCALL . SETS ECLIPSE IN CONFLICT MODE_NO CONTENT
		// ASSIST
		this.cComp = donut.getRunTimeContext().getState(
				StateKey.CHART_COMPUTATION_KEY);
		this.donutseries = (DonutSeries) donut.getSeries();
		this.deviceScale = donut.getDeviceScale();
		this.seriesPalette = seriesPalette;

		// DIMENSIONAL DEPTH OF RENDERING
		this.sliceDepth = ((cwoa.getDimension() == ChartDimension.TWO_DIMENSIONAL_LITERAL) ? 0
				: cwoa.getSeriesThickness())
				* donut.getDeviceScale();

		this.titleLabelPos = donutseries.getTitlePosition();
		this.titleLabelText = donutseries.getTitle();

		this.labelPos = donutseries.getLabelPosition();
		this.labelText = donutseries.getLabel();

		this.explosion = (donutseries.isSetExplosion() ? donutseries
				.getExplosion() : 0);
		this.explosion *= this.deviceScale;
		this.rotation = (donutseries.isSetRotation() ? donutseries
				.getRotation() : 0);
		this.rotation *= this.deviceScale;

		this.ratio = donutseries.getRatio();
		this.ratio *= this.deviceScale;

		// SIZE OF THE DONUTSLICE
		this.frameThickness = (donutseries.getThickness() == 0
				|| donutseries.getThickness() < 0 ? 0 : donutseries
				.getThickness());
		this.frameThickness *= this.deviceScale;

		titleLabelText.getCaption().getFont().setAlignment(
				donut.switchTextAlignment(titleLabelText.getCaption().getFont()
						.getAlignment()));

		// call script BEFORE_DRAW_SERIES_TITLE
		final ScriptHandler sh = donut.getRunTimeContext().getScriptHandler();
		ScriptHandler.callFunction(sh, ScriptHandler.BEFORE_DRAW_SERIES_TITLE,
				donutseries, titleLabelText, donut.getRunTimeContext()
						.getScriptContext());
		donut.getRunTimeContext().notifyStructureChange(
				IStructureDefinitionListener.BEFORE_DRAW_SERIES_TITLE,
				titleLabelPos);

		this.leaderLineStyle = donutseries.getLeaderLineStyle();
		this.leaderLineAttributes = donutseries.getLeaderLineAttributes();
		if (donutseries.getLeaderLineAttributes().isVisible()) {
			leaderLinesLength = donutseries.getLeaderLineLength()
					* donut.getDeviceScale();
		} else {
			leaderLinesLength = 0;
		}
		int sliceCount = dataPoints.length;
		this.sliceList = new DonutSlice[sliceCount];
		// computeSlices(asPrimitiveDoubleValues, dataPoints);

	}

	protected void initSlices(double[] primitiveDoubleValues,
			DataPointHints[] dataPoints, ChartWithoutAxes cwoa) {

		isMinSliceDefined = cwoa.isSetMinSlice();
		minSliceValue = cwoa.getMinSlice();
		isPercentageMinSlice = cwoa.isMinSlicePercent();

		double total = 0;
		for (int i = 0; i < primitiveDoubleValues.length; i++) {
			total += primitiveDoubleValues[i];
		}
		if (isMinSliceDefined) {

		} else {

			double lastAngle = this.rotation;
			for (int i = 0; i < dataPoints.length; i++) {
				DataPointHints dph = dataPoints[i];
				Fill fillColor = this.seriesPalette.getEntries().get(i);

				double startAngle = lastAngle + MIN_DOUBLE;
				double angleExtent = (360d / total)
						* Math.abs(primitiveDoubleValues[i]);

				sliceList[i] = new DonutSlice(startAngle, angleExtent,
						fillColor, dph, sliceDepth, frameThickness, ratio);

				// Check quadrants
				double angleArea = sliceList[i].getMiddleAngle();
				if (angleArea < 90) {
					sliceList[i].setQuadrant(1);
				} else if (angleArea < 180) {
					sliceList[i].setQuadrant(2);
				} else if (angleArea < 270) {
					sliceList[i].setQuadrant(3);
				} else {
					sliceList[i].setQuadrant(4);
				}
				lastAngle = lastAngle + angleExtent;
			}

		}
		initExplosion();

	}

	private void initExplosion() {

		if (explosion == 0) {
			return;
		}

		for (DonutSlice slice : sliceList) {
			try {
				donut.getRunTimeContext().getScriptHandler().registerVariable(
						ScriptHandler.BASE_VALUE,
						slice.getDataPoint().getBaseValue());
				donut.getRunTimeContext().getScriptHandler().registerVariable(
						ScriptHandler.ORTHOGONAL_VALUE,
						slice.getDataPoint().getOrthogonalValue());
				donut.getRunTimeContext().getScriptHandler().registerVariable(
						ScriptHandler.SERIES_VALUE,
						slice.getDataPoint().getSeriesValue());

				Object obj = donut.getRunTimeContext().getScriptHandler()
						.evaluate("" + (explosion != 0));

				if (obj instanceof Boolean) {
					slice.setExplosion(explosion);
				}

				donut.getRunTimeContext().getScriptHandler()
						.unregisterVariable(ScriptHandler.BASE_VALUE);
				donut.getRunTimeContext().getScriptHandler()
						.unregisterVariable(ScriptHandler.ORTHOGONAL_VALUE);
				donut.getRunTimeContext().getScriptHandler()
						.unregisterVariable(ScriptHandler.SERIES_VALUE);

			} catch (ChartException e) {
				// logger.log( e );
			}
		}
	}

	public void debugRender(IDeviceRenderer idr, Fill bgcolor)
			throws ChartException {

		for (int k = 0; k < sliceList.length; k++) {
			
		DonutSlice slice = sliceList[k];

		DataPointHints dph = slice.getDataPoint();

//		Location[] upperLocs = new Location[] {
//				goFactory.createLocation(250 + 150, 150 + 75), 
//		goFactory.createLocation(250 + 300, 150 + 75),
//		goFactory.createLocation(250 + 300, 150),
//				goFactory.createLocation(250 + 150, 150)};

		double mx = slice.getWidth()/2 + slice.getXc();
		double my = slice.getHeight()/2 + slice.getYc();
		
		int locationpoints = (int) (slice.getAngleExtent()*2); 
		
		slice.setAngleExtent(slice.getAngleExtent()-slice.getExplosion());
		
		Location[] allLocs = new Location[(int) (slice.getAngleExtent()*2)+2];
		Location[] allLocsLow = new Location[(int) (slice.getAngleExtent()*2)+2];
		
		for (int i = 0; i < (int)slice.getAngleExtent()+1; i++) {
			double x = Math.cos(Math.toRadians(slice.getStartAngle() + i)) * slice.getWidth()/2;
			double y = Math.sin(Math.toRadians(slice.getStartAngle()+ i)) * slice.getHeight()/2;
			allLocs[i] = goFactory.createLocation(slice.getXc()+slice.getWidth()/2 + x, slice.getYc()+slice.getHeight()/2 - y);
			allLocsLow[i] = goFactory.createLocation(slice.getXc()+slice.getWidth()/2 + x, slice.getYc()+sliceDepth+slice.getHeight()/2 - y);
		}
		
		for (int i = 0; i < (int)slice.getAngleExtent()+1; i++) {
			double x = Math.cos(Math.toRadians(slice.getStartAngle()+slice.getAngleExtent() - i)) * (slice.getWidth()/2-frameThickness);
			double y = Math.sin(Math.toRadians(slice.getStartAngle()+slice.getAngleExtent() - i)) * (slice.getHeight()/2-frameThickness);
			allLocs[(int)slice.getAngleExtent()+i+1] = goFactory.createLocation(slice.getXc()+slice.getWidth()/2 + x, slice.getYc()+slice.getHeight()/2 - y);
			allLocsLow[(int)slice.getAngleExtent()+i+1] = goFactory.createLocation(slice.getXc()+slice.getWidth()/2 + x, slice.getYc()+sliceDepth+slice.getHeight()/2 - y);
		}
		

		PolygonRenderEvent poly = new PolygonRenderEvent(WrappedStructureSource
				.createSeriesDataPoint(donutseries, dph));
		poly.setPoints(allLocs);
		
		poly.setBackground(slice.getFillColor());
		
		PolygonRenderEvent polyLow = (PolygonRenderEvent) poly.copy();
		polyLow.setPoints(allLocsLow);
		polyLow.setBackground(((ColorDefinition) slice.getFillColor()).darker());
//		ClipRenderEvent cre = new ClipRenderEvent(WrappedStructureSource
//				.createSeriesDataPoint(donutseries, dph));

//		cre.setVertices(upperLocs);

		
//		idr.setClip(cre);
//		idr.fillArc(coloredarc);
		idr.fillPolygon(polyLow);
		idr.fillPolygon(poly);
//		cre.reset();
//		idr.setClip(cre);
		drawLabel(slice, goFactory.createLocation(slice.getXc(), slice.getYc()), idr);
		}		
	}

	public void render(IDeviceRenderer idr, Fill bgcolor) throws ChartException {

		if (sliceDepth != 0) {
			for (int i = 0; i < sliceList.length; i++) {

				DonutSlice slice = sliceList[i];
				DataPointHints dph = slice.getDataPoint();
				ArcRenderEvent coloredarc = new ArcRenderEvent(
						WrappedStructureSource.createSeriesDataPoint(
								donutseries, dph));

				// Fill fPaletteEntry = slice.getFillColor();
				ColorDefinition color = (ColorDefinition) slice.getFillColor();
				coloredarc.setBackground(color.darker());
				coloredarc.setOutline(LineAttributesImpl
						.create(ColorDefinitionImpl.BLACK(),
								LineStyle.SOLID_LITERAL, 2));

				coloredarc.getOutline().setVisible(true);
				coloredarc.getOutline().setColor(ColorDefinitionImpl.BLACK());
				coloredarc.getOutline().setThickness(1);

				coloredarc.setStartAngle(slice.getStartAngle());
				coloredarc.setAngleExtent(slice.getAngleExtent()
						- slice.getExplosion());

				Location sliceLocation = goFactory.createLocation(
						slice.getXc(), slice.getYc() + sliceDepth);
				coloredarc.setTopLeft(sliceLocation);

				coloredarc.setWidth(slice.getWidth());
				coloredarc.setHeight(slice.getHeight());

				ClipRenderEvent cre = new ClipRenderEvent(
						WrappedStructureSource.createSeriesDataPoint(
								donutseries, dph));
				Location[] allUpperLocs = createLocationPoints(slice
						.getStartAngle(), slice.getAngleExtent(), slice
						.getWidth() / 2, slice.getHeight() / 2, 0, 0);
				Location[] allLowerLocs = createLocationPoints(
						slice.getStartAngle(),
						slice.getAngleExtent(),
						(coloredarc.getWidth() - 30 * (coloredarc.getWidth() / 100)) / 2,
						(coloredarc.getHeight() - 30 * (coloredarc.getHeight() / 100) / 2),
						0, 0);

				Location[] allLocs = new Location[allUpperLocs.length
						+ allLowerLocs.length];
				for (int j = 0; j < allUpperLocs.length; j++) {
					allLocs[j] = allUpperLocs[j];
				}
				for (int j = 0; j < allLowerLocs.length; j++) {
					allLocs[j + allUpperLocs.length] = allLowerLocs[j];
				}

				cre.setVertices(allLocs);

				// idr.drawArc(coloredarc);
				coloredarc.setStyle(ArcRenderEvent.SECTOR);
				idr.fillArc(coloredarc);
				idr.setClip(cre);

				cre.reset();

				// backgroundArc.setStyle(ArcRenderEvent.SECTOR);
				// idr.fillArc(backgroundArc);

				// ArcRenderEvent backgroundArc = (ArcRenderEvent) coloredarc
				// .copy();
				// Location sliceLocationInner = goFactory.createLocation(slice
				// .getXc()
				// + 15 * (coloredarc.getWidth() / 100), slice.getYc()
				// + sliceDepth + 15 * (coloredarc.getHeight() / 100));
				// backgroundArc.setTopLeft(sliceLocationInner);
				// backgroundArc.setWidth(coloredarc.getWidth() - 30
				// * (coloredarc.getWidth() / 100));
				// backgroundArc.setHeight(coloredarc.getHeight() - 30
				// * (coloredarc.getHeight() / 100));
				// backgroundArc.setStartAngle(coloredarc.getStartAngle() - 5);
				// backgroundArc.setAngleExtent(coloredarc.getAngleExtent() +
				// 10);
				// backgroundArc.setBackground(bgcolor);
				//
				// Location[] allLocs = createLocationPoints(
				// slice.getStartAngle(), slice.getAngleExtent(), slice
				// .getWidth() / 2, slice.getHeight() / 2);
				// drawLabel(slice, sliceLocation, idr);
			}
		}
		for (int i = 0; i < sliceList.length; i++) {

			DonutSlice slice = sliceList[i];
			DataPointHints dph = slice.getDataPoint();
			ArcRenderEvent coloredarc = new ArcRenderEvent(
					WrappedStructureSource.createSeriesDataPoint(donutseries,
							dph));

			ColorDefinition color = (ColorDefinition) slice.getFillColor();
			coloredarc.setBackground(color);
			coloredarc.setOutline(LineAttributesImpl.create(ColorDefinitionImpl
					.BLACK(), LineStyle.SOLID_LITERAL, 2));

			coloredarc.getOutline().setVisible(true);
			coloredarc.getOutline().setColor(ColorDefinitionImpl.BLACK());
			coloredarc.getOutline().setThickness(1);

			coloredarc.setStartAngle(slice.getStartAngle());
			coloredarc.setAngleExtent(slice.getAngleExtent()
					- slice.getExplosion());

			Location sliceLocation = goFactory.createLocation(slice.getXc(),
					slice.getYc());
			coloredarc.setTopLeft(sliceLocation);
			coloredarc.setWidth(slice.getWidth());
			coloredarc.setHeight(slice.getHeight());

			ArcRenderEvent backgroundArc = (ArcRenderEvent) coloredarc.copy();
			Location sliceLocationInner = goFactory.createLocation(slice
					.getXc()
					+ 15 * (coloredarc.getWidth() / 100), slice.getYc() + 15
					* (coloredarc.getHeight() / 100));
			backgroundArc.setTopLeft(sliceLocationInner);
			backgroundArc.setWidth(coloredarc.getWidth() - 30
					* (coloredarc.getWidth() / 100));
			backgroundArc.setHeight(coloredarc.getHeight() - 30
					* (coloredarc.getHeight() / 100));
			backgroundArc.setStartAngle(coloredarc.getStartAngle() - 5);
			backgroundArc.setAngleExtent(coloredarc.getAngleExtent() + 10);
			backgroundArc.setBackground(bgcolor);

			ClipRenderEvent cre = new ClipRenderEvent(WrappedStructureSource
					.createSeriesDataPoint(donutseries, dph));

			int points = 100;
			Location[] allLocs = new Location[points];

			for (int j = 0; j < points; j++) {

				if (slice.getAngleExtent() < points) {
					double a = slice.getWidth() / 2;
					double b = slice.getHeight() / 2;

					double x = Math.cos(Math.toRadians(slice.getStartAngle()
							+ i))
							* a;
					double y = Math.sin(Math.toRadians(slice.getStartAngle()
							+ i))
							* b;

					allLocs[i] = goFactory.createLocation(x, y);
				}

			}

			// Location[] abc = new Location[4];
			// abc[0] = goFactory.createLocation(slice.getXc(),slice.getYc());
			// abc[1] =
			// goFactory.createLocation(slice.getXc()+200,slice.getYc());
			// abc[2] =
			// goFactory.createLocation(slice.getXc()+200,slice.getYc()+200);
			// abc[3] =
			// goFactory.createLocation(slice.getXc(),slice.getYc()+200);
			// cre.setVertices(allLocs);

			// idr.fillArc(coloredarc);
			// cre.fill(idr);
			// idr.fillArc(backgroundArc);
			// ArcRenderEvent backgroundArc2 = (ArcRenderEvent)
			// coloredarc.copy();
			// Location sliceLocationInner2 = goFactory.createLocation(slice
			// .getXc()
			// + frameThickness + sliceDepth, slice.getYc()
			// + frameThickness + sliceDepth);
			// backgroundArc2.setTopLeft(sliceLocationInner2);
			// backgroundArc2.setWidth(coloredarc.getWidth() - 2 *
			// (frameThickness+sliceDepth));
			// backgroundArc2
			// .setHeight(coloredarc.getHeight() - 2 *
			// (frameThickness+sliceDepth));
			// backgroundArc2.setStartAngle(coloredarc.getStartAngle() - 4);
			// backgroundArc2.setAngleExtent(coloredarc.getAngleExtent() + 8);
			// backgroundArc2.setBackground(bgcolor);

			// idr.fillArc(backgroundArc2);
			// drawLabel(slice, sliceLocation, idr);
		}

		// DonutSlice slice = sliceList[0];
		// DataPointHints dph = slice.getDataPoint();
		//		 
		// LineRenderEvent lre = new LineRenderEvent(null);
		// lre.
		// .createSeriesDataPoint(donutseries, dph));
		// Fill fPaletteEntry = slice.getFillColor();
		// coloredarc.setBackground(fPaletteEntry);
		// coloredarc.setOutline(LineAttributesImpl.create(ColorDefinitionImpl
		// .BLACK(), LineStyle.SOLID_LITERAL, 1));
		//		
		// coloredarc.getOutline().setVisible(true);
		// coloredarc.getOutline().setColor(ColorDefinitionImpl.BLACK());
		// coloredarc.getOutline().setThickness(1);
		//		
		// coloredarc.setStartAngle(50);
		// coloredarc.setAngleExtent(90);
		//		
		// Location sliceLocation = goFactory.createLocation(200, 100);
		// coloredarc.setTopLeft(sliceLocation);
		//		
		// coloredarc.setWidth(400);
		// coloredarc.setHeight(200);
		//		
		// coloredarc.setBackground(slice.getFillColor());
		//		
		// ArcRenderEvent depthArc = (ArcRenderEvent) coloredarc.copy();
		// depthArc.getTopLeft().translate(0, 25);
		//		
		// depthArc.setBackground(ColorDefinitionImpl.GREY());
		// depthArc.setWidth(400);
		// depthArc.setHeight(200);
		//
		//		 
		// idr.fillArc(coloredarc);
		//		 
	}

	private Location[] createLocationPoints(double startAngle,
			double angleExtent, double a, double b, double mx, double my) {

		Location[] allLocs = new Location[(int) angleExtent + 1];
		for (int i = 0; i < (int) angleExtent; i++) {
			double x = Math.cos(Math.toRadians(startAngle + i)) * a;
			double y = Math.sin(Math.toRadians(startAngle + i)) * b;

			allLocs[i] = goFactory.createLocation(mx + x, my + y);
		}
		allLocs[allLocs.length - 1] = goFactory.createLocation(mx, my);

		return allLocs;
	}

	private void drawLabel(DonutSlice slice, Location sliceLocation,
			IDeviceRenderer idr) throws ChartException {

		DataPointHints dph = slice.getDataPoint();

		double tickLength = 20.0;
		double mx = sliceLocation.getX() + slice.getWidth() / 2;
		double my = sliceLocation.getY() + slice.getHeight() / 2;
		double a = slice.getWidth() / 2;
		double b = (slice.getHeight()) / 2;

		double u = Math.cos(Math.toRadians(slice.getMiddleAngle())) * a;
		double v = Math.sin(Math.toRadians(slice.getMiddleAngle())) * b;

		Location loc1 = goFactory.createLocation(mx + u, my - v);
		double deltaY = my - loc1.getY();
		double deltaX = loc1.getX() - mx;
		double m = deltaY / deltaX;

		Location loc3 = goFactory.createLocation(0, 0);
		double alpha = Math.atan(m);

		double loc3x;
		double loc3y;
		if (slice.getQuadrant() == 1 || slice.getQuadrant() == 4) {
			loc3x = loc1.getX() + Math.cos(alpha) * tickLength;
			loc3y = loc1.getY() - Math.sin(alpha) * tickLength;
		} else {
			loc3x = loc1.getX() - Math.cos(alpha) * tickLength;
			loc3y = loc1.getY() + Math.sin(alpha) * tickLength;
		}
		loc3.setX(loc3x);
		loc3.setY(loc3y);

		LineRenderEvent debug = new LineRenderEvent(WrappedStructureSource
				.createSeriesDataPoint(donutseries, dph));
		debug.setStart(loc1);

		debug.setEnd(loc3);
		debug.setLineAttributes(leaderLineAttributes);
		idr.drawLine(debug);

		renderSliceLabel(idr, loc3, slice);

	}

	private void drawDebugLine(IDeviceRenderer idr, Location loc1, double mx,
			double my, DataPointHints dph, Fill fill, int quadrant)
			throws ChartException {

		LineRenderEvent lre = new LineRenderEvent(WrappedStructureSource
				.createSeriesDataPoint(donutseries, dph));
		lre.setStart(loc1);

		Location loc2 = goFactory.createLocation(mx, my);
		lre.setEnd(loc2);
		lre.setLineAttributes(LineAttributesImpl.create(ColorDefinitionImpl
				.BLACK(), LineStyle.SOLID_LITERAL, 1));
		idr.drawLine(lre);

		double deltaY = my - loc1.getY();
		double deltaX = loc1.getX() - mx;
		double m = deltaY / deltaX;

		System.out.println(fill.toString() + " :" + m);

		Location loc3 = goFactory.createLocation(0, 0);
		double alpha = Math.atan(m);

		double loc3x = loc1.getX() + Math.cos(alpha) * 30;
		double loc3y = loc1.getY() - Math.sin(alpha) * 30;
		loc3.setX(loc3x);
		loc3.setY(loc3y);

		LineRenderEvent debug = new LineRenderEvent(WrappedStructureSource
				.createSeriesDataPoint(donutseries, dph));
		debug.setStart(loc1);

		debug.setEnd(loc3);
		debug.setLineAttributes(leaderLineAttributes);
		idr.drawLine(debug);

	}

	private void renderSliceLabel(IDeviceRenderer idr, Location loc2,
			DonutSlice slice) throws ChartException {
		DataPointHints dph = slice.getDataPoint();
		LineRenderEvent lre = new LineRenderEvent(WrappedStructureSource
				.createSeriesDataPoint(donutseries, dph));
		lre.setStart(loc2);

		double linelength = Math.abs(loc2.getX() - slice.getLabelBound());
		Label laText = LabelImpl.create();
		TextRenderEvent tre = (TextRenderEvent) ((EventObjectCache) idr)
				.getEventObject(WrappedStructureSource.createSeriesTitle(
						donutseries, laText), TextRenderEvent.class);
		Text txt;
		try {
			double text = Double.parseDouble(dph.getDisplayValue());
			String strText = "" + text;
			txt = TextImpl.create(strText);
		} catch (Exception e) {
			txt = TextImpl.create(dph.getDisplayValue());
		}
		txt.setColor(ColorDefinitionImpl.create(0, 0, 0));
		txt.setFont(FontDefinitionImpl.create("Arial", 10, false, false, false,
				false, false, 0.0, TextAlignmentImpl.create()));
		laText.setCaption(txt);
		tre.setLabel(laText);
		tre.setBlockAlignment(null);
		tre.setAction(TextRenderEvent.RENDER_TEXT_IN_BLOCK);

		if (slice.getQuadrant() == 1 || slice.getQuadrant() == 4) {
			Location loc3 = goFactory.createLocation(loc2.getX() + linelength,
					loc2.getY());
			lre.setEnd(loc3);
			lre.setLineAttributes(leaderLineAttributes);
			idr.drawLine(lre);
			tre.setBlockBounds(BoundsImpl.create(loc3.getX(), loc3.getY() - 25,
					50, 50));
		} else if (slice.getQuadrant() == 2 || slice.getQuadrant() == 3) {
			Location loc3 = goFactory.createLocation(loc2.getX() - linelength,
					loc2.getY());
			lre.setEnd(loc3);
			lre.setLineAttributes(leaderLineAttributes);
			idr.drawLine(lre);
			tre.setBlockBounds(BoundsImpl.create(loc3.getX() - 50,
					loc3.getY() - 25, 50, 50));
		}
		idr.drawText(tre);

	}

	public void computeBounds(Bounds cellBounds) throws ChartException {

		boundsBeforeComputation = goFactory.copyOf(cellBounds);
		idserver = donut.getXServer();

		// ALLOCATE SPACE FOR THE SERIES TITLE
		computeTitleArea(cellBounds);

		// BOUNDS AFTER COMPUTING TITLE AREA
		boundsAfterTitleComp = goFactory.copyOf(cellBounds);

		ChartWithoutAxes cwoa = (ChartWithoutAxes) donut.getModel();
		// If there is a percentage of chart-size : cellbounds
		// Specifies the percentage of size that the chart graphics (pie or
		// dial) in client area. By default it's not set, which means the size
		// will be auto adjusted.
		if (cwoa.isSetCoverage()) {
			double rate = cwoa.getCoverage();
			double ww = 0.5 * (1d - rate) * cellBounds.getWidth();
			double hh = 0.5 * (1d - rate) * cellBounds.getHeight();
			chartContainer = goFactory.createInsets(hh, ww, hh, ww);
		} else {

			// Bounds just for the chart - without title, label
			cellBounds = computeLabelBounds(cellBounds);

			// CREATE CHARTCONTAINER WITH ALL COMPUTATIONS
			chartContainer = goFactory.createInsets(cellBounds.getTop()
					- boundsBeforeComputation.getTop(), // TOP
					cellBounds.getLeft() - boundsBeforeComputation.getLeft(), // LEFT
					boundsBeforeComputation.getTop()
							+ boundsBeforeComputation.getHeight()
							- (cellBounds.getTop() + cellBounds.getHeight()), // BOTTOM
					boundsBeforeComputation.getLeft()
							+ boundsBeforeComputation.getWidth()
							- (cellBounds.getLeft() + cellBounds.getWidth())); // RIGHT
		}
		boundsChanged = false;
	}

	private Bounds computeLabelBounds(Bounds cellBounds) throws ChartException {
		// IF Value-LABELS SHALL BE SHOWN OUTSIDE THE DONUT SLICES
		if (labelPos == Position.OUTSIDE_LITERAL) {
			Bounds cellBoundsWithoutLabelArea = goFactory.copyOf(cellBounds);
			if (donutseries.getLabel().isVisible())
			// FILTERED FOR PERFORMANCE GAIN
			{
				computeSliceLabelBounds(cellBoundsWithoutLabelArea, true);
				// ADJUST THE BOUNDS TO ACCOMODATE THE DATA POINT LABELS +
				// LEADER LINES RENDERED OUTSIDE
				// Bounds boBeforeAdjusted = BoundsImpl.copyInstance( bo );
				// Insets trimContainer = goFactory.createInsets(0, 0, 0, 0);
				// do {
				// adjust(cellBounds, cellBoundsWithoutLabelArea,
				// trimContainer);
				// cellBoundsWithoutLabelArea.adjust(trimContainer);
				// } while (!trimContainer.areLessThan(0.5)
				// && cellBoundsWithoutLabelArea.getWidth() > 0
				// && cellBoundsWithoutLabelArea.getHeight() > 0);
				// cellBounds = cellBoundsWithoutLabelArea;
			} else {
				computeSliceBounds(cellBounds);
			}
		}
		// IF Value-LABELS SHALL BE SHOWN IN DONUT SLICES
		else if (labelPos == Position.INSIDE_LITERAL) {
			if (donutseries.getLabel().isVisible())
			// FILTERED FOR PERFORMANCE GAIN
			{
				computeSliceLabelBounds(cellBounds, false);
			}
		} else {
			throw new IllegalArgumentException(
					"exception.invalid.datapoint.position.donut");
		}
		return cellBounds;
	}

	private void computeSliceBounds(Bounds cellBounds) {

		cellBounds.setHeight(cellBounds.getHeight());
		for (DonutSlice slice : sliceList) {
			slice.setBounds(cellBounds);
		}
	}

	private void computeTitleArea(Bounds cellBounds) throws ChartException {
		titleContainerBounds = null;
		if (titleLabelText.isSetVisible()) {
			if (titleLabelPos == null) {
				throw new ChartException(ChartEngineExtensionPlugin.ID,
						ChartException.UNDEFINED_VALUE,
						"exception.unspecified.visible.series.title");
			}

			// Compute the bounding box ( location and size ) of a label.
			final BoundingBox bb = cComp.computeBox(idserver, IConstants.BELOW,
					titleLabelText, 0, 0);

			titleContainerBounds = goFactory.createBounds(0, 0, 0, 0);

			switch (titleLabelPos.getValue()) {
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

	// private Insets adjust(Bounds cellBounds, Bounds boAdjusted,
	// Insets trimContainer) throws ChartException {
	// // boAdjusted = initial 0 Values
	// // Set slice.width, slice.height, slice.x, slice.y
	// computeSliceLabelBounds(boAdjusted, true);
	//
	// trimContainer.set(0, 0, 0, 0);
	// double dDelta = 0;
	// for (DonutSlice slice : sliceList) {
	// BoundingBox sliceLabelBounds = slice.getLabelBound();
	//
	// if (sliceLabelBounds.getLeft() < cellBounds.getLeft()) {
	// dDelta = cellBounds.getLeft() - sliceLabelBounds.getLeft();
	// if (cellBounds.getLeft() < dDelta) {
	// cellBounds.setLeft(dDelta);
	// }
	// }
	// if (sliceLabelBounds.getTop() < cellBounds.getTop()) {
	// dDelta = cellBounds.getTop() - sliceLabelBounds.getTop();
	// if (trimContainer.getTop() < dDelta) {
	// trimContainer.setTop(dDelta);
	// }
	// }
	// if (sliceLabelBounds.getLeft() + sliceLabelBounds.getWidth() > cellBounds
	// .getLeft()
	// + cellBounds.getWidth()) {
	// dDelta = sliceLabelBounds.getLeft()
	// + sliceLabelBounds.getWidth() - cellBounds.getLeft()
	// - cellBounds.getWidth();
	// if (trimContainer.getRight() < dDelta) {
	// trimContainer.setRight(dDelta);
	// }
	// }
	// if (sliceLabelBounds.getTop() + sliceLabelBounds.getHeight() > cellBounds
	// .getTop()
	// + cellBounds.getHeight()) {
	// dDelta = sliceLabelBounds.getTop()
	// + sliceLabelBounds.getHeight() - cellBounds.getTop()
	// - cellBounds.getHeight();
	// if (trimContainer.getBottom() < dDelta) {
	// trimContainer.setBottom(dDelta);
	// }
	// }
	// }
	// return trimContainer;
	// }

	private void computeSliceLabelBounds(Bounds cellBounds, boolean isOutside)
			throws ChartException {

		double dataPointLabelOffset = 50;
		cellBounds.setWidth(cellBounds.getWidth() - 2 * leaderLinesLength - 2
				* dataPointLabelOffset);
		cellBounds.setLeft(cellBounds.getLeft() + leaderLinesLength
				+ dataPointLabelOffset);

		for (DonutSlice slice : sliceList) {
			slice.setBounds(cellBounds);
			// FIRST SET LABELBOUNDS _ THEN COMPUTE CELLBOUNDS AS RESULT OF
			// CELLBOUNDS-SLICELABELBOUNDS
			slice.computeLabelBoundOutside(leaderLineStyle, leaderLinesLength,
					null, dataPointLabelOffset);
		}

		// if (!isOutside) {
		// slice.computeLabelBoundOutside(leaderLineStyle,
		// leaderLinesLength, null);
		// } else {
		// slice.computeLabelBoundInside();
		// }
	}

}
