package com.johnston.lmhapp.Settings;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.TimePicker;
import android.widget.Toast;

import com.johnston.lmhapp.MealMenus.NotificationsService;
import com.johnston.lmhapp.R;

/**
 * Created by Jake on 15/11/2014.
 */
public class NotifyTimeDialog extends DialogFragment {
    private View view;

    static NotifyTimeDialog newInstance() {
        NotifyTimeDialog f = new NotifyTimeDialog();
        Bundle args = new Bundle();
        f.setArguments(args);
        return f;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        view = inflater.inflate(R.layout.notify_time_layout, null);
        builder.setView(view);
        builder.setTitle("Notify Time");
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                NumberPicker notifyTimePicker = (NumberPicker) view.findViewById(R.id.NotifyTime);
                SharedPreferences notifyTimePreference = getActivity().getSharedPreferences("NotifyTime", 0);
                SharedPreferences.Editor editor = notifyTimePreference.edit();
                int notifyTime = notifyTimePicker.getValue();
                editor.putInt("NotifyTime", notifyTime);
                Toast toast = Toast.makeText(getActivity(), "Time Saved.", Toast.LENGTH_SHORT);
                toast.show();
                editor.apply();
                Intent intent = new Intent(getActivity(), NotificationsService.class);
                getActivity().sendBroadcast(intent);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        NumberPicker notifyTimePicker = (NumberPicker) view.findViewById(R.id.NotifyTime);
        SharedPreferences notifyTimePreference = getActivity().getSharedPreferences("NotifyTime", 0);
        int current = notifyTimePreference.getInt("NotifyTime", 10);
        notifyTimePicker.setMaxValue(0);
        notifyTimePicker.setMaxValue(30);
        notifyTimePicker.setDescendantFocusability(TimePicker.FOCUS_BLOCK_DESCENDANTS);
        notifyTimePicker.setValue(current);
        return builder.create();
    }
}
