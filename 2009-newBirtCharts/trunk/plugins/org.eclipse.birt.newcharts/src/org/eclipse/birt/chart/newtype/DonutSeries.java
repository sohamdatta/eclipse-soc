package org.eclipse.birt.chart.newtype;

import org.eclipse.birt.chart.model.component.Series;

public interface DonutSeries extends Series {

	int getExplosion();

	void setExplosion(int pExplosion);

	void unsetExplosion();

	boolean isSetExplosion();

	
	int getRotation();
	void setRotation(int pRotationInDegree);
	void unsetRotation();
	boolean isRotatet();

	int getThickness();
	void setThickness(int pThickness);
	void unsetThickness();
	
	
	public void setText(String text);

	public String getText();


}
