package tools;

import structures.Cluster;
import structures.Clustering;
import structures.Point2D;

/**
 * This class contains the operations to calculate the silhouette of a
 * clustering.
 */
public class Silhouette {

	/**
	 * Calculate the silhouette of a clustering with values between 0 (no
	 * structure) and 1 (strong structure) - precise version of silhouette but
	 * with high costs: each point is compared with each other => only for
	 * evaluation of the resulting clustering (the number of empty clusters is
	 * encoded as exponentially in the return value to avoid the
	 * single-return-value restrictions)
	 */
	public static double calculateSilhouetteEmptyClusters(Clustering clustering) {
		double silhouetteSum = 0;
		double pointNumber = 0;
		int emptyClusterCounter = 0;
		for (int i = 0; i < clustering.getSize(); i++) {
			// only for real clusters with points
			if (clustering.getCluster(i).getPointNumber() > 0) {
				for (Point2D o : clustering.getCluster(i).getPoints()) {
					silhouetteSum += silhouetteOfObjectEvaluation(o, clustering, i);
					pointNumber++;
				}
			} else {
				emptyClusterCounter++;
			}
		}
		// System.out.println("Silhouette: " + silhouetteSum /
		// clustering.getPoints().size());
		if (emptyClusterCounter == 0) {
			return silhouetteSum / pointNumber;
		}
		return silhouetteSum / pointNumber + Math.pow(10, emptyClusterCounter - 1);
	}

	/**
	 * Calculate the silhouette of a clustering with values between 0 (no
	 * structure) and 1 (strong structure) - precise version of silhouette but
	 * with high costs: each point is compared with each other.
	 */
	public static double calculateSilhouette(Clustering clustering) {
		double silhouetteSum = 0;
		for (int i = 0; i < clustering.getSize(); i++) {
			for (Point2D o : clustering.getCluster(i).getPoints()) {
				silhouetteSum += silhouetteOfObject(o, clustering, i);
			}
		}
		return silhouetteSum / clustering.getPointNumber();
	}

	/**
	 * Return average distance between object o and the objects in its cluster
	 * A.
	 */
	private static double aOfObject(Point2D o, Cluster c) {
		double distSum = 0;
		for (Point2D p : c.getPoints()) {
			distSum += Functions.euclideanDistance(o, p);
		}
		return distSum / c.getPoints().size();
	}

	/**
	 * Return the smallest average distance of all other clusters between object
	 * o and the objects of another cluster.
	 * 
	 * @param index
	 *            the index of the cluster of o
	 */
	private static double bOfObject(Point2D o, Clustering clustering, int index) {
		double minAverageDist = Double.MAX_VALUE;
		for (int i = 0; i < clustering.getSize(); i++) {
			// No calculation for o's own cluster or empty neighbors
			if (i != index && clustering.getCluster(i).getPointNumber() > 0) {
				double distSum = 0;
				for (Point2D p : clustering.getCluster(i).getPoints()) {
					distSum += Functions.euclideanDistance(o, p);
				}
				minAverageDist = Math.min(minAverageDist, distSum / clustering.getCluster(i).getPointNumber());
			}
		}
		return minAverageDist;
	}

	/**
	 * Calculate the silhouette of an object o with values between -1 (bad
	 * assignment of o to its cluster - o is closer to other clusters) and 1
	 * (good assignment of o to its cluster - o is far from other clusters).
	 * Clusterings with all points in a single cluster have the silhouette of 1.
	 */
	private static double silhouetteOfObjectEvaluation(Point2D o, Clustering clustering, int index) {
		double a = aOfObject(o, clustering.getCluster(index));
		double b = bOfObject(o, clustering, index);
		double result = -1;
		if (a == 0) {
			// o is the only point in its cluster
			result = 0;
		} else if (b == Double.MAX_VALUE) {
			// all other clusters than o's cluster have no points
			result = 1;
		} else {
			result = (b - a) / Math.max(a, b);
		}
		// System.out.println("Silhouette of " + o.getX() + " " + o.getY() + ":"
		// + result);
		return result;
	}

	/**
	 * Calculate the silhouette of an object o with values between -1 (bad
	 * assignment of o to its cluster - o is closer to other clusters) and 1
	 * (good assignment of o to its cluster - o is far from other clusters).
	 * Clusterings with all points in a single cluster have the silhouette of 0.
	 */
	private static double silhouetteOfObject(Point2D o, Clustering clustering, int index) {
		double a = aOfObject(o, clustering.getCluster(index));
		double b = bOfObject(o, clustering, index);
		double result = -1;
		if (a == 0) {
			// o is the only point in its cluster
			result = 0;
		} else if (b == Double.MAX_VALUE) {
			// all other clusters than o's cluster have no points
			result = 0;
		} else {
			result = (b - a) / Math.max(a, b);
		}
		// System.out.println("Silhouette of " + o.getX() + " " + o.getY() + ":"
		// + result);
		return result;
	}
}