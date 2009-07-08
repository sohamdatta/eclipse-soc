package org.mati.zest.examples;

import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.zest.layout.algorithms.SpaceTreeLayoutAlgorithm;
import org.mati.zest.core.widgets.Graph;
import org.mati.zest.core.widgets.GraphConnection;
import org.mati.zest.core.widgets.GraphItem;
import org.mati.zest.core.widgets.GraphNode;

public class SpaceTreeExample {
	
	public static void main(String[] args) {
		Display d = new Display();
		Shell shell = new Shell(d);
		shell.setText("GraphSnippet1");
		shell.setLayout(new FillLayout());
		shell.setSize(400, 400);

		Graph g = new Graph(shell, SWT.NONE);
		
		createTree(g, "!", 4, 4);
		g.setLayoutAlgorithm(new SpaceTreeLayoutAlgorithm(), true);
		
		hookMenu(g);

		shell.open();
		while (!shell.isDisposed()) {
			while (!d.readAndDispatch()) {
				d.sleep();
			}
		}
	}

	private static GraphNode createTree(Graph g, String rootTitle, int depth, int breadth) {
		GraphNode root = new GraphNode(g, SWT.NONE, rootTitle);
		if (depth > 0) {
			for (int i = 0; i < breadth; i++) {
				GraphNode child = createTree(g, rootTitle + i, depth-1, breadth);
				new GraphConnection(g, SWT.NONE, root, child);
			}
		}
		return root;
	}

	private static void hookMenu(final Graph g) {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		
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
