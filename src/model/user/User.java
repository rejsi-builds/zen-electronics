package model.user;

import java.io.Serial;
import java.io.Serializable;

public abstract class User implements Serializable {

	@Serial
	private static final long serialVersionUID = 1;

	private String username;
	private String password;

	public User(String username, String password) {
		this.username = username;
		this.password = password;
	}

	public User() {

	}

	public String getUsername() {
		return this.username;
	}

	public String getPassword() {
		return this.password;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
