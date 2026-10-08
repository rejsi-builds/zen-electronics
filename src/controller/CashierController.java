package controller;

import javafx.scene.Scene;
import javafx.stage.Stage;
import model.user.Cashier;
import view.POS;

public class CashierController {
	private final Stage stage;
	private final Runnable onLogout;
	private final Cashier cashier;

	public CashierController(Stage stage, Runnable onLogout, Cashier cashier) {
		this.stage = stage;
		this.onLogout = onLogout;
		this.cashier = cashier;
	}

	public void showCashierMenu() {
		POS cashierUI = new POS(cashier);
		cashierUI.setOnLogout(onLogout);

		Scene scene = new Scene(cashierUI, 1000, 700);
		stage.setTitle("Cashier System - " + cashier.getName());
		stage.setScene(scene);
		stage.show();
	}
}
