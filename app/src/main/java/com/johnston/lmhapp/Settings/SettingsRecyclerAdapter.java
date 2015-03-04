package com.johnston.lmhapp.Settings;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.johnston.lmhapp.MealMenus.NotificationsService;
import com.johnston.lmhapp.R;

import java.util.List;

/**
 * Created by Johnston on 11/12/2014.
 */
class SettingsRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public Handler switchHandler;
    private final Context context;
    private final List<String> strings;

    public SettingsRecyclerAdapter(Context context, List<String> objects) {
        this.context = context;
        strings = objects;
    }

    @Override
    public int getItemViewType(int position) {
        if (strings.get(position).equals("SSO Login Details") || strings.get(position).equals("Notification Settings") || strings.get(position).equals("Other Settings")) {
            return 0;
        } else {
            return 1;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v;
        if (viewType == 0) {
            v = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.settings_header_item, viewGroup, false);
            return new HeaderHolder(v);
        } else {
            v = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.settings_list_item, viewGroup, false);

            return new EntryHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (getItemViewType(position) == 0) {
            ((HeaderHolder) holder).itemTitle.setText(strings.get(position));
        } else {
            EntryHolder entryHolder = (EntryHolder) holder;
            entryHolder.itemTitle.setText(strings.get(position));

            entryHolder.widgetFrame.removeAllViews();
            entryHolder.itemCaption.setVisibility(View.GONE);
            if (strings.get(position).equals("Notifications")) {
                entryHolder.widgetFrame.addView(SwitchViewCreator(position));
            } else if (strings.get(position).equals("Lunch")) {
                entryHolder.widgetFrame.addView(CheckBoxViewCreator(position, entryHolder.widgetFrame));
            } else if (strings.get(position).equals("Dinner")) {
                entryHolder.widgetFrame.addView(CheckBoxViewCreator(position, entryHolder.widgetFrame));
            } else if (strings.get(position).equals("Refresh Time")) {
                entryHolder.itemCaption.setText("Set how often to check for a new menu.");
                entryHolder.itemCaption.setVisibility(View.VISIBLE);
            }
        }
    }

    private View CheckBoxViewCreator(int position, View parent) {
        final String title = strings.get(position);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final CheckBox checkBox = (CheckBox) inflater.inflate(R.layout.checkbox, (ViewGroup) parent, false);
        SharedPreferences mealsToNotifyFor = context.getSharedPreferences("mealsToNotifyFor", 0);
        if (mealsToNotifyFor.getBoolean(title, true)) {
            checkBox.setChecked(true);
        } else {
            checkBox.setChecked(false);
        }
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences mealsToNotifyFor = context.getSharedPreferences("mealsToNotifyFor", 0);
                SharedPreferences.Editor editor = mealsToNotifyFor.edit();
                editor.putBoolean(title, checkBox.isChecked());
                editor.apply();
                Intent intent = new Intent(context, NotificationsService.class);
                context.sendBroadcast(intent);
            }
        });
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

    @Override
    public int getItemCount() {
        return strings.size();
    }

    public static class HeaderHolder extends RecyclerView.ViewHolder {
        public TextView itemTitle;

        public HeaderHolder(View itemView) {
            super(itemView);
            itemTitle = (TextView) itemView;
        }
    }

    public static class EntryHolder extends RecyclerView.ViewHolder {
        public TextView itemTitle;
        public TextView itemCaption;
        public LinearLayout widgetFrame;
        public RelativeLayout settingsListItemRelativeLayout;

        public EntryHolder(View itemView) {
            super(itemView);
            itemTitle = (TextView) itemView.findViewById(R.id.itemTitle);
            itemCaption = (TextView) itemView.findViewById(R.id.itemCaption);
            widgetFrame = (LinearLayout) itemView.findViewById(R.id.widget_frame);
            settingsListItemRelativeLayout = (RelativeLayout) itemView.findViewById(R.id.settingListItemRelativeLayout);
        }
    }


}
