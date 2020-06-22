package structures;

import java.util.Random;

/**
 * A distribution generates random points according to the parameters of the
 * 2D-Gauss-Function.
 */
public class NormalDistribution {

	private double meanX;
	private double meanY;
	private double varianceX;
	private double varianceY;
	private double rotationCos;
	private double rotationSin;
	private Random generator;

	/**
	 * A 2D-Gauss/Normal-distribution with random rotation, position and size
	 * for creating random data points.
	 * 
	 * @param size
	 *            the interval for mean and variance values of the distribution
	 * @param distNumber
	 *            number of distributions in the generating process; used to
	 *            optimize variance of the distribution (more distributions
	 *            result in lower variances to balance distances between the
	 *            distributions)
	 */
	public NormalDistribution(double size, int distNumber) {
		meanX = Math.random() * size;
		meanY = Math.random() * size;
		double variance = Math.pow(size, 0.8) / (Math.sqrt(distNumber) * 2);
		varianceY = Math.random() * variance + 0.2 * variance;
		varianceX = Math.random() * variance + 0.2 * variance;
		double rotation = Math.random() * 2 * Math.PI;
		rotationCos = Math.sin(rotation);
		rotationSin = Math.cos(rotation);
		generator = new Random();
	}

	/**
	 * Generate a random point. Formula for rotation according to ellipse
	 * equation.
	 * 
	 * @return a new 2D-point with random coordinates with respect to this
	 *         distribution
	 */
	public Point2D generatePoint() {
		double rand1 = generator.nextGaussian();
		double rand2 = generator.nextGaussian();
		// rotation with ellipse formula
		double x = meanX + rand1 * varianceX * rotationCos - rand2 * varianceY * rotationSin;
		double y = meanY + rand1 * varianceX * rotationSin + rand2 * varianceY * rotationCos;
		Point2D p = new Point2D(x, y);
		return p;
	}
}
