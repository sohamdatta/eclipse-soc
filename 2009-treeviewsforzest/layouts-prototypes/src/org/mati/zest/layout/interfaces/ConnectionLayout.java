package org.mati.zest.layout.interfaces;

import org.eclipse.zest.layouts.dataStructures.DisplayIndependentPoint;

public interface ConnectionLayout {
	public NodeLayout getSource();

	public NodeLayout getTarget();

	public DisplayIndependentPoint[] getBendingPoints();

	public void setBendingPoints(DisplayIndependentPoint[] points);
}
