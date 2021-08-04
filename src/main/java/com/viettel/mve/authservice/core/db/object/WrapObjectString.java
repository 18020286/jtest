package com.viettel.mve.authservice.core.db.object;

public class WrapObjectString {
	private String text;

	public WrapObjectString() {
	}

	public WrapObjectString(String text) {
		this.text = text;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	@Override
	public String toString() {
		return text;
	}

}
