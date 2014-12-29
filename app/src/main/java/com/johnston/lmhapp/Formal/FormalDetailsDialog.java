package com.johnston.lmhapp.Formal;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;

import com.johnston.lmhapp.R;

import java.util.ArrayList;

/**
 * Created by Tom on 01/12/2014.
 */
public class FormalDetailsDialog extends DialogFragment {
    View view;

    static FormalDetailsDialog newInstance(ArrayList<String> passedEntries) {
        FormalDetailsDialog f = new FormalDetailsDialog();
        Bundle args = new Bundle();
        args.putStringArrayList("entries", passedEntries);
        f.setArguments(args);
        return f;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        ArrayList<String> entries = getArguments().getStringArrayList("entries");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        view = inflater.inflate(R.layout.formal_details_dialog, null);
        ListView listView = (ListView) view.findViewById(R.id.formal_details_listView);
        listView.setAdapter(new FormalDetailsListAdapter(getActivity(), R.layout.formal_details_list_item, entries));
        builder.setView(view)
                .setTitle("List of People Going")
                .setNeutralButton("Close", null);
        return builder.create();
    }

}
