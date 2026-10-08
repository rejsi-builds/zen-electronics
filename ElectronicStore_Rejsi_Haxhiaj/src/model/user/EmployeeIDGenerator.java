package model.user;

import java.security.SecureRandom;

import dao.EmployeeDAO;

public final class EmployeeIDGenerator {
	private static final SecureRandom RAND = new SecureRandom();

	private EmployeeIDGenerator() {
	}

	public static String generate(AccessLevel role) {
		char prefix = (role == AccessLevel.CASHIER) ? 'C' : 'M';

		while (true) {
			int n = RAND.nextInt(10_000);
			String digits = String.format("%04d", n);
			String id = prefix + digits;

			if (!EmployeeDAO.idExists(id)) {
				return id;
			}
		}
	}

	public static String changePrefix(String oldId, AccessLevel newRole) {
		if (oldId == null || oldId.length() < 2) {
			throw new IllegalArgumentException("Invalid employee ID: " + oldId);
		}

		char newPrefix = (newRole == AccessLevel.CASHIER) ? 'C' : 'M';

		String digits = oldId.substring(1);
		return newPrefix + digits;
	}
}
