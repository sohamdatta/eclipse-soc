package org.eclipse.birt.chart.model.newtype.data;

import org.eclipse.birt.chart.model.data.DataSet;

public interface VennDataSet extends DataSet {

	
	VennDataSet copyInstance();
	
	void addDataSet(DataSet data);
	DataSet[] getDataSets();

	
}
