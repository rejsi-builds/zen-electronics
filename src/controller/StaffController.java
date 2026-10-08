package controller;

import dao.EmployeeDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.user.*;
import view.EmployeeView;
import java.time.LocalDate;
import java.util.ArrayList;

public class StaffController {
	private final Stage stage;
	private final Scene previousScene;

	private final ObservableList<Employee> staff = FXCollections.observableArrayList();
	private final FilteredList<Employee> cashiers;

	public StaffController(Stage stage, Scene previousScene) {
		this.stage = stage;
		this.previousScene = previousScene;

		staff.setAll(EmployeeDAO.loadAll());

		cashiers = new FilteredList<>(staff, p -> p.getRole() == AccessLevel.CASHIER);
	}

	public void refreshData() {
		staff.setAll(EmployeeDAO.loadAll());
	}

	public void setupSearch(javafx.scene.control.TextField searchField) {
		searchField.textProperty().addListener((observable, oldValue, newValue) -> {
			cashiers.setPredicate(employee -> {
				boolean isNotAdmin = employee.getRole() != AccessLevel.ADMIN;

				if (newValue == null || newValue.isEmpty()) {
					return isNotAdmin;
				}

				String lowerCaseFilter = newValue.toLowerCase();
				boolean matchesSearch = employee.getName().toLowerCase().contains(lowerCaseFilter)
						|| employee.getEmployeeID().toLowerCase().contains(lowerCaseFilter);

				return isNotAdmin && matchesSearch;
			});
		});
	}

	public SortedList<Employee> getSortedStaff() {
		SortedList<Employee> sortedStaff = new SortedList<>(cashiers);
		sortedStaff.setComparator((a, b) -> a.getName().compareToIgnoreCase(b.getName()));
		return sortedStaff;
	}

	public void showAddCashierForm() {
		EmployeeView addView = new EmployeeView();
		addView.getTypeBox().setValue(AccessLevel.CASHIER);
		addView.getTypeBox().setDisable(true);

		Scene addScene = new Scene(addView, 900, 700);
		addView.getBackBtn().setOnAction(e -> stage.setScene(previousScene));

		addView.getSaveBtn().setOnAction(e -> {
			try {
				String username = safeTrim(addView.getUsernameField().getText());
				String password = safeTrim(addView.getPasswordField().getText());
				String name = safeTrim(addView.getNameField().getText());
				LocalDate dob = addView.getDobPicker().getValue();
				String phone = safeTrim(addView.getPhoneField().getText());
				String email = safeTrim(addView.getEmailField().getText());

				if (username.isBlank() || password.isBlank() || name.isBlank() || dob == null || phone.isBlank()
						|| email.isBlank()) {
					addView.setMessage("All fields are required.");
					return;
				}

				if (EmployeeDAO.usernameExists(username)) {
					addView.setMessage("Username already exists.");
					return;
				}

				double salary = AdminController.salaryForRole(AccessLevel.CASHIER);
				String id = EmployeeIDGenerator.generate(AccessLevel.CASHIER);

				Cashier newCashier = new Cashier(id, username, password, name, dob, phone, email, salary);

				staff.add(newCashier);
				ArrayList<Employee> emp = new ArrayList<>();
				emp.addAll(staff);
				EmployeeDAO.saveAll(emp);

				refreshData();
				stage.setScene(previousScene);

			} catch (Exception ex) {
				addView.setMessage("Error saving cashier.");
			}
		});

		stage.setScene(addScene);
	}

	private String safeTrim(String s) {
		return s == null ? "" : s.trim();
	}
}