package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import model.user.Employee;

public class ManagerDashboard {

	private final Runnable onLogout;
	private final Employee manager;

	private Node spView;
	private StackPane rootStack;
	private StackPane centerArea;

	public ManagerDashboard(Runnable onLogout, Employee manager) {
		this.onLogout = onLogout;
		this.manager = manager;

		this.spView = new SalesReportView().getView();
		buildUI();
	}

	public Parent getRoot() {
		return rootStack;
	}

	private void buildUI() {
		BorderPane layout = new BorderPane();

		centerArea = new StackPane();
		centerArea.setStyle("-fx-padding: 20;");
		centerArea.getChildren().setAll(new HomeView(manager.getName()).getView());

		layout.setLeft(createSideMenu());
		layout.setTop(createTopBar());
		layout.setCenter(centerArea);

		rootStack = new StackPane(layout);
	}

	private VBox createSideMenu() {
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

		Button homeBtn = createMenuButton("Home");
		homeBtn.setOnAction(e -> centerArea.getChildren().setAll(new HomeView(manager.getName()).getView()));

		Button inventoryBtn = createMenuButton("Inventory");
		Button staffBtn = createMenuButton("Staff");
		Button performanceBtn = createMenuButton("Performance");
		Button reportsBtn = createMenuButton("Sales Reports");

		inventoryBtn.setOnAction(e -> centerArea.getChildren().setAll(new InventoryView().getView()));
		staffBtn.setOnAction(e -> {
			Stage currentStage = (Stage) rootStack.getScene().getWindow();
			Scene currentScene = rootStack.getScene();
			StaffView staffView = new StaffView(currentStage, currentScene);
			centerArea.getChildren().setAll(staffView.getView());
		});

		performanceBtn.setOnAction(e -> centerArea.getChildren().setAll(new CashierPerformanceView().getView()));
		reportsBtn.setOnAction(e -> centerArea.getChildren().setAll(spView));

		Region spacer = new Region();
		VBox.setVgrow(spacer, Priority.ALWAYS);

		Button logoutBtn = createMenuButton("Log Out");
		logoutBtn.setStyle(logoutBtn.getStyle() + "-fx-text-fill: #ff7675;");
		logoutBtn.setOnAction(e -> onLogout.run());

		menu.getChildren().addAll(logoBox, separator, homeBtn, inventoryBtn, staffBtn, performanceBtn, reportsBtn,
				spacer, logoutBtn);
		return menu;
	}

	private Button createMenuButton(String text) {
		Button b = new Button(text);
		b.setMaxWidth(Double.MAX_VALUE);
		b.setStyle("""
				    -fx-background-color: #2C4D6E;
				    -fx-text-fill: white;
				    -fx-font-size: 13px;
				    -fx-font-weight: bold;
				    -fx-padding: 10;
				    -fx-background-radius: 4;
				""");
		b.setOnMouseEntered(e -> b.setStyle(b.getStyle() + "-fx-background-color: #355F87; -fx-cursor: hand;"));
		b.setOnMouseExited(e -> b.setStyle(b.getStyle() + "-fx-background-color: #2C4D6E;"));
		return b;
	}

	private HBox createTopBar() {
		Label name = new Label(manager.getName());
		name.setStyle("-fx-font-weight: bold; -fx-text-fill: #1a334b; -fx-font-size: 13px;");

		Label role = new Label(manager.getRole().name());
		role.setStyle("-fx-font-size: 10px; -fx-text-fill: #576574; -fx-font-weight: bold;");

		VBox userInfo = new VBox(-1, name, role);
		userInfo.setAlignment(Pos.CENTER);

		HBox profileWrapper = new HBox(userInfo);
		profileWrapper.setAlignment(Pos.CENTER);
		profileWrapper.setPadding(new Insets(8, 25, 8, 25));
		profileWrapper.setStyle("""
				    -fx-background-color: white;
				    -fx-background-radius: 40;
				    -fx-border-color: #dcdde1;
				    -fx-border-radius: 40;
				    -fx-border-width: 1;
				""");

		HBox bar = new HBox(profileWrapper);
		bar.setAlignment(Pos.CENTER_RIGHT);
		bar.setPadding(new Insets(12, 30, 12, 30));
		bar.setStyle("-fx-background-color: #1a334b;");
		bar.setPrefWidth(Double.MAX_VALUE);

		return bar;
	}
}