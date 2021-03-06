package com.johnston.lmhapp.EPOS;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.johnston.lmhapp.BaseFragment;
import com.johnston.lmhapp.MainActivity;
import com.johnston.lmhapp.R;

import java.util.ArrayList;

/**
 * Created by Tom on 02/06/2014.
 */
public class EPOSFragment extends BaseFragment
{
    private TextView status;
    private final int localFragmentNumber = 1;
    private ArrayList<String> transactions =new ArrayList<>();
    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message message) {


            if (message.what == -1) {
                setFinishedRefreshing();
                if (view == null) {
                    return;
                }
                showMessage(getResources().getString(R.string.somethingWentWrong));
                return;
            }

            if(message.what == MainActivity.STATUS_UPDATE){
                if(status!=null) {
                    status.setText((String) message.obj);
                }
                return;
            }


            if (message.what == 0) {
                transactions = (ArrayList<String>) message.obj;
                setFinishedRefreshing();
                if (view == null) {
                    return;
                }
                showCards();
                addEntriesToList();

            } else if(message.what == 1){
                String[] strings = (String[]) message.obj;
                if (view == null) {
                    return;
                }
                TextView AccountBalance = (TextView) view.findViewById(R.id.AccountBalance);
                TextView TokenBalance = (TextView) view.findViewById(R.id.TokenBalance);
                TextView DateBalance = (TextView) view.findViewById(R.id.DateBalance);
                TextView DateBalance2 = (TextView) view.findViewById(R.id.DateBalance2);
                AccountBalance.setText(strings[0]);
                TokenBalance.setText(strings[1]);
                DateBalance.setText(strings[2]);
                DateBalance2.setText(strings[2]);
            }
        }


    };
    private MenuItem actionRefresh;

    @Override
    public void loadData() {
        setStartedRefreshing();
        MainActivity main = (MainActivity) getActivity();
        main.getInfo(handler, (byte) localFragmentNumber);
    }

    @Override
    public View getScrollingView()
    {
        return view;
    }


    void addEntriesToList() {
        LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.transactionList);
        linearLayout.removeAllViews();
        Activity activity = getActivity();
        if(activity==null){
            return;
        }
        for (int i = 0; i < transactions.size(); i++) {
            String data = transactions.get(i);
            String code = data.substring(0, 2);
            String message = data.substring(2);
            TextView tv = new TextView(activity);
            tv.setText(message);
            if (code.equals("12")) {
                tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            } else if (code.equals("02")) {
                tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            }
            View divider = new View(activity);
            divider.setBackgroundColor(Color.parseColor("#1f000000"));
            divider.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 2));

            linearLayout.addView(tv);
            linearLayout.addView(divider);
        }
    }
    @Override
    protected void showCards(){
        (view.findViewById(R.id.progressBar)).setVisibility(View.GONE);
        (view.findViewById(R.id.nothingToShow)).setVisibility(View.GONE);
        (view.findViewById(R.id.card_view)).setVisibility(View.VISIBLE);
        (view.findViewById(R.id.card_view2)).setVisibility(View.VISIBLE);
        (view.findViewById(R.id.card_view3)).setVisibility(View.VISIBLE);
    }
    @Override
    protected void showMessage(String message){
        (view.findViewById(R.id.progressBar)).setVisibility(View.GONE);
        (view.findViewById(R.id.nothingToShow)).setVisibility(View.VISIBLE);
        ((TextView)view.findViewById(R.id.nothingToShow)).setText(message);
        (view.findViewById(R.id.card_view)).setVisibility(View.GONE);
        (view.findViewById(R.id.card_view2)).setVisibility(View.GONE);
        (view.findViewById(R.id.card_view3)).setVisibility(View.GONE);
    }
    @Override
    protected void showProgressBar(){
        (view.findViewById(R.id.progressBar)).setVisibility(View.VISIBLE);
        (view.findViewById(R.id.nothingToShow)).setVisibility(View.GONE);
        (view.findViewById(R.id.card_view)).setVisibility(View.GONE);
        (view.findViewById(R.id.card_view2)).setVisibility(View.GONE);
        (view.findViewById(R.id.card_view3)).setVisibility(View.GONE);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(null, null, savedInstanceState);
        view = inflater.inflate(R.layout.epos_layout, container, false);
        status = (TextView) view.findViewById(R.id.Status);
        if (refreshing)
        {
            setStartedRefreshing();
        }
        if (finished) {
            addEntriesToList();
            showCards();
        }
        if(!refreshing && !finished){
            loadData();
        }
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        view = null;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        actionRefresh = menu.findItem(R.id.action_refresh);
        actionRefresh.setEnabled(true);
        actionRefresh.setVisible(true);
    }
}
