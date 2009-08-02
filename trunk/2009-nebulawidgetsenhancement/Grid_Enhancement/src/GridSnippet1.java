
 

import org.eclipse.nebula.widgets.grid.Grid;
import org.eclipse.nebula.widgets.grid.GridColumn;
import org.eclipse.nebula.widgets.grid.GridEditor;
import org.eclipse.nebula.widgets.grid.GridItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/*
 * Create a simple hierarchical grid.
 *
 * For a list of all Nebula Grid example snippets see
 * http://www.eclipse.org/nebula/widgets/grid/snippets.php
 */
public class GridSnippet1 {

public static void main (String [] args) {
    Display display = new Display ();
    Shell shell = new Shell (display);
    shell.setLayout(new FillLayout());

    final Grid grid = new Grid(shell,SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
    grid.setHeaderVisible(true);
    grid.setCellSelectionEnabled(true);
    GridColumn column = new GridColumn(grid,SWT.NONE);
    column.setTree(true);
    column.setText("Column 1");
    column.setWidth(100);
    GridItem item1 = new GridItem(grid,SWT.NONE);
    item1.setText("Root Item");
    GridItem item2 = new GridItem(item1,SWT.NONE);
    item2.setText("Second item");
    GridItem item3 = new GridItem(item2,SWT.NONE);
    item3.setText("Third Item");
    GridItem item4 = new GridItem(item1,SWT.NONE,-1);
    item4.setText("test");
    
    shell.setSize(200,200);
    shell.open ();
    
    final GridEditor editor = new GridEditor(grid);
    editor.grabHorizontal = true;

    final int COLUMN = 1;
    grid.addSelectionListener(new SelectionAdapter() {

        @Override
        public void widgetSelected(SelectionEvent e) {
        	System.out.println(e);
            Control oldEditor = editor.getEditor();
            if (oldEditor != null)
                oldEditor.dispose();

            GridItem item = (GridItem) e.item;
            if (item == null)
                return;

            Text newEditor = new Text(grid, SWT.None);
            newEditor.setText(item.getText(COLUMN));
            newEditor.addModifyListener(new ModifyListener() {
                public void modifyText(ModifyEvent e) {
                    Text text = (Text) editor.getEditor();
                    editor.getItem().setText(COLUMN, text.getText());
                }
            });
            newEditor.selectAll();
            newEditor.setFocus();
            editor.setEditor(newEditor,item,COLUMN);

        }
    });

    while (!shell.isDisposed()) {
        if (!display.readAndDispatch ()) display.sleep ();
    }
    display.dispose ();
}
}