package org.mati.zest.core.widgets;

import org.eclipse.zest.layout.interfaces.NodeLayout;
import org.eclipse.zest.layout.interfaces.SubgraphLayout;

public interface SubgraphFactory {
	SubgraphLayout createSubgraph(NodeLayout[] nodes, InternalLayoutContext context);
}
