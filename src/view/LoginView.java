package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

public class LoginView extends GridPane {

	private final Button loginBtn = new Button("Log In");
	private final TextField userField = new TextField();
	private final PasswordField passField = new PasswordField();
	private final Label messageLabel = new Label();

	private static final double FIELD_WIDTH = 220;

	public LoginView() {

		this.setAlignment(Pos.CENTER);
		this.setHgap(15);
		this.setVgap(15);
		this.setPadding(new Insets(25));
		this.setStyle("-fx-background-color: #1a334b;");

		ImageView logo = new ImageView(new Image("file:src/logo/logo.png"));
		logo.setFitHeight(115);
		logo.setPreserveRatio(true);

		Label welcomeLabel = new Label("Welcome to ZEN!");
		welcomeLabel.setStyle("""
				    -fx-text-fill: white;
				    -fx-font-size: 24px;
				    -fx-font-weight: bold;
				""");

		VBox header = new VBox(2, logo, welcomeLabel);
		header.setAlignment(Pos.CENTER);
		VBox.setMargin(header, new Insets(0, 0, 10, 0));

		Label userLabel = new Label("Username:");
		userLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");

		Label passLabel = new Label("Password:");
		passLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");

		fixFieldSize(userField);
		fixFieldSize(passField);
		userField.setStyle("-fx-background-radius: 4;");
		passField.setStyle("-fx-background-radius: 4;");

		loginBtn.setStyle("""
				    -fx-background-color: #2C4D6E;
				    -fx-text-fill: white;
				    -fx-font-size: 14px;
				    -fx-font-weight: bold;
				    -fx-padding: 8 22;
				    -fx-background-radius: 4;
				    -fx-cursor: hand;
				""");

		loginBtn.setOnMouseEntered(e -> loginBtn.setStyle("""
				    -fx-background-color: #355F87;
				    -fx-text-fill: white;
				    -fx-font-size: 14px;
				    -fx-font-weight: bold;
				    -fx-padding: 8 22;
				    -fx-background-radius: 4;
				    -fx-cursor: hand;
				"""));
		loginBtn.setOnMouseExited(e -> loginBtn.setStyle("""
				    -fx-background-color: #2C4D6E;
				    -fx-text-fill: white;
				    -fx-font-size: 14px;
				    -fx-font-weight: bold;
				    -fx-padding: 8 22;
				    -fx-background-radius: 4;
				    -fx-cursor: hand;
				"""));

		messageLabel.setMinHeight(20);
		messageLabel.setVisible(false);
		messageLabel.setAlignment(Pos.CENTER_LEFT);

		VBox loginActionBox = new VBox(10, loginBtn, messageLabel);
		loginActionBox.setAlignment(Pos.CENTER_LEFT);

		this.add(header, 0, 0, 2, 1);
		this.add(userLabel, 0, 1);
		this.add(userField, 1, 1);
		this.add(passLabel, 0, 2);
		this.add(passField, 1, 2);

		this.add(loginActionBox, 1, 3);
	}

	private void fixFieldSize(TextField field) {
		field.setPrefWidth(FIELD_WIDTH);
		field.setMinWidth(FIELD_WIDTH);
		field.setMaxWidth(FIELD_WIDTH);
	}

	public void showError(String msg) {
		messageLabel.setText(msg);
		messageLabel.setStyle("-fx-text-fill: #ff7675; -fx-font-size: 14px; -fx-font-weight: bold;");
		messageLabel.setVisible(true);
	}

	public void showInfo(String msg) {
		messageLabel.setText(msg);
		messageLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
		messageLabel.setVisible(true);
	}

	public Button getLoginBtn() {
		return loginBtn;
	}

	public TextField getUserField() {
		return userField;
	}

	public PasswordField getPassField() {
		return passField;
	}
}