package org.eclipse.birt.chart.newtype.render;

import org.eclipse.birt.chart.computation.DataPointHints;
import org.eclipse.birt.chart.computation.GObjectFactory;
import org.eclipse.birt.chart.computation.IGObjectFactory;
import org.eclipse.birt.chart.computation.Rectangle;
import org.eclipse.birt.chart.computation.withoutaxes.SeriesRenderingHints;
import org.eclipse.birt.chart.device.IDeviceRenderer;
import org.eclipse.birt.chart.device.IPrimitiveRenderer;
import org.eclipse.birt.chart.event.ArcRenderEvent;
import org.eclipse.birt.chart.event.EventObjectCache;
import org.eclipse.birt.chart.event.RectangleRenderEvent;
import org.eclipse.birt.chart.event.TextRenderEvent;
import org.eclipse.birt.chart.event.WrappedStructureSource;
import org.eclipse.birt.chart.exception.ChartException;
import org.eclipse.birt.chart.model.ChartWithoutAxes;
import org.eclipse.birt.chart.model.attribute.Bounds;
import org.eclipse.birt.chart.model.attribute.ColorDefinition;
import org.eclipse.birt.chart.model.attribute.Fill;
import org.eclipse.birt.chart.model.attribute.FontDefinition;
import org.eclipse.birt.chart.model.attribute.Location;
import org.eclipse.birt.chart.model.attribute.Palette;
import org.eclipse.birt.chart.model.attribute.Text;
import org.eclipse.birt.chart.model.attribute.TextAlignment;
import org.eclipse.birt.chart.model.attribute.impl.BoundsImpl;
import org.eclipse.birt.chart.model.attribute.impl.ColorDefinitionImpl;
import org.eclipse.birt.chart.model.attribute.impl.FillImpl;
import org.eclipse.birt.chart.model.attribute.impl.FontDefinitionImpl;
import org.eclipse.birt.chart.model.attribute.impl.TextAlignmentImpl;
import org.eclipse.birt.chart.model.attribute.impl.TextImpl;
import org.eclipse.birt.chart.model.component.Label;
import org.eclipse.birt.chart.model.component.impl.LabelImpl;
import org.eclipse.birt.chart.model.data.SeriesDefinition;
import org.eclipse.birt.chart.model.layout.Legend;
import org.eclipse.birt.chart.model.layout.Plot;
import org.eclipse.birt.chart.model.type.PieSeries;
import org.eclipse.birt.chart.newtype.DonutSeries;
import org.eclipse.birt.chart.plugin.ChartEngineExtensionPlugin;
import org.eclipse.birt.chart.render.BaseRenderer;
import org.eclipse.birt.chart.render.ISeriesRenderingHints;
import org.eclipse.birt.chart.util.FillUtil;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;

public class Donut extends BaseRenderer {

	public Donut() {
		super();
	}

	@Override
	public void compute(Bounds bo, Plot p, ISeriesRenderingHints isrh)
			throws ChartException {

		// p = Ploat Area -> Space for drawing

		// Maybe a link for datasets
		final SeriesRenderingHints srh = (SeriesRenderingHints) isrh;

		// VALIDATE CONSISTENT DATASET COUNT BETWEEN BASE AND ORTHOGONAL
		try {
			validateDataSetCount(isrh);
		} catch (ChartException vex) {
			throw new ChartException(ChartEngineExtensionPlugin.ID,
					ChartException.GENERATION, vex);
		}
	}

	@Override
	public void renderLegendGraphic(IPrimitiveRenderer ipr, Legend lg,
			Fill paletteEntry, Bounds bo) throws ChartException {
		// TODO Auto-generated method stub

	}

