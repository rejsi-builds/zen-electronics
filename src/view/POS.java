package view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import model.billing.*;
import model.inventory.Item;
import model.user.Cashier;
import controller.InventoryController;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class POS extends BorderPane {

	private InventoryController inventoryController;
	private Cashier currentCashier;

	private TableView<Item> stockTAB;
	private TableView<BillItem> cartTAB;
	private ObservableList<Item> stockLIST;
	private ObservableList<BillItem> cartLIST;
	private Label totalLAB;
	private ComboBox<PaymentMethod> paymentMethodComboBox;
	private DatePicker reportDatePicker;
	private Spinner<Integer> quantSPIN;
	private Button logoutButton;

	private final String DARK_BLUE = "#1a334b";
	private final String MENU_BLUE = "#2C4D6E";
	private final String SUCCESS_GREEN = "#27ae60";
	private final String DANGER_RED = "#ff7675";
	private final String INPUT_RADIUS = "4";

	public POS(Cashier loggedInCashier) {
		inventoryController = new InventoryController();
		currentCashier = loggedInCashier;
		stockLIST = inventoryController.getItems();
		cartLIST = FXCollections.observableArrayList();
		buildUI();
	}

	private void buildUI() {
		this.setPadding(new Insets(20));
		this.setStyle("-fx-background-color: #f4f7f9;");
		this.setTop(createTopHeader());

		GridPane centerGrid = new GridPane();
		centerGrid.setHgap(20);
		centerGrid.setVgap(20);
		centerGrid.setPadding(new Insets(20, 0, 20, 0));

		ColumnConstraints col1 = new ColumnConstraints();
		col1.setPercentWidth(55);
		ColumnConstraints col2 = new ColumnConstraints();
		col2.setPercentWidth(45);
		centerGrid.getColumnConstraints().addAll(col1, col2);

		centerGrid.add(makeStockSection(), 0, 0);
		centerGrid.add(makeCartSection(), 1, 0);

		this.setCenter(centerGrid);

		VBox bottomLayout = new VBox(15);
		bottomLayout.getChildren().addAll(makeCheckoutBar(), makeReportsPanel());
		this.setBottom(bottomLayout);
	}

	private HBox createTopHeader() {
		HBox header = new HBox(15);
		header.setAlignment(Pos.CENTER_LEFT);
		header.setPadding(new Insets(12, 25, 12, 25));
		header.setStyle("-fx-background-color: " + DARK_BLUE + "; -fx-background-radius: 8;");

		ImageView logo = new ImageView(new Image("file:src/logo/logo.png"));
		logo.setFitHeight(45);
		logo.setPreserveRatio(true);

		VBox titleBox = new VBox(0);
		Label mainTitle = new Label("ZEN ELECTRONICS");
		mainTitle.setFont(Font.font("System", FontWeight.BOLD, 22));
		mainTitle.setStyle("-fx-text-fill: white;");
		Label subTitle = new Label("Electronics Store System - POS");
		subTitle.setStyle("-fx-text-fill: #a4b0be; -fx-font-size: 11px;");
		titleBox.getChildren().addAll(mainTitle, subTitle);

		Region spacer = new Region();
		HBox.setHgrow(spacer, Priority.ALWAYS);

		logoutButton = new Button("Log Out");
		logoutButton.setPrefWidth(120);
		logoutButton.setStyle("-fx-background-color: #2C4D6E; -fx-text-fill: " + DANGER_RED
				+ "; -fx-font-weight: bold; -fx-padding: 10; -fx-background-radius: 4; -fx-cursor: hand;");

		header.getChildren().addAll(logo, titleBox, spacer, logoutButton);
		return header;
	}

	private VBox makeStockSection() {
		VBox container = createCard();
		Label title = new Label("Product Inventory");
		title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + DARK_BLUE + ";");

		stockTAB = new TableView<>(stockLIST);
		stockTAB.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

		TableColumn<Item, String> nameCol = new TableColumn<>("Name");
		nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
		TableColumn<Item, Double> priceCol = new TableColumn<>("Price(€)");
		priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
		TableColumn<Item, Integer> stockCol = new TableColumn<>("In Stock");
		stockCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));

		stockTAB.getColumns().addAll(nameCol, priceCol, stockCol);

		HBox controls = new HBox(12);
		controls.setAlignment(Pos.CENTER_LEFT);

		quantSPIN = new Spinner<>(1, 100, 1);
		quantSPIN.setPrefWidth(100);
		quantSPIN.getStyleClass().add(Spinner.STYLE_CLASS_SPLIT_ARROWS_HORIZONTAL);
		quantSPIN.setStyle("-fx-background-radius: 4; -fx-border-color: #ddd; -fx-border-radius: 4;");

		Button addBtn = new Button("Add to Cart");
		styleButton(addBtn, MENU_BLUE, false);
		addBtn.setOnAction(e -> {
			Item selected = stockTAB.getSelectionModel().getSelectedItem();
			if (selected != null)
				addToCART(selected, quantSPIN.getValue());
		});

		Label qtyLabel = new Label("Quantity:");
		qtyLabel.setStyle("-fx-font-weight: bold;");

		controls.getChildren().addAll(qtyLabel, quantSPIN, addBtn);
		container.getChildren().addAll(title, stockTAB, controls);
		return container;
	}

	private VBox makeCartSection() {
		VBox container = createCard();
		Label title = new Label("🛒 Active Order");
		title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + DARK_BLUE + ";");

		cartTAB = new TableView<>(cartLIST);
		cartTAB.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

		TableColumn<BillItem, String> nameCol = new TableColumn<>("Item");
		nameCol.setCellValueFactory(new PropertyValueFactory<>("productName"));
		TableColumn<BillItem, Integer> qtyCol = new TableColumn<>("Quantity");
		qtyCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
		TableColumn<BillItem, Double> totalCol = new TableColumn<>("Subtotal(€)");
		totalCol.setCellValueFactory(new PropertyValueFactory<>("total"));

		cartTAB.getColumns().addAll(nameCol, qtyCol, totalCol);

		HBox totalBox = new HBox();
		totalBox.setAlignment(Pos.CENTER_RIGHT);
		totalBox.setPadding(new Insets(10, 15, 10, 15));
		totalBox.setStyle(
				"-fx-background-color: #f8f9fa; -fx-background-radius: 5; -fx-border-color: #e9ecef; -fx-border-radius: 5;");

		totalLAB = new Label("Order Total: €0.00");
		totalLAB.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #e74c3c;");

		totalBox.getChildren().add(totalLAB);
		container.getChildren().addAll(title, cartTAB, totalBox);
		return container;
	}

	private HBox makeCheckoutBar() {
		HBox bar = new HBox(15);
		bar.setAlignment(Pos.CENTER);
		bar.setPadding(new Insets(15, 25, 15, 25));
		bar.setStyle(
				"-fx-background-color: white; -fx-background-radius: 8; -fx-border-color: #dcdde1; -fx-border-radius: 8;");

		paymentMethodComboBox = new ComboBox<>();
		paymentMethodComboBox.getItems().addAll(PaymentMethod.values());
		paymentMethodComboBox.setValue(PaymentMethod.CASH);
		paymentMethodComboBox.setPrefWidth(150);
		paymentMethodComboBox.setStyle(
				"-fx-background-color: white; -fx-border-color: #ced4da; -fx-border-radius: 4; -fx-padding: 2;");

		Label payLabel = new Label("Payment:");
		payLabel.setStyle("-fx-font-weight: bold;");

		Region midSpacer = new Region();
		midSpacer.setPrefWidth(40);

		Button clearBtn = new Button("Clear Cart");
		styleButton(clearBtn, "#95a5a6", true);
		clearBtn.setOnAction(e -> {
			cartLIST.clear();
			updateTOTAL();
		});

		Button checkoutBtn = new Button("Checkout");
		styleButton(checkoutBtn, SUCCESS_GREEN, false);
		checkoutBtn.setOnAction(e -> doCHECKOUT());

		bar.getChildren().addAll(payLabel, paymentMethodComboBox, midSpacer, clearBtn, checkoutBtn);
		return bar;
	}

	private VBox makeReportsPanel() {
		VBox panel = createCard();
		Label title = new Label("Daily Bills & Turnover");
		title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + DARK_BLUE + ";");

		HBox tools = new HBox(15);
		tools.setAlignment(Pos.CENTER_LEFT);

		reportDatePicker = new DatePicker(LocalDate.now());
		reportDatePicker.setStyle("-fx-border-color: #ced4da; -fx-border-radius: 4;");

		Label selectDateLabel = new Label("Select Date:");
		selectDateLabel.setStyle("-fx-font-weight: bold;");

		Button viewBillsBtn = new Button("Bills");
		Button summaryBtn = new Button("Turnover");

		styleButton(viewBillsBtn, MENU_BLUE, false);
		styleButton(summaryBtn, MENU_BLUE, false);

		viewBillsBtn.setOnAction(e -> showDailyBills());
		summaryBtn.setOnAction(e -> showDailySummary());

		tools.getChildren().addAll(selectDateLabel, reportDatePicker, viewBillsBtn, summaryBtn);
		panel.getChildren().addAll(title, tools);
		return panel;
	}

	private void doCHECKOUT() {
		if (cartLIST.isEmpty())
			return;

		ArrayList<BillItem> currentItems = new ArrayList<>(cartLIST);

		if (inventoryController.processSale(currentItems)) {
			Bill bill = currentCashier.generateBill(currentItems, paymentMethodComboBox.getValue());

			ReceiptPrinter.print(bill);
			showRECEIPT(bill);

			cartLIST.clear();
			updateTOTAL();
			stockTAB.refresh();
			quantSPIN.getValueFactory().setValue(1);
		}
	}

	private void showDailyBills() {
		LocalDate date = reportDatePicker.getValue();
		ArrayList<Bill> bills = BillFile.getInstance().getBills();
		StringBuilder sb = new StringBuilder();
		boolean found = false;

		for (Bill b : bills) {
			if (b.getTs().toLocalDate().equals(date) && b.getCashierName().equals(currentCashier.getName())) {
				sb.append(String.format("Bill #%s - %s - €%.2f\n", b.getBillID(), b.getPaymentMethod(),
						b.calculateTotal()));
				found = true;
			}
		}

		if (!found) {
			new Alert(Alert.AlertType.INFORMATION, "No records found for " + currentCashier.getName() + " on " + date)
					.show();
			return;
		}

		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("Transactions - " + date);
		alert.setHeaderText("Daily Bills for " + currentCashier.getName());
		alert.setContentText(sb.toString());
		alert.showAndWait();
	}

	private void showDailySummary() {
		LocalDate date = reportDatePicker.getValue();
		ArrayList<Bill> bills = BillFile.getInstance().getBills();
		StringBuilder sb = new StringBuilder();
		double total = 0;
		int count = 0;

		for (Bill b : bills) {
			if (b.getTs().toLocalDate().equals(date) && b.getCashierName().equals(currentCashier.getName())) {
				sb.append(String.format("Bill #%s - %s - €%.2f\n", b.getBillID(), b.getPaymentMethod(),
						b.calculateTotal()));
				total += b.calculateTotal();
				count++;
			}
		}

		if (count == 0) {
			new Alert(Alert.AlertType.INFORMATION, "No records found for " + currentCashier.getName() + " on " + date)
					.show();
			return;
		}

		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("Daily Summary - " + date);
		alert.setHeaderText("Daily Turnover for " + currentCashier.getName());
		alert.setContentText("Total Bills: " + count + "\n" + "Total Revenue: €" + String.format("%.2f", total));
		alert.showAndWait();
	}

	private VBox createCard() {
		VBox vbox = new VBox(12);
		vbox.setPadding(new Insets(15));
		vbox.setStyle(
				"-fx-background-color: white; -fx-background-radius: 8; -fx-border-color: #dcdde1; -fx-border-radius: 8;");
		VBox.setVgrow(vbox, Priority.ALWAYS);
		return vbox;
	}

	private void styleButton(Button b, String color, boolean isOutline) {
		b.setStyle("-fx-background-color: " + (isOutline ? "transparent" : color) + "; -fx-text-fill: "
				+ (isOutline ? color : "white") + "; -fx-border-color: " + color + "; -fx-border-radius: "
				+ INPUT_RADIUS + "; -fx-font-weight: bold; -fx-padding: 8 18; -fx-cursor: hand;");
	}

	private void addToCART(Item item, int quant) {
		if (quant > item.getQuantity()) {
			new Alert(Alert.AlertType.WARNING, "Not enough stock!").show();
			return;
		}
		cartLIST.add(new BillItem(item.getItemID(), item.getName(), item.getPrice(), quant));
		updateTOTAL();
	}

	private void updateTOTAL() {
		double total = cartLIST.stream().mapToDouble(BillItem::getTotal).sum();
		totalLAB.setText(String.format("Order Total: €%.2f", total));
	}

	private void showRECEIPT(Bill bill) {
		Stage receiptSTG = new Stage();
		receiptSTG.setTitle("Official Receipt #" + bill.getBillID());

		VBox receiptBOX = new VBox(10);
		receiptBOX.setPadding(new Insets(20));
		receiptBOX.setAlignment(Pos.TOP_CENTER);
		receiptBOX.setStyle("-fx-background-color: white; -fx-border-color: " + DARK_BLUE + "; -fx-border-width: 2;");

		Label storeLAB = new Label("ZEN ELECTRONICS");
		storeLAB.setFont(Font.font("System", FontWeight.BOLD, 20));
		storeLAB.setStyle("-fx-text-fill: " + DARK_BLUE + ";");

		DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
		VBox infoBox = new VBox(5);
		infoBox.getChildren().addAll(new Label("Receipt ID: " + bill.getBillID()),
				new Label("Date: " + bill.getTs().format(fmt)), new Separator());

		VBox itemsBOX = new VBox(5);
		for (BillItem item : bill.getItems()) {
			String line = String.format("%-15s %2d x €%-6.2f = €%.2f", item.getProductName(), item.getQuantity(),
					item.getTotal() / item.getQuantity(), item.getTotal());
			Label itemLAB = new Label(line);
			itemLAB.setFont(Font.font("Monospaced", 12));
			itemsBOX.getChildren().add(itemLAB);
		}

		Label totalLine = new Label(String.format("TOTAL: €%.2f", bill.calculateTotal()));
		totalLine.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: " + SUCCESS_GREEN + ";");

		Button closeBtn = new Button("Close");
		styleButton(closeBtn, DARK_BLUE, false);
		closeBtn.setOnAction(e -> receiptSTG.close());

		receiptBOX.getChildren().addAll(storeLAB, infoBox, itemsBOX, new Separator(), totalLine,
				new Label("Payment: " + bill.getPaymentMethod()), closeBtn);

		receiptSTG.setScene(new Scene(receiptBOX, 380, 450));
		receiptSTG.show();
	}

	public void setOnLogout(Runnable onLogout) {
		logoutButton.setOnAction(e -> onLogout.run());
	}
}
