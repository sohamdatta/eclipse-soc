/*******************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation Chisel Group,
 * University of Victoria - Adapted for XY Scaled Graphics
 ******************************************************************************/
package org.eclipse.zest.core.widgets.internal;

import org.eclipse.draw2d.FreeformLayer;
import org.eclipse.draw2d.IFigure;

/**
 * The root figure for Zest. The figure is broken up into following segments:
 * <ol>
 * <li>The Connections</li>
 * <li>The Subgraphs</li>
 * <li>The Nodes</li>
 * <li>The Highlighted Connections</li>
 * <li>The Highlighted Nodes</li></ol>
 * 
 */
public class ZestRootLayer extends FreeformLayer {

	public static final int CONNECTIONS_LAYER = 0;
	
	public static final int SUBGRAPHS_LAYER = 4;

	public static final int NODES_LAYER = 1;
	
	public static final int CONNECTIONS_HIGHLIGHTED_LAYER = 2;
	
	public static final int NODES_HIGHLIGHTED_LAYER = 3;
	
	public static final int NUMBER_OF_LAYERS = 5;
	
	private final int[] itemsInLayer = new int[NUMBER_OF_LAYERS];

	/**
	 * Adds a node to the ZestRootLayer
	 * @param nodeFigure The figure representing the node
	 */
	public void addNode(IFigure nodeFigure) {
		addFigure(nodeFigure, NODES_LAYER);
	}

	public void addConnection(IFigure connectionFigure) {
		addFigure(connectionFigure, CONNECTIONS_LAYER);
	}

	public void addSubgraph(IFigure subgraphFigrue) {
		addFigure(subgraphFigrue, SUBGRAPHS_LAYER);
	}

	public void highlightNode(IFigure nodeFigure) {
		remove(nodeFigure);
		addFigure(nodeFigure, NODES_HIGHLIGHTED_LAYER);
		this.invalidate();
		this.repaint();
	}

	public void highlightConnection(IFigure connectionFigure) {
		remove(connectionFigure);
		addFigure(connectionFigure, CONNECTIONS_HIGHLIGHTED_LAYER);
		this.invalidate();
		this.repaint();
	}

	public void unHighlightNode(IFigure nodeFigure) {
		remove(nodeFigure);
		addFigure(nodeFigure, NODES_LAYER);
		this.invalidate();
		this.repaint();
	}

	public void unHighlightConnection(IFigure connectionFigure) {
		remove(connectionFigure);
		addFigure(connectionFigure, CONNECTIONS_LAYER);
		this.invalidate();
		this.repaint();
	}
	
	/**
	 * 
	 * @param layer
	 * @return position after the last element in given layer
	 */
	private int getPosition(int layer) {
		int result = 0;
		for(int i = 0; i <= layer; i++)
			result += itemsInLayer[i];
		return result;
	}
	
	public void addFigure(IFigure figure, int layer) {
		int position = getPosition(layer);
		itemsInLayer[layer]++;
		add(figure, position);
	}

	public void remove(IFigure child) {
		int position = this.getChildren().indexOf(child);
		if (position == -1)
			throw new RuntimeException("Can't remove a figure that is not on ZestRootLayer");
		int layer = 0;
		int positionInLayer = itemsInLayer[0];
		while (positionInLayer <= position) {
			layer++;
			positionInLayer += itemsInLayer[layer];
		}
		itemsInLayer[layer]--;
		super.remove(child);
	}
}
