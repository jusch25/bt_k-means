package model;

import java.util.ArrayList;
import java.util.List;

import other.ClusterType;
import structures.Cluster;
import structures.Clustering;
import structures.Point2D;
import tools.SeriesComparator;

/**
 * This class contains the operations for clustering a set of (unclassified)
 * spatial data points according to the k-Means algorithm for a given k (for
 * evaluation).
 */
public class KMean {

	private int kNumber;
	private int totalIterations;
	private int basicIterations;
	private long basicRuntime;
	private Clustering inputClustering;
	private List<Point2D> unclassifiedPoints;
	private List<Point2D> lastCentroids;
	private List<Point2D> centroids;
	private List<List<Point2D>> clusters;
	private Clustering clustering;

	/**
	 * Standard k-means algorithm.
	 * 
	 * @param kNumber
	 *            number of clusters
	 * @param inputClustering
	 *            unstructured data to be clustered
	 * @return list of all intermediate clusterings
	 */
	public Clustering cluster(int kNumber, Clustering inputClustering) {
		this.inputClustering = inputClustering;
		this.kNumber = kNumber;
		centroids = null;
		basicIterations = 0;
		baseAlgorithm();
		totalIterations = basicIterations;
		return clustering;
	}

	/**
	 * Standard k-means algorithm with initial centroids.
	 * 
	 * @param kNumber
	 *            number of clusters
	 * @param inputClustering
	 *            unstructured data to be clustered
	 * @param centroids
	 *            initial centroids
	 * @return list of all intermediate clusterings
	 */
	public Clustering cluster(int kNumber, Clustering inputClustering, List<Point2D> centroids) {
		this.inputClustering = inputClustering;
		this.kNumber = kNumber;
		this.centroids = centroids;
		basicIterations = 0;
		baseAlgorithm();
		totalIterations = basicIterations;
		return clustering;
	}

	/**
	 * Algorithm for splitting a cluster by applying k-means on the given new
	 * centroids in the cluster. Apply then k-Means on the resulting clustering
	 * until stable.
	 * 
	 * @param inputClustering
	 *            the clustered data
	 * @param newCentroids
	 *            k new centroids for splitting the chosen cluster in k parts
	 * @param splittingClusterIndex
	 *            indicates the number of the cluster which is chosen for
	 *            splitting
	 * @return list of all intermediate clusterings
	 */
	public Clustering splitCluster(Clustering inputClustering, List<Point2D> newRepresentatives,
			int splittingClusterIndex) {
		this.inputClustering = new Clustering(inputClustering.getCluster(splittingClusterIndex), ClusterType.CLUSTERED,
				-1);
		kNumber = newRepresentatives.size();
		centroids = newRepresentatives;
		basicIterations = 0;
		baseAlgorithm();

		totalIterations = basicIterations;
		basicIterations = 0;
		long runtime = basicRuntime;
		inputClustering.replaceSplittedCluster(splittingClusterIndex, clustering.getClusters());
		kNumber = inputClustering.getSize();
		this.inputClustering = inputClustering;
		centroids = inputClustering.getCentroids();
		baseAlgorithm();

		totalIterations += basicIterations;
		basicRuntime += runtime;
		return clustering;
	}

	/**
	 * Algorithm for splitting two clusters, each by applying k-means on the
	 * given new centroids in the cluster. Apply then k-Means on the resulting
	 * clustering until stable.
	 * 
	 * @param inputClustering
	 *            the clustered data
	 * @param newCentroids
	 *            k new centroids for splitting the chosen cluster in k parts
	 * @param splittingClusterIndex
	 *            indicates the number of the first cluster which is chosen for
	 *            splitting
	 * @param splittingClusterIndex2
	 *            indicates the number of the second cluster which is chosen for
	 *            splitting
	 * @return list of all intermediate clusterings
	 */
	public Clustering splitCluster(Clustering inputClustering, List<Point2D> newRepresentatives,
			int splittingClusterIndex, int splittingClusterIndex2) {
		this.inputClustering = new Clustering(inputClustering.getCluster(splittingClusterIndex), ClusterType.CLUSTERED,
				-1);
		kNumber = newRepresentatives.size() / 2;
		centroids = newRepresentatives.subList(0, 2);
		basicIterations = 0;
		baseAlgorithm();

		totalIterations = basicIterations;
		basicIterations = 0;
		long runtime = basicRuntime;
		List<Cluster> clustersToSplit = clustering.getClusters();

		this.inputClustering = new Clustering(inputClustering.getCluster(splittingClusterIndex2), ClusterType.CLUSTERED,
				-1);
		centroids = newRepresentatives.subList(2, 4);
		basicIterations = 0;
		baseAlgorithm();

		totalIterations += basicIterations;
		basicIterations = 0;
		runtime += basicRuntime;
		inputClustering.replaceSplittedCluster(splittingClusterIndex, splittingClusterIndex2, clustersToSplit,
				clustering.getClusters());

		kNumber = inputClustering.getSize();
		this.inputClustering = inputClustering;
		centroids = inputClustering.getCentroids();
		baseAlgorithm();

		totalIterations += basicIterations;
		basicRuntime += runtime;
		return clustering;
	}

