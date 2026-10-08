package model.performance;

import model.user.Employee;
import model.billing.*;

public class EmployeePerformance {

	private Employee employee;
	private int totalBills;
	private int totalItemsSold;
	private double totalRevenue;

	public EmployeePerformance(Employee employee) {
		this.employee = employee;
		this.totalBills = totalBills;
		this.totalItemsSold = totalItemsSold;
		this.totalRevenue = totalRevenue;
	}

	public void updateEmployeePerformance(Bill b) {
		totalBills++;
		totalItemsSold += b.getItems().size();
		totalRevenue += b.calculateTotal();
	}

	public void resetRanking() {
		totalBills = 0;
		totalItemsSold = 0;
		totalRevenue = 0;
	}

	public String getEmployeeID() {
		return employee.getEmployeeID();
	}

	public String getName() {
		return employee.getName();
	}

	public int getTotalBills() {
		return totalBills;
	}

	public int getTotalItemsSold() {
		return totalItemsSold;
	}

	public double getTotalRevenue() {
		return totalRevenue;
	}

	public double getScore() {
		return totalRevenue;
	}

	public Employee getEmployee() {
		return employee;
	}
}
