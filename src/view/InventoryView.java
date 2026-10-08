package view;

import controller.InventoryController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import model.inventory.*;

public class InventoryView {

	private final InventoryController controller = new InventoryController();
	private final ObservableList<Item> items = controller.getItems();
	private final TableView<Item> table = new TableView<>();

	private ComboBox<String> discountMode;
	private TextField discountInput;
	private ComboBox<Sector> sectorSelect;
	private TextField searchItemField;
	private FilteredList<Item> filteredItems;
	private Button discountBtn;

	public Node getView() {
		BorderPane pane = new BorderPane();
		pane.setPadding(new Insets(20));
		pane.setStyle("-fx-background-color: transparent;");

		Label title = new Label("Inventory Overview");
		title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #1a334b;");

		setupTable();
		pane.setTop(createActionsSection(title));
		pane.setCenter(table);
		pane.setBottom(createAddItemSection());

		return pane;
	}

	private void setupTable() {
		table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		table.setStyle("-fx-background-radius: 8; -fx-border-radius: 8;");

		TableColumn<Item, String> idCol = new TableColumn<>("ID");
		idCol.setCellValueFactory(new PropertyValueFactory<>("itemID"));
		idCol.setPrefWidth(40);

		TableColumn<Item, Sector> sectorCol = new TableColumn<>("Sector");
		sectorCol.setCellValueFactory(new PropertyValueFactory<>("sector"));
		sectorCol.setPrefWidth(80);

		TableColumn<Item, String> nameCol = new TableColumn<>("Item");
		nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
		nameCol.setPrefWidth(150);

		TableColumn<Item, Double> priceCol = new TableColumn<>("Price(€)");
		priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
		priceCol.setPrefWidth(40);

		TableColumn<Item, Integer> stockCol = new TableColumn<>("Stock");
		stockCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
		stockCol.setPrefWidth(40);

		TableColumn<Item, Void> supplierCol = new TableColumn<>("Supplier");
		supplierCol.setCellFactory(col -> new TableCell<>() {
			private final Button infoBtn = new Button("ⓘ");
			{
				infoBtn.setStyle(
						"-fx-background-color: transparent; -fx-text-fill: #2C4D6E; -fx-font-size: 18px; -fx-cursor: hand;");
				infoBtn.setOnAction(e -> {
					Item i = getTableView().getItems().get(getIndex());
					Supplier s = i.getSupplier();
					if (s != null) {
						Alert alert = new Alert(Alert.AlertType.INFORMATION);
						alert.setTitle("Supplier Info");
						alert.setHeaderText(i.getName() + " - Supplier Details");
						alert.setContentText("Name: " + s.getName() + "\nContact: " + s.getContactInfo());
						alert.show();
					} else {
						new Alert(Alert.AlertType.WARNING, "No supplier assigned!").show();
					}
				});
			}

			@Override
			protected void updateItem(Void v, boolean empty) {
				super.updateItem(v, empty);
				setGraphic(empty ? null : infoBtn);
			}
		});
		supplierCol.setStyle("-fx-alignment: CENTER;");
		supplierCol.setPrefWidth(40);

		TableColumn<Item, String> alertCol = new TableColumn<>("!");
		alertCol.setStyle("-fx-alignment: CENTER; -fx-text-fill: #e74c3c; -fx-font-weight: bold;");
		alertCol.setCellValueFactory(
				cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().isLowStock() ? "⚠" : ""));
		alertCol.setPrefWidth(40);

		table.getColumns().addAll(idCol, sectorCol, nameCol, priceCol, stockCol, supplierCol, alertCol);

