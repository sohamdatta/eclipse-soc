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
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.mati.zest.core.widgets.Graph;
import org.mati.zest.core.widgets.GraphConnection;
import org.mati.zest.core.widgets.GraphNode;
import org.mati.zest.layout.algorithms.SpringLayoutAlgorithm;

/**
 * This snippet shows how to use the findFigureAt to get the figure under the
 * mouse
 * 
 * @author Ian Bull
 * 
 */
public class LayoutExample {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// Create the shell
		Display d = new Display();
		Shell shell = new Shell(d);
		shell.setText("GraphSnippet1");
		shell.setLayout(new FillLayout());
		shell.setSize(500, 500);

		final Graph g = new Graph(shell, SWT.NONE);
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

		g.setLayoutAlgorithm(springLayoutAlgorithm, true);
		shell.open();
		while (!shell.isDisposed()) {
			while (!d.readAndDispatch()) {
				d.sleep();
			}
		}
	}

}
