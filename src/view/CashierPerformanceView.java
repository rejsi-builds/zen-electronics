package view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import model.billing.Bill;
import model.billing.BillFile;
import model.performance.EmployeePerformance;
import model.user.Employee;
import dao.EmployeeDAO;

import java.time.Month;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

public class CashierPerformanceView {

	private TableView<EmployeePerformance> table = new TableView<>();
	private BillFile billFile = BillFile.getInstance();
	private ComboBox<Month> monthPicker = new ComboBox<>();
	private Spinner<Integer> yearPicker = new Spinner<>(2020, 2030, YearMonth.now().getYear());

	public Node getView() {
		BorderPane pane = new BorderPane();
		pane.setPadding(new Insets(20));
		pane.setStyle("-fx-background-color: transparent;");

		Label title = new Label("Cashier Performance");
		title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #1a334b;");

		Label lblMonth = new Label("Select Month:");
		lblMonth.setStyle("-fx-font-weight: bold;");
		Label lblYear = new Label("Year:");
		lblYear.setStyle("-fx-font-weight: bold;");

		monthPicker.getItems().setAll(Month.values());
		monthPicker.setValue(YearMonth.now().getMonth());
		styleComboBox(monthPicker);
		monthPicker.setPrefWidth(150);

		yearPicker.setPrefWidth(100);
		styleSpinner(yearPicker);

		Button calculate = new Button("Calculate & Rank");
		stylePrimaryButton(calculate);

		Button reset = new Button("Reset Ranking");
		styleDeleteButton(reset);

		HBox filterBox = new HBox(15, lblMonth, monthPicker, lblYear, yearPicker, calculate, reset);
		filterBox.setAlignment(Pos.CENTER_LEFT);
		filterBox.setPadding(new Insets(15));
		filterBox.setStyle(
				"-fx-background-color: #f8f9fa; -fx-background-radius: 10; -fx-border-color: #ddd; -fx-border-radius: 10;");

		setupColumns();

		pane.setTop(new VBox(15, title, filterBox));
		pane.setCenter(table);

		calculate.setOnAction(e -> processPerformance());
		reset.setOnAction(e -> table.getItems().clear());

		return pane;
	}

	private void setupColumns() {
		table.getColumns().clear();
		table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		table.setStyle("-fx-background-radius: 8; -fx-border-radius: 8; -fx-selection-bar: #355F87;");

		TableColumn<EmployeePerformance, String> idCol = new TableColumn<>("ID");
		idCol.setCellValueFactory(new PropertyValueFactory<>("employeeID"));
		idCol.setSortable(false);

		TableColumn<EmployeePerformance, String> nameCol = new TableColumn<>("Name");
		nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
		nameCol.setSortable(false);

		TableColumn<EmployeePerformance, Integer> billsCol = new TableColumn<>("Bills");
		billsCol.setCellValueFactory(new PropertyValueFactory<>("totalBills"));
		billsCol.setSortable(false);

		TableColumn<EmployeePerformance, Integer> itemsCol = new TableColumn<>("Items Sold");
		itemsCol.setCellValueFactory(new PropertyValueFactory<>("totalItemsSold"));
		itemsCol.setSortable(false);

		TableColumn<EmployeePerformance, Double> revCol = new TableColumn<>("Revenue (€)");
		revCol.setCellValueFactory(new PropertyValueFactory<>("totalRevenue"));
		revCol.setSortable(false);

		table.getColumns().addAll(idCol, nameCol, billsCol, itemsCol, revCol);
	}

	private void styleComboBox(ComboBox<?> cb) {
		cb.setStyle(
				"-fx-background-radius: 15; -fx-border-color: #ddd; -fx-border-radius: 15; -fx-background-color: white;");
	}

	private void styleSpinner(Spinner<Integer> sp) {
		sp.setStyle(
				"-fx-background-radius: 15; -fx-border-color: #ddd; -fx-border-radius: 15; -fx-background-color: white;");
		sp.getEditor().setStyle("-fx-background-color: transparent; -fx-background-radius: 15; -fx-padding: 2 10;");
	}

	private void stylePrimaryButton(Button b) {
		b.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold; "
				+ "-fx-padding: 8 22; -fx-background-radius: 5; -fx-cursor: hand;");
	}

	private void styleDeleteButton(Button b) {
		b.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold; "
				+ "-fx-padding: 8 22; -fx-background-radius: 5; -fx-cursor: hand;");
	}

	private void processPerformance() {
		ArrayList<Bill> allBills = billFile.getBills();
		List<Employee> allEmployees = EmployeeDAO.loadAll();
		ObservableList<EmployeePerformance> data = FXCollections.observableArrayList();
		Month selectedMonth = monthPicker.getValue();
		int selectedYear = yearPicker.getValue();

		for (Bill b : allBills) {
			if (b.getTs().getMonth() == selectedMonth && b.getTs().getYear() == selectedYear) {
				EmployeePerformance existing = findInList(data, b.getCashierName());
				if (existing != null)
					existing.updateEmployeePerformance(b);
				else {
					Employee empObj = findEmployeeByName(allEmployees, b.getCashierName());
					if (empObj != null) {
						EmployeePerformance newEp = new EmployeePerformance(empObj);
						newEp.updateEmployeePerformance(b);
						data.add(newEp);
					}
				}
			}
		}
		if (data.isEmpty()) {
			new Alert(Alert.AlertType.WARNING, "No sales found.").show();
			return;
		}
		data.sort((a, b) -> Double.compare(b.getTotalRevenue(), a.getTotalRevenue()));
		table.setItems(data);
		showEmployeeOfMonth(data.get(0));
	}

	private void showEmployeeOfMonth(EmployeePerformance top) {
		Alert alert = new Alert(Alert.AlertType.NONE);
		alert.getButtonTypes().add(ButtonType.CLOSE);
		VBox card = new VBox(5);
		card.setAlignment(Pos.CENTER);
		card.setPadding(new Insets(25));
		card.setStyle("-fx-background-color: white; -fx-background-radius: 15;");

		Label trophy = new Label("🏆");
		trophy.setStyle("-fx-font-size: 40px;");

		Label title = new Label("Employee Of The Month");
		title.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #27ae60;");

		Label name = new Label(top.getName());
		name.setStyle("-fx-font-size: 24px; -fx-font-weight: 800; -fx-text-fill: #1a334b;");

		Label statsLabel = new Label(
				String.format("Bills: %d    |    Revenue: €%.2f", top.getTotalBills(), top.getTotalRevenue()));
		statsLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #7f8c8d; -fx-padding: 10 0 0 0;");

		card.getChildren().addAll(trophy, title, name, statsLabel);

		alert.getDialogPane().setContent(card);
		alert.getDialogPane().setStyle("-fx-background-color: #1a334b; -fx-padding: 15;");
		alert.showAndWait();
	}

	private EmployeePerformance findInList(ObservableList<EmployeePerformance> data, String name) {
		for (EmployeePerformance ep : data) {
			if (ep.getName().equalsIgnoreCase(name))
				return ep;
		}
		return null;
	}

	private Employee findEmployeeByName(List<Employee> list, String name) {
		for (Employee e : list) {
			if (e.getName().equalsIgnoreCase(name))
				return e;
		}
		return null;
	}
}