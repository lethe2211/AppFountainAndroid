package com.appfountain;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.appfountain.component.CommentListAdapter;
import com.appfountain.component.EndlessScrollActionBarActivity;
import com.appfountain.component.QuestionAppAdapter;
import com.appfountain.external.AppsSource;
import com.appfountain.external.BitmapLruCache;
import com.appfountain.external.CommentSource;
import com.appfountain.external.CommentsSource;
import com.appfountain.external.GsonRequest;
import com.appfountain.external.QuestionSource;
import com.appfountain.external.UserSource;
import com.appfountain.model.App;
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
	private UserContainer user = null;
	private RequestQueue queue = null;
	private Question question = null;
	// 質問に付与されたアプリ情報
	private ListView appList;
	private List<App> apps = new ArrayList<App>();
	private QuestionAppAdapter appAdapter;
	// 質問者のユーザ情報
	private TextView questionUserName;
	// コメント一覧
	private ListView commentList;
	private List<Comment> comments = new ArrayList<Comment>();
	private CommentListAdapter commentListAdapter;
	// コメント投稿ボタン
	private Button commentPostButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_question_detail);

		// Homeボタンを押せるようにする
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		user = Common.getUserContainer(this);
		if (user == null)
			finish();

		// 遷移前画面からQuestionを受け取る
		Intent intent = getIntent();
		question = (Question) intent.getSerializableExtra(EXTRA_QUESTION);
		if (question == null)
			finish(); // 受け取りに失敗したら画面終了
		Log.d(TAG, "get question => id: " + question.getId() + ", title: "
				+ question.getTitle(10));

		queue = Volley.newRequestQueue(this);
		initViews();

		// 通信処理
		loadQuestionRelation(question);
		loadQuestionUser(question.getUserId());
		loadComments();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		// Homeボタンが押されたら戻る
		case android.R.id.home:
			Log.d("home", "clicked!");
			finish();
			break;

		}
		return false;
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

		// 関連app情報
		appList = (ListView) findViewById(R.id.question_detail_question_apps);
		// Use 1/16th of the available memory for this memory cache.
		int memClass = ((ActivityManager) this
				.getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass();
		int cacheSize = 1024 * 1024 * memClass / 16;
		appAdapter = new QuestionAppAdapter(this,
				R.layout.list_item_question_app, apps, new ImageLoader(queue,
						new BitmapLruCache(cacheSize)));
		appList.setAdapter(appAdapter);
		appList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				App selectedApp = apps.get(position);
				String packageName = selectedApp.getPackageName();
				try {
					startActivity(new Intent(Intent.ACTION_VIEW, Uri
							.parse("market://details?id=" + packageName)));
				} catch (android.content.ActivityNotFoundException anfe) {
					startActivity(new Intent(
							Intent.ACTION_VIEW,
							Uri.parse("http://play.google.com/store/apps/details?id="
									+ packageName)));
				}
			}
		});
		// 質問者の情報表示用View
		questionUserName = (TextView) findViewById(R.id.question_detail_quesion_user_name_value);

		// コメント情報表用View
		commentList = (ListView) findViewById(R.id.question_detail_comment_list);
		commentListAdapter = new CommentListAdapter(this,
				R.layout.list_item_comment, comments, question,
				isQuestionAuthor(Common.getUserContainer(this),
						question.getUserId()));
		commentList.setAdapter(commentListAdapter);
		commentList.setOnScrollListener(this);

		// コメント投稿用フォーム

		// コメント投稿用Button
		commentPostButton = (Button) findViewById(R.id.comment_post_button);
		commentPostButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.d("comment_post_button", "clicked");
				final String commentPostBody = "hogehogehoge";
				postComment(commentPostBody);
			}
		});
	}

	private Boolean isQuestionAuthor(UserContainer userContainer, int userId) {
		return userContainer != null && userContainer.getId() == userId;
	}

	// 質問者のユーザ情報表示
	private void loadQuestionRelation(Question q) {
		GsonRequest<AppsSource> req = new GsonRequest<AppsSource>(Method.GET,
				getQuestionRelationUrl(q.getId()), AppsSource.class, null,
				null, new Listener<AppsSource>() {
					@Override
					public void onResponse(AppsSource response) {
						List<App> applications = response.getApplications();
						if (applications != null && !applications.isEmpty()) {
							apps.addAll(applications);
							appAdapter.notifyDataSetChanged();
						}
					}
				}, new ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						try {
							String responseBody = new String(
									error.networkResponse.data, "utf-8");
							Toast.makeText(self, responseBody,
									Toast.LENGTH_SHORT).show();
						} catch (UnsupportedEncodingException e) {
						}
					}
				});
		queue.add(req);
	}

	private String getQuestionRelationUrl(int id) {
		return Common.getApiBaseUrl(this) + "question/" + id + "/apps";
	}

	// 質問者のユーザ情報表示
	private void loadQuestionUser(int userId) {
		GsonRequest<UserSource> req = new GsonRequest<UserSource>(Method.GET,
				getUserInfoUrl(userId), UserSource.class, null, null,
				new Listener<UserSource>() {
					@Override
					public void onResponse(UserSource response) {
						User questionUser = response.getUser();
						questionUserName.setText(questionUser.getName());
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
						if (response.getComments().isEmpty())
							finishLoading();
						comments.addAll(response.getComments());
						commentListAdapter.notifyDataSetChanged();
					}
				}, new ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						try {
							String responseBody = new String(
									error.networkResponse.data, "utf-8");
							Toast.makeText(self, responseBody,
									Toast.LENGTH_SHORT).show();
						} catch (UnsupportedEncodingException e) {
						}
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

	private void postComment(String commentPostBody) {
		if (queue == null)
			queue = Volley.newRequestQueue(this);

		Map<String, String> params = new HashMap<String, String>();
		params.put("body", commentPostBody);

		Map<String, String> headers = new HashMap<String, String>();
		headers.put(Common.getPostHeader(this), user.getRk()); // POST時はrkをヘッダに付与

		GsonRequest<CommentSource> req = new GsonRequest<CommentSource>(
				Method.POST, Common.getApiBaseUrl(this) + "question/" + "40",
				CommentSource.class, params, headers,
				new Listener<CommentSource>() {
					@Override
					public void onResponse(CommentSource response) {
						Log.d("comment_post", "posted");
						comments.add(response.getComment());
						commentListAdapter.notifyDataSetChanged();
					}
				}, new ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
//						try {
//							String responseBody = new String(
//									error.networkResponse.data, "utf-8");
//							Toast.makeText(self, responseBody,
//									Toast.LENGTH_SHORT).show();
//						} catch (UnsupportedEncodingException e) {
//						}
					}
				});

		queue.add(req);
	}
}
