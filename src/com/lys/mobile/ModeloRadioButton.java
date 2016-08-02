package com.lys.mobile;

public class ModeloRadioButton {
	private String name;
	private String namesub;

	public ModeloRadioButton(String name, String namesub) {
		this.name = name;
		this.namesub = namesub;
	}

	public String getName() {
		return name;
	}

	public String getNameSub() {
		return namesub;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setNameSub(String namesub) {
		this.namesub = namesub;
	}
}
