package org.mati.zest.examples;

import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.zest.layout.algorithms.SpaceTreeLayoutAlgorithm;
import org.mati.zest.core.widgets.ExperimentalSubgraphLayout;
import org.mati.zest.core.widgets.Graph;
import org.mati.zest.core.widgets.GraphConnection;
import org.mati.zest.core.widgets.GraphItem;
import org.mati.zest.core.widgets.GraphNode;

/**
 * This example shows how SpringLayoutAlgorithm reacts to graph structure
 * related events, automatically rebuilding trees every time a new connection is
 * added.
 */
public class SpaceTreeBuilding {
	private static GraphNode parentNode = null;
	private static GraphNode childNode = null;

	public static void main(String[] args) {
		Display d = new Display();
		Shell shell = new Shell(d);
		shell.setText("GraphSnippet1");
		shell.setLayout(new FillLayout());
		shell.setSize(400, 400);

		final Graph g = new Graph(shell, SWT.NONE);
		
		hookMenu(g);

		g.setLayoutAlgorithm(new SpaceTreeLayoutAlgorithm(), false);
		
		g.setSubgraphFactory(ExperimentalSubgraphLayout.FACTORY);

		for (int i = 0; i < 20; i++) {
			GraphNode graphNode = new GraphNode(g, SWT.NONE);
			graphNode.setText("" + i);
		}

		shell.open();
		while (!shell.isDisposed()) {
			while (!d.readAndDispatch()) {
				d.sleep();
			}
		}
	}

	private static void tryToAddConnection(Graph g) {
		if (parentNode != null && childNode != null) {
			new GraphConnection(g, SWT.NONE, parentNode, childNode);
			parentNode = childNode = null;
		}
	}

	private static void hookMenu(final Graph g) {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		
		Action parentAction = new Action() {
			public void run() {
				List selection = g.getSelection();
				if (!selection.isEmpty()) {
					GraphNode selected = (GraphNode) selection.get(0);
					parentNode = selected;
					tryToAddConnection(g);
				}
			}
		};
		parentAction.setText("use as parent");
		menuMgr.add(parentAction);

		Action childAction = new Action() {
			public void run() {
				List selection = g.getSelection();
				if (!selection.isEmpty()) {
					GraphNode selected = (GraphNode) selection.get(0);
					childNode = selected;
					tryToAddConnection(g);
				}
			}
		};
		childAction.setText("use as child");
		menuMgr.add(childAction);

		Action expandAction = new Action() {
			public void run() {
				List selection = g.getSelection();
				if (!selection.isEmpty()) {
					GraphNode selected = (GraphNode) selection.get(0);
					g.setExpanded((GraphNode) selected, true);
				}
			}
		};
		expandAction.setText("expand");
		menuMgr.add(expandAction);

		Action collapseAction = new Action() {
			public void run() {
				List selection = g.getSelection();
				if (!selection.isEmpty()) {
					GraphItem selected = (GraphItem) selection.get(0);
					g.setExpanded((GraphNode) selected, false);
				}
			}
		};
		collapseAction.setText("collapse");
		menuMgr.add(collapseAction);

		g.setMenu(menuMgr.createContextMenu(g));
	}
}
