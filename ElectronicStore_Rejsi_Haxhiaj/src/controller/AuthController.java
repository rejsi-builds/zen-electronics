package controller;

import dao.AdminDAO;
import dao.EmployeeDAO;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.user.*;
import view.LoginView;

public class AuthController {

	private final LoginView view;
	private final Scene loginScene;

	private Stage stage;

	public AuthController() {
		this.view = new LoginView();
		this.loginScene = new Scene(view, 600, 500);

		view.getLoginBtn().setOnAction(e -> handleLogin());
	}

	public void init(Stage stage) {
		this.stage = stage;
		this.stage.setTitle("LogIn");
		this.stage.setScene(loginScene);
		this.stage.show();
	}

	private void handleLogin() {
		if (stage == null)
			stage = (Stage) view.getScene().getWindow();

		String username = view.getUserField().getText().trim();
		String password = view.getPassField().getText();

		if (isAdmin(username, password)) {
			showAdminMenu();
			return;
		}

		Employee emp = findEmployee(username, password);
		if (emp == null) {
			view.showError("Invalid username or password");
			return;
		}

		switch (emp.getRole()) {
		case MANAGER -> showManagerMenu(emp);
		case CASHIER -> showCashierMenu(emp);
		default -> view.showError("Unknown role");
		}
	}

	private boolean isAdmin(String username, String password) {
		User admin = AdminDAO.readAdminCredentials();
		return admin != null && admin.getUsername() != null && admin.getPassword() != null
				&& admin.getUsername().equalsIgnoreCase(username) && admin.getPassword().equals(password);
	}

	private Employee findEmployee(String username, String password) {
		for (Employee e : EmployeeDAO.loadAll()) {
			if (e.getUsername() != null && e.getPassword() != null && e.getUsername().equalsIgnoreCase(username)
					&& e.getPassword().equals(password)) {
				return e;
			}
		}
		return null;
	}

	private void logout() {
		view.getUserField().clear();
		view.getPassField().clear();
		view.showError("");
		stage.setScene(loginScene);
	}

	private void showAdminMenu() {
		AdminController adminController = new AdminController(stage, this::logout);
		adminController.showAdminMenu();
	}

	private void showManagerMenu(Employee loggedInManager) {
		ManagerController managerController = new ManagerController(stage, this::logout, loggedInManager);
		managerController.showManagerMenu();
	}

	private void showCashierMenu(Employee loggedInCashier) {
		if (!(loggedInCashier instanceof Cashier cashier)) {
			view.showError("This employee is not a cashier!");
			return;
		}

		CashierController cashierController = new CashierController(stage, this::logout, cashier);
		cashierController.showCashierMenu();
	}
}
