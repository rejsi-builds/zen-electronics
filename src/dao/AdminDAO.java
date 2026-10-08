package dao;

import model.user.Administrator;
import model.user.User;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public final class AdminDAO {

	private static final String ADMIN_FILE = "AdminLogInInfo.dat";

	public static void ensureAdminFileExists() {
		File f = new File(ADMIN_FILE);
		if (!f.exists() || f.length() == 0) {
			User defaultAdmin = new Administrator("elani", "4321");
			writeAdminCredentials(defaultAdmin);
		}
	}

	public static void writeAdminCredentials(User creds) {
		try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(ADMIN_FILE))) {
			out.writeObject(creds);
		} catch (IOException e) {
			throw new RuntimeException("Failed to write admin credentials: ", e);
		}
	}

	public static User readAdminCredentials() {
		File f = new File(ADMIN_FILE);
		if (!f.exists()) {
			throw new RuntimeException("Admin file not found: " + f.getAbsolutePath());
		}

		try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(f))) {
			Object obj = in.readObject();
			return (User) obj;
		} catch (IOException | ClassNotFoundException e) {
			throw new RuntimeException("Failed to read admin credentials: ", e);
		}
	}
}
