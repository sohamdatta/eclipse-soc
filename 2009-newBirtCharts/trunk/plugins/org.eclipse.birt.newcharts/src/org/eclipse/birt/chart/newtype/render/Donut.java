package org.eclipse.birt.chart.newtype.render;

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
import org.eclipse.birt.chart.model.attribute.Bounds;
import org.eclipse.birt.chart.model.attribute.ColorDefinition;
import org.eclipse.birt.chart.model.attribute.Fill;
import org.eclipse.birt.chart.model.attribute.FontDefinition;
import org.eclipse.birt.chart.model.attribute.Text;
import org.eclipse.birt.chart.model.attribute.TextAlignment;
import org.eclipse.birt.chart.model.attribute.impl.BoundsImpl;
import org.eclipse.birt.chart.model.attribute.impl.ColorDefinitionImpl;
import org.eclipse.birt.chart.model.attribute.impl.FontDefinitionImpl;
import org.eclipse.birt.chart.model.attribute.impl.TextAlignmentImpl;
import org.eclipse.birt.chart.model.attribute.impl.TextImpl;
import org.eclipse.birt.chart.model.component.Label;
import org.eclipse.birt.chart.model.component.impl.LabelImpl;
import org.eclipse.birt.chart.model.layout.Legend;
import org.eclipse.birt.chart.model.layout.Plot;
import org.eclipse.birt.chart.model.type.PieSeries;
import org.eclipse.birt.chart.newtype.DonutSeries;
import org.eclipse.birt.chart.render.BaseRenderer;
import org.eclipse.birt.chart.render.ISeriesRenderingHints;

public class Donut extends BaseRenderer {

	public Donut() {
		super();
	}

	@Override
	public void compute(Bounds bo, Plot p, ISeriesRenderingHints isrh)
			throws ChartException {
		// TODO Auto-generated method stub

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
		final DonutSeries ps = (DonutSeries) getSeries();

		Label laText = LabelImpl.create();
		Text txt = TextImpl.create(ps.getText());
		txt.setColor(ColorDefinitionImpl.create(255, 0, 0));
		txt.setFont(FontDefinitionImpl.create("Arial", 15, false, false, false,
				false, false, 0.0, TextAlignmentImpl.create()));
		laText.setCaption(txt);

		TextRenderEvent tre = (TextRenderEvent) ((EventObjectCache) idr)
				.getEventObject(WrappedStructureSource.createSeriesTitle(ps,
						laText), TextRenderEvent.class);

		tre.setLabel(laText);
		tre.setBlockBounds(BoundsImpl.create(50, 0, 100, 100));
		tre.setBlockAlignment(null);
		tre.setAction(TextRenderEvent.RENDER_TEXT_IN_BLOCK);
		idr.drawText(tre);
	}
}
