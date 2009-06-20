package org.eclipse.birt.newcharts.views;

import org.eclipse.birt.chart.model.Chart;
import org.eclipse.birt.chart.model.ChartWithoutAxes;
import org.eclipse.birt.chart.model.attribute.ChartDimension;
import org.eclipse.birt.chart.model.attribute.Fill;
import org.eclipse.birt.chart.model.attribute.FontDefinition;
import org.eclipse.birt.chart.model.attribute.LegendItemType;
import org.eclipse.birt.chart.model.attribute.Text;
import org.eclipse.birt.chart.model.attribute.impl.ColorDefinitionImpl;
import org.eclipse.birt.chart.model.component.Series;
import org.eclipse.birt.chart.model.component.impl.SeriesImpl;
import org.eclipse.birt.chart.model.data.NumberDataSet;
import org.eclipse.birt.chart.model.data.SeriesDefinition;
import org.eclipse.birt.chart.model.data.TextDataSet;
import org.eclipse.birt.chart.model.data.impl.NumberDataSetImpl;
import org.eclipse.birt.chart.model.data.impl.SeriesDefinitionImpl;
import org.eclipse.birt.chart.model.data.impl.TextDataSetImpl;
import org.eclipse.birt.chart.model.impl.ChartWithoutAxesImpl;
import org.eclipse.birt.chart.model.layout.Legend;
import org.eclipse.birt.chart.newtype.DonutSeries;
import org.eclipse.birt.chart.newtype.impl.DonutSeriesImpl;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.ui.part.ViewPart;

public class NewChartView extends ViewPart {

	public static final String ID = "org.eclipse.birt.newcharts";
	private TabFolder tabFolder;

	public void createPartControl(Composite parent) {
		parent.setLayout(new FillLayout());
		createChartFolder(parent);
	}

	private void createChartFolder(Composite parent) {
		tabFolder = new TabFolder(parent, SWT.TOP);
		ChartCanvas donutChart = new ChartCanvas(tabFolder, SWT.NONE);
		donutChart.setChart(createDonutChart());
	}

	private Chart createDonutChart() {
		ChartWithoutAxes chart = ChartWithoutAxesImpl.create();
		chart.setDimension(ChartDimension.TWO_DIMENSIONAL_LITERAL);
		Text caption = chart.getTitle().getLabel().getCaption();
		caption.setValue("Percentage of charts looking like Pac-man");
		adjustFont(caption.getFont());
		Legend legend = chart.getLegend();
		legend.setItemType(LegendItemType.CATEGORIES_LITERAL);
		legend.setVisible(true);
		adjustFont(legend.getText().getFont());
		TextDataSet categoryValues = TextDataSetImpl.create(new String[] {
				"Pac-man", "not a Pac-man" });//$NON-NLS-1$ //$NON-NLS-2$
		NumberDataSet seriesOneValues = NumberDataSetImpl.create(new double[] {
				80, 20 });
		// Base Series
		Series seCategory = SeriesImpl.create();
		seCategory.setDataSet(categoryValues);
		SeriesDefinition sd = SeriesDefinitionImpl.create();
		chart.getSeriesDefinitions().add(sd);
		sd.getSeriesPalette().shift(0);
		sd.getSeries().add(seCategory);
		// new colors
		final Fill[] fiaBase = { ColorDefinitionImpl.ORANGE(),
				ColorDefinitionImpl.GREY() };
		sd.getSeriesPalette().getEntries().clear();
		for (int i = 0; i < fiaBase.length; i++) {
			sd.getSeriesPalette().getEntries().add(fiaBase[i]);
		}
		
		// Add new Donut Series
		DonutSeries seDonut = (DonutSeries) DonutSeriesImpl.create();
		seDonut.setDataSet(seriesOneValues);
		seDonut.getLabel().setVisible(false);
		SeriesDefinition sdCity = SeriesDefinitionImpl.create();
		sd.getSeriesDefinitions().add(sdCity);
		sdCity.getSeries().add(seDonut);
		return chart;
	}

	@Override
	public void setFocus() {

	}

	private void adjustFont(FontDefinition font) {
		font.setSize(8);
		font.setName("Verdana");
	}

}