package com.appfountain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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
import com.appfountain.model.Category;
import com.appfountain.model.Question;
import com.appfountain.model.UserContainer;
import com.appfountain.util.Common;

/*
 * トップページ
 */
public class TopPageActivity extends EndlessScrollActionBarActivity {
	private static final String TAG = TopPageActivity.class.getSimpleName();
	private static final int QUESTION_POST = 1;
	private final String url = Common.getApiBaseUrl(this) + "question";

	private ActionBarActivity self = this;
	private RequestQueue queue = null;
	private ListView questionListView;
	private boolean inError = false;
	private List<Question> questions = new ArrayList<Question>();
	private QuestionListAdapter questionListAdapter;
	private UserContainer user = null;

	private DrawerLayout drawerLayout; // DrawerLayout(NavigationDrawerを使うのに必要なレイアウト)
	private ActionBarDrawerToggle drawerToggle; // ActionBar中のアイコンをタップすると，NavigationDrawerが開く/閉じるようにする

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_top_page);

		user = Common.getUserContainer(this);

		questionListView = (ListView) findViewById(R.id.activity_top_page_question_list);
		questionListAdapter = new QuestionListAdapter(this,
				R.layout.list_item_question, questions);
		questionListView.setAdapter(questionListAdapter);
		questionListView.setOnScrollListener(this);
		questionListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// クリックされた質問を取得し，次の画面へ渡す
				Question question = questions.get(position);
				Intent intent = new Intent(TopPageActivity.this,
						QuestionDetailActivity.class);
				intent.putExtra(QuestionDetailActivity.EXTRA_QUESTION, question);
				startActivity(intent);
			}
		});

		// ActionBar中のアイコンのタップを有効にする
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);

		drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout); // DrawerLayout

		// NavigationDrawerを開く/閉じるトグルボタン
		drawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
				R.drawable.ic_launcher, /* アイコン画像 */
				R.string.drawer_open, /* Drawerを開くときのメッセージ(？) */
				R.string.drawer_close /* Drawerを閉じるときのメッセージ(？) */
		);

		// レイアウトにボタンを設定
		drawerLayout.setDrawerListener(drawerToggle);

		final EditText searchEditText = (EditText) findViewById(R.id.search_text_box); // 検索ボックス
		Button searchExecButton = (Button) findViewById(R.id.search_exec_btn); // NavigationDrawer中の検索ボタン

		searchExecButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				String query = searchEditText.getText().toString(); // 検索ボックスに入力されたクエリ
				if (query.equals(""))
					return; // クエリが空文字列なら検索しない

				// NavigationDrawerを閉じる
				drawerLayout.closeDrawers();

				// 画面遷移
				Intent intent = new Intent(TopPageActivity.this,
						SearchResultActivity.class);
				// intent.putExtra("category_id", position + 1); //
				// category_idを持ち越す
				intent.putExtra("query", query);
				startActivity(intent);
			}
		});

		// ListViewとそれに対応するアダプタ
		final ListView categoryListView = (ListView) findViewById(R.id.activity_top_page_category_list);
//		ArrayList<String> categories = new ArrayList<String>(Arrays.asList(
//				"ウィジェット", "エンタテイメント", "カスタマイズ", "コミック", "ショッピング", "スポーツ",
//				"ソーシャルネットワーク", "ツール", "ニュース＆雑誌", "ビジネス", "ファイナンス", "メディア＆動画",
//				"ライフスタイル", "ライブラリ＆デモ", "ライブ壁紙", "交通", "仕事効率化", "健康＆フィットネス",
//				"写真", "医療", "天気", "教育", "旅行＆地域", "書籍＆文献", "通信", "音楽＆オーディオ"));
		
		// カテゴリの名前の配列
		ArrayList<String> categoryNames = new ArrayList<String>();
		List<Category> categories = Common.getCategories();
		for (Category c : categories) {
			categoryNames.add(c.getName());
		}
		
		final ArrayAdapter<String> categoryListAdapter = new ArrayAdapter<String>(
				this, R.layout.list_item_category, categoryNames); // list_item_category.xmlをレイアウトに指定(R.layout.simple_list_item_1と中身は同じ)
		categoryListView.setAdapter(categoryListAdapter);
		categoryListView.setOnScrollListener(this);

		categoryListView
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						ListView listView = (ListView) parent;
						String item = (String) listView
								.getItemAtPosition(position); // 押されたリスト要素の文字列
						Log.d("click", String.format("onItemClick: %s", item));

						// NavigationDrawerを閉じる
						drawerLayout.closeDrawers();

						// 画面遷移
						Intent intent = new Intent(TopPageActivity.this,
								SearchResultActivity.class);
						intent.putExtra("category_id", position + 1); // category_idを持ち越す
						startActivity(intent);
					}

				});

		setUserInfoButton();
	}

	// drawer内のユーザ情報view初期化
	private void setUserInfoButton() {
		final TextView userInfoButton = (TextView) findViewById(R.id.left_drawer_user_info);
		final TextView userLoginButton = (TextView) findViewById(R.id.left_drawer_user_login);
		final TextView userRegisterButton = (TextView) findViewById(R.id.left_drawer_user_register);
		final TextView userLogoutButton = (TextView) findViewById(R.id.left_drawer_user_logout);
		if (user == null) {
			// ユーザログインしてない場合
			userInfoButton.setVisibility(View.GONE);
			userLogoutButton.setVisibility(View.GONE);
			userLoginButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View arg0) {

					// NavigationDrawerを閉じる
					drawerLayout.closeDrawers();

					Intent intent = new Intent(TopPageActivity.this,
							LoginActivity.class);
					startActivity(intent);
				}
			});
			userRegisterButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View arg0) {

					// NavigationDrawerを閉じる
					drawerLayout.closeDrawers();

					Intent intent = new Intent(TopPageActivity.this,
							RegisterActivity.class);
					startActivity(intent);
				}
			});
		} else {
			// ユーザログインしてる場合
			userLoginButton.setVisibility(View.GONE);
			userRegisterButton.setVisibility(View.GONE);
			userInfoButton.setText("ユーザページ: " + user.getName());
			userInfoButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View arg0) {

					// NavigationDrawerを閉じる
					drawerLayout.closeDrawers();

					Intent intent = new Intent(TopPageActivity.this,
							UserPageActivity.class);
					intent.putExtra(Intent.EXTRA_UID, user.getId());
					startActivity(intent);
				}
			});
			userLogoutButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View arg0) {

					// NavigationDrawerを閉じる
					drawerLayout.closeDrawers();

					Common.logout(TopPageActivity.this);
					Intent intent = new Intent(TopPageActivity.this,
							MainActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);
				}
			});
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.top_page, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// ActionBar中のアプリアイコン(ホームボタン)がタップされたら
		if (drawerToggle.onOptionsItemSelected(item)) {
			return true;
		}

		// menuボタンのいずれかがタップ
		switch (item.getItemId()) {
		case R.id.top_page_move_post_question:
			// ログイン済みなら質問投稿画面へ
			if (user != null) {

				// NavigationDrawerを閉じる
				drawerLayout.closeDrawers();

				Intent intent = new Intent(TopPageActivity.this,
						PostActivity.class);
				startActivityForResult(intent, QUESTION_POST);
			} else {

				// NavigationDrawerを閉じる
				drawerLayout.closeDrawers();

				// TODO ログイン画面へいい感じに(メッセージつけて)遷移
				Toast.makeText(this, "ログインしてください", Toast.LENGTH_SHORT).show();
			}
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (hasNext() && !inError) {
			loadPage();
		}
	}

	// startActivityForResultで呼ばれたActivityが停止した際に呼ばれる
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
		case QUESTION_POST:
			// 質問投稿後は再読み込みさせる
			if (resultCode == RESULT_OK) {
				questions.clear();
				inError = false;
				loadPage();
			}
			break;
		}
	}

	@Override
	protected void loadPage() {
		if (queue == null)
			queue = Volley.newRequestQueue(this);

		int next = questions.size();
		GsonRequest<QuestionsSource> req = new GsonRequest<QuestionsSource>(
				Method.GET, url + "?count=20&next=" + next,
				QuestionsSource.class, null, null,
				new Listener<QuestionsSource>() {
					@Override
					public void onResponse(QuestionsSource response) {
						if (response.getQuestions().isEmpty())
							finishLoading();
						questions.addAll(response.getQuestions());
						questionListAdapter.notifyDataSetChanged();
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
