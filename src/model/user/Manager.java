package model.user;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;

import controller.AdminController;
import dao.EmployeeDAO;
import model.inventory.*;
import model.performance.EmployeePerformance;
import model.user.Employee;
import model.user.Cashier;

public class Manager extends Employee implements Serializable {
	@Serial
	private static final long serialVersionUID = 1;

	private ArrayList<Item> items;

	public Manager(String employeeID, String username, String password, String name, LocalDate dateOfBirth,
			String phone, String email, double salary) {
		super(employeeID, username, password, name, dateOfBirth, phone, email, salary, AccessLevel.MANAGER);
		this.items = new ArrayList<>();
	}

	public void addItem(Item i) {
		items.add(i);
	}

	public ArrayList<Item> getLowStockItems() {
		ArrayList<Item> lowStock = new ArrayList<>();
		for (Item i : items) {
			if (i.isLowStock())
				lowStock.add(i);
		}

		return lowStock;
	}

	public void restockItem(Item i, int quantity) {
		i.updateStock(quantity);
	}

	public static void applyDiscount(Item i, double discountP) {
		i.setPrice(i.getPrice() * (1 - discountP));
	}

	public static void addCashier(Employee e) {
		if (e == null || e.getUsername().isBlank() || e.getPassword().isBlank() || e.getName().isBlank()
				|| e.getDateOfBirth() == null || e.getPhone().isBlank() || e.getEmail().isBlank()) {
			System.out.println("All data fields are required.");
			return;
		}

		if (EmployeeDAO.usernameExists(e.getUsername())) {
			System.out.println("Username already exists.");
			return;
		}

		String cashierID = EmployeeIDGenerator.generate(AccessLevel.CASHIER);

		double salary = AdminController.salaryForRole(AccessLevel.CASHIER);

		Cashier newCashier = new Cashier(cashierID, e.getUsername(), e.getPassword(), e.getName(), e.getDateOfBirth(),
				e.getPhone(), e.getEmail(), salary);

		ArrayList<Employee> staff = new ArrayList<>(EmployeeDAO.loadAll());
		staff.add(newCashier);
		EmployeeDAO.saveAll(staff);
	}

}
