package com.appfountain.external;

import java.util.List;

import com.appfountain.model.Question;

/**
 * {"questions":[{"valid":true,"body":"body","created":"2013-10-04T17:29:43",
 * "category_id"
 * :1,"id":1,"user_id":1,"title":"\u305f\u3044\u308d\u3064","updated"
 * :"2013-10-04T17:29:43"}], "status":true}
 */
public class QuestionsSource extends BaseSource {
	private List<Question> questions;

	public List<Question> getQuestions() {
		return questions;
	}
}
