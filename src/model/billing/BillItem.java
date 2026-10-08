package model.billing;

import java.io.Serializable;

public class BillItem implements Serializable {
	private String itemId;
	private String productName;
	private double price;
	private int quantity;

	public BillItem(String itemId, String productName, double price, int quantity) {
		this.itemId = itemId;
		this.productName = productName;
		this.price = price;
		this.quantity = quantity;
	}

	public double getTotal() {
		return price * quantity;
	}

	public String getProductName() {
		return productName;
	}

	public String getItemId() {
		return itemId;
	}

	public double getPrice() {
		return price;
	}

	public int getQuantity() {
		return quantity;
	}
}