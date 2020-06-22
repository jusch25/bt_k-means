package other;

/**
 * Enum for the heuristics that increment the number of clusters by two.
 */
public enum DoubleIncrementKTests implements Tests {

	INC_DR("2)\nLargest diameter + random centroids"), INC_DF("3)\nLargest diameter + farthest centroids"), INC_DL(
			"4)\nLargest diameter + linear approx. centroids"), INC_DP("5)\nLargest diameter + pca centroids"), INC_2DR(
					"6)\n2x Largest diameter + random centroids"), INC_2DF(
							"7)\n2x Largest diameter + farthest centroids"), INC_2DL(
									"8)\n2x Largest diameter + linear approx. centroids"), INC_2DP(
											"9)\n2x Largest diameter + pca centroids"), INC_DR3(
													"10)\nLargest diameter + 3 random centroids"), INC_DF3(
															"11)\nLargest diameter + 3 farthest centroids"), INC_DL3(
																	"12)\nLargest diameter + 3 linear approx. centroids"), INC_DP3(
																			"13)\nLargest diameter + 3 pca centroids"), INC_MOD(
																					"14)\nStandard k-Means with 2 additional random centroids"), INC_STD(
																							"15)\nStandard k-Means with k increased by 2");

	public static int size = values().length;

	private String name;

	private DoubleIncrementKTests(String name) {
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
}