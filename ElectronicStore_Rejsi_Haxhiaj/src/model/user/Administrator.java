package model.user;

import java.io.Serial;
import java.io.Serializable;

public class Administrator extends User implements Serializable {

	@Serial
	private static final long serialVersionUID = 1;

	public Administrator(String username, String password) {
		super(username, password);
	}

	public Administrator() {

	}

	public void updateEmployee(Employee e) {

	}

	public void deleteEmployee(String employeeID) {

	}

	public void assignRole(Employee e, AccessLevel role) {

	}

	public void revokeAccess(Employee e) {

	}
}