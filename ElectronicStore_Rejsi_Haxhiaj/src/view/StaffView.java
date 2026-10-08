package view;

import controller.StaffController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import model.user.Employee;
import model.user.AccessLevel;
import javafx.stage.Stage;
import javafx.scene.Scene;

public class StaffView {

	private final StaffController controller;
	private final ListView<Employee> listView = new ListView<>();
	private final TextField searchField = new TextField();

	public StaffView(Stage stage, Scene currentScene) {
		this.controller = new StaffController(stage, currentScene);
	}

	public Node getView() {
		BorderPane pane = new BorderPane();
		pane.setPadding(new Insets(20));
		pane.setStyle("-fx-background-color: transparent;");

		Label title = new Label("Staff Management");
		title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #1a334b;");

		searchField.setPromptText("Search by name or ID...");
		searchField.setPrefWidth(280);
		searchField.setStyle(
				"-fx-background-radius: 15; -fx-padding: 8 12; -fx-border-color: #ddd; -fx-border-radius: 15; -fx-background-color: white;");

		controller.setupSearch(searchField);

		Button addBtn = new Button("+ Add Cashier");
		stylePrimaryButton(addBtn);
		addBtn.setOnAction(e -> controller.showAddCashierForm());

		HBox topBar = new HBox(20, title, searchField, new Region(), addBtn);
		HBox.setHgrow(topBar.getChildren().get(2), Priority.ALWAYS);
		topBar.setAlignment(Pos.CENTER_LEFT);
		topBar.setPadding(new Insets(0, 0, 20, 0));

		setupListView();

		pane.setTop(topBar);
		pane.setCenter(listView);

		return pane;
	}

	private void setupListView() {
		listView.setStyle(
				"-fx-background-color: transparent; -fx-control-inner-background: transparent; -fx-background-insets: 0;");
		listView.setItems(controller.getSortedStaff());

		listView.setCellFactory(lv -> new ListCell<>() {
			@Override
			protected void updateItem(Employee emp, boolean empty) {
				super.updateItem(emp, empty);
				if (empty || emp == null) {
					setGraphic(null);
					setStyle("-fx-background-color: transparent;");
				} else {
					HBox root = new HBox(15);
					root.setAlignment(Pos.CENTER_LEFT);
					root.setPadding(new Insets(12, 25, 12, 25));

					String pillStyle = """
								-fx-background-color: white;
								-fx-background-radius: 50;
								-fx-border-radius: 50;
								-fx-border-color: #dcdde1;
								-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 4);
							""";

					if (isSelected()) {
						root.setStyle(pillStyle
								+ "-fx-border-color: #355F87; -fx-border-width: 2; -fx-background-color: #f0f4f8;");
					} else {
						root.setStyle(pillStyle);
					}

					VBox info = new VBox(2);
					Label name = new Label(emp.getName());
					name.setStyle("-fx-text-fill: #1a334b; -fx-font-size: 15px; -fx-font-weight: bold;");

					Label workInfo = new Label("ID: " + emp.getEmployeeID() + " • Username: " + emp.getRole());
					workInfo.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 11px;");
					info.getChildren().addAll(name, workInfo);

					Region spacer = new Region();
					HBox.setHgrow(spacer, Priority.ALWAYS);

					Label badge = new Label(emp.getRole().name());
					String color = (emp.getRole() == AccessLevel.MANAGER) ? "#355F87" : "#2C4D6E";
					badge.setStyle("-fx-background-color: " + color
							+ "; -fx-text-fill: white; -fx-font-size: 10px; -fx-font-weight: bold; -fx-padding: 4 14; -fx-background-radius: 25;");

					Button btn = new Button("ⓘ");
					btn.setStyle(
							"-fx-background-color: transparent; -fx-text-fill: #355F87; -fx-font-size: 18px; -fx-cursor: hand;");
					btn.setOnAction(e -> showDetails(emp));

					root.getChildren().addAll(info, spacer, badge, btn);
					setGraphic(root);
					setStyle("-fx-background-color: transparent; -fx-padding: 8 0;");
				}
			}
		});
	}

	private void stylePrimaryButton(Button b) {
		b.setStyle(
				"-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 22; -fx-background-radius: 5; -fx-cursor: hand;");
	}

	private void showDetails(Employee e) {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("Employee Profile");
		alert.setHeaderText(null);

		VBox content = new VBox(8);
		content.setPadding(new Insets(15));

		Label personalHeader = new Label("Personal Information");
		personalHeader.setStyle("-fx-font-weight: bold; -fx-text-fill: black; -fx-font-size: 11px;");
		VBox personalData = new VBox(3, new Label("Full Name: " + e.getName()),
				new Label("Date of Birth: " + e.getDateOfBirth()));

		Label workHeader = new Label("Work Profile");
		workHeader.setStyle("-fx-font-weight: bold; -fx-text-fill: black; -fx-font-size: 11px;");
		VBox workData = new VBox(3, new Label("ID: " + e.getEmployeeID()), new Label("Role: " + e.getRole()),
				new Label("Salary: $" + String.format("%.2f", e.getSalary())));

		Label contactHeader = new Label("Contact Details");
		contactHeader.setStyle("-fx-font-weight: bold; -fx-text-fill: black; -fx-font-size: 11px;");
		VBox contactData = new VBox(3, new Label("Email: " + e.getEmail()), new Label("Phone: " + e.getPhone()));

		Separator s1 = new Separator();
		s1.setPadding(new Insets(5, 0, 5, 0));
		Separator s2 = new Separator();
		s2.setPadding(new Insets(5, 0, 5, 0));

		content.getChildren().addAll(personalHeader, personalData, s1, workHeader, workData, s2, contactHeader,
				contactData);

		alert.getDialogPane().setContent(content);
		alert.showAndWait();
	}
}
