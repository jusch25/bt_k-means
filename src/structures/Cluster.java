package structures;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.chart.XYChart.Series;
import other.ClusterType;

/**
 * A cluster contains an arbitrary number of points an is represented by the
 * mean value of their coordinates.
 */
public class Cluster {

	private List<Point2D> points;
	private ClusterType clusterType;
	// private int clusterNumber;
	private int pointNumber;
	private Point2D centroid;
	private List<Point2D> selectedPoints;
	private Series<Number, Number> cluster;
	private Series<Number, Number> centroidSeries;

	public Cluster(ClusterType type) {
		clusterType = type;
		points = new ArrayList<Point2D>();
		pointNumber = 0;
	}

	public Cluster(List<Point2D> points, ClusterType type) {
		clusterType = type;
		this.points = points;
		pointNumber = points.size();
	}

	public int getPointNumber() {
		return pointNumber;
	}

	public Series<Number, Number> getCluster(int clusterIndex) {
		cluster = new Series<Number, Number>();
		cluster.setName(clusterType.getName() + (clusterIndex + 1));
		points.forEach(p -> cluster.getData().add(p.getPoint()));
		return cluster;
	}

	public Series<Number, Number> getCentroidSeries(int clusterIndex) {
		centroidSeries = new Series<Number, Number>();
		centroidSeries.setName("Centroid" + (clusterIndex + 1));
		if (centroid != null) {
			centroidSeries.getData().add(centroid.getPoint());
		}
		if (selectedPoints != null) {
			selectedPoints.forEach(e -> centroidSeries.getData().add(e.getPoint()));
		}
		return centroidSeries;
	}

	public Point2D getCentroid() {
		return centroid;
	}

	public List<Point2D> getPoints() {
		return points;
	}

	public void addPoint(Point2D point) {
		points.add(point);
		pointNumber++;
	}

	public Point2D getPoint(int index) {
		return points.get(index);
	}

	public void setCentroid(Point2D centroid) {
		this.centroid = centroid;
	}

	public void setSelectedPoints(List<Point2D> selectedPoints) {
		this.selectedPoints = selectedPoints;
	}

	@Deprecated
	public boolean equalCluster(Cluster comp) {
		if (pointNumber != comp.getPointNumber()) {
			return false;
		}
		for (int i = 0; i < points.size(); i++) {
			if (!points.get(i).equalPoint(comp.getPoint(i))) {
				return false;
			}
		}
		return true;
	}

	public Cluster clone() {
		List<Point2D> clones = new ArrayList<Point2D>();
		points.forEach(p -> clones.add(p.clone()));
		Cluster clone = new Cluster(points, clusterType);
		clone.setCentroid(centroid);
		return clone;
	}
}
