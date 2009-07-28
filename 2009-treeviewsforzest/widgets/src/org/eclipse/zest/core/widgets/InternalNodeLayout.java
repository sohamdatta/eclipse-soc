package org.eclipse.zest.core.widgets;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.zest.layout.dataStructures.DisplayIndependentDimension;
import org.eclipse.zest.layout.dataStructures.DisplayIndependentPoint;
import org.eclipse.zest.layout.interfaces.ConnectionLayout;
import org.eclipse.zest.layout.interfaces.NodeLayout;
import org.eclipse.zest.layout.interfaces.SubgraphLayout;

class InternalNodeLayout implements NodeLayout {
	private DisplayIndependentPoint location;
	private DisplayIndependentDimension size;
	private boolean minimized = false;
	private final GraphNode node;
	private final InternalLayoutContext ownerLayoutContext;
	private InternalSubgraphLayout subgraph;
	private boolean isDisposed = false;

	public InternalNodeLayout(GraphNode graphNode) {
		this.node = graphNode;
		this.ownerLayoutContext = node.parent.getLayoutContext();
	}

	public DisplayIndependentPoint getLocation() {
		if (location == null) {
			refreshLocation();
		}
		return new DisplayIndependentPoint(location);
	}

	public DisplayIndependentDimension getSize() {
		if (size == null) {
			refreshSize();
		}
		return new DisplayIndependentDimension(size);
	}

	public SubgraphLayout getSubgraph() {
		return subgraph;
	}

	public boolean isMovable() {
		return true;
	}

	public boolean isPrunable() {
		return ownerLayoutContext.isPruningEnabled();
	}

	public boolean isPruned() {
		return subgraph != null;
	}

	public boolean isResizable() {
		return (node.parent.getAdaptee().getStyle() & ZestStyles.NODES_NO_LAYOUT_RESIZE) == 0;
	}

	public void prune(SubgraphLayout subgraph) {
		if (subgraph != null && !(subgraph instanceof InternalSubgraphLayout))
			throw new RuntimeException("InternalNodeLayout can pruned only to InternalSubgarphLayout.");
		ownerLayoutContext.checkChangesAllowed();
		if (subgraph == this.subgraph)
			return;
		if (this.subgraph != null) {
			SubgraphLayout subgraph2 = this.subgraph;
			this.subgraph = null;
			subgraph2.removeNodes(new NodeLayout[] { this });
		}
		if (subgraph != null) {
			this.subgraph = (InternalSubgraphLayout) subgraph;
			subgraph.addNodes(new NodeLayout[] { this });
		}
	}

	public void setLocation(double x, double y) {
		ownerLayoutContext.checkChangesAllowed();
		internalSetLocation(x, y);
	}

	private void internalSetLocation(double x, double y) {
		if (location != null) {
			location.x = x;
			location.y = y;
		} else {
			location = new DisplayIndependentPoint(x, y);
		}
	}

	public void setSize(double width, double height) {
		ownerLayoutContext.checkChangesAllowed();
		internalSetSize(width, height);
	}

	private void internalSetSize(double width, double height) {
		if (size != null) {
			size.width = width;
			size.height = height;
		} else {
			size = new DisplayIndependentDimension(width, height);
		}
	}

	public void setMinimized(boolean minimized) {
		ownerLayoutContext.checkChangesAllowed();
		getSize();
		this.minimized = minimized;
	}

	public boolean isMinimized() {
		return minimized;
	}

	public NodeLayout[] getPredecessingNodes() {
		ConnectionLayout[] connections = getIncomingConnections();
		NodeLayout[] result = new NodeLayout[connections.length];
		for (int i = 0; i < connections.length; i++) {
			result[i] = connections[i].getSource();
			if (result[i] == this)
				result[i] = connections[i].getTarget();
		}
		return result;
	}

	public NodeLayout[] getSuccessingNodes() {
		ConnectionLayout[] connections = getOutgoingConnections();
		NodeLayout[] result = new NodeLayout[connections.length];
		for (int i = 0; i < connections.length; i++) {
			result[i] = connections[i].getTarget();
			if (result[i] == this)
				result[i] = connections[i].getSource();
		}
		return result;
	}

	public NodeLayout[] getSuccessingEntities() {
		// TODO Auto-generated method stub
		return getSuccessingNodes();
	}

	public NodeLayout[] getPredecessingEntities() {
		// TODO Auto-generated method stub
		return getPredecessingNodes();
	}

	public ConnectionLayout[] getIncomingConnections() {
		ArrayList result = new ArrayList();
		for (Iterator iterator = node.getTargetConnections().iterator(); iterator.hasNext();) {
			GraphConnection connection = (GraphConnection) iterator.next();
			if (!ownerLayoutContext.isLayoutItemFiltered(connection))
				result.add(connection.getLayout());
		}
		for (Iterator iterator = node.getSourceConnections().iterator(); iterator.hasNext();) {
			GraphConnection connection = (GraphConnection) iterator.next();
			if (!connection.isDirected() && !ownerLayoutContext.isLayoutItemFiltered(connection))
				result.add(connection.getLayout());
		}
		return (ConnectionLayout[]) result.toArray(new ConnectionLayout[result.size()]);
	}

	public ConnectionLayout[] getOutgoingConnections() {
		ArrayList result = new ArrayList();
		for (Iterator iterator = node.getSourceConnections().iterator(); iterator.hasNext();) {
			GraphConnection connection = (GraphConnection) iterator.next();
			if (!ownerLayoutContext.isLayoutItemFiltered(connection))
				result.add(connection.getLayout());
		}
		for (Iterator iterator = node.getTargetConnections().iterator(); iterator.hasNext();) {
			GraphConnection connection = (GraphConnection) iterator.next();
			if (!connection.isDirected() && !ownerLayoutContext.isLayoutItemFiltered(connection))
				result.add(connection.getLayout());
		}
		return (ConnectionLayout[]) result.toArray(new ConnectionLayout[result.size()]);
	}

	public double getPreferredAspectRatio() {
		return 0;
	}

	void applyLayout() {
		if (minimized) {
			node.setSize(0, 0);
			if (location != null)
				node.setLocation(location.x, location.y);
		} else {
			node.setSize(-1, -1);
			if (location != null)
				node.setLocation(location.x - getSize().width / 2, location.y - size.height / 2);
			if (size != null) {
				Dimension currentSize = node.getSize();
				if (size.width != currentSize.width || size.height != currentSize.height)
					node.setSize(size.width, size.height);
			}
		}
	}

	InternalLayoutContext getOwnerLayoutContext() {
		return ownerLayoutContext;
	}

	void refreshSize() {
		Dimension size2 = node.getSize();
		internalSetSize(size2.width, size2.height);
	}

	void refreshLocation() {
		Point location2 = node.getLocation();
		internalSetLocation(location2.x + getSize().width / 2, location2.y + size.height / 2);
	}

	public String toString() {
		return node.toString() + "(layout)";
	}

	void dispose() {
		isDisposed = true;
		if (subgraph != null)
			subgraph.removeDisposedNodes();
		ownerLayoutContext.fireNodeRemovedEvent(node.getLayout());
	}

	boolean isDisposed() {
		return isDisposed;
	}
}
