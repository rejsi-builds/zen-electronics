package model.user;

import model.billing.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;

public class Cashier extends Employee implements Serializable {

	@Serial
	private static final long serialVersionUID = 1;

	private transient BillFile billFile;

	private BillFile getBillFile() {
		if (billFile == null) {
			billFile = BillFile.getInstance();
		}
		return billFile;
	}

	public Cashier(String employeeID, String username, String password, String name, LocalDate dateOfBirth,
			String phone, String email, double salary) {
		super(employeeID, username, password, name, dateOfBirth, phone, email, salary, AccessLevel.CASHIER);
	}

	public Cashier() {
		super();
	}

	public Bill generateBill(ArrayList<BillItem> items, PaymentMethod method) {
		Bill bill = new Bill((int) (Math.random() * 1000), method, this.getName());

		for (BillItem item : items)
			bill.addItem(item);

		bill.calculateTotal();
		getBillFile().saveBill(bill);
		return bill;
	}

	public ArrayList<Bill> viewBill(LocalDate date) {
		ArrayList<Bill> result = new ArrayList<>();

		for (Bill bill : getBillFile().getBills()) {
			if (bill.getTs().toLocalDate().equals(date))
				result.add(bill);
		}
		return result;
	}
}