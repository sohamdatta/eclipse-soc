package org.eclipse.birt.newcharts.views;

import org.eclipse.birt.chart.model.Chart;
import org.eclipse.birt.chart.model.ChartWithAxes;
import org.eclipse.birt.chart.model.ChartWithoutAxes;
import org.eclipse.birt.chart.model.attribute.ChartDimension;
import org.eclipse.birt.chart.model.attribute.Fill;
import org.eclipse.birt.chart.model.attribute.FontDefinition;
import org.eclipse.birt.chart.model.attribute.LegendItemType;
import org.eclipse.birt.chart.model.attribute.LineStyle;
import org.eclipse.birt.chart.model.attribute.Position;
import org.eclipse.birt.chart.model.attribute.Text;
import org.eclipse.birt.chart.model.attribute.impl.ColorDefinitionImpl;
import org.eclipse.birt.chart.model.attribute.impl.TextImpl;
import org.eclipse.birt.chart.model.component.Axis;
import org.eclipse.birt.chart.model.component.Label;
import org.eclipse.birt.chart.model.component.Series;
import org.eclipse.birt.chart.model.component.impl.LabelImpl;
import org.eclipse.birt.chart.model.component.impl.SeriesImpl;
import org.eclipse.birt.chart.model.data.NumberDataSet;
import org.eclipse.birt.chart.model.data.SeriesDefinition;
import org.eclipse.birt.chart.model.data.TextDataSet;
import org.eclipse.birt.chart.model.data.impl.NumberDataSetImpl;
import org.eclipse.birt.chart.model.data.impl.SeriesDefinitionImpl;
import org.eclipse.birt.chart.model.data.impl.TextDataSetImpl;
import org.eclipse.birt.chart.model.donut.series.DonutSeries;
import org.eclipse.birt.chart.model.donut.series.impl.DonutSeriesImpl;
import org.eclipse.birt.chart.model.impl.ChartWithAxesImpl;
import org.eclipse.birt.chart.model.impl.ChartWithoutAxesImpl;
import org.eclipse.birt.chart.model.layout.Legend;
import org.eclipse.birt.chart.model.layout.Plot;
import org.eclipse.birt.chart.model.newtype.VennSeries;
import org.eclipse.birt.chart.model.newtype.impl.IntersectionColorType;
import org.eclipse.birt.chart.model.newtype.impl.VennSeriesImpl;
import org.eclipse.birt.chart.model.type.BarSeries;
import org.eclipse.birt.chart.model.type.PieSeries;
import org.eclipse.birt.chart.model.type.impl.BarSeriesImpl;
import org.eclipse.birt.chart.model.type.impl.PieSeriesImpl;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.ui.part.ViewPart;


public class NewChartsView extends ViewPart {

  public static final String ID = "org.eclipse.birt.newcharts.views.newchartsview";
  private TabFolder tabFolder;

  public void createPartControl( Composite parent ) {
    parent.setLayout( new FillLayout() );
    createChartFolder( parent );
  }

  private void createChartFolder( Composite parent ) {
    tabFolder = new TabFolder( parent, SWT.TOP );
    
    ChartCanvas donutChart = new ChartCanvas( tabFolder, SWT.NONE );
    donutChart.setChart( createDonutChart() );
    TabItem donutTabItem = new TabItem( tabFolder, SWT.NONE );
    donutTabItem.setText( "Donut Chart" );
    donutTabItem.setControl( donutChart );
    
    ChartCanvas pieChart = new ChartCanvas( tabFolder, SWT.NONE );
    pieChart.setChart( createPieChart() );
    TabItem pieTabItem = new TabItem( tabFolder, SWT.NONE );
    pieTabItem.setText( "Pie Chart" );
    pieTabItem.setControl( pieChart );
    
    ChartCanvas vennChart = new ChartCanvas( tabFolder, SWT.NONE );
    vennChart.setChart( createVennChart() );
    TabItem vennTabItem = new TabItem(tabFolder, SWT.NONE);
    vennTabItem.setText( "Venn Chart" );
    vennTabItem.setControl( vennChart );
    
    ChartCanvas barChart = new ChartCanvas( tabFolder, SWT.NONE );
    barChart.setChart( createBarChart() );
    TabItem barTabItem = new TabItem( tabFolder, SWT.NONE );
    barTabItem.setText( "Bar Chart" );
    barTabItem.setControl( barChart );
  }

