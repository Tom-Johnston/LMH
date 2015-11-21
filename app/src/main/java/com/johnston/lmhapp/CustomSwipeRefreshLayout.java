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
	private boolean mMeasured = false;
	private boolean mPreMeasureRefreshing = false;

	public CustomSwipeRefreshLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	public void setChildScrollDelegate(BaseFragment delegate) {
		fragment = delegate;
		setEnabled(fragment != null);
	}

	@Override
	public boolean canChildScrollUp() {
		if (fragment != null) {
			return fragment.canChildScrollUp();
		}
		return super.canChildScrollUp();
	}

	@Override
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		if (!mMeasured) {
			mMeasured = true;
			setRefreshing(mPreMeasureRefreshing);
		}
	}

	@Override
	public void setRefreshing(boolean refreshing) {
		if (mMeasured) {
			super.setRefreshing(refreshing);
		} else {
			mPreMeasureRefreshing = refreshing;
		}
	}
}
