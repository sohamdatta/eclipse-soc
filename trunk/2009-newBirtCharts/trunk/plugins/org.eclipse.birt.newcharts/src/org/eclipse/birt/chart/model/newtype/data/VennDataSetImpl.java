package org.eclipse.birt.chart.model.newtype.data;

import java.util.ArrayList;

import org.eclipse.birt.chart.model.data.BubbleDataSet;
import org.eclipse.birt.chart.model.data.DataSet;
import org.eclipse.birt.chart.model.data.NumberDataSet;
import org.eclipse.birt.chart.model.data.impl.BubbleDataSetImpl;
import org.eclipse.birt.chart.model.data.impl.DataSetImpl;
import org.eclipse.birt.chart.model.data.impl.NumberDataSetImpl;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;

public class VennDataSetImpl extends DataSetImpl implements VennDataSet {

	private DataSet[] dataSets;

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
		dataSets = new DataSet[3];
	}
	
	public VennDataSet copyInstance( )
	{
		VennDataSetImpl dest = new VennDataSetImpl( );
		dest.set( this );
		dest.dataSets = this.dataSets;
		return dest;
	}

	protected void set( VennDataSet src )
	{
		super.set( src );
		
	}

	public static VennDataSet create( EObject parent, EReference ref )
	{
		return new VennDataSetImpl( );
	}

	public void addDataSet(DataSet data) {
		for (int i = 0; i < this.dataSets.length; i++) {
			if (null != dataSets[i]){
				dataSets[i] = data;
				break;
			}
		}
	}

	public DataSet[] getDataSets() {
		return dataSets;
	}
	
}
