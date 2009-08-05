package org.eclipse.zest.core.widgets;

import org.eclipse.zest.layout.interfaces.LayoutContext;
import org.eclipse.zest.layout.interfaces.NodeLayout;
import org.eclipse.zest.layout.interfaces.SubgraphLayout;

/**
 * Factory used by {@link Graph} to create subgraphs. One instance of
 * SubgraphFactory can be used with multiple graphs unless explicitly stated
 * otherwise.
 */
public interface SubgraphFactory {
	SubgraphLayout createSubgraph(NodeLayout[] nodes, LayoutContext context);
}
