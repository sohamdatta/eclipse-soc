package org.eclipse.zest.layout.algorithms;

import org.eclipse.zest.layout.interfaces.LayoutAlgorithm;
import org.eclipse.zest.layout.interfaces.LayoutContext;

public class CompositeLayoutAlgorithm implements LayoutAlgorithm {

	private LayoutAlgorithm[] algorithms = null;

	public CompositeLayoutAlgorithm(LayoutAlgorithm[] algorithms) {
		this.algorithms = algorithms;
	}

	public void applyLayout(boolean clean) {
		for (int i = 0; i < algorithms.length; i++) {
			algorithms[i].applyLayout(clean);
		}
	}

	public void setLayoutContext(LayoutContext context) {
		for (int i = 0; i < algorithms.length; i++) {
			algorithms[i].setLayoutContext(context);
		}
	}
}
