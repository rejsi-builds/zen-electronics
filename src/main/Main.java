package main;

import controller.AuthController;
import dao.AdminDAO;
import dao.EmployeeDAO;
import dao.GenericDAO;
import javafx.application.Application;
import javafx.stage.Stage;
import model.inventory.*;

public class Main extends Application {
	@Override
	public void start(Stage stage) {
		AuthController controller = new AuthController();
		controller.init(stage);
	}

	public static void main(String[] args) {

		EmployeeDAO.ensureEmployeeFileExists();
		AdminDAO.ensureAdminFileExists();
		seedInventoryData();
		launch(args);
	}

	private static void seedInventoryData() {
		GenericDAO<Item> item = new GenericDAO<>("items.dat");

		if (item.getAll().isEmpty()) {
			Item[] items = { new Item("Laptop", 5, 1200, Sector.IT, Supplier.getDefaultSupplier(Sector.IT)),
					new Item("Mouse", 20, 25, Sector.ACCESSORIES, Supplier.getDefaultSupplier(Sector.ACCESSORIES)),
					new Item("Console", 2, 499, Sector.GAMING, Supplier.getDefaultSupplier(Sector.GAMING)),
					new Item("Keyboard", 15, 50, Sector.ACCESSORIES, Supplier.getDefaultSupplier(Sector.ACCESSORIES)),
					new Item("Monitor", 7, 300, Sector.IT, Supplier.getDefaultSupplier(Sector.IT)) };

			for (Item i : items) {
				item.create(i);
			}

			System.out.println("Inventory data seeded successfully.");

		}
	}
}
