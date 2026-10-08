package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import model.user.AccessLevel;

public class EmployeeView extends StackPane {

	private final ComboBox<AccessLevel> typeBox = new ComboBox<>();
	private final TextField usernameField = new TextField();
	private final PasswordField passwordField = new PasswordField();
	private final TextField nameField = new TextField();
	private final DatePicker dobPicker = new DatePicker();
	private final TextField phoneField = new TextField();
	private final TextField emailField = new TextField();

	private final Button saveBtn = new Button("Save Employee");
	private final Button backBtn = new Button("Cancel");
	private final Label msgLabel = new Label();

	public EmployeeView() {
		this.setStyle("-fx-background-color: #1a334b;");

		VBox layoutWrapper = new VBox();
		layoutWrapper.setAlignment(Pos.TOP_CENTER);
		layoutWrapper.setPadding(new Insets(50, 20, 40, 20));

		VBox card = new VBox(15);
		card.setAlignment(Pos.TOP_CENTER);
		card.setPadding(new Insets(30, 35, 30, 35));

		card.setMaxWidth(420);

		card.setStyle("-fx-background-color: white; " + "-fx-background-radius: 30; "
				+ "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.06), 15, 0, 0, 10);");

		Label title = new Label("Add Employee");
		title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #1a334b;");

		GridPane formGrid = new GridPane();
		formGrid.setHgap(10);
		formGrid.setVgap(12);
		formGrid.setAlignment(Pos.CENTER);

		setupComboBox();

		styleInput(nameField);
		styleInput(dobPicker);
		styleInput(phoneField);
		styleInput(emailField);
		styleInput(typeBox);
		styleInput(usernameField);
		styleInput(passwordField);

		int r = 0;
		addSectionHeader(formGrid, "Personal Information", r++);
		addFormRow(formGrid, "Name:", nameField, r++);
		addFormRow(formGrid, "Date Of Birth:", dobPicker, r++);

		addSectionHeader(formGrid, "Contact Details", r++);
		addFormRow(formGrid, "Phone:", phoneField, r++);
		addFormRow(formGrid, "Email:", emailField, r++);

		addSectionHeader(formGrid, "System Access", r++);
		addFormRow(formGrid, "Role:", typeBox, r++);
		addFormRow(formGrid, "Username:", usernameField, r++);
		addFormRow(formGrid, "Password:", passwordField, r++);

		stylePrimaryButton(saveBtn);
		styleSecondaryButton(backBtn);

		HBox buttonBox = new HBox(10, backBtn, saveBtn);
		buttonBox.setAlignment(Pos.CENTER_RIGHT);
		buttonBox.setPadding(new Insets(10, 0, 0, 0));

		msgLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-size: 12px;");

		card.getChildren().addAll(title, formGrid, buttonBox, msgLabel);
		layoutWrapper.getChildren().add(card);

		this.getChildren().add(layoutWrapper);
	}

	private void addSectionHeader(GridPane grid, String text, int row) {
		Label header = new Label(text);
		header.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #b2bec3; -fx-padding: 5 0 0 0;");
		grid.add(header, 0, row, 2, 1);
	}

	private void addFormRow(GridPane grid, String labelText, Control field, int row) {
		Label label = new Label(labelText);
		label.setStyle("-fx-font-weight: bold; -fx-text-fill: #1a334b; -fx-font-size: 13px;");
		grid.add(label, 0, row);
		grid.add(field, 1, row);
	}

	private void setupComboBox() {
		typeBox.getItems().addAll(AccessLevel.CASHIER, AccessLevel.MANAGER);
		typeBox.setPromptText("Select Role");
		typeBox.setButtonCell(new ListCell<AccessLevel>() {
			@Override
			protected void updateItem(AccessLevel item, boolean empty) {
				super.updateItem(item, empty);
				if (empty || item == null)
					setText("Select Role");
				else {
					setText(item.toString());
					setStyle("-fx-text-fill: #1a334b; -fx-font-size: 13px;");
				}
			}
		});

		typeBox.setCellFactory(lv -> new ListCell<AccessLevel>() {
			@Override
			protected void updateItem(AccessLevel item, boolean empty) {
				super.updateItem(item, empty);
				if (empty || item == null)
					setText(null);
				else {
					setText(item.toString());
					setStyle("-fx-text-fill: #1a334b; -fx-font-size: 13px; -fx-padding: 5;");
				}
			}
		});
	}

	private void styleInput(Control c) {
		String baseStyle = "-fx-background-color: white; " + "-fx-border-color: #dfe6e9; " + "-fx-border-radius: 15; "
				+ "-fx-background-radius: 15; " + "-fx-padding: 6 12; " + "-fx-font-size: 13px;";

		c.setStyle(baseStyle);
		c.setPrefWidth(220);

		c.focusedProperty().addListener((obs, oldVal, newVal) -> {
			if (newVal) {
				c.setStyle(baseStyle + "-fx-border-color: #3498db; -fx-border-width: 1;");
			} else {
				c.setStyle(baseStyle);
			}
		});
	}

	private void stylePrimaryButton(Button b) {
		b.setStyle(
				"-fx-background-color: #27ae60; -fx-text-fill: white; " + "-fx-font-weight: bold; -fx-padding: 8 18; "
						+ "-fx-background-radius: 12; -fx-cursor: hand; -fx-font-size: 13px;");
	}

	private void styleSecondaryButton(Button b) {
		b.setStyle("-fx-background-color: transparent; -fx-text-fill: #636e72; "
				+ "-fx-font-weight: bold; -fx-padding: 8 15; "
				+ "-fx-border-color: #dfe6e9; -fx-border-radius: 12; -fx-cursor: hand; -fx-font-size: 13px;");
	}

	public ComboBox<AccessLevel> getTypeBox() {
		return typeBox;
	}

	public TextField getUsernameField() {
		return usernameField;
	}

	public PasswordField getPasswordField() {
		return passwordField;
	}

	public TextField getNameField() {
		return nameField;
	}

	public DatePicker getDobPicker() {
		return dobPicker;
	}

	public TextField getPhoneField() {
		return phoneField;
	}

	public TextField getEmailField() {
		return emailField;
	}

	public Button getSaveBtn() {
		return saveBtn;
	}

	public Button getBackBtn() {
		return backBtn;
	}

	public void setMessage(String msg) {
		msgLabel.setText(msg);
	}
}