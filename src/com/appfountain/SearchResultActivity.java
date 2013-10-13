package com.appfountain;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;

/*
 * 検索結果表示ページ
 * タイトル・タグ検索，カテゴリ検索
 */
public class SearchResultActivity extends ActionBarActivity {
	private static final String TAG = SearchResultActivity.class
			.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_result);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.search_result, menu);
		return true;
	}

}
