package com.appfountain.model;

/**
 * 端末に保管しておくUserクラス
 */
public class UserContainer {
	private final int id;
	private final String name;
	private final String password;
	private final String rk;

	public UserContainer(int id, String name, String password, String rk) {
		this.id = id;
		this.name = name;
		this.password = password;
		this.rk = rk;
	}

	public int getId() {
		return id;
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
