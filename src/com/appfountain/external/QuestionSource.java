package com.appfountain.external;

import java.util.List;
import com.appfountain.model.App;

import com.appfountain.model.Question;

/**
 * {"question":{"valid":true,"body":"body","created":"2013-10-04T17:29:43",
 * "category_id"
 * :1,"id":1,"user_id":1,"title":"\u305f\u3044\u308d\u3064","updated"
 * :"2013-10-04T17:29:43"}, "status":true}
 */
public class QuestionSource extends BaseSource {
	private Question question;
	private List<App> applications;

	public Question getQuestion() {
		return question;
	}

	public List<App> getApplications() {
		return applications;
	}
}
