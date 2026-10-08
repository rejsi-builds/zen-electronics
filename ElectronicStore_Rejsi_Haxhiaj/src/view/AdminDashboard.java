package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import model.user.AccessLevel;
import model.user.Employee;

public class AdminDashboard extends BorderPane {

	private Runnable onLogout;

	private final Button homeBtn = new Button("Home");
	private final Button staffBtn = new Button("Staff");
	private final Button logoutBtn = new Button("Log Out");

	private final Button btnAddEmployee = new Button("+ Add Employee");
	private final Button btnChangeRole = new Button("Change Role");
	private final ComboBox<AccessLevel> roleBox = new ComboBox<>();
	private final Button btnApplyRole = new Button("Apply");
	private final Button btnCancelRole = new Button("Cancel");
	private final Button btnRemoveEmployee = new Button("Remove Employee");

	private final TextField searchField = new TextField();
	private final ListView<Employee> employeeListView = new ListView<>();
	private final Label statusLabel = new Label("");
	private final HBox roleEditor = new HBox(15);

	private final StackPane centerArea = new StackPane();
	private Pane homeView;
	private Pane staffView;

	private final String adminName = "Enso Lani";

	public AdminDashboard() {
		setStyle("-fx-background-color: #f3f5f7;");

		setTop(buildTopBar());
		setLeft(buildSidebar());

		homeView = new HomeView("Enso Lani").getView();
		staffView = buildStaffView();

		centerArea.setPadding(new Insets(20));
		centerArea.getChildren().setAll(homeView);
		setCenter(centerArea);
	}

	private HBox buildTopBar() {
		Label name = new Label(adminName);
		name.setStyle("-fx-font-weight: bold; -fx-text-fill: #1a334b; -fx-font-size: 13px;");
		Label role = new Label("ADMINISTRATOR");
		role.setStyle("-fx-font-size: 10px; -fx-text-fill: #576574; -fx-font-weight: bold;");

		VBox userInfo = new VBox(-1, name, role);
		userInfo.setAlignment(Pos.CENTER);

		HBox profileWrapper = new HBox(userInfo);
		profileWrapper.setAlignment(Pos.CENTER);
		profileWrapper.setPadding(new Insets(8, 25, 8, 25));
		profileWrapper.setStyle(
				"-fx-background-color: white; -fx-background-radius: 40; -fx-border-color: #dcdde1; -fx-border-radius: 40; -fx-border-width: 1;");

		HBox bar = new HBox(profileWrapper);
		bar.setAlignment(Pos.CENTER_RIGHT);
		bar.setPadding(new Insets(12, 30, 12, 30));
		bar.setStyle("-fx-background-color: #1a334b;");
		return bar;
	}

	private VBox buildSidebar() {
		VBox menu = new VBox(10);
		menu.setMinWidth(210);
		menu.setPrefWidth(210);
		menu.setMaxWidth(210);
		menu.setAlignment(Pos.TOP_CENTER);
		menu.setStyle("""
				    -fx-background-color: #1a334b;
				    -fx-padding: 25 15 25 15;
				""");

		ImageView logo = new ImageView(new Image("file:src/logo/logo.png"));
		logo.setFitHeight(90);
		logo.setPreserveRatio(true);

		Label mainTitle = new Label("ZEN Electronics");
		mainTitle.setStyle("-fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: bold;");

		Label subTitle = new Label("Electronic Store System");
		subTitle.setStyle("-fx-text-fill: white; -fx-font-size: 12px; -fx-font-style: italic;");

		VBox logoBox = new VBox(8, logo, mainTitle, subTitle);
		logoBox.setAlignment(Pos.CENTER);
		VBox.setMargin(logoBox, new Insets(0, 0, 10, 0));

		Separator separator = new Separator();
		separator.setOpacity(0.3);

		styleMenuButton(homeBtn);
		styleMenuButton(staffBtn);
		styleMenuButton(logoutBtn);

		setActiveNav(homeBtn);

		homeBtn.setOnAction(e -> {
			setActiveNav(homeBtn);
			centerArea.getChildren().setAll(homeView);
		});

		staffBtn.setOnAction(e -> {
			setActiveNav(staffBtn);
			centerArea.getChildren().setAll(staffView);
		});

		logoutBtn.setStyle(logoutBtn.getStyle() + "-fx-text-fill: #ff7675;");
		logoutBtn.setOnAction(e -> {
			if (onLogout != null)
				onLogout.run();
		});

		Region spacer = new Region();
		VBox.setVgrow(spacer, Priority.ALWAYS);

		menu.getChildren().addAll(logoBox, separator, homeBtn, staffBtn, spacer, logoutBtn);

		return menu;
	}

	private Pane buildStaffView() {
		VBox layout = new VBox(20);
		layout.setPadding(new Insets(10));

		Label title = new Label("Staff Management");
		title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #1a334b;");

		styleTextField(searchField);
		searchField.setPromptText("Search by name, ID, or role...");
		searchField.setPrefWidth(300);

		stylePrimaryButton(btnChangeRole);
		styleRedButton(btnRemoveEmployee);
		stylePrimaryButton(btnAddEmployee);
		styleComboBox(roleBox);
		roleBox.getItems().setAll(AccessLevel.CASHIER, AccessLevel.MANAGER);
		roleBox.setPromptText("Select Role");
		stylePrimaryButton(btnApplyRole);
		styleRedButton(btnCancelRole);

		HBox topRow = new HBox(15, searchField, btnChangeRole, btnRemoveEmployee);
		topRow.setAlignment(Pos.CENTER_LEFT);

		roleEditor.setStyle(
				"-fx-background-color: #f8f9fa; -fx-padding: 12; -fx-background-radius: 15; -fx-border-color: #ddd; -fx-border-radius: 15;");
		roleEditor.setAlignment(Pos.CENTER_LEFT);
		roleEditor.setVisible(false);
		roleEditor.setManaged(false);
		Label newRoleLabel = new Label("New Role:");
		newRoleLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #1a334b;");

		roleEditor.getChildren().setAll(newRoleLabel, roleBox, btnApplyRole, btnCancelRole);

		employeeListView.setStyle(
				"-fx-background-color: transparent; -fx-control-inner-background: transparent; -fx-background-insets: 0;");
		VBox.setVgrow(employeeListView, Priority.ALWAYS);

		HBox footer = new HBox(statusLabel, new Region(), btnAddEmployee);
		HBox.setHgrow(footer.getChildren().get(1), Priority.ALWAYS);
		footer.setAlignment(Pos.CENTER_LEFT);

		layout.getChildren().addAll(title, topRow, roleEditor, employeeListView, footer);
		return layout;
	}

	private void stylePrimaryButton(Button b) {
		b.setStyle(
				"-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 22; -fx-background-radius: 5; -fx-cursor: hand;");
		b.setMinWidth(Region.USE_PREF_SIZE);
	}

	private void styleRedButton(Button b) {
		b.setStyle(
				"-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 22; -fx-background-radius: 5; -fx-cursor: hand;");
		b.setMinWidth(Region.USE_PREF_SIZE);
	}

	private void styleTextField(TextField tf) {
		tf.setStyle(
				"-fx-background-radius: 15; -fx-padding: 6 12; -fx-border-color: #ddd; -fx-border-radius: 15; -fx-background-color: white;");
	}

	private void styleComboBox(ComboBox<?> cb) {
		cb.setStyle(
				"-fx-background-radius: 15; -fx-border-color: #ddd; -fx-border-radius: 15; -fx-background-color: white;");
	}

	private void styleMenuButton(Button b) {
		b.setMaxWidth(Double.MAX_VALUE);
		b.setStyle(
				"-fx-background-color: #2C4D6E; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10; -fx-background-radius: 4; -fx-cursor: hand;");
	}

	private void setActiveNav(Button active) {
		homeBtn.setStyle(
				homeBtn.getStyle().replace("-fx-background-color: #355F87;", "-fx-background-color: #2C4D6E;"));
		staffBtn.setStyle(
				staffBtn.getStyle().replace("-fx-background-color: #355F87;", "-fx-background-color: #2C4D6E;"));
		active.setStyle(active.getStyle().replace("-fx-background-color: #2C4D6E;", "-fx-background-color: #355F87;"));
	}

	public void setOnLogout(Runnable onLogout) {
		this.onLogout = onLogout;
	}

	public Button getBtnAddEmployee() {
		return btnAddEmployee;
	}

	public Button getBtnRemoveEmployee() {
		return btnRemoveEmployee;
	}

	public Button getLogoutBtn() {
		return logoutBtn;
	}

	public Button getBtnChangeRole() {
		return btnChangeRole;
	}

	public ComboBox<AccessLevel> getRoleBox() {
		return roleBox;
	}

	public Button getBtnApplyRole() {
		return btnApplyRole;
	}

	public Button getBtnCancelRole() {
		return btnCancelRole;
	}

	public TextField getSearchField() {
		return searchField;
	}

	public ListView<Employee> getEmployeeListView() {
		return employeeListView;
	}

	public void setStatus(String msg) {
		statusLabel.setText(msg);
	}

	public void showRoleEditor(boolean show) {
		roleEditor.setVisible(show);
		roleEditor.setManaged(show);
	}
}