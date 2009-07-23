/**
 * 
 */
package org.mati.zest.core.widgets;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.eclipse.draw2d.Animation;
import org.eclipse.zest.core.widgets.ZestStyles;
import org.eclipse.zest.layout.dataStructures.DisplayIndependentRectangle;
import org.eclipse.zest.layout.interfaces.ConnectionLayout;
import org.eclipse.zest.layout.interfaces.ContextListener;
import org.eclipse.zest.layout.interfaces.EntityLayout;
import org.eclipse.zest.layout.interfaces.ExpandCollapseManager;
import org.eclipse.zest.layout.interfaces.GraphStructureListener;
import org.eclipse.zest.layout.interfaces.LayoutAlgorithm;
import org.eclipse.zest.layout.interfaces.LayoutContext;
import org.eclipse.zest.layout.interfaces.LayoutListener;
import org.eclipse.zest.layout.interfaces.NodeLayout;
import org.eclipse.zest.layout.interfaces.PruningListener;
import org.eclipse.zest.layout.interfaces.SubgraphLayout;

public class InternalLayoutContext implements LayoutContext {

	final NodeContainerAdapter container;
	private final List filters = new ArrayList();
	private final List contextListeners = new ArrayList();
	private final List graphStructureListeners = new ArrayList();
	private final List layoutListeners = new ArrayList();
	private final List pruningListeners = new ArrayList();
	private LayoutAlgorithm mainAlgorithm;
	private ExpandCollapseManager expandCollapseManager;
	private SubgraphFactory subgraphFactory = DummySubgraphLayout.FACTORY;
	private final HashSet subgraphs = new HashSet();
	private boolean eventsOn = true;

	private final LayoutFilter defaultFilter = new LayoutFilter() {
		public boolean isObjectFiltered(GraphItem item) {
			// filter out invisible elements
			if (item instanceof GraphItem && ZestStyles.checkStyle(container.getGraph().getStyle(), ZestStyles.IGNORE_INVISIBLE_LAYOUT)
					&& !((GraphItem) item).isVisible())
				return true;
			return false;
		}
	};

	/**
	 * @param graph
	 *            the graph owning this context
	 */
	InternalLayoutContext(Graph graph) {
		this.container = NodeContainerAdapter.get(graph);
		addFilter(defaultFilter);
	}

