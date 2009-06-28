package org.mati.zest.examples;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.mati.zest.core.widgets.Graph;
import org.mati.zest.core.widgets.GraphConnection;
import org.mati.zest.core.widgets.GraphNode;
import org.mati.zest.layout.algorithms.TreeLayoutAlgorithm;

public class TreeLayoutExample {
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// Create the shell
		Display d = new Display();
		Shell shell = new Shell(d);
		shell.setText("GraphSnippet1");
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 10;
		shell.setLayout(gridLayout);
		shell.setSize(500, 500);

		final Graph g = new Graph(shell, SWT.NONE);
		g.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 10, 10));
		g.setSize(500, 500);
		GraphNode root = new GraphNode(g, SWT.NONE, "Root");
		for (int i = 0; i < 3; i++) {
			GraphNode n = new GraphNode(g, SWT.NONE, "1 - " + i);
			for (int j = 0; j < 3; j++) {
				GraphNode n2 = new GraphNode(g, SWT.NONE, "2 - " + j);
				new GraphConnection(g, SWT.NONE, n, n2).setWeight(-1);
			}
			new GraphConnection(g, SWT.NONE, root, n);
		}

		final TreeLayoutAlgorithm algorithm = new TreeLayoutAlgorithm();
		g.setLayoutAlgorithm(algorithm, false);

		final Button buttonTopDown = new Button(shell, SWT.FLAT);
		buttonTopDown.setText("TOP_DOWN");

		final Button buttonBottomUp = new Button(shell, SWT.FLAT);
		buttonBottomUp.setText("BOTTOM_UP");
		buttonBottomUp.setLayoutData(new GridData());

		final Button buttonLeftRight = new Button(shell, SWT.FLAT);
		buttonLeftRight.setText("LEFT_RIGHT");

		final Button buttonRightLeft = new Button(shell, SWT.FLAT);
		buttonRightLeft.setText("RIGHT_LEFT");

		SelectionAdapter buttonListener = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (e.widget == buttonTopDown)
					algorithm.setDirection(TreeLayoutAlgorithm.TOP_DOWN);
				if (e.widget == buttonBottomUp)
					algorithm.setDirection(TreeLayoutAlgorithm.BOTTOM_UP);
				if (e.widget == buttonLeftRight)
					algorithm.setDirection(TreeLayoutAlgorithm.LEFT_RIGHT);
				if (e.widget == buttonRightLeft)
					algorithm.setDirection(TreeLayoutAlgorithm.RIGHT_LEFT);

				g.applyLayout();
			}
		};
		buttonTopDown.addSelectionListener(buttonListener);
		buttonBottomUp.addSelectionListener(buttonListener);
		buttonLeftRight.addSelectionListener(buttonListener);
		buttonRightLeft.addSelectionListener(buttonListener);

		shell.open();
		while (!shell.isDisposed()) {
			while (!d.readAndDispatch()) {
				d.sleep();
			}
		}
	}
}
