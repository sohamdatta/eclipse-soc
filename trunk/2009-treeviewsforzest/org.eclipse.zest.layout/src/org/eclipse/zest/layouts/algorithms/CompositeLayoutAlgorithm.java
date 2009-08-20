/*******************************************************************************
 * Copyright (c) 2005-2009 The Chisel Group and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: The Chisel Group - initial API and implementation
 *               Mateusz Matela 
 *               Ian Bull
 ******************************************************************************/
package org.eclipse.zest.layouts.algorithms;

import org.eclipse.zest.layouts.LayoutAlgorithm;
import org.eclipse.zest.layouts.interfaces.LayoutContext;

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
