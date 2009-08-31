/*******************************************************************************
 * Copyright (c) 2009 Mateusz Matela and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Mateusz Matela - initial API and implementation
 *               Ian Bull
 ******************************************************************************/
package org.eclipse.zest.core.widgets;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.zest.layouts.interfaces.ConnectionLayout;
import org.eclipse.zest.layouts.interfaces.ContextListener;
import org.eclipse.zest.layouts.interfaces.ExpandCollapseManager;
import org.eclipse.zest.layouts.interfaces.GraphStructureListener;
import org.eclipse.zest.layouts.interfaces.LayoutContext;
import org.eclipse.zest.layouts.interfaces.NodeLayout;

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
 * {@link DefaultSubgraph}, which doesn't show any information about subgraphs
 * in the graph. That's because for this manager it doesn't matter which
 * subgraph a node belongs to (each pruning creates a new subgraph). Also, this
 * manager adds a label to each collapsed node showing number of its successors.
 * </p>
 * One instance of this class can serve only one instance of <code>Graph</code>.
 * 
 * @since 2.0
 */
public class DAGExpandCollapseManager implements ExpandCollapseManager {

	private InternalLayoutContext context;

	private HashSet expandedNodes = new HashSet();

	private HashSet nodesToPrune = new HashSet();

	private HashSet nodesToUnprune = new HashSet();

	private HashSet nodesToUpdate = new HashSet();

	private HashMap connectionsToChangeVisibility = new HashMap();

	private boolean cleanLayoutScheduled = false;

	private boolean hidingConnections = false;

	public void initExpansion(final LayoutContext context2) {
		if (!(context2 instanceof InternalLayoutContext)) {
			throw new RuntimeException(
					"This manager works only with org.eclipse.zest.core.widgets.InternalLayoutContext");
		}
		context = (InternalLayoutContext) context2;

		context.addGraphStructureListener(new GraphStructureListener.Stub() {
			public boolean nodeRemoved(LayoutContext context, NodeLayout node) {
				if (isExpanded(node)) {
					collapse(node);
				}
				flushChanges(false, true);
				return false;
			}

			public boolean nodeAdded(LayoutContext context, NodeLayout node) {
				resetState(node);
				flushChanges(false, true);
				return false;
			}

			public boolean connectionRemoved(LayoutContext context,
					ConnectionLayout connection) {
				NodeLayout target = connection.getTarget();
				if (!isExpanded(target)
						&& target.getIncomingConnections().length == 0) {
					expand(target);
				}
				flushChanges(false, true);
				return false;
			}

			public boolean connectionAdded(LayoutContext context,
					ConnectionLayout connection) {
				if (!connection.isDirected()) {
					throw new RuntimeException(
							"Only directed connections can be used with DAGExpandCollapseManager");
				}
				resetState(connection.getTarget());
				updateNodeLabel(connection.getSource());
				refreshConnectionsVisibility(connection.getSource());
				flushChanges(false, true);
				return false;
			}

		});

		context.addContextListener(new ContextListener.Stub() {
			public void backgroundEnableChanged(LayoutContext context) {
				flushChanges(false, false);
			}
		});
	}

	public boolean canCollapse(LayoutContext context, NodeLayout node) {
		return isExpanded(node) && !node.isPruned()
				&& node.getOutgoingConnections().length > 0;
	}

	public boolean canExpand(LayoutContext context, NodeLayout node) {
		return !isExpanded(node) && !node.isPruned()
				&& node.getOutgoingConnections().length > 0;
	}

