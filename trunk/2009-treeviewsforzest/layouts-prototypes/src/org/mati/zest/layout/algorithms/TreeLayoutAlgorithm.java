/*******************************************************************************
 * Copyright 2005, CHISEL Group, University of Victoria, Victoria, BC, Canada.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     The Chisel Group, University of Victoria
 *******************************************************************************/
package org.mati.zest.layout.algorithms;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.zest.layouts.dataStructures.DisplayIndependentRectangle;
import org.mati.zest.layout.interfaces.EntityLayout;
import org.mati.zest.layout.interfaces.LayoutAlgorithm;
import org.mati.zest.layout.interfaces.LayoutContext;
import org.mati.zest.layout.interfaces.NodeLayout;

import sun.misc.Queue;

/**
 * The TreeLayoutAlgorithm class implements a simple algorithm to arrange graph
 * nodes in a layered tree-like layout.
 * 
 * @version 3.0
 * @author Mateusz Matela
 * @author Casey Best and Rob Lintern (version 2.0)
 * @author Jingwei Wu (version 1.0)
 */
public class TreeLayoutAlgorithm implements LayoutAlgorithm {

	/**
	 * Tree direction constant for which root is placed at the top and branches
	 * spread downwards
	 */
	public final static int TOP_DOWN = 1;

	/**
	 * Tree direction constant for which root is placed at the bottom and
	 * branches spread upwards
	 */
	public final static int BOTTOM_UP = 2;

	/**
	 * Tree direction constant for which root is placed at the left and branches
	 * spread to the right
	 */
	public final static int LEFT_RIGHT = 3;

	/**
	 * Tree direction constant for which root is placed at the right and
	 * branches spread to the left
	 */
	public final static int RIGHT_LEFT = 4;

	private class EntityInfo {
		public EntityInfo(EntityLayout entity) {
			this.entity = entity;
		}

		public void addChild(EntityInfo child) {
			children.add(child);
		}

		public void precomputeTree() {
			if (children.isEmpty()) {
				numOfLeaves = 1;
			} else
			for (Iterator iterator = children.iterator(); iterator.hasNext();) {
				EntityInfo child = (EntityInfo) iterator.next();
					child.depth = this.depth + 1;
				child.precomputeTree();
				this.numOfLeaves += child.numOfLeaves;
				this.height = Math.max(this.height, child.height + 1);
			}
		}

		final public EntityLayout entity;
		public int height = 0;
		public int depth = -1;
		public int numOfLeaves = 0;
		public final List children = new ArrayList();
	}

	private int direction = TOP_DOWN;

	private boolean resize = false;

	private LayoutContext context;

	private DisplayIndependentRectangle bounds;

	private double leafSize, layerSize;

	private EntityInfo superRoot;

	public TreeLayoutAlgorithm() {
	}

	TreeLayoutAlgorithm(int direction) {
		setDirection(direction);
	}

	public int getDirection() {
		return direction;
	}

	public void setDirection(int direction) {
		if (direction == TOP_DOWN || direction == BOTTOM_UP || direction == LEFT_RIGHT || direction == RIGHT_LEFT)
			this.direction = direction;
		else
			throw new RuntimeException("Invalid direction: " + direction);
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
	}

	public void setLayoutContext(LayoutContext context) {
		this.context = context;
	}

	public void applyLayout() {
		EntityLayout[] entities = context.getEntities();

		internalApplyLayout(entities);

		if (resize)
			AlgorithmHelper.maximizeSizes(entities);
		AlgorithmHelper.fitWithinBounds(entities, bounds, resize);
	}

	void internalApplyLayout(EntityLayout[] entities) {
		superRoot = new EntityInfo(null);
		createTrees(entities);

		bounds = context.getBounds();
		if (direction == TOP_DOWN || direction == BOTTOM_UP) {
			leafSize = bounds.width / superRoot.numOfLeaves;
			layerSize = bounds.height / superRoot.height;
		} else {
			leafSize = bounds.height / superRoot.numOfLeaves;
			layerSize = bounds.width / superRoot.height;
		}
		int leafCountSoFar = 0;
		for (Iterator iterator = superRoot.children.iterator(); iterator.hasNext();) {
			EntityInfo rootInfo = (EntityInfo) iterator.next();
			computePositionRecursively(rootInfo, leafCountSoFar);
			leafCountSoFar = leafCountSoFar + rootInfo.numOfLeaves;
		}

		superRoot = null;
	}

	/**
	 * Builds a tree structure using BFS method. Created trees are children of
	 * {@link #superRoot}.
	 * 
	 * @param entities
	 */
	private void createTrees(EntityLayout[] entities) {
		HashSet alreadyVisited = new HashSet();
		Queue nodesToAdd = new Queue();
		for (int i = 0; i < entities.length; i++) {
			EntityLayout root = findRoot(entities[i], alreadyVisited);
			if (root != null) {
				alreadyVisited.add(root);
				nodesToAdd.enqueue(new Object[] { root, superRoot });
			}
		}
		while (!nodesToAdd.isEmpty()) {
			EntityLayout entity;
			EntityInfo parentEntityInfo;
			try {
				Object[] dequeued = (Object[]) nodesToAdd.dequeue();
				entity = (EntityLayout) dequeued[0];
				parentEntityInfo = (EntityInfo) dequeued[1];
			} catch (InterruptedException e) {
				throw new RuntimeException("This should never happen");
			}
			EntityInfo currentEntityInfo = new EntityInfo(entity);
			parentEntityInfo.addChild(currentEntityInfo);
			NodeLayout[] children = entity.getSuccessingEntities();
			for (int i = 0; i < children.length; i++) {
				if (!alreadyVisited.contains(children[i])) {
					alreadyVisited.add(children[i]);
					nodesToAdd.enqueue(new Object[] { children[i], currentEntityInfo });
				}
			}
		}
		superRoot.precomputeTree();
	}

	/**
	 * Searches for a root of a tree containing given entity by continuously
	 * grabbing a predecessor of current entity. If it reaches an entity that
	 * exists in alreadyVisited set, it returns null. If it detects a cycle, it
	 * returns the first found entity of that cycle. If it reaches an entity
	 * that has no predecessors, it returns that entity.
	 * 
	 * @param entityLayout
	 *            starting entity
	 * @param alreadyVisited
	 *            set of entities that can't lay on path to the root (if one
	 *            does, method stops and returns null).
	 * @return
	 */
	private EntityLayout findRoot(EntityLayout entityLayout, Set alreadyVisited) {
		HashSet alreadyVisitedRoot = new HashSet();
		while (true) {
			if (alreadyVisited.contains(entityLayout))
				return null;
			if (alreadyVisitedRoot.contains(entityLayout))
				return entityLayout;
			alreadyVisitedRoot.add(entityLayout);
			NodeLayout[] predecessingEntities = entityLayout.getPredecessingEntities();
			if (predecessingEntities.length > 0) {
				entityLayout = predecessingEntities[0];
			} else {
				return entityLayout;
			}
		}
	}

	/**
	 * Computes positions recursively until the leaf nodes are reached.
	 */
	private void computePositionRecursively(EntityInfo entityInfo, int relativePosition) {
		double breadthPosition = relativePosition + entityInfo.numOfLeaves / 2.0;
		double depthPosition = (entityInfo.depth + 0.5);

		switch (direction) {
		case TOP_DOWN:
			entityInfo.entity.setLocation(breadthPosition * leafSize, depthPosition * layerSize);
			break;
		case BOTTOM_UP:
			entityInfo.entity.setLocation(breadthPosition * leafSize, bounds.height - depthPosition * layerSize);
			break;
		case LEFT_RIGHT:
			entityInfo.entity.setLocation(bounds.width - depthPosition * layerSize, breadthPosition * leafSize);
			break;
		case RIGHT_LEFT:
			entityInfo.entity.setLocation(depthPosition * layerSize, breadthPosition * leafSize);
			break;
		}

		for (Iterator iterator = entityInfo.children.iterator(); iterator.hasNext();) {
			EntityInfo childInfo = (EntityInfo) iterator.next();
			computePositionRecursively(childInfo, relativePosition);
			relativePosition += childInfo.numOfLeaves;
		}
	}
} 
