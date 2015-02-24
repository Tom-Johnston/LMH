package com.johnston.lmhapp.Settings;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.TimePicker;
import android.widget.Toast;

import com.johnston.lmhapp.R;

/**
 * Created by Tom on 20/11/2014.
 */
public class RefreshTimeDialog extends DialogFragment {
    private View view;
    private final long[] refreshTimeChoices = {1800000, 3600000, 7200000, 14400000, 21600000, 43200000, 86400000, -1};

    static RefreshTimeDialog newInstance() {
        RefreshTimeDialog f = new RefreshTimeDialog();
        Bundle args = new Bundle();
        f.setArguments(args);
        return f;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        view = inflater.inflate(R.layout.refresh_time_dialog, null);
        builder.setView(view);
        builder.setTitle("Refresh Time");
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                NumberPicker refreshTimePicker = (NumberPicker) view.findViewById(R.id.RefreshTime);
                SharedPreferences refreshTimePreference = getActivity().getSharedPreferences("RefreshTime", 0);
                SharedPreferences.Editor editor = refreshTimePreference.edit();
                int selection = refreshTimePicker.getValue();
                editor.putLong("refreshTime", refreshTimeChoices[selection]);
                Toast toast = Toast.makeText(getActivity(), "Time Saved.", Toast.LENGTH_SHORT);
                toast.show();
                editor.apply();


            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        NumberPicker notifyTimePicker = (NumberPicker) view.findViewById(R.id.RefreshTime);
        SharedPreferences refreshTimePreference = getActivity().getSharedPreferences("RefreshTime", 0);
        Long current = refreshTimePreference.getLong("refreshTime", 2);
        int currentPosition = 2;
        for (int i = 0; i < refreshTimeChoices.length; i++) {
            if (current == refreshTimeChoices[i]) {
                currentPosition = i;
                break;
            }
        }
        String[] choices = getResources().getStringArray(R.array.refreshTimeChoices);
        notifyTimePicker.setMinValue(0);
        notifyTimePicker.setMaxValue(choices.length - 1);
        notifyTimePicker.setDisplayedValues(choices);
        notifyTimePicker.setValue(currentPosition);
        notifyTimePicker.setDescendantFocusability(TimePicker.FOCUS_BLOCK_DESCENDANTS);
        return builder.create();
    }

}
