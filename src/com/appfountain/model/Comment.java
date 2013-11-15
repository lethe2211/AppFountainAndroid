package com.appfountain.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.text.format.DateFormat;

/**
 * 
 * {"up":0,"useful":0,"user_id": 1,
 * "user_name":"aaaaaa","body":"bodddddddddddddd", "created"
 * :"2013-10-05T06:34:03","question_id":1,"updated":"2013-10-05T06:34:03"
 * ,"id":3,"down":0}
 */
public class Comment {
	private final int id;
	private final int question_id;
	private final int user_id;
	private final String user_name;
	private final String body;
	private final int refer_comment_id;
	private final String refer_comment_user_name;
	private final String created;
	private Date _created = null;
	private final String updated;
	private Date _updated = null;
	private final int up;
	private final int down;
	private Boolean useful;
	private String evaluation;
	
	public Comment(int id, int questionId, int userId, String userName,
			String body, int referCommentId, String referCommentUserName, String created, String updated, int up, int down,
			Boolean useful, String evaluation) {
		this.id = id;
		this.question_id = questionId;
		this.user_id = userId;
		this.user_name = userName;
		this.body = body;
		this.refer_comment_id = referCommentId;
		this.refer_comment_user_name = referCommentUserName;
		this.created = created;
		this.updated = updated;
		this.up = up;
		this.down = down;
		this.useful = useful;
		this.evaluation = evaluation;
	}

	public int getId() {
		return id;
	}

	public int getQuestionId() {
		return question_id;
	}

	public int getUserId() {
		return user_id;
	}

	public String getUserName() {
		return user_name;
	}

	public String getBody() {
		return body;
	}

	public String getBody(int maxBodySize) {
		if (body.length() > maxBodySize)
			return body.substring(0, maxBodySize);
		return body;
	}

	public int getReferCommentId() {
		return refer_comment_id;
	}

	public String getReferCommentUserName() {
		return refer_comment_user_name;
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

	public CharSequence getUpdatedString() {
		return DateFormat.format("yyyy/MM/dd kk:mm", getUpdated());
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

	public int getUp() {
		return up;
	}

	public int getDown() {
		return down;
	}

	public Boolean isUseful() {
		return useful;
	}

	public Boolean isUpEvaluation() {
		return evaluation != null && evaluation.equals("up");
	}

	public void evaluate() {
		if (isUpEvaluation()) {
			evaluation = "none";
		} else {
			evaluation = "up";
		}
	}

	public void usefulEvaluate() {
		this.useful = !useful;
	}

	public boolean isReply() {
		return refer_comment_id != 0;
	}
}
