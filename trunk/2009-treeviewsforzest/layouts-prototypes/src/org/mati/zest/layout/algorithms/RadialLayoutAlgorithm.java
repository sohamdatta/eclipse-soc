package org.mati.zest.layout.algorithms;

import org.eclipse.zest.layouts.dataStructures.DisplayIndependentPoint;
import org.eclipse.zest.layouts.dataStructures.DisplayIndependentRectangle;
import org.mati.zest.layout.interfaces.EntityLayout;
import org.mati.zest.layout.interfaces.LayoutAlgorithm;
import org.mati.zest.layout.interfaces.LayoutContext;

/**
 * This layout will take the given entities, apply a tree layout to them, and
 * then display the tree in a circular fashion with the roots in the center.
 * 
 * @author Casey Best
 * @auhtor Rob Lintern
 */
public class RadialLayoutAlgorithm implements LayoutAlgorithm {

	private static final double MAX_DEGREES = Math.PI * 2;
	private double startDegree = 0;
	private double endDegree = MAX_DEGREES;

	private LayoutContext context;
	private boolean resize = false;

	private TreeLayoutAlgorithm treeLayout = new TreeLayoutAlgorithm();
	
	public void applyLayout() {
		EntityLayout[] entities = context.getEntities();
		treeLayout.internalApplyLayout(entities);
		DisplayIndependentRectangle bounds = context.getBounds();
		computeRadialPositions(entities, bounds);
		if (resize)
			AlgorithmHelper.maximizeSizes(entities);
		AlgorithmHelper.fitWithinBounds(entities, bounds, resize);
	}

	private void computeRadialPositions(EntityLayout[] entities, DisplayIndependentRectangle bounds) {
		DisplayIndependentRectangle layoutBounds = AlgorithmHelper.getLayoutBounds(entities, false);
		layoutBounds.x = bounds.x;
		layoutBounds.width = bounds.width;
		for (int i = 0; i < entities.length; i++) {
			DisplayIndependentPoint location = entities[i].getLocation();
			double percenttheta = (location.x - layoutBounds.x) / layoutBounds.width;
			double distance = (location.y - layoutBounds.y) / layoutBounds.height;
			double theta = startDegree + Math.abs(endDegree - startDegree) * percenttheta;
			location.x = distance * Math.cos(theta);
			location.y = distance * Math.sin(theta);
			entities[i].setLocation(location.x, location.y);
		}
	}

	public void setLayoutContext(LayoutContext context) {
		this.context = context;
		treeLayout.setLayoutContext(context);
	}

	/**
	 * Set the range the radial layout will use when applyLayout is called. Both
	 * values must be in radians.
	 */
	public void setRangeToLayout(double startDegree, double endDegree) {
		this.startDegree = startDegree;
		this.endDegree = endDegree;
	}

	/**
	 * 
	 * @return true if this algorithm is set to resize elements
	 */
	public boolean isResizing() {
		return resize;
	}

	/**
	 * 
	 * @param resizing
	 *            true if this algorithm should resize elements (default is
	 *            false)
	 */
	public void setResizing(boolean resizing) {
		resize = resizing;
		treeLayout.setResizing(resize);
	}
}