	InternalLayoutContext(GraphContainer container) {
		this.container = NodeContainerAdapter.get(container);
		addFilter(defaultFilter);
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

	public SubgraphLayout createSubgraph(NodeLayout[] nodes) {
		SubgraphLayout subgraph = subgraphFactory.createSubgraph(nodes, this);
		subgraphs.add(subgraph);
		return subgraph;
	}

	void removeSubgrah(DummySubgraphLayout subgraph) {
		subgraphs.remove(subgraph);
	}

	public void flushChanges(boolean animationHint) {
		// TODO Auto-generated method stub
		// TODO support for asynchronous call
		eventsOn = false;
		animationHint = animationHint && container.getGraph().isVisible();
		if (animationHint) {
			Animation.markBegin();
		}
		for (Iterator iterator = container.getNodes().iterator(); iterator.hasNext();) {
			GraphNode node = (GraphNode) iterator.next();
			node.applyLayoutChanges();
		}
		for (Iterator iterator = container.getConnections().iterator(); iterator.hasNext();) {
			GraphConnection connection = (GraphConnection) iterator.next();
			connection.applyLayoutChanges();
		}
		for (Iterator iterator = subgraphs.iterator(); iterator.hasNext();) {
			DummySubgraphLayout subgraph = (DummySubgraphLayout) iterator.next();
			subgraph.applyLayoutChanges();
		}
		if (animationHint) {
			Animation.run(Graph.ANIMATION_TIME);
		}
		eventsOn = true;
	}

	public DisplayIndependentRectangle getBounds() {
		return container.getLayoutBounds();
	}

	public LayoutAlgorithm getMainLayoutAlgorithm() {
		return mainAlgorithm;
	}

	public ExpandCollapseManager getExpandCollapseManager() {
		return expandCollapseManager;
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
		return expandCollapseManager != null;
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

	public void setExpandCollapseManager(ExpandCollapseManager pruningManager) {
		this.expandCollapseManager = pruningManager;
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

	void addFilter(LayoutFilter filter) {
		filters.add(filter);
	}

	void removeFilter(LayoutFilter filter) {
		filters.remove(filter);
	}

	boolean isLayoutItemFiltered(GraphItem item) {
		for (Iterator it = filters.iterator(); it.hasNext();) {
			LayoutFilter filter = (LayoutFilter) it.next();
			if (filter.isObjectFiltered(item))
				return true;
		}
		return false;
	}

	void setExpanded(NodeLayout node, boolean expanded) {
		if (expandCollapseManager != null)
			expandCollapseManager.setExpanded(node, expanded);
	}

	void setSubgraphFactory(SubgraphFactory factory) {
		subgraphFactory = factory;
	}

	void applyMainAlgorithm() {
		if (mainAlgorithm != null) {
			mainAlgorithm.applyLayout();
			flushChanges(false);
		}
	}

	void fireNodeAddedEvent(NodeLayout node) {
		boolean intercepted = !eventsOn;
		GraphStructureListener[] listeners = (GraphStructureListener[]) graphStructureListeners
				.toArray(new GraphStructureListener[graphStructureListeners.size()]);
		for (int i = 0; i < listeners.length && !intercepted; i++) {
			intercepted = listeners[i].nodeAdded(this, node);
		}
		if (!intercepted)
			applyMainAlgorithm();
	}

	void fireNodeRemovedEvent(NodeLayout node) {
		boolean intercepted = !eventsOn;
		GraphStructureListener[] listeners = (GraphStructureListener[]) graphStructureListeners
				.toArray(new GraphStructureListener[graphStructureListeners.size()]);
		for (int i = 0; i < listeners.length && !intercepted; i++) {
			intercepted = listeners[i].nodeRemoved(this, node);
		}
		if (!intercepted)
			applyMainAlgorithm();
	}

	void fireConnectionAddedEvent(ConnectionLayout connection) {
		InternalLayoutContext sourceContext = ((InternalNodeLayout) connection.getSource()).getOwnerLayoutContext();
		InternalLayoutContext targetContext = ((InternalNodeLayout) connection.getTarget()).getOwnerLayoutContext();
		if (sourceContext != targetContext)
			return;
		if (sourceContext == this) {
			boolean intercepted = !eventsOn;
			GraphStructureListener[] listeners = (GraphStructureListener[]) graphStructureListeners
					.toArray(new GraphStructureListener[graphStructureListeners.size()]);
			for (int i = 0; i < listeners.length && !intercepted; i++) {
				intercepted = listeners[i].connectionAdded(this, connection);
			}
			if (!intercepted)
				applyMainAlgorithm();
		} else {
			sourceContext.fireConnectionAddedEvent(connection);
		}
	}

	void fireConnectionRemovedEvent(ConnectionLayout connection) {
		InternalLayoutContext sourceContext = ((InternalNodeLayout) connection.getSource()).getOwnerLayoutContext();
		InternalLayoutContext targetContext = ((InternalNodeLayout) connection.getTarget()).getOwnerLayoutContext();
		if (sourceContext != targetContext)
			return;
		if (sourceContext == this) {
			boolean intercepted = !eventsOn;
			GraphStructureListener[] listeners = (GraphStructureListener[]) graphStructureListeners
					.toArray(new GraphStructureListener[graphStructureListeners.size()]);
			for (int i = 0; i < listeners.length && !intercepted; i++) {
				intercepted = listeners[i].connectionRemoved(this, connection);
			}
			if (!intercepted)
				applyMainAlgorithm();
		} else {
			sourceContext.fireConnectionAddedEvent(connection);
		}
	}

	void fireBoundsChangedEvent() {
		boolean intercepted = !eventsOn;
		ContextListener[] listeners = (ContextListener[]) contextListeners.toArray(new ContextListener[contextListeners.size()]);
		for (int i = 0; i < listeners.length && !intercepted; i++) {
			intercepted = listeners[i].boundsChanged(this);
		}
		if (!intercepted)
			applyMainAlgorithm();
	}

	void fireNodeMovedEvent(InternalNodeLayout node) {
		if (eventsOn)
			node.refreshLocation();
		boolean intercepted = !eventsOn;
		LayoutListener[] listeners = (LayoutListener[]) layoutListeners.toArray(new LayoutListener[layoutListeners.size()]);
		for (int i = 0; i < listeners.length && !intercepted; i++) {
			intercepted = listeners[i].nodeMoved(this, node);
		}
		if (!intercepted)
			applyMainAlgorithm();
	}

	void fireNodeResizedEvent(InternalNodeLayout node) {
		if (eventsOn)
			node.refreshSize();
		boolean intercepted = !eventsOn;
		LayoutListener[] listeners = (LayoutListener[]) layoutListeners.toArray(new LayoutListener[layoutListeners.size()]);
		for (int i = 0; i < listeners.length && !intercepted; i++) {
			intercepted = listeners[i].nodeResized(this, node);
		}
		if (!intercepted)
			applyMainAlgorithm();
	}
}