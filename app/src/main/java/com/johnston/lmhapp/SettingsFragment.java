package com.johnston.lmhapp;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.SwitchCompat;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Tom on 08/08/2014.
 */
public class SettingsFragment extends Fragment {
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
    List<String> strings;
    SettingsListAdapter settingsListAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(null, null, savedInstanceState);
        view = inflater.inflate(R.layout.settings_layout, container, false);
        vibrationStrings = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.vibrations)));
        Main = (MainActivity) getActivity();
        ListView listView = (ListView) view.findViewById(R.id.settingsList);
        strings = Arrays.asList(getResources().getStringArray(R.array.settings));
        settingsListAdapter = new SettingsListAdapter(this.getActivity(),R.layout.settings_list_item,strings);
        settingsListAdapter.switchHandler = switchHandler;
        listView.setAdapter(settingsListAdapter);
        listView.setOnItemClickListener(itemClickListener);
        SharedPreferences Notifications = this.getActivity().getSharedPreferences("Notifications", 0);
        Boolean toggle = Notifications.getBoolean("toggle", false);
        switchHandler.obtainMessage(0,toggle).sendToTarget();
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
                    adapter.notifyDataSetChanged();
                }
            }


        }
    }

    AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//            TODO Vibration and notify time.
            if (view.getTag().equals("Standard")){
                String title = ((TextView)view.findViewById(R.id.itemTitle)).getText().toString();
                if (title.equals("LED Colour")){
                    LedColorDialog newFragment = LedColorDialog.newInstance();
                    newFragment.show(getFragmentManager(), "missiles");
                }else if(title.equals("Lunch")||title.equals("Dinner")){
                    CheckBox checkBox = (CheckBox)((LinearLayout)view.findViewById(R.id.widget_frame)).getChildAt(0);
                    if(checkBox.isChecked()){
                        checkBox.setChecked(false);
                    }else{
                        checkBox.setChecked(true);
                    }
                    toggleMealNotification(checkBox,title);
                }else if(title.equals("Notifications")){

                    SwitchCompat switchCompat = (SwitchCompat)((LinearLayout)view.findViewById(R.id.widget_frame)).getChildAt(0);
                    if(switchCompat.isChecked()){
                        switchCompat.setChecked(false);
                    }else{
                        switchCompat.setChecked(true);
                    }
                }else if(title.equals("Vibration")){

                }else if(title.equals("Notification Sound")){
                    notificationSound();
                }else if(title.equals("Login Details")){
                    LoginDialog newFragment = LoginDialog.newInstance();
                    newFragment.show(getFragmentManager(), "logIn");
                }
            }

        }
    };


    public void notificationSound() {
        SharedPreferences NotificationSound = getActivity().getSharedPreferences("NotificationSound", 0);
        Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Tone");
        if (NotificationSound.contains("SoundURI")) {
            Uri uri = Uri.parse(NotificationSound.getString("SoundURI", "This is irrelevant"));
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, uri);
        } else {
            Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, uri);
        }
        this.startActivityForResult(intent, 5);
    }

    public void toggleMealNotification(CheckBox checkBox,String title) {
        SharedPreferences mealsToNotifyFor = getActivity().getSharedPreferences("mealsToNotifyFor", 0);
        SharedPreferences.Editor editor = mealsToNotifyFor.edit();
        editor.putBoolean(title, checkBox.isChecked());
//        Replace an existing notification..
        Intent intent = new Intent(this.getActivity(), NotificationsService.class);
        this.getActivity().sendBroadcast(intent);
        SharedPreferences widgetEnabled = getActivity().getSharedPreferences("widgetEnabled", 0);
//       Update the widget.
        if (widgetEnabled.getBoolean("widgetEnabled", false)) {
            Intent updateWidget = new Intent(this.getActivity(), MealMenuWidgetReceiver.class);
            this.getActivity().sendBroadcast(updateWidget);
        }
        editor.commit();
    }

    final Handler switchHandler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            if (!(Boolean)message.obj) {
                SharedPreferences Notifications = getActivity().getSharedPreferences("Notifications", 0);
                SharedPreferences.Editor editor = Notifications.edit();
                editor.putBoolean("toggle", false);
                editor.commit();
                Intent newIntent = new Intent(getActivity(), NotificationsService.class);
                getActivity().sendBroadcast(newIntent);
                settingsListAdapter.strings = Arrays.asList(getResources().getStringArray(R.array.settings2));
                settingsListAdapter.notifyDataSetChanged();
            } else {
                SharedPreferences Notifications = getActivity().getSharedPreferences("Notifications", 0);
                SharedPreferences.Editor editor = Notifications.edit();
                editor.putBoolean("toggle", true);
                editor.commit();
                settingsListAdapter.strings = Arrays.asList(getResources().getStringArray(R.array.settings));
                settingsListAdapter.notifyDataSetChanged();

            }
        }
    };

}