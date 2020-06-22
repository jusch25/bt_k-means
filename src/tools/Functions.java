package tools;

import structures.Cluster;
import structures.Point2D;

/**
 * This class contains several functions for frequent calculations.
 */
public class Functions {

	public static double euclideanDistance(double x1, double y1, double x2, double y2) {
		return Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
	}

	public static double euclideanDistance(Point2D p1, Point2D p2) {
		return euclideanDistance(p1.getX(), p1.getY(), p2.getX(), p2.getY());
	}

	public static int getWeightedRandom(double weightSum, double[] weights) {
		double rand = Math.random() * weightSum;
		double sum = 0;
		for (int i = 0; i < weights.length; i++) {
			if (rand < weights[i] + sum) {
				return i;
			}
			sum += weights[i];
		}
		return -1;
	}

	public static double getDiameter(Cluster cluster) {
		double diameter = 0;
		if (cluster.getPointNumber() > 1) {
			Point2D reference = null;
			double clusterDistance = 0;
			// farthest point from centroid
			for (int j = 0; j < cluster.getPointNumber(); j++) {
				double distance = Functions.euclideanDistance(cluster.getPoint(j), cluster.getCentroid());
				if (distance > clusterDistance) {
					clusterDistance = distance;
					reference = cluster.getPoint(j);
				}
			}
			// farthest point from reference
			for (int j = 0; j < cluster.getPointNumber(); j++) {
				double distance = Functions.euclideanDistance(cluster.getPoint(j), reference);
				if (distance > diameter) {
					diameter = distance;
				}
			}
		}
		return diameter;
	}
}
