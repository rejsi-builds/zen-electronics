package model.user;

import java.io.Serializable;

public class CredentialsRecord implements Serializable {

	private String username;
	private String password;

	public CredentialsRecord(String username, String password) {
		this.username = username;
		this.password = password;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

}
