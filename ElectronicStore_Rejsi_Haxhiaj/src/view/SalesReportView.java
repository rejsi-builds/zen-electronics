package view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import model.billing.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class SalesReportView {
	private TableView<SalesSummary> table;
	private Label totalRevenueLabel;
	private BillFile billFile = BillFile.getInstance();

	public Node getView() {
		BorderPane pane = new BorderPane();
		pane.setPadding(new Insets(20));
		pane.setStyle("-fx-background-color: transparent;");

		Label title = new Label("Sales Reports");
		title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #1a334b;");

		DatePicker fromDate = new DatePicker(LocalDate.now());
		DatePicker toDate = new DatePicker(LocalDate.now());

		Label lblFrom = new Label("From:");
		lblFrom.setStyle("-fx-font-weight: bold;");
		Label lblTo = new Label("To:");
		lblTo.setStyle("-fx-font-weight: bold;");

		Button generateBtn = new Button("Generate Report");
		stylePrimaryButton(generateBtn);

		Button clearBtn = new Button("Clear");
		styleDeleteButton(clearBtn);

		HBox filterBox = new HBox(15, lblFrom, fromDate, lblTo, toDate, generateBtn, clearBtn);
		filterBox.setAlignment(Pos.CENTER_LEFT);
		filterBox.setPadding(new Insets(15));
		filterBox.setStyle(
				"-fx-background-color: #f8f9fa; -fx-background-radius: 10; -fx-border-color: #ddd; -fx-border-radius: 10;");

		table = new TableView<>();
		table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		table.setStyle("-fx-background-radius: 8; -fx-border-radius: 8;");

		TableColumn<SalesSummary, String> dateCol = new TableColumn<>("Period");
		dateCol.setCellValueFactory(new PropertyValueFactory<>("dateRange"));
		dateCol.setSortable(false);

		TableColumn<SalesSummary, String> nameCol = new TableColumn<>("Cashier");
		nameCol.setCellValueFactory(new PropertyValueFactory<>("cashierName"));
		nameCol.setSortable(false);

		TableColumn<SalesSummary, Integer> countCol = new TableColumn<>("Bills");
		countCol.setCellValueFactory(new PropertyValueFactory<>("nrOfBills"));
		countCol.setSortable(false);

		TableColumn<SalesSummary, Double> revCol = new TableColumn<>("Revenue (€)");
		revCol.setCellValueFactory(new PropertyValueFactory<>("totalRevenue"));
		revCol.setSortable(false);

		table.getColumns().addAll(dateCol, nameCol, countCol, revCol);

		totalRevenueLabel = new Label("Total Revenue: €0.00");
		totalRevenueLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 18px; -fx-text-fill: #1a334b; "
				+ "-fx-background-color: white; -fx-padding: 10 25; -fx-background-radius: 30; "
				+ "-fx-border-color: #dcdde1; -fx-border-radius: 30; -fx-border-width: 1;");

		HBox footer = new HBox(totalRevenueLabel);
		footer.setAlignment(Pos.CENTER_RIGHT);
		footer.setPadding(new Insets(20, 0, 0, 0));

		pane.setTop(new VBox(15, title, filterBox));
		pane.setCenter(table);
		pane.setBottom(footer);

		generateBtn.setOnAction(e -> processReport(fromDate.getValue(), toDate.getValue()));
		clearBtn.setOnAction(e -> {
			table.getItems().clear();
			totalRevenueLabel.setText("Total Revenue: €0.00");
		});

		return pane;
	}

	private void processReport(LocalDate start, LocalDate end) {
		if (start == null || end == null)
			return;

		ArrayList<Bill> allBills = billFile.getBills();
		ObservableList<SalesSummary> reportData = FXCollections.observableArrayList();
		double grandTotal = 0;

		String rangeStr = start.format(DateTimeFormatter.ofPattern("dd/MM")) + "-"
				+ end.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

		for (Bill b : allBills) {
			LocalDate billDate = b.getTs().toLocalDate();
			if (!billDate.isBefore(start) && !billDate.isAfter(end)) {
				double amount = b.calculateTotal();
				grandTotal += amount;

				SalesSummary existing = null;
				for (SalesSummary s : reportData) {
					if (s.getCashierName().equalsIgnoreCase(b.getCashierName())) {
						existing = s;
						break;
					}
				}
				if (existing != null) {
					existing.addBill(amount);
				} else {
					reportData.add(new SalesSummary(rangeStr, b.getCashierName(), 1, amount));
				}
			}
		}
		table.setItems(reportData);
		totalRevenueLabel.setText(String.format("Total Revenue: €%.2f", grandTotal));
	}

	private void stylePrimaryButton(Button b) {
		b.setStyle(
				"-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 22; -fx-background-radius: 5; -fx-cursor: hand;");
	}

	private void styleDeleteButton(Button b) {
		b.setStyle(
				"-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 22; -fx-background-radius: 5; -fx-cursor: hand;");
	}
}