package com.appfountain;

import java.util.HashMap;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.appfountain.external.CommentSource;
import com.appfountain.external.GsonRequest;
import com.appfountain.model.Question;
import com.appfountain.model.UserContainer;
import com.appfountain.util.Common;

public class CommentBodyActivity extends ActionBarActivity {
	private static final String TAG = QuestionDetailActivity.class
			.getSimpleName();
	protected static final String EXTRA_QUESTION = "question_detail_extra_question";

	private ActionBarActivity self = this;
	private UserContainer user = null;
	private RequestQueue queue = null;
	private Question question = null;

	// コメント投稿フォーム・ボタン
	private EditText commentPostEditText;
	private Button commentPostButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_comment_body);

		// Homeボタンを押せるようにする
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		user = Common.getUserContainer(this);
		if (user == null)
			finish();

		// 遷移前画面からQuestionを受け取る
		Intent intent = getIntent();
		question = (Question) intent.getSerializableExtra("EXTRA_QUESTION");

		if (question == null)
			finish(); // 受け取りに失敗したら画面終了
		Log.d(TAG, "get question => id: " + question.getId() + ", title: "
				+ question.getTitle(10));

		queue = Volley.newRequestQueue(this);

		initViews();
	}

	private void initViews() {
		// コメント投稿用フォーム
		commentPostEditText = (EditText) findViewById(R.id.comment_post_body);

		// コメント投稿用ボタン
		commentPostButton = (Button) findViewById(R.id.comment_post_body_button);
		commentPostButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				final String commentPostBody = commentPostEditText.getText()
						.toString();
				if (isValidComment(commentPostBody)) {
					// 連続でクリックされないようにClick出来なくする
					commentPostButton.setClickable(false);

					postComment(commentPostBody);
				}
			}
		});
	}

	private Boolean isValidComment(String commentPostBody) {
		return commentPostBody.length() != 0;
	}

	// コメントを投稿する
	private void postComment(String commentPostBody) {
		if (queue == null)
			queue = Volley.newRequestQueue(this);

		Map<String, String> params = new HashMap<String, String>();
		params.put("body", commentPostBody);

		Map<String, String> headers = new HashMap<String, String>();
		headers.put(Common.getPostHeader(this), user.getRk()); // POST時はrkをヘッダに付与

		GsonRequest<CommentSource> req = new GsonRequest<CommentSource>(
				Method.POST, Common.getApiBaseUrl(this) + "question/"
						+ question.getId(), CommentSource.class, params,
				headers, new Listener<CommentSource>() {
					@Override
					public void onResponse(CommentSource response) {
						Log.d("comment_post", "posted");

						// 投稿後の処理
						commentPostEditText.setText("");
						commentPostButton.setClickable(true);
						Toast.makeText(self, "コメントが投稿されました", Toast.LENGTH_SHORT)
								.show();
						setResult(RESULT_OK);
						finish();
					}
				}, new ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						commentPostEditText.setText("");
						commentPostButton.setClickable(true);
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
