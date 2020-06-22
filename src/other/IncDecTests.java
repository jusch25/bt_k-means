package other;

/**
 * Enum for the compared heuristics that either decrement or increment the
 * number of clusters by one.
 */
public enum IncDecTests implements Tests {

	INC_INIT("1)\nIncrement initial"), INC_DR("2)\nLargest diameter + random centroids"), INC_DF(
			"3)\nLargest diameter + farthest centroids"), INC_DL(
					"4)\nLargest diameter + linear approx. centroids"), INC_DP(
							"5)\nLargest diameter + pca centroids"), DEC_C("6)\nClosest clusters"), DEC_SSC(
									"7)\nLeast silhouette + closest cluster"), DEC_PC(
											"8)\nLeast points + closest cluster"), DEC_DC(
													"9)\nLeast diameter + closest cluster"), DEC_INIT(
															"10\nDecrement initial");

	public static int size = values().length;
	private String name;

	private IncDecTests(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getDescription() {
		return "+";
	}
}