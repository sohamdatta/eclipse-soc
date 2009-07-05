package org.eclipse.birt.chart.model.newtype.render;

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
import org.eclipse.birt.chart.event.EventObjectCache;
import org.eclipse.birt.chart.event.LineRenderEvent;
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
import org.eclipse.birt.chart.model.attribute.Text;
import org.eclipse.birt.chart.model.attribute.impl.BoundsImpl;
import org.eclipse.birt.chart.model.attribute.impl.ColorDefinitionImpl;
import org.eclipse.birt.chart.model.attribute.impl.FontDefinitionImpl;
import org.eclipse.birt.chart.model.attribute.impl.LineAttributesImpl;
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
						fillColor, dph, sliceDepth, frameThickness);

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

	public void render(IDeviceRenderer idr, Fill bgcolor) throws ChartException {

		for (int i = 0; i < sliceList.length; i++) {

			DonutSlice slice = sliceList[i];
			DataPointHints dph = slice.getDataPoint();
			Arc3DRenderEvent coloredarc = new Arc3DRenderEvent(
					WrappedStructureSource.createSeriesDataPoint(donutseries,
							dph));

			Fill fPaletteEntry = slice.getFillColor();
			coloredarc.setBackground(fPaletteEntry);
			coloredarc.setOutline(LineAttributesImpl.create(ColorDefinitionImpl
					.BLACK(), LineStyle.SOLID_LITERAL, 2));
			
			
			coloredarc.getOutline().setVisible(true);
			coloredarc.getOutline().setColor(ColorDefinitionImpl.BLACK());
			coloredarc.getOutline().setThickness(3);
			
			
			
			coloredarc.setStartAngle(slice.getStartAngle()
					+ slice.getExplosion() / 2);
			coloredarc.setAngleExtent(slice.getAngleExtent()
					- slice.getExplosion());

			Location sliceLocation = goFactory.createLocation(slice.getXc(),
					slice.getYc());
			coloredarc.setTopLeft(sliceLocation);

			coloredarc.setWidth(slice.getWidth());
			coloredarc.setHeight(slice.getHeight());
			coloredarc.setDepth(50);

			coloredarc.setOuterRadius(coloredarc.getWidth() / 2);
			coloredarc.setInnerRadius(coloredarc.getOuterRadius()
					- slice.getFrameThickness());
			idr.fillArc(coloredarc);
			
			drawLabel(slice, sliceLocation, idr);
		}
	}

	private void drawLabel(DonutSlice slice, Location sliceLocation,
			IDeviceRenderer idr) throws ChartException {

		DataPointHints dph = slice.getDataPoint();

		double tickLength = 20.0;
		double mx = sliceLocation.getX() + slice.getWidth() / 2;
		double my = sliceLocation.getY() + slice.getHeight() / 2;
		double a = slice.getWidth() / 2;
		double b = slice.getHeight() / 2;

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
