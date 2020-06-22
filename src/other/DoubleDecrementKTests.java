package other;

/**
 * Enum for the heuristics that decrement the number of clusters by two.
 */
public enum DoubleDecrementKTests implements Tests {

	DEC_C("2)\nClosest clusters"), DEC_SSC("3)\nLeast silhouette + closest cluster"), DEC_PC(
			"4)\nLeast points + closest cluster"), DEC_DC("5)\nLeast diameter + closest cluster"), DEC_2C(
					"6)\n2x Closest clusters"), DEC_2SSC("7)\n2x Least silhouette + closest cluster"), DEC_2PC(
							"8)\n2x Least points + closest cluster"), DEC_2DC(
									"9)\n2x Least diameter + closest cluster"), DEC_C3(
											"10)\n3 Closest clusters"), DEC_SSC3(
													"11)\nLeast silhouette + 2 closest clusters"), DEC_PC3(
															"12)\nLeast points + 2 closest clusters"), DEC_DC3(
																	"13)\nLeast diameter + 2 closest clusters"), DEC_MOD(
																			"14)\nStandard k-Means with two random deleted centroid"), DEC_STD(
																					"15)\nStandard k-Means with k decreased by two");

	public static int size = values().length;
	private String name;

	private DoubleDecrementKTests(String name) {
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
