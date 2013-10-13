package com.appfountain.model;

public class UserContainer {
	private final String name;
	private final String password;
	private final String rk;

	public UserContainer(String name, String password, String rk) {
		this.name = name;
		this.password = password;
		this.rk = rk;
	}

	public String getName() {
		return name;
	}

	public String getPassword() {
		return password;
	}

	public String getRk() {
		return rk;
	}
}
