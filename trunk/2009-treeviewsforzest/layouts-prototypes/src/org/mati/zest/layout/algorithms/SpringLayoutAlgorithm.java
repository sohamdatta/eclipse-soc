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

import java.util.HashMap;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Region;
import org.eclipse.zest.layouts.dataStructures.DisplayIndependentPoint;
import org.eclipse.zest.layouts.dataStructures.DisplayIndependentRectangle;
import org.mati.zest.layout.interfaces.ConnectionLayout;
import org.mati.zest.layout.interfaces.EntityLayout;
import org.mati.zest.layout.interfaces.LayoutAlgorithm;
import org.mati.zest.layout.interfaces.LayoutContext;
import org.mati.zest.layout.interfaces.NodeLayout;

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

    private int iteration;

	private double[][] srcDestToSumOfWeights;

	private EntityLayout[] entities;

	private double[] forcesX, forcesY;

	private double[] locationsX, locationsY;

	private DisplayIndependentRectangle bounds;

	private double boundaryScale = 1.0;

	private LayoutContext context;

	public void applyLayout() {
		initLayout();
		while (performAnotherNonContinuousIteration()) {
			computeOneIteration();
		}
		saveLocations();
		AlgorithmHelper.maximizeSizes(entities);
		AlgorithmHelper.fitWithinBounds(entities, bounds);
	}
    
	public void setLayoutContext(LayoutContext context) {
		this.context = context;
	}

	public void performIteration() {
		if (iteration == 0) {
			entities = context.getEntities();
			loadLocations();
			initLayout();
		}
		computeOneIteration();
		saveLocations();
		context.flushChanges(false);
	}

	public void paint(GC gc) {
		if (iteration > 0) {
			gc.setClipping((Region) null);
			gc.setForeground(ColorConstants.black);
			gc.drawText(", it=" + iteration + ", bS=" + boundaryScale, 5, 5, true);
			NodeLayout[] nodes = context.getNodes();
			computeForces();
			gc.setForeground(ColorConstants.red);
			for (int i = 0; i < nodes.length; i++) {
				DisplayIndependentPoint location = nodes[i].getLocation();
				double distToCenterX = bounds.x + bounds.width / 2 - location.x;
				double distToCenterY = bounds.y + bounds.height / 2 - location.y;
				double distToCenter = Math.sqrt(distToCenterX * distToCenterX + distToCenterY * distToCenterY);
				double forcesX = sprGravitation * distToCenterX / distToCenter;
				double forcesY = sprGravitation * distToCenterY / distToCenter;
				gc.drawLine((int) location.x, (int) location.y, (int) (location.x + forcesX * 20), (int) (location.y + forcesY * 20));
			}
		}
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
			Integer source = (Integer) entityToPosition.get(connection.getSource());
			Integer target = (Integer) entityToPosition.get(connection.getTarget());
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

    private void loadLocations() {
		if (locationsX == null || locationsX.length != entities.length) {
			locationsX = new double[entities.length];
			locationsY = new double[entities.length];
		}
		if (forcesX == null || forcesX.length != entities.length) {
			forcesX = new double[entities.length];
			forcesY = new double[entities.length];
		}
		for (int i = 0; i < entities.length; i++) {
			DisplayIndependentPoint location = entities[i].getLocation();
			locationsX[i] = location.x;
			locationsY[i] = location.y;
		}
	}

	private void saveLocations() {
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
		improveBoundary();
		moveToCenter();
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
				double dx = (locationsX[i] - locationsX[j]) / bounds.width / boundaryScale;
				double dy = (locationsY[i] - locationsY[j]) / bounds.height / boundaryScale;
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

				locationsX[i] += deltaX * bounds.width * boundaryScale;
				locationsY[i] += deltaY * bounds.height * boundaryScale;
            }
        }
	}

	private void improveBoundary() {
		double minX, maxX, minY, maxY;
		minX = minY = Double.POSITIVE_INFINITY;
		maxX = maxY = Double.NEGATIVE_INFINITY;

		for (int i = 0; i < locationsX.length; i++) {
			maxX = Math.max(maxX, locationsX[i]);
			minX = Math.min(minX, locationsX[i]);
			maxY = Math.max(maxY, locationsY[i]);
			minY = Math.min(minY, locationsY[i]);
		}

		double boundaryProportion = Math.max((maxX - minX) / bounds.width, (maxY - minY) / bounds.height);
		if (boundaryProportion < 0.9)
			boundaryScale *= 1.01;
		if (boundaryProportion > 1)
			boundaryScale *= 0.99;
	}

	private void moveToCenter() {
		double sumX, sumY;
		sumX = sumY = 0;
		for (int i = 0; i < locationsX.length; i++) {
			sumX += locationsX[i];
			sumY += locationsY[i];
		}
		double avgX = sumX / locationsX.length - (bounds.x + bounds.width / 2);
		double avgY = sumY / locationsX.length - (bounds.y + bounds.height / 2);
		for (int i = 0; i < locationsX.length; i++) {
			locationsX[i] -= avgX;
			locationsY[i] -= avgY;
		}
	}
}

