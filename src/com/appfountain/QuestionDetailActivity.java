package com.appfountain;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.appfountain.component.CommentListAdapter;
import com.appfountain.component.EndlessScrollActionBarActivity;
import com.appfountain.external.CommentsSource;
import com.appfountain.external.GsonRequest;
import com.appfountain.external.UserSource;
import com.appfountain.model.Comment;
import com.appfountain.model.Question;
import com.appfountain.model.User;
import com.appfountain.model.UserContainer;
import com.appfountain.util.Common;

/**
 * 質問詳細ページ：コメント等
 */
public class QuestionDetailActivity extends EndlessScrollActionBarActivity {
	private static final String TAG = QuestionDetailActivity.class
			.getSimpleName();
	protected static final String EXTRA_QUESTION = "question_detail_extra_question";
	private static final int DEFAULT_COMMENT_GET_COUNT = 20;

	private ActionBarActivity self = this;
	private RequestQueue queue = null;
	private Question question = null;
	// 質問者のユーザ情報
	private TextView questionUserName;
	// コメント一覧
	private ListView commentList;
	private List<Comment> comments = new ArrayList<Comment>();
	private CommentListAdapter commentListAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_question_detail);

		// 遷移前画面からQuestionを受け取る
		Intent intent = getIntent();
		question = (Question) intent.getSerializableExtra(EXTRA_QUESTION);
		if (question == null)
			finish(); // 受け取りに失敗したら画面終了
		Log.d(TAG, "get question => id: " + question.getId() + ", title: "
				+ question.getTitle(10));

		initViews();

		// 通信処理
		queue = Volley.newRequestQueue(this);
		loadQuestionUser(question.getUserId());
		loadComments();
	}

	private void initViews() {
		// 質問の情報表示用View
		TextView questionTitle = (TextView) findViewById(R.id.question_detail_question_title);
		questionTitle.setText(question.getTitle());
		TextView questionCreated = (TextView) findViewById(R.id.question_detail_question_created);
		questionCreated.setText(question.getCreatedString());
		TextView questionBody = (TextView) findViewById(R.id.question_detail_question_body);
		questionBody.setText(question.getBody());
		ImageView questionCategory = (ImageView) findViewById(R.id.question_detail_question_category);
		questionCategory.setImageResource(question.getCategory()
				.getDrawableId());

		// 質問者の情報表示用View
		questionUserName = (TextView) findViewById(R.id.question_detail_quesion_user_name_value);

		// コメント情報表用View
		commentList = (ListView) findViewById(R.id.question_detail_comment_list);
		commentListAdapter = new CommentListAdapter(this,
				R.layout.list_item_comment, comments);
		commentList.setAdapter(commentListAdapter);
		commentList.setOnScrollListener(this);
	}

	// 質問者のユーザ情報表示
	private void loadQuestionUser(int userId) {
		GsonRequest<UserSource> req = new GsonRequest<UserSource>(Method.GET,
				getUserInfoUrl(userId), UserSource.class, null, null,
				new Listener<UserSource>() {
					@Override
					public void onResponse(UserSource response) {
						if (response.isSuccess() && questionUserName != null) {
							User questionUser = response.getUser();
							questionUserName.setText(questionUser.getName());
						} else {
							Toast.makeText(self, response.getMessage(),
									Toast.LENGTH_SHORT).show();
						}
					}
				}, new ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						Toast.makeText(self, error.getMessage(),
								Toast.LENGTH_SHORT).show();
					}
				});
		queue.add(req);
	}

	// コメント情報など受け取る
	private void loadComments() {
		loadPage(); // EndlessScrollListViewを使う
	}

	@Override
	protected void loadPage() {
		int next = comments.size();
		GsonRequest<CommentsSource> req = new GsonRequest<CommentsSource>(
				Method.GET, getCommentsUrl(DEFAULT_COMMENT_GET_COUNT, next),
				CommentsSource.class, null, null,
				new Listener<CommentsSource>() {
					@Override
					public void onResponse(CommentsSource response) {
						if (response.isSuccess()) {
							if (response.getComments().isEmpty())
								finishLoading();
							comments.addAll(response.getComments());
							commentListAdapter.notifyDataSetChanged();
						} else {
							Toast.makeText(self, response.getMessage(),
									Toast.LENGTH_SHORT).show();
						}
					}
				}, new ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						Toast.makeText(self,
								"Error occurred\nSomething wrong...",
								Toast.LENGTH_SHORT).show();
					}
				});
		queue.add(req);
	}

	// ユーザ情報取得するAPIのURI
	private String getUserInfoUrl(int userId) {
		return Common.getApiBaseUrl(this) + "user/info?id=" + userId;
	}

	// コメント情報取得するAPIのURI
	private String getCommentsUrl(int count, int next) {
		UserContainer uc = Common.getUserContainer(this);
		String url = Common.getApiBaseUrl(this) + "question/"
				+ question.getId() + "?count=" + count + "&next=" + next;
		if (uc != null) {
			url += "&user_id=" + uc.getId();
		}
		return url;
	}
}
