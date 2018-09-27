package edu.polyu.appinventor.positioning.algorithm.leastRatio;
import org.apache.commons.math3.fitting.leastsquares.MultivariateJacobianFunction;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.util.Pair;

/**
 * Models the Trilateration problem. This is a formulation for a nonlinear least
 * squares optimizer.
 *
 * @author scott
 *
 */
public class LeastRatioFunction implements MultivariateJacobianFunction {

	protected static final double epsilon = 1E-7;

	/**
	 * Known positions of static nodes
	 */
	protected final double positions[][];

	/**
	 * Euclidean distances from static nodes to mobile node
	 */
	protected final double distances[];

	public LeastRatioFunction(double positions[][], double distances[]) {

		if(positions.length < 2) {
			throw new IllegalArgumentException("Need at least two positions.");
		}

		if(positions.length != distances.length) {
			throw new IllegalArgumentException("The number of positions you provided, " + positions.length + ", does not match the number of distances, " + distances.length + ".");
		}

		// bound distances to strictly positive domain
		for (int i = 0; i < distances.length; i++) {
			distances[i] = Math.max(distances[i], epsilon);
		}

		int positionDimension = positions[0].length;
		for (int i = 1; i < positions.length; i++) {
			if(positionDimension != positions[i].length) {
				throw new IllegalArgumentException("The dimension of all positions should be the same.");
			}
		}

		this.positions = positions;
		this.distances = distances;
	}

	public final double[] getDistances() {
		return distances;
	}

	public final double[][] getPositions() {
		return positions;
	}

	/**
	 * Calculate and return Jacobian function Actually return initialized function
	 *
	 * Jacobian matrix, [i][j] at
	 * J[i][0] = delta_[(x0-xi)^2 + (y0-yi)^2 - ri^2]/delta_[x0] at
	 * J[i][1] = delta_[(x0-xi)^2 + (y0-yi)^2 - ri^2]/delta_[y0] partial derivative with respect to the parameters passed to value() method
	 *
	 * @param point for which to calculate the slope
	 * @return Jacobian matrix for point
	 */
	public RealMatrix jacobian(RealVector point) {
		double[] pointArray = point.toArray();

		double[][] jacobian = new double[distances.length][pointArray.length];

		int N = this.distances.length;

		for (int i = 0; i < N; i++) {
			double temp1 = 0.0;
			for (int k = 0; k < pointArray.length; k++)
				temp1 += (pointArray[k] - this.getPositions()[i][k]) * (pointArray[k] - this.getPositions()[i][k]);
			for (int j = 1; j < N; j++) {
				double temp2 = 0.0;
				for (int k = 0; k < pointArray.length; k++) {
					temp2 += (pointArray[k] - this.getPositions()[j][k]) * (pointArray[k] - this.getPositions()[j][k]);
				}
				for (int k = 0; k < pointArray.length; k++)
					jacobian[i * (N - 1) + j][k] = (2 * pointArray[k] - 2 * positions[i][k]) * temp2 - (2 * pointArray[k] - 2 * positions[j][k]) * temp1 / (temp2 * temp2);
			}
		}
		return new Array2DRowRealMatrix(jacobian);
	}

	@Override
	public Pair<RealVector, RealMatrix> value(RealVector point) {

		// input
		double[] pointArray = point.toArray();

		int N = this.distances.length;

		// output with length N * (N - 1)
		double[] resultPoint = new double[N * (N - 1)];

		// compute least ratio
		for (int i = 0; i < N; i++) {
			double temp1 = 0.0;
			//temp1 = (x - xi) ^ 2 + (y - yi) ^ 2
			for (int k = 0; k < pointArray.length; k++) {
				temp1 += (pointArray[k] - this.getPositions()[i][k]) * (pointArray[k] - this.getPositions()[i][k]);
			}
			// calculate sum, add to overall
			double temp2 = 0.0;
			for (int j = 1; j < N; j++) {
				for (int k = 0; k < pointArray.length; k++) {
					// temp2 = (x - xj) ^ 2 + (y - yj) ^2
					temp2 += (pointArray[k] - this.getPositions()[j][k]) * (pointArray[k] - this.getPositions()[j][k]);
				}
				//res = temp1 / temp2 - (di / dj) ^ 2
				resultPoint[i * (N - 1) + j] = temp1 / temp2 - (this.getDistances()[i] / this.getDistances()[j]) * (this.getDistances()[i] / this.getDistances()[j]);
			}
		}

		RealMatrix jacobian = jacobian(point);
		return new Pair<RealVector, RealMatrix>(new ArrayRealVector(resultPoint), jacobian);
	}
}
