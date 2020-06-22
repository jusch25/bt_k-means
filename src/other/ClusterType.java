package other;

/**
 * Enum for the property of a cluster or a clustering.
 */
public enum ClusterType {

	UNCLASSIFIED("Unclassified"), INITIAL("Initial"), CLUSTERED("Cluster");

	private String name;

	private ClusterType(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
