package org.eclipse.zest.core.widgets;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import org.eclipse.draw2d.Animation;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.FigureListener;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Display;
import org.eclipse.zest.core.widgets.internal.GraphLabel;
import org.eclipse.zest.layout.dataStructures.DisplayIndependentPoint;
import org.eclipse.zest.layout.interfaces.ConnectionLayout;
import org.eclipse.zest.layout.interfaces.ContextListener;
import org.eclipse.zest.layout.interfaces.ExpandCollapseManager;
import org.eclipse.zest.layout.interfaces.GraphStructureListener;
import org.eclipse.zest.layout.interfaces.LayoutContext;
import org.eclipse.zest.layout.interfaces.NodeLayout;

/**
 * <p>
 * An {@link ExpandCollapseManager} specialized for Directed Acyclic Graphs. It
 * works correctly only when all connections are directed (and of course nodes
 * form an acyclic graph). It's supposed to be used with
 * {@link InternalLayoutContext}.
 * </p>
 * <p>
 * When a node is collapsed, all its outgoing connections are hidden and these
 * successors that have no visible incoming nodes are pruned. When a node is
 * expanded, all its successors are unpruned and connections pointing to them
 * are shown.
 * </p>
 * <p>
 * <b>NOTE:</b> This manager registers a {@link GraphStructureListener} in its
 * layout context and may intercept some events if layout algorithm shouldn't
 * receive them (for example when a connection added to a pruned node is
 * instantly made invisible). That's why <b>{@link DAGExpandCollapseManager}
 * should be set in a context before a layout algorithm</b>.
 * </p>
 * <p>
 * <b>NOTE:</b> A <code>Graph</code> using this manger should use
 * {@link DummySubgraphLayout} s, which don't show any information about
 * subgraphs in the graph. That's because for this manager it doesn't matter
 * which subgraph a node belongs to (each pruning creates a new subgraph). Also,
 * this manager adds a label to each collapsed node showing number of its
 * children.
 * </p>
 * One instance of this class can serve only one instance of <code>Graph</code>.
 */
public class DAGExpandCollapseManager implements ExpandCollapseManager {

	private InternalLayoutContext context;

	private HashSet expandedNodes = new HashSet();

	private HashSet nodesToPrune = new HashSet();

	private HashSet nodesToUnprune = new HashSet();

	private HashSet connectionsToHide = new HashSet();

	private HashSet connectionsToShow = new HashSet();

	private boolean cleanLayoutScheduled = false;

	private boolean filterOn = true;

	private HashMap nodeFigureToLabel = new HashMap();

	private HashMap nodeFigureToNode = new HashMap();

	private final FigureListener nodeFigureListener = new FigureListener() {
		public void figureMoved(IFigure source) {
			if (!Animation.isAnimating()) {
				InternalNodeLayout node = (InternalNodeLayout) nodeFigureToNode.get(source);
				updateFigureForNode(node);
			}
		}
	};

	public void initExpansion(final LayoutContext context2) {
		if (!(context2 instanceof InternalLayoutContext))
			throw new RuntimeException("This manager works only with org.eclipse.zest.core.widgets.InternalLayoutContext");
		context = (InternalLayoutContext) context2;

		context.addFilter(new LayoutFilter() {
			public boolean isObjectFiltered(GraphItem item) {
				if (filterOn && item instanceof GraphConnection) {
					GraphConnection connection = (GraphConnection) item;
					return !isExpanded(connection.getSource().getLayout());
				}
				return false;
			}
		});

		context.addGraphStructureListener(new GraphStructureListener() {
			public boolean nodeRemoved(LayoutContext context, NodeLayout node) {
				if (isExpanded(node)) {
					collapse(node);
					flushChanges(false, true);
				}
				removeFigureForNode((InternalNodeLayout) node);
				return false;
			}

			public boolean nodeAdded(LayoutContext context, NodeLayout node) {
				resetState(node);
				flushChanges(false, true);
				return false;
			}

			public boolean connectionRemoved(LayoutContext context, ConnectionLayout connection) {
				NodeLayout source = connection.getSource();
				NodeLayout target = connection.getTarget();
				if (!isExpanded(target) && getUnfilteredIncomingConnections(target).length == 0) {
					expand(target);
					flushChanges(false, true);
				}
				updateFigureForNode((InternalNodeLayout) source);
				return !isExpanded(source);
			}

			public boolean connectionAdded(LayoutContext context, ConnectionLayout connection) {
				NodeLayout source = connection.getSource();
				NodeLayout target = connection.getTarget();

				if (isExpanded(source))
					showConnection(connection);
				else
					hideConnection(connection);
				resetState(target);

				flushChanges(false, true);
				updateFigureForNode((InternalNodeLayout) source);
				return !isExpanded(source);
			}

		});

		context.addContextListener(new ContextListener.Stub() {
			public void backgroundEnableChanged(LayoutContext context) {
				flushChanges(false, false);
			}
		});
	}

