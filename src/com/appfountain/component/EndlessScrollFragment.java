package com.appfountain.component;

import android.os.Handler;
import android.support.v4.app.Fragment;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;

/**
 * Scrollしていくと勝手に続きを読み込むListViewを持つActionBarActivity
 */
public abstract class EndlessScrollFragment extends Fragment implements
		OnScrollListener {

	private int visibleThreshold = 3;
	private int previousTotal = 0;
	private boolean loading = true;
	private boolean isLast = false;

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		if (loading) {
			if (totalItemCount > previousTotal) {
				previousTotal = totalItemCount;
				Handler hdl = new Handler();
				hdl.postDelayed(new ReloadHandler(), 500);
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

	class ReloadHandler implements Runnable {
		public void run() {
			loading = false;
		}
	}
}
