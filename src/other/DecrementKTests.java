package other;

/**
 * Enum for the heuristics that decrement the number of clusters by one.
 */
public enum DecrementKTests implements Tests {

	DEC_C("2)\nClosest clusters"), DEC_BS("3)\nLargest sum of silhouettes"), DEC_SS(
			"4)\nLeast sum of silhouettes"), DEC_P("5)\nLeast sum of point numbers"), DEC_D(
					"6)\nLeast sum of diameters"), DEC_BSC("7)\nLargest silhouette + closest cluster"), DEC_SSC(
							"8)\nLeast silhouette + closest cluster"), DEC_PC(
									"9)\nLeast points + closest cluster"), DEC_DC(
											"10)\nLeast diameter + closest cluster"), DEC_RAND(
													"11)\nRandom clusters"), DEC_MOD(
															"12)\nStandard k-Means with random deleted centroid"), DEC_STD(
																	"13)\nStandard k-Means with decreased k");

	public static int size = values().length;
	private String name;

	private DecrementKTests(String name) {
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

	public static DecClusterSelection getClusterSelection(String heuristic) {
		if (heuristic.contains("Closest clusters")) {
			return DecClusterSelection.CLOSEST;
		}
		if (heuristic.contains("Largest sum of silhouettes")) {
			return DecClusterSelection.MAXSILHOUETTE;
		}
		if (heuristic.contains("Least sum of silhouettes")) {
			return DecClusterSelection.MINSILHOUETTE;
		}
		if (heuristic.contains("Least sum of point numbers")) {
			return DecClusterSelection.MINPOINTS;
		}
		if (heuristic.contains("Least sum of diameters")) {
			return DecClusterSelection.MINDIAMETER;
		}
		if (heuristic.contains("Largest silhouette + closest cluster")) {
			return DecClusterSelection.MAXSILHOUETTECOMB;
		}
		if (heuristic.contains("Least silhouette + closest cluster")) {
			return DecClusterSelection.MINSILHOUETTECOMB;
		}
		if (heuristic.contains("Least points + closest cluster")) {
			return DecClusterSelection.MINPOINTSCOMB;
		}
		if (heuristic.contains("Least diameter + closest cluster")) {
			return DecClusterSelection.MINDIAMETERCOMB;
		}
		if (heuristic.contains("Random clusters")) {
			return DecClusterSelection.RANDOM;
		}
		return null;
	}
}
