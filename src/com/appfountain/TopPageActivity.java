package com.appfountain;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.appfountain.component.CategoryListAdapter;
import com.appfountain.component.EndlessScrollActionBarActivity;
import com.appfountain.component.QuestionListAdapter;
import com.appfountain.external.GsonRequest;
import com.appfountain.external.QuestionsSource;
import com.appfountain.model.Category;
import com.appfountain.model.Question;
import com.appfountain.model.UserContainer;
import com.appfountain.util.Common;

import java.util.ArrayList;
import java.util.List;

/*
 * トップページ
 */

public class TopPageActivity extends EndlessScrollActionBarActivity implements TextView.OnEditorActionListener {
    private static final String TAG = TopPageActivity.class.getSimpleName();
    private static final int QUESTION_POST = 1;
    private static final int OPEN_POST_ACTIVITY = 2;
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
    private EditText searchEditText;
    private Button searchExecButton;
    private ListView categoryListView;
    private List<Category> categories = new ArrayList<Category>();
    private ArrayAdapter<Category> categoryListAdapter;

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

        searchEditText = (EditText) findViewById(R.id.search_text_box); // 検索ボックス
        searchEditText.setOnEditorActionListener(this);

//        ImageButton searchExecButton = (ImageButton) findViewById(R.id.search_exec_btn); // NavigationDrawer中の検索ボタン
//
//        searchExecButton.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                searchQuestions();
//            }
//        });

        // ListViewとそれに対応するアダプタ
        categoryListView = (ListView) findViewById(R.id.activity_top_page_category_list);

        categories = Common.getCategories(); // カテゴリの配列

        categoryListAdapter = new CategoryListAdapter(
                this, R.layout.list_item_category, categories); // list_item_category.xmlをレイアウトに指定
        categoryListView.setAdapter(categoryListAdapter);
        categoryListView.setOnScrollListener(this);

        categoryListView
                .setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {

                        // NavigationDrawerを閉じる
                        drawerLayout.closeDrawers();

                        // 画面遷移
                        Intent intent = new Intent(TopPageActivity.this,
                                SearchResultActivity.class);
                        intent.putExtra("category_id", position + 1); // category_idを持ち越す
                        startActivityForResult(intent, OPEN_POST_ACTIVITY);
                    }

                });

        setUserInfoButton();
    }

    private void searchQuestions() {
        String query = searchEditText.getText().toString(); // 検索ボックスに入力されたクエリ
        if (query.equals(""))
            return; // クエリが空文字列なら検索しない

        // NavigationDrawerを閉じる
        drawerLayout.closeDrawers();

        // 画面遷移
        Intent intent = new Intent(TopPageActivity.this,
                SearchResultActivity.class);
        intent.putExtra("query", query);
        startActivityForResult(intent, OPEN_POST_ACTIVITY);
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

    // startActivityForResultで呼ばれたActivityが停止した際に呼ばれる
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case QUESTION_POST:
                // 質問投稿後は再読み込みさせる
                if (resultCode == RESULT_OK) {
                    questions.clear();
                    questionListAdapter.notifyDataSetChanged();
                    inError = false;
                }
                break;
            case OPEN_POST_ACTIVITY:
                if (resultCode == RESULT_OK) {
                    Intent intent = new Intent(TopPageActivity.this,
                            PostActivity.class);
                    startActivityForResult(intent, QUESTION_POST);
                }
                break;
        }

    }

    @Override
    protected void loadPage() {
        if (!Common.isInternetAvailable(self)) {
            Toast.makeText(self,
                    getString(R.string.common_internet_unavailable),
                    Toast.LENGTH_SHORT).show();
            inError = true;
            return;
        }
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
        }
        );
        queue.add(req);
    }

    @Override
    public boolean onEditorAction(TextView textView, int i, KeyEvent event) {
        if (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
            searchQuestions();
            return true;
        }
        return false;
    }
}
