package org.eclipse.zest.core.widgets;

import org.eclipse.zest.layout.interfaces.SubgraphLayout;

public interface SubgraphFactory {
	SubgraphLayout createSubgraph(InternalNodeLayout[] nodes, InternalLayoutContext context);
}
