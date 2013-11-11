package com.appfountain;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

/**
 * 開発用，全ての画面へ遷移可能な画面
 */
public class MainActivity extends Activity {
	private static final String TAG = MainActivity.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Handler hdl = new Handler();
		hdl.postDelayed(new splashHandler(), 1500);
	}

	class splashHandler implements Runnable {
		public void run() {
			Intent i = new Intent(getApplication(), TopPageActivity.class);
			startActivity(i);
			finish();
		}
	}
}
