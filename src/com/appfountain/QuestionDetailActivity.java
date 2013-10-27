package com.appfountain;

import com.appfountain.model.Question;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;

/**
 * 質問詳細ページ：コメント等
 */
public class QuestionDetailActivity extends ActionBarActivity {
	private static final String TAG = QuestionDetailActivity.class
			.getSimpleName();
	protected static final String EXTRA_QUESTION = "question_detail_extra_question";

	private Question question = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_question_detail);
		
		// 遷移前画面からQuestionを受け取る
		Intent intent = getIntent();
		question = (Question)intent.getSerializableExtra(EXTRA_QUESTION);
		if (question == null)
			finish(); // 受け取りに失敗したら画面終了
		
		Log.d(TAG, "get question => id: " + question.getId() + ", title: " + question.getTitle(10));
		// コメント情報など受け取る
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.question_detail, menu);
		return true;
	}

}
