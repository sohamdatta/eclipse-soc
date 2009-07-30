/**
 * 
 */
package org.eclipse.zest.core.widgets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.eclipse.draw2d.Animation;
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
	private LayoutAlgorithm layoutAlgorithm;
	private ExpandCollapseManager expandCollapseManager;
	private SubgraphFactory subgraphFactory = DummySubgraphLayout.FACTORY;
	private final HashSet subgraphs = new HashSet();
	private boolean eventsOn = true;
	private boolean backgorundLayoutEnabled = true;
	private boolean externalLayoutInvocation = false;

	/**
	 * @param graph
	 *            the graph owning this context
	 */
	InternalLayoutContext(Graph graph) {
		this.container = NodeContainerAdapter.get(graph);
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

	public SubgraphLayout createSubgraph(NodeLayout[] nodes) {
		checkChangesAllowed();
		InternalNodeLayout[] internalNodes = new InternalNodeLayout[nodes.length];
		for (int i = 0; i < nodes.length; i++) {
			internalNodes[i] = (InternalNodeLayout) nodes[i];
		}
		SubgraphLayout subgraph = subgraphFactory.createSubgraph(internalNodes, this);
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
		HashSet addedSubgraphs = new HashSet();
		ArrayList result = new ArrayList();
		for (Iterator iterator = this.container.getNodes().iterator(); iterator.hasNext();) {
			GraphNode node = (GraphNode) iterator.next();
			if (!isLayoutItemFiltered(node)) {
				InternalNodeLayout nodeLayout = node.getLayout();
				if (!nodeLayout.isPruned()) {
					result.add(nodeLayout);
				} else {
					SubgraphLayout subgraph = nodeLayout.getSubgraph();
					if (!addedSubgraphs.contains(subgraph)) {
						result.add(subgraph);
						addedSubgraphs.add(subgraph);
					}
				}
			}
		}
		return (EntityLayout[]) result.toArray(new EntityLayout[result.size()]);
	}

	public SubgraphLayout[] getSubgraphs() {
		SubgraphLayout[] result = new SubgraphLayout[subgraphs.size()];
		int subgraphCount = 0;
		for (Iterator iterator = subgraphs.iterator(); iterator.hasNext();) {
			SubgraphLayout subgraph = (SubgraphLayout) iterator.next();
			NodeLayout[] nodes = subgraph.getNodes();
			for (int i = 0; i < nodes.length; i++) {
				if (!isLayoutItemFiltered(((InternalNodeLayout) nodes[i]).getNode())) {
					result[subgraphCount++] = subgraph;
					break;
				}
			}
		}
		if (subgraphCount == subgraphs.size()) {
			return result;
		} else {
			SubgraphLayout[] result2 = new SubgraphLayout[subgraphCount];
			System.arraycopy(result, 0, result2, 0, subgraphCount);
			return result2;
		}
	}

	public boolean isBoundsExpandable() {
		return false;
	}

	public boolean isBackgroundLayoutEnabled() {
		return backgorundLayoutEnabled;
	}

	void setBackgroundLayoutEnabled(boolean enabled) {
		if (this.backgorundLayoutEnabled != enabled) {
			this.backgorundLayoutEnabled = enabled;
			fireBackgroundEnableChangedEvent();
		}

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

	public void setExpandCollapseManager(ExpandCollapseManager expandCollapseManager) {
		this.expandCollapseManager = expandCollapseManager;
		expandCollapseManager.initExpansion(this);
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
		ArrayList result = new ArrayList();

		ArrayList sourcesList = new ArrayList();
		if (source instanceof NodeLayout)
			sourcesList.add(source);
		if (source instanceof SubgraphLayout)
			sourcesList.addAll(Arrays.asList(((SubgraphLayout) source).getNodes()));

		HashSet targets = new HashSet();
		if (target instanceof NodeLayout)
			targets.add(target);
		if (target instanceof SubgraphLayout)
			targets.addAll(Arrays.asList(((SubgraphLayout) target).getNodes()));

		for (Iterator iterator = sourcesList.iterator(); iterator.hasNext();) {
			NodeLayout source2 = (NodeLayout) iterator.next();
			ConnectionLayout[] outgoingConnections = source2.getOutgoingConnections();
			for (int i = 0; i < outgoingConnections.length; i++) {
				ConnectionLayout connection = outgoingConnections[i];
				if ((connection.getSource() == source2 && targets.contains(connection.getTarget()))
						|| (connection.getTarget() == source2 && targets.contains(connection.getSource())))
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
		externalLayoutInvocation = true;
		if (expandCollapseManager != null)
			expandCollapseManager.setExpanded(this, node, expanded);
		externalLayoutInvocation = false;
	}

	boolean canExpand(NodeLayout node) {
		return expandCollapseManager != null && expandCollapseManager.canExpand(this, node);
	}

	boolean canCollapse(NodeLayout node) {
		return expandCollapseManager != null && expandCollapseManager.canCollapse(this, node);
	}

	void setSubgraphFactory(SubgraphFactory factory) {
		subgraphFactory = factory;
	}

	void applyMainAlgorithm() {
		if (backgorundLayoutEnabled && mainAlgorithm != null) {
			mainAlgorithm.applyLayout(true);
			flushChanges(false);
		}
	}

	/**
	 * Sets layout algorithm for this context. It differs from
	 * {@link #setMainLayoutAlgorithm(LayoutAlgorithm) main algorithm} in that
	 * it's always used when {@link #applyLayoutAlgorithm(boolean)} and not
	 * after firing of events.
	 */
	void setLayoutAlgorithm(LayoutAlgorithm algorithm) {
		if (this.layoutAlgorithm != null) {
			this.layoutAlgorithm.setLayoutContext(null);
		}
		this.layoutAlgorithm = algorithm;
		this.layoutAlgorithm.setLayoutContext(this);
	}

	LayoutAlgorithm getLayoutAlgorithm() {
		return layoutAlgorithm;
	}

	void applyLayout(boolean clean) {
		if (layoutAlgorithm != null) {
			externalLayoutInvocation = true;
			layoutAlgorithm.applyLayout(clean);
			externalLayoutInvocation = false;
		}
	}

	void checkChangesAllowed() {
		if (!backgorundLayoutEnabled && !externalLayoutInvocation)
			throw new RuntimeException("Layout not allowed to perform changes in layout context!");
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

	void fireBackgroundEnableChangedEvent() {
		ContextListener[] listeners = (ContextListener[]) contextListeners.toArray(new ContextListener[contextListeners.size()]);
		for (int i = 0; i < listeners.length; i++) {
			listeners[i].backgroundEnableChanged(this);
		}
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
		if (eventsOn) {
			node.refreshSize();
			node.refreshLocation();
		}
		boolean intercepted = !eventsOn;
		LayoutListener[] listeners = (LayoutListener[]) layoutListeners.toArray(new LayoutListener[layoutListeners.size()]);
		for (int i = 0; i < listeners.length && !intercepted; i++) {
			intercepted = listeners[i].nodeResized(this, node);
		}
		if (!intercepted)
			applyMainAlgorithm();
	}
}