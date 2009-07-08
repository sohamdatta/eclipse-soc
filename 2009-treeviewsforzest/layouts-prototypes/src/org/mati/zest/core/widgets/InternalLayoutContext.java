/**
 * 
 */
package org.mati.zest.core.widgets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.zest.core.widgets.ZestStyles;
import org.eclipse.zest.layout.dataStructures.DisplayIndependentDimension;
import org.eclipse.zest.layout.dataStructures.DisplayIndependentPoint;
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
import org.eclipse.zest.layout.interfaces.SubgraphFactory;
import org.eclipse.zest.layout.interfaces.SubgraphLayout;

class InternalLayoutContext implements LayoutContext {

	/**
	 * This factory throws all nodes into one subgraph that doesn't show
	 * anything on the screen. A node pruned to this factory's subtree is
	 * resized to (0, 0) and all connections adjacent to it are made invisible.
	 */
	private class DummySubgraphFacotry implements SubgraphFactory {
		private class DummySubgraph implements SubgraphLayout {
			private LayoutContext context;

			public DummySubgraph(LayoutContext context) {
				super();
				this.context = context;
			}

			private Set nodes = new HashSet();

			public boolean isGraphEntity() {
				return false;
			}

			public void setSize(double width, double height) {
				// do nothing
			}

			public void setLocation(double x, double y) {
				// do nothing
			}

			public boolean isResizable() {
				return false;
			}

			public boolean isMovable() {
				return false;
			}

			public NodeLayout[] getSuccessingEntities() {
				// TODO Auto-generated method stub
				return null;
			}

			public DisplayIndependentDimension getSize() {
				DisplayIndependentRectangle bounds = context.getBounds();
				return new DisplayIndependentDimension(bounds.width, bounds.height);
			}

			public double getPreferredAspectRatio() {
				return 0;
			}

			public NodeLayout[] getPredecessingEntities() {
				// TODO Auto-generated method stub
				return null;
			}

			public DisplayIndependentPoint getLocation() {
				DisplayIndependentRectangle bounds = context.getBounds();
				return new DisplayIndependentPoint(bounds.x + bounds.width / 2, bounds.y + bounds.height / 2);
			}

			public void removeNodes(NodeLayout[] nodes) {
				for (int i = 0; i < nodes.length; i++) {
					if (this.nodes.remove(nodes[i])) {
						nodes[i].prune(null);
						nodes[i].setMinimized(false);
						showConnections(nodes[i].getIncomingConnections());
						showConnections(nodes[i].getOutgoingConnections());
					}
				}
			}

			public NodeLayout[] getNodes() {
				return (NodeLayout[]) nodes.toArray(new NodeLayout[nodes.size()]);
			}

			public void addNodes(NodeLayout[] nodes) {
				for (int i = 0; i < nodes.length; i++) {
					if (this.nodes.add(nodes[i])) {
						nodes[i].prune(this);
						nodes[i].setMinimized(true);
						hideConnections(nodes[i].getIncomingConnections());
						hideConnections(nodes[i].getOutgoingConnections());
					}
				}
			}

			private void hideConnections(ConnectionLayout[] connections) {
				for (int i = 0; i < connections.length; i++)
					connections[i].setVisible(false);
			}

			private void showConnections(ConnectionLayout[] connections) {
				for (int i = 0; i < connections.length; i++) {
					if (!connections[i].getSource().isPruned() && !connections[i].getTarget().isPruned())
						connections[i].setVisible(true);
				}
			}

		};

		private HashMap contextToSubgraph = new HashMap();

		public SubgraphLayout createSubgraph(NodeLayout[] nodes, LayoutContext context) {
			DummySubgraph subgraph = (DummySubgraph) contextToSubgraph.get(context);
			if (subgraph == null) {
				subgraph = new DummySubgraph(context);
				contextToSubgraph.put(context, subgraph);
			}
			subgraph.addNodes(nodes);
			return subgraph;
		}

	}

	private final NodeContainerAdapter container;
	private List filters = new ArrayList();
	private List contextListeners = new ArrayList();
	private List graphStructureListeners = new ArrayList();
	private List layoutListeners = new ArrayList();
	private List pruningListeners = new ArrayList();
	private LayoutAlgorithm mainAlgorithm;
	private ExpandCollapseManager expandCollapseManager;
	private SubgraphFactory subgraphFactory = new DummySubgraphFacotry();
	private ArrayList subgraphs = new ArrayList();

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

	public SubgraphLayout addSubgraph(NodeLayout[] nodes) {
		return subgraphFactory.createSubgraph(nodes, this);
	}

	public void flushChanges(boolean animationHint) {
		// TODO Auto-generated method stub
		// TODO probably OK for nodes, need to add subgraphs
		// TODO respect animation hint
		// TODO support for asynchronous call
		for (Iterator iterator = container.getNodes().iterator(); iterator.hasNext();) {
			GraphNode node = (GraphNode) iterator.next();
			node.applyLayoutChanges();
		}
		for (Iterator iterator = container.getConnections().iterator(); iterator.hasNext();) {
			GraphConnection connection = (GraphConnection) iterator.next();
			connection.applyLayoutChanges();
		}
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
		boolean intercepted = false;
		for (Iterator iterator = graphStructureListeners.iterator(); iterator.hasNext() && !intercepted;) {
			GraphStructureListener listener = (GraphStructureListener) iterator.next();
			intercepted = listener.nodeAdded(this, node);
		}
		if (!intercepted && mainAlgorithm != null)
			mainAlgorithm.applyLayout();
	}

	void fireNodeRemovedEvent(NodeLayout node) {
		boolean intercepted = false;
		for (Iterator iterator = graphStructureListeners.iterator(); iterator.hasNext() && !intercepted;) {
			GraphStructureListener listener = (GraphStructureListener) iterator.next();
			intercepted = listener.nodeRemoved(this, node);
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
			boolean intercepted = false;
			for (Iterator iterator = graphStructureListeners.iterator(); iterator.hasNext() && !intercepted;) {
				GraphStructureListener listener = (GraphStructureListener) iterator.next();
				intercepted = listener.connectionAdded(this, connection);
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
			boolean intercepted = false;
			for (Iterator iterator = graphStructureListeners.iterator(); iterator.hasNext() && !intercepted;) {
				GraphStructureListener listener = (GraphStructureListener) iterator.next();
				intercepted = listener.connectionRemoved(this, connection);
			}
			if (!intercepted)
				applyMainAlgorithm();
		} else {
			sourceContext.fireConnectionAddedEvent(connection);
		}
	}

	void fireBoundsChangedEvent() {
		boolean intercepted = false;
		for (Iterator iterator = contextListeners.iterator(); iterator.hasNext() && !intercepted;) {
			ContextListener listener = (ContextListener) iterator.next();
			intercepted = listener.boundsChanged(this);
		}
		if (!intercepted)
			applyMainAlgorithm();
	}
}