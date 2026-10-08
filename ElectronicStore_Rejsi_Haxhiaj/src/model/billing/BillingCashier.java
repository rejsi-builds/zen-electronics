package model.billing;

import java.time.LocalDate;
import java.util.ArrayList;

public class BillingCashier {
	private BillFile billFile = BillFile.getInstance();

	public BillingCashier() {
	}

	public Bill generateBill(ArrayList<BillItem> items, PaymentMethod method, String cashierName) {
		Bill bill = new Bill((int) (Math.random() * 1000), method, cashierName);
		for (BillItem item : items) {
			bill.addItem(item);
		}
		bill.calculateTotal();
		billFile.saveBill(bill);
		return bill;
	}

	public ArrayList<Bill> getAllBills() {
		return billFile.getBills();
	}

	public ArrayList<Bill> viewBill(LocalDate date) {
		ArrayList<Bill> result = new ArrayList<>();
		for (Bill bill : billFile.getBills()) {
			if (bill.getTs().toLocalDate().equals(date)) {
				result.add(bill);
			}
		}
		return result;
	}
}