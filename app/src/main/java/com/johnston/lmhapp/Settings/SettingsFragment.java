package com.johnston.lmhapp.Settings;

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.johnston.lmhapp.MealMenus.NotificationsService;
import com.johnston.lmhapp.R;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Tom on 08/08/2014.
 */
public class SettingsFragment extends Fragment {
    final Handler switchHandler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            if (!(Boolean) message.obj) {
                SharedPreferences Notifications = getActivity().getSharedPreferences("Notifications", 0);
                SharedPreferences.Editor editor = Notifications.edit();
                editor.putBoolean("toggle", false);
                editor.apply();
                Intent newIntent = new Intent(getActivity(), NotificationsService.class);
                getActivity().sendBroadcast(newIntent);
                for (int i = 4; i < 10; i++) {
                    strings.remove(4);
                }
                settingsRecyclerAdapter.notifyItemRangeRemoved(4, 6);

            } else {
                SharedPreferences Notifications = getActivity().getSharedPreferences("Notifications", 0);
                SharedPreferences.Editor editor = Notifications.edit();
                editor.putBoolean("toggle", true);
                editor.apply();
                Intent newIntent = new Intent(getActivity(), NotificationsService.class);
                getActivity().sendBroadcast(newIntent);
                String[] stringArray = getResources().getStringArray(R.array.settings);
                for (int i = 9; i >= 4; i--) {
                    strings.add(4, stringArray[i]);
                    settingsRecyclerAdapter.notifyItemInserted(4);
                }

            }
        }
    };
    View view;
    ArrayList<String> strings;
    SettingsRecyclerAdapter settingsRecyclerAdapter;
    SwitchCompat switchCompat;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(null, null, savedInstanceState);
        switchCompat = null;
//        Get rid of the old view.
        view = inflater.inflate(R.layout.settings_layout, container, false);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);
        if (strings == null) {
            strings = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.settings)));
            SharedPreferences Notifications = getActivity().getSharedPreferences("Notifications", 0);
            Boolean toggle = Notifications.getBoolean("toggle", false);
            if (!toggle) {
                for (int i = 4; i < 10; i++) {
                    strings.remove(4);
                }
            }
        }
        if (settingsRecyclerAdapter == null) {
            settingsRecyclerAdapter = new SettingsRecyclerAdapter(this.getActivity(), strings);
            settingsRecyclerAdapter.switchHandler = switchHandler;
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(settingsRecyclerAdapter);
        recyclerView.setItemAnimator(new SettingsAnimator());
        return view;
    }

    public void itemClicked(View view) {
        String title = ((TextView) view.findViewById(R.id.itemTitle)).getText().toString();
        switch (title) {
            case "LED Colour": {
                LEDColourDialog newFragment = LEDColourDialog.newInstance();
                newFragment.show(getFragmentManager(), "missiles");
                break;
            }
            case "Lunch":
            case "Dinner":
                CheckBox checkBox = (CheckBox) ((LinearLayout) view.findViewById(R.id.widget_frame)).getChildAt(0);
                if (checkBox.isChecked()) {
                    checkBox.setChecked(false);
                } else {
                    checkBox.setChecked(true);
                }
                toggleMealNotification(checkBox, title);
                break;
            case "Notifications":
                switchCompat = (SwitchCompat) ((LinearLayout) view.findViewById(R.id.widget_frame)).getChildAt(0);
                if (switchCompat.isChecked()) {
                    switchCompat.setChecked(false);
                } else {
                    switchCompat.setChecked(true);
                }
                break;
            case "Vibration": {
                VibrationDialog newFragment = VibrationDialog.newInstance();
                newFragment.show(getFragmentManager(), "vibrations");
                break;
            }
            case "Notification Sound":
                notificationSound();
                break;
            case "Login Details": {
                LoginDialog newFragment = LoginDialog.newInstance();
                newFragment.show(getFragmentManager(), "logIn");
                break;
            }
            case "Notify Time": {
                NotifyTimeDialog newFragment = NotifyTimeDialog.newInstance();
                newFragment.show(getFragmentManager(), "notifyTime");
                break;
            }
            case "Set Wallpaper": {
                BackgroundGeneratorDialog newFragment = BackgroundGeneratorDialog.newInstance();
                newFragment.show(getFragmentManager(), "backgroundGenerator");
                break;
            }
            case "Set Live Wallpaper": {

            }
            case "Refresh Time": {
                RefreshTimeDialog newFragment = RefreshTimeDialog.newInstance();
                newFragment.show(getFragmentManager(), "refreshTimeDialog");
                break;
            }
        }
    }

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

    public void toggleMealNotification(CheckBox checkBox, String title) {
        SharedPreferences mealsToNotifyFor = getActivity().getSharedPreferences("mealsToNotifyFor", 0);
        SharedPreferences.Editor editor = mealsToNotifyFor.edit();
        editor.putBoolean(title, checkBox.isChecked());
        Intent intent = new Intent(this.getActivity(), NotificationsService.class);
        this.getActivity().sendBroadcast(intent);
        editor.apply();
    }

}