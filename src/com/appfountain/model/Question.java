package com.appfountain.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * {"valid":true,"body":"body","created":"2013-10-04T17:29:43","category_id":1,
 * "id":1,"user_id":1,"title":"\u305f\u3044\u308d\u3064","updated":
 * "2013-10-04T17:29:43"}
 */
public class Question {
	private final int id;
	private final int categoryId;
	private final int userId;
	private final String title;
	private final String body;
	private final String created;
	private Date _created = null;
	private final String updated;
	private Date _updated = null;
	private final Boolean valid;

	public Question(int id, int categoryId, int userId, String title,
			String body, String created, String updated, Boolean valid) {
		this.id = id;
		this.categoryId = categoryId;
		this.userId = userId;
		this.title = title;
		this.body = body;
		this.created = created;
		this.updated = updated;
		this.valid = valid;
	}

	public int getId() {
		return id;
	}

	public int getCategoryId() {
		return categoryId;
	}

	public int getUserId() {
		return userId;
	}

	public String getTitle() {
		return title;
	}

	public String getBody() {
		return body;
	}

	public String getCreatedString() {
		return created;
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

	public String getUpdatedString() {
		return updated;
	}

	public Date getUpdated() {
		if (_updated == null) {
			SimpleDateFormat formatter = new SimpleDateFormat(
					"yyyy-MM-dd'T'HH:mm:ssZ", Locale.JAPAN);
			String date = updated.replaceAll("\\+0([0-9]){1}\\:00", "+0$100");
			try {
				_updated = formatter.parse(date);
			} catch (ParseException e) {
				_updated = new Date();
			}
		}
		return _updated;
	}

	public Boolean getValid() {
		return valid;
	}
}
