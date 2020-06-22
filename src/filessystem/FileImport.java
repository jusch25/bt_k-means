package filessystem;

import java.io.BufferedReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import other.ClusterType;
import structures.Cluster;
import structures.Clustering;
import structures.Point2D;

/**
 * This class contains operations to parse text or csv files and generate the
 * clusterings.
 */
public class FileImport {

	// ccpp: real multivariant data from UCI repository
	// (http://archive.ics.uci.edu)
	// art: artificial data from deric's clustering-benchmark
	// (https://github.com/deric/clustering-benchmark)
	public static final String[] files = { "mouse.csv", "blobs.csv", "art_1.txt", "art_2.txt", "art_3.txt", "art_4.txt",
			"art_5.txt", "art_6.txt", "art_7.txt", "art_8.txt",
			"art_9.txt", }; /*
							 * "ccpp_1.txt", "ccpp_2.txt", "ccpp_3.txt",
							 * "ccpp_4.txt", "ccpp_5.txt", "ccpp_6.txt",
							 * "ccpp_7.txt", "ccpp_8.txt", "ccpp_9.txt",
							 * "ccpp_10.txt", "ccpp_11.txt", "ccpp_12.txt",
							 * "ccpp_13.txt", "ccpp_14.txt", "ccpp_15.txt",
							 * "ccpp_16.txt", "ccpp_17.txt", "ccpp_18.txt",
							 * "ccpp_19.txt", "ccpp_20.txt", };
							 */

	public static Clustering loadUnclassified(String fileName) {
		List<Point2D> points = new ArrayList<Point2D>();
		int kNumber = -1;
		Path file = (Path) Paths.get("./../TestData/" + fileName + ".txt");
		try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
			String content = reader.readLine();
			while (content != null) {
				switch (content.substring(0, Math.min(1, content.length()))) {
				case "": // empty line
					break;
				case "#": // comments
					break;
				case "k": // number of initial clusters
					kNumber = Integer.parseInt(content.substring(3));
					break;
				default: // x and y position as x,y
					String[] coordinates = content.split(",");
					points.add(new Point2D(Double.parseDouble(coordinates[0]), Double.parseDouble(coordinates[1])));
					break;
				}
				content = reader.readLine();
			}
			reader.close();
			Cluster c = new Cluster(points, ClusterType.UNCLASSIFIED);
			return new Clustering(c, ClusterType.UNCLASSIFIED, kNumber);
		} catch (Exception e) {
			System.err.println("Import of unclassified data failed");
			e.printStackTrace();
		}
		return null;
	}

	public static Clustering loadUnclassified(int fileNumber) {
		List<Point2D> points = new ArrayList<Point2D>();
		int kNumber = -1;
		Path file = (Path) Paths.get("./../TestData/" + files[fileNumber]);
		try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
			String content = reader.readLine();
			kNumber = Integer.parseInt(content.substring(3));
			content = reader.readLine();
			while (content != null) {
				String[] coordinates = content.split(",");
				points.add(new Point2D(Double.parseDouble(coordinates[0]), Double.parseDouble(coordinates[1])));
				content = reader.readLine();
			}
			reader.close();
			Cluster c = new Cluster(points, ClusterType.UNCLASSIFIED);
			return new Clustering(c, ClusterType.UNCLASSIFIED, kNumber);
		} catch (Exception e) {
			System.err.println("Import of unclassified data failed");
			e.printStackTrace();
		}
		return null;

	}

	public static Clustering loadInitial(int fileNumber) {
		List<List<Point2D>> allPoints = new ArrayList<List<Point2D>>();
		Path file = (Path) Paths.get("./../RawData/initial" + fileNumber + ".txt");
		try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
			String content = reader.readLine();
			while (content != null) {
				switch (content.substring(0, Math.min(1, content.length()))) {
				case "": // empty line
					break;
				case "#": // comments
					break;
				case "D":
					allPoints.add(new ArrayList<Point2D>());
					break;
				default:
					String[] coordinates = content.split(",");
					allPoints.get(allPoints.size() - 1)
							.add(new Point2D(Double.parseDouble(coordinates[0]), Double.parseDouble(coordinates[1])));
					break;
				}
				content = reader.readLine();
			}
			reader.close();
			List<Cluster> clusters = new ArrayList<Cluster>();
			for (List<Point2D> l : allPoints) {
				clusters.add(new Cluster(l, ClusterType.INITIAL));
			}
			return new Clustering(clusters, ClusterType.INITIAL);
		} catch (Exception e) {
			System.err.println("Import of initial distributions failed");
			e.printStackTrace();
		}
		return null;
	}

	public static Clustering loadClustering(int fileNumber) {
		List<List<Point2D>> allPoints = new ArrayList<List<Point2D>>();
		Path file = (Path) Paths.get("./../ClusteredData/clustering" + fileNumber + ".txt");
		try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
			String content = reader.readLine();
			while (content != null) {
				switch (content.substring(0, Math.min(1, content.length()))) {
				case "": // empty line
					break;
				case "#": // comments
					break;
				case "C":
					allPoints.add(new ArrayList<Point2D>());
					break;
				default:
					String[] coordinates = content.split(",");
					allPoints.get(allPoints.size() - 1)
							.add(new Point2D(Double.parseDouble(coordinates[0]), Double.parseDouble(coordinates[1])));
					break;
				}
				content = reader.readLine();
			}
			reader.close();
			List<Cluster> clusters = new ArrayList<Cluster>();
			for (List<Point2D> l : allPoints) {
				clusters.add(new Cluster(l, ClusterType.CLUSTERED));
			}
			return new Clustering(clusters, ClusterType.CLUSTERED);
		} catch (Exception e) {
			System.err.println("Import of clustering failed");
			e.printStackTrace();
		}
		return null;
	}

	public static List<Clustering> loadTestData(String fileName) {
		List<Clustering> testData = new ArrayList<Clustering>();
		for (int i = 1; i < 101; i++) {
			testData.add(loadUnclassified(fileName + i));
		}
		return testData;
	}
}
