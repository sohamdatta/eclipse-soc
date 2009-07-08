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

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.zest.core.widgets.ZestStyles;
import org.eclipse.zest.layout.algorithms.TreeLayoutAlgorithm;
import org.eclipse.zest.layout.interfaces.Filter;
import org.mati.zest.core.widgets.Graph;
import org.mati.zest.core.widgets.GraphConnection;
import org.mati.zest.core.widgets.GraphNode;

/**
 * This snippet shows how to filter elements in the layout.  The Data on the tree
 * connections are set to "False", meaning they won't be filtered.  
 * 
 * @author Ian Bull
 * 
 */
public class FilterGraphSnippet {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setText("GraphSnippet8");
		shell.setLayout(new FillLayout());
		shell.setSize(400, 400);

		final Graph graph = new Graph(shell, SWT.NONE);

		GraphNode a = new GraphNode(graph, SWT.NONE, "Root");
		GraphNode b = new GraphNode(graph, SWT.NONE, "B");
		GraphNode c = new GraphNode(graph, SWT.NONE, "C");
		GraphNode d = new GraphNode(graph, SWT.NONE, "D");
		GraphNode e = new GraphNode(graph, SWT.NONE, "E");
		GraphNode f = new GraphNode(graph, SWT.NONE, "F");
		GraphNode g = new GraphNode(graph, SWT.NONE, "G");
		GraphNode h = new GraphNode(graph, SWT.NONE, "H");
		GraphConnection connection = new GraphConnection(graph, ZestStyles.CONNECTIONS_DIRECTED, a, b);
		connection.setData(Boolean.TRUE);
		connection = new GraphConnection(graph, ZestStyles.CONNECTIONS_DIRECTED, a, c);
		connection.setData(Boolean.TRUE);
		connection = new GraphConnection(graph, ZestStyles.CONNECTIONS_DIRECTED, a, c);
		connection.setData(Boolean.TRUE);
		connection = new GraphConnection(graph, ZestStyles.CONNECTIONS_DIRECTED, a, d);
		connection.setData(Boolean.TRUE);
		connection = new GraphConnection(graph, ZestStyles.CONNECTIONS_DIRECTED, b, e);
		connection.setData(Boolean.FALSE);
		connection = new GraphConnection(graph, ZestStyles.CONNECTIONS_DIRECTED, b, f);
		connection.setData(Boolean.FALSE);
		connection = new GraphConnection(graph, ZestStyles.CONNECTIONS_DIRECTED, c, g);
		connection.setData(Boolean.FALSE);
		connection = new GraphConnection(graph, ZestStyles.CONNECTIONS_DIRECTED, d, h);
		connection.setData(Boolean.FALSE);
		
		connection = new GraphConnection(graph, ZestStyles.CONNECTIONS_DIRECTED, b, c);
		connection.setLineColor(ColorConstants.red);
		connection.setLineWidth(3);
		connection = new GraphConnection(graph, ZestStyles.CONNECTIONS_DIRECTED, c, d);
		connection.setLineColor(ColorConstants.red);
		connection.setLineWidth(3);
		connection = new GraphConnection(graph, ZestStyles.CONNECTIONS_DIRECTED, e, f);
		connection.setLineColor(ColorConstants.red);
		connection.setLineWidth(3);
		connection = new GraphConnection(graph, ZestStyles.CONNECTIONS_DIRECTED, f, g);
		connection.setLineColor(ColorConstants.red);
		connection.setLineWidth(3);
		
		connection = new GraphConnection(graph, ZestStyles.CONNECTIONS_DIRECTED, h, e);
		connection.setLineColor(ColorConstants.red);
		connection.setLineWidth(3);
		
		TreeLayoutAlgorithm treeLayoutAlgorithm = new TreeLayoutAlgorithm();
		Filter filter = new Filter() {
			public boolean isObjectFiltered(Object item) {
				if  (item instanceof GraphConnection ) {
					GraphConnection connection = (GraphConnection) item;
					Object data = connection.getData();
					if ( data != null && data instanceof Boolean ) {
						// If the data is false, don't filter, otherwise, filter.
						return ((Boolean)data).booleanValue();
					}
					return true;
				}
				return false;
			}
		};
		graph.addLayoutFilter(filter);
		graph.setLayoutAlgorithm(treeLayoutAlgorithm, true);
		

		shell.open();
		while (!shell.isDisposed()) {
			while (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

}
