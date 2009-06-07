package org.eclipse.birt.newcharts.views;


import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.ui.part.ViewPart;


public class NewChartView extends ViewPart {

	private TabFolder tabFolder;

	@Override
	public void createPartControl(Composite parent) {
		tabFolder = new TabFolder(parent,SWT.None);
		
		TabItem donutTab = new TabItem(tabFolder,SWT.TOP);
		donutTab.setText("Donut Tab");
		
		Label tbd = new Label(parent,SWT.TOP);
		tbd.setText("Donut chart not implemented, yet");

	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub
		
	}

}