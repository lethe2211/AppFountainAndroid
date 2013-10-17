package com.appfountain.model;

import java.util.List;

public class UserComments {
	private final List<UserComment> questions;
	
	public UserComments(List<UserComment> questions) {
		this.questions = questions;
	}

	public List<UserComment> getQuestions() {
		return questions;
	}
}
