package view;

import java.util.ArrayList;

import dao.GenericDAO;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import model.inventory.Item;
import model.user.Manager;

public class HomeView {

	private final String fullName;

	public HomeView(String fullName) {
		this.fullName = fullName;
	}

	public VBox getView() {
		Label welcome = new Label("Welcome back, " + fullName + "!");
		welcome.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #1a334b;");

		Label notifTitle = new Label("New Notifications");
		notifTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #1a334b;");

		VBox notifBox = new VBox(8);
		notifBox.setPadding(new Insets(15));
		notifBox.setStyle("""
				-fx-background-color: #ffffff;
				-fx-background-radius: 15;
				-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 4);
				""");

		GenericDAO<Item> items = new GenericDAO<>("items.dat");
		ArrayList<Item> items1 = new ArrayList<>(items.getAll());

		Manager m = new Manager(null, null, null, null, null, null, null, 0);

		for (Item i : items1) {
			m.addItem(i);
		}

		ArrayList<Item> lowStock = m.getLowStockItems();

		if (lowStock.isEmpty()) {
			Label noNotif = new Label(" ");
			noNotif.setStyle("-fx-text-fill: #7f8c8d; -fx-font-style: italic;");
			notifBox.getChildren().add(noNotif);
		} else {
			for (Item i : lowStock) {
				Label alert = new Label("• " + i.getName() + " is in low stock.");
				alert.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
				notifBox.getChildren().add(alert);
			}
		}

		VBox root = new VBox(20, welcome, notifTitle, notifBox);
		root.setAlignment(Pos.TOP_LEFT);
		return root;
	}
}