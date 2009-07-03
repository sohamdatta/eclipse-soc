package org.eclipse.birt.chart.model.newtype.render;

import org.eclipse.birt.chart.computation.withoutaxes.SeriesRenderingHints;
import org.eclipse.birt.chart.device.IDeviceRenderer;
import org.eclipse.birt.chart.device.IPrimitiveRenderer;
import org.eclipse.birt.chart.event.EventObjectCache;
import org.eclipse.birt.chart.event.TextRenderEvent;
import org.eclipse.birt.chart.event.WrappedStructureSource;
import org.eclipse.birt.chart.exception.ChartException;
import org.eclipse.birt.chart.model.ChartWithoutAxes;
import org.eclipse.birt.chart.model.attribute.Bounds;
import org.eclipse.birt.chart.model.attribute.Fill;
import org.eclipse.birt.chart.model.attribute.Text;
import org.eclipse.birt.chart.model.attribute.impl.BoundsImpl;
import org.eclipse.birt.chart.model.attribute.impl.ColorDefinitionImpl;
import org.eclipse.birt.chart.model.attribute.impl.FontDefinitionImpl;
import org.eclipse.birt.chart.model.attribute.impl.TextAlignmentImpl;
import org.eclipse.birt.chart.model.attribute.impl.TextImpl;
import org.eclipse.birt.chart.model.component.Label;
import org.eclipse.birt.chart.model.component.impl.LabelImpl;
import org.eclipse.birt.chart.model.data.SeriesDefinition;
import org.eclipse.birt.chart.model.layout.Legend;
import org.eclipse.birt.chart.model.layout.Plot;
import org.eclipse.birt.chart.model.newtype.DonutSeries;
import org.eclipse.birt.chart.plugin.ChartEngineExtensionPlugin;
import org.eclipse.birt.chart.render.BaseRenderer;
import org.eclipse.birt.chart.render.ISeriesRenderingHints;

public class Donut extends BaseRenderer {

	private DonutRenderer donutRenderer;

	public Donut() {
		super();
	}

	@Override
	public void compute(Bounds bo, Plot p, ISeriesRenderingHints isrh)
			throws ChartException {

		// p = Ploat Area -> Space for drawing

		final SeriesRenderingHints srh = (SeriesRenderingHints) isrh;

		// VALIDATE CONSISTENT DATASET COUNT BETWEEN BASE AND ORTHOGONAL
		try {
			validateDataSetCount(isrh);
		} catch (ChartException vex) {
			throw new ChartException(ChartEngineExtensionPlugin.ID,
					ChartException.GENERATION, vex);
		}

		// SCALE VALIDATION
		final ChartWithoutAxes cwoa = (ChartWithoutAxes) getModel();
		final SeriesDefinition sd = getSeriesDefinition();
		final Bounds boCB = getCellBounds();
		
		try {
			donutRenderer = new DonutRenderer(cwoa, this, srh.getDataPoints(),
					srh.asPrimitiveDoubleValues(), sd.getSeriesPalette());
			// donutRenderer.computeInsets( boCB );
			donutRenderer.initSlices(srh.asPrimitiveDoubleValues(), srh.getDataPoints(), cwoa);
			donutRenderer.computeBounds(boCB);
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new ChartException(ChartEngineExtensionPlugin.ID,
					ChartException.GENERATION, ex);
		}
		// new DonutRenderer
		// getDeviceScale() -> BASERENDERER
	}

	@Override
	public void renderLegendGraphic(IPrimitiveRenderer ipr, Legend lg,
			Fill paletteEntry, Bounds bo) throws ChartException {
		// TODO Auto-generated method stub

	}

	@Override
	public void renderSeries(IPrimitiveRenderer ipr, Plot p,
			ISeriesRenderingHints isrh) throws ChartException {

		IDeviceRenderer idr = getDevice();
		Fill bgcolor = p.getClientArea().getBackground() != null ? p
				.getClientArea().getBackground() : ColorDefinitionImpl.WHITE();
				
		donutRenderer.render(idr,bgcolor);
		
		Label laText = LabelImpl.create();

		DonutSeries donutseries = (DonutSeries) getSeries();
		// GET CONNECTION TO THE SERIESIMPL (DONUTSERIESIMPL)
		Text txt = TextImpl.create("Teststring");
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
