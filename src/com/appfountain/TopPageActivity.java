package com.appfountain;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;

/*
 * トップページ
 */
public class TopPageActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_top_page);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.top_page, menu);
		return true;
	}

}
