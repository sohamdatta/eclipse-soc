package org.mati.zest.layout.algorithms;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.graph.DirectedGraph;
import org.eclipse.draw2d.graph.DirectedGraphLayout;
import org.eclipse.draw2d.graph.Edge;
import org.eclipse.draw2d.graph.Node;
import org.mati.zest.layout.interfaces.ConnectionLayout;
import org.mati.zest.layout.interfaces.EntityLayout;
import org.mati.zest.layout.interfaces.LayoutAlgorithm;
import org.mati.zest.layout.interfaces.LayoutContext;

public class DirectedGraphLayoutAlgorithm implements LayoutAlgorithm {
	
	class ExtendedDirectedGraphLayout extends DirectedGraphLayout {

		public void visit(DirectedGraph graph) {
			Field field;
			try {
				field = DirectedGraphLayout.class.getDeclaredField("steps");
				field.setAccessible(true);
				Object object = field.get(this);
				List steps = (List) object;
				steps.remove(10);
				steps.remove(9);
				steps.remove(8);
				steps.remove(2);
				field.setAccessible(false);
				super.visit(graph);
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (NoSuchFieldException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}

	public static final int HORIZONTAL = 1;

	public static final int VERTICAL = 2;

	private int orientation = VERTICAL;

	private LayoutContext context;

	public DirectedGraphLayoutAlgorithm() {
	}

	public DirectedGraphLayoutAlgorithm(int orientation) {
		if (orientation == VERTICAL)
			this.orientation = orientation;
	}

	public int getOrientation() {
		return orientation;
	}

	public void setOrientation(int orientation) {
		if (orientation == HORIZONTAL || orientation == VERTICAL)
			this.orientation = orientation;
	}

	public void applyLayout() {
		HashMap mapping = new HashMap();
		DirectedGraph graph = new DirectedGraph();
		EntityLayout[] entities = context.getEntities();
		for (int i = 0; i < entities.length; i++) {
			Node node = new Node(entities[i]);
			node.setSize(new Dimension(10, 10));
			mapping.put(entities[i], node);
			graph.nodes.add(node);
		}
		ConnectionLayout[] connections = context.getConnections();
		for (int i = 0; i < connections.length; i++) {
			Node source = (Node) mapping.get(connections[i].getSource());
			Node dest = (Node) mapping.get(connections[i].getTarget());
			Edge edge = new Edge(connections[i], source, dest);
			graph.edges.add(edge);
		}
		DirectedGraphLayout directedGraphLayout = new ExtendedDirectedGraphLayout();
		directedGraphLayout.visit(graph);

		for (Iterator iterator = graph.nodes.iterator(); iterator.hasNext();) {
			Node node = (Node) iterator.next();
			EntityLayout entity = (EntityLayout) node.data;
			if (orientation == VERTICAL) {
				entity.setLocation(node.x, node.y);
			} else {
				entity.setLocation(node.y, node.x);
			}
		}
	}

	public void setLayoutContext(LayoutContext context) {
		this.context = context;
	}

}