	public Clustering mergeCluster(Clustering currentClustering, List<Integer> candidateIndices) {
		inputClustering = currentClustering;
		inputClustering.mergeClusters(candidateIndices);
		centroids = inputClustering.getCentroids();
		kNumber = inputClustering.getSize();
		basicIterations = 0;
		baseAlgorithm();
		totalIterations = basicIterations;
		return clustering;
	}

	public Clustering mergeCluster(Clustering currentClustering, List<Integer> candidateIndices,
			List<Integer> candidateIndices2) {
		inputClustering = currentClustering;
		inputClustering.mergeClusters(candidateIndices, candidateIndices2);
		centroids = inputClustering.getCentroids();
		kNumber = inputClustering.getSize();
		basicIterations = 0;
		baseAlgorithm();
		totalIterations = basicIterations;
		return clustering;
	}

	/**
	 * Perform k-means on the given data.
	 */
	private void baseAlgorithm() {
		long start = System.nanoTime();
		initCentroids();
		int counter = 0;
		while (counter < 1000
				&& (lastCentroids == null || !SeriesComparator.equalClustering(lastCentroids, centroids))) {
			resetClusters();
			assignPoints();
			lastCentroids = new ArrayList<Point2D>(centroids);
			calculateNewMean();
			basicIterations++;
			counter++;
		}
		convertToClustering();
		basicRuntime = System.nanoTime() - start;
	}

	private void resetClusters() {
		clusters = new ArrayList<List<Point2D>>();
		for (int i = 0; i < kNumber; i++) {
			clusters.add(new ArrayList<Point2D>());
		}
	}

	private void initCentroids() {
		unclassifiedPoints = new ArrayList<Point2D>(inputClustering.getPoints());
		lastCentroids = new ArrayList<Point2D>();
		if (centroids == null) {
			centroids = new ArrayList<Point2D>();
			for (int i = 0; i < kNumber; i++) {
				int rand = (int) (Math.random() * unclassifiedPoints.size());
				centroids.add(unclassifiedPoints.get(rand));
				unclassifiedPoints.remove(rand);
			}
			unclassifiedPoints = new ArrayList<Point2D>(inputClustering.getPoints());
		}
	}

	private void assignPoints() {
		for (Point2D p : unclassifiedPoints) {
			double minDist = Double.MAX_VALUE;
			int cluster = 0;
			for (int i = 0; i < kNumber; i++) {
				double dist = p.getDist(centroids.get(i));
				if (dist < minDist) {
					minDist = dist;
					cluster = i;
				}
			}
			clusters.get(cluster).add(p);
		}
	}

	private void calculateNewMean() {
		centroids.clear();
		for (int i = 0; i < clusters.size(); i++) {
			double xSum = 0;
			double ySum = 0;
			for (Point2D p : clusters.get(i)) {
				xSum += p.getX();
				ySum += p.getY();
			}
			centroids.add(new Point2D(xSum / clusters.get(i).size(), ySum / clusters.get(i).size()));
		}
	}

	private void convertToClustering() {
		List<Cluster> cs = new ArrayList<Cluster>();
		for (int i = 0; i < kNumber; i++) {
			Cluster c = new Cluster(ClusterType.CLUSTERED);
			for (Point2D p : clusters.get(i)) {
				c.addPoint(p);
			}
			c.setCentroid(centroids.get(i));
			cs.add(c);
		}
		clustering = new Clustering(cs, ClusterType.CLUSTERED);
	}

	public int getTotalIterations() {
		return totalIterations;
	}

	public void addTotalIterations(int totalIterations) {
		this.totalIterations += totalIterations;
	}

	public int getBasicIterations() {
		return basicIterations;
	}

	public void addBasicIterations(int basicIterations) {
		this.basicIterations += basicIterations;
	}

	public long getBasicRuntime() {
		return basicRuntime;
	}

	public void addBasicRuntime(double basicRuntime) {
		this.basicRuntime += basicRuntime;
	}
}
