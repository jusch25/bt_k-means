package filessystem;

import java.io.BufferedWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import structures.Cluster;
import structures.Clustering;
import structures.Point2D;

/**
 * This class contains operations to store clustering data to text files.
 */
public class FileExport {

	public static void storeUnclassified(Clustering clustering) {
		storeUnclassified(clustering, "unclassified");
	}

	public static void storeUnclassified(Clustering clustering, String fileName) {
		Path file = (Path) Paths.get("./../TestData/" + fileName + ".txt");
		try (BufferedWriter writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
			writer.write("# Set of unclassified points\n# Time: "
					+ LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"))
					+ "\n\n# Description of data:\n# Number of points: " + clustering.getPointNumber()
					+ "\n# Number of distributions: \n# Shares: \n# Characteristics: \n\n");
			writer.write("# Number of initial distributions\nk: " + clustering.getHiddenK() + "\n\n");
			for (Point2D p : clustering.getPoints()) {
				writer.write(p.getX() + "," + p.getY() + "\n");
			}
			writer.flush();
			writer.close();
		} catch (Exception e) {
			System.err.println("Export of unclassified data failed");
			e.printStackTrace();
		}
	}

	public static void storeDistributions(Clustering clustering) {
		Path file = (Path) Paths.get("./../RawData/initial.txt");
		try (BufferedWriter writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
			writer.write("# Initial distributions\n# Time: "
					+ LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"))
					+ "\n\n# Description of data:\n# Number of points: " + clustering.getPointNumber()
					+ "\n# Number of distributions: \n# Shares: \n# Characteristics: \n\n");
			for (Cluster c : clustering.getClusters()) {
				writer.write("Distribution: " + c.getPointNumber() + "\n");
				for (Point2D p : c.getPoints()) {
					writer.write(p.getX() + "," + p.getY() + "\n");
				}
				writer.write("\n");
			}
			writer.flush();
			writer.close();
		} catch (Exception e) {
			System.err.println("Export of initial data failed");
			e.printStackTrace();
		}
	}

	public static void storeClusteringResult(Clustering clustering, int fileNumber) {
		Path file = (Path) Paths.get("./../ClusteredData/clustering" + fileNumber + ".txt");
		try (BufferedWriter writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
			writer.write("# Clustering\n# Time: "
					+ LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyyHH:mm"))
					+ "\n# Number of points: " + clustering.getPointNumber() + "\n# Description: \n\n");
			for (Point2D p : clustering.getPoints()) {
				writer.write(p.getX() + "," + p.getY() + "\n");
			}
			writer.flush();
			writer.close();
		} catch (Exception e) {
			System.err.println("Export of clustering failed");
			e.printStackTrace();
		}
	}

}
