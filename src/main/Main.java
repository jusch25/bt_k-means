package main;

import controller.Controller;
import evaluation.EvaluationCenter;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.Model;

/**
 * The main class of the JavaFX application initializes the GUI (via a
 * controller) and the model or the evaluation environment, depending on the
 * selected mode. Set the mode flag to false to run evaluations, default setting
 * is true.
 */
public class Main extends Application {

	// flag for the operation mode (visual or evaluation)
	public static final boolean visualMode = true;

	@Override
	public void start(Stage primaryStage) {
		Model model = new Model();
		Controller controller = new Controller(model);

		Scene clustering = new Scene(controller.getPane(), 900, 700);

		primaryStage.setTitle("k-means");
		primaryStage.setScene(clustering);
		primaryStage.setMaximized(true);
		primaryStage.show();
	}

	/**
	 * Launches either the JavaFX application or the main evaluation class. To
	 * run tests, uncomment the corresponding line(s). See class Evaluation
	 * Center for more information.
	 */
	public static void main(String[] args) {
		if (visualMode) {
			launch(args);
		} else {

			/************** Tests **************/
			// PointGenerator gen = new PointGenerator();
			// KMean kMean = new KMean();
			// for (int i = 5; i < 14; i++) {
			// int ii = 100 * (int) (Math.pow(2, i));
			// for (int j = 2; j < 8; j++) {
			// long avg = 0;
			// for (int k = 0; k < 10; k++) {
			// Clustering unclassified = gen.generate(ii, j, 1.5);
			// Clustering classified = kMean.cluster(j - 1, unclassified);
			// long start = System.nanoTime();
			// pcaCentroids(classified.getCluster(0));
			// avg += System.nanoTime() - start;
			// }
			// System.out.println(ii + "," + j + ": " + (avg) * 1.0 / 10000);
			// }
			// System.out.println();
			// }

			EvaluationCenter evCenter = new EvaluationCenter();

			/************** Generate data sets **************/
			// evCenter.createRandomData(100000, 5);

			/************** Incrementing **************/
			// evCenter.evaluateKInc(10, 3, "200points_", true);
			// evCenter.evaluateKInc(10, 3, "1000points_", true);
			// evCenter.evaluateKInc(10, 3, "1000M_", true);
			// evCenter.evaluateKInc(10, 3, "5000points_", true);
			// evCenter.evaluateKInc(5, 3, "20000points_", false);
			// evCenter.evaluateKInc(5, 3, "100000points_", false);

			/************** Decrementing **************/
			// evCenter.evaluateKDec(10, 3, "200points_", true);
			// evCenter.evaluateKDec(10, 3, "1000points_", true);
			// evCenter.evaluateKDec(10, 3, "1000M_", true);
			// evCenter.evaluateKDec(10, 3, "5000points_", true);
			// evCenter.evaluateKDec(5, 3, "20000points_", false);
			// evCenter.evaluateKDec(5, 3, "100000points_", false);

			/************** Comparison **************/
			// evCenter.evaluateKIncDec(10, 3, "200points_", true);
			// evCenter.evaluateKIncDec(10, 3, "1000points_", true);
			// evCenter.evaluateKIncDec(10, 3, "1000M_", true);
			// evCenter.evaluateKIncDec(10, 3, "5000points_", true);
			// evCenter.evaluateKIncDec(5, 3, "20000points_", false);
			// evCenter.evaluateKIncDec(5, 3, "100000points_", false);

			/************** Extended variation of cluster number **************/
			// evCenter.evaluateKIncExt(5, 8, "200points_", true);
			// evCenter.evaluateKIncExt(5, 8, "1000points_", true);
			// evCenter.evaluateKIncExt(5, 8, "1000M_", true);
			// evCenter.evaluateKIncExt(5, 8, "5000points_", true);
			// evCenter.evaluateKDecExt(5, 8, "200points_", true);
			// evCenter.evaluateKDecExt(5, 8, "1000points_", true);
			// evCenter.evaluateKDecExt(5, 8, "1000M_", true);
			// evCenter.evaluateKDecExt(5, 8, "5000points_", true);

			/************** Higher Incrementing **************/
			// evCenter.evaluateKIncDouble(10, 3, "200points_", true);
			// evCenter.evaluateKIncDouble(10, 3, "1000points_", true);
			// evCenter.evaluateKIncDouble(10, 3, "1000M_", true);
			// evCenter.evaluateKIncDouble(10, 3, "5000points_", true);
			// evCenter.evaluateKIncDouble(5, 3, "20000points_", false);
			// evCenter.evaluateKIncDouble(5, 3, "100000points_", false);

			/************** Higher Decrementing **************/
			// evCenter.evaluateKDecDouble(10, 3, "200points_", true);
			// evCenter.evaluateKDecDouble(10, 3, "1000points_", true);
			// evCenter.evaluateKDecDouble(10, 3, "1000M_", true);
			// evCenter.evaluateKDecDouble(10, 3, "5000points_", true);
			// evCenter.evaluateKDecDouble(5, 3, "20000points_", false);
			// evCenter.evaluateKDecDouble(5, 3, "100000points_", false);

			/******************** Detect k ********************/
			// evCenter.evaluateFindK(8, "200points_");
			// evCenter.evaluateFindK(8, "1000points_");
			// evCenter.evaluateFindK(8, "1000M_");
			// evCenter.evaluateFindK(8, "5000points_");
			// evCenter.evaluateFindK(8, "20000points_");
			evCenter.evaluateFindK(8, "100000points_");

			System.exit(0);
		}
	}
}