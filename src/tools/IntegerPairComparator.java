package tools;

import java.util.Comparator;

import javafx.util.Pair;

/**
 * Construct to compare two pairs, each with two integer values.
 */
public class IntegerPairComparator implements Comparator<Pair<Integer, Integer>> {

	@Override
	public int compare(Pair<Integer, Integer> p1, Pair<Integer, Integer> p2) {
		return Integer.compare(p1.getValue(), p2.getValue());
	}
}