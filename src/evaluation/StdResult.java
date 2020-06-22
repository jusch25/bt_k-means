package evaluation;

/**
 * Container for most of the test results. Contains the results of a single
 * dataset.
 */
public class StdResult extends TestResult {

	public StdResult(int sampleSize, int differentK, int testSize) {
		initialize(sampleSize, differentK, testSize);
	}

	@Override
	public void addClustering(int clusterNumber, long runtimeTotal, long runtimeBasic, int iterationTotal,
			int iterationBasic, double silhouette) {
		runtimesTotal[clusterNumber] += 1.0 * runtimeTotal / (sampleSize * 1000000);
		if (iterationBasic > 999) {
			maxIterationsTotal[clusterNumber]++;
			maxIterationsBasic[clusterNumber]++;
		}
		if (iterationTotal - iterationBasic > 999) {
			maxIterationsTotal[clusterNumber]++;
		}
		iterationsTotal[clusterNumber] += 1.0 * iterationTotal / sampleSize;
		int emptyClusterNumber = (int) silhouette;
		silhouettes[clusterNumber] += (silhouette - emptyClusterNumber) / sampleSize;
		emptyClusters[clusterNumber] += 1.0 * emptyClusterNumber;
		runtimesBasic[clusterNumber] += 1.0 * runtimeBasic / (sampleSize * 1000000);
		iterationsBasic[clusterNumber] += 1.0 * iterationBasic / sampleSize;
	}

	@Override
	public void addInitial(int kRound, long runtimeTotal, long runtimeBasic, int iterationTotal) {
		initialRuntimeTotal[kRound] += 1.0 * runtimeTotal / (sampleSize * 1000000);
		initialRuntimeBasic[kRound] += 1.0 * runtimeBasic / (sampleSize * 1000000);
		initialIteration[kRound] += 1.0 * iterationTotal / sampleSize;
	}

	@Override
	public int getEmptyClusters(int index) {
		double val = Math.round(emptyClusters[index]);
		if (val < 0.000000001) {
			return 0;
		}
		return (int) Math.ceil(Math.log10(emptyClusters[index]));
	}
}
