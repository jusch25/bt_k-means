package tools;

import structures.Cluster;
import structures.Clustering;
import structures.Point2D;

/**
 * This class contains the operations to calculate the approximate silhouette of
 * a clustering.
 */
public class SilhouetteLight {

	/**
	 * calculate the silhouette of a clustering with values between 0 (no
	 * structure) and 1 (strong structure) - rough version of silhouette where
	 * each point is only compared with the centroids => for analyzing the best
	 * clustering during runtime of the heuristics
	 */
	public static double calculateSilhouetteLight(Clustering clustering) {
		double silhouetteSum = 0;
		for (int i = 0; i < clustering.getSize(); i++) {
			for (Point2D o : clustering.getCluster(i).getPoints()) {
				silhouetteSum += silhouetteOfObjectLight(o, clustering, i);
			}
		}
		// System.out.println("Silhouette: " + silhouetteSum /
		// clustering.getPoints().size());
		return silhouetteSum / clustering.getPointNumber();
	}

	/**
	 * calculate the cluster with the smallest silhouette and return its index -
	 * light version (only with centroids)
	 */
	public static int minSilhouetteLight(Clustering clustering) {
		double minSilhouette = Double.MAX_VALUE;
		int minIndex = 0;
		for (int i = 0; i < clustering.getSize(); i++) {
			double silhouette = silhouetteOfClusterLight(clustering, i);
			// System.out.println("ClusterMin " + (i + 1) + ": " + silhouette);
			if (silhouette < minSilhouette && silhouette > 0.000000001) {
				minSilhouette = silhouette;
				minIndex = i;
			}
		}
		// System.out.println();
		// System.out.println("Lowest silhouette at Cluster " + (minIndex + 1));
		return minIndex;
	}

	/**
	 * calculate the cluster with the biggest silhouette and return its index -
	 * light version (only with centroids)
	 */
	public static int maxSilhouetteLight(Clustering clustering) {
		double maxSilhouette = Double.MIN_VALUE;
		int maxIndex = 0;
		for (int i = 0; i < clustering.getSize(); i++) {
			double silhouette = silhouetteOfClusterLight(clustering, i);
			// System.out.println("ClusterMax " + (i + 1) + ": " + silhouette);
			if (silhouette > maxSilhouette) {
				maxSilhouette = silhouette;
				maxIndex = i;
			}
		}
		// System.out.println();
		// System.out.println("Greatest silhouette at Cluster " + (maxIndex +
		// 1));
		return maxIndex;
	}

	/**
	 * return average distance between object o and the objects in its cluster A
	 */
	private static double aOfObjectLight(Point2D o, Cluster c) {
		return Functions.euclideanDistance(o, c.getCentroid());
	}

	/**
	 * return the smallest average distance of all other clusters between object
	 * o and the objects of another cluster
	 * 
	 * @param index
	 *            the index of the cluster of o
	 */
	private static double bOfObjectLight(Point2D o, Clustering clustering, int index) {
		double minAverageDist = Double.MAX_VALUE;
		for (int i = 0; i < clustering.getSize(); i++) {
			// No calculation for o's own cluster and for empty clusters
			if (i != index && clustering.getCluster(i).getPointNumber() > 0) {
				minAverageDist = Math.min(minAverageDist,
						Functions.euclideanDistance(o, clustering.getCluster(i).getCentroid()));
			}
		}
		return minAverageDist;
	}

	/**
	 * calculate the silhouette of an object o with values between -1 (bad
	 * assignment of o to its cluster - o is closer to other clusters) and 1
	 * (good assignment of o to its cluster - o is far from other clusters)
	 */
	private static double silhouetteOfObjectLight(Point2D o, Clustering clustering, int index) {
		double a = aOfObjectLight(o, clustering.getCluster(index));
		double b = bOfObjectLight(o, clustering, index);
		double result = -1;
		if (a == 0) {
			result = 0;
		} else if (b == Double.MAX_VALUE) {
			result = 0;
		} else {
			result = (b - a) / Math.max(a, b);
		}
		// System.out.println("Silhouette of " + o.getX() + " " + o.getY() + ":"
		// + result);
		return result;
	}

	/**
	 * return the silhouette of a cluster which is the average silhouette of its
	 * objects
	 */
	public static double silhouetteOfClusterLight(Clustering clustering, int index) {
		double silhouetteSum = 0;
		for (Point2D o : clustering.getCluster(index).getPoints()) {
			silhouetteSum += silhouetteOfObjectLight(o, clustering, index);
		}
		// System.out.println("Silhouette of " + (index + 1) + ": "
		// + silhouetteSum / clustering.getCluster(index).getPoints().size());
		return silhouetteSum / clustering.getCluster(index).getPointNumber();
	}
}
