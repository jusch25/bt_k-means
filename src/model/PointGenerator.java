package model;

import java.util.ArrayList;
import java.util.List;

import other.ClusterType;
import structures.Cluster;
import structures.Clustering;
import structures.NormalDistribution;
import structures.Point2D;
import tools.Functions;

/**
 * This class takes care of the generation of clustering data by using
 * distributions.
 */
public class PointGenerator {

	private int distributionNumber;
	private int pointNumber;
	private double distChanceSum;
	private double[] distChances;
	private List<NormalDistribution> distributions;
	private List<Point2D> points;
	private List<Cluster> generatedDistributions;

	public Clustering generate(int pointNumber, int distributionNumber, double spreadFactor) {
		this.pointNumber = pointNumber;
		this.distributionNumber = distributionNumber;
		generateDistributions(spreadFactor);
		generatePoints();
		return new Clustering(new Cluster(points, ClusterType.UNCLASSIFIED), ClusterType.UNCLASSIFIED,
				distributionNumber);
	}

	private void generateDistributions(double spreadFactor) {
		if (spreadFactor < 0.1 || spreadFactor > 2) {
			spreadFactor = 1;
		}
		generatedDistributions = new ArrayList<Cluster>();
		distributions = new ArrayList<NormalDistribution>();
		distChances = new double[distributionNumber];
		distChanceSum = 0;
		for (int i = 0; i < distributionNumber; i++) {
			distChances[i] = Math.random() * spreadFactor + 0.2;
			distChanceSum += distChances[i];
			distributions.add(new NormalDistribution(Math.sqrt(pointNumber), distributionNumber));
			generatedDistributions.add(new Cluster(ClusterType.INITIAL));
		}
	}

	private void generatePoints() {
		points = new ArrayList<Point2D>();
		for (int i = 0; i < distributionNumber; i++) {
			Point2D p = distributions.get(i).generatePoint();
			points.add(p);
			generatedDistributions.get(i).addPoint(p);
		}
		for (int i = 0; i < pointNumber - distributionNumber; i++) {
			int rand = Functions.getWeightedRandom(distChanceSum, distChances);
			Point2D p = distributions.get(rand).generatePoint();
			// System.out.println(p.getX() + " " + p.getY());
			points.add(p);
			generatedDistributions.get(rand).addPoint(p);
		}
	}

	public Clustering getInitialDistributions() {
		return new Clustering(generatedDistributions, ClusterType.INITIAL);
	}
}
