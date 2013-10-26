package com.appfountain;

import java.util.ArrayList;
import java.util.List;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.Volley;
import com.appfountain.component.EndlessScrollActionBarActivity;
import com.appfountain.component.QuestionListAdapter;
import com.appfountain.external.GsonRequest;
import com.appfountain.external.QuestionsSource;
import com.appfountain.model.Question;
import com.appfountain.util.Common;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.widget.ListView;
import android.widget.Toast;

/*
 * 検索結果表示ページ
 * タイトル・タグ検索，カテゴリ検索
 */
public class SearchResultActivity extends EndlessScrollActionBarActivity {
	private static final String TAG = SearchResultActivity.class
			.getSimpleName();
	private final String url = Common.getApiBaseUrl(this) + "question";
	
	private ActionBarActivity self = this;
	private ListView questionListView;
	private boolean inError = false;
	private List<Question> questions = new ArrayList<Question>();
	private QuestionListAdapter questionListAdapter;
	
	private Intent intent; // 前の画面から受け取るインテント
	private int category_id; // カテゴリ検索の際に取得したカテゴリID

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_result);
		
		questionListView = (ListView) findViewById(R.id.activity_top_page_question_list);
		questionListAdapter = new QuestionListAdapter(this,
				R.layout.list_item_question, questions);
		questionListView.setAdapter(questionListAdapter);
		questionListView.setOnScrollListener(this);
		
		intent = getIntent();
		category_id = intent.getIntExtra("category_id", -1); // -1ならエラー(エラー処理書いてない)
		Log.d("category_id", Integer.toString(category_id));
		
		
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (hasNext() && !inError) {
			loadPage();
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.search_result, menu);
		return true;
	}

	protected void loadPage() {
		RequestQueue queue = Volley.newRequestQueue(this);

		int next = questions.size();
		GsonRequest<QuestionsSource> req = new GsonRequest<QuestionsSource>(
				Method.GET, url + "?category_id=" + category_id + "&count=5&next=" + next, // TODO:APIの仕様がよくわかってないので直す
				QuestionsSource.class, null, null,
				new Listener<QuestionsSource>() {
					@Override
					public void onResponse(QuestionsSource response) {
						if (response.isSuccess()) {
							if (response.getQuestions().isEmpty())
								finishLoading();
							questions.addAll(response.getQuestions());
							questionListAdapter.notifyDataSetChanged();
						} else {
							Toast.makeText(self, response.getMessage(),
									Toast.LENGTH_SHORT).show();
						}
					}
				}, new ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						showErrorMessage();
					}
				});
		queue.add(req);
	}
	
	private void showErrorMessage() {
		inError = true;

		Toast.makeText(this, "Error occurred\nSomething wrong...",
				Toast.LENGTH_SHORT).show();
	}

}
