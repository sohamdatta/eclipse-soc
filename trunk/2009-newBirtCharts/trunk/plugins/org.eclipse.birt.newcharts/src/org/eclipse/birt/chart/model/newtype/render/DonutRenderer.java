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
import org.eclipse.birt.chart.engine.extension.i18n.Messages;
import org.eclipse.birt.chart.event.ArcRenderEvent;
import org.eclipse.birt.chart.event.WrappedStructureSource;
import org.eclipse.birt.chart.exception.ChartException;
import org.eclipse.birt.chart.factory.RunTimeContext.StateKey;
import org.eclipse.birt.chart.model.ChartWithoutAxes;
import org.eclipse.birt.chart.model.attribute.Bounds;
import org.eclipse.birt.chart.model.attribute.Fill;
import org.eclipse.birt.chart.model.attribute.Insets;
import org.eclipse.birt.chart.model.attribute.LeaderLineStyle;
import org.eclipse.birt.chart.model.attribute.LineAttributes;
import org.eclipse.birt.chart.model.attribute.Location;
import org.eclipse.birt.chart.model.attribute.Palette;
import org.eclipse.birt.chart.model.attribute.Position;
import org.eclipse.birt.chart.model.component.Label;
import org.eclipse.birt.chart.model.layout.Plot;
import org.eclipse.birt.chart.model.newtype.DonutSeries;
import org.eclipse.birt.chart.plugin.ChartEngineExtensionPlugin;
import org.eclipse.birt.chart.render.PieRenderer.PieSlice;
import org.eclipse.birt.chart.script.ScriptHandler;

public class DonutRenderer {

	protected static final IGObjectFactory goFactory = GObjectFactory.instance( );
	
	private Donut donut;
	private Slice[] sliceList;
	private DonutSeries donutseries;
	private int exploration;
	private int rotation;
	private int thickness;
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

	public DonutRenderer(ChartWithoutAxes cwoa, Donut donut,
			DataPointHints[] dataPoints, double[] asPrimitiveDoubleValues,
			Palette seriesPalette) throws ChartException {

		this.donut = donut;
		this.cComp = donut.getRunTimeContext().getState(
				StateKey.CHART_COMPUTATION_KEY);
		this.donutseries = (DonutSeries) donut.getSeries();
		this.deviceScale = donut.getDeviceScale();
		this.seriesPalette = seriesPalette;

		this.titleLabelPos = donutseries.getTitlePosition();
		this.titleLabelText = donutseries.getTitle();

		this.labelPos = donutseries.getLabelPosition();
		this.labelText = donutseries.getLabel();
		
		this.exploration = (donutseries.isSetExplosion() ? donutseries
				.getExplosion() : 0);
		this.exploration *= this.deviceScale;
		this.rotation = (donutseries.isSetRotation() ? donutseries
				.getRotation() : 0);
		this.rotation *= this.deviceScale;
		this.thickness = (donutseries.getThickness() == 0
				|| donutseries.getThickness() < 0 ? 0 : donutseries
				.getThickness());
		this.thickness *= this.deviceScale;

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
		this.sliceList = new Slice[sliceCount];
		// computeSlices(asPrimitiveDoubleValues, dataPoints);

	}

	protected void computeSlices(double[] primitiveDoubleValues,
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

				// TODO Check pixel
				double startAngle = lastAngle + MIN_DOUBLE;
				double angleExtent = (360d / total)
						* Math.abs(primitiveDoubleValues[i]);

				sliceList[i] = new Slice(startAngle, angleExtent, fillColor,
						dph);
				lastAngle = lastAngle + angleExtent;
			}

		}
		initExploded();

	}

	private void initExploded() {

		if (exploration == 0) {
			return;
		}

		for (Slice slice : sliceList) {
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
						.evaluate("" + (exploration != 0));

				if (obj instanceof Boolean) {
					slice.setExploded(exploration);
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
			negativarc.setStartAngle(slice.getStartAngle() - 5);
			negativarc.setAngleExtent(slice.getAngleExtent() + 5);
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

	public void computeFrame(Bounds cellBounds) throws ChartException {

		idserver = donut.getXServer();

		// ALLOCATE SPACE FOR THE SERIES TITLE
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
			
			titleContainerBounds = goFactory.createBounds( 0, 0, 0, 0 );

			switch ( titleLabelPos.getValue( ) )
			{
				case Position.BELOW :
					cellBounds.setHeight( cellBounds.getHeight( ) - bb.getHeight( ) );
					titleContainerBounds.set( cellBounds.getLeft( ), cellBounds.getTop( )
							+ cellBounds.getHeight( ), cellBounds.getWidth( ), cellBounds.getHeight( ) );
					break;
				case Position.ABOVE :
					titleContainerBounds.set( cellBounds.getLeft( ),
							cellBounds.getTop( ),
							cellBounds.getWidth( ),
							bb.getHeight( ) );
					cellBounds.setTop( cellBounds.getTop( ) + bb.getHeight( ) );
					cellBounds.setHeight( cellBounds.getHeight( ) - bb.getHeight( ) );
					break;
				case Position.LEFT :
					cellBounds.setWidth( cellBounds.getWidth( ) - bb.getWidth( ) );
					titleContainerBounds.set( cellBounds.getLeft( ),
							cellBounds.getTop( ),
							bb.getWidth( ),
							cellBounds.getHeight( ) );
					cellBounds.setLeft( cellBounds.getLeft( ) + bb.getWidth( ) );
					break;
				case Position.RIGHT :
					cellBounds.setWidth( cellBounds.getWidth( ) - bb.getWidth( ) );
					titleContainerBounds.set( cellBounds.getLeft( ) + cellBounds.getWidth( ),
							cellBounds.getTop( ),
							bb.getWidth( ),
							cellBounds.getHeight( ) );
					break;
				default :
					throw new IllegalArgumentException("exception.illegal.pie.series.title.position");
			}
			
			
			ChartWithoutAxes cwoa = (ChartWithoutAxes) donut.getModel();
			
			// If there is a percentage of chart-size : cellbounds
			if ( cwoa.isSetCoverage( ) )
			{
				double rate = cwoa.getCoverage( );
				double ww = 0.5 * ( 1d - rate ) * cellBounds.getWidth( );
				double hh = 0.5 * ( 1d - rate ) * cellBounds.getHeight( );
				chartContainer = goFactory.createInsets( hh, ww, hh, ww );
			}
			else{
			}
		}

		
		
		
		
		
//		double plotwidth = p.getBounds().getWidth();
//		double plotheight = p.getBounds().getHeight();
//		donutwidth = 300;
//		double x = plotwidth - donutwidth * 0.75;
//		double y = plotheight - donutwidth * 0.75;
//
//		IGObjectFactory goFactory = GObjectFactory.instance();
//		loc = goFactory.createLocation(x, y);
	}

}

class Slice {

	private DataPointHints dataPoint;
	private Fill fillColor;
	private double angleExtent;
	private double startAngle;
	private int exploration;

	public Slice(double startAngle, double angleExtent, Fill fillColor,
			DataPointHints dph) {

		this.setStartAngle(startAngle);
		this.setAngleExtent(angleExtent);
		this.setFillColor(fillColor);
		this.setDataPoint(dph);
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

}
