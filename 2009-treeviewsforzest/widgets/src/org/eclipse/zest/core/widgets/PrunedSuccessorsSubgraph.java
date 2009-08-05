package org.eclipse.zest.core.widgets;

import java.util.HashMap;

import org.eclipse.zest.layout.interfaces.LayoutContext;
import org.eclipse.zest.layout.interfaces.NodeLayout;
import org.eclipse.zest.layout.interfaces.SubgraphLayout;

/**
 * A subgraph that for each unexpanded node in a graph adds a label showing
 * number of pruned successors. It doesn't matter which subgraph a node is
 * pruned to, so the factory for this subgraph uses one instance for whole
 * layout context.
 */
public class PrunedSuccessorsSubgraph extends DefaultSubgraph {

	/**
	 * Factory for {@link PrunedSuccessorsSubgraph}. It creates one subgraph for a
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
	
	protected PrunedSuccessorsSubgraph(LayoutContext context2) {
		super(context2);
	}

}
