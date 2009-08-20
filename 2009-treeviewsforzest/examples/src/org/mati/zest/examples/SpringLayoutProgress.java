/*******************************************************************************
 * Copyright 2005-2007, CHISEL Group, University of Victoria, Victoria, BC,
 * Canada. All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: The Chisel Group, University of Victoria
 ******************************************************************************/
package org.mati.zest.examples;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.zest.core.widgets.Graph;
import org.eclipse.zest.core.widgets.GraphConnection;
import org.eclipse.zest.core.widgets.GraphNode;
import org.eclipse.zest.layouts.algorithms.SpringLayoutAlgorithm;

/**
 * 
 */
public class SpringLayoutProgress {
	static Runnable r = null;
			
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// Create the shell
		Display d = new Display();
		Shell shell = new Shell(d);
		shell.setText("GraphSnippet1");
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 5;
		shell.setLayout(gridLayout);
		shell.setSize(500, 500);

		final Graph g = new Graph(shell, SWT.NONE);
		g.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 5, 5));
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


		final SpringLayoutAlgorithm springLayoutAlgorithm = new SpringLayoutAlgorithm();

		g.setLayoutAlgorithm(springLayoutAlgorithm, false);

		Button b = new Button(shell, SWT.FLAT);
		b.setText("step");

		final Label label = new Label(shell, SWT.LEFT);
		label.setText("<--click");
		label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		b.addSelectionListener(new SelectionAdapter() {
			int steps = 0;
			public void widgetSelected(SelectionEvent e) {
				
				r = new Runnable() {
					public void run() {
						springLayoutAlgorithm.performOneIteration();
						g.redraw();
						label.setText("steps: " + ++steps);
						try {
							Thread.sleep(50);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						Display.getCurrent().asyncExec(r);
					}
				};
				Display.getCurrent().asyncExec(r);

			}
		});

		shell.open();
		while (!shell.isDisposed()) {
			while (!d.readAndDispatch()) {
				d.sleep();
			}
		}
	}

}