  private Chart createVennChart() {
    
    ChartWithoutAxes chart = ChartWithoutAxesImpl.create();
    chart.setDimension( ChartDimension.TWO_DIMENSIONAL_LITERAL );
    
    chart.getTitle().getLabel().getCaption().setValue("three datasets with one intersection" );
    adjustFont( chart.getTitle().getLabel().getCaption().getFont() );
    
    Legend legend = chart.getLegend();
    legend.setItemType( LegendItemType.CATEGORIES_LITERAL );
    legend.setVisible( true );
    adjustFont( legend.getText().getFont() );
    
    TextDataSet categoryValues = TextDataSetImpl.create( new String[]{
      "amount1", "amount2","amount3"} );//$NON-NLS-1$ //$NON-NLS-2$
    
    
    NumberDataSet seriesOneValues = NumberDataSetImpl.create(
      new double[]{1,2,3,4,5});
    
    
//    VennDataSet allDataSets = VennDataSetImpl.create(seriesOneValues);
//    allDataSets.addDataSet( seriesTwoValues );
    
    // Base Series
    SeriesDefinition baseSeriesDefinition = SeriesDefinitionImpl.create();
    chart.getSeriesDefinitions().add( baseSeriesDefinition );
    
    final Fill[] fiaBase = {
      ColorDefinitionImpl.RED(),
      ColorDefinitionImpl.GREEN(),
      ColorDefinitionImpl.BLUE()
    };
    baseSeriesDefinition.getSeriesPalette().getEntries().clear();
    for( int i = 0; i < fiaBase.length; i++ ) {
      baseSeriesDefinition.getSeriesPalette().getEntries().add( fiaBase[ i ] );
    }

    Series seBase = SeriesImpl.create();
    seBase.setDataSet( categoryValues );
    baseSeriesDefinition.getSeries().add( seBase );
    
    
    // Add new Venn Series
    SeriesDefinition baseSeriesDefinition1 = SeriesDefinitionImpl.create();
    baseSeriesDefinition.getSeriesDefinitions().add( baseSeriesDefinition1 );
    
    VennSeries seVenn1 = ( VennSeries )VennSeriesImpl.create();
    seVenn1.getTitle().getCaption().setValue( "Super Label" );
    adjustFont( seVenn1.getTitle().getCaption().getFont() );
    seVenn1.getTitle().setVisible( false );
    seVenn1.setTitlePosition( Position.BELOW_LITERAL );
    seVenn1.setIntersectionColorType( IntersectionColorType.SUBTRACTIVE_COLOR );
//    seVenn1.getDataDefinition().add( seriesOneValues );
//    seVenn1.getDataDefinition().add( seriesTwoValues );
//
    seVenn1.setDataSet(seriesOneValues );
//    seVenn1.getDataPoint().
    
    
//    seVenn1.setDataSet( "dataset2", seriesTwoValues );
    
    
    
    baseSeriesDefinition1.getSeries().add( seVenn1 );
//    VennSeries seVenn2 = (VennSeries) VennSeriesImpl.create( );
//    seVenn2.setDataSet( seriesTwoValues );
//    
//    baseSeriesDefinition1.getSeries().add( seVenn2 );
    return chart;
  }

