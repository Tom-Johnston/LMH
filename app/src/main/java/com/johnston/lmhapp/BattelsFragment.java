package com.johnston.lmhapp;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Tom on 27/10/2014.
 */
public class BattelsFragment extends Fragment {
    View view;

    public void LoadBattels() {
        MainActivity main = (MainActivity) this.getActivity();
        main.GetBattels(view);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.battels, container, false);
        LoadBattels();


        return null;
    }

}
