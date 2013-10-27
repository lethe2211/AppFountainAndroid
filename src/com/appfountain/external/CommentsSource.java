package com.appfountain.external;

import java.util.List;

import com.appfountain.model.Comment;

/**
 * 質問についてるコメント一覧取得するやつ
 * {"comments":[], "status": true}
 */
public class CommentsSource extends BaseSource {
	private List<Comment> comments;

	public List<Comment> getComments() {
		return comments;
	}
}
