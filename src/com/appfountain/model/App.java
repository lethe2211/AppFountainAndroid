package com.appfountain.model;

import android.graphics.drawable.Drawable;

public class App {
	private final int id;
	private final String name;
	private final String package_name;
	private final Drawable icon;

	public App(int id,String name, String packageName, Drawable icon) {
		this.id = id;
		this.name = name;
		this.package_name = packageName;
		this.icon = icon;
	}

	public String getName() {
		return name;
	}

	public String getPackageName() {
		return package_name;
	}

	public Drawable getIcon() {
		return icon;
	}
}
