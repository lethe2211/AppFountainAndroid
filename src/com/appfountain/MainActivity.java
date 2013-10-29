package com.appfountain;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.appfountain.util.Common;

/**
 * 開発用，全ての画面へ遷移可能な画面
 */
public class MainActivity extends ActionBarActivity implements OnClickListener {
	private static final String TAG = MainActivity.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (Common.isInternetAvailable(this)) {
			Toast.makeText(this, "インターネットが利用出来ます", Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(this, "インターネットに接続されていません", Toast.LENGTH_SHORT)
					.show();
		}

		findViewById(R.id.move_to_top_activity_button).setOnClickListener(this);
		findViewById(R.id.move_to_search_result_activity_button)
				.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		Intent intent = null;
		switch (v.getId()) {
		case R.id.move_to_search_result_activity_button:
			intent = new Intent(MainActivity.this, SearchResultActivity.class);
			break;
		case R.id.move_to_top_activity_button:
			intent = new Intent(MainActivity.this, TopPageActivity.class);
			break;
		default:
			new IllegalArgumentException();
		}
		startActivity(intent);
	}
}
