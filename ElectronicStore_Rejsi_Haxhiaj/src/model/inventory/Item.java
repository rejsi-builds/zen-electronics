package model.inventory;

import java.io.Serial;
import java.io.Serializable;

public class Item implements Serializable {
	@Serial
	private static final long serialVersionUID = 1;

	private String itemID;
	private String name;
	private int quantity;
	private int minStock = 3;
	private double price;
	private Sector sector;
	private Supplier supplier;

	public Item(String name, int quantity, double price, Sector sector, Supplier supplier) {
		this.name = name;
		this.quantity = quantity;
		// this.minStock = minStock;
		this.price = price;
		this.sector = sector;
		this.supplier = supplier;
		this.itemID = generateID(sector);
	}

	private String generateID(Sector s) {
		int n = (int) (Math.random() * 9000) + 1000;
		char sectorCode = sector.name().charAt(0);

		return sectorCode + String.valueOf(n);
	}

	public void updateStock(int amount) {
		quantity += amount;
	}

	public boolean decreaseStock(int amount) {
		if (amount <= 0)
			return false;
		if (quantity - amount < 0)
			return false;
		quantity -= amount;
		return true;
	}

	public boolean isLowStock() {
		if (quantity <= minStock)
			return true;

		return false;
	}

	public String getItemID() {
		return itemID;
	}

	public String getName() {
		return name;
	}

	public int getQuantity() {
		return quantity;
	}

	public double getPrice() {
		return price;
	}

	public Sector getSector() {
		return sector;
	}

	public Supplier getSupplier() {
		return supplier;
	}

	public void setPrice(double price) {
		this.price = price;
	}
}
