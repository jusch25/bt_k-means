package structures;

import javafx.scene.chart.XYChart.Data;
import tools.Functions;

/**
 * Basic element of every structure. Consists of two values that represent the
 * two dimensions.
 */
public class Point2D {

	private double x;
	private double y;

	public Point2D(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public double getDist(Point2D p) {
		return Functions.euclideanDistance(this, p);
	}

	/**
	 * Return a new Data object with every call, otherwise
	 * "IllegalArgumentException: duplicate childern"
	 * 
	 * @return
	 */
	public Data<Number, Number> getPoint() {
		return new Data<Number, Number>(x, y);
	}

	@Override
	public String toString() {
		return x + " " + y + "\n";
	}

	public boolean equalPoint(Point2D p) {
		double cx = Math.abs(x - p.x);
		double cy = Math.abs(y - p.y);
		return cx < 0.000000001 && cy < 0.000000001;
	}

	public Point2D clone() {
		return new Point2D(x, y);
	}
}
