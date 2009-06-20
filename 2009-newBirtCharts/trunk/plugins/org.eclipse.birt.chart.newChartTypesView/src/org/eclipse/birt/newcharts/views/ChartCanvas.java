package org.eclipse.birt.newcharts.views;

import org.eclipse.birt.chart.device.IDeviceRenderer;
import org.eclipse.birt.chart.factory.GeneratedChartState;
import org.eclipse.birt.chart.factory.Generator;
import org.eclipse.birt.chart.model.Chart;
import org.eclipse.birt.chart.model.attribute.Bounds;
import org.eclipse.birt.chart.model.attribute.impl.BoundsImpl;
import org.eclipse.birt.chart.util.PluginSettings;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

public class ChartCanvas extends Canvas {

	  private Chart chart;

	  public Chart getChart() {
	    return chart;
	  }

	  public void setChart( Chart chart ) {
	    this.chart = chart;
	  }

	  public ChartCanvas( Composite parent, int style ) {
	    super( parent, style );
	    setLayout( new FillLayout() );
	    addPaintListener( new PaintListener() {

	      public void paintControl( PaintEvent e ) {
	        // let's get a SWT device renderer
	        IDeviceRenderer deviceRenderer = null;
	        try {
	          deviceRenderer = PluginSettings.instance().getDevice( "dv.SWT" );
	        } catch( Exception ex ) {
	          ex.printStackTrace();
	        }
	        deviceRenderer.setProperty( IDeviceRenderer.GRAPHICS_CONTEXT, e.gc );
	        // now let's make sure we stay in the client area's bounds
	        Rectangle rect = ( ( Composite )e.widget ).getClientArea();
	        final Bounds bounds = BoundsImpl.create( rect.x + 2,
	                                                 rect.y + 2,
	                                                 rect.width - 4,
	                                                 rect.height - 4 );
	        bounds.scale( 72d / deviceRenderer.getDisplayServer()
	          .getDpiResolution() );
	        // create Rohit's chart
	        // and finally, generate it...
	        final Generator gr = Generator.instance();
	        GeneratedChartState state;
	        try {
	          state = gr.build( deviceRenderer.getDisplayServer(),
	                            chart,
	                            bounds,
	                            null,
	                            null,
	                            null );
	          gr.render( deviceRenderer, state );
	        } catch( Exception ex ) {
	          ex.printStackTrace();
	        }
	      }
	    } );
	  }
}