	@Override
	public void renderSeries(IPrimitiveRenderer ipr, Plot p,
			ISeriesRenderingHints isrh) throws ChartException {

		final SeriesRenderingHints srh = (SeriesRenderingHints) isrh;
		IDeviceRenderer idr = getDevice();
		final DonutSeries donutseries = (DonutSeries) getSeries();
		ChartWithoutAxes cwoa = (ChartWithoutAxes) getModel();
		final Bounds boCB = getCellBounds();
		final SeriesDefinition seriesdefinition = getSeriesDefinition();

		Fill bgcolor = p.getClientArea().getBackground() != null ? p
				.getClientArea().getBackground() : ColorDefinitionImpl.WHITE();
		
		double plotwidth = p.getBounds().getWidth();
		double plotheight = p.getBounds().getHeight();
		
		//TODO RELATIVE WIDTH FOR DONUT
		double donutwidth = 300;
		
		double x = plotwidth-donutwidth*0.75;
		double y = plotheight-donutwidth*0.75;
		
		int rotation = donutseries.getRotation();
		int explosion = donutseries.getExplosion();
		int thickness = donutseries.getThickness();

		DataPointHints[] datapointhints = srh.getDataPoints();
		double[] primitiveDataPoints = srh.asPrimitiveDoubleValues();
		double sum = 0;
		for (int i = 0; i < primitiveDataPoints.length; i++) {
			sum += primitiveDataPoints[i];
		}

//		// 50x50 Rectangle for testcases
//		RectangleRenderEvent rec = (RectangleRenderEvent) ((EventObjectCache) idr)
//		.getEventObject(WrappedStructureSource.createSeries(
//				donutseries), RectangleRenderEvent.class);
//		rec.setBounds(BoundsImpl.create(x, y, donutwidth, donutwidth));
//		rec.setBackground(ColorDefinitionImpl.ORANGE());
//		idr.fillRectangle(rec);
		
		Palette categoryColors = seriesdefinition.getSeriesPalette();
		IGObjectFactory goFactory = GObjectFactory.instance();
		Location loc = goFactory.createLocation(x, y);
		
		double lastAngle = rotation;
		for (int i = 0; i < primitiveDataPoints.length; i++) {

			// oBaseValue = category1
			// oOrthogonalValue = 30 -> absoluter Wert, der eingegebn wurde
			// oOrhtogonalPercentile = 0.3
			DataPointHints dph = datapointhints[i];
			ArcRenderEvent coloredarc = new ArcRenderEvent(
					WrappedStructureSource.createSeriesDataPoint(donutseries,
							dph));
			Fill fPaletteEntry = categoryColors.getEntries().get(i);
			coloredarc.setBackground(fPaletteEntry);
			coloredarc.setStartAngle(lastAngle);
			//TODO Check pixel
			double deltaAngle = (360d / sum) * primitiveDataPoints[i];
			coloredarc.setAngleExtent(deltaAngle);
			coloredarc.setTopLeft(loc);
			coloredarc.setWidth(donutwidth);
			coloredarc.setHeight(donutwidth);
//			coloredarc.setStyle(ArcRenderEvent.SECTOR);
			coloredarc.setStyle(ArcRenderEvent.SECTOR);
			idr.fillArc(coloredarc);

			// Apply Thickness
			ArcRenderEvent negativarc = new ArcRenderEvent(
					WrappedStructureSource.createSeriesDataPoint(donutseries,
							dph));
			negativarc.setBackground(bgcolor);
			negativarc.setStartAngle(lastAngle);
			negativarc.setAngleExtent(deltaAngle);
			negativarc.setTopLeft(goFactory.createLocation(x+thickness, y + thickness));
//			negativarc.setTopLeft((int)loc.getX()-thickness,(int)loc.getY()-thickness);

			negativarc.setWidth(donutwidth - 2*thickness);
			negativarc.setHeight(donutwidth - 2*thickness);
			negativarc.setStyle(ArcRenderEvent.SECTOR);
			idr.fillArc(negativarc);

			lastAngle = lastAngle + deltaAngle;
		}

		Label laText = LabelImpl.create();

		System.out.println("Amount of categories slices to draw: "
				+ datapointhints.length);

		// GET CONNECTION TO THE SERIESIMPL (DONUTSERIESIMPL)
		Text txt = TextImpl.create(donutseries.getText());
		txt.setColor(ColorDefinitionImpl.create(255, 0, 0));
		txt.setFont(FontDefinitionImpl.create("Arial", 15, false, false, false,
				false, false, 0.0, TextAlignmentImpl.create()));
		laText.setCaption(txt);

		TextRenderEvent tre = (TextRenderEvent) ((EventObjectCache) idr)
				.getEventObject(WrappedStructureSource.createSeriesTitle(
						donutseries, laText), TextRenderEvent.class);
		tre.setLabel(laText);
		tre.setBlockBounds(BoundsImpl.create(50, 0, 100, 100));
		tre.setBlockAlignment(null);
		tre.setAction(TextRenderEvent.RENDER_TEXT_IN_BLOCK);

		idr.drawText(tre);
	}
}
