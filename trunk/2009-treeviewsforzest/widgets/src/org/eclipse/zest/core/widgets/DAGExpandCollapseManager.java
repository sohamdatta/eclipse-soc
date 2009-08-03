package org.eclipse.zest.core.widgets;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import org.eclipse.draw2d.AncestorListener;
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
import org.eclipse.zest.core.widgets.internal.ZestRootLayer;
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
 * </p>
 * <p>
 * <b>NOTE:</b> A <code>Graph</code> using this manger should use
 * {@link DummySubgraphLayout}, which doesn't show any information about
 * subgraphs in the graph. That's because for this manager it doesn't matter
 * which subgraph a node belongs to (each pruning creates a new subgraph). Also,
 * this manager adds a label to each collapsed node showing number of its
 * successors.
 * </p>
 * One instance of this class can serve only one instance of <code>Graph</code>.
 */
public class DAGExpandCollapseManager implements ExpandCollapseManager {

	private class LabelAncestorListener extends AncestorListener.Stub {
		private final IFigure originalFigure;
		private IFigure fisheyeFigure;

		public LabelAncestorListener(IFigure originalFigure, IFigure fisheyeFigure) {
			this.originalFigure = originalFigure;
			this.fisheyeFigure = fisheyeFigure;
		}

		public void ancestorRemoved(IFigure ancestor) {
			if (fisheyeFigure != null) {
				final GraphLabel label = (GraphLabel) nodeFigureToLabel.get(fisheyeFigure);
				if (label == null)
					return;
				nodeFigureToLabel.remove(fisheyeFigure);
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						label.removeAncestorListener(LabelAncestorListener.this);
					}
				});
				fisheyeFigure.removeFigureListener(nodeFigureListener);
				originalFigure.addFigureListener(nodeFigureListener);
				labelToAncestorListener.remove(label);
				fisheyeFigure = null;
				addLabelForFigure(originalFigure, label);
				refreshLabelBounds(originalFigure, label);
			}
		}
	}

	private InternalLayoutContext context;

	private HashSet expandedNodes = new HashSet();

	private HashSet nodesToPrune = new HashSet();

	private HashSet nodesToUnprune = new HashSet();

	private HashSet connectionsToHide = new HashSet();

	private HashSet connectionsToShow = new HashSet();

	private HashSet nodeLabelsToUpdate = new HashSet();

	private boolean cleanLayoutScheduled = false;

	/**
	 * Maps from figures of nodes to labels showing number of nodes hidden
	 * successors
	 */
	private HashMap nodeFigureToLabel = new HashMap();

	private HashMap labelToAncestorListener = new HashMap();

	private final FigureListener nodeFigureListener = new FigureListener() {
		public void figureMoved(IFigure source) {
			GraphLabel label = (GraphLabel) nodeFigureToLabel.get(source);
			refreshLabelBounds(source, label);
		}
	};

	public void initExpansion(final LayoutContext context2) {
		if (!(context2 instanceof InternalLayoutContext))
			throw new RuntimeException("This manager works only with org.eclipse.zest.core.widgets.InternalLayoutContext");
		context = (InternalLayoutContext) context2;

		context.addGraphStructureListener(new GraphStructureListener() {
			public boolean nodeRemoved(LayoutContext context, NodeLayout node) {
				if (isExpanded(node)) {
					collapse(node);
				}
				removeFigureForNode((InternalNodeLayout) node);
				flushChanges(false, true);
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
				if (!isExpanded(target) && target.getIncomingConnections().length == 0) {
					expand(target);
				}
				updateNodeLabel((InternalNodeLayout) source);
				flushChanges(false, true);
				return false;
			}

			public boolean connectionAdded(LayoutContext context, ConnectionLayout connection) {
				NodeLayout source = connection.getSource();
				NodeLayout target = connection.getTarget();
				resetState(target);
				updateNodeLabel((InternalNodeLayout) source);
				if (!isPruned(target) && !isPruned(source))
					showConnection(connection);
				else
					hideConnection(connection);

				flushChanges(false, true);
				updateNodeLabel((InternalNodeLayout) source);
				return false;
			}

		});

		context.addContextListener(new ContextListener.Stub() {
			public void backgroundEnableChanged(LayoutContext context) {
				flushChanges(false, false);
			}
		});

		context.container.getGraph().addFisheyeListener(new FisheyeListener() {

			public void fisheyeReplaced(Graph graph, IFigure oldFisheyeFigure, IFigure newFisheyeFigure) {
				oldFisheyeFigure.removeFigureListener(nodeFigureListener);
				newFisheyeFigure.addFigureListener(nodeFigureListener);
				GraphLabel label = (GraphLabel) nodeFigureToLabel.remove(oldFisheyeFigure);
				nodeFigureToLabel.put(newFisheyeFigure, label);

				LabelAncestorListener ancestorListener = (LabelAncestorListener) labelToAncestorListener.get(label);
				ancestorListener.fisheyeFigure = null;
				addLabelForFigure(newFisheyeFigure, label);
				ancestorListener.fisheyeFigure = newFisheyeFigure;
				refreshLabelBounds(newFisheyeFigure, label);
			}

			public void fisheyeRemoved(Graph graph, IFigure originalFigure, IFigure fisheyeFigure) {
				// do nothing - labelAncestorListener will take care of cleaning
				// up
			}

			public void fisheyeAdded(Graph graph, IFigure originalFigure, IFigure fisheyeFigure) {
				originalFigure.removeFigureListener(nodeFigureListener);
				fisheyeFigure.addFigureListener(nodeFigureListener);
				GraphLabel label = (GraphLabel) nodeFigureToLabel.get(originalFigure);
				if (label == null)
					return;
				nodeFigureToLabel.put(fisheyeFigure, label);
				refreshLabelBounds(fisheyeFigure, label);
				addLabelForFigure(fisheyeFigure, label);
				LabelAncestorListener labelAncestorListener = new LabelAncestorListener(originalFigure, fisheyeFigure);
				label.addAncestorListener(labelAncestorListener);
				labelToAncestorListener.put(label, labelAncestorListener);
			}
		});
	}

	public boolean canCollapse(LayoutContext context, NodeLayout node) {
		return isExpanded(node) && !node.isPruned() && node.getOutgoingConnections().length > 0;
	}

	public boolean canExpand(LayoutContext context, NodeLayout node) {
		return !isExpanded(node) && !node.isPruned() && node.getOutgoingConnections().length > 0;
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
		updateNodeLabel((InternalNodeLayout) node);
		NodeLayout[] successingNodes = node.getSuccessingNodes();
		for (int i = 0; i < successingNodes.length; i++) {
			unpruneNode(successingNodes[i]);
		}
	}

	private void collapse(NodeLayout node) {
		if (isExpanded(node))
			setExpanded(node, false);
		else
			return;
		NodeLayout[] successors = node.getSuccessingNodes();
		for (int i = 0; i < successors.length; i++) {
			checkPruning(successors[i]);
			if (isPruned(successors[i]))
				collapse(successors[i]);
		}
	}

	private void checkPruning(NodeLayout node) {
		boolean prune = true;
		NodeLayout[] predecessors = node.getPredecessingNodes();
		for (int j = 0; j < predecessors.length; j++) {
			if (isExpanded(predecessors[j])) {
				prune = false;
				break;
			}
		}
		if (prune)
			pruneNode(node);
		else
			unpruneNode(node);
	}

	/**
	 * By default nodes at the top are expanded. The rest are collapsed and
	 * pruned if they don't have any expanded predecessors
	 * 
	 * @param target
	 */
	private void resetState(NodeLayout node) {
		NodeLayout[] predecessors = node.getPredecessingNodes();
		if (predecessors.length == 0)
			expand(node);
		else {
			collapse(node);
			checkPruning(node);
		}
	}

	private void pruneNode(NodeLayout node) {
		if (isPruned(node))
			return;
		nodesToUnprune.remove(node);
		nodesToPrune.add(node);
		ConnectionLayout[] incoming = node.getIncomingConnections();
		for (int i = 0; i < incoming.length; i++) {
			hideConnection(incoming[i]);
			updateNodeLabel((InternalNodeLayout) incoming[i].getSource());
		}
		ConnectionLayout[] outgoing = node.getOutgoingConnections();
		for (int i = 0; i < outgoing.length; i++) {
			hideConnection(outgoing[i]);
		}
	}

	private void unpruneNode(NodeLayout node) {
		if (!isPruned(node))
			return;
		nodesToPrune.remove(node);
		nodesToUnprune.add(node);
		updateNodeLabel((InternalNodeLayout) node);
		ConnectionLayout[] incoming = node.getIncomingConnections();
		for (int i = 0; i < incoming.length; i++) {
			if (!isPruned(incoming[i].getSource()))
				showConnection(incoming[i]);
			updateNodeLabel((InternalNodeLayout) incoming[i].getSource());
		}
		ConnectionLayout[] outgoing = node.getOutgoingConnections();
		for (int i = 0; i < outgoing.length; i++) {
			if (!isPruned(outgoing[i].getTarget()))
				showConnection(outgoing[i]);
		}
	}

	private void showConnection(ConnectionLayout connection) {
		connectionsToHide.remove(connection);
		connectionsToShow.add(connection);
	}

	private void hideConnection(ConnectionLayout connection) {
		connectionsToShow.remove(connection);
		connectionsToHide.add(connection);
	}

	private boolean isPruned(NodeLayout node) {
		if (nodesToUnprune.contains(node))
			return false;
		if (nodesToPrune.contains(node))
			return true;
		return node.isPruned();
	}

	private void flushChanges(boolean force, boolean clean) {
		cleanLayoutScheduled = cleanLayoutScheduled || clean;
		if (!force && !context.isBackgroundLayoutEnabled()) {
			return;
		}

		for (Iterator iterator = nodeLabelsToUpdate.iterator(); iterator.hasNext();) {
			InternalNodeLayout node = (InternalNodeLayout) iterator.next();
			updateNodeLabel2(node);
		}
		nodeLabelsToUpdate.clear();

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

	private boolean isExpanded(NodeLayout node) {
		return expandedNodes.contains(node);
	}

	private void setExpanded(NodeLayout node, boolean expanded) {
		if (expanded) {
			expandedNodes.add(node);
		} else {
			expandedNodes.remove(node);
		}
		updateNodeLabel((InternalNodeLayout) node);
	}

	private void updateNodeLabel(InternalNodeLayout internalNode) {
		nodeLabelsToUpdate.add(internalNode);
	}

	private void updateNodeLabel2(InternalNodeLayout internalNode) {
		IFigure figure = internalNode.getNode().getFigure();
		GraphLabel label = (GraphLabel) nodeFigureToLabel.get(figure);
		IFigure fisheye = getFisheyeFigure(figure);
		if (fisheye != null)
			figure = fisheye;
		if (label == null) {
			label = new GraphLabel(false);
			label.setForegroundColor(ColorConstants.white);
			label.setBackgroundColor(ColorConstants.red);
			FontData fontData = Display.getDefault().getSystemFont().getFontData()[0];
			fontData.setHeight(6);
			label.setFont(new Font(Display.getCurrent(), fontData));
			figure.addFigureListener(nodeFigureListener);
			addLabelForFigure(figure, label);
			nodeFigureToLabel.put(figure, label);
		}

		NodeLayout[] successors = internalNode.getSuccessingNodes();
		if (isExpanded(internalNode) || isPruned(internalNode) || successors.length == 0) {
			label.setVisible(false);
		} else {
			int numberOfHiddenSuccessors = 0;
			for (int i = 0; i < successors.length; i++) {
				if (isPruned(successors[i]))
					numberOfHiddenSuccessors++;
			}
			label.setText(numberOfHiddenSuccessors > 0 ? "" + numberOfHiddenSuccessors : "");
			label.setVisible(true);
		}

		refreshLabelBounds(figure, label);
	}

	private IFigure getFisheyeFigure(IFigure originalFigure) {
		// a node has a fisheye if and only if its label has an AncestorListener
		GraphLabel label = (GraphLabel) nodeFigureToLabel.get(originalFigure);
		LabelAncestorListener ancestorListener = (LabelAncestorListener) labelToAncestorListener.get(label);
		if (ancestorListener != null)
			return ancestorListener.fisheyeFigure;
		return null;
	}

	private void addLabelForFigure(IFigure figure, GraphLabel label) {
		IFigure parent = figure.getParent();
		if (parent instanceof ZestRootLayer) {
			((ZestRootLayer) parent).addDecoration(figure, label);
		} else {
			if (parent.getChildren().contains(label))
				parent.remove(label);
			int index = parent.getChildren().indexOf(figure);
			parent.add(label, index + 1);
		}
	}

	private void refreshLabelBounds(IFigure figure, GraphLabel label) {
		Rectangle figureBounds = figure.getBounds();
		if (figureBounds.width * figureBounds.height > 0) {
			label.setText(label.getText()); // hack: resets label's size
			Dimension labelSize = label.getSize();
			labelSize.expand(-6, -4);
			Point anchorPoint = figure.getBounds().getBottomRight();
			anchorPoint.x -= labelSize.width / 2;
			anchorPoint.y -= labelSize.height / 2;
			Rectangle bounds = new Rectangle(anchorPoint, labelSize);
			label.setBounds(bounds);
			label.getParent().setConstraint(label, bounds);
		} else {
			label.getParent().setConstraint(label, new Rectangle(figureBounds.x, figureBounds.y, 0, 0));
			label.setBounds(new Rectangle(figureBounds.x, figureBounds.y, 0, 0));
		}
	}

	private void removeFigureForNode(InternalNodeLayout internalNode) {
		IFigure figure = internalNode.getNode().getFigure();
		GraphLabel label = (GraphLabel) nodeFigureToLabel.get(figure);
		if (label != null && label.getParent() != null) {
			label.getParent().remove(label);
		}
		nodeFigureToLabel.remove(figure);
		nodeLabelsToUpdate.remove(internalNode);
	}
}
