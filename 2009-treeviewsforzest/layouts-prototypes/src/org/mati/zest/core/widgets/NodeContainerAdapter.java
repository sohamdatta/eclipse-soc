package org.mati.zest.core.widgets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.zest.layout.dataStructures.DisplayIndependentRectangle;

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
			return filterConnections(graph.getConnections());
		}

		public void addNode(GraphNode graphNode) {
			graph.addNode(graphNode);
		}

		public void addFigure(IFigure figure) {
			graph.addSubgraphFigure(figure);
		}

		public int getItemType() {
			return GraphItem.GRAPH;
		}

		public DisplayIndependentRectangle getLayoutBounds() {
			Dimension preferredSize = graph.getPreferredSize();
			return new DisplayIndependentRectangle(0, 0, preferredSize.width, preferredSize.height);
		}

		public InternalLayoutContext getLayoutContext() {
			return graph.getLayoutContext();
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
			return filterConnections(container.getGraph().getConnections());
		}

		public void addNode(GraphNode graphNode) {
			container.addNode(graphNode);
		}

		public void addFigure(IFigure figure) {
			container.addSubgraphFigure(figure);
		}

		public int getItemType() {
			return GraphItem.CONTAINER;
		}

		public DisplayIndependentRectangle getLayoutBounds() {
			return container.getLayoutBounds();
		}

		public InternalLayoutContext getLayoutContext() {
			return container.getLayoutContext();
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

	/**
	 * Returns list of connections laying inside this container. Only
	 * connections which both source and target nodes lay directly in this
	 * container are returned.
	 * 
	 * @return
	 */
	public abstract List getConnections();

	public abstract void addNode(GraphNode graphNode);

	/**
	 * Adds a custom figure to be displayed on top of nodes. Can be used to add
	 * subgraph figures.
	 * 
	 * @param figure
	 */
	public abstract void addFigure(IFigure figure);

	public abstract int getItemType();

	public abstract DisplayIndependentRectangle getLayoutBounds();

	public abstract InternalLayoutContext getLayoutContext();

	/**
	 * Takes a list of connections and returns only those which source and
	 * target nodes lay directly in this container.
	 * 
	 * @param connections
	 *            list of GraphConnection to filter
	 * @return filtered list
	 */
	protected List filterConnections(List connections) {
		List result = new ArrayList();
		for (Iterator iterator = connections.iterator(); iterator.hasNext();) {
			GraphConnection connection = (GraphConnection) iterator.next();
			if (connection.getSource().getParent() == this && connection.getDestination().getParent() == this)
				result.add(connection);
		}
		return result;
	}
}
