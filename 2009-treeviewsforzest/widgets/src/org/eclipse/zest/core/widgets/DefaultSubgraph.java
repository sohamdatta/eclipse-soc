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
import org.eclipse.zest.layout.interfaces.LayoutContext;
import org.eclipse.zest.layout.interfaces.NodeLayout;
import org.eclipse.zest.layout.interfaces.SubgraphLayout;

/**
 * Default implementation of {@link SubgraphLayout}. Every subgraph added to
 * Zest {@link Graph} should inherit from this class.
 * The default implementation is very simple. A node pruned to this subgraph
 * is minimized and all connections adjacent to it are made invisible. No
 * additional graphic elements are added to the graph, but subclasses may add
 * them.
 */
public class DefaultSubgraph implements SubgraphLayout {

	/**
	 * Default factory for {@link DefaultSubgraph}. It creates one subgraph for a
	 * whole graph and throws every node into it.
	 */
	public static class Factory implements SubgraphFactory {
		private HashMap contextToSubgraph = new HashMap();

		public SubgraphLayout createSubgraph(NodeLayout[] nodes, LayoutContext context) {
			DefaultSubgraph subgraph = (DefaultSubgraph) contextToSubgraph.get(context);
			if (subgraph == null) {
				subgraph = new DefaultSubgraph(context);
				contextToSubgraph.put(context, subgraph);
			}
			subgraph.addNodes(nodes);
			return subgraph;
		}

	};

	protected final InternalLayoutContext context;

	protected final Set nodes = new HashSet();

	protected boolean disposed = false;

	protected DefaultSubgraph(LayoutContext context2) {
		if (context2 instanceof InternalLayoutContext)
			this.context = (InternalLayoutContext) context2;
		else
			throw new RuntimeException("This subgraph can be only created with LayoutContext provided by Zest Graph");
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

	/**
	 * Makes sure that value returned by {@link #getLocation()} will be equal to
	 * current location of this subgraph.
	 */
	protected void refreshLocation() {
		// do nothing, to reimplement in subclasses
	}

	/**
	 * Makes sure that value returned by {@link #getSize()} will be equal to
	 * current size of this subgraph.
	 */
	protected void refreshSize() {
		// do nothing, to reimplement in subclasses
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