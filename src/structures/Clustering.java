package structures;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javafx.scene.chart.XYChart.Series;
import other.ClusterType;

/**
 * A clustering is either a set of unclassified points, or a set of clusters.
 */
public class Clustering {

	private List<Series<Number, Number>> chartClustering;
	private List<Cluster> clusters;
	private List<Point2D> points;
	private ClusterType clusterType;
	private int pointNumber;
	private int k;
	// number of underlying distributions, that created the data
	private int hiddenK;

	/**
	 * constructor for a single cluster (e.g. unclassified points)
	 * 
	 * @param cluster
	 *            the single cluster
	 * @param type
	 *            type of the cluster (e.g. unclassified)
	 * @param hiddenK
	 *            number of initial distributions, if available, -1 otherwise
	 */
	public Clustering(Cluster cluster, ClusterType type, int hiddenK) {
		clusters = new ArrayList<Cluster>();
		clusters.add(cluster);
		clusterType = type;
		points = cluster.getPoints();
		pointNumber = cluster.getPointNumber();
		k = 0;
		this.hiddenK = hiddenK;
	}

	/**
	 * constructor for clusters represented by an array of points
	 */
	public Clustering(List<Cluster> clusters, ClusterType type) {
		this.clusters = clusters;
		clusterType = type;
		points = new ArrayList<Point2D>();
		for (Cluster c : clusters) {
			points.addAll(c.getPoints());
		}
		pointNumber = points.size();
		k = clusters.size();
	}

	public void replaceSplittedCluster(int oldClusterIndex, List<Cluster> newClusters) {
		clusters.remove(oldClusterIndex);
		clusters.addAll(newClusters);
		// update variables
		points = new ArrayList<Point2D>();
		for (Cluster c : clusters) {
			points.addAll(c.getPoints());
		}
		pointNumber = points.size();
		k = clusters.size();
	}

	public void replaceSplittedCluster(int oldClusterIndex, int oldClusterIndex2, List<Cluster> newClusters,
			List<Cluster> newClusters2) {
		Cluster remainder = clusters.get(oldClusterIndex2);
		clusters.remove(oldClusterIndex);
		clusters.addAll(newClusters);
		clusters.remove(remainder);
		clusters.addAll(newClusters2);
		// update variables
		points = new ArrayList<Point2D>();
		for (Cluster c : clusters) {
			points.addAll(c.getPoints());
		}
		pointNumber = points.size();
		k = clusters.size();
	}

	public void mergeClusters(List<Integer> candidates) {
		List<Point2D> newPoints = new ArrayList<Point2D>();
		Collections.sort(candidates);
		for (int i = candidates.size() - 1; i > -1; i--) {
			int j = candidates.get(i);
			newPoints.addAll(clusters.remove(j).getPoints());
		}
		Cluster c = new Cluster(newPoints, ClusterType.CLUSTERED);
		// calculate new centroid
		double xSum = 0;
		double ySum = 0;
		for (Point2D p : c.getPoints()) {
			xSum += p.getX();
			ySum += p.getY();
		}
		c.setCentroid(new Point2D(xSum / c.getPointNumber(), ySum / c.getPointNumber()));
		clusters.add(c);
		// update variables
		points = new ArrayList<Point2D>();
		for (Cluster cluster : clusters) {
			points.addAll(cluster.getPoints());
		}
		pointNumber = points.size();
		k = clusters.size();
	}

	public void mergeClusters(List<Integer> candidates, List<Integer> candidates2) {
		// merge first two clusters
		List<Point2D> newPoints = new ArrayList<Point2D>();
		for (int i = 0; i < candidates.size(); i++) {
			newPoints.addAll(clusters.get(candidates.get(i)).getPoints());
		}
		Cluster c = new Cluster(newPoints, ClusterType.CLUSTERED);
		double xSum = 0;
		double ySum = 0;
		for (Point2D p : c.getPoints()) {
			xSum += p.getX();
			ySum += p.getY();
		}
		c.setCentroid(new Point2D(xSum / c.getPointNumber(), ySum / c.getPointNumber()));

		// merge second two clusters
		List<Point2D> newPoints2 = new ArrayList<Point2D>();
		for (int i = 0; i < candidates2.size(); i++) {
			newPoints2.addAll(clusters.get(candidates2.get(i)).getPoints());
		}
		Cluster c2 = new Cluster(newPoints2, ClusterType.CLUSTERED);
		xSum = 0;
		ySum = 0;
		for (Point2D p : c2.getPoints()) {
			xSum += p.getX();
			ySum += p.getY();
		}
		c2.setCentroid(new Point2D(xSum / c2.getPointNumber(), ySum / c2.getPointNumber()));

		// remove old clusters and add new ones
		List<Integer> allCandidates = new ArrayList<Integer>();
		allCandidates.addAll(candidates);
		allCandidates.addAll(candidates2);
		Collections.sort(allCandidates);
		for (int i = allCandidates.size() - 1; i > -1; i--) {
			int ii = allCandidates.get(i);
			clusters.remove(ii);
		}
		clusters.add(c);
		clusters.add(c2);

		// update variables
		points = new ArrayList<Point2D>();
		for (Cluster cluster : clusters) {
			points.addAll(cluster.getPoints());
		}
		pointNumber = points.size();
		k = clusters.size();
	}

	public List<Series<Number, Number>> getChartClustering() {
		chartClustering = new ArrayList<Series<Number, Number>>();
		for (int i = 0; i < getSize(); i++) {
			chartClustering.add(getCluster(i).getCluster(i));
		}
		if (clusterType == ClusterType.CLUSTERED) {
			for (int i = 0; i < getSize(); i++) {
				chartClustering.add(getCluster(i).getCentroidSeries(i));
			}
		}
		return chartClustering;
	}

	public List<Cluster> getClusters() {
		return clusters;
	}

	public Cluster getCluster(int index) {
		return clusters.get(index);
	}

	public List<Point2D> getCentroids() {
		List<Point2D> centroids = new ArrayList<Point2D>();
		if (clusterType == ClusterType.CLUSTERED) {
			clusters.forEach(c -> centroids.add(c.getCentroid()));
		}
		return centroids;
	}

	public int getSize() {
		return clusters.size();
	}

	public List<Point2D> getPoints() {
		return points;
	}

	public int getK() {
		return k;
	}

	public void setHiddenK(int hiddenK) {
		this.hiddenK = hiddenK;
	}

	public int getHiddenK() {
		return hiddenK;
	}

	public int getPointNumber() {
		return pointNumber;
	}

	public void setClustered() {
		clusterType = ClusterType.CLUSTERED;
	}

	public Clustering clone() {
		List<Cluster> clones = new ArrayList<Cluster>();
		clusters.forEach(c -> clones.add(c.clone()));
		return new Clustering(clones, clusterType);
	}

	public Clustering lightClone() {
		return new Clustering(new ArrayList<>(clusters), clusterType);
	}
}
