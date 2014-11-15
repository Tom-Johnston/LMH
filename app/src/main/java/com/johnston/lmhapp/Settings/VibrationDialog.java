package com.johnston.lmhapp.Settings;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.Toast;

import com.johnston.lmhapp.MealMenus.NotificationsService;
import com.johnston.lmhapp.R;

import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * Created by Jake on 15/11/2014.
 */
public class VibrationDialog extends DialogFragment {
    View view;

    static VibrationDialog newInstance() {
        VibrationDialog f = new VibrationDialog();
        Bundle args = new Bundle();
        f.setArguments(args);
        return f;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        view = inflater.inflate(R.layout.vibration_dialog, null);
        builder.setView(view);
        builder.setTitle("Vibration Settings");
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int id) {
                EditText editText = (EditText) view.findViewById(R.id.Pattern);
                String pattern = editText.getText().toString();
                if (!pattern.endsWith(",")) {
                    pattern = pattern + ",";
                }
                StringTokenizer stringTokenizer = new StringTokenizer(pattern, ",");
                Boolean parse = true;
                ArrayList<Long> vibratePatternList = new ArrayList<Long>();
                while (stringTokenizer.hasMoreElements()) {
                    try {
                        vibratePatternList.add(Long.parseLong(stringTokenizer.nextToken()));
                    } catch (NumberFormatException nfe) {
                        parse = false;
                        break;
                    }
                }
                if (!parse || vibratePatternList.size() < 1) {
                    Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Problem parsing integers.", Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    SharedPreferences vibratePreference = getActivity().getSharedPreferences("vibratePattern",0);
                    SharedPreferences.Editor editor = vibratePreference.edit();
                    editor.putString("vibratePattern", pattern);
                    Toast toast = Toast.makeText(getActivity(), "Vibrate Pattern Saved.", Toast.LENGTH_SHORT);
                    toast.show();
                    editor.commit();
                }


            }
        });
        builder.setNeutralButton("Test", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Vibrate();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });

        RadioButton buzz1 = (RadioButton) view.findViewById(R.id.OneBuzz);
        RadioButton buzz2 = (RadioButton) view.findViewById(R.id.TwoBuzz);
        RadioButton buzz3 = (RadioButton) view.findViewById(R.id.ThreeBuzz);
        RadioButton buzzCustom = (RadioButton) view.findViewById(R.id.CustomBuzz);
        buzz1.setOnClickListener(radioListener);
        buzz2.setOnClickListener(radioListener);
        buzz3.setOnClickListener(radioListener);
        buzzCustom.setOnClickListener(radioListener);
        EditText patternText = (EditText) view.findViewById(R.id.Pattern);
        SharedPreferences vibratePreference = getActivity().getSharedPreferences("vibratePattern",0);
        patternText.setText(vibratePreference.getString("vibratePattern","0,600"));

        if (vibratePreference.getString("vibratePattern","0,600").equals(getResources().getString(R.string.buzz1))){
            buzz1.setChecked(true);
        }else if (vibratePreference.getString("vibratePattern","0,600").equals(getResources().getString(R.string.buzz2))){
            buzz2.setChecked(true);
        }else if (vibratePreference.getString("vibratePattern","0,600").equals(getResources().getString(R.string.buzz3))){
            buzz3.setChecked(true);
        }else{
            buzzCustom.setChecked(true);
        }

        final AlertDialog d = builder.create();
        return d;
    }


    View.OnClickListener radioListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            EditText pattern = (EditText) view.findViewById(R.id.Pattern);
            if(v.getId()==R.id.OneBuzz){
                pattern.setText(getResources().getString(R.string.buzz1));
                pattern.setEnabled(false);
            }else if(v.getId()==R.id.TwoBuzz){
                pattern.setText(getResources().getString(R.string.buzz2));
                pattern.setEnabled(false);
            }else if(v.getId()==R.id.ThreeBuzz){
                pattern.setText(getResources().getString(R.string.buzz3));
                pattern.setEnabled(false);
            }else{
                pattern.setEnabled(true);
            }
        }
    };

    public void Vibrate() {
        EditText editText = (EditText) view.findViewById(R.id.Pattern);
        String pattern = editText.getText().toString();
        if (!pattern.endsWith(",")) {
            pattern = pattern + ",";
        }
        StringTokenizer stringTokenizer = new StringTokenizer(pattern, ",");
        Boolean parse = true;
        ArrayList<Long> vibratePatternList = new ArrayList<Long>();
        while (stringTokenizer.hasMoreElements()) {
            try {
                vibratePatternList.add(Long.parseLong(stringTokenizer.nextToken()));
            } catch (NumberFormatException nfe) {
                parse = false;
                break;
            }
        }
        if (!parse || vibratePatternList.size() < 1) {
            Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Problem parsing integers.", Toast.LENGTH_SHORT);
            toast.show();
        } else {
            Vibrator v = (Vibrator) getActivity().getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
            long[] vibrateArray = new long[vibratePatternList.size()];
            for (int i = 0; i < vibratePatternList.size(); i++) {
                vibrateArray[i] = vibratePatternList.get(i);
            }
            v.vibrate(vibrateArray, -1);
        }

    }
}
