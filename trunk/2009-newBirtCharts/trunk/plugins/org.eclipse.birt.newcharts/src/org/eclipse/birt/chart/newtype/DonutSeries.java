package org.eclipse.birt.chart.newtype;

import org.eclipse.birt.chart.model.component.Series;

public interface DonutSeries extends Series{

	
	public void setText(String text);
	
	public void setRadius(double rad);
	public double getRadius();
	
}
