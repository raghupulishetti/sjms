package com.sjms.model;

public enum JobPriority {

	LOW(1), MEDIUM(2), HIGH(3);

	private int value;

	JobPriority(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

}
