package com.appfountain;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.appfountain.component.QuestionListAdapter;
import com.appfountain.external.GsonRequest;
import com.appfountain.external.QuestionsSource;
import com.appfountain.model.Question;
import com.appfountain.util.Common;

/*
 * トップページ
 */
public class TopPageActivity extends ActionBarActivity {
	private static final String TAG = TopPageActivity.class.getSimpleName();
	private final String url = Common.getApiBaseUrl(this) + "question";

	private ActionBarActivity self = this;
	private ListView questionListView;
	private boolean inError = false;
	private boolean isLast = false;
	private List<Question> questions = new ArrayList<Question>();
	private QuestionListAdapter questionListAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_top_page);

		questionListView = (ListView) findViewById(R.id.activity_top_page_question_list);
		questionListAdapter = new QuestionListAdapter(this,
				R.layout.list_item_question, questions);
		questionListView.setAdapter(questionListAdapter);
		questionListView.setOnScrollListener(new EndlessScrollListener());
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (!isLast && !inError) {
			loadQuestions();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.top_page, menu);
		return true;
	}

	private void loadQuestions() {
		RequestQueue queue = Volley.newRequestQueue(this);

		int next = questions.size();
		GsonRequest<QuestionsSource> req = new GsonRequest<QuestionsSource>(
				Method.GET, url + "?count=5&next=" + next,
				QuestionsSource.class, null, null,
				new Listener<QuestionsSource>() {
					@Override
					public void onResponse(QuestionsSource response) {
						if (response.isSuccess()) {
							if (response.getQuestions().isEmpty())
								isLast = true;
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

		Toast.makeText(this, "Error occurd\nSomething wrong...",
				Toast.LENGTH_LONG).show();
	}

	/**
	 * Scrollしていくと勝手に続きを読み込むやつ
	 */
	public class EndlessScrollListener implements OnScrollListener {
		private int visibleThreshold = 3;
		private int previousTotal = 0;
		private boolean loading = true;

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {
			if (loading) {
				if (totalItemCount > previousTotal) {
					loading = false;
					previousTotal = totalItemCount;
				}
			}
			if (!loading
					&& !isLast
					&& (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
				loadQuestions();
				loading = true;
			}
		}

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
		}
	}
}