		filteredItems = new FilteredList<>(items, e -> true);
		SortedList<Item> sortedItems = new SortedList<>(filteredItems);
		sortedItems.setComparator((a, b) -> a.getName().compareToIgnoreCase(b.getName()));
		table.setItems(sortedItems);
	}

	private VBox createActionsSection(Label title) {
		VBox vbox = new VBox(15, title);

		Button restockBtn = new Button("Restock");
		stylePrimaryButton(restockBtn);

		Button deleteBtn = new Button("Delete Selected");
		stylePrimaryButton(deleteBtn);
		deleteBtn.setStyle(deleteBtn.getStyle() + "-fx-background-color: #e74c3c;");

		discountMode = new ComboBox<>();
		styleComboBox(discountMode);
		discountMode.setItems(FXCollections.observableArrayList("Specific Item", "Sector"));
		discountMode.setPromptText("Apply Discount To");

		discountInput = new TextField();
		styleTextField(discountInput);
		discountInput.setPromptText("Discount %");
		discountInput.setPrefWidth(100);
		discountInput.setVisible(false);

		sectorSelect = new ComboBox<>();
		styleComboBox(sectorSelect);
		sectorSelect.setItems(FXCollections.observableArrayList(Sector.values()));
		sectorSelect.setPromptText("Select Sector");
		sectorSelect.setVisible(false);

		searchItemField = new TextField();
		searchItemField.textProperty().addListener((obs, t, t1) -> {
			String search = t1.toLowerCase().trim();

			filteredItems.setPredicate(item -> {
				if (search.isEmpty())
					return true;
				return item.getName().toLowerCase().contains(search);
			});
		});

		styleTextField(searchItemField);
		searchItemField.setPromptText("Search item");
		searchItemField.setVisible(false);

		discountBtn = new Button("Apply");
		stylePrimaryButton(discountBtn);
		discountBtn.setVisible(false);

		restockBtn.setOnAction(e -> {
			ObservableList<Item> lowStock = items.filtered(i -> i.isLowStock());
			if (!lowStock.isEmpty()) {
				for (Item i : lowStock)
					controller.restockItem(i, 5);
				table.refresh();
			} else
				showAlert("No low stock items.");
			resetActionControls();
		});

		deleteBtn.setOnAction(e -> {
			Item selected = table.getSelectionModel().getSelectedItem();
			if (selected != null) {
				boolean deleted = controller.deleteItem(selected);
				if (deleted) {
					table.refresh();
				} else {
					showAlert("Failed to delete item.");
				}
			} else {
				showAlert("Select an item to delete.");
			}
			resetActionControls();
		});

		discountBtn.setOnAction(e -> {
			try {
				double discountPercent = Double.parseDouble(discountInput.getText()) / 100.0;
				if (discountPercent < 0 || discountPercent >= 1)
					throw new Exception();
				String mode = discountMode.getValue();
				if ("Sector".equals(mode)) {
					Sector sector = sectorSelect.getValue();
					if (sector == null)
						throw new Exception();
					controller.applySectorDiscount(sector, discountPercent);
				} else if ("Specific Item".equals(mode)) {
					ObservableList<Item> matched = controller.searchItems(searchItemField.getText(), null);
					if (!matched.isEmpty())
						controller.applyDiscount(matched.get(0), discountPercent);
					else
						showAlert("No item found.");
				}
				table.refresh();
			} catch (Exception ex) {
				showAlert("Enter valid discount and selection.");
			}
			resetActionControls();
		});

		discountMode.setOnAction(e -> {
			String mode = discountMode.getValue();
			sectorSelect.setVisible("Sector".equals(mode));
			searchItemField.setVisible("Specific Item".equals(mode));
			discountInput.setVisible(true);
			discountBtn.setVisible(true);
		});

		HBox actions = new HBox(15, restockBtn, deleteBtn, discountMode, searchItemField, sectorSelect, discountInput,
				discountBtn);
		actions.setPadding(new Insets(12));
		actions.setAlignment(Pos.CENTER_LEFT);
		actions.setStyle(
				"-fx-background-color: #f8f9fa; -fx-background-radius: 10; -fx-border-color: #ddd; -fx-border-radius: 10;");

		vbox.getChildren().add(actions);
		return vbox;
	}

	private HBox createAddItemSection() {
		TextField tfName = new TextField();
		styleTextField(tfName);
		tfName.setPromptText("Item name");

		TextField tfQty = new TextField();
		styleTextField(tfQty);
		tfQty.setPromptText("Qty");
		tfQty.setPrefWidth(70);

		TextField tfPrice = new TextField();
		styleTextField(tfPrice);
		tfPrice.setPromptText("Price");
		tfPrice.setPrefWidth(90);

		ComboBox<Sector> cbSector = new ComboBox<>();
		styleComboBox(cbSector);
		cbSector.setItems(FXCollections.observableArrayList(Sector.values()));
		cbSector.setPromptText("Sector");

		TextField tfSupplier = new TextField();
		styleTextField(tfSupplier);
		tfSupplier.setPromptText("Supplier");
		tfSupplier.setEditable(false);
		tfSupplier.setStyle(tfSupplier.getStyle() + "-fx-background-color: #eee;");

		cbSector.setOnAction(e -> {
			Sector selectedSector = cbSector.getValue();
			if (selectedSector != null)
				tfSupplier.setText(Supplier.getDefaultSupplier(selectedSector).getName());
			else
				tfSupplier.clear();
		});

		Button btnAdd = new Button("+ Add Item");
		stylePrimaryButton(btnAdd);

		btnAdd.setOnAction(e -> {
			try {
				Sector selectedSector = cbSector.getValue();
				if (selectedSector == null)
					throw new Exception();
				String itemName = tfName.getText().trim();
				if (itemName.isEmpty())
					throw new Exception();

				StringBuilder toSentenceCase = new StringBuilder(itemName.toLowerCase());
				toSentenceCase.setCharAt(0, Character.toUpperCase(toSentenceCase.charAt(0)));
				String name = toSentenceCase.toString();

				boolean success = controller.addItem(name, Integer.parseInt(tfQty.getText()),
						Double.parseDouble(tfPrice.getText()), selectedSector);

				if (!success) {
					showAlert("Failed to add item. It might already exist or have invalid data.");
				} else {
					clearFields(tfName, tfQty, tfPrice, cbSector, tfSupplier);
					table.refresh();
				}

			} catch (NumberFormatException ex) {
				showAlert("Please enter valid numbers for quantity and price.");
			} catch (Exception ex) {
				showAlert("Please fill all fields correctly.");
			}
			resetActionControls();
		});

		HBox addSection = new HBox(15, tfName, tfQty, tfPrice, cbSector, tfSupplier, btnAdd);
		addSection.setPadding(new Insets(15, 0, 0, 0));
		addSection.setAlignment(Pos.CENTER_LEFT);

		return addSection;
	}

	private void stylePrimaryButton(Button b) {
		b.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold; "
				+ "-fx-padding: 8 22; -fx-background-radius: 5; -fx-cursor: hand; " + "-fx-text-overrun: clip;");
		b.setMinWidth(Region.USE_PREF_SIZE);
	}

	private void styleTextField(TextField tf) {
		tf.setStyle(
				"-fx-background-radius: 15; -fx-padding: 6 12; -fx-border-color: #ddd; -fx-border-radius: 15; -fx-background-color: white;");
		tf.setMinWidth(Region.USE_PREF_SIZE);
	}

	private void styleComboBox(ComboBox<?> cb) {
		cb.setStyle(
				"-fx-background-radius: 15; -fx-border-color: #ddd; -fx-border-radius: 15; -fx-background-color: white;");
		cb.setMinWidth(Region.USE_PREF_SIZE);
	}

	private void showAlert(String msg) {
		new Alert(Alert.AlertType.INFORMATION, msg).show();
	}

	private void resetActionControls() {
		discountMode.setValue(null);
		discountInput.clear();
		discountInput.setVisible(false);
		sectorSelect.setVisible(false);
		searchItemField.setVisible(false);
		discountBtn.setVisible(false);
	}

	private void clearFields(TextField tfN, TextField tfQ, TextField tfP, ComboBox<Sector> cb, TextField tfS) {
		tfN.clear();
		tfQ.clear();
		tfP.clear();
		tfS.clear();
		cb.setValue(null);
	}
}