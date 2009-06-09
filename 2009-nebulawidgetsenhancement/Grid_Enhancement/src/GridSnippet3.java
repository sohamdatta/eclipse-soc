
 

import org.eclipse.nebula.widgets.grid.Grid;
import org.eclipse.nebula.widgets.grid.GridColumn;
import org.eclipse.nebula.widgets.grid.GridItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/*
 * Create a grid with a checkbox in the second column.
 *
 * For a list of all Nebula Grid example snippets see
 * http://www.eclipse.org/nebula/widgets/grid/snippets.php
 */
public class GridSnippet3 {

public static void main (String [] args) {
    Display display = new Display ();
    Shell shell = new Shell (display);
    shell.setLayout(new FillLayout());

    Grid grid = new Grid(shell,SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
    grid.setHeaderVisible(true);
    GridColumn column = new GridColumn(grid,SWT.NONE);
    column.setText("Column 1");
    column.setWidth(100);
    GridColumn column2 = new GridColumn(grid,SWT.CHECK | SWT.CENTER);
    column2.setText("Column 2");
    column2.setWidth(100);
    GridItem item1 = new GridItem(grid,SWT.NONE);
    item1.setText("First Item");
    item1.setChecked(1,true);
    GridItem item2 = new GridItem(grid,SWT.NONE);
    item2.setText("Second Item");
    GridItem item3 = new GridItem(grid,SWT.NONE);
    item3.setText("Third Item");
    
    shell.setSize(250,250);
    shell.open ();
    while (!shell.isDisposed()) {
        if (!display.readAndDispatch ()) display.sleep ();
    }
    display.dispose ();
}
} 