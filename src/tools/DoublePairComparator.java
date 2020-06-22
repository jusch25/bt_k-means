package tools;

import java.util.Comparator;

import javafx.util.Pair;

/**
 * Construct to compare two pairs, each with an integer and a double value.
 */
public class DoublePairComparator implements Comparator<Pair<Integer, Double>> {

	@Override
	public int compare(Pair<Integer, Double> p1, Pair<Integer, Double> p2) {
		return Double.compare(p1.getValue(), p2.getValue());
	}
}
