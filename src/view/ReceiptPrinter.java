package view;

import java.time.format.DateTimeFormatter;

import model.billing.Bill;
import model.billing.BillItem;

public class ReceiptPrinter {

	public static void print(Bill bill) {
		DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

		System.out.println("===== RECEIPT =====");
		System.out.println("Bill ID: " + bill.getBillID());
		System.out.println("Time: " + bill.getTs().format(fmt));
		System.out.println("-----------------------------------------------");

		double grandTotal = 0;
		for (BillItem item : bill.getItems()) {
			double itemTotal = item.getTotal();
			grandTotal += itemTotal;
			double unitPrice = itemTotal / item.getQuantity();

			System.out.printf("%-15s | %3d * %7.2f | %10.2f%n", item.getProductName(), item.getQuantity(), unitPrice,
					itemTotal);
		}
		System.out.println("------------------------------------------------");
		System.out.printf("%-15s %24.2f%n", "TOTAL", grandTotal);
		System.out.println("Payment: " + bill.getPaymentMethod());
		System.out.println("===============================================");
	}
}
