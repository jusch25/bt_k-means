package controller;

import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.layout.BorderPane;
import model.Model;
import view.View;

/**
 * Controller to transport messages between the view and the model
 */
public class Controller {

	private Model model;
	private ScatterChart<Number, Number> coordinates;
	private View view;
	private NumberAxis xAxis;
	private NumberAxis yAxis;

	public Controller(Model model) {
		this.model = model;
		view = new View(this);
		xAxis = new NumberAxis();
		xAxis.setLabel("X");
		yAxis = new NumberAxis();
		yAxis.setLabel("Y");
		coordinates = new ScatterChart<Number, Number>(xAxis, yAxis);
	}

	public BorderPane getPane() {
		view.getPane().setCenter(coordinates);
		return view.getPane();
	}

	public void initData(int pointNumber, int distNumber) {
		coordinates.getData().clear();
		coordinates.getData().addAll(model.initData(pointNumber, distNumber).getChartClustering());
	}

	public void storeClustering() {
		model.storeClustering();
	}

	public void loadClustering() {
		coordinates.getData().clear();
		coordinates.getData().addAll(model.loadClustering().getChartClustering());
	}

	public void startClustering(int kNumber, int interruption) {
		model.clusterData(kNumber, interruption);
		coordinates.getData().clear();
		coordinates.getData().addAll(model.getUnclassified().getChartClustering());
	}

	public void startClustering(int kNumber) {
		startClustering(kNumber, -1);
	}

	public boolean nextClustering() {
		coordinates.getData().clear();
		coordinates.getData().addAll(model.nextClustering().getChartClustering());
		return model.position() == 1;
	}

	public void lastClustering() {
		coordinates.getData().clear();
		coordinates.getData().addAll(model.lastClustering().getChartClustering());
	}

	public boolean previousClustering() {
		coordinates.getData().clear();
		coordinates.getData().addAll(model.previousClustering().getChartClustering());
		return model.position() == -1;
	}

	public void firstClustering() {
		coordinates.getData().clear();
		coordinates.getData().addAll(model.firstClustering().getChartClustering());
	}

	public int findK(String heuristic1, String heuristic2, int k) {
		coordinates.getData().clear();
		coordinates.getData().addAll(model.findK(heuristic1, heuristic2, k).getChartClustering());
		return getK();
	}

	public boolean incrementK(String heuristic, int interruption) {
		coordinates.getData().clear();
		coordinates.getData().addAll(model.incrementK(heuristic, interruption).getChartClustering());
		return model.kValue() == 1;
	}

	public boolean incrementK(String heuristic) {
		return incrementK(heuristic, -1);
	}

	public boolean decrementK(String heuristic, int interruption) {
		coordinates.getData().clear();
		coordinates.getData().addAll(model.decrementK(heuristic, interruption).getChartClustering());
		return model.kValue() == -1;
	}

	public boolean decrementK(String heuristic) {
		return decrementK(heuristic, -1);
	}

	public int getK() {
		return model.getK();
	}

	public String getInterruptionText() {
		return model.getInterruptionText();
	}

	public double getCurrentSilhouette() {
		return model.getCurrentSilhouette();
	}

	public int getHistorySize() {
		return model.getHistorySize();
	}

	public int getCounter() {
		return model.getCounter();
	}
}
