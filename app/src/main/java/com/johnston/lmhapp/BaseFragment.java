package com.johnston.lmhapp;

import android.app.Activity;
import android.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.View;
import android.widget.TextView;

/**
 * Created by ben on 06/11/15.
 */
public abstract class BaseFragment extends Fragment
{
    public View view;
    public int fragmentNumber = -1;

    public boolean refreshing = false;
	public boolean finished = false;
    
    protected void showProgressBar(){
        Activity act = getActivity();
        if(act instanceof MainActivity)
        {
            ((MainActivity)act).disableSwipeLayout();
        }
        view.findViewById(R.id.Status).setVisibility(View.VISIBLE);
        view.findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
        view.findViewById(R.id.nothingToShow).setVisibility(View.GONE);
        view.findViewById(R.id.my_recycler_view).setVisibility(View.GONE);
    }

    protected void showMessage(String message){
        Activity act = getActivity();
        if(act instanceof MainActivity)
        {
            ((MainActivity)act).enableSwipeLayout();
        }
        view.findViewById(R.id.Status).setVisibility(View.VISIBLE);
        view.findViewById(R.id.progressBar).setVisibility(View.GONE);
        view.findViewById(R.id.nothingToShow).setVisibility(View.VISIBLE);
        ((TextView)view.findViewById(R.id.nothingToShow)).setText(message);
        view.findViewById(R.id.my_recycler_view).setVisibility(View.GONE);
    }

    protected void showCards(){
        Activity act = getActivity();
        if(act instanceof MainActivity)
        {
            ((MainActivity)act).enableSwipeLayout();
        }
        view.findViewById(R.id.Status).setVisibility(View.GONE);
        view.findViewById(R.id.progressBar).setVisibility(View.GONE);
        view.findViewById(R.id.nothingToShow).setVisibility(View.GONE);
        view.findViewById(R.id.my_recycler_view).setVisibility(View.VISIBLE);
    }

    protected void setStartedRefreshing() {
        Activity act = getActivity();
        refreshing = true;
        if (act instanceof MainActivity)
        {
            if(finished)
            {
                ((MainActivity)act).startRefreshAnimation();
            } else
            {
                showProgressBar();
            }
            ((MainActivity)act).startRefresh(fragmentNumber);
        }
    }

	protected void setFinishedRefreshing()
	{
		Activity act = getActivity();
		refreshing = false;
        finished = true;
		if(act instanceof MainActivity) {
            ((MainActivity) act).stopRefreshAnimation();
            ((MainActivity) act).stopRefresh(fragmentNumber);
        }
	}
	public abstract void loadData();

	public boolean canChildScrollUp()
	{
		return ViewCompat.canScrollVertically(getScrollingView(), -1);
	}

	public abstract View getScrollingView();
}
