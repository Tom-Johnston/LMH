package com.johnston.lmhapp.Settings;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import com.johnston.lmhapp.R;

/**
 * Created by Johnston on 17/09/2014.
 */
public class LEDColourDialog extends DialogFragment {
    View view;
    int r;
    int g;
    int b;

    static LEDColourDialog newInstance() {
        LEDColourDialog f = new LEDColourDialog();
        Bundle args = new Bundle();
        f.setArguments(args);
        return f;
    }

    public void changeRectangleColour() {
        ImageView img = (ImageView) view.findViewById(R.id.led);
        img.setBackgroundColor(Color.argb(255, r, g, b));
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        view = inflater.inflate(R.layout.led_colour_picker, null);
        builder.setView(view)
                .setTitle("Set the LED Settings")
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String onForS = ((EditText) view.findViewById(R.id.editText)).getText().toString();
                        String offForS = ((EditText) view.findViewById(R.id.editText2)).getText().toString();
                        SharedPreferences LEDSettings = getActivity().getSharedPreferences("LEDSettings", 0);
                        SharedPreferences.Editor editor = LEDSettings.edit();
                        Boolean showToast = false;
                        Boolean negative = false;
                        if (!onForS.equals("")) {
                            try {
                                int onFor = Integer.parseInt(onForS);
                                if (onFor < 0) {
                                    negative = true;
                                } else {
                                    editor.putInt("onFor", onFor);
                                }
                            } catch (NumberFormatException nfe) {
                                showToast = true;
                            }
                        }
                        if (!offForS.equals("")) {
                            try {
                                int offFor = Integer.parseInt(offForS);
                                if (offFor < 0) {
                                    negative = true;
                                } else {
                                    editor.putInt("offFor", offFor);
                                }

                            } catch (NumberFormatException nfe) {
                                showToast = true;
                            }
                        }
                        if (showToast) {
                            Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Problem parsing integers.", Toast.LENGTH_SHORT);
                            toast.show();
                        } else if (negative) {
                            Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Negative values are not allowed.", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                        editor.putInt("redValue", r);
                        editor.putInt("greenValue", g);
                        editor.putInt("blueValue", b);

                        editor.apply();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        SeekBar seekBar = (SeekBar) view.findViewById(R.id.seekBar);
        SeekBar seekBar2 = (SeekBar) view.findViewById(R.id.seekBar2);
        SeekBar seekBar3 = (SeekBar) view.findViewById(R.id.seekBar3);
        SeekBar.OnSeekBarChangeListener redListener = new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                r = i;
                changeRectangleColour();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        };
        SeekBar.OnSeekBarChangeListener greenListener = new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                g = i;
                changeRectangleColour();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        };
        SeekBar.OnSeekBarChangeListener blueListener = new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b2) {
                b = i;
                changeRectangleColour();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        };
        seekBar.setOnSeekBarChangeListener(redListener);
        seekBar2.setOnSeekBarChangeListener(greenListener);
        seekBar3.setOnSeekBarChangeListener(blueListener);
        SharedPreferences LEDSettings = getActivity().getSharedPreferences("LEDSettings", 0);
        seekBar.setProgress(LEDSettings.getInt("redValue", 0));
        seekBar2.setProgress(LEDSettings.getInt("greenValue", 33));
        seekBar3.setProgress(LEDSettings.getInt("blueValue", 71));
        EditText editText = (EditText) view.findViewById(R.id.editText);
        EditText editText2 = (EditText) view.findViewById(R.id.editText2);
        int onFor = LEDSettings.getInt("onFor", -1);
        int offFor = LEDSettings.getInt("offFor", -1);
        if (onFor > -1) {
            editText.setText(String.valueOf(onFor));
        }
        if (offFor > -1) {
            editText2.setText(String.valueOf(offFor));
        }
        return builder.create();
    }
}