	public boolean canCollapse(LayoutContext context, NodeLayout node) {
		return isExpanded(node) && !node.isPruned() && getUnfilteredOutgoingConnections(node).length > 0;
	}

	public boolean canExpand(LayoutContext context, NodeLayout node) {
		return !isExpanded(node) && !node.isPruned() && getUnfilteredOutgoingConnections(node).length > 0;
	}

	public void setExpanded(LayoutContext context, NodeLayout node, boolean expanded) {
		if (isExpanded(node) == expanded)
			return;
		if (expanded) {
			if (canExpand(context, node)) {
				expand(node);
			}
		} else {
			if (canCollapse(context, node)) {
				collapse(node);
			}
		}
		flushChanges(true, true);
	}

	private void expand(NodeLayout node) {
		setExpanded(node, true);
		ConnectionLayout[] connections = getUnfilteredOutgoingConnections(node);
		for (int i = 0; i < connections.length; i++) {
			showConnection(connections[i]);
			NodeLayout target = connections[i].getTarget();
			unpruneNode(target);
		}
	}

	private void collapse(NodeLayout node) {
		if (isExpanded(node))
			setExpanded(node, false);
		else
			return;
		ConnectionLayout[] connections = getUnfilteredOutgoingConnections(node);
		for (int i = 0; i < connections.length; i++) {
			hideConnection(connections[i]);
		}
		for (int i = 0; i < connections.length; i++) {
			NodeLayout target = connections[i].getTarget();
			collapse(target);
			ConnectionLayout[] incomingConnections = target.getIncomingConnections();
			boolean shouldPrune = true;
			for (int j = 0; j < incomingConnections.length; j++) {
				if (isVisible(incomingConnections[j])) {
					shouldPrune = false;
					break;
				}
			}
			if (shouldPrune)
				pruneNode(target);
		}
	}

	/**
	 * By default nodes at the top are expanded. Nodes that have no visible
	 * incoming connections must be pruned.
	 * 
	 * @param target
	 */
	private void resetState(NodeLayout target) {
		ConnectionLayout[] incomingConnections = getUnfilteredIncomingConnections(target);
		if (incomingConnections.length == 0) {
			expand(target);
		} else {
			collapse(target);
			for (int i = 0; i < incomingConnections.length; i++) {
				if (isVisible(incomingConnections[i]))
					return;
			}
			pruneNode(target);
		}
	}

	private void pruneNode(NodeLayout node) {
		nodesToUnprune.remove(node);
		nodesToPrune.add(node);
	}

	private void unpruneNode(NodeLayout node) {
		nodesToPrune.remove(node);
		nodesToUnprune.add(node);
	}

	private void showConnection(ConnectionLayout connection) {
		connectionsToHide.remove(connection);
		connectionsToShow.add(connection);
	}

	private void hideConnection(ConnectionLayout connection) {
		connectionsToShow.remove(connection);
		connectionsToHide.add(connection);
	}

	private boolean isVisible(ConnectionLayout connection) {
		return (connection.isVisible() && !connectionsToHide.contains(connection)) || (connectionsToShow.contains(connection));
	}

	private void flushChanges(boolean force, boolean clean) {
		cleanLayoutScheduled = cleanLayoutScheduled || clean;
		if (!force && !context.isBackgroundLayoutEnabled()) {
			return;
		}

		for (Iterator iterator = nodesToUnprune.iterator(); iterator.hasNext();) {
			NodeLayout node = (NodeLayout) iterator.next();
			node.prune(null);
		}
		nodesToUnprune.clear();

		if (!nodesToPrune.isEmpty()) {
			context.createSubgraph((NodeLayout[]) nodesToPrune.toArray(new NodeLayout[nodesToPrune.size()]));
			nodesToPrune.clear();
		}

		for (Iterator iterator = connectionsToShow.iterator(); iterator.hasNext();) {
			((ConnectionLayout) iterator.next()).setVisible(true);
		}
		connectionsToShow.clear();

		for (Iterator iterator = connectionsToHide.iterator(); iterator.hasNext();) {
			((ConnectionLayout) iterator.next()).setVisible(false);
		}
		connectionsToHide.clear();

		((InternalLayoutContext) context).applyLayout(cleanLayoutScheduled);
		cleanLayoutScheduled = false;
		context.flushChanges(true);
	}

	private ConnectionLayout[] getUnfilteredOutgoingConnections(NodeLayout node) {
		filterOn = false;
		ConnectionLayout[] result = node.getOutgoingConnections();
		filterOn = true;
		return result;
	}

	private ConnectionLayout[] getUnfilteredIncomingConnections(NodeLayout node) {
		filterOn = false;
		ConnectionLayout[] result = node.getIncomingConnections();
		filterOn = true;
		return result;
	}

	private boolean isExpanded(NodeLayout node) {
		return expandedNodes.contains(node);
	}

	private void setExpanded(NodeLayout node, boolean expanded) {
		if (expanded) {
			expandedNodes.add(node);
		} else {
			expandedNodes.remove(node);
		}
		updateFigureForNode((InternalNodeLayout) node);
	}

	private void updateFigureForNode(InternalNodeLayout internalNode) {
		IFigure figure = internalNode.getNode().getFigure();
		GraphLabel label = (GraphLabel) nodeFigureToLabel.get(figure);
		if (label == null) {
			label = new GraphLabel(false);
			label.setForegroundColor(ColorConstants.white);
			label.setBackgroundColor(ColorConstants.red);
			FontData fontData = Display.getDefault().getSystemFont().getFontData()[0];
			fontData.setHeight(6);
			label.setFont(new Font(Display.getCurrent(), fontData));
			figure.addFigureListener(nodeFigureListener);
			context.container.addSubgraphFigure(label);
			nodeFigureToLabel.put(figure, label);
			nodeFigureToNode.put(figure, internalNode);
		}
		if (internalNode.isMinimized()) {
			DisplayIndependentPoint location = internalNode.getLocation();
			label.getParent().setConstraint(label, new Rectangle((int) location.x, (int) location.y, 0, 0));
		} else {
			int numberOfSuccessors = getUnfilteredOutgoingConnections(internalNode).length;
			label.setVisible(numberOfSuccessors > 0 && !isExpanded(internalNode));
			label.setText("" + numberOfSuccessors);
			Dimension size = label.getSize();
			size.expand(-6, -4);
			Point anchorPoint = figure.getBounds().getBottomRight();
			anchorPoint.x -= size.width / 2;
			anchorPoint.y -= size.height / 2;
			label.getParent().setConstraint(label, new Rectangle(anchorPoint, size));
		}
	}

	private void removeFigureForNode(InternalNodeLayout internalNode) {
		IFigure figure = internalNode.getNode().getFigure();
		GraphLabel label = (GraphLabel) nodeFigureToLabel.get(figure);
		if (label != null) {
			label.getParent().remove(label);
		}
	}
}
