package com.johnston.lmhapp.Settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.johnston.lmhapp.R;

import java.util.List;

/**
 * Created by Jake on 13/11/2014.
 */
public class SettingsListAdapter extends ArrayAdapter<String> {
    Context context;
    int resourceId;
    List<String> strings;
    public Handler switchHandler;

    public SettingsListAdapter(Context context, int resource, List<String> objects) {
        super(context, resource, objects);
        this.context = context;
        resourceId = resource;
        strings = objects;
    }

    @Override
    public boolean isEnabled(int position) {
        if (strings.get(position).equals("SSO Login Details")) {
            return false;
        } else if (strings.get(position).equals("Notification Settings")) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public int getCount() {
        return strings.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (strings.get(position).equals("SSO Login Details") || strings.get(position).equals("Notification Settings")) {
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.settings_header_item, parent, false);
                convertView.setTag("Header");
                ((TextView) convertView.findViewById(R.id.itemTitle)).setText(strings.get(position));
            } else if (!convertView.getTag().equals("Header")) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.settings_header_item, parent, false);
                convertView.setTag("Header");
                ((TextView) convertView.findViewById(R.id.itemTitle)).setText(strings.get(position));
            }
        } else {


            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.settings_list_item, parent, false);
                convertView.setTag("Standard");
            } else if (!convertView.getTag().equals("Standard")) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.settings_list_item, parent, false);
                convertView.setTag("Standard");
            } else {
                ((LinearLayout) convertView.findViewById(R.id.widget_frame)).removeAllViews();
            }
            ((TextView) convertView.findViewById(R.id.itemTitle)).setText(strings.get(position));

            if (strings.get(position).equals("Notifications")) {
                LinearLayout view = (LinearLayout) convertView.findViewById(R.id.widget_frame);
                view.addView(SwitchViewCreator(position));
            } else if (strings.get(position).equals("Lunch")) {
                LinearLayout view = (LinearLayout) convertView.findViewById(R.id.widget_frame);
                view.addView(CheckBoxViewCreator(position, view));
            } else if (strings.get(position).equals("Dinner")) {
                LinearLayout view = (LinearLayout) convertView.findViewById(R.id.widget_frame);
                view.addView(CheckBoxViewCreator(position, view));
            } else if (strings.get(position).equals("Notify Time")) {
            }

        }
        return convertView;
    }

    private View CheckBoxViewCreator(int position, View parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        CheckBox checkBox = (CheckBox) inflater.inflate(R.layout.checkbox, (ViewGroup) parent, false);
        SharedPreferences mealsToNotifyFor = context.getSharedPreferences("mealsToNotifyFor", 0);
        if(mealsToNotifyFor.getBoolean(strings.get(position),true)){
         checkBox.setChecked(true);
        }else{
            checkBox.setChecked(false);
        }
        return checkBox;
    }

    private View SwitchViewCreator(int position) {
        SwitchCompat switchCompat = new SwitchCompat(context);
        switchCompat.setTag("Switch");
        if (strings.get(position).equals("Notifications")) {
            SharedPreferences Notifications = context.getSharedPreferences("Notifications", 0);
            Boolean toggle = Notifications.getBoolean("toggle", false);
            switchCompat.setChecked(toggle);
        }

        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                switchHandler.obtainMessage(0, isChecked).sendToTarget();
            }
        });

        return switchCompat;
    }
}
