package org.mati.zest.core.widgets;

import org.eclipse.draw2d.PolylineConnection;

abstract class NodeContainerAdapter {
	private static class GraphAdapter extends NodeContainerAdapter {
		private Graph graph;

		private GraphAdapter(Graph graph) {
			this.graph = graph;
		}

		public Graph getGraph() {
			return graph;
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
	}

	private static class GraphContainerAdapter extends NodeContainerAdapter {
		private GraphContainer container;

		private GraphContainerAdapter(GraphContainer container) {
			this.container = container;
		}

		public Graph getGraph() {
			return container.getGraph();
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
	}

	static NodeContainerAdapter create(Graph graph) {
		return new GraphAdapter(graph);
	}

	static NodeContainerAdapter create(GraphContainer container) {
		return new GraphContainerAdapter(container);
	}

	public abstract Graph getGraph();

	public abstract void addNode(GraphNode graphNode);

	public abstract void highlightNode(GraphNode graphNode);

	public abstract void unhighlightNode(GraphNode graphNode);

	public abstract int getItemType();

	public abstract void addConnectionFigure(PolylineConnection figure);

}
