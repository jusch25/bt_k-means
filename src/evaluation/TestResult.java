package evaluation;

/**
 * Specifies the required attributes and methods for all standard tests.
 */
public abstract class TestResult {

	// how often is each test executed (average values of many tests =
	// statistically significant)
	protected int sampleSize;
	// indicates, how many different tests below the initial number of clusters
	// are executed (kNumber = 3: for initial k=4: 3 tests with k=1,k=2,k=3; for
	// initial k=2: only kNumber=1 possible)
	protected int differentK;
	// how many different heuristics (including standard k-Means) are tested
	protected int testNumber;
	protected double[] initialRuntimeTotal;
	protected double[] initialRuntimeBasic;
	protected double[] initialIteration;
	protected double[] runtimesTotal;
	protected double[] iterationsTotal;
	protected double[] silhouettes;
	protected double[] runtimesBasic;
	protected double[] iterationsBasic;
	protected double[] emptyClusters;
	protected int[] maxIterationsTotal;
	protected int[] maxIterationsBasic;

	protected void initialize(int sampleSize, int differentK, int testNumber) {
		this.sampleSize = sampleSize;
		this.differentK = differentK;
		this.testNumber = testNumber;
		runtimesTotal = new double[differentK * testNumber];
		iterationsTotal = new double[differentK * testNumber];
		silhouettes = new double[differentK * testNumber];
		runtimesBasic = new double[differentK * testNumber];
		iterationsBasic = new double[differentK * testNumber];
		emptyClusters = new double[differentK * testNumber];
		maxIterationsTotal = new int[differentK * testNumber];
		maxIterationsBasic = new int[differentK * testNumber];
		initialRuntimeTotal = new double[differentK];
		initialRuntimeBasic = new double[differentK];
		initialIteration = new double[differentK];
	}

	public abstract void addClustering(int clusterNumber, long runtimeTotal, long runtimeBasic, int iterationTotal,
			int iterationBasic, double silhouette);

	public abstract void addInitial(int kRound, long runtimeTotal, long runtimeBasic, int iterationTotal);

	public abstract int getEmptyClusters(int index);

	public int getDifferentK() {
		return differentK;
	}

	public double getRuntimeTotal(int index) {
		return runtimesTotal[index];
	}

	public double getIterationTotal(int index) {
		return iterationsTotal[index];
	}

	public double getSilhouette(int index) {
		return silhouettes[index];
	}

	public double getRuntimeBasic(int index) {
		return runtimesBasic[index];
	}

	public double getIterationBasic(int index) {
		return iterationsBasic[index];
	}

	public int getMaxIterationsTotal(int index) {
		return maxIterationsTotal[index];
	}

	public int getMaxIterationsBasic(int index) {
		return maxIterationsBasic[index];
	}

	public double getInitialRuntimeTotal(int index) {
		return initialRuntimeTotal[index];
	}

	public double getInitialRuntimeBasic(int index) {
		return initialRuntimeBasic[index];
	}

	public double getInitialIteration(int index) {
		return initialIteration[index];
	}

	public double getAvgRuntimeTotal(int index) {
		double sum = 0.0;
		for (int i = index * testNumber; i < (index + 1) * testNumber; i++) {
			sum += runtimesTotal[i];
		}
		return sum / testNumber;
	}

	public double getAvgIterationTotal(int index) {
		double sum = 0.0;
		for (int i = index * testNumber; i < (index + 1) * testNumber; i++) {
			sum += iterationsTotal[i];
		}
		return sum / testNumber;
	}

	public double getAvgSilhouette(int index) {
		double sum = 0.0;
		for (int i = index * testNumber; i < (index + 1) * testNumber; i++) {
			sum += silhouettes[i];
		}
		return sum / testNumber;
	}

	public double getAvgRuntimeBasic(int index) {
		double sum = 0.0;
		for (int i = index * testNumber; i < (index + 1) * testNumber; i++) {
			sum += runtimesBasic[i];
		}
		return sum / testNumber;
	}

	public double getAvgIterationBasic(int index) {
		double sum = 0.0;
		for (int i = index * testNumber; i < (index + 1) * testNumber; i++) {
			sum += iterationsBasic[i];
		}
		return sum / testNumber;
	}
}
