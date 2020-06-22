package tools;

import java.util.List;

import structures.Point2D;

/**
 * Compares two lists of centroids, to determine equality of the corresponding
 * clusterings (the termination condition of k-Means). The lists are only equal,
 * if the contain the same centroids in the same order.
 */
public class SeriesComparator {

	public static boolean equalClustering(List<Point2D> list1, List<Point2D> list2) {
		if (list1.size() != list2.size()) {
			return false;
		}
		for (int i = 0; i < list1.size(); i++) {
			if (!list1.get(i).equalPoint(list2.get(i))) {
				return false;
			}
		}
		return true;
	}
}