  private Chart createDonutChart() {
    ChartWithoutAxes chart = ChartWithoutAxesImpl.create();
    chart.setDimension( ChartDimension.THREE_DIMENSIONAL_LITERAL);
    Text caption = chart.getTitle().getLabel().getCaption();
    caption.setValue( "First base frame of new chart type" );
    adjustFont( caption.getFont() );
    Legend legend = chart.getLegend();
    legend.setItemType( LegendItemType.CATEGORIES_LITERAL );
    legend.setVisible( true );
    adjustFont( legend.getText().getFont() );
    TextDataSet categoryValues = TextDataSetImpl.create( new String[]{
      "category1", "category2", "category3", "category4"} );//$NON-NLS-1$ //$NON-NLS-2$
    NumberDataSet seriesOneValues = NumberDataSetImpl.create( new double[]{
      5,5,5,16
    } );
    // Base Series
    SeriesDefinition sd = SeriesDefinitionImpl.create();
    sd.getSeriesPalette().shift( 0 );
    chart.getSeriesDefinitions().add( sd );

    Series seBase = SeriesImpl.create();
    seBase.setDataSet( categoryValues );
    sd.getSeries().add( seBase );
    // new colors
    final Fill[] fiaBase = {
      ColorDefinitionImpl.GREY(),
      ColorDefinitionImpl.RED(),
      ColorDefinitionImpl.BLACK(),
      ColorDefinitionImpl.ORANGE()
    };
    sd.getSeriesPalette().getEntries().clear();
    for( int i = 0; i < fiaBase.length; i++ ) {
      sd.getSeriesPalette().getEntries().add( fiaBase[ i ] );
    }
    
    // Add new Donut Series
    SeriesDefinition sdCity = SeriesDefinitionImpl.create();
    sd.getSeriesDefinitions().add( sdCity );
    DonutSeries seDonut = ( DonutSeries )DonutSeriesImpl.create();
    seDonut.setDataSet( seriesOneValues );
   
    // Test properties
    seDonut.getLabel().setVisible( true );
//    seDonut.getLabel().unsetVisible();
//    seDonut.setLabelPosition( Position.ABOVE_LITERAL );
    seDonut.setRotation( 0 );
    seDonut.setExplosion(10 );
    seDonut.setThickness( 30 );
    seDonut.getLeaderLineAttributes().setVisible( true );
    seDonut.getLeaderLineAttributes().setThickness( 1 );
    seDonut.getLeaderLineAttributes().setColor( ColorDefinitionImpl.BLACK() );
    seDonut.getLeaderLineAttributes().setStyle( LineStyle.SOLID_LITERAL );
    seDonut.setLeaderLineLength( 5 );
    
    sdCity.getSeries().add( seDonut );
    return chart;
  }

  private Chart createPieChart() {
    ChartWithoutAxes chart = ChartWithoutAxesImpl.create();
    chart.setDimension( ChartDimension.TWO_DIMENSIONAL_WITH_DEPTH_LITERAL );
    Text caption = chart.getTitle().getLabel().getCaption();
    caption.setValue( "Percentage of charts looking like Pac-man" );
    adjustFont( caption.getFont() );
    Legend legend = chart.getLegend();
    legend.setItemType( LegendItemType.CATEGORIES_LITERAL );
    legend.setVisible( true );
    adjustFont( legend.getText().getFont() );
    TextDataSet categoryValues = TextDataSetImpl.create( new String[]{
      "Pac-man", "not a Pac-man"} );//$NON-NLS-1$ //$NON-NLS-2$
    NumberDataSet seriesOneValues = NumberDataSetImpl.create( new double[]{
      75, 25
    } );
    // 1) SeriesDefinition
    SeriesDefinition sd = SeriesDefinitionImpl.create();
    // 2) Base Series
    Series seCategory = SeriesImpl.create();
    seCategory.setDataSet( categoryValues );
    chart.getSeriesDefinitions().add( sd );
    sd.getSeriesPalette().shift( 0 );
    sd.getSeries().add( seCategory );
    // new colors
    final Fill[] fiaBase = {
      ColorDefinitionImpl.ORANGE(), ColorDefinitionImpl.GREY()
    };
    sd.getSeriesPalette().getEntries().clear();
    for( int i = 0; i < fiaBase.length; i++ ) {
      sd.getSeriesPalette().getEntries().add( fiaBase[ i ] );
    }
    // 3) Orthogonal Series
    PieSeries sePie = ( PieSeries )PieSeriesImpl.create();
    sePie.getTitle().getCaption().setColor( ColorDefinitionImpl.BLACK() );
    sePie.setTitlePosition( Position.BELOW_LITERAL );
    sePie.getTitle().setVisible( true );
    sePie.setDataSet( seriesOneValues );
    sePie.setExplosion( 0 );
    sePie.setRotation( 0 );
    sePie.setRatio(0 );
    sePie.getLabel().setVisible( true );
    sePie.getLabel().getCaption().setValue( "LABEL" );
    sePie.setLabelPosition( Position.OUTSIDE_LITERAL );
    sePie.getLeaderLineAttributes().setVisible( true );
    sePie.getLeaderLineAttributes().setThickness( 3 );
    sePie.getLeaderLineAttributes().setColor( ColorDefinitionImpl.CYAN() );
    sePie.getLeaderLineAttributes().setStyle( LineStyle.DOTTED_LITERAL );
    
    SeriesDefinition sdCity = SeriesDefinitionImpl.create();
    sd.getSeriesDefinitions().add( sdCity );
    sdCity.getSeries().add( sePie );
    return chart;
  }

  private Chart createBarChart() {
    ChartWithAxes chart = ChartWithAxesImpl.create();
    chart.setDimension( ChartDimension.TWO_DIMENSIONAL_LITERAL );
    Plot plot = chart.getPlot();
    plot.setBackground( ColorDefinitionImpl.WHITE() );
    plot.getClientArea().setBackground( ColorDefinitionImpl.WHITE() );
    Legend legend = chart.getLegend();
    legend.setItemType( LegendItemType.CATEGORIES_LITERAL );
    legend.setVisible( true );
    adjustFont( legend.getText().getFont() );
    Text caption = chart.getTitle().getLabel().getCaption();
    caption.setValue( "Distribution of Chart Column Heights" );
    adjustFont( caption.getFont() );
    Axis xAxis = ( ( ChartWithAxes )chart ).getPrimaryBaseAxes()[ 0 ];
    xAxis.getTitle().setVisible( true );
    xAxis.getTitle().getCaption().setValue( "" );
    Axis yAxis = ( ( ChartWithAxes )chart ).getPrimaryOrthogonalAxis( xAxis );
    yAxis.getTitle().setVisible( true );
    yAxis.getTitle().getCaption().setValue( "" );
    yAxis.getScale().setStep( 1.0 );
    TextDataSet categoryValues = TextDataSetImpl.create( new String[]{
      "short", "medium", "tall"
    } );
    Series seCategory = SeriesImpl.create();
    seCategory.setDataSet( categoryValues );
    adjustFont( seCategory.getLabel().getCaption().getFont() );
    SeriesDefinition sdX = SeriesDefinitionImpl.create();
    sdX.getSeriesPalette().shift( 1 );
    xAxis.getSeriesDefinitions().add( sdX );
    sdX.getSeries().add( seCategory );
    NumberDataSet orthoValuesDataSet1 = NumberDataSetImpl.create( new double[]{
      1, 2, 3
    } );
    BarSeries bs1 = ( BarSeries )BarSeriesImpl.create();
    bs1.setDataSet( orthoValuesDataSet1 );
    adjustFont( bs1.getLabel().getCaption().getFont() );
    SeriesDefinition sdY = SeriesDefinitionImpl.create();
    yAxis.getSeriesDefinitions().add( sdY );
    sdY.getSeries().add( bs1 );
    return chart;
  }

  private void adjustFont( FontDefinition font ) {
    font.setSize(8);
    font.setName( "Verdana" );
  }

  public void setFocus() {
    tabFolder.setFocus();
  }
}
