package com.appfountain.component;

import android.support.v7.app.ActionBarActivity;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;

/**
 * Scrollしていくと勝手に続きを読み込むListViewを持つActionBarActivity
 */
public abstract class EndlessScrollActionBarActivity extends ActionBarActivity
		implements OnScrollListener {

	private int visibleThreshold = 3;
	private int previousTotal = 0;
	private boolean loading = true;
	private boolean isLast = false;

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		if (loading) {
			if (totalItemCount > previousTotal) {
				loading = false;
				previousTotal = totalItemCount;
			}
		}
		if (!loading
				&& !isLast
				&& (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
			loadPage();
			loading = true;
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
	}

	/**
	 * ページの読み込みを行う
	 */
	protected abstract void loadPage();

	protected Boolean hasNext() {
		return !isLast;
	}

	protected void finishLoading() {
		isLast = true;
	}

	protected void restartLoading() {
		isLast = false;
	}
}
