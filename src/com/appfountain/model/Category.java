package com.appfountain.model;

import java.io.Serializable;

public class Category implements Serializable{
	private static final long serialVersionUID = 4647914187937336355L;
	
	private final int id;
	private final String idName;
	private final String name;
	private final int drawableId;
	
	public Category(int id, String idName, String name, int drawableId) {
		this.id = id;
		this.idName = idName;
		this.name = name;
		this.drawableId = drawableId;
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

	public int getDrawableId() {
		return drawableId;
	}
}