	public void setExpanded(LayoutContext context, NodeLayout node,
			boolean expanded) {

		if (isExpanded(node) == expanded) {
			return;
		}
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

	/**
	 * Returns true if this collapse manager hides all outgoing connections of
	 * collapsed nodes. Returns false if connections pointing to a node that is
	 * not pruned (because it as predecessing node which is expanded) stay
	 * visible.
	 * 
	 * @return whether or not all connections going out of collapsed nodes are
	 *         hidden.
	 */
	public boolean isHidingConnections() {
		return hidingConnections;
	}

	/**
	 * Changes the way connections outgoing from collapsed nodes are hidden. If
	 * set to true, all such connections are always hidden. If set to false, a
	 * connection is visible if a node it points to is not pruned (which means
	 * that node has another predecessing node which is expanded).
	 * 
	 * Default is false.
	 * 
	 * @param hidingConnections
	 *            true if all outgoing connections of collapsed nodes should be
	 *            hidden.
	 */
	public void setHidingConnections(boolean hidingConnections) {
		this.hidingConnections = hidingConnections;
		NodeLayout[] nodes = context.getNodes();
		for (int i = 0; i < nodes.length; i++) {
			refreshConnectionsVisibility(nodes[i]);
		}
		flushChanges(false, false);
	}

	private void refreshConnectionsVisibility(NodeLayout node) {
		ConnectionLayout[] outgoingConnections = node.getOutgoingConnections();
		for (int i = 0; i < outgoingConnections.length; i++) {
			setConnectionVisible(
					outgoingConnections[i],
					isExpanded(node)
							|| (!hidingConnections && !isPruned(node) && !isPruned(outgoingConnections[i]
									.getTarget())));
		}
	}

	private void expand(NodeLayout node) {
		setExpanded(node, true);
		NodeLayout[] successingNodes = node.getSuccessingNodes();
		for (int i = 0; i < successingNodes.length; i++) {
			unpruneNode(successingNodes[i]);
		}
		updateNodeLabel(node);
		refreshConnectionsVisibility(node);
	}

	private void collapse(NodeLayout node) {
		if (isExpanded(node)) {
			setExpanded(node, false);
		} else {
			return;
		}
		NodeLayout[] successors = node.getSuccessingNodes();
		for (int i = 0; i < successors.length; i++) {
			checkPruning(successors[i]);
			if (isPruned(successors[i])) {
				collapse(successors[i]);
			}
		}
		updateNodeLabel(node);
		refreshConnectionsVisibility(node);
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
		if (prune) {
			pruneNode(node);
		} else {
			unpruneNode(node);
		}
	}

	/**
	 * By default nodes at the top (having no predecessors) are expanded. The
	 * rest are collapsed and pruned if they don't have any expanded
	 * predecessors
	 * 
	 * @param target
	 */
	private void resetState(NodeLayout node) {
		NodeLayout[] predecessors = node.getPredecessingNodes();
		if (predecessors.length == 0) {
			expand(node);
		} else {
			collapse(node);
			checkPruning(node);
		}
	}

	/**
	 * If given node belongs to a layout context using
	 * {@link PrunedSuccessorsSubgraph}, update of the nodes's label is forced.
	 * 
	 * @param node
	 *            node to update
	 */
	private void updateNodeLabel(NodeLayout node) {
		nodesToUpdate.add(node);
	}

	private void updateNodeLabel2(InternalNodeLayout node) {
		SubgraphFactory subgraphFactory = node.getOwnerLayoutContext()
				.getSubgraphFactory();
		if (subgraphFactory instanceof DefaultSubgraph.PrunedSuccessorsSubgraphFactory) {
			((DefaultSubgraph.PrunedSuccessorsSubgraphFactory) subgraphFactory)
					.updateLabelForNode(node);
		}
	}

	private void pruneNode(NodeLayout node) {
		if (isPruned(node)) {
			return;
		}
		nodesToUnprune.remove(node);
		nodesToPrune.add(node);
	}

	private void unpruneNode(NodeLayout node) {
		if (!isPruned(node)) {
			return;
		}
		nodesToPrune.remove(node);
		nodesToUnprune.add(node);
	}

	private void setConnectionVisible(ConnectionLayout connection,
			boolean visible) {
		if (connection.isVisible() == visible) {
			return;
		}
		connectionsToChangeVisibility.put(connection, new Boolean(visible));
	}

	private boolean isPruned(NodeLayout node) {
		if (nodesToUnprune.contains(node)) {
			return false;
		}
		if (nodesToPrune.contains(node)) {
			return true;
		}
		return node.isPruned();
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
			context.createSubgraph((NodeLayout[]) nodesToPrune
					.toArray(new NodeLayout[nodesToPrune.size()]));
			nodesToPrune.clear();
		}

		for (Iterator iterator = nodesToUpdate.iterator(); iterator.hasNext();) {
			InternalNodeLayout node = (InternalNodeLayout) iterator.next();
			updateNodeLabel2(node);
		}
		nodesToUpdate.clear();

		for (Iterator iterator = connectionsToChangeVisibility.entrySet()
				.iterator(); iterator.hasNext();) {
			Map.Entry entry = (Map.Entry) iterator.next();
			ConnectionLayout connection = (ConnectionLayout) entry.getKey();
			Boolean visible = (Boolean) entry.getValue();
			connection.setVisible(visible.booleanValue());
		}

		context.applyLayout(cleanLayoutScheduled);
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
	}
}
