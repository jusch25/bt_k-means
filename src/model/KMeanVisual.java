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
 * visualization).
 */
public class KMeanVisual {

	private int kNumber;
	private int interruption;
	private int counterSum;
	private String interruptionText;
	private Clustering inputClustering;
	private List<Point2D> unclassifiedPoints;
	private List<Point2D> initialCentroids;
	private List<Point2D> lastCentroids;
	private List<Point2D> centroids;
	private List<List<Point2D>> clusters;
	private List<Clustering> history;
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
	public List<Clustering> cluster(int kNumber, Clustering inputClustering, int interruption) {
		this.inputClustering = inputClustering;
		this.kNumber = kNumber;
		if (interruption == -1) {
			interruption = Integer.MAX_VALUE;
		}
		this.interruption = interruption;
		interruptionText = null;
		counterSum = 0;
		history = new ArrayList<Clustering>();
		centroids = null;
		Clustering inputCopy = null;
		inputCopy = inputClustering.clone();
		baseAlgorithm();
		inputCopy.getCluster(0).setSelectedPoints(initialCentroids);
		inputCopy.setClustered();
		history.add(0, inputCopy);
		return history;
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
	public List<Clustering> cluster(int kNumber, Clustering inputClustering, List<Point2D> centroids) {
		this.inputClustering = inputClustering;
		this.kNumber = kNumber;
		interruption = Integer.MAX_VALUE;
		interruptionText = null;
		counterSum = 0;
		history = new ArrayList<Clustering>();
		this.centroids = centroids;
		baseAlgorithm();
		return history;
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
	public List<Clustering> splitCluster(Clustering inputClustering, List<Point2D> newRepresentatives,
			int splittingClusterIndex, int interruption) {
		if (interruption == -1) {
			interruption = Integer.MAX_VALUE;
		}
		this.interruption = interruption;
		interruptionText = null;
		counterSum = 0;

		history = new ArrayList<Clustering>();
		history.add(inputClustering.clone());

		this.inputClustering = new Clustering(inputClustering.getCluster(splittingClusterIndex), ClusterType.CLUSTERED,
				-1);
		kNumber = newRepresentatives.size();
		centroids = newRepresentatives;
		baseAlgorithm();

		inputClustering.replaceSplittedCluster(splittingClusterIndex, clustering.getClusters());
		kNumber = inputClustering.getSize();
		this.inputClustering = inputClustering;
		centroids = inputClustering.getCentroids();
		baseAlgorithm();

		return history;
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
	public List<Clustering> splitCluster(Clustering inputClustering, List<Point2D> newRepresentatives,
			int splittingClusterIndex, int splittingClusterIndex2, int interruption) {
		this.interruption = Integer.MAX_VALUE;
		interruptionText = null;
		counterSum = 0;

		history = new ArrayList<Clustering>();
		history.add(inputClustering.clone());

		this.inputClustering = new Clustering(inputClustering.getCluster(splittingClusterIndex), ClusterType.CLUSTERED,
				-1);
		kNumber = newRepresentatives.size() / 2;
		centroids = newRepresentatives.subList(0, 2);
		baseAlgorithm();
		List<Cluster> clustersToSplit = clustering.getClusters();

		this.inputClustering = new Clustering(inputClustering.getCluster(splittingClusterIndex2), ClusterType.CLUSTERED,
				-1);
		centroids = newRepresentatives.subList(2, 4);
		baseAlgorithm();
		inputClustering.replaceSplittedCluster(splittingClusterIndex, splittingClusterIndex2, clustersToSplit,
				clustering.getClusters());

		kNumber = inputClustering.getSize();
		this.inputClustering = inputClustering;
		centroids = inputClustering.getCentroids();
		baseAlgorithm();

		return history;
	}

	public List<Clustering> mergeCluster(Clustering currentClustering, List<Integer> candidateIndices,
			int interruption) {
		inputClustering = currentClustering;
		if (interruption == -1) {
			interruption = Integer.MAX_VALUE;
		}
		this.interruption = interruption;
		interruptionText = null;
		counterSum = 0;

		history = new ArrayList<Clustering>();
		history.add(inputClustering.clone());
		inputClustering.mergeClusters(candidateIndices);
		history.add(inputClustering.clone());
		centroids = inputClustering.getCentroids();
		kNumber = inputClustering.getSize();
		baseAlgorithm();
		return history;
	}

	public List<Clustering> mergeCluster(Clustering currentClustering, List<Integer> candidateIndices,
			List<Integer> candidateIndices2, int interruption) {
		inputClustering = currentClustering;
		if (interruption == -1) {
			interruption = Integer.MAX_VALUE;
		}
		this.interruption = interruption;
		interruptionText = null;
		counterSum = 0;

		history = new ArrayList<Clustering>();
		history.add(inputClustering.clone());
		inputClustering.mergeClusters(candidateIndices, candidateIndices2);
		history.add(inputClustering.clone());
		centroids = inputClustering.getCentroids();
		kNumber = inputClustering.getSize();
		baseAlgorithm();
		return history;
	}

	/**
	 * Perform k-means on the given data.
	 */
	private void baseAlgorithm() {
		initCentroids();
		int counter = 0;
		if (counterSum == interruption) {
			counterSum--;
			counter = counterSum;
		}
		while (counterSum < interruption && counter < 1000
				&& (lastCentroids == null || !SeriesComparator.equalClustering(lastCentroids, centroids))) {
			assignPoints();
			lastCentroids = new ArrayList<Point2D>(centroids);
			calculateNewMean();
			convertToClustering();
			history.add(clustering);
			resetClusters();
			counter++;
			counterSum++;
		}
		if (counterSum == interruption) {
			if (counterSum == counter) {
				interruptionText = "Interrupted after " + interruption + " iterations";
			} else {
				interruptionText = "Interrupted after " + (interruption - counter) + "+" + counter + " iterations";
			}
		} else {
			if (counterSum == counter) {
				interruptionText = "Finished after " + counterSum + " iterations";
			} else {
				interruptionText = "Finished after " + (counterSum - counter) + "+" + counter + " iterations";
			}
		}
	}

	private void resetClusters() {
		unclassifiedPoints = new ArrayList<Point2D>(inputClustering.getPoints());
		clusters = new ArrayList<List<Point2D>>();
		for (int i = 0; i < kNumber; i++) {
			clusters.add(new ArrayList<Point2D>());
		}
	}

	private void initCentroids() {
		unclassifiedPoints = new ArrayList<Point2D>(inputClustering.getPoints());
		clusters = new ArrayList<List<Point2D>>();
		lastCentroids = new ArrayList<Point2D>();
		initialCentroids = new ArrayList<Point2D>();
		for (int i = 0; i < kNumber; i++) {
			clusters.add(new ArrayList<Point2D>());
		}
		if (centroids == null) {
			centroids = new ArrayList<Point2D>();
			for (int i = 0; i < kNumber; i++) {
				int rand = (int) (Math.random() * unclassifiedPoints.size());
				centroids.add(unclassifiedPoints.get(rand));
				initialCentroids.add(unclassifiedPoints.get(rand));
				unclassifiedPoints.remove(rand);
			}
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

	public String getInterruptionText() {
		return interruptionText;
	}
}
