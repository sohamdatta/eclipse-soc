package org.mati.zest.core.widgets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.zest.layouts.dataStructures.DisplayIndependentRectangle;

abstract class NodeContainerAdapter {
	private static class GraphAdapter extends NodeContainerAdapter {
		private Graph graph;

		private GraphAdapter(Graph graph) {
			graph.getStyle();
			this.graph = graph;
		}

		public Widget getAdaptee() {
			return graph;
		}

		public Graph getGraph() {
			return graph;
		}

		public List getNodes() {
			return graph.getNodes();
		}

		public List getConnections() {
			return graph.getConnections();
		}

		public void addNode(GraphNode graphNode) {
			graph.addNode(graphNode);
		}

		public void highlightNode(GraphNode graphNode) {
			graph.highlightNode(graphNode);
		}

		public void unhighlightNode(GraphNode graphNode) {
			graph.highlightNode(graphNode);
		}

		public int getItemType() {
			return GraphItem.GRAPH;
		}

		public void addConnectionFigure(PolylineConnection figure) {
			/* do nothing */
		}

		public DisplayIndependentRectangle getLayoutBounds() {
			Dimension preferredSize = graph.getPreferredSize();
			return new DisplayIndependentRectangle(0, 0, preferredSize.width, preferredSize.height);
		}
	}

	private static class GraphContainerAdapter extends NodeContainerAdapter {
		private GraphContainer container;

		private GraphContainerAdapter(GraphContainer container) {
			this.container = container;
		}

		public Widget getAdaptee() {
			return container;
		}

		public Graph getGraph() {
			return container.getGraph();
		}

		public List getNodes() {
			return container.getNodes();
		}

		public List getConnections() {
			List connections = container.getGraph().getConnections();
			List result = new ArrayList();
			for (Iterator iterator = connections.iterator(); iterator.hasNext();) {
				GraphConnection connection = (GraphConnection) iterator.next();
				if (connection.getSource().getParent() == this && connection.getDestination().getParent() == this)
					result.add(connection);
			}
			return result;
		}

		public void addNode(GraphNode graphNode) {
			container.addNode(graphNode);
		}

		public void highlightNode(GraphNode graphNode) {
			/* do nothing */
		}

		public void unhighlightNode(GraphNode graphNode) {
			/* do nothing */
		}

		public int getItemType() {
			return GraphItem.CONTAINER;
		}

		public void addConnectionFigure(PolylineConnection figure) {
			container.addConnectionFigure(figure);
		}

		public DisplayIndependentRectangle getLayoutBounds() {
			return container.getLayoutBounds();
		}
	}

	private static HashMap adaptersMap = new HashMap();

	static NodeContainerAdapter get(Graph graph) {
		NodeContainerAdapter adapter = (NodeContainerAdapter) adaptersMap.get(graph);
		if (adapter != null)
			return adapter;

		adapter = new GraphAdapter(graph);
		adaptersMap.put(graph, adapter);
		return adapter;
	}

	static NodeContainerAdapter get(GraphContainer container) {
		NodeContainerAdapter adapter = (NodeContainerAdapter) adaptersMap.get(container);
		if (adapter != null)
			return adapter;

		adapter = new GraphContainerAdapter(container);
		adaptersMap.put(container, adapter);
		return adapter;
	}

	public abstract Widget getAdaptee();

	public abstract Graph getGraph();

	public abstract List getNodes();

	public abstract List getConnections();

	public abstract void addNode(GraphNode graphNode);

	public abstract void highlightNode(GraphNode graphNode);

	public abstract void unhighlightNode(GraphNode graphNode);

	public abstract int getItemType();

	public abstract void addConnectionFigure(PolylineConnection figure);

	public abstract DisplayIndependentRectangle getLayoutBounds();

}