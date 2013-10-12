package com.appfountain;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;

/**
 * 質問投稿ページ
 */
public class PostActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_post);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.post, menu);
		return true;
	}

}
