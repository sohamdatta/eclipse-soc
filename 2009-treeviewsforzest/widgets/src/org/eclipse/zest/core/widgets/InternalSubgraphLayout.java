package org.eclipse.zest.core.widgets;

import org.eclipse.zest.layout.interfaces.NodeLayout;
import org.eclipse.zest.layout.interfaces.SubgraphLayout;

/**
 * Extension for {@link SubgraphLayout} containing maintenance methods that
 * should not be used for layout algorithms but are necessary for proper working
 * of layout context.
 */
public interface InternalSubgraphLayout extends SubgraphLayout {

	/**
	 * Removes disposed nodes from this subgraph (only
	 * {@link InternalNodeLayout}s can be disposed). The state of removed nodes
	 * is not updated as in case of
	 * {@link SubgraphLayout#removeNodes(NodeLayout[])}.
	 */
	public void removeDisposedNodes();
}
