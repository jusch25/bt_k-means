package model;

import java.util.ArrayList;
import java.util.List;

import filessystem.FileExport;
import filessystem.FileImport;
import structures.Clustering;
import tools.Silhouette;

/**
 * This class contains the basic logic and calls other model components; is
 * connected with the view over the controller
 */
public class Model {

	private PointGenerator generator;
	private KMeanVisual kMean;
	private KModifier kModifier;
	private Clustering unclassifiedClustering;
	private Clustering initialDistributions;
	private List<Clustering> history;
	private int pointNumber;
	private int k;
	private int counter;
	private int fileCounter;

	public Model() {
		generator = new PointGenerator();
		kMean = new KMeanVisual();
		kModifier = new KModifier(kMean);
	}

	public Clustering initData(int pointNumber, int distNumber) {
		pointNumber = Math.min(pointNumber, 100000);
		distNumber = Math.min(distNumber, pointNumber);
		unclassifiedClustering = generator.generate(pointNumber, distNumber, 1.5);
		initialDistributions = generator.getInitialDistributions();
		// unclassifiedClustering = FileImport.loadUnclassified("1000points_9");
		// initialDistributions = FileImport.loadUnclassified("1000points_9");
		this.pointNumber = pointNumber;
		return initialDistributions;
	}

	public void storeClustering() {
		FileExport.storeUnclassified(unclassifiedClustering);
		FileExport.storeDistributions(initialDistributions);
	}

	public Clustering loadClustering() {
		// history = FileImport.loadTestData("1000points_");
		// unclassifiedClustering = history.get(31);
		// initialDistributions = history.get(31);
		// counter = 0;
		unclassifiedClustering = FileImport.loadUnclassified(fileCounter);
		initialDistributions = unclassifiedClustering;
		pointNumber = unclassifiedClustering.getPointNumber();
		fileCounter = (fileCounter + 1) % FileImport.files.length;
		return unclassifiedClustering;
	}

	public void clusterData(int kNumber, int interruption) {
		k = Math.min(kNumber, pointNumber);
		history = kMean.cluster(k, unclassifiedClustering, interruption);
		history.add(0, unclassifiedClustering);
		history.add(0, initialDistributions);
		counter = 0;
		// System.out.println("Std: " + k + ": " +
		// Silhouette.calculateSilhouette(history.get(history.size() - 1)));
	}

	public Clustering nextClustering() {
		counter++;
		return history.get(counter);
	}

	public Clustering lastClustering() {
		counter = history.size() - 1;
		return history.get(counter);
	}

	public Clustering previousClustering() {
		counter--;
		return history.get(counter);
	}

	public Clustering firstClustering() {
		counter = 0;
		return history.get(counter);
	}

	public Clustering getUnclassified() {
		counter = 1;
		return history.get(counter);
	}

	public Clustering incrementK(String heuristic, int interruption) {
		Clustering currentClustering = history.get(history.size() - 1);
		history = kModifier.incrementK(currentClustering, heuristic, interruption);
		history.add(0, initialDistributions);
		counter = 1;
		k = history.get(history.size() - 1).getSize();
		// System.out.println("Inc: " + k + ": " +
		// Silhouette.calculateSilhouette(history.get(history.size() - 1)));
		return history.get(counter);
	}

	public Clustering decrementK(String heuristic, int interruption) {
		Clustering currentClustering = history.get(history.size() - 1);
		history = kModifier.decrementK(currentClustering, heuristic, interruption);
		history.add(0, initialDistributions);
		counter = 1;
		k = history.get(history.size() - 1).getSize();
		// System.out.println("Dec: " + k + ": " +
		// Silhouette.calculateSilhouette(history.get(history.size() - 1)));
		return history.get(counter);
	}

	public Clustering findK(String heuristic1, String heuristic2, int k) {
		ArrayList<Clustering> totalHistory = new ArrayList<Clustering>();
		clusterData(k, -1);
		totalHistory.add(initialDistributions);
		totalHistory.add(history.get(history.size() - 1).lightClone());
		double bestSilhouette = Silhouette.calculateSilhouette(history.get(history.size() - 1));
		double currentSilhouette = 0;
		boolean endWhile = false;
		while (!endWhile) {
			incrementK(heuristic1, -1);
			currentSilhouette = Silhouette.calculateSilhouette(history.get(history.size() - 1));
			if (currentSilhouette > bestSilhouette) {
				bestSilhouette = currentSilhouette;
				totalHistory.add(history.get(history.size() - 1).lightClone());
			} else {
				endWhile = true;
				totalHistory.add(history.get(history.size() - 1).lightClone());
			}
		}
		history.add(totalHistory.get(totalHistory.size() - 2).lightClone());
		totalHistory.add(history.get(history.size() - 1).lightClone());
		k--;
		endWhile = false;
		while (!endWhile) {
			decrementK(heuristic2, -1);
			currentSilhouette = Silhouette.calculateSilhouette(history.get(history.size() - 1));
			if (currentSilhouette > bestSilhouette) {
				bestSilhouette = currentSilhouette;
				totalHistory.add(history.get(history.size() - 1).lightClone());
			} else {
				endWhile = true;
				totalHistory.add(history.get(history.size() - 1).lightClone());
			}
		}
		k++;
		history = totalHistory;
		counter = history.size() - 2;
		return history.get(counter);
	}

	public int position() {
		if (counter == 0) {
			return -1;
		}
		if (counter == history.size() - 1) {
			return 1;
		}
		return 0;
	}

	public int kValue() {
		if (k == 1) {
			return -1;
		}
		if (k == pointNumber) {
			return 1;
		}
		return 0;
	}

	public int getK() {
		return k;
	}

	public String getInterruptionText() {
		return kMean.getInterruptionText();
	}

	public double getCurrentSilhouette() {
		return Silhouette.calculateSilhouette(history.get(counter));
	}

	public int getHistorySize() {
		return history.size();
	}

	public int getCounter() {
		return counter + 1;
	}
}
