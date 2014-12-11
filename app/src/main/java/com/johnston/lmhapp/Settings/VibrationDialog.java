package com.johnston.lmhapp.Settings;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.johnston.lmhapp.R;

import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * Created by Jake on 15/11/2014.
 */
public class VibrationDialog extends DialogFragment {
    View view;
    View.OnClickListener radioListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            EditText pattern = (EditText) view.findViewById(R.id.Pattern);
            if (v.getId() == R.id.OneBuzz) {
                pattern.setText(getResources().getString(R.string.buzz1));
                pattern.setEnabled(false);
            } else if (v.getId() == R.id.TwoBuzz) {
                pattern.setText(getResources().getString(R.string.buzz2));
                pattern.setEnabled(false);
            } else if (v.getId() == R.id.ThreeBuzz) {
                pattern.setText(getResources().getString(R.string.buzz3));
                pattern.setEnabled(false);
            } else {
                pattern.setEnabled(true);
            }
        }
    };

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
        builder.setPositiveButton("Save", null);
        builder.setNeutralButton("Test", null);
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
        SharedPreferences vibratePreference = getActivity().getSharedPreferences("vibratePattern", 0);
        patternText.setText(vibratePreference.getString("vibratePattern", "0,600"));
        patternText.setEnabled(false);
        if (vibratePreference.getString("vibratePattern", "0,600").equals(getResources().getString(R.string.buzz1))) {
            buzz1.setChecked(true);
        } else if (vibratePreference.getString("vibratePattern", "0,600").equals(getResources().getString(R.string.buzz2))) {
            buzz2.setChecked(true);
        } else if (vibratePreference.getString("vibratePattern", "0,600").equals(getResources().getString(R.string.buzz3))) {
            buzz3.setChecked(true);
        } else {
            buzzCustom.setChecked(true);
            patternText.setEnabled(true);
        }

        final AlertDialog d = builder.create();
        d.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {

                Button b2 = d.getButton(AlertDialog.BUTTON_NEUTRAL);
                b2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View useless) {
                        TestString(true);

                    }

                });
                Button b1 = d.getButton(AlertDialog.BUTTON_POSITIVE);
                b1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EditText editText = (EditText) view.findViewById(R.id.Pattern);
                        String pattern = editText.getText().toString();
                        if (TestString(false)) {
                            SharedPreferences vibratePreference = getActivity().getSharedPreferences("vibratePattern", 0);
                            SharedPreferences.Editor editor = vibratePreference.edit();
                            editor.putString("vibratePattern", pattern);
                            Toast toast = Toast.makeText(getActivity(), "Vibrate Pattern Saved.", Toast.LENGTH_SHORT);
                            toast.show();
                            editor.apply();
                            d.dismiss();
                        }
                    }
                });
            }
        });
        return d;
    }

    public boolean TestString(Boolean vibrate) {
        // Tests for a valid string. If vibrate is true, will vibrate on valid string.
        EditText editText = (EditText) view.findViewById(R.id.Pattern);
        String pattern = editText.getText().toString();
        if (!pattern.endsWith(",")) {
            pattern = pattern + ",";
        }
        StringTokenizer stringTokenizer = new StringTokenizer(pattern, ",");
        Boolean parse = true;
        ArrayList<Long> vibratePatternList = new ArrayList<>();
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
            return false;
        } else {
            if (vibrate) {
                Vibrate(vibratePatternList);
            }
            return true;
        }
    }

    public void Vibrate(ArrayList<Long> vibratePatternList) {
        Vibrator v = (Vibrator) getActivity().getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
        long[] vibrateArray = new long[vibratePatternList.size()];
        for (int i = 0; i < vibratePatternList.size(); i++) {
            vibrateArray[i] = vibratePatternList.get(i);
        }
        v.vibrate(vibrateArray, -1);
    }
}
