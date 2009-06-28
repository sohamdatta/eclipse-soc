package org.mati.zest.core.widgets;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.zest.layouts.dataStructures.DisplayIndependentDimension;
import org.eclipse.zest.layouts.dataStructures.DisplayIndependentPoint;
import org.mati.zest.layout.interfaces.ConnectionLayout;
import org.mati.zest.layout.interfaces.NodeLayout;
import org.mati.zest.layout.interfaces.SubgraphLayout;

class InternalNodeLayout implements NodeLayout {
	private DisplayIndependentPoint location;
	private DisplayIndependentDimension size;
	private final GraphNode node;
	private final GraphLayoutContext rootLayoutContext;

	public InternalNodeLayout(GraphNode graphNode) {
		this.node = graphNode;
		this.rootLayoutContext = node.parent.getGraph().getLayoutContext();
	}

	public DisplayIndependentPoint getLocation() {
		if (location == null) {
			Point location2 = node.getLocation();
			location = new DisplayIndependentPoint(location2.x + getSize().width / 2, location2.y + size.height / 2);
		}
		return new DisplayIndependentPoint(location);
	}

	public DisplayIndependentDimension getSize() {
		if (size == null) {
			Dimension size2 = node.getSize();
			size = new DisplayIndependentDimension(size2.width, size2.height);
		}
		return new DisplayIndependentDimension(size);
	}

	public SubgraphLayout getSubgraph() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isMovable() {
		return true;
	}

	public boolean isPrunable() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isPruned() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isResizable() {
		return true;
	}

	public void prune(SubgraphLayout subgraph) {
		// TODO Auto-generated method stub

	}

	public void setLocation(double x, double y) {
		location = new DisplayIndependentPoint(x, y);
	}

	public void setSize(double width, double height) {
		size = new DisplayIndependentDimension(width, height);
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
			if (!rootLayoutContext.isLayoutItemFiltered(connection))
				result.add(connection.getLayout());
		}
		for (Iterator iterator = node.getSourceConnections().iterator(); iterator.hasNext();) {
			GraphConnection connection = (GraphConnection) iterator.next();
			if (!connection.isDirected() && !rootLayoutContext.isLayoutItemFiltered(connection))
				result.add(connection.getLayout());
		}
		return (ConnectionLayout[]) result.toArray(new ConnectionLayout[result.size()]);
	}

	public ConnectionLayout[] getOutgoingConnections() {
		ArrayList result = new ArrayList();
		for (Iterator iterator = node.getSourceConnections().iterator(); iterator.hasNext();) {
			GraphConnection connection = (GraphConnection) iterator.next();
			if (!rootLayoutContext.isLayoutItemFiltered(connection))
				result.add(connection.getLayout());
		}
		for (Iterator iterator = node.getTargetConnections().iterator(); iterator.hasNext();) {
			GraphConnection connection = (GraphConnection) iterator.next();
			if (!connection.isDirected() && !rootLayoutContext.isLayoutItemFiltered(connection))
				result.add(connection.getLayout());
		}
		return (ConnectionLayout[]) result.toArray(new ConnectionLayout[result.size()]);
	}

	public double getPreferredAspectRatio() {
		return 0;
	}

	void applyLayout() {
		if (size != null)
			node.setSize(size.width, size.height);
		if (location != null)
			node.setLocation(location.x - getSize().width / 2, location.y - size.height / 2);
	}
}
