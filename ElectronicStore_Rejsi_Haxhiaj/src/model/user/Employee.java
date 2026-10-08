package model.user;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

public class Employee extends User implements Serializable {

	@Serial
	private static final long serialVersionUID = 1;

	private String employeeID;
	private String name;
	private LocalDate dateOfBirth;
	private String phone;
	private String email;
	private double salary;
	private AccessLevel role;

	public Employee(String employeeID, String username, String password, String name, LocalDate dateOfBirth,
			String phone, String email, double salary, AccessLevel role) {
		super(username, password);
		this.employeeID = employeeID;
		this.name = name;
		this.dateOfBirth = dateOfBirth;
		this.phone = phone;
		this.email = email;
		this.salary = salary;
		this.role = role;
	}

	public Employee() {

	}

	public String getEmployeeID() {
		return employeeID;
	}

	public String getName() {
		return this.name;
	}

	public LocalDate getDateOfBirth() {
		return this.dateOfBirth;
	}

	public String getPhone() {
		return this.phone;
	}

	public String getEmail() {
		return this.email;
	}

	public double getSalary() {
		return this.salary;
	}

	public AccessLevel getRole() {
		return this.role;
	}

	public void setEmployeeID(String employeeID) {
		this.employeeID = employeeID;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setDateOfBirth(LocalDate dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setSalary(double salary) {
		this.salary = salary;
	}

	public void setRole(AccessLevel role) {
		this.role = role;
	}

}