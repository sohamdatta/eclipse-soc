package org.eclipse.zest.layout.interfaces;

public interface SubgraphFactory {
	SubgraphLayout createSubgraph(NodeLayout[] nodes, LayoutContext context);
}
