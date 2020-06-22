package evaluation;

/**
 * This result aggregates the values of all datasets to compute their average
 * values.
 */
public class AvgResult extends TestResult {

	private int[] testSizes;

	public AvgResult(int sampleSize, int differentK, int testNumber) {
		initialize(sampleSize, differentK, testNumber);
		testSizes = new int[differentK];
	}

	@Override
	public void addClustering(int clusterNumber, long runtimeTotal, long runtimeBasic, int iterationTotal,
			int iterationBasic, double silhouette) {
		addClustering(clusterNumber, 1.0 * runtimeTotal, 1.0 * runtimeBasic, 1.0 * iterationTotal, 1.0 * iterationBasic,
				silhouette);
	}

	public void addClustering(int clusterNumber, double runtimeTotal, double runtimeBasic, double iterationTotal,
			double iterationBasic, double silhouette) {
		runtimesTotal[clusterNumber] += runtimeTotal;
		iterationsTotal[clusterNumber] += iterationTotal;
		silhouettes[clusterNumber] += silhouette - (int) silhouette;
		runtimesBasic[clusterNumber] += runtimeBasic;
		iterationsBasic[clusterNumber] += iterationBasic;
		testSizes[clusterNumber / testNumber]++;
	}

	@Override
	public void addInitial(int kRound, long runtimeTotal, long runtimeBasic, int iterationTotal) {
		setInitial(kRound, 1.0 * runtimeTotal, 1.0 * runtimeBasic, 1.0 * iterationTotal);
	}

	public void setInitial(int kRound, double runtimeTotal, double runtimeBasic, double iterationTotal) {
		initialRuntimeTotal[kRound] += runtimeTotal;
		initialRuntimeBasic[kRound] += runtimeBasic;
		initialIteration[kRound] += iterationTotal;
	}

	@Override
	public double getRuntimeTotal(int index) {
		return runtimesTotal[index] / testSizes[index / testNumber] * testNumber;
	}

	@Override
	public double getIterationTotal(int index) {
		return iterationsTotal[index] / testSizes[index / testNumber] * testNumber;
	}

	@Override
	public double getSilhouette(int index) {
		return silhouettes[index] / testSizes[index / testNumber] * testNumber;
	}

	@Override
	public double getRuntimeBasic(int index) {
		return runtimesBasic[index] / testSizes[index / testNumber] * testNumber;
	}

	@Override
	public double getIterationBasic(int index) {
		return iterationsBasic[index] / testSizes[index / testNumber] * testNumber;
	}

	@Override
	public int getEmptyClusters(int index) {
		return 0;
	}

	@Override
	public double getInitialRuntimeTotal(int index) {
		return initialRuntimeTotal[index] / testSizes[index] * testNumber;
	}

	@Override
	public double getInitialRuntimeBasic(int index) {
		return initialRuntimeBasic[index] / testSizes[index] * testNumber;
	}

	@Override
	public double getInitialIteration(int index) {
		return initialIteration[index] / testSizes[index] * testNumber;
	}
}
