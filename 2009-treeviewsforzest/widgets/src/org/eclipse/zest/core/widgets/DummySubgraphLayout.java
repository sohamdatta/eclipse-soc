package org.eclipse.zest.core.widgets;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.zest.layout.dataStructures.DisplayIndependentDimension;
import org.eclipse.zest.layout.dataStructures.DisplayIndependentPoint;
import org.eclipse.zest.layout.dataStructures.DisplayIndependentRectangle;
import org.eclipse.zest.layout.interfaces.ConnectionLayout;
import org.eclipse.zest.layout.interfaces.EntityLayout;
import org.eclipse.zest.layout.interfaces.NodeLayout;
import org.eclipse.zest.layout.interfaces.SubgraphLayout;

/**
 * A primitive implementation of subgraph layout. A node pruned to this subgraph
 * is minimized and all connections adjacent to it are made invisible. No
 * additional graphic elements are added to the graph, but subclasses may add
 * them.
 */
public class DummySubgraphLayout implements InternalSubgraphLayout {

	/**
	 * Default factory for {@link DummySubgraphLayout}. It creates one subgraph for a
	 * whole graph and throws every node into it.
	 */
	public final static SubgraphFactory FACTORY = new SubgraphFactory() {
		private HashMap contextToSubgraph = new HashMap();

		public SubgraphLayout createSubgraph(InternalNodeLayout[] nodes, InternalLayoutContext context) {
			DummySubgraphLayout subgraph = (DummySubgraphLayout) contextToSubgraph.get(context);
			if (subgraph == null) {
				subgraph = new DummySubgraphLayout(context);
				contextToSubgraph.put(context, subgraph);
			}
			subgraph.addNodes(nodes);
			return subgraph;
		}

	};

	protected final InternalLayoutContext context;

	protected final Set nodes = new HashSet();

	protected boolean disposed = false;

	protected DummySubgraphLayout(InternalLayoutContext context) {
		this.context = context;
	}

	public boolean isGraphEntity() {
		return false;
	}

	public void setSize(double width, double height) {
		// do nothing
		context.checkChangesAllowed();
	}

	public void setLocation(double x, double y) {
		// do nothing
		context.checkChangesAllowed();
	}

	public boolean isResizable() {
		return false;
	}

	public boolean isMovable() {
		return false;
	}

	public EntityLayout[] getSuccessingEntities() {
		return new EntityLayout[0];
	}

	public DisplayIndependentDimension getSize() {
		DisplayIndependentRectangle bounds = context.getBounds();
		return new DisplayIndependentDimension(bounds.width, bounds.height);
	}

	public double getPreferredAspectRatio() {
		return 0;
	}

	public EntityLayout[] getPredecessingEntities() {
		return new EntityLayout[0];
	}

	public DisplayIndependentPoint getLocation() {
		DisplayIndependentRectangle bounds = context.getBounds();
		return new DisplayIndependentPoint(bounds.x + bounds.width / 2, bounds.y + bounds.height / 2);
	}

	public boolean isDirectionDependant() {
		return false;
	}

	public void setDirection(int direction) {
		context.checkChangesAllowed();
		// do nothing
	}

	public void removeNodes(NodeLayout[] nodes) {
		context.checkChangesAllowed();
		for (int i = 0; i < nodes.length; i++) {
			if (this.nodes.remove(nodes[i])) {
				nodes[i].prune(null);
				nodes[i].setMinimized(false);
				refreshConnectionsVisibility(nodes[i].getIncomingConnections());
				refreshConnectionsVisibility(nodes[i].getOutgoingConnections());
			}
		}
		if (this.nodes.isEmpty()) {
			dispose();
		}
	}

	public void removeDisposedNodes() {
		for (Iterator iterator = nodes.iterator(); iterator.hasNext();) {
			InternalNodeLayout node = (InternalNodeLayout) iterator.next();
			if (node.isDisposed())
				iterator.remove();
		}
	}

	public NodeLayout[] getNodes() {
		// TODO perform filtering
		return (NodeLayout[]) nodes.toArray(new NodeLayout[nodes.size()]);
	}

	public int countNodes() {
		return nodes.size();
	}

	public void addNodes(NodeLayout[] nodes) {
		context.checkChangesAllowed();
		for (int i = 0; i < nodes.length; i++) {
			if (this.nodes.add(nodes[i])) {
				nodes[i].prune(this);
				nodes[i].setMinimized(true);
				refreshConnectionsVisibility(nodes[i].getIncomingConnections());
				refreshConnectionsVisibility(nodes[i].getOutgoingConnections());
			}
		}
	}

	protected void refreshConnectionsVisibility(ConnectionLayout[] connections) {
		for (int i = 0; i < connections.length; i++)
			connections[i].setVisible(!connections[i].getSource().isPruned() && !connections[i].getTarget().isPruned());
	}

	protected void applyLayoutChanges() {
		// do nothing
	}

	protected void dispose() {
		if (!disposed) {
			context.removeSubgrah(this);
			disposed = true;
		}
	}

};