package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.EigenDecomposition;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

import javafx.util.Pair;
import main.Main;
import other.ClusterType;
import other.DecClusterSelection;
import other.DecrementKTests;
import other.DoubleDecrementKTests;
import other.DoubleIncrementKTests;
import other.IncCentroidSelection;
import other.IncClusterSelection;
import other.IncDecTests;
import other.IncrementKTests;
import structures.Cluster;
import structures.Clustering;
import structures.Point2D;
import tools.DoublePairComparator;
import tools.Functions;
import tools.IntegerPairComparator;
import tools.SilhouetteLight;

/**
 * This class contains all operations on a given clustering to increment or
 * decrement the number of clusters based on different criteria.
 */
public class KModifier {

	private KMeanVisual kMeanVisual;
	private KMean kMean;
	private Cluster oldCluster;
	private int candidateIndex;
	private int candidateIndex2;
	private List<Point2D> newCentroids;
	private List<Integer> candidateIndices;
	private List<Integer> candidateIndices2;
	private Clustering currentClustering;
	private Cluster clusterToSplit;
	private double criterionValue;

	/**
	 * This constructor is used for a visualization environment.
	 * 
	 * @param kMean
	 *            the visualization version of the k-Means algorithm
	 */
	public KModifier(KMeanVisual kMean) {
		this.kMeanVisual = kMean;
	}

	/**
	 * This constructor is used for an evaluation environment.
	 * 
	 * @param kMean
	 *            the evaluation version of the k-Means algorithm
	 */
	public KModifier(KMean kMean) {
		this.kMean = kMean;
	}

	/* ############################ Increment k ############################ */

	/**
	 * Increment the number of clusters by splitting one cluster based on the
	 * selected criteria. Special version for visualization.
	 * 
	 * @param currentClustering
	 *            the clustered data with k centroids
	 * @param criterion1
	 *            specifies how to select a cluster for splitting
	 * @param criterion2
	 *            specifies how to split the selected cluster
	 * @return list of all intermediate clusterings of the k-means algorithm
	 *         with k+1 centroids
	 */
	public List<Clustering> incrementK(Clustering currentClustering, String heuristic, int interruption) {
		newCentroids = new ArrayList<Point2D>();
		candidateIndex = -1;
		candidateIndex2 = -1;
		this.currentClustering = currentClustering;
		findCandidate(IncrementKTests.getClusterSelection(heuristic));
		clusterToSplit = currentClustering.getCluster(candidateIndex);
		oldCluster = clusterToSplit.clone();
		findCentroids(IncrementKTests.getCentroidSelection(heuristic));
		// clusterToSplit = currentClustering.getCluster(candidateIndex2);
		// findCentroids(IncrementKTests.getCentroidSelection(heuristic));
		List<Clustering> history = kMeanVisual.splitCluster(currentClustering, newCentroids, candidateIndex,
				interruption);
		history.add(1, new Clustering(oldCluster, ClusterType.CLUSTERED, -1));
		return history;
	}

	/**
	 * Increment the number of clusters by splitting one cluster based on the
	 * selected criteria.
	 * 
	 * @param currentClustering
	 *            the clustered data with k centroids
	 * @param criterion1
	 *            specifies how to select a cluster for splitting
	 * @param criterion2
	 *            specifies how to split the selected cluster
	 * @return final clustering of the k-means algorithm with k+1 centroids
	 */
	private Clustering incrementKEvaluation(IncClusterSelection criterion1, IncCentroidSelection criterion2) {
		newCentroids = new ArrayList<Point2D>();
		candidateIndex = -1;
		findCandidate(criterion1);
		clusterToSplit = currentClustering.getCluster(candidateIndex);
		findCentroids(criterion2);
		return kMean.splitCluster(currentClustering, newCentroids, candidateIndex);
	}

	/**
	 * For re-execution of the standard k-means with incremented k by an
	 * additional random centroid (for comparison with incrementK-heuristics).
	 */
	public Clustering incrementBaseK(int increment) {
		List<Point2D> centroids = currentClustering.getCentroids();
		Point2D additionalCentroid = null;
		boolean unique = false;
		for (int i = 0; i < increment; i++) {
			additionalCentroid = null;
			unique = false;
			while (additionalCentroid == null || !unique) {
				additionalCentroid = currentClustering.getPoints()
						.get((int) (Math.random() * currentClustering.getPointNumber()));
				unique = true;
				for (Cluster c : currentClustering.getClusters()) {
					if (c.getCentroid().equalPoint(additionalCentroid)) {
						unique = false;
						break;
					}
				}
			}
			centroids.add(additionalCentroid);
		}
		return kMean.cluster(currentClustering.getSize() + increment, currentClustering, centroids);
	}

	/**
	 * Increment the number of clusters by splitting one cluster based on the
	 * selected criteria.
	 * 
	 * @param currentClustering
	 *            intput Clustering with k centroids
	 * @param type
	 *            specifies the cluster selection and split criterion
	 * @return result clustering with k+1 centroids
	 */
	public Clustering incrementKEvaluation(Clustering clustering, IncrementKTests type) {
		currentClustering = clustering.lightClone();
		switch (type) {
		case INC_MR:
			return incrementKEvaluation(IncClusterSelection.MAXPOINTS, IncCentroidSelection.RANDOM);
		case INC_MF:
			return incrementKEvaluation(IncClusterSelection.MAXPOINTS, IncCentroidSelection.FARTHEST);
		case INC_ML:
			return incrementKEvaluation(IncClusterSelection.MAXPOINTS, IncCentroidSelection.LINEARAPPROX);
		case INC_MP:
			return incrementKEvaluation(IncClusterSelection.MAXPOINTS, IncCentroidSelection.PCA);
		case INC_DR:
			return incrementKEvaluation(IncClusterSelection.MAXDIAMETER, IncCentroidSelection.RANDOM);
		case INC_DF:
			return incrementKEvaluation(IncClusterSelection.MAXDIAMETER, IncCentroidSelection.FARTHEST);
		case INC_DL:
			return incrementKEvaluation(IncClusterSelection.MAXDIAMETER, IncCentroidSelection.LINEARAPPROX);
		case INC_DP:
			return incrementKEvaluation(IncClusterSelection.MAXDIAMETER, IncCentroidSelection.PCA);
		case INC_SR:
			return incrementKEvaluation(IncClusterSelection.MINSILHOUETTE, IncCentroidSelection.RANDOM);
		case INC_SF:
			return incrementKEvaluation(IncClusterSelection.MINSILHOUETTE, IncCentroidSelection.FARTHEST);
		case INC_SL:
			return incrementKEvaluation(IncClusterSelection.MINSILHOUETTE, IncCentroidSelection.LINEARAPPROX);
		case INC_SP:
			return incrementKEvaluation(IncClusterSelection.MINSILHOUETTE, IncCentroidSelection.PCA);
		case INC_MOD:
			return incrementBaseK(1);
		case INC_STD:
			return kMean.cluster(currentClustering.getSize() + 1, currentClustering);
		default:
			return null;
		}
	}

	/**
	 * Increment the number of clusters twofold based on the selected criteria.
	 * The criteria belong to either iterative splitting, splitting one cluster
	 * in three parts or splitting two clusters each in two parts.
	 * 
	 * @param currentClustering
	 *            intput Clustering with k centroids
	 * @param type
	 *            specifies the cluster selection and split criterion
	 * @return result clustering with k+2 centroids
	 */
	public Clustering incrementKEvaluation(Clustering clustering, DoubleIncrementKTests type) {
		currentClustering = clustering.lightClone();
		newCentroids = new ArrayList<Point2D>();
		candidateIndex = -1;
		candidateIndex2 = -1;
		switch (type) {
		case INC_DR:
			doubleExecution(IncClusterSelection.MAXDIAMETER, IncCentroidSelection.RANDOM);
			break;
		case INC_DF:
			doubleExecution(IncClusterSelection.MAXDIAMETER, IncCentroidSelection.FARTHEST);
			break;
		case INC_DL:
			doubleExecution(IncClusterSelection.MAXDIAMETER, IncCentroidSelection.LINEARAPPROX);
			break;
		case INC_DP:
			doubleExecution(IncClusterSelection.MAXDIAMETER, IncCentroidSelection.PCA);
			break;
		case INC_2DR:
			largest2Diameter();
			clusterToSplit = currentClustering.getCluster(candidateIndex);
			randomCentroids();
			clusterToSplit = currentClustering.getCluster(candidateIndex2);
			randomCentroids();
			currentClustering = kMean.splitCluster(currentClustering, newCentroids, candidateIndex, candidateIndex2);
			break;
		case INC_2DF:
			largest2Diameter();
			clusterToSplit = currentClustering.getCluster(candidateIndex);
			farthestCentroids();
			clusterToSplit = currentClustering.getCluster(candidateIndex2);
			farthestCentroids();
			currentClustering = kMean.splitCluster(currentClustering, newCentroids, candidateIndex, candidateIndex2);
			break;
		case INC_2DL:
			largest2Diameter();
			clusterToSplit = currentClustering.getCluster(candidateIndex);
			linearApproximationCentroids();
			clusterToSplit = currentClustering.getCluster(candidateIndex2);
			linearApproximationCentroids();
			currentClustering = kMean.splitCluster(currentClustering, newCentroids, candidateIndex, candidateIndex2);
			break;
		case INC_2DP:
			largest2Diameter();
			clusterToSplit = currentClustering.getCluster(candidateIndex);
			pcaCentroids();
			clusterToSplit = currentClustering.getCluster(candidateIndex2);
			pcaCentroids();
			currentClustering = kMean.splitCluster(currentClustering, newCentroids, candidateIndex, candidateIndex2);
			break;
		case INC_DR3:
			findCandidate(IncClusterSelection.MAXDIAMETER);
			clusterToSplit = currentClustering.getCluster(candidateIndex);
			random3Centroids();
			currentClustering = kMean.splitCluster(currentClustering, newCentroids, candidateIndex);
			break;
		case INC_DF3:
			findCandidate(IncClusterSelection.MAXDIAMETER);
			clusterToSplit = currentClustering.getCluster(candidateIndex);
			farthest3Centroids();
			currentClustering = kMean.splitCluster(currentClustering, newCentroids, candidateIndex);
			break;
		case INC_DL3:
			findCandidate(IncClusterSelection.MAXDIAMETER);
			clusterToSplit = currentClustering.getCluster(candidateIndex);
			linearApproximation3Centroids();
			currentClustering = kMean.splitCluster(currentClustering, newCentroids, candidateIndex);
			break;
		case INC_DP3:
			findCandidate(IncClusterSelection.MAXDIAMETER);
			clusterToSplit = currentClustering.getCluster(candidateIndex);
			pca3Centroids();
			currentClustering = kMean.splitCluster(currentClustering, newCentroids, candidateIndex);
			break;
		case INC_MOD:
			currentClustering = incrementBaseK(2);
			break;
		case INC_STD:
			currentClustering = kMean.cluster(currentClustering.getSize() + 2, currentClustering);
			break;
		default:
			currentClustering = null;
			break;
		}
		return currentClustering;
	}

	/**
	 * Executes the method for cluster selection based on the criterion.
	 * 
	 * @param criterion
	 *            specifies the method for selecting a suitable cluster for
	 *            splitting
	 */
	private void findCandidate(IncClusterSelection criterion) {
		switch (criterion) {
		case MAXPOINTS:
			mostPoints();
			break;
		case MAXDIAMETER:
			largestDiameter();
			break;
		case MINSILHOUETTE:
			candidateIndex = SilhouetteLight.minSilhouetteLight(currentClustering);
			break;
		}
	}

	/**
	 * Select the cluster with the most points for the split.
	 */
	private void mostPoints() {
		int pointNumber = 0;
		for (int i = 0; i < currentClustering.getSize(); i++) {
			if (currentClustering.getCluster(i).getPointNumber() > pointNumber) {
				pointNumber = currentClustering.getCluster(i).getPointNumber();
				candidateIndex = i;
			}
		}
	}

	/**
	 * Select the cluster with the largest diameter for the split.
	 */
	private void largestDiameter() {
		double maxDiameter = 0;
		for (int i = 0; i < currentClustering.getSize(); i++) {
			double diameter = Functions.getDiameter(currentClustering.getCluster(i));
			if (diameter > maxDiameter) {
				candidateIndex = i;
				maxDiameter = diameter;
			}
		}
	}

	/**
	 * Select two clusters with the largest and second largest diameter for the
	 * splits. Requires at least two clusters.
	 */
	private void largest2Diameter() {
		double maxDiameter = 0;
		double secondMaxDiameter = 0;
		for (int i = 0; i < currentClustering.getSize(); i++) {
			double diameter = Functions.getDiameter(currentClustering.getCluster(i));
			if (diameter > secondMaxDiameter) {
				if (diameter > maxDiameter) {
					candidateIndex2 = candidateIndex;
					secondMaxDiameter = maxDiameter;
					candidateIndex = i;
					maxDiameter = diameter;
				} else {
					candidateIndex2 = i;
					secondMaxDiameter = diameter;
				}
			}
		}
	}

	/**
	 * Executes the method to calculate two new centroids for the selected
	 * cluster based on the criterion.
	 * 
	 * @param criterion
	 *            specifies the method for the calculation of new centroids
	 */
	private void findCentroids(IncCentroidSelection criterion) {
		switch (criterion) {
		case RANDOM:
			randomCentroids();
			break;
		case FARTHEST:
			farthestCentroids();
			break;
		case LINEARAPPROX:
			linearApproximationCentroids();
			break;
		case PCA:
			pcaCentroids();
			break;
		}
	}

	/**
	 * Choose random points of the selected cluster as new centroids.
	 */
	private void randomCentroids() {
		int rand1 = (int) (Math.random() * clusterToSplit.getPointNumber());
		int rand2 = 1 + (int) (Math.random() * (clusterToSplit.getPointNumber() - 1));
		// % instead of floorMod, because both values >0
		int rand3 = (rand1 + rand2) % clusterToSplit.getPointNumber();
		newCentroids.add(clusterToSplit.getPoint(rand1));
		newCentroids.add(clusterToSplit.getPoint(rand3));
	}

	/**
	 * Choose three random points of the selected cluster as new centroids.
	 */
	private void random3Centroids() {
		int rand1 = (int) (Math.random() * clusterToSplit.getPointNumber());
		int rand2 = 1 + (int) (Math.random() * (clusterToSplit.getPointNumber() - 1));
		// % instead of floorMod, because both values >0
		int rand3 = (rand1 + rand2) % clusterToSplit.getPointNumber();
		int rand4 = -1;
		// find another random point, that is not one of the points selected
		// before
		do {
			rand4 = (int) (Math.random() * clusterToSplit.getPointNumber());
		} while (rand4 == rand3 || rand4 == rand1);
		newCentroids.add(clusterToSplit.getPoint(rand1));
		newCentroids.add(clusterToSplit.getPoint(rand3));
		newCentroids.add(clusterToSplit.getPoint(rand4));
	}

	/**
	 * Choose points with a great distance to another and to the center as new
	 * centroids.
	 */
	private void farthestCentroids() {
		Point2D reference = null;
		Point2D oldReference = null;
		double clusterDistance = 0;
		// farthest point from centroid
		for (int j = 0; j < clusterToSplit.getPointNumber(); j++) {
			double distance = Functions.euclideanDistance(clusterToSplit.getPoint(j), clusterToSplit.getCentroid());
			if (distance > clusterDistance) {
				clusterDistance = distance;
				reference = clusterToSplit.getPoint(j);
			}
		}
		newCentroids.add(reference);
		oldReference = reference;
		clusterDistance = 0;
		// farthest point from reference
		for (int j = 0; j < clusterToSplit.getPointNumber(); j++) {
			double distance = Functions.euclideanDistance(clusterToSplit.getPoint(j), oldReference);
			if (distance > clusterDistance) {
				clusterDistance = distance;
				reference = clusterToSplit.getPoint(j);
			}
		}
		newCentroids.add(reference);
	}

	/**
	 * Choose points with a great distance to another and to the center and the
	 * old centroid itself as new centroids.
	 */
	private void farthest3Centroids() {
		farthestCentroids();
		// use original centroid as the third new centroid
		newCentroids.add(clusterToSplit.getCentroid());
	}

	/**
	 * Calculate new centroids based on a line through the middle of the
	 * cluster.
	 */
	private void linearApproximationCentroids() {
		// find the two farthest points from the centroid
		List<Point2D> points = clusterToSplit.getPoints();
		double[] dists = new double[2];
		int[] candidates = new int[2];
		Point2D centroid = clusterToSplit.getCentroid();
		double dist = 0;
		for (int i = 0; i < points.size(); i++) {
			dist = Functions.euclideanDistance(centroid, points.get(i));
			if (dist > dists[1]) {
				if (dist > dists[0]) {
					dists[0] = dist;
					candidates[0] = i;
				} else {
					dists[1] = dist;
					candidates[1] = i;
				}
			}
		}

		// calculate a straight line through the sample point and the middle of
		// the cluster and its SSE for every sample and choose the "best" line
		// (least error)
		double bX = 0, bY = 0, aX = 0, aY = 0, mX = 0, mY = 0;
		int bestPoint = -1;
		double minSSE = Double.MAX_VALUE; // sum of squared errors to the
		double sse = 0; // straight line
		aX = centroid.getX();
		aY = centroid.getY();

		// calculate the sum of squared errors (SSE) for every line
		for (int i = 0; i < candidates.length; i++) {
			sse = 0;
			mX = points.get(candidates[i]).getX() - aX;
			mY = points.get(candidates[i]).getY() - aY;
			// f(b) = a + t*m => for a=centroid, m=gradient or
			// direction vector, b=sample point
			// after the formula for the distance between line and point from
			// http://www.mathematik-oberstufe.de/vektoren/a/abstand-punkt-gerade-formel.html
			// |(bx-ax)*my-(by-ay)*mx|/(sqrt(mx*mx+my*my))
			double mAbsoluteValue = Math.sqrt(mX * mX + mY * mY);
			for (Point2D p : points) {
				bX = p.getX();
				bY = p.getY();
				sse += Math.abs((aX - bX) * mY - (aY - bY) * mX) / mAbsoluteValue;
			}
			if (sse < minSSE) {
				minSSE = sse;
				bestPoint = candidates[i];
			}
		}

		// for visualization of the chosen points
		if (Main.visualMode) {
			List<Point2D> selectedPoints = new ArrayList<Point2D>();
			selectedPoints.add(points.get(bestPoint));
			oldCluster.setSelectedPoints(selectedPoints);
		}

		// calculate the new centroids on the line with the lowest SSE on both
		// sides of the
		// old center with half the distance to the best sample point
		newCentroids.add(new Point2D(centroid.getX() + (points.get(bestPoint).getX() - centroid.getX()) / 2,
				centroid.getY() + (points.get(bestPoint).getY() - centroid.getY()) / 2));
		newCentroids.add(new Point2D(centroid.getX() - (points.get(bestPoint).getX() - centroid.getX()) / 2,
				centroid.getY() - (points.get(bestPoint).getY() - centroid.getY()) / 2));

	}

	/**
	 * Calculate new centroids based on a line through the middle of the cluster
	 * and add the old center as third centroid.
	 */
	private void linearApproximation3Centroids() {
		linearApproximationCentroids();
		// finding the closest points to the candidates calculated by linear
		// approximation
		double minDist = Double.MAX_VALUE;
		int candidateIndex = 0;
		for (int i = 0; i < 2; i++) {
			minDist = Double.MAX_VALUE;
			Point2D p = newCentroids.get(i);
			for (int j = 0; j < clusterToSplit.getPointNumber(); j++) {
				double dist = Functions.euclideanDistance(p, clusterToSplit.getPoint(j));
				if (dist < minDist) {
					minDist = dist;
					candidateIndex = j;
				}
			}
			newCentroids.set(i, clusterToSplit.getPoint(candidateIndex));
		}
		// use original centroid as the third new centroid
		newCentroids.add(clusterToSplit.getCentroid());
	}

	/**
	 * Calculate the greatest variance with the principle component analysis and
	 * choose new centroids by adding and substracting the eigenvector to the
	 * centroid.
	 */
	private void pcaCentroids() {
		// construct data matrix with removed bias
		double[][] data = new double[clusterToSplit.getPointNumber()][2];
		for (int i = 0; i < clusterToSplit.getPointNumber(); i++) {
			data[i][0] = clusterToSplit.getPoint(i).getX() - clusterToSplit.getCentroid().getX();
			data[i][1] = clusterToSplit.getPoint(i).getY() - clusterToSplit.getCentroid().getY();
		}
		RealMatrix centeredData = new Array2DRowRealMatrix(data);
		// compute covariance-matrix
		double scalar = 1.0 / clusterToSplit.getPointNumber();
		RealMatrix copy = centeredData.copy();
		RealMatrix transposed = copy.transpose();
		RealMatrix covariance = transposed.multiply(centeredData);
		RealMatrix normalizedCovariance = covariance.scalarMultiply(scalar);
		// compute eigenvector and eigenvalue with LU-DecompositionSolver
		EigenDecomposition solver = new EigenDecomposition(normalizedCovariance);
		double[] eigenValues = solver.getRealEigenvalues();
		int highestEigenValue = 0;
		for (int i = 0; i < eigenValues.length; i++) {
			if (eigenValues[i] > eigenValues[highestEigenValue]) {
				highestEigenValue = i;
			}
		}
		RealVector eigenVector = solver.getEigenvector(highestEigenValue);
		// assign 2 new centroids by adding and substracting the
		// eigenvalue-scaled eigenvector
		newCentroids.add(new Point2D(
				clusterToSplit.getCentroid().getX() + eigenValues[highestEigenValue] * eigenVector.getEntry(0),
				clusterToSplit.getCentroid().getY() + eigenValues[highestEigenValue] * eigenVector.getEntry(1)));
		newCentroids.add(new Point2D(
				clusterToSplit.getCentroid().getX() - eigenValues[highestEigenValue] * eigenVector.getEntry(0),
				clusterToSplit.getCentroid().getY() - eigenValues[highestEigenValue] * eigenVector.getEntry(1)));
	}

	/**
	 * Calculate the greatest variance with the principle component analysis and
	 * choose new centroids by adding and substracting the eigenvector to the
	 * centroid and add the original centroid as new one.
	 */
	private void pca3Centroids() {
		pcaCentroids();
		// finding the closest points to the candidates calculated by PCA
		double minDist = Double.MAX_VALUE;
		int candidateIndex = 0;
		for (int i = 0; i < 2; i++) {
			minDist = Double.MAX_VALUE;
			Point2D p = newCentroids.get(i);
			for (int j = 0; j < clusterToSplit.getPointNumber(); j++) {
				double dist = Functions.euclideanDistance(p, clusterToSplit.getPoint(j));
				if (dist < minDist) {
					minDist = dist;
					candidateIndex = j;
				}
			}
			newCentroids.set(i, clusterToSplit.getPoint(candidateIndex));
		}
		// use original centroid as the third new centroid
		newCentroids.add(clusterToSplit.getCentroid());
	}

	/**
	 * Basic method for the iterative execution of incrementing k.
	 * 
	 * @param cluster
	 *            the criterion for cluster selection
	 * @param centroid
	 *            the criterion for centroid calculation
	 */
	private void doubleExecution(IncClusterSelection cluster, IncCentroidSelection centroid) {
		int totalIterations = 0;
		int basicIterations = 0;
		double basicRuntime = 0;
		findCandidate(cluster);
		clusterToSplit = currentClustering.getCluster(candidateIndex);
		findCentroids(centroid);
		currentClustering = kMean.splitCluster(currentClustering, newCentroids, candidateIndex);
		totalIterations = kMean.getTotalIterations();
		basicIterations = kMean.getBasicIterations();
		basicRuntime = kMean.getBasicRuntime();
		newCentroids = new ArrayList<Point2D>();
		candidateIndex = -1;
		findCandidate(cluster);
		clusterToSplit = currentClustering.getCluster(candidateIndex);
		findCentroids(centroid);
		currentClustering = kMean.splitCluster(currentClustering, newCentroids, candidateIndex);
		kMean.addTotalIterations(totalIterations);
		kMean.addBasicIterations(basicIterations);
		kMean.addBasicRuntime(basicRuntime);
	}

	/* ############################ Decrement k ############################ */

	/**
	 * Decrement the number of clusters by merging two clusters based on the
	 * selected criterion. Special version for visualization.
	 * 
	 * @param currentClustering
	 *            the clustered data with k centroids
	 * @param criterion
	 *            specifies cluster selction criterion for merging
	 * @return list of all intermediate clusterings of the k-means algorithm
	 *         with k-1 centroids
	 */
	public List<Clustering> decrementK(Clustering currentClustering, String heuristic, int interruption) {
		this.currentClustering = currentClustering;
		candidateIndices = new ArrayList<Integer>();
		// candidateIndices2 = new ArrayList<Integer>();
		findCandidate(DecrementKTests.getClusterSelection(heuristic));
		List<Clustering> history = kMeanVisual.mergeCluster(currentClustering, candidateIndices, interruption);
		return history;
	}

	/**
	 * Decrement the number of clusters by merging two clusters based on the
	 * chosen criterion.
	 * 
	 * @param currentClustering
	 *            the clustered data with k centroids
	 * @param criterion
	 *            specifies cluster selction criterion for merging
	 * @return final clustering of the k-means algorithm with k-1 centroids
	 */
	private Clustering decrementKEvaluation(DecClusterSelection criterion) {
		candidateIndices = new ArrayList<Integer>();
		findCandidate(criterion);
		return kMean.mergeCluster(currentClustering, candidateIndices);
	}

	/**
	 * For re-execution of the standard k-means with decremented k by deleting a
	 * random centroid (for comparison with decrementK-heuristics).
	 */
	public Clustering decrementBaseK(int decrement) {
		List<Point2D> centroids = currentClustering.getCentroids();
		for (int i = 0; i < decrement; i++) {
			centroids.remove((int) (Math.random() * centroids.size()));
		}
		return kMean.cluster(currentClustering.getSize() - decrement, currentClustering, centroids);
	}

	/**
	 * Decrement the number of clusters by merging two clusters based on the
	 * chosen criterion.
	 * 
	 * @param clustering
	 *            the clustered data with k centroids
	 * @param type
	 *            specifies cluster selction criterion for merging
	 * @return final clustering of the k-means algorithm with k-1 centroids
	 */
	public Clustering decrementKEvaluation(Clustering clustering, DecrementKTests type) {
		currentClustering = clustering.lightClone();
		switch (type) {
		case DEC_C:
			return decrementKEvaluation(DecClusterSelection.CLOSEST);
		case DEC_BS:
			return decrementKEvaluation(DecClusterSelection.MAXSILHOUETTE);
		case DEC_SS:
			return decrementKEvaluation(DecClusterSelection.MINSILHOUETTE);
		case DEC_P:
			return decrementKEvaluation(DecClusterSelection.MINPOINTS);
		case DEC_D:
			return decrementKEvaluation(DecClusterSelection.MINDIAMETER);
		case DEC_BSC:
			return decrementKEvaluation(DecClusterSelection.MAXSILHOUETTECOMB);
		case DEC_SSC:
			return decrementKEvaluation(DecClusterSelection.MINSILHOUETTECOMB);
		case DEC_PC:
			return decrementKEvaluation(DecClusterSelection.MINPOINTSCOMB);
		case DEC_DC:
			return decrementKEvaluation(DecClusterSelection.MINDIAMETERCOMB);
		case DEC_RAND:
			return decrementKEvaluation(DecClusterSelection.RANDOM);
		case DEC_MOD:
			return decrementBaseK(1);
		case DEC_STD:
			return kMean.cluster(currentClustering.getSize() - 1, currentClustering);
		default:
			return null;
		}
	}

	/**
	 * Decrement the number of clusters twofold based on the selected criteria.
	 * The criteria belong to either iterative merging, merging three clusters
	 * or merging two cluster-pairs.
	 * 
	 * @param currentClustering
	 *            intput Clustering with k centroids
	 * @param type
	 *            specifies cluster selction criterion for merging
	 * @return result clustering with k-2 centroids
	 */
	public Clustering decrementKEvaluation(Clustering clustering, DoubleDecrementKTests type) {
		currentClustering = clustering.lightClone();
		candidateIndices = new ArrayList<Integer>();
		int totalIterations = 0;
		double basicRuntime = 0;
		switch (type) {
		case DEC_C:
			closestClusters();
			currentClustering = kMean.mergeCluster(currentClustering, candidateIndices);
			totalIterations = kMean.getTotalIterations();
			basicRuntime = kMean.getBasicRuntime();
			candidateIndices = new ArrayList<Integer>();
			closestClusters();
			currentClustering = kMean.mergeCluster(currentClustering, candidateIndices);
			kMean.addTotalIterations(totalIterations);
			kMean.addBasicRuntime(basicRuntime);
			break;
		case DEC_SSC:
			minSilhouetteComb();
			currentClustering = kMean.mergeCluster(currentClustering, candidateIndices);
			totalIterations = kMean.getTotalIterations();
			basicRuntime = kMean.getBasicRuntime();
			candidateIndices = new ArrayList<Integer>();
			minSilhouetteComb();
			currentClustering = kMean.mergeCluster(currentClustering, candidateIndices);
			kMean.addTotalIterations(totalIterations);
			kMean.addBasicRuntime(basicRuntime);
			break;
		case DEC_PC:
			minPointsComb();
			currentClustering = kMean.mergeCluster(currentClustering, candidateIndices);
			totalIterations = kMean.getTotalIterations();
			basicRuntime = kMean.getBasicRuntime();
			candidateIndices = new ArrayList<Integer>();
			minPointsComb();
			currentClustering = kMean.mergeCluster(currentClustering, candidateIndices);
			kMean.addTotalIterations(totalIterations);
			kMean.addBasicRuntime(basicRuntime);
			break;
		case DEC_DC:
			minDiameterComb();
			currentClustering = kMean.mergeCluster(currentClustering, candidateIndices);
			totalIterations = kMean.getTotalIterations();
			basicRuntime = kMean.getBasicRuntime();
			candidateIndices = new ArrayList<Integer>();
			minDiameterComb();
			currentClustering = kMean.mergeCluster(currentClustering, candidateIndices);
			kMean.addTotalIterations(totalIterations);
			kMean.addBasicRuntime(basicRuntime);
			break;
		case DEC_2C:
			candidateIndices2 = new ArrayList<Integer>();
			closestClusters2();
			currentClustering = kMean.mergeCluster(currentClustering, candidateIndices, candidateIndices2);
			break;
		case DEC_2SSC:
			candidateIndices2 = new ArrayList<Integer>();
			minSilhouetteComb2();
			currentClustering = kMean.mergeCluster(currentClustering, candidateIndices, candidateIndices2);
			break;
		case DEC_2PC:
			candidateIndices2 = new ArrayList<Integer>();
			minPointsComb2();
			currentClustering = kMean.mergeCluster(currentClustering, candidateIndices, candidateIndices2);
			break;
		case DEC_2DC:
			candidateIndices2 = new ArrayList<Integer>();
			minDiameterComb2();
			currentClustering = kMean.mergeCluster(currentClustering, candidateIndices, candidateIndices2);
			break;
		case DEC_C3:
			closest3Clusters();
			currentClustering = kMean.mergeCluster(currentClustering, candidateIndices);
			break;
		case DEC_SSC3:
			minSilhouette2Comb();
			currentClustering = kMean.mergeCluster(currentClustering, candidateIndices);
			break;
		case DEC_PC3:
			minPoints2Comb();
			currentClustering = kMean.mergeCluster(currentClustering, candidateIndices);
			break;
		case DEC_DC3:
			minDiameter2Comb();
			currentClustering = kMean.mergeCluster(currentClustering, candidateIndices);
			break;
		case DEC_MOD:
			currentClustering = decrementBaseK(2);
			break;
		case DEC_STD:
			currentClustering = kMean.cluster(currentClustering.getSize() - 2, currentClustering);
			break;
		default:
			currentClustering = null;
			break;
		}
		return currentClustering;
	}

	/**
	 * Executes the method for merging two clusters based on the criterion.
	 * 
	 * @param criterion
	 *            specifies the method to identify the best choice of two
	 *            clusters for the merge
	 */
	private void findCandidate(DecClusterSelection criterion) {
		switch (criterion) {
		case RANDOM:
			randomClusters();
			break;
		case CLOSEST:
			closestClusters();
			break;
		case MAXSILHOUETTE:
			maxSilhouette();
			break;
		case MINSILHOUETTE:
			minSilhouette();
			break;
		case MINPOINTS:
			minPoints();
			break;
		case MINDIAMETER:
			minDiameter();
			break;
		case MAXSILHOUETTECOMB:
			maxSilhouetteComb();
			break;
		case MINSILHOUETTECOMB:
			minSilhouetteComb();
			break;
		case MINPOINTSCOMB:
			minPointsComb();
			break;
		case MINDIAMETERCOMB:
			minDiameterComb();
			break;
		}
	}

	/**
	 * Choose two random clusters for merging.
	 */
	private void randomClusters() {
		int rand1 = (int) (Math.random() * currentClustering.getSize());
		int rand2 = 1 + (int) (Math.random() * (currentClustering.getSize() - 1));
		// % instead of floorMod, because both values >0
		int rand3 = (rand1 + rand2) % currentClustering.getSize();
		candidateIndices.add(rand1);
		candidateIndices.add(rand3);
	}

	/**
	 * Choose the pair of clusters with greatest distance for merging.
	 */
	private void closestClusters() {
		double minDist = Double.MAX_VALUE;
		for (int i = 0; i < currentClustering.getSize(); i++) {
			for (int j = i + 1; j < currentClustering.getSize(); j++) {
				double dist = Functions.euclideanDistance(currentClustering.getCluster(i).getCentroid(),
						currentClustering.getCluster(j).getCentroid());
				if (dist < minDist) {
					candidateIndices.clear();
					candidateIndices.add(i);
					candidateIndices.add(j);
					minDist = dist;
				}
			}
		}
	}

	/**
	 * Choose two pairs of clusters with greatest distances for merging. This
	 * requires two runs over all pairs of clusters. The first run determines
	 * the pair with the lowest distance. The second one then identifies the
	 * pair with lowest distance out of the remaining clusters, that are not
	 * part of the first pair.
	 */
	private void closestClusters2() {
		double minDist = Double.MAX_VALUE;
		for (int i = 0; i < currentClustering.getSize(); i++) {
			for (int j = i + 1; j < currentClustering.getSize(); j++) {
				double dist = Functions.euclideanDistance(currentClustering.getCluster(i).getCentroid(),
						currentClustering.getCluster(j).getCentroid());
				if (dist < minDist) {
					candidateIndices.clear();
					candidateIndices.add(i);
					candidateIndices.add(j);
					minDist = dist;
				}
			}
		}
		minDist = Double.MAX_VALUE;
		for (int i = 0; i < currentClustering.getSize(); i++) {
			for (int j = i + 1; j < currentClustering.getSize(); j++) {
				// consider only pairs where both clusters are not already part
				// of the first pair
				if (i != candidateIndices.get(0) && i != candidateIndices.get(1) && j != candidateIndices.get(0)
						&& j != candidateIndices.get(1)) {
					double dist = Functions.euclideanDistance(currentClustering.getCluster(i).getCentroid(),
							currentClustering.getCluster(j).getCentroid());
					if (dist < minDist) {
						candidateIndices2.clear();
						candidateIndices2.add(i);
						candidateIndices2.add(j);
						minDist = dist;
					}
				}
			}
		}
	}

	/**
	 * Choose a group of three clusters that are close to each other for
	 * merging.
	 */
	private void closest3Clusters() {
		double minDist = Double.MAX_VALUE;
		double dist = 0;
		for (int i = 0; i < currentClustering.getSize(); i++) {
			for (int j = i + 1; j < currentClustering.getSize(); j++) {
				dist = Functions.euclideanDistance(currentClustering.getCluster(i).getCentroid(),
						currentClustering.getCluster(j).getCentroid());
				if (dist < minDist) {
					candidateIndices.clear();
					candidateIndices.add(i);
					candidateIndices.add(j);
					minDist = dist;
				}
			}
		}
		double x = currentClustering.getCluster(candidateIndices.get(0)).getCentroid().getX()
				+ currentClustering.getCluster(candidateIndices.get(1)).getCentroid().getX();
		double y = currentClustering.getCluster(candidateIndices.get(0)).getCentroid().getY()
				+ currentClustering.getCluster(candidateIndices.get(1)).getCentroid().getY();
		Point2D mid = new Point2D(x / 2, y / 2);
		minDist = Double.MAX_VALUE;
		int index = 0;
		for (int i = 0; i < currentClustering.getSize(); i++) {
			if (i != candidateIndices.get(0) && i != candidateIndices.get(1)) {
				dist = Functions.euclideanDistance(currentClustering.getCluster(i).getCentroid(), mid);
				if (dist < minDist) {
					minDist = dist;
					index = i;
				}
			}
		}
		candidateIndices.add(index);
	}

	/**
	 * Choose the two clusters with the largest silhouettes for merging.
	 */
	private void maxSilhouette() {
		List<Pair<Integer, Double>> silhouettes = new ArrayList<Pair<Integer, Double>>();
		for (int i = 0; i < currentClustering.getSize(); i++) {
			silhouettes
					.add(new Pair<Integer, Double>(i, SilhouetteLight.silhouetteOfClusterLight(currentClustering, i)));
		}
		Collections.sort(silhouettes, new DoublePairComparator());
		candidateIndices.add(silhouettes.get(silhouettes.size() - 1).getKey());
		candidateIndices.add(silhouettes.get(silhouettes.size() - 2).getKey());
	}

	/**
	 * Choose the two clusters with the lowest silhouettes for merging.
	 */
	private void minSilhouette() {
		List<Pair<Integer, Double>> silhouettes = new ArrayList<Pair<Integer, Double>>();
		for (int i = 0; i < currentClustering.getSize(); i++) {
			silhouettes
					.add(new Pair<Integer, Double>(i, SilhouetteLight.silhouetteOfClusterLight(currentClustering, i)));
		}
		Collections.sort(silhouettes, new DoublePairComparator());
		candidateIndices.add(silhouettes.get(0).getKey());
		candidateIndices.add(silhouettes.get(1).getKey());
	}

	/**
	 * Choose the two clusters with the lowest number of points for merging.
	 */
	private void minPoints() {
		List<Pair<Integer, Integer>> points = new ArrayList<Pair<Integer, Integer>>();
		for (int i = 0; i < currentClustering.getSize(); i++) {
			points.add(new Pair<Integer, Integer>(i, currentClustering.getCluster(i).getPointNumber()));
		}
		Collections.sort(points, new IntegerPairComparator());
		candidateIndices.add(points.get(0).getKey());
		candidateIndices.add(points.get(1).getKey());
	}

	/**
	 * Choose the two clusters with the lowest diameter for merging.
	 */
	private void minDiameter() {
		List<Pair<Integer, Double>> diameters = new ArrayList<Pair<Integer, Double>>();
		for (int i = 0; i < currentClustering.getSize(); i++) {
			diameters.add(new Pair<Integer, Double>(i, Functions.getDiameter(currentClustering.getCluster(i))));
		}
		Collections.sort(diameters, new DoublePairComparator());
		candidateIndices.add(diameters.get(0).getKey());
		candidateIndices.add(diameters.get(1).getKey());
	}

	/**
	 * Choose the cluster with the largest silhouette and its closest neighbor
	 * for merging.
	 */
	private void maxSilhouetteComb() {
		int index = 0;
		double maxSilhouetteSum = 0;
		for (int i = 0; i < currentClustering.getSize(); i++) {
			double silhouette = SilhouetteLight.silhouetteOfClusterLight(currentClustering, i);
			if (silhouette > maxSilhouetteSum) {
				index = i;
				maxSilhouetteSum = silhouette;
			}
		}
		candidateIndices.add(index);
		candidateIndices.add(getClosestCluster(index));
	}

	/**
	 * Choose the cluster with the lowest silhouette and its closest neighbor
	 * for merging.
	 */
	private void minSilhouetteComb() {
		int index = 0;
		double minSilhouetteSum = Double.MAX_VALUE;
		for (int i = 0; i < currentClustering.getSize(); i++) {
			double silhouette = SilhouetteLight.silhouetteOfClusterLight(currentClustering, i);
			if (silhouette < minSilhouetteSum) {
				index = i;
				minSilhouetteSum = silhouette;
			}
		}
		candidateIndices.add(index);
		candidateIndices.add(getClosestCluster(index));
	}

	/**
	 * Choose the cluster with the lowest silhouette and its two closest
	 * neighbors for merging.
	 */
	private void minSilhouette2Comb() {
		int index = 0;
		double minSilhouetteSum = Double.MAX_VALUE;
		for (int i = 0; i < currentClustering.getSize(); i++) {
			double silhouette = SilhouetteLight.silhouetteOfClusterLight(currentClustering, i);
			if (silhouette < minSilhouetteSum) {
				index = i;
				minSilhouetteSum = silhouette;
			}
		}
		candidateIndices.add(index);
		getClosestClusterDouble(index);
	}

	/**
	 * Choose the two clusters with the lowest silhouettes and their two closest
	 * neighbors for merging each.
	 */
	private void minSilhouetteComb2() {
		minSilhouette();
		getTwoClosestClusters();
	}

	/**
	 * Choose the cluster with the lowest number of points and its closest
	 * neighbor for merging.
	 */
	private void minPointsComb() {
		int index = 0;
		int minPoints = Integer.MAX_VALUE;
		for (int i = 0; i < currentClustering.getSize(); i++) {
			int points = currentClustering.getCluster(i).getPointNumber();
			if (points < minPoints) {
				index = i;
				minPoints = points;
			}
		}
		candidateIndices.add(index);
		candidateIndices.add(getClosestCluster(index));
	}

	/**
	 * Choose the cluster with the lowest number of points and its two closest
	 * neighbors for merging.
	 */
	private void minPoints2Comb() {
		int index = 0;
		int minPoints = Integer.MAX_VALUE;
		for (int i = 0; i < currentClustering.getSize(); i++) {
			int points = currentClustering.getCluster(i).getPointNumber();
			if (points < minPoints) {
				index = i;
				minPoints = points;
			}
		}
		candidateIndices.add(index);
		getClosestClusterDouble(index);
	}

	/**
	 * Choose the two clusters with the lowest numbers of points and their two
	 * closest neighbors for merging each.
	 */
	private void minPointsComb2() {
		minPoints();
		getTwoClosestClusters();
	}

	/**
	 * Choose the cluster with the lowest diameter and its closest neighbor for
	 * merging.
	 */
	private void minDiameterComb() {
		int index = 0;
		double minDiameter = Double.MAX_VALUE;
		for (int i = 0; i < currentClustering.getSize(); i++) {
			double diameter = Functions.getDiameter(currentClustering.getCluster(i));
			if (diameter < minDiameter) {
				index = i;
				minDiameter = diameter;
			}
		}
		candidateIndices.add(index);
		candidateIndices.add(getClosestCluster(index));
	}

	/**
	 * Choose the cluster with the lowest diameter and its two closest neighbors
	 * for merging.
	 */
	private void minDiameter2Comb() {
		int index = 0;
		double minDiameter = Double.MAX_VALUE;
		for (int i = 0; i < currentClustering.getSize(); i++) {
			double diameter = Functions.getDiameter(currentClustering.getCluster(i));
			if (diameter < minDiameter) {
				index = i;
				minDiameter = diameter;
			}
		}
		candidateIndices.add(index);
		getClosestClusterDouble(index);
	}

	/**
	 * Choose the two clusters with the lowest diameters and their two closest
	 * neighbors for merging each.
	 */
	private void minDiameterComb2() {
		minDiameter();
		getTwoClosestClusters();
	}

	/**
	 * Identifies the closest neighbor for a given cluster.
	 */
	private int getClosestCluster(int index) {
		int index2 = 0;
		double minDist = Double.MAX_VALUE;
		for (int i = 0; i < currentClustering.getSize(); i++) {
			if (i != index) {
				double dist = Functions.euclideanDistance(currentClustering.getCluster(index).getCentroid(),
						currentClustering.getCluster(i).getCentroid());
				if (dist < minDist) {
					index2 = i;
					minDist = dist;
				}
			}
		}
		return index2;
	}

	/**
	 * Identifies the two closest neighbors for a given cluster.
	 */
	private void getClosestClusterDouble(int index) {
		int index2 = 0;
		double minDist = Double.MAX_VALUE;
		for (int i = 0; i < currentClustering.getSize(); i++) {
			if (i != index) {
				double dist = Functions.euclideanDistance(currentClustering.getCluster(index).getCentroid(),
						currentClustering.getCluster(i).getCentroid());
				if (dist < minDist) {
					index2 = i;
					minDist = dist;
				}
			}
		}
		candidateIndices.add(index2);
		double x = currentClustering.getCluster(candidateIndices.get(0)).getCentroid().getX()
				+ currentClustering.getCluster(candidateIndices.get(1)).getCentroid().getX();
		double y = currentClustering.getCluster(candidateIndices.get(0)).getCentroid().getY()
				+ currentClustering.getCluster(candidateIndices.get(1)).getCentroid().getY();
		Point2D mid = new Point2D(x / 2, y / 2);
		minDist = Double.MAX_VALUE;
		double dist = 0;
		int index3 = 0;
		for (int i = 0; i < currentClustering.getSize(); i++) {
			if (i != candidateIndices.get(0) && i != candidateIndices.get(1)) {
				dist = Functions.euclideanDistance(currentClustering.getCluster(i).getCentroid(), mid);
				if (dist < minDist) {
					minDist = dist;
					index3 = i;
				}
			}
		}
		candidateIndices.add(index3);
	}

	/**
	 * Identifies the two closest neighbors for the two given clusters. Requires
	 * at least four clusters to be able to merge two pairs.
	 */
	private void getTwoClosestClusters() {
		candidateIndices2.add(candidateIndices.get(1));
		candidateIndices2.add(1, -2);
		candidateIndices.set(1, -1);
		int index1 = candidateIndices.get(0);
		int index2 = candidateIndices2.get(0);
		double minDist1 = Double.MAX_VALUE;
		double minDist2 = Double.MAX_VALUE;
		for (int i = 0; i < currentClustering.getSize(); i++) {
			// consider only clusters that were not chosen by the given
			// criterion in the previous step (cluster a and b)
			if (i != index1 && i != index2) {
				// calculate the distance of cluster c to a and b
				double dist1 = Functions.euclideanDistance(currentClustering.getCluster(i).getCentroid(),
						currentClustering.getCluster(index1).getCentroid());
				double dist2 = Functions.euclideanDistance(currentClustering.getCluster(i).getCentroid(),
						currentClustering.getCluster(index2).getCentroid());
				if (dist1 < dist2) { // c is closer to a
					if (minDist1 == Double.MAX_VALUE || dist1 < minDist1) {
						if (candidateIndices.get(1) != -1) {
							double distTemp = Functions.euclideanDistance(
									currentClustering.getCluster(candidateIndices.get(1)).getCentroid(),
									currentClustering.getCluster(index2).getCentroid());
							if (distTemp < minDist2) {
								candidateIndices2.set(1, candidateIndices.get(1));
								minDist2 = distTemp;
							}
						}
						candidateIndices.set(1, i);
						minDist1 = dist1;
					} else if (minDist2 == Double.MAX_VALUE || dist2 < minDist2) {
						candidateIndices2.set(1, i);
						minDist2 = dist2;
					}
				} else { // c is closer to b
					if (minDist2 == Double.MAX_VALUE || dist2 < minDist2) {
						if (candidateIndices2.get(1) != -2) {
							double distTemp = Functions.euclideanDistance(
									currentClustering.getCluster(candidateIndices2.get(1)).getCentroid(),
									currentClustering.getCluster(index1).getCentroid());
							if (distTemp < minDist1) {
								candidateIndices.set(1, candidateIndices2.get(1));
								minDist1 = distTemp;
							}
						}
						candidateIndices2.set(1, i);
						minDist2 = dist2;
					} else if (minDist1 == Double.MAX_VALUE || dist1 < minDist1) {
						candidateIndices.set(1, i);
						minDist1 = dist1;
					}
				}
			}
		}
	}

	/* ############ Comparison of incrementing and decrementing ############ */

	/**
	 * Increment or decrement the number of clusters by splitting or merging
	 * clusters based on the selected criteria.
	 * 
	 * @param clustering
	 *            intput Clustering with k centroids
	 * @param type
	 *            specifies the criterion for incrementing or decrementing the
	 *            number of clusters
	 * @return result clustering with k+1 centroids for a incrementing setup, or
	 *         a clustering with k-1 centroids for the decrementing case
	 */
	public Clustering incDecEvaluation(Clustering clustering, IncDecTests type) {
		currentClustering = clustering.lightClone();
		switch (type) {
		case INC_DR:
			return incrementKEvaluation(IncClusterSelection.MAXDIAMETER, IncCentroidSelection.RANDOM);
		case INC_DF:
			return incrementKEvaluation(IncClusterSelection.MAXDIAMETER, IncCentroidSelection.FARTHEST);
		case INC_DL:
			return incrementKEvaluation(IncClusterSelection.MAXDIAMETER, IncCentroidSelection.LINEARAPPROX);
		case INC_DP:
			return incrementKEvaluation(IncClusterSelection.MAXDIAMETER, IncCentroidSelection.PCA);
		case DEC_C:
			return decrementKEvaluation(DecClusterSelection.CLOSEST);
		case DEC_SSC:
			return decrementKEvaluation(DecClusterSelection.MINSILHOUETTECOMB);
		case DEC_PC:
			return decrementKEvaluation(DecClusterSelection.MINPOINTSCOMB);
		case DEC_DC:
			return decrementKEvaluation(DecClusterSelection.MINDIAMETERCOMB);
		default:
			return null;
		}
	}

	/* ############################## Other ############################## */

	public double getCriterionValue() {
		return criterionValue;
	}
}
