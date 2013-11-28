package com.appfountain.model;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.text.format.DateFormat;
import android.util.Log;

import com.appfountain.util.Common;

/**
 * {"valid":true,"body":"body","created":"2013-10-04T17:29:43","category_id":1,
 * "id":1,"user_id":1,"title":"\u305f\u3044\u308d\u3064","updated":
 * "2013-10-04T17:29:43"}
 */
// 画面遷移時の値渡しに使うため，Serialziableを実装
public class Question implements Serializable {
	private static final long serialVersionUID = -1472475592153424625L;

	private final int id;
	private final int category_id;
	private Category _category = null;
	private final int user_id;
	private final String title;
	private final String body;
	private final Boolean finished;
	private final int comment_count;
	private final String created;
	private Date _created = null;
	private final String updated;
	private Date _updated = null;

	public Question(int id, int categoryId, int userId, String title,
			String body, Boolean finished,int comment_count, String created, String updated) {
		this.id = id;
		this.category_id = categoryId;
		this.user_id = userId;
		this.title = title;
		this.body = body;
		this.finished = finished;
		this.comment_count = comment_count;
		this.created = created;
		this.updated = updated;
	}

	public int getId() {
		return id;
	}

	public int getCategoryId() {
		return category_id;
	}

	public Category getCategory() {
		if (_category == null) {
			_category = Common.getCategory(category_id);
		}
		return _category;
	}

	public int getUserId() {
		return user_id;
	}

	public String getTitle() {
		return title;
	}

	public String getTitle(int maxTitleSize) {
		if (title.length() > maxTitleSize)
			return title.substring(0, maxTitleSize);
		return title;
	}

	public String getBody() {
		return body;
	}

	public String getBody(int maxBodySize) {
		if (body.length() > maxBodySize)
			return body.substring(0, maxBodySize);
		return body;
	}

	public Boolean isFinished() {
		return finished;
	}

	public int getCommentCount() {
		return comment_count;
	}

	public CharSequence getCreatedString() {
		return DateFormat.format("yyyy/MM/dd kk:mm", getCreated());
	}

	public Date getCreated() {
		if (_created == null) {
			SimpleDateFormat formatter = new SimpleDateFormat(
					"yyyy-MM-dd'T'HH:mm:ss", Locale.JAPAN);
			String date = created.replaceAll("\\+0([0-9]){1}00", "");
			try {
				_created = formatter.parse(date);
			} catch (ParseException e) {
				_created = new Date();
			}
		}
		return _created;
	}

	public CharSequence getUpdatedString() {
		return DateFormat.format("yyyy/MM/dd kk:mm", getUpdated());
	}

	public Date getUpdated() {
		if (_updated == null) {
			SimpleDateFormat formatter = new SimpleDateFormat(
					"yyyy-MM-dd'T'HH:mm:ss", Locale.JAPAN);
			String date = updated.replaceAll("\\+0([0-9]){1}00", "");
			try {
				_updated = formatter.parse(date);
			} catch (ParseException e) {
				_updated = new Date();
			}
		}
		return _updated;
	}
}
