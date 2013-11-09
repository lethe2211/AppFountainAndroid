package com.appfountain.external;

import java.util.List;

import com.appfountain.model.UserComment;

public class UserCommentsSource extends BaseSource {
	// for success case
	private List<UserComment> questioncomments;

	public List<UserComment> getUserComments() {
		return questioncomments;
	}
}
