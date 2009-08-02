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


public class GridSnippet0 {
	public static void main(String[] args){
		Display display = new Display ();
	    Shell shell = new Shell (display);
	    shell.setLayout(new FillLayout());

	    final Grid grid = new Grid(shell,SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
	    grid.setHeaderVisible(true);
	    grid.setCellSelectionEnabled(false);
	    //Note: if the grid(or a cell) has set cell selection enabled,
	    //the editor of its corresponding cell will not work.
	    //grid.setCellSelectionEnabled(true);
	    
	    GridColumn column1 = new GridColumn(grid,SWT.NONE);
	    column1.setText("Column 1");
	    column1.setWidth(100);
	    
	    GridColumn column2 = new GridColumn(grid,SWT.NONE);
	    column2.setText("Column 2");
	    column2.setWidth(100);
	    
	    GridItem item1 = new GridItem(grid,SWT.NONE);
	    item1.setText(0,"aaaaaaa");
	    item1.setText(1, "bbbbbbbb");
	    
	    GridItem item2 = new GridItem(grid,SWT.NONE);
	    item2.setText(0,"ccccc");
	    item2.setText(1, "dddddd");
	    
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
