package model.billing;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Bill implements Serializable {
	private int billID;
	private LocalDateTime ts;
	private ArrayList<BillItem> items;
	private PaymentMethod paymentMethod;
	private String cashierName;

	public Bill(int billID, PaymentMethod paymentMethod, String cashierName) {
		this.billID = billID;
		this.paymentMethod = paymentMethod;
		this.cashierName = cashierName;
		this.ts = LocalDateTime.now();
		this.items = new ArrayList<>();
	}

	public void addItem(BillItem item) {
		items.add(item);
	}

	public double calculateTotal() {
		double total = 0;
		for (BillItem item : items) {
			total += item.getTotal();
		}
		return total;
	}

	public int getBillID() {
		return billID;
	}

	public LocalDateTime getTs() {
		return ts;
	}

	public ArrayList<BillItem> getItems() {
		return items;
	}

	public PaymentMethod getPaymentMethod() {
		return paymentMethod;
	}

	public String getCashierName() {
		return cashierName;
	}
}