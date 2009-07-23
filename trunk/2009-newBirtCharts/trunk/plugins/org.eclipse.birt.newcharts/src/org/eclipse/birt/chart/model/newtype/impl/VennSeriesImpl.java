package org.eclipse.birt.chart.model.newtype.impl;

import org.eclipse.birt.chart.model.attribute.LeaderLineStyle;
import org.eclipse.birt.chart.model.attribute.LineStyle;
import org.eclipse.birt.chart.model.attribute.Position;
import org.eclipse.birt.chart.model.attribute.impl.ColorDefinitionImpl;
import org.eclipse.birt.chart.model.attribute.impl.LineAttributesImpl;
import org.eclipse.birt.chart.model.component.Label;
import org.eclipse.birt.chart.model.component.Series;
import org.eclipse.birt.chart.model.component.impl.LabelImpl;
import org.eclipse.birt.chart.model.component.impl.SeriesImpl;
import org.eclipse.birt.chart.model.newtype.DonutSeries;
import org.eclipse.birt.chart.model.newtype.NewtypeFactory;
import org.eclipse.birt.chart.model.newtype.VennSeries;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;

public class VennSeriesImpl extends SeriesImpl implements VennSeries{

	
	private static final String TITLE_DEFAULT = "default venn title";
	private String title = TITLE_DEFAULT;
	
	public static VennSeries create(EObject parent, EReference ref){
		return new VennSeriesImpl();
	}
	
	/**
	 * A convenient method to get an instance copy. This is much faster than the
	 * ECoreUtil.copy().
	 */
	public VennSeries copyInstance( )
	{
		VennSeriesImpl dest = new VennSeriesImpl( );
		dest.set( this );
		return dest;
	}

	protected void set( VennSeries src )
	{
		super.set( src );
		src.setTitle(this.getTitle());
		
	}

	@Override
	public String getTitle() {
		return this.title;
	}

	@Override
	public void setTitle(String title) {
		this.title = title;
	}
	
	
	/**
	 * A convenience method to create an initialized 'Series' instance
	 * 
	 * @return
	 */
	public static final Series create( )
	{
		final VennSeries se = NewtypeFactory.eINSTANCE.createVennSeries();
		( (VennSeriesImpl) se ).initialize( );
		return se;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.birt.chart.model.component.Series#initialize()
	 */
	protected final void initialize( )
	{
		super.initialize( );
	}
	
}
