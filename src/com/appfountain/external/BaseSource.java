package com.appfountain.external;

class BaseSource {
	private Boolean status;
	
	// for failure case
	private int code;
	private String message;
	
	public Boolean getStatus() {
		return status;
	}
	
	public int getCode() {
		return code;
	}
	
	public String getMessage() {
		return message;
	}
}
