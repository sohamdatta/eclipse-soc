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

import org.eclipse.zest.layouts.dataStructures.DisplayIndependentPoint;
import org.eclipse.zest.layouts.dataStructures.DisplayIndependentRectangle;
import org.mati.zest.layout.interfaces.ConnectionLayout;
import org.mati.zest.layout.interfaces.EntityLayout;
import org.mati.zest.layout.interfaces.LayoutAlgorithm;
import org.mati.zest.layout.interfaces.LayoutContext;

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

    private double[] forcesX;

    private double[] forcesY;
    
	private LayoutContext context;

	public void applyLayout() {
		EntityLayout[] entities = context.getEntities();
		initLayout(entities);
		while (performAnotherNonContinuousIteration()) {
			computeOneIteration(entities);
		}
		AlgorithmHelper.maximizeSizes(entities);
		AlgorithmHelper.fitWithinBounds(entities, context.getBounds());
		reset();
	}
    
    public void setLayoutContext(LayoutContext context) {
		this.context = context;
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

    /**
     * Clean up after done
     */
	private void reset() {
        forcesX = null;
        forcesY = null;
		sprMove = DEFAULT_SPRING_MOVE;
		sprStrain = DEFAULT_SPRING_STRAIN;
		sprLength = DEFAULT_SPRING_LENGTH;
		sprGravitation = DEFAULT_SPRING_GRAVITATION;
		sprIterations = DEFAULT_SPRING_ITERATIONS;
    }

    private long startTime = 0;

	private void initLayout(EntityLayout[] entities) {
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
			placeRandomly(entities); // put vertices in random places

        iteration = 1;

		forcesX = new double[entities.length];
		forcesY = new double[entities.length];

		startTime = System.currentTimeMillis();
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

	protected void computeOneIteration(EntityLayout[] entities) {
		computeForces(entities);
		computePositions(entities);

        iteration++;
    }
        
    /**
     * Puts vertices in random places, all between (0,0) and (1,1).
     */
	public void placeRandomly(EntityLayout[] entities) {
        // If only one node in the data repository, put it in the middle
		DisplayIndependentRectangle bounds = context.getBounds();
		if (entities.length == 1) {
            // If only one node in the data repository, put it in the middle
			entities[0].setLocation(bounds.x + 0.5 * bounds.width, bounds.y + 0.5 * bounds.height);
        } else {
			entities[0].setLocation(bounds.x, bounds.y);
			entities[1].setLocation(bounds.x + bounds.width, bounds.y + bounds.height);
			for (int i = 2; i < entities.length; i++) {
				entities[i].setLocation(bounds.x + Math.random() * bounds.width, bounds.y + Math.random() * bounds.height);
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
	protected void computeForces(EntityLayout[] entities) {

        // initialize all forces to zero
		for (int i = 0; i < entities.length; i++) {
            forcesX[i] = 0.0;
            forcesY[i] = 0.0;
        }
		DisplayIndependentRectangle bounds = context.getBounds();

        // TODO: Again really really slow!

		for (int i = 0; i < entities.length - 1; i++) {
			EntityLayout sourceEntity = entities[i];
			DisplayIndependentPoint srcLocation = sourceEntity.getLocation();

			for (int j = i + 1; j < entities.length; j++) {
				EntityLayout destinationEntity = entities[j];
				DisplayIndependentPoint destLocation = destinationEntity.getLocation();
                if (!destinationEntity.equals(sourceEntity)) {
					double dx = (srcLocation.x - destLocation.x) / bounds.width;
					double dy = (srcLocation.y - destLocation.y) / bounds.height;
					double distance_sq = dx * dx + dy * dy;
					// make sure distance and distance squared not too small
					distance_sq = Math.max(MIN_DISTANCE * MIN_DISTANCE, distance_sq);
					double distance = Math.sqrt(distance_sq);

                    // If there are relationships between srcObj and destObj
                    // then decrease force on srcObj (a pull) in direction of destObj
                    // If no relation between srcObj and destObj then increase
                    // force on srcObj (a push) from direction of destObj.
					double sumOfWeights = 2 * srcDestToSumOfWeights[i][j];

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
    }

    /**
     * Computes the position for each node in this SpringLayoutAlgorithm.
     * The computed position will be stored in the data repository. position =
     * position + sprMove * force
     */
	protected void computePositions(EntityLayout[] entities) {
		DisplayIndependentRectangle bounds = context.getBounds();
		for (int i = 0; i < entities.length; i++) {
			if (entities[i].isMovable()) {
                double deltaX = sprMove * forcesX[i];
                double deltaY = sprMove * forcesY[i];

                // constrain movement, so that nodes don't shoot way off to the edge
                double maxMovement = 0.2d * sprMove;
                if (deltaX >= 0) {
                    deltaX = Math.min(deltaX, maxMovement);
                } else {
                    deltaX = Math.max(deltaX, -maxMovement);
                }
                if (deltaY >= 0) {
                    deltaY = Math.min(deltaY, maxMovement);
                } else {
                    deltaY = Math.max(deltaY, -maxMovement);
                }

				DisplayIndependentPoint location = entities[i].getLocation();
				entities[i].setLocation(location.x + deltaX * bounds.width, location.y + deltaY * bounds.height);
            }
            
        }

    }
}

