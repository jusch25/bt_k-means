package view;

import java.util.ArrayList;
import java.util.List;

import controller.Controller;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import other.DecrementKTests;
import other.IncrementKTests;
import tools.NumericTextField;

/**
 * Main class for all the graphical components of the javafx application.
 */
public class View {

	private Controller controller;
	private BorderPane pane;
	private Button generatePointsButton;
	private Button startClusteringButton;
	private Button nextClusteringButton;
	private Button previousClusteringButton;
	private Button lastClusteringButton;
	private Button firstClusteringButton;
	private Button incrementKButton;
	private Button decrementKButton;
	private Button findK;
	private Button loadClustering;
	private NumericTextField pointNumberTextField;
	private NumericTextField kNumberTextField;
	private NumericTextField distNumberTextField;
	private NumericTextField interruptField;
	private Label pointsLabel;
	private Label clusteringLabel;
	private Label kModifierLabel;
	private Label interruptLabel;
	private Label otherLabel;
	private Label infoLabel;
	private Label kIndicatorLabel;
	private Label iterationIndicatorLabel;
	private Label silhouetteLabel;
	private Label pictureNumberLabel;
	private CheckBox checkBox;
	private ChoiceBox<String> decHeuristics;
	private ChoiceBox<String> incHeuristics;
	private VBox layer1;
	private HBox layer11;
	private VBox layer12;
	private VBox layer111;
	private VBox layer112;
	private HBox layer121;
	private HBox layer122;
	private VBox layer1111;
	private VBox layer1112;
	private VBox layer1121;
	private VBox layer1122;
	private VBox layer1123;
	private HBox layer11121;
	private HBox layer11122;

	public View(Controller controller) {
		this.controller = controller;
		pane = new BorderPane();
		initGUI();
	}

	private void initGUI() {
		initButtons();
		initRest();

		String borderStyle = "-fx-padding: 10;-fx-border-style: solid inside;-fx-border-width: 2;-fx-border-insets: 5;-fx-border-radius: 5;-fx-border-color: black;";

		layer11121 = new HBox(previousClusteringButton, nextClusteringButton);
		layer11121.setSpacing(20);
		layer11122 = new HBox(firstClusteringButton, lastClusteringButton);
		layer11122.setSpacing(20);

		layer1111 = new VBox(pointsLabel, pointNumberTextField, distNumberTextField, generatePointsButton);
		layer1111.setSpacing(20);
		layer1111.setStyle(borderStyle);
		layer1112 = new VBox(clusteringLabel, kNumberTextField, startClusteringButton, layer11121, layer11122);
		layer1112.setSpacing(20);
		layer1112.setStyle(borderStyle);
		layer1121 = new VBox(infoLabel, iterationIndicatorLabel, pictureNumberLabel, silhouetteLabel);
		layer1121.setSpacing(20);
		layer1121.setStyle(borderStyle);
		layer1122 = new VBox(interruptLabel, interruptField, checkBox);
		layer1122.setSpacing(20);
		layer1122.setStyle(borderStyle);
		layer1123 = new VBox(otherLabel, findK, loadClustering);
		layer1123.setSpacing(20);
		layer1123.setStyle(borderStyle);

		layer111 = new VBox(layer1111, layer1112);
		layer111.setAlignment(Pos.TOP_CENTER);
		layer111.setSpacing(20);
		layer112 = new VBox(layer1121, layer1122, layer1123);
		layer112.setAlignment(Pos.TOP_CENTER);
		layer112.setSpacing(20);
		layer121 = new HBox(incHeuristics, incrementKButton);
		layer121.setSpacing(20);
		layer122 = new HBox(decHeuristics, decrementKButton);
		layer122.setSpacing(20);

		layer11 = new HBox(layer111, layer112);
		layer11.setSpacing(30);
		layer12 = new VBox(kModifierLabel, kIndicatorLabel, layer121, layer122);
		layer12.setAlignment(Pos.TOP_CENTER);
		layer12.setSpacing(20);
		layer12.setStyle(borderStyle);

		layer1 = new VBox(layer11, layer12);
		layer1.setSpacing(20);
		layer1.setPadding(new Insets(20, 20, 20, 20));
		pane.setRight(layer1);
	}

