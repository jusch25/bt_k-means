package evaluation;

/**
 * Special container for the results of the tests to determine the number of
 * distributions.
 */
public class FindKResult {

	// how many different values for k are tested
	protected int testNumber;
	protected double[] optimalKTotal;
	protected double[] hits;
	protected double[] silhouetteTotal;

	public FindKResult(int testNumber) {
		this.testNumber = testNumber;
		optimalKTotal = new double[testNumber];
		hits = new double[testNumber];
		silhouetteTotal = new double[testNumber];
	}

	public void addData(int clusterNumber, double silhouette, int optimalK, int hit) {
		optimalKTotal[clusterNumber] += 1.0 * optimalK / 10;
		hits[clusterNumber] += hit * 10;
		silhouetteTotal[clusterNumber] += 1.0 * silhouette / 10;
	}

	public double getOptimalK(int index) {
		return optimalKTotal[index];
	}

	public double getSilhouette(int index) {
		return silhouetteTotal[index];
	}

	public double getHits(int index) {
		return hits[index];
	}
}