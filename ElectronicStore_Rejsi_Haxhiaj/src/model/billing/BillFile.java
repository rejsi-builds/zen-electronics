package model.billing;

import java.io.*;
import java.util.ArrayList;

public class BillFile implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;

	private static BillFile instance;

	private static ArrayList<Bill> bills = new ArrayList<>();

	private static final String BASE_DIR = System.getProperty("user.dir") + File.separator + "bills";
	private static final String FOLDER = BASE_DIR + File.separator + "bills";
	private static final String DATA_FILE = FOLDER + File.separator + "data.dat";

	private BillFile() {
		File folder = new File(FOLDER);
		if (!folder.exists()) {
			folder.mkdirs();
		}
		loadBills();
	}

	public static BillFile getInstance() {
		if (instance == null) {
			instance = new BillFile();
		}
		return instance;
	}

	public void saveBill(Bill bill) {
		bills.add(bill);
		saveData();
		saveToTextFile(bill);
	}

	public ArrayList<Bill> getBills() {
		return bills;
	}

	private void saveData() {
		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
			oos.writeObject(bills);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void loadBills() {
		File file = new File(DATA_FILE);

		if (!file.exists()) {
			bills = new ArrayList<>();
			return;
		}

		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {

			Object o = ois.readObject();
			if (o instanceof ArrayList<?>) {
				bills = (ArrayList<Bill>) o;
			} else {
				bills = new ArrayList<>();
			}

		} catch (IOException | ClassNotFoundException e) {
			bills = new ArrayList<>();
		}
	}

	private void saveToTextFile(Bill bill) {
		String filename = FOLDER + File.separator + "Receipt_" + bill.getBillID() + ".txt";

		try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
			writer.println("====================================");
			writer.println("ZEN ELECTRONICS RECEIPT");
			writer.println("====================================");
			writer.println("Bill ID: " + bill.getBillID());
			writer.println("Cashier: " + bill.getCashierName());
			writer.println("Total: €" + String.format("%.2f", bill.calculateTotal()));
			writer.println("====================================");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
