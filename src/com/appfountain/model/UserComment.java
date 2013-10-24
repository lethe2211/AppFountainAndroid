package com.appfountain.model;

import java.util.List;

public class UserComment {
	private final Question question;
	private final List<Comment> comments;

	public UserComment(Question question, List<Comment> comments) {
		this.question = question;
		this.comments = comments;
	}

	public Question getQuestion() {
		return question;
	}

	public List<Comment> getComments() {
		return comments;
	}
}
