package org.eclipse.birt.chart.newtype.render;

import org.eclipse.birt.chart.computation.withoutaxes.SeriesRenderingHints;
import org.eclipse.birt.chart.device.IDeviceRenderer;
import org.eclipse.birt.chart.device.IPrimitiveRenderer;
import org.eclipse.birt.chart.event.ArcRenderEvent;
import org.eclipse.birt.chart.event.RectangleRenderEvent;
import org.eclipse.birt.chart.event.WrappedStructureSource;
import org.eclipse.birt.chart.exception.ChartException;
import org.eclipse.birt.chart.model.attribute.Bounds;
import org.eclipse.birt.chart.model.attribute.Fill;
import org.eclipse.birt.chart.model.layout.Legend;
import org.eclipse.birt.chart.model.layout.Plot;
import org.eclipse.birt.chart.model.type.PieSeries;
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
		
		final SeriesRenderingHints srh = (SeriesRenderingHints) isrh;

		final Bounds boCB = getCellBounds();
		IDeviceRenderer idr = getDevice();
		final PieSeries ps = (PieSeries) getSeries( );
		
		double x = boCB.getWidth() / 2;
		double y = boCB.getHeight() / 2;

		ArcRenderEvent are = new ArcRenderEvent( WrappedStructureSource.createSeriesDataPoint( ps,
				srh.getDataPoints()[0].getVirtualCopy( ) ) );
		idr.fillArc(are);

	}

}
