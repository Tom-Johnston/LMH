package com.johnston.lmhapp;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.ViewConfiguration;
import android.widget.AbsListView;

/**
 * Created by ben on 09/11/15.
 */
public class CustomSwipeRefreshLayout extends SwipeRefreshLayout
{
	private BaseFragment fragment;

	public CustomSwipeRefreshLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	public void setChildScrollDelegate(BaseFragment delegate) {
		fragment = delegate;
		setEnabled(fragment != null);
		setRefreshing(false);
	}

	@Override
	public boolean canChildScrollUp() {
		if (fragment != null) {
			return fragment.canChildScrollUp();
		}
		return super.canChildScrollUp();
	}

}
