package com.appfountain.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.text.format.DateFormat;

/**
 * "created":"2013-10-04T17:23:11","invalid_count":0,
 * "name":"aaaaaa","useful_count":0,"down":0,"up":0,
 * "rk":"_ZywdneWS5kDr1zY5vN6XEIDB9WFO0f7","id":1
 * 
 * APIとの通信用に用いるUserクラス
 */
public class User {
	private final int id;
	private final String name;
	private final String rk;
	private final String created;
	private Date _created = null;
	private int up;
	private int useful_count;
	private int question_count;
	private int comment_count;

	public User(int id, String name, String rk, String created, int up,
			int usefulCount) {
		this.id = id;
		this.name = name;
		this.rk = rk;
		this.created = created;
		this.setUp(up);
		this.setUsefulCount(usefulCount);
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getRk() {
		return rk;
	}

	public CharSequence getCreatedString() {
		return DateFormat.format("yyyy/MM/dd kk:mm", getCreated());
	}

	public Date getCreated() {
		if (_created == null) {
			SimpleDateFormat formatter = new SimpleDateFormat(
					"yyyy-MM-dd'T'HH:mm:ssZ", Locale.JAPAN);
			String date = created.replaceAll("\\+0([0-9]){1}\\:00", "+0$100");
			try {
				_created = formatter.parse(date);
			} catch (ParseException e) {
				_created = new Date();
			}
		}
		return _created;
	}

	public int getUp() {
		return up;
	}

	public void setUp(int up) {
		this.up = up;
	}

	public int getUsefulCount() {
		return useful_count;
	}

	public void setUsefulCount(int usefulCount) {
		this.useful_count = usefulCount;
	}

	public int getQuestionCount() {
		return question_count;
	}

	public int getCommentCount() {
		return comment_count;
	}
}
