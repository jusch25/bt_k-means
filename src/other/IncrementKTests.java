package other;

/**
 * Enum for the heuristics that increment the number of clusters by one.
 */
public enum IncrementKTests implements Tests {

	INC_MR("2)\nMost points + random centroids"), INC_MF("3)\nMost points + farthest centroids"), INC_ML(
			"4)\nMost points + linear approx. centroids"), INC_MP("5)\nMost points + pca centroids"), INC_DR(
					"6)\nLargest diameter + random centroids"), INC_DF(
							"7)\nLargest diameter + farthest centroids"), INC_DL(
									"8)\nLargest diameter + linear approx. centroids"), INC_DP(
											"9)\nLargest diameter + pca centroids"), INC_SR(
													"10)\nLeast Silhouette + random centroids"), INC_SF(
															"11)\nLeast Silhouette + farthest centroids"), INC_SL(
																	"12)\nLeast Silhouette + linear approx. centroids"), INC_SP(
																			"13)\nLeast Silhouette + pca centroids"), INC_MOD(
																					"14)\nStandard k-Means with additional random centroid"), INC_STD(
																							"15)\nStandard k-Means with increased k");

	public static int size = values().length;

	private String name;

	private IncrementKTests(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getDescription() {
		return "-";
	}

	public static IncClusterSelection getClusterSelection(String heuristic) {
		if (heuristic.contains("Most points")) {
			return IncClusterSelection.MAXPOINTS;
		}
		if (heuristic.contains("Largest diameter")) {
			return IncClusterSelection.MAXDIAMETER;
		}
		if (heuristic.contains("Least Silhouette")) {
			return IncClusterSelection.MINSILHOUETTE;
		}
		return null;
	}

	public static IncCentroidSelection getCentroidSelection(String heuristic) {
		if (heuristic.contains("random centroids")) {
			return IncCentroidSelection.RANDOM;
		}
		if (heuristic.contains("farthest centroids")) {
			return IncCentroidSelection.FARTHEST;
		}
		if (heuristic.contains("linear approx. centroids")) {
			return IncCentroidSelection.LINEARAPPROX;
		}
		if (heuristic.contains("pca centroids")) {
			return IncCentroidSelection.PCA;
		}
		return null;
	}
}
