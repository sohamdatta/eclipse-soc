package org.eclipse.birt.chart.model.newtype.render;

import java.util.ArrayList;
import java.util.Hashtable;

import org.eclipse.birt.chart.computation.DataPointHints;
import org.eclipse.birt.chart.computation.GObjectFactory;
import org.eclipse.birt.chart.computation.IGObjectFactory;
import org.eclipse.birt.chart.computation.withoutaxes.SeriesRenderingHints;
import org.eclipse.birt.chart.device.IDeviceRenderer;
import org.eclipse.birt.chart.device.IPrimitiveRenderer;
import org.eclipse.birt.chart.event.ArcRenderEvent;
import org.eclipse.birt.chart.event.EventObjectCache;
import org.eclipse.birt.chart.event.TextRenderEvent;
import org.eclipse.birt.chart.event.WrappedStructureSource;
import org.eclipse.birt.chart.exception.ChartException;
import org.eclipse.birt.chart.model.ChartWithoutAxes;
import org.eclipse.birt.chart.model.attribute.Bounds;
import org.eclipse.birt.chart.model.attribute.Fill;
import org.eclipse.birt.chart.model.attribute.Location;
import org.eclipse.birt.chart.model.attribute.impl.ColorDefinitionImpl;
import org.eclipse.birt.chart.model.attribute.impl.DataPointImpl;
import org.eclipse.birt.chart.model.component.Series;
import org.eclipse.birt.chart.model.data.SeriesDefinition;
import org.eclipse.birt.chart.model.layout.ClientArea;
import org.eclipse.birt.chart.model.layout.Legend;
import org.eclipse.birt.chart.model.layout.Plot;
import org.eclipse.birt.chart.model.newtype.VennSeries;
import org.eclipse.birt.chart.render.BaseRenderer;
import org.eclipse.birt.chart.render.ISeriesRenderingHints;

public class Venn extends BaseRenderer {

	ArrayList<double[]> assignedSeriesDefinition;
	Bounds bounds;
	private Hashtable<Double, Double> duplicateValues;
	private DataPointHints dph = null;
	
	protected static final IGObjectFactory goFactory = GObjectFactory
	.instance();
	private VennSeries vennseries;
	private IDeviceRenderer idr = null;

	@Override
	public void compute(Bounds bo, Plot p, ISeriesRenderingHints isrh)
			throws ChartException {

		
		final SeriesRenderingHints srh = (SeriesRenderingHints) isrh;
		
		vennseries = (VennSeries) getSeries();

		bounds = getCellBounds().adjustedInstance(p.getClientArea().getInsets());
		final SeriesDefinition sd = getSeriesDefinition();

		assignedSeriesDefinition = new ArrayList<double[]>();

		for (SeriesDefinition sdtmp : sd.getSeriesDefinitions()) {
			for (Series series : sdtmp.getSeries()) {
				assignedSeriesDefinition.add((double[]) series.getDataSet()
						.getValues());
			}
		}

		duplicateValues = new Hashtable<Double, Double>();
		
		for (double d1 : assignedSeriesDefinition.get(0)) {
			for (double d2 : assignedSeriesDefinition.get(1)) {
				if (d1 == d2){
					duplicateValues.put(d1, d2);
				}
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


		if (idr == null)
		idr = getDevice(); 
		
		double mx = bounds.getLeft()+bounds.getWidth()/2;
		double my = bounds.getTop() + bounds.getHeight()/2;
		

		ArcRenderEvent circle1 = (ArcRenderEvent) ((EventObjectCache) idr)
		.getEventObject(WrappedStructureSource.createSeries(vennseries), ArcRenderEvent.class);
		
		circle1.setStartAngle(0);
		circle1.setAngleExtent(360);
		
		circle1.setBackground(ColorDefinitionImpl.BLUE());
		
		Location sliceLoc = goFactory.createLocation(150, 150);
		circle1.setTopLeft(sliceLoc);
		
		circle1.setWidth(bounds.getWidth()/2);
		circle1.setHeight(bounds.getWidth()/2);
		
		
		ArcRenderEvent circle2 = (ArcRenderEvent) circle1.copy();
		circle2.setBackground(ColorDefinitionImpl.ORANGE());
		Location circleLoc2 = circle1.getTopLeft().copyInstance();
		circleLoc2.translate(50, 0);
		circle2.setTopLeft(circleLoc2);
		
		idr.fillArc(circle1);
		idr.fillArc(circle2);
		
		
		
		
		

	}

}
