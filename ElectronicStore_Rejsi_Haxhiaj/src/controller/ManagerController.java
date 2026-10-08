package controller;

import javafx.scene.Scene;
import javafx.stage.Stage;
import model.user.Employee;
import view.ManagerDashboard;

public class ManagerController {

	private final Stage stage;
	private final Runnable onLogout;
	private final Employee manager;

	public ManagerController(Stage stage, Runnable onLogout, Employee manager) {
		this.stage = stage;
		this.onLogout = onLogout;
		this.manager = manager;
	}

	public void showManagerMenu() {
		ManagerDashboard dash = new ManagerDashboard(onLogout, manager);
		Scene scene = new Scene(dash.getRoot(), 1250, 720);
		stage.setTitle("Manager Dashboard");
		stage.setScene(scene);
	}
}