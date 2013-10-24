package com.appfountain.model;

public class Category {
	private final int id;
	private final String idName;
	private final String name;
	
	public Category(int id, String idName, String name) {
		this.id = id;
		this.idName = idName;
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public String getIdName() {
		return idName;
	}

	public String getName() {
		return name;
	}
}
