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

import java.util.HashMap;

import org.eclipse.zest.layouts.LayoutAlgorithm;
import org.eclipse.zest.layouts.dataStructures.DisplayIndependentDimension;
import org.eclipse.zest.layouts.dataStructures.DisplayIndependentPoint;
import org.eclipse.zest.layouts.dataStructures.DisplayIndependentRectangle;
import org.eclipse.zest.layouts.interfaces.ConnectionLayout;
import org.eclipse.zest.layouts.interfaces.EntityLayout;
import org.eclipse.zest.layouts.interfaces.LayoutContext;
import org.eclipse.zest.layouts.interfaces.LayoutListener;
import org.eclipse.zest.layouts.interfaces.NodeLayout;
import org.eclipse.zest.layouts.interfaces.SubgraphLayout;

/**
 * The SpringLayoutAlgorithm has its own data repository and relation
 * repository. A user can populate the repository, specify the layout
 * conditions, do the computation and query the computed results.
 * <p>
 * Instructions for using SpringLayoutAlgorithm: <br>
 * 1. Instantiate a SpringLayout object; <br>
 * 2. Populate the data repository using {@link #add add(...)}; <br>
 * 3. Populate the relation repository using
 * {@link #addRelation addRelation(...)}; <br>
 * 4. Execute {@link #compute compute()}; <br>
 * 5. Execute {@link #fitWithinBounds fitWithinBounds(...)}; <br>
 * 6. Query the computed results(node size and node position).
 * 
 * @version 2.0
 * @author Ian Bull
 * @author Casey Best (version 1.0 by Jingwei Wu/Rob Lintern)
 */
public class SpringLayoutAlgorithm implements LayoutAlgorithm {

    /**
     * The default value for the spring layout number of interations.
     */
    public static final int DEFAULT_SPRING_ITERATIONS = 1000;

    /**
     * the default value for the time algorithm runs.
     */
    public static final long MAX_SPRING_TIME = 10000;

    /**
     * The default value for positioning nodes randomly.
     */
    public static final boolean DEFAULT_SPRING_RANDOM = true;

    /**
     * The default value for the spring layout move-control.
     */
    public static final double DEFAULT_SPRING_MOVE = 1.0f;

    /**
     * The default value for the spring layout strain-control.
     */
	public static final double DEFAULT_SPRING_STRAIN = 1.0f;

    /**
     * The default value for the spring layout length-control.
     */
    public static final double DEFAULT_SPRING_LENGTH = 1.0f;

    /**
     * The default value for the spring layout gravitation-control.
     */
	public static final double DEFAULT_SPRING_GRAVITATION = 1.0f;

	/**
	 * Minimum distance considered between nodes
	 */
	protected static final double MIN_DISTANCE = 0.001d;

	/**
	 * An arbitrarily small value in mathematics.
	 */
	protected static final double EPSILON = 0.001d;

	/**
	 * The variable can be customized to set the number of iterations used.
	 */
	private int sprIterations = DEFAULT_SPRING_ITERATIONS;

    /**
     * This variable can be customized to set the max number of MS the algorithm
     * should run
     */
	private long maxTimeMS = MAX_SPRING_TIME;

    /**
     * The variable can be customized to set whether or not the spring layout
     * nodes are positioned randomly before beginning iterations.
     */
	private boolean sprRandom = DEFAULT_SPRING_RANDOM;

	/**
	 * The variable can be customized to set the spring layout move-control.
	 */
    private double sprMove = DEFAULT_SPRING_MOVE;

    /**
     * The variable can be customized to set the spring layout strain-control.
     */
	private double sprStrain = DEFAULT_SPRING_STRAIN;

    /**
     * The variable can be customized to set the spring layout length-control.
     */
	private double sprLength = DEFAULT_SPRING_LENGTH;

    /**
     * The variable can be customized to set the spring layout
     * gravitation-control.
     */
	private double sprGravitation = DEFAULT_SPRING_GRAVITATION;

	/**
	 * Variable indicating whether the algorithm should resize elements.
	 */
	private boolean resize = false;

    private int iteration;

	private double[][] srcDestToSumOfWeights;

	private EntityLayout[] entities;

	private double[] forcesX, forcesY;

	private double[] locationsX, locationsY;

	private double[] sizeW, sizeH;

	private DisplayIndependentRectangle bounds;

	private double boundsScale = 0.2;

	private LayoutContext context;

	private LayoutListener springLayoutListener = new LayoutListener() {

		public boolean nodeMoved(LayoutContext context, NodeLayout node) {
			updateLocation(node);
			return false;
		}

		public boolean nodeResized(LayoutContext context, NodeLayout node) {
			updateLocation(node);
			return false;
		}

		public boolean subgraphMoved(LayoutContext context, SubgraphLayout subgraph) {
			updateLocation(subgraph);
			return false;
		}

		public boolean subgraphResized(LayoutContext context, SubgraphLayout subgraph) {
			updateLocation(subgraph);
			return false;
		}

		private void updateLocation(EntityLayout entity) {
			if (entities != null) {
				for (int i = 0; i < entities.length; i++) {
					if (entities[i] == entity) {
						locationsX[i] = entities[i].getLocation().x;
						locationsY[i] = entities[i].getLocation().y;
					}
				}
			}
		}
	};

	public void applyLayout(boolean clean) {
		if (!clean)
			return;

		initLayout();
		while (performAnotherNonContinuousIteration()) {
			computeOneIteration();
		}
		saveLocations();
		if (resize)
			AlgorithmHelper.maximizeSizes(entities);

		DisplayIndependentRectangle bounds2 = new DisplayIndependentRectangle(bounds);
		AlgorithmHelper.fitWithinBounds(entities, bounds2, resize);
	}
    
	public void setLayoutContext(LayoutContext context) {
		if (this.context != null) {
			this.context.removeLayoutListener(springLayoutListener);
		}
		this.context = context;
		this.context.addLayoutListener(springLayoutListener);
	}

	public void performOneIteration() {
		if (entities == null) {
			initLayout();
		}
		bounds = context.getBounds();
		computeOneIteration();
		saveLocations();
		context.flushChanges(false);
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

	/**
	 * Sets the spring layout move-control.
	 * 
	 * @param move
	 *            The move-control value.
	 */
    public void setSpringMove(double move) {
        sprMove = move;
    }

    /**
     * Returns the move-control value of this SpringLayoutAlgorithm in
     * double presion.
     * 
     * @return The move-control value.
     */
    public double getSpringMove() {
        return sprMove;
    }

    /**
     * Sets the spring layout strain-control.
     * 
     * @param strain
     *            The strain-control value.
     */
    public void setSpringStrain(double strain) {
        sprStrain = strain;
    }

    /**
     * Returns the strain-control value of this SpringLayoutAlgorithm in
     * double presion.
     * 
     * @return The strain-control value.
     */
    public double getSpringStrain() {
        return sprStrain;
    }

    /**
     * Sets the spring layout length-control.
     * 
     * @param length
     *            The length-control value.
     */
    public void setSpringLength(double length) {
        sprLength = length;
    }

    /**
     * Gets the max time this algorithm will run for
     * 
     * @return
     */
    public long getSpringTimeout() {
        return maxTimeMS;
    }

    /**
     * Sets the spring timeout
     * 
     * @param timeout
     */
    public void setSpringTimeout(long timeout) {
        maxTimeMS = timeout;
    }

    /**
     * Returns the length-control value of this SpringLayoutAlgorithm in
     * double presion.
     * 
     * @return The length-control value.
     */
    public double getSpringLength() {
        return sprLength;
    }

    /**
     * Sets the spring layout gravitation-control.
     * 
     * @param gravitation
     *            The gravitation-control value.
     */
    public void setSpringGravitation(double gravitation) {
        sprGravitation = gravitation;
    }

    /**
     * Returns the gravitation-control value of this SpringLayoutAlgorithm
     * in double presion.
     * 
     * @return The gravitation-control value.
     */
    public double getSpringGravitation() {
        return sprGravitation;
    }

    /**
     * Sets the number of iterations to be used.
     * 
     * @param gravitation
     *            The number of iterations.
     */
    public void setIterations(int iterations) {
        sprIterations = iterations;
    }

    /**
     * Returns the number of iterations to be used.
     * 
     * @return The number of iterations.
     */
    public int getIterations() {
        return sprIterations;
    }

    /**
     * Sets whether or not this SpringLayoutAlgorithm will layout the
     * nodes randomly before beginning iterations.
     * 
     * @param random
     *            The random placement value.
     */
    public void setRandom(boolean random) {
        sprRandom = random;
    }

    /**
     * Returns whether or not this SpringLayoutAlgorithm will layout the
     * nodes randomly before beginning iterations.
     */
    public boolean getRandom() {
        return sprRandom;
    }

    private long startTime = 0;

	private void initLayout() {
		entities = context.getEntities();
		bounds = context.getBounds();
		loadLocations();

		srcDestToSumOfWeights = new double[entities.length][entities.length];
		HashMap entityToPosition = new HashMap();
		for (int i = 0; i < entities.length; i++) {
			entityToPosition.put(entities[i], new Integer(i));
		}

		ConnectionLayout[] connections = context.getConnections();
		for (int i = 0; i < connections.length; i++) {
			ConnectionLayout connection = connections[i];
			Integer source = (Integer) entityToPosition.get(getEntity(connection.getSource()));
			Integer target = (Integer) entityToPosition.get(getEntity(connection.getTarget()));
			if (source == null || target == null)
				continue;
			double weight = connection.getWeight();
			weight = (weight <= 0 ? 0.1 : weight);
			srcDestToSumOfWeights[source.intValue()][target.intValue()] += weight;
			srcDestToSumOfWeights[target.intValue()][source.intValue()] += weight;
		}

        if (sprRandom)
			placeRandomly(); // put vertices in random places

        iteration = 1;

		startTime = System.currentTimeMillis();
    }

	private EntityLayout getEntity(NodeLayout node) {
		if (!node.isPruned())
			return node;
		SubgraphLayout subgraph = node.getSubgraph();
		if (subgraph.isGraphEntity())
			return subgraph;
		return null;
	}

    private void loadLocations() {
		if (locationsX == null || locationsX.length != entities.length) {
			int length = entities.length;
			locationsX = new double[length];
			locationsY = new double[length];
			sizeW = new double[length];
			sizeH = new double[length];
			forcesX = new double[length];
			forcesY = new double[length];
		}
		for (int i = 0; i < entities.length; i++) {
			DisplayIndependentPoint location = entities[i].getLocation();
			locationsX[i] = location.x;
			locationsY[i] = location.y;
			DisplayIndependentDimension size = entities[i].getSize();
			sizeW[i] = size.width;
			sizeH[i] = size.height;
		}
	}

	private void saveLocations() {
		if (entities == null)
			return;
		for (int i = 0; i < entities.length; i++) {
			entities[i].setLocation(locationsX[i], locationsY[i]);
		}
	}

	/**
     * Scales the current iteration counter based on how long the algorithm has
     * been running for. You can set the MaxTime in maxTimeMS!
     */
    private void setSprIterationsBasedOnTime() {
        if (maxTimeMS <= 0)
            return;

		long currentTime = System.currentTimeMillis();
        double fractionComplete = (double) ((double) (currentTime - startTime) / ((double) maxTimeMS));
        int currentIteration = (int) (fractionComplete * sprIterations);
        if (currentIteration > iteration) {
            iteration = currentIteration;
        }

    }

    protected boolean performAnotherNonContinuousIteration() {
        setSprIterationsBasedOnTime();
		return (iteration <= sprIterations);
    }

    protected int getCurrentLayoutStep() {
        return iteration;
    }

    protected int getTotalNumberOfLayoutSteps() {
        return sprIterations;
    }

	protected void computeOneIteration() {
		computeForces();
		computePositions();
		DisplayIndependentRectangle currentBounds = getLayoutBounds();
		improveBoundsScale(currentBounds);
		moveToCenter(currentBounds);
        iteration++;
    }
        
    /**
     * Puts vertices in random places, all between (0,0) and (1,1).
     */
	public void placeRandomly() {
        // If only one node in the data repository, put it in the middle
		if (locationsX.length == 1) {
            // If only one node in the data repository, put it in the middle
			locationsX[0] = bounds.x + 0.5 * bounds.width;
			locationsY[0] = bounds.y + 0.5 * bounds.height;
        } else {
			locationsX[0] = bounds.x;
			locationsY[0] = bounds.y;
			locationsX[1] = bounds.x + bounds.width;
			locationsY[1] = bounds.y + bounds.height;
			for (int i = 2; i < locationsX.length; i++) {
				locationsX[i] = bounds.x + Math.random() * bounds.width;
				locationsY[i] = bounds.y + Math.random() * bounds.height;
            }
        }
    }

    // /////////////////////////////////////////////////////////////////
    // /// Protected Methods /////
    // /////////////////////////////////////////////////////////////////

    /**
     * Computes the force for each node in this SpringLayoutAlgorithm. The
     * computed force will be stored in the data repository
     */
	protected void computeForces() {

        // initialize all forces to zero
		for (int i = 0; i < forcesX.length; i++) {
            forcesX[i] = 0.0;
            forcesY[i] = 0.0;
        }
        // TODO: Again really really slow!

		for (int i = 0; i < locationsX.length; i++) {

			for (int j = i + 1; j < locationsX.length; j++) {
				double dx = (locationsX[i] - locationsX[j]) / bounds.width / boundsScale;
				double dy = (locationsY[i] - locationsY[j]) / bounds.height / boundsScale;
					double distance_sq = dx * dx + dy * dy;
					// make sure distance and distance squared not too small
					distance_sq = Math.max(MIN_DISTANCE * MIN_DISTANCE, distance_sq);
					double distance = Math.sqrt(distance_sq);

                    // If there are relationships between srcObj and destObj
                    // then decrease force on srcObj (a pull) in direction of destObj
                    // If no relation between srcObj and destObj then increase
                    // force on srcObj (a push) from direction of destObj.
					double sumOfWeights = srcDestToSumOfWeights[i][j];

					double f;
					if (sumOfWeights > 0) {
                        // nodes are pulled towards each other
						f = -sprStrain * Math.log(distance / sprLength) * sumOfWeights;
                    } else {
                        // nodes are repelled from each other
						f = sprGravitation / (distance_sq);
                    }
					double dfx = f * dx / distance;
					double dfy = f * dy / distance;

					forcesX[i] += dfx;
					forcesY[i] += dfy;

					forcesX[j] -= dfx;
					forcesY[j] -= dfy;
			}
        }
    }

    /**
     * Computes the position for each node in this SpringLayoutAlgorithm.
     * The computed position will be stored in the data repository. position =
     * position + sprMove * force
     */
	protected void computePositions() {
		for (int i = 0; i < entities.length; i++) {
			if (entities[i].isMovable()) {
                double deltaX = sprMove * forcesX[i];
                double deltaY = sprMove * forcesY[i];

				// constrain movement, so that nodes don't shoot way off to the
				// edge
				double dist = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
				double maxMovement = 0.2d * sprMove;
				if (dist > maxMovement) {
					deltaX *= maxMovement / dist;
					deltaY *= maxMovement / dist;
				}

				locationsX[i] += deltaX * bounds.width * boundsScale;
				locationsY[i] += deltaY * bounds.height * boundsScale;
            }
        }
	}

	private DisplayIndependentRectangle getLayoutBounds() {
		double minX, maxX, minY, maxY;
		minX = minY = Double.POSITIVE_INFINITY;
		maxX = maxY = Double.NEGATIVE_INFINITY;

		for (int i = 0; i < locationsX.length; i++) {
			maxX = Math.max(maxX, locationsX[i] + sizeW[i] / 2);
			minX = Math.min(minX, locationsX[i] - sizeW[i] / 2);
			maxY = Math.max(maxY, locationsY[i] + sizeH[i] / 2);
			minY = Math.min(minY, locationsY[i] - sizeH[i] / 2);
		}
		return new DisplayIndependentRectangle(minX, minY, maxX - minX, maxY - minY);
	}

	private void improveBoundsScale(DisplayIndependentRectangle currentBounds) {
		double boundaryProportion = Math.max(currentBounds.width / bounds.width, currentBounds.height / bounds.height);
		if (boundaryProportion < 0.9)
			boundsScale *= 1.01;
		if (boundaryProportion > 1)
			boundsScale /= 1.01;
	}

	private void moveToCenter(DisplayIndependentRectangle currentBounds) {
		double moveX = (currentBounds.x + currentBounds.width / 2) - (bounds.x + bounds.width / 2);
		double moveY = (currentBounds.y + currentBounds.height / 2) - (bounds.y + bounds.height / 2);
		for (int i = 0; i < locationsX.length; i++) {
			locationsX[i] -= moveX;
			locationsY[i] -= moveY;
		}
	}
}

