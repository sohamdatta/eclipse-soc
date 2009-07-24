/**
 * 
 */
package org.eclipse.zest.layout.interfaces;

/**
 * A manager that controls expanding and collapsing nodes in a Graph.
 */
public interface ExpandCollapseManager {

	/**
	 * Changes the expanded state of given node. It prunes/unprunes nodes and
	 * hides/shows connections in the graph according to its policy. If
	 * requested operation cannot be currently performed on the node, it does
	 * nothing.
	 * 
	 * @param context
	 *            context in which to perform the operation
	 * @param node
	 *            node to expand or collapse
	 * @param expanded
	 *            true to expand, false to collapse
	 */
	public void setExpanded(LayoutContext context, NodeLayout node, boolean expanded);

	/**
	 * Checks if given node can be expanded.
	 * 
	 * @param context
	 *            context containing the node
	 * @param node
	 *            node to check
	 * @return
	 */
	public boolean canExpand(LayoutContext context, NodeLayout node);

	/**
	 * Checks if given node can be collapsed.
	 * 
	 * @param context
	 *            context containing the node
	 * @param node
	 *            node to check
	 * @return
	 */
	public boolean canCollapse(LayoutContext context, NodeLayout node);
}