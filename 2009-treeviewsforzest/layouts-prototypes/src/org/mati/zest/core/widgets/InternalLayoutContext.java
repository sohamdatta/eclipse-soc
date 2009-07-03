/**
 * 
 */
package org.mati.zest.core.widgets;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.eclipse.zest.layouts.dataStructures.DisplayIndependentRectangle;
import org.mati.zest.layout.interfaces.ConnectionLayout;
import org.mati.zest.layout.interfaces.ContextListener;
import org.mati.zest.layout.interfaces.EntityLayout;
import org.mati.zest.layout.interfaces.Filter;
import org.mati.zest.layout.interfaces.GraphStructureListener;
import org.mati.zest.layout.interfaces.LayoutAlgorithm;
import org.mati.zest.layout.interfaces.LayoutContext;
import org.mati.zest.layout.interfaces.LayoutListener;
import org.mati.zest.layout.interfaces.NodeLayout;
import org.mati.zest.layout.interfaces.PruningListener;
import org.mati.zest.layout.interfaces.SubgraphLayout;

class InternalLayoutContext implements LayoutContext {

	private final NodeContainerAdapter container;
	private List filters = new ArrayList();
	private List contextListeners = new ArrayList();
	private List graphStructureListeners = new ArrayList();
	private List layoutListeners = new ArrayList();
	private List pruningListeners = new ArrayList();
	private LayoutAlgorithm mainAlgorithm;
	private ArrayList subgraphs = new ArrayList();

	/**
	 * @param graph
	 *            the graph owning this context
	 */
	InternalLayoutContext(Graph graph) {
		this.container = NodeContainerAdapter.get(graph);

		addFilter(new Filter() {
			// filter out connections connecting nodes lying inside containers
			// with outside nodes
			public boolean isObjectFiltered(GraphItem item) {
				if (item instanceof GraphConnection) {
					GraphConnection connection = (GraphConnection) item;
					if (connection.getSource().parent != connection.getDestination().parent)
						return true;
				}
				return false;
			}
		});
	}

	InternalLayoutContext(GraphContainer container) {
		this.container = NodeContainerAdapter.get(container);
	}

	public void addContextListener(ContextListener listener) {
		contextListeners.add(listener);
	}

	public void addGraphStructureListener(GraphStructureListener listener) {
		graphStructureListeners.add(listener);
	}

	public void addLayoutListener(LayoutListener listener) {
		layoutListeners.add(listener);
	}

	public void addPruningListener(PruningListener listener) {
		pruningListeners.add(listener);
	}

	public SubgraphLayout addSubgraph(NodeLayout[] nodes) {
		// TODO Auto-generated method stub
		return null;
	}

	public void flushChanges(boolean animationHint) {
		// TODO Auto-generated method stub
		// TODO probably OK for nodes, need to add subgraphs
		// TODO respect animation hint
		for (Iterator iterator = container.getNodes().iterator(); iterator.hasNext();) {
			GraphNode node = (GraphNode) iterator.next();
			node.applyLayoutChanges();
		}
	}

	public DisplayIndependentRectangle getBounds() {
		return container.getLayoutBounds();
	}

	public LayoutAlgorithm getMainLayoutAlgorithm() {
		return mainAlgorithm;
	}

	public NodeLayout[] getNodes() {
		ArrayList result = new ArrayList();
		for (Iterator iterator = this.container.getNodes().iterator(); iterator.hasNext();) {
			GraphNode node = (GraphNode) iterator.next();
			if (!isLayoutItemFiltered(node))
				result.add(node.getLayout());
		}
		return (NodeLayout[]) result.toArray(new NodeLayout[result.size()]);
	}

	public EntityLayout[] getEntities() {
		// TODO Auto-generated method stub
		return getNodes();
	}

	public SubgraphLayout[] getSubgraphs() {
		// TODO filter out subgraphs that have all elements filtered out
		return (SubgraphLayout[]) subgraphs.toArray(new SubgraphLayout[subgraphs.size()]);
	}

	public boolean isBoundsExpandable() {
		return false;
	}

	public boolean isContinuousLayoutEnabled() {
		return false;
	}

	public boolean isPruningEnabled() {
		return false;
	}

	public void removeContextListener(ContextListener listener) {
		contextListeners.remove(listener);
	}

	public void removeGraphStructureListener(GraphStructureListener listener) {
		graphStructureListeners.remove(listener);
	}

	public void removeLayoutListener(LayoutListener listener) {
		layoutListeners.remove(listener);
	}

	public void removePruningListener(PruningListener listener) {
		pruningListeners.remove(listener);
	}

	public void setMainLayoutAlgorithm(LayoutAlgorithm algorithm) {
		mainAlgorithm = algorithm;
	}

	public ConnectionLayout[] getConnections() {
		List connections = container.getConnections();
		ConnectionLayout[] result = new ConnectionLayout[connections.size()];
		int i = 0;
		for (Iterator iterator = connections.iterator(); iterator.hasNext();) {
			GraphConnection connection = (GraphConnection) iterator.next();
			if (!isLayoutItemFiltered(connection))
				result[i++] = connection.getLayout();
		}
		if (i == result.length)
			return result;
		ConnectionLayout[] result2 = new ConnectionLayout[i];
		System.arraycopy(result, 0, result2, 0, i);
		return result2;
	}

	public ConnectionLayout[] getConnections(EntityLayout source, EntityLayout target) {
		HashSet result = new HashSet();
		// TODO add support for subgraphs
		if (source instanceof NodeLayout && target instanceof NodeLayout) {
			ConnectionLayout[] outgoingConnections = ((NodeLayout) source).getOutgoingConnections();
			for (int i = 0; i < outgoingConnections.length; i++) {
				ConnectionLayout connection = outgoingConnections[i];
				if ((connection.getTarget() == target && connection.getSource() == source)
						|| (connection.getTarget() == source && connection.getSource() == target))
					result.add(connection);
			}
		}
		return (ConnectionLayout[]) result.toArray(new ConnectionLayout[result.size()]);
	}

	void addFilter(Filter filter) {
		filters.add(filter);
	}

	void removeFilter(Filter filter) {
		filters.remove(filter);
	}

	boolean isLayoutItemFiltered(GraphItem item) {
		for (Iterator it = filters.iterator(); it.hasNext();) {
			Filter filter = (Filter) it.next();
			if (filter.isObjectFiltered(item))
				return true;
		}
		if (container.getItemType() == GraphItem.CONTAINER) {
			InternalLayoutContext parentLayout = container.getGraph().getLayoutContext();
			if (parentLayout.isLayoutItemFiltered(item))
				return true;
		}
		return false;
	}

}