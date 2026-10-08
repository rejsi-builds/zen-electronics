package model.billing;

public class SalesSummary {
	private String dateRange;
	private String cashierName;
	private int nrOfBills;
	private double totalRevenue;

	public SalesSummary(String dateRange, String cashierName, int nrOfBills, double totalRevenue) {
		this.dateRange = dateRange;
		this.cashierName = cashierName;
		this.nrOfBills = nrOfBills;
		this.totalRevenue = totalRevenue;
	}

	public void addBill(double amount) {
		this.nrOfBills++;
		this.totalRevenue += amount;
	}

	public String getDateRange() {
		return dateRange;
	}

	public String getCashierName() {
		return cashierName;
	}

	public int getNrOfBills() {
		return nrOfBills;
	}

	public double getTotalRevenue() {
		return totalRevenue;
	}
}