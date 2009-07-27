package org.eclipse.birt.chart.model.newtype.data;

import java.util.ArrayList;

import org.eclipse.birt.chart.model.data.BubbleDataSet;
import org.eclipse.birt.chart.model.data.DataSet;
import org.eclipse.birt.chart.model.data.impl.BubbleDataSetImpl;
import org.eclipse.birt.chart.model.data.impl.DataSetImpl;

public class VennDataSetImpl extends DataSetImpl implements VennDataSet {

	private ArrayList<DataSet> dataSets;

	protected VennDataSetImpl(){
		super();
	}
	
	public static final VennDataSet create(DataSet data){
		final VennDataSetImpl venn = new VennDataSetImpl();
		venn.initialize();
		venn.addDataSet(data);
		venn.setValues(data.getValues());
		return venn;
	}
	
	private void initialize() {
		dataSets = new ArrayList<DataSet>(3);
	}
	
	public VennDataSet copyInstance( )
	{
		VennDataSetImpl dest = new VennDataSetImpl( );
		dest.set( this );
		return dest;
	}

	protected void set( VennDataSet src )
	{
		super.set( src );

	}
	
	
	@Override
	public Object getValues() {
		return super.getValues();
	}

	@Override
	protected void set(DataSet src) {
		addDataSet(src);
	}

	@Override
	public void setValues(Object newValues) {
		super.setValues(newValues);
	}

	@Override
	public void addDataSet(DataSet data) {
		if (this.dataSets.size()<3){
			dataSets.add(data);
		}
	}

	@Override
	public DataSet[] getDataSets() {
		return dataSets.toArray(new DataSet[]{});
	}

}
