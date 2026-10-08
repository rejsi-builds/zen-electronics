package dao;

import model.user.Employee;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public final class EmployeeDAO {

	private static final String EMPLOYEE_FILE = "EmployeeFile.dat";

	public static void ensureEmployeeFileExists() {
		File f = new File(EMPLOYEE_FILE);
		if (f.exists() && f.length() > 0)
			return;

		try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(f))) {
			out.writeObject(new ArrayList<Employee>());
		} catch (IOException e) {
			throw new RuntimeException("Cannot create Employee file: " + EMPLOYEE_FILE, e);
		}
	}

	private EmployeeDAO() {
	}

	public static List<Employee> loadAll() {
		File f = new File(EMPLOYEE_FILE);
		if (!f.exists() || f.length() == 0)
			return new ArrayList<>();

		try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(f))) {
			Object obj = in.readObject();
			if (obj instanceof List<?>)
				return (List<Employee>) obj;
			return new ArrayList<>();
		} catch (Exception e) {
			return new ArrayList<>();
		}
	}

	public static void saveAll(List<Employee> employees) {
		try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(EMPLOYEE_FILE))) {
			out.writeObject(employees);
		} catch (IOException e) {
			throw new RuntimeException("Failed to write EmployeeFile", e);
		}
	}

	public static boolean usernameExists(String username) {
		return loadAll().stream().anyMatch(e -> e.getUsername().equalsIgnoreCase(username));
	}

	public static boolean idExists(String id) {
		return loadAll().stream().anyMatch(e -> e.getEmployeeID().equalsIgnoreCase(id));
	}

	public static boolean idExistsExcept(String id, String currentId) {
		return loadAll().stream().anyMatch(e -> e.getEmployeeID() != null && e.getEmployeeID().equalsIgnoreCase(id)
				&& (currentId == null || !e.getEmployeeID().equalsIgnoreCase(currentId)));
	}

}
