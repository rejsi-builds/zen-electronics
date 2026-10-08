package controller;

import dao.EmployeeDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import model.user.*;
import view.EmployeeView;
import view.AdminDashboard;
import java.util.ArrayList;

public class AdminController {

	private final Stage stage;
	private final Runnable onLogout;
	private AdminDashboard adminView;
	private Scene adminScene;
	private ObservableList<Employee> employees;
	private FilteredList<Employee> filtered;

	public AdminController(Stage stage, Runnable onLogout) {
		this.stage = stage;
		this.onLogout = onLogout;
	}

	public void showAdminMenu() {
		adminView = new AdminDashboard();
		employees = FXCollections.observableArrayList(EmployeeDAO.loadAll());
		filtered = new FilteredList<>(employees, e -> true);

		adminView.getEmployeeListView().setItems(filtered);
		adminView.getEmployeeListView().setCellFactory(lv -> new ListCell<>() {
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

					Label meta = new Label("ID: " + emp.getEmployeeID() + " • Username: " + emp.getUsername());
					meta.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 11px;");
					info.getChildren().addAll(name, meta);

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

		adminView.getSearchField().textProperty().addListener((obs, old, val) -> {
			filtered.setPredicate(e -> {
				if (val == null || val.isEmpty())
					return true;
				String lower = val.toLowerCase();
				return e.getName().toLowerCase().contains(lower) || e.getEmployeeID().toLowerCase().contains(lower);
			});
		});

		adminView.getBtnAddEmployee().setOnAction(e -> showAddEmployee());
		adminView.getBtnRemoveEmployee().setOnAction(e -> removeSelectedEmployee());
		adminView.getBtnChangeRole().setOnAction(e -> beginInlineRoleChange());
		adminView.getBtnCancelRole().setOnAction(e -> cancelInlineRoleChange());
		adminView.getBtnApplyRole().setOnAction(e -> applyInlineRoleChange());
		adminView.getLogoutBtn().setOnAction(e -> onLogout.run());

		adminScene = new Scene(adminView, 1250, 720);
		stage.setScene(adminScene);
	}

	private void removeSelectedEmployee() {
		Employee selected = adminView.getEmployeeListView().getSelectionModel().getSelectedItem();

		if (selected == null) {
			adminView.setStatus("Select an employee first.");
			return;
		}

		employees.remove(selected);
		EmployeeDAO.saveAll(new ArrayList<>(employees));

		adminView.setStatus("Removed: " + selected.getEmployeeID());
	}

	private void showAddEmployee() {
		EmployeeView addView = new EmployeeView();
		Scene addScene = new Scene(addView, 1000, 700);

		addView.getBackBtn().setOnAction(e -> stage.setScene(adminScene));

		addView.getSaveBtn().setOnAction(e -> {
			try {
				AccessLevel type = addView.getTypeBox().getValue();
				String username = safeTrim(addView.getUsernameField().getText());
				String password = addView.getPasswordField().getText() == null ? ""
						: addView.getPasswordField().getText().trim();
				String name = safeTrim(addView.getNameField().getText());
				var dob = addView.getDobPicker().getValue();
				String phone = safeTrim(addView.getPhoneField().getText());
				String email = safeTrim(addView.getEmailField().getText());

				if (type == null || username.isBlank() || password.isBlank() || name.isBlank() || dob == null
						|| phone.isBlank() || email.isBlank()) {
					addView.setMessage("All fields are required.");
					return;
				}

				if (EmployeeDAO.usernameExists(username)) {
					addView.setMessage("Username already exists.");
					return;
				}

				double salary = salaryForRole(type);

				String id = EmployeeIDGenerator.generate(type);

				Employee newEmp = (type == AccessLevel.CASHIER)
						? new Cashier(id, username, password, name, dob, phone, email, salary)
						: new Manager(id, username, password, name, dob, phone, email, salary);

				employees.add(newEmp);
				EmployeeDAO.saveAll(new ArrayList<>(employees));

				addView.setMessage("Saved. Employee ID: " + id);

				stage.setScene(adminScene);
				adminView.setStatus("Added: " + id);

			} catch (Exception ex) {
				ex.printStackTrace();
				addView.setMessage("Error saving employee.");
			}
		});

		stage.setScene(addScene);
	}

	private void beginInlineRoleChange() {
		Employee selected = adminView.getEmployeeListView().getSelectionModel().getSelectedItem();
		if (selected == null) {
			adminView.setStatus("Select an employee first.");
			return;
		}

		adminView.getRoleBox().setValue(selected.getRole());

		adminView.showRoleEditor(true);
		adminView.setStatus("Change role for: " + selected.getEmployeeID());
	}

	private void cancelInlineRoleChange() {
		adminView.getRoleBox().setValue(null);
		adminView.showRoleEditor(false);
		adminView.setStatus("");
	}

	private void applyInlineRoleChange() {
		Employee emp = adminView.getEmployeeListView().getSelectionModel().getSelectedItem();
		if (emp == null) {
			adminView.setStatus("Select an employee first.");
			return;
		}

		AccessLevel newRole = adminView.getRoleBox().getValue();
		if (newRole == null || (newRole != AccessLevel.CASHIER && newRole != AccessLevel.MANAGER)) {
			adminView.setStatus("Pick Cashier or Manager.");
			return;
		}

		if (emp.getRole() == newRole) {
			adminView.setStatus("Role is already " + newRole + ".");
			adminView.showRoleEditor(false);
			return;
		}

		String oldId = emp.getEmployeeID();
		String newId = changeIdPrefix(oldId, newRole);

		boolean conflict = employees.stream()
				.anyMatch(x -> x.getEmployeeID() != null && x.getEmployeeID().equalsIgnoreCase(newId) && x != emp);

		if (conflict) {
			adminView.setStatus("ID conflict: " + newId + " already exists.");
			return;
		}

		emp.setRole(newRole);
		emp.setEmployeeID(newId);

		EmployeeDAO.saveAll(new java.util.ArrayList<>(employees));

		adminView.getEmployeeListView().refresh();
		adminView.showRoleEditor(false);
		adminView.setStatus("Updated: " + oldId + " → " + newId);
	}

	private void showDetails(Employee e) {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("Employee Profile");
		alert.setHeaderText(null);

		VBox content = new VBox(8);
		content.setPadding(new Insets(15));

		Label personalHeader = new Label("Personal Information");
		personalHeader.setStyle("-fx-font-weight: bold; -fx-text-fill: black; -fx-font-size: 11px;");
		VBox personalGroup = new VBox(3, new Label("Full Name: " + e.getName()),
				new Label("Date of Birth: " + e.getDateOfBirth()));

		Label workHeader = new Label("Work Profile");
		workHeader.setStyle("-fx-font-weight: bold; -fx-text-fill: black; -fx-font-size: 11px;");
		VBox workGroup = new VBox(3, new Label("ID: " + e.getEmployeeID()), new Label("Role: " + e.getRole()),
				new Label("Salary: $" + String.format("%.2f", e.getSalary())));

		Label contactHeader = new Label("Contact Details");
		contactHeader.setStyle("-fx-font-weight: bold; -fx-text-fill: black; -fx-font-size: 11px;");
		VBox contactGroup = new VBox(3, new Label("Email: " + e.getEmail()), new Label("Phone: " + e.getPhone()));

		Separator s1 = new Separator();
		s1.setPadding(new Insets(5, 0, 5, 0));
		Separator s2 = new Separator();
		s2.setPadding(new Insets(5, 0, 5, 0));

		content.getChildren().addAll(personalHeader, personalGroup, s1, workHeader, workGroup, s2, contactHeader,
				contactGroup);

		alert.getDialogPane().setContent(content);
		alert.showAndWait();
	}

	private String changeIdPrefix(String oldId, AccessLevel newRole) {
		if (oldId == null || oldId.length() < 2) {
			return EmployeeIDGenerator.generate(newRole);
		}

		char prefix = (newRole == AccessLevel.CASHIER) ? 'C' : 'M';
		String digits = oldId.substring(1);
		return prefix + digits;
	}

	public static double salaryForRole(AccessLevel role) {
		return switch (role) {
		case CASHIER -> 1000;
		case MANAGER -> 1700;
		default -> throw new IllegalArgumentException("Unsupported role: " + role);
		};
	}

	private String safeTrim(String s) {
		return s == null ? "" : s.trim();
	}

	private String safe(String s) {
		return s == null ? "" : s;
	}
}