package com.appfountain;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.appfountain.component.EndlessScrollActionBarActivity;
import com.appfountain.component.QuestionListAdapter;
import com.appfountain.external.GsonRequest;
import com.appfountain.external.QuestionsSource;
import com.appfountain.model.Question;
import com.appfountain.util.Common;

/*
 * 検索結果表示ページ
 * タイトル・タグ検索，カテゴリ検索
 */
public class SearchResultActivity extends EndlessScrollActionBarActivity {
	private static final String TAG = SearchResultActivity.class
			.getSimpleName();
    private static final int QUESTION_POST = 1;
    private final String url = Common.getApiBaseUrl(this) + "question";

	private ActionBarActivity self = this;
	private ListView questionListView;
	private boolean inError = false;
	private List<Question> questions = new ArrayList<Question>();
	private QuestionListAdapter questionListAdapter;

	private Intent intent; // 前の画面から受け取るインテント
	private int category_id; // カテゴリ検索の際に取得したカテゴリID
	private String query; // キーワード検索の際に取得したクエリ
    private TextView emptyText;

	// カテゴリ検索を行うときはtrue，キーワード検索を行うときはfalse
	// TODO:APIの仕様変更によってどちらも同じAPIから結果を表示するようにしたい
	private boolean isCategorySearch = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_result);

		questionListView = (ListView) findViewById(R.id.activity_top_page_question_list);
		questionListAdapter = new QuestionListAdapter(this,
				R.layout.list_item_question, questions);
		questionListView.setAdapter(questionListAdapter);
		questionListView.setOnScrollListener(this);

		// Homeボタンを押せるようにする
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		intent = getIntent();
		category_id = intent.getIntExtra("category_id", -1); // -1なら，TopPageからのリクエストにcategory_idは含まれていない(つまりキーワード検索)
		Log.d("category_id", Integer.toString(category_id));
		if (category_id == -1) {
			isCategorySearch = false;
			query = intent.getStringExtra("query");
			if (query != null)
				Log.d("query", query);
		} else {
			isCategorySearch = true;
		}

        emptyText = (TextView) findViewById(R.id.activity_search_result_empty_text);
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (hasNext() && !inError) {
			loadPage();
		}
	}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // menuボタンのいずれかがタップ
        switch (item.getItemId()) {
            case android.R.id.home:
                Log.d("home", "clicked!");
                finish();
                break;
            case R.id.top_page_move_post_question:
                // ログイン済みなら質問投稿画面へ
                if (Common.getUserContainer(this) != null) {
                    setResult(RESULT_OK);
                    finish();
                } else {
                    // TODO ログイン画面へいい感じに(メッセージつけて)遷移
                    Toast.makeText(this, "ログインしてください", Toast.LENGTH_SHORT).show();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.search_result, menu);
		return true;
	}

	protected void loadPage() {
		if (!Common.isInternetAvailable(self)) {
			Toast.makeText(self,
					getString(R.string.common_internet_unavailable),
					Toast.LENGTH_SHORT).show();
			inError = true;
			return;
		}

		RequestQueue queue = Volley.newRequestQueue(this);

		int next = questions.size();

		// FIXME:API変更までの暫定措置
		// FIXME:キーワード検索Pagingがうまく行ってないかも
		String requestUrl;
		if (isCategorySearch) {
			requestUrl = url + "?category_id=" + category_id + "&count=5&next="
					+ next;
		} else {
			if (query == null) { // FIXME:よくわかんないけどquery == null弾いとくと動く
				requestUrl = "";
				return;
			} else {
				try {
					requestUrl = url + "/search?title="
							+ URLEncoder.encode(query, "utf-8");
				} catch (UnsupportedEncodingException e) {
					requestUrl = "";
					e.printStackTrace();
				}
			}
		}
		GsonRequest<QuestionsSource> req = new GsonRequest<QuestionsSource>(
				Method.GET, requestUrl, QuestionsSource.class, null, null,
				new Listener<QuestionsSource>() {
					@Override
					public void onResponse(QuestionsSource response) {
						if (response.getQuestions().isEmpty())
							finishLoading();
						questions.addAll(response.getQuestions());
						questionListAdapter.notifyDataSetChanged();
                        if(questions.isEmpty()) {
                            emptyText.setVisibility(View.VISIBLE);
                        }
					}
				}, new ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						inError = true;
						String responseBody = null;
						try {
							responseBody = new String(
									error.networkResponse.data, "utf-8");
						} catch (Exception e) {
							e.printStackTrace();
						}
						Toast.makeText(self, responseBody, Toast.LENGTH_SHORT)
								.show();
					}
				});
		queue.add(req);
	}
}