	private void initButtons() {

		generatePointsButton = new Button();
		generatePointsButton.setPrefSize(160, 40);
		generatePointsButton.setText("Generate Points");
		generatePointsButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if (!pointNumberTextField.getText().equals("") && !distNumberTextField.getText().equals("")) {
					controller.initData(Integer.parseInt(pointNumberTextField.getText()),
							Integer.parseInt(distNumberTextField.getText()));
					kNumberTextField.setDisable(false);
					startClusteringButton.setDisable(false);
					nextClusteringButton.setDisable(true);
					lastClusteringButton.setDisable(true);
					previousClusteringButton.setDisable(true);
					firstClusteringButton.setDisable(true);
					incrementKButton.setDisable(true);
					decrementKButton.setDisable(true);
					findK.setDisable(false);
					kIndicatorLabel.setText("No clustering");
					iterationIndicatorLabel.setText("No clustering");
					pictureNumberLabel.setText("No Clustering");
					silhouetteLabel.setText("No Clustering");
				}
			}
		});

		startClusteringButton = new Button();
		startClusteringButton.setPrefSize(160, 40);
		startClusteringButton.setText("Start Clustering");
		startClusteringButton.setDisable(true);
		startClusteringButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if (!kNumberTextField.getText().equals("")) {
					if (checkBox.isSelected() && !interruptField.getText().equals("")
							&& Integer.parseInt(interruptField.getText()) != 0) {
						controller.startClustering(Integer.parseInt(kNumberTextField.getText()),
								Integer.parseInt(interruptField.getText()));
					} else {
						controller.startClustering(Integer.parseInt(kNumberTextField.getText()));
					}
					iterationIndicatorLabel.setText(controller.getInterruptionText());
					nextClusteringButton.setDisable(false);
					lastClusteringButton.setDisable(false);
					previousClusteringButton.setDisable(false);
					firstClusteringButton.setDisable(false);
					incrementKButton.setDisable(true);
					decrementKButton.setDisable(true);
					findK.setDisable(false);
					kIndicatorLabel.setText("k = " + controller.getK());
					pictureNumberLabel
							.setText("Chart # " + controller.getCounter() + " of " + controller.getHistorySize());
					silhouetteLabel.setText("Silhouette: " + controller.getCurrentSilhouette());
				}
			}
		});

		nextClusteringButton = new Button();
		nextClusteringButton.setPrefSize(70, 40);
		nextClusteringButton.setText(">");
		nextClusteringButton.setDisable(true);
		nextClusteringButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if (controller.nextClustering()) {
					nextClusteringButton.setDisable(true);
					lastClusteringButton.setDisable(true);
					incrementKButton.setDisable(false);
					if (controller.getK() != 1) {
						decrementKButton.setDisable(false);
					}
				}
				previousClusteringButton.setDisable(false);
				firstClusteringButton.setDisable(false);
				pictureNumberLabel.setText("Chart # " + controller.getCounter() + " of " + controller.getHistorySize());
				silhouetteLabel.setText("Silhouette: " + controller.getCurrentSilhouette());
			}
		});

		previousClusteringButton = new Button();
		previousClusteringButton.setPrefSize(70, 40);
		previousClusteringButton.setText("<");
		previousClusteringButton.setDisable(true);
		previousClusteringButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if (controller.previousClustering()) {
					previousClusteringButton.setDisable(true);
					firstClusteringButton.setDisable(true);
				}
				nextClusteringButton.setDisable(false);
				lastClusteringButton.setDisable(false);
				pictureNumberLabel.setText("Chart # " + controller.getCounter() + " of " + controller.getHistorySize());
				silhouetteLabel.setText("Silhouette: " + controller.getCurrentSilhouette());
			}
		});

		lastClusteringButton = new Button();
		lastClusteringButton.setPrefSize(70, 40);
		lastClusteringButton.setText(">>");
		lastClusteringButton.setDisable(true);
		lastClusteringButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				controller.lastClustering();
				nextClusteringButton.setDisable(true);
				lastClusteringButton.setDisable(true);
				incrementKButton.setDisable(false);
				if (controller.getK() != 1) {
					decrementKButton.setDisable(false);
				}
				previousClusteringButton.setDisable(false);
				firstClusteringButton.setDisable(false);
				pictureNumberLabel.setText("Chart # " + controller.getCounter() + " of " + controller.getHistorySize());
				silhouetteLabel.setText("Silhouette: " + controller.getCurrentSilhouette());
			}
		});

		firstClusteringButton = new Button();
		firstClusteringButton.setPrefSize(70, 40);
		firstClusteringButton.setText("<<");
		firstClusteringButton.setDisable(true);
		firstClusteringButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				controller.firstClustering();
				previousClusteringButton.setDisable(true);
				firstClusteringButton.setDisable(true);
				nextClusteringButton.setDisable(false);
				lastClusteringButton.setDisable(false);
				pictureNumberLabel.setText("Chart # " + controller.getCounter() + " of " + controller.getHistorySize());
				silhouetteLabel.setText("Silhouette: " + controller.getCurrentSilhouette());
			}
		});

		incrementKButton = new Button();
		incrementKButton.setPrefSize(60, 30);
		incrementKButton.setText("+");
		incrementKButton.setDisable(true);
		incrementKButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if (checkBox.isSelected() && !interruptField.getText().equals("")
						&& Integer.parseInt(interruptField.getText()) != 0) {
					if (controller.incrementK(incHeuristics.getValue(), Integer.parseInt(interruptField.getText()))) {
						incrementKButton.setDisable(true);
					}
				} else if (controller.incrementK(incHeuristics.getValue())) {
					incrementKButton.setDisable(true);
				}
				iterationIndicatorLabel.setText(controller.getInterruptionText());
				decrementKButton.setDisable(false);
				previousClusteringButton.setDisable(false);
				firstClusteringButton.setDisable(false);
				nextClusteringButton.setDisable(false);
				lastClusteringButton.setDisable(false);
				kIndicatorLabel.setText("k = " + controller.getK());
				pictureNumberLabel.setText("Chart # " + controller.getCounter() + " of " + controller.getHistorySize());
				silhouetteLabel.setText("Silhouette: " + controller.getCurrentSilhouette());
			}
		});

		decrementKButton = new Button();
		decrementKButton.setPrefSize(60, 30);
		decrementKButton.setText("-");
		decrementKButton.setDisable(true);
		decrementKButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if (checkBox.isSelected() && !interruptField.getText().equals("")
						&& Integer.parseInt(interruptField.getText()) != 0) {
					if (controller.decrementK(decHeuristics.getValue(), Integer.parseInt(interruptField.getText()))) {
						decrementKButton.setDisable(true);
					}
				} else if (controller.decrementK(decHeuristics.getValue())) {
					decrementKButton.setDisable(true);
				}
				iterationIndicatorLabel.setText(controller.getInterruptionText());
				incrementKButton.setDisable(false);
				previousClusteringButton.setDisable(false);
				firstClusteringButton.setDisable(false);
				nextClusteringButton.setDisable(false);
				lastClusteringButton.setDisable(false);
				kIndicatorLabel.setText("k = " + controller.getK());
				pictureNumberLabel.setText("Chart # " + controller.getCounter() + " of " + controller.getHistorySize());
				silhouetteLabel.setText("Silhouette: " + controller.getCurrentSilhouette());
			}
		});

		findK = new Button();
		findK.setPrefSize(160, 40);
		findK.setText("Find best k");
		findK.setDisable(true);
		findK.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if (!kNumberTextField.getText().equals("")) {
					kIndicatorLabel.setText("k = " + controller.findK(incHeuristics.getValue(),
							decHeuristics.getValue(), Integer.parseInt(kNumberTextField.getText())));
					previousClusteringButton.setDisable(false);
					firstClusteringButton.setDisable(false);
					nextClusteringButton.setDisable(false);
					lastClusteringButton.setDisable(false);
					pictureNumberLabel
							.setText("Chart # " + controller.getCounter() + " of " + controller.getHistorySize());
					silhouetteLabel.setText("Silhouette: " + controller.getCurrentSilhouette());
				}
			}
		});

		loadClustering = new Button();
		loadClustering.setPrefSize(160, 40);
		loadClustering.setText("Load Clustering");
		loadClustering.setDisable(false);
		loadClustering.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				controller.loadClustering();
				kNumberTextField.setDisable(false);
				startClusteringButton.setDisable(false);
				nextClusteringButton.setDisable(true);
				lastClusteringButton.setDisable(true);
				previousClusteringButton.setDisable(true);
				firstClusteringButton.setDisable(true);
				incrementKButton.setDisable(true);
				decrementKButton.setDisable(true);
				findK.setDisable(false);
				kIndicatorLabel.setText("No clustering");
				iterationIndicatorLabel.setText("No clustering");
				pictureNumberLabel.setText("No Clustering");
				silhouetteLabel.setText("No Clustering");
			}
		});
	}

	private void initRest() {
		pointNumberTextField = new NumericTextField();
		pointNumberTextField.setPromptText("# of points");
		pointNumberTextField.setPrefSize(160, 30);
		distNumberTextField = new NumericTextField();
		distNumberTextField.setPromptText("# of distributions");
		distNumberTextField.setPrefSize(160, 30);
		kNumberTextField = new NumericTextField();
		kNumberTextField.setPromptText("# of clusters");
		kNumberTextField.setPrefSize(160, 30);
		kNumberTextField.setDisable(true);
		interruptField = new NumericTextField();
		interruptField.setPromptText("# of iterations before interruption");
		interruptField.setPrefSize(160, 30);

		pointsLabel = new Label("GENERATOR");
		pointsLabel.setPrefSize(160, 20);
		pointsLabel.setAlignment(Pos.CENTER);
		clusteringLabel = new Label("INITIAL CLUSTERING");
		clusteringLabel.setPrefSize(160, 20);
		clusteringLabel.setAlignment(Pos.CENTER);
		interruptLabel = new Label("INTERRUPTION");
		interruptLabel.setPrefSize(160, 20);
		interruptLabel.setAlignment(Pos.CENTER);
		otherLabel = new Label("OTHER");
		otherLabel.setPrefSize(160, 20);
		otherLabel.setAlignment(Pos.CENTER);
		infoLabel = new Label("INFORMATION");
		infoLabel.setPrefSize(160, 20);
		infoLabel.setAlignment(Pos.CENTER);
		kModifierLabel = new Label("INCREMENT/DECREMENT K");
		kModifierLabel.setPrefSize(200, 20);
		kModifierLabel.setAlignment(Pos.CENTER);
		kIndicatorLabel = new Label("No clustering");
		kIndicatorLabel.setPrefSize(160, 20);
		kIndicatorLabel.setAlignment(Pos.CENTER);
		iterationIndicatorLabel = new Label("No clustering");
		iterationIndicatorLabel.setPrefSize(160, 20);
		iterationIndicatorLabel.setAlignment(Pos.CENTER);
		pictureNumberLabel = new Label("No clustering");
		pictureNumberLabel.setPrefSize(160, 20);
		pictureNumberLabel.setAlignment(Pos.CENTER);
		silhouetteLabel = new Label("No clustering");
		silhouetteLabel.setPrefSize(160, 20);
		silhouetteLabel.setAlignment(Pos.CENTER);

		List<String> incTypes = new ArrayList<String>();
		for (int i = 0; i < Math.min(IncrementKTests.size - 2, 8); i++) {
			incTypes.add(IncrementKTests.values()[i].getName().substring(3));
		}
		for (int i = Math.min(IncrementKTests.size - 2, 8); i < IncrementKTests.size - 2; i++) {
			incTypes.add(IncrementKTests.values()[i].getName().substring(4));
		}
		incHeuristics = new ChoiceBox<String>(FXCollections.observableArrayList(incTypes));
		incHeuristics.setValue(IncrementKTests.values()[0].getName().substring(3));
		incHeuristics.setPrefSize(300, 30);

		List<String> decTypes = new ArrayList<String>();
		for (int i = 0; i < Math.min(DecrementKTests.size - 2, 8); i++) {
			decTypes.add(DecrementKTests.values()[i].getName().substring(3));
		}
		for (int i = Math.min(DecrementKTests.size - 2, 8); i < DecrementKTests.size - 2; i++) {
			decTypes.add(DecrementKTests.values()[i].getName().substring(4));
		}
		decHeuristics = new ChoiceBox<String>(FXCollections.observableArrayList(decTypes));
		decHeuristics.setValue(DecrementKTests.values()[0].getName().substring(3));
		decHeuristics.setPrefSize(300, 30);

		checkBox = new CheckBox("Interrupt");
	}

	public BorderPane getPane() {
		return pane;
	}
}
