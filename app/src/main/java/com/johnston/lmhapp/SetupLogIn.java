package com.johnston.lmhapp;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Tom on 08/08/2014.
 */
public class SetupLogIn extends Fragment {
    final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message message) {

            finishedDialog((Boolean) message.obj);
        }
    };
    View view;
    MainActivity Main;
    VibrateSpinnerListener vsl;
    ArrayList<String> vibrationStrings;
    ArrayAdapter adapter;
    Spinner spinner;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(null, null, savedInstanceState);
        view = inflater.inflate(R.layout.login, container, false);
        vibrationStrings = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.vibrations)));
        Main = (MainActivity) getActivity();

        String[] LogInDetails = Main.returnLogIn();
        if (LogInDetails != null) {
            EditText Username = (EditText) view.findViewById(R.id.Username);
            EditText Password = (EditText) view.findViewById(R.id.Password);
            Username.setText(LogInDetails[0]);
            Password.setText(LogInDetails[1]);
        }
        SharedPreferences Notifications = this.getActivity().getSharedPreferences("Notifications", 0);
        Boolean toggle = Notifications.getBoolean("toggle", false);
        Switch tb = (Switch) view.findViewById(R.id.switchNotifications);
        tb.setChecked(toggle);
        RelativeLayout notification = (RelativeLayout) view.findViewById(R.id.notificationLayout);
        if (toggle) {
            notification.setVisibility(LinearLayout.VISIBLE);
        } else {
            notification.setVisibility(LinearLayout.GONE);
        }
        SharedPreferences LEDSettings = this.getActivity().getSharedPreferences("LEDSettings", 0);
        int r = LEDSettings.getInt("redValue", 0);
        int g = LEDSettings.getInt("greenValue", 33);
        int b = LEDSettings.getInt("blueValue", 71);
        drawCircle(r, g, b);

        spinner = (Spinner) view.findViewById(R.id.vibrations);
        adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, vibrationStrings);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        SharedPreferences vibratePattern = this.getActivity().getSharedPreferences("vibratePattern", 0);
        String current = vibratePattern.getString("vibratePattern", "null");
        if (vsl == null) {
            vsl = new VibrateSpinnerListener();
        }
        if (current.equals(getResources().getString(R.string.buzz1))) {
            ;
            spinner.setSelection(0);
        } else if (current.equals(getResources().getString(R.string.buzz2))) {
            spinner.setSelection(1);
        } else if (current.equals(getResources().getString(R.string.buzz2))) {
            spinner.setSelection(2);
        } else {
            finishedDialog(true);
        }
        vsl.main = (MainActivity) getActivity();
        vsl.firstCall = true;
        spinner.setOnItemSelectedListener(vsl);
        vsl.handler = handler;


        SharedPreferences mealsToNotifyFor = getActivity().getSharedPreferences("mealsToNotifyFor", 0);
        ToggleButton Lunch = (ToggleButton) view.findViewById(R.id.toggleLunch);
        ToggleButton Dinner = (ToggleButton) view.findViewById(R.id.toggleDinner);
        Lunch.setChecked(mealsToNotifyFor.getBoolean("Lunch", true));
        Lunch.setChecked(mealsToNotifyFor.getBoolean("Dinner", true));

        return view;
    }


    public void finishedDialog(Boolean savedCustom) {
        String changeCustom = getResources().getString(R.string.changeCustom);
        int vibrationStringsSize = vibrationStrings.size();
        if (savedCustom) {
            vsl.last = 3;
            spinner.setSelection(3, true);
            if (!vibrationStrings.get(vibrationStringsSize - 1).equals(changeCustom)) {
                vibrationStrings.add(changeCustom);
                adapter.notifyDataSetChanged();
            }
        } else {
            if (vsl.last == 4) {
                vsl.last = 3;
                spinner.setSelection(3, true);
            } else {
                spinner.setSelection(vsl.lastBeforeDialog, true);
                if (vibrationStrings.get(vibrationStringsSize - 1).equals(changeCustom)) {
                    vibrationStrings.remove(vibrationStringsSize - 1);
                    System.out.println("Removed");
                    adapter.notifyDataSetChanged();
                }
            }


        }
    }

    public void drawCircle(int r, int g, int b) {
        Bitmap.Config conf = Bitmap.Config.ARGB_8888;
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int size = (int) (32 * metrics.density);
        Bitmap bmp = Bitmap.createBitmap(size, size, conf);
        Canvas c = new Canvas(bmp);
        int radius = size / 2;
        Paint paint = new Paint();
        paint.setARGB(255, r, g, b);
        c.drawCircle(radius, radius, radius, paint);
        ImageView img = (ImageView) view.findViewById(R.id.ledColour);
        img.setImageBitmap(bmp);
    }

}