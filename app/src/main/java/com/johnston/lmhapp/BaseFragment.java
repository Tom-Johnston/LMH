package com.johnston.lmhapp;

import android.app.Activity;
import android.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.View;

/**
 * Created by ben on 06/11/15.
 */
public abstract class BaseFragment extends Fragment
{
	public boolean refreshing = false;
	public boolean finished = false;

	public boolean isGoingToRefresh()
	{
		return refreshing || !finished;
	}

	public boolean isRefreshing()
	{
		return refreshing;
	}

	protected void setFinishedRefreshing()
	{
		Activity act = getActivity();
		refreshing = false;
		finished = true;
		if(act instanceof MainActivity)
			((MainActivity)act).stopRefreshAnimation();

	}

	public abstract void loadData();

	public boolean canChildScrollUp()
	{
		return ViewCompat.canScrollVertically(getScrollingView(), -1);
	}

	public abstract View getScrollingView();
}
