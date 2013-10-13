package com.appfountain.external;


/**
 *{"status":true,
 * "user":{
 * 		"created":"2013-10-04T17:23:11","invalid_count":0,
 * 		"name":"aaaaaa","useful_count":0,"down":0,"up":0,
 * 		"rk":"_ZywdneWS5kDr1zY5vN6XEIDB9WFO0f7","id":1}}
 */
public class UserSource {
	private Boolean status;
	
	// for success case
	private com.appfountain.model.User user;
	
	// for failure case
	private int code;
	private String message;
	
	public Boolean getStatus() {
		return status;
	}
	
	public com.appfountain.model.User getUser() {
		return user;
	}
	
	public int getCode() {
		return code;
	}
	
	public String getMessage() {
		return message;
	}
}
