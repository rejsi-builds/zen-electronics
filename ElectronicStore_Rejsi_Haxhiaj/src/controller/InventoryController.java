package controller;

import java.util.List;

import dao.GenericDAO;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.billing.BillItem;
import model.inventory.*;
import model.user.Manager;

public class InventoryController {

	private final GenericDAO<Item> item;
	private final ObservableList<Item> items;

	public InventoryController() {
		this.item = new GenericDAO<>("items.dat");
		this.items = FXCollections.observableArrayList(item.getAll());
	}

	public ObservableList<Item> getItems() {
		return items;
	}

	public boolean addItem(String name, int quantity, double price, Sector sector) {
		if (name.isBlank() || quantity < 0 || price < 0 || sector == null)
			return false;

		for (Item existing : items) {
			if (existing.getName().equalsIgnoreCase(name.trim())) {
				return false;
			}
		}

		Supplier s = Supplier.getDefaultSupplier(sector);
		Item newItem = new Item(name, quantity, price, sector, s);

		boolean saved = item.create(newItem);

		if (saved) {
			items.add(newItem);
			return true;
		}

		return false;
	}

	public boolean deleteItem(Item selected) {
		if (selected == null)
			return false;

		items.remove(selected);
		item.delete(selected);

		return true;
	}

	public boolean restockItem(Item selected, int change) {
		if (selected == null)
			return false;

		int newQty = selected.getQuantity() + change;
		if (newQty < 0)
			return false;

		selected.updateStock(change);
		item.updateAll();
		return true;
	}

	public boolean applyDiscount(Item selected, double discountP) {
		if (selected == null || discountP < 0 || discountP >= 1)
			return false;

		Manager.applyDiscount(selected, discountP);
		item.updateAll();

		return true;
	}

	public boolean applySectorDiscount(Sector sector, double discountP) {
		if (sector == null || discountP < 0 || discountP >= 1)
			return false;

		for (Item i : items)
			if (i.getSector() == sector)
				Manager.applyDiscount(i, discountP);

		item.updateAll();
		return true;
	}

	public boolean processSale(List<BillItem> cartItems) {
		if (cartItems == null || cartItems.isEmpty())
			return false;

		for (BillItem bi : cartItems) {
			Item invItem = findItemById(bi.getItemId());
			if (invItem == null)
				return false;
			if (invItem.getQuantity() < bi.getQuantity())
				return false;
		}

		for (BillItem bi : cartItems) {
			Item invItem = findItemById(bi.getItemId());
			invItem.updateStock(-bi.getQuantity());
		}

		item.updateAll();
		return true;
	}

	private Item findItemById(String id) {
		if (id == null)
			return null;
		for (Item i : items) {
			if (i.getItemID() != null && i.getItemID().equalsIgnoreCase(id)) {
				return i;
			}
		}
		return null;
	}

	public void saveInventory() {
		item.updateAll();
	}

	public ObservableList<Item> searchItems(String nameQuery, Sector sector) {
		ObservableList<Item> filtered = FXCollections.observableArrayList();
		for (Item i : items) {
			boolean matchesName = nameQuery == null || i.getName().toLowerCase().contains(nameQuery.toLowerCase());
			boolean matchesSector = sector == null || i.getSector() == sector;
			if (matchesName && matchesSector)
				filtered.add(i);
		}
		return filtered;
	}

}
