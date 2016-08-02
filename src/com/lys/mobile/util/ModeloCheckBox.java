package com.lys.mobile.util;

public class ModeloCheckBox {
	private String name;
	private String namesub;
	private boolean selected;
	private boolean enabled;

	public ModeloCheckBox(String name, String namesub) {
		this.name = name;
		this.namesub = namesub;
		selected = false;
		enabled = true;
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

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
}
