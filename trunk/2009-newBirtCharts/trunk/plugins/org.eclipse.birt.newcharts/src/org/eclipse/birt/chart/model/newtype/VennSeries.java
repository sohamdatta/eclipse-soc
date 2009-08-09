package org.eclipse.birt.chart.model.newtype;

import org.eclipse.birt.chart.model.attribute.LeaderLineStyle;
import org.eclipse.birt.chart.model.attribute.LineAttributes;
import org.eclipse.birt.chart.model.attribute.Position;
import org.eclipse.birt.chart.model.component.Label;
import org.eclipse.birt.chart.model.component.Series;

public interface VennSeries extends Series {

	
	LineAttributes getLeaderLineAttributes();
	void setLeaderLineAttributes(LineAttributes value);
	
	LeaderLineStyle getLeaderLineStyle();
	void setLeaderLineStyle(LeaderLineStyle value);
	
	double getLeaderLineLength();
	void setLeaderLineLength(double value);
	
	void setTitle(Label title);
	Label getTitle();
	
	void setTitlePosition(Position p);
	Position getTitlePosition();
	
}
