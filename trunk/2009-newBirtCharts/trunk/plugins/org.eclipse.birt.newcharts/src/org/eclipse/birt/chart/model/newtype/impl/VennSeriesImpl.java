package org.eclipse.birt.chart.model.newtype.impl;

import org.eclipse.birt.chart.model.attribute.LeaderLineStyle;
import org.eclipse.birt.chart.model.attribute.LineAttributes;
import org.eclipse.birt.chart.model.attribute.LineStyle;
import org.eclipse.birt.chart.model.attribute.Position;
import org.eclipse.birt.chart.model.attribute.impl.ColorDefinitionImpl;
import org.eclipse.birt.chart.model.attribute.impl.LineAttributesImpl;
import org.eclipse.birt.chart.model.component.Label;
import org.eclipse.birt.chart.model.component.Series;
import org.eclipse.birt.chart.model.component.impl.LabelImpl;
import org.eclipse.birt.chart.model.component.impl.SeriesImpl;
import org.eclipse.birt.chart.model.data.DataSet;
import org.eclipse.birt.chart.model.newtype.DonutSeries;
import org.eclipse.birt.chart.model.newtype.NewtypeFactory;
import org.eclipse.birt.chart.model.newtype.VennSeries;
import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;

public class VennSeriesImpl extends SeriesImpl implements VennSeries{

	
	private static Label TITLE_DEFAULT ;
	private Label title = TITLE_DEFAULT;
	private LineAttributes leaderLineAttributes;
	private double leaderLineLength;
	private LeaderLineStyle leaderLineStyle;
	private Position titlePosition;
	private IntersectionColorType intersectionColorType;
	
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
//		src.setTitle(this.getTitle());
//		src.setTitlePosition(this.getTitlePosition());
//		src.setLeaderLineAttributes(this.getLeaderLineAttributes());
//		src.setLeaderLineLength(this.getLeaderLineLength());
//		src.setLeaderLineStyle(this.getLeaderLineStyle());
		this.setTitle(src.getTitle());
		this.setTitlePosition(src.getTitlePosition());
		this.setLeaderLineAttributes(src.getLeaderLineAttributes());
		this.setLeaderLineLength(src.getLeaderLineLength());
		this.setLeaderLineStyle(src.getLeaderLineStyle());
		this.setIntersectionColorType(src.getIntersectionColorType());
		
	}

	@Override
	public Label getTitle() {
		return this.title;
	}

	@Override
	public void setTitle(Label title) {
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
		TITLE_DEFAULT = LabelImpl.create();
		TITLE_DEFAULT.getCaption().setValue("Default Venn Diagram Title");
		TITLE_DEFAULT.setVisible(false);
		setTitle(TITLE_DEFAULT);
	}

	@Override
	public LineAttributes getLeaderLineAttributes() {
		return this.leaderLineAttributes;
	}

	@Override
	public double getLeaderLineLength() {
		return this.leaderLineLength;
	}

	@Override
	public LeaderLineStyle getLeaderLineStyle() {
		return this.leaderLineStyle;
	}

	@Override
	public void setLeaderLineAttributes(LineAttributes value) {
		this.leaderLineAttributes = value;
	}

	@Override
	public void setLeaderLineLength(double value) {
		this.leaderLineLength = value;
	}

	@Override
	public void setLeaderLineStyle(LeaderLineStyle value) {
		this.leaderLineStyle = value;
	}

	@Override
	public Position getTitlePosition() {
		return this.titlePosition;
	}

	@Override
	public void setTitlePosition(Position p) {
		this.titlePosition = p;
	}
	
	public void setIntersectionColorType(IntersectionColorType colorType){
		this.intersectionColorType = colorType;
	}
	
	public IntersectionColorType getIntersectionColorType(){
		return this.intersectionColorType;
	}
	
}
