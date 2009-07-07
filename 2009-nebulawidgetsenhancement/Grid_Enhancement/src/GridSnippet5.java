
 

import org.eclipse.nebula.widgets.grid.Grid;
import org.eclipse.nebula.widgets.grid.GridColumn;
import org.eclipse.nebula.widgets.grid.GridColumnGroup;
import org.eclipse.nebula.widgets.grid.GridItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/*
 * Create a grid with a collapsible column group where the third column is a 
 * 'detail' only column (i.e. it will show/hide as the group is expanded/collapsed).
 *
 * For a list of all Nebula Grid example snippets see
 * http://www.eclipse.org/nebula/widgets/grid/snippets.php
 */
public class GridSnippet5 {

public static void main (String [] args) {
    Display display = new Display ();
    Shell shell = new Shell (display);
    shell.setLayout(new FillLayout());

    Grid grid = new Grid(shell,SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
    grid.setHeaderVisible(true);
    GridColumn column = new GridColumn(grid,SWT.NONE);
    column.setText("Column 1");
    column.setWidth(100);
    GridColumnGroup columnGroup = new GridColumnGroup(grid,SWT.TOGGLE);
    columnGroup.setText("Column Group");
    GridColumn column2 = new GridColumn(columnGroup,SWT.NONE);
    column2.setText("Column 2");
    column2.setWidth(60);
    GridColumn column3 = new GridColumn(columnGroup,SWT.NONE);
    column3.setText("Column 3");
    column3.setWidth(60);
    column3.setSummary(false);
    column3.setDetail(true);
    GridItem item1 = new GridItem(grid,SWT.NONE);
    item1.setText("First Item");
    item1.setText(1,"abc");
    item1.setColumnSpan(1, 1);
    GridItem item2 = new GridItem(grid,SWT.NONE);
    item2.setText("Second Item");
    item2.setText(2,"def");
    item2.setAreaSpan(0, 2, 1);
    GridItem item3 = new GridItem(grid,SWT.NONE);
    item3.setText("Third Item");
    item3.setText(1,"xyz");
    
    shell.setSize(250,250);
    shell.open ();
    while (!shell.isDisposed()) {
        if (!display.readAndDispatch ()) display.sleep ();
    }
    display.dispose ();
}
}