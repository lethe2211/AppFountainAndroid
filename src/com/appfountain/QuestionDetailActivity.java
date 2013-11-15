package com.appfountain;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.appfountain.component.CommentListAdapter;
import com.appfountain.component.EndlessScrollActionBarActivity;
import com.appfountain.component.QuestionAppAdapter;
import com.appfountain.external.AppsSource;
import com.appfountain.external.BitmapLruCache;
import com.appfountain.external.CommentsSource;
import com.appfountain.external.GsonRequest;
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
public class QuestionDetailActivity extends ActionBarActivity implements
		OnScrollListener {
	private static final String TAG = QuestionDetailActivity.class
			.getSimpleName();
	protected static final String EXTRA_QUESTION = "question_detail_extra_question";
	private static final int DEFAULT_COMMENT_GET_COUNT = 200;
	private static final int COMMENT_POST = 1;

	private ActionBarActivity self = this;
	private UserContainer user = null;
	private RequestQueue queue = null;
	private Question question = null;
	private ImageLoader imageLoader;
	// 質問に付与されたアプリ情報
	private LinearLayout appList;
	// 質問者のユーザ情報
	private TextView questionUserName;
	// コメント一覧
	private ListView commentListWithQuestionDetailHeader;
	private List<Comment> comments = new ArrayList<Comment>();
	private CommentListAdapter commentListAdapter;
	// コメント投稿ボタン
	private ImageButton commentPostButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_question_detail);

		// Homeボタンを押せるようにする
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		user = Common.getUserContainer(this);

		// 遷移前画面からQuestionを受け取る
		Intent intent = getIntent();
		question = (Question) intent.getSerializableExtra(EXTRA_QUESTION);
		if (question == null)
			finish(); // 受け取りに失敗したら画面終了
		Log.d(TAG, "get question => id: " + question.getId() + ", title: "
				+ question.getTitle(10));

		queue = Volley.newRequestQueue(this);
		initViews();

		// Use 1/16th of the available memory for this memory cache.
		int memClass = ((ActivityManager) this
				.getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass();
		int cacheSize = 1024 * 1024 * memClass / 16;
		imageLoader = new ImageLoader(queue, new BitmapLruCache(cacheSize));

		// 通信処理
		loadQuestionRelation(question);
		loadQuestionUser(question.getUserId());
		loadPage();
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
		// コメント情報表用View + 質問詳細をheaderにつける
		commentListWithQuestionDetailHeader = (ListView) findViewById(R.id.question_detail_comment_list);

		// 質問詳細(header)
		LayoutInflater myinflater = (LayoutInflater) getApplicationContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout questionDetailContainer = (LinearLayout) myinflater
				.inflate(R.layout.header_activity_question_detail, null);
		setQuestionDetail(questionDetailContainer, question);
		commentListWithQuestionDetailHeader
				.addHeaderView(questionDetailContainer);

		// コメント一覧
		commentListAdapter = new CommentListAdapter(this,
				R.layout.list_item_comment, comments, question,
				isQuestionAuthor(Common.getUserContainer(this),
						question.getUserId()));
		commentListWithQuestionDetailHeader.setAdapter(commentListAdapter);
		commentListWithQuestionDetailHeader.setOnScrollListener(this);

		commentPostButton = (ImageButton) findViewById(R.id.comment_post_button);
		commentPostButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.d("comment_post_button", "clicked");
				if (user == null) { // ログインしていないならコメントできない
					// TODO ログイン画面へいい感じに(メッセージつけて)遷移
					Toast.makeText(self, "ログインしてください", Toast.LENGTH_SHORT)
							.show();
				} else {
					Intent intent = new Intent(self, CommentBodyActivity.class);
					intent.putExtra("EXTRA_QUESTION", question);
					intent.putExtra("EXTRA_QUESTION_USER_NAME", questionUserName.getText());
					startActivityForResult(intent, COMMENT_POST);
				}
			}
		});

	}

	private void setQuestionDetail(LinearLayout questionDetailContainer,
			Question question) {
		// 質問の情報表示用View
		((TextView) questionDetailContainer
				.findViewById(R.id.question_detail_question_title))
				.setText(question.getTitle());
		((TextView) questionDetailContainer
				.findViewById(R.id.question_detail_question_created))
				.setText(question.getCreatedString());
		((TextView) questionDetailContainer
				.findViewById(R.id.question_detail_question_body))
				.setText(question.getBody());
		((ImageView) questionDetailContainer
				.findViewById(R.id.question_detail_question_category))
				.setImageResource(question.getCategory().getDrawableId());

		// 関連app情報
		appList = (LinearLayout) questionDetailContainer
				.findViewById(R.id.question_detail_question_apps);

		// 質問者の情報表示用View
		questionUserName = (TextView) questionDetailContainer
				.findViewById(R.id.question_detail_quesion_user_name_value);
	}

	private Boolean isQuestionAuthor(UserContainer userContainer, int userId) {
		return userContainer != null && userContainer.getId() == userId;
	}

	// startActivityForResultで呼ばれたActivityが停止した際に呼ばれる
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
		case COMMENT_POST:
			// コメント投稿後は再読み込みさせる
			if (resultCode == RESULT_OK) { // 起動先のActivityでsetResult(RESULT_OK)が呼ばれていたら
				comments.clear();
				loadPage();
			}
			break;
		}
	}

	// 質問者のユーザ情報表示
	private void loadQuestionRelation(Question q) {
		GsonRequest<AppsSource> req = new GsonRequest<AppsSource>(Method.GET,
				getQuestionRelationUrl(q.getId()), AppsSource.class, null,
				null, new Listener<AppsSource>() {
					@Override
					public void onResponse(AppsSource response) {
						for (App app : response.getApplications()) {
							addQuestionAppLayout(app);
						}
					}
				}, new ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
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

	private void addQuestionAppLayout(App app) {
		LayoutInflater myinflater = (LayoutInflater) getApplicationContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout appLayout = (LinearLayout) myinflater.inflate(
				R.layout.list_item_question_app, null);
		setQuestionAppLayout(app, appLayout);

		appList.addView(appLayout);
	}

	private void setQuestionAppLayout(final App app, LinearLayout appLayout) {
		((TextView) appLayout.findViewById(R.id.list_item_question_app_name))
				.setText(app.getName());
		NetworkImageView niv = (NetworkImageView) appLayout
				.findViewById(R.id.list_item_question_app_network_image_view);
		niv.setImageUrl(getImageResourceURL(app), imageLoader);

		appLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String packageName = app.getPackageName();
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
	}

	private String getImageResourceURL(App app) {
		return Common.getIconBaseUrl(this) + app.getPackageName() + ".png";
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

	protected void loadPage() {
		final int next = comments.size();
		GsonRequest<CommentsSource> req = new GsonRequest<CommentsSource>(
				Method.GET, getCommentsUrl(DEFAULT_COMMENT_GET_COUNT, next),
				CommentsSource.class, null, null,
				new Listener<CommentsSource>() {
					@Override
					public void onResponse(CommentsSource response) {
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

	private int lastFirstVisibleItem = 0;

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		if (lastFirstVisibleItem < firstVisibleItem) {
			// 下移動
			if (commentPostButton.isClickable()) {
				lastFirstVisibleItem = firstVisibleItem;
				commentPostButton.setClickable(false);
				AlphaAnimation alpha = new AlphaAnimation(1, 0);
				alpha.setDuration(300);
				alpha.setFillAfter(true);
				commentPostButton.startAnimation(alpha);
			}
		} else if (lastFirstVisibleItem > firstVisibleItem) {
			// 上移動
			if (!commentPostButton.isClickable()) {
				lastFirstVisibleItem = firstVisibleItem;
				commentPostButton.setClickable(true);
				AlphaAnimation alpha = new AlphaAnimation(0, 1);
				alpha.setDuration(300);
				alpha.setFillAfter(true);
				commentPostButton.startAnimation(alpha);
			}
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
	}
}
