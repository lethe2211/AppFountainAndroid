package com.appfountain;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.app.ActionBar.TabListener;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;

/*
 * ユーザページ（ログインしたユーザについての情報ページ）
 * ViewPagerとTABの双方に対応させている
 */
public class UserPageActivity extends ActionBarActivity implements TabListener {
	private static final String TAG = UserPageActivity.class.getSimpleName();

	UserPagePagerAdapter pagerAdapter;
	ViewPager viewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_page);

		// Homeボタンを押せるようにする
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		int userId = getIntent().getIntExtra(Intent.EXTRA_UID, -1);
		if (userId < 0)
			finish();

		final ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		pagerAdapter = new UserPagePagerAdapter(getSupportFragmentManager(),
				userId);

		viewPager = (ViewPager) findViewById(R.id.user_page_view_pager);
		viewPager.setAdapter(pagerAdapter);

		viewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						// viewPagerの位置が変わった時にTABに変更を伝える
						actionBar.setSelectedNavigationItem(position);
					}
				});

		actionBar.addTab(actionBar.newTab().setText("基本情報")
				.setTabListener(this));
		actionBar.addTab(actionBar.newTab().setText("質問一覧")
				.setTabListener(this));
		actionBar.addTab(actionBar.newTab().setText("回答一覧")
				.setTabListener(this));
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		// Homeボタンが押されたら戻る
		case android.R.id.home:
			Log.d("home", "clicked!");
			finish();
			break;
		
		}
		return false;
	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		// TABが選択された時に，viewPagerにも変更を伝える
		viewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
	}

	public class UserPagePagerAdapter extends FragmentPagerAdapter {
		private final Bundle bundle;

		public UserPagePagerAdapter(FragmentManager fm, int userId) {
			super(fm);
			bundle = new Bundle();
			bundle.putInt(Intent.EXTRA_UID, userId);
		}

		@Override
		public Fragment getItem(int position) {
			// position は追加されたタブ順に左から指定される
			Fragment fragment = null;
			switch (position) {
			case 0:
				fragment = new UserInfoFragment();
				break;
			case 1:
				fragment = new UserQuestionFragment();
				break;
			case 2:
				fragment = new UserCommentFragment();
				break;
			}
			if (fragment != null)
				fragment.setArguments(bundle);
			return fragment;
		}

		@Override
		public int getCount() {
			// タブの数
			return 3;
		}
	}
}
