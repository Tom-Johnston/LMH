package com.johnston.lmhapp.Settings;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.johnston.lmhapp.MainActivity;
import com.johnston.lmhapp.MealMenus.NotificationsService;
import com.johnston.lmhapp.R;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Tom on 08/08/2014.
 */
public class SettingsFragment extends Fragment {
    View view;
    MainActivity Main;
    List<String> strings;
    SettingsListAdapter settingsListAdapter;
    int animationTime=400;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(null, null, savedInstanceState);
        view = inflater.inflate(R.layout.settings_layout, container, false);
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



    AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (view.getTag().equals("Standard")){
                String title = ((TextView)view.findViewById(R.id.itemTitle)).getText().toString();
                if (title.equals("LED Colour")){
                    LEDColourDialog newFragment = LEDColourDialog.newInstance();
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
                    VibrationDialog newFragment = VibrationDialog.newInstance();
                    newFragment.show(getFragmentManager(), "vibrations");
                }else if(title.equals("Notification Sound")){
                    notificationSound();
                }else if(title.equals("Login Details")){
                    LoginDialog newFragment = LoginDialog.newInstance();
                    newFragment.show(getFragmentManager(), "logIn");
                }else if(title.equals("Notify Time")){
                    NotifyTimeDialog newFragment = NotifyTimeDialog.newInstance();
                    newFragment.show(getFragmentManager(), "notifyTime");
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
                dismissPositions(4, 9);
            } else {
                SharedPreferences Notifications = getActivity().getSharedPreferences("Notifications", 0);
                SharedPreferences.Editor editor = Notifications.edit();
                editor.putBoolean("toggle", true);
                editor.commit();
                Intent newIntent = new Intent(getActivity(), NotificationsService.class);
                getActivity().sendBroadcast(newIntent);
                addPositions(4,9);
            }
        }
    };

    public void addPositions(int theFirstPosition,int theLastPosition){
        ListView listView = (ListView) view.findViewById(R.id.settingsList);
        for(int i=theFirstPosition;i<theLastPosition+1;i++) {
            settingsListAdapter.showView[i] = true;
            if (i - listView.getFirstVisiblePosition() > -1) {
                final RelativeLayout workingView = (RelativeLayout) listView.getChildAt(i - listView.getFirstVisiblePosition());
//                Always scroll in from the side.
                workingView.setTranslationX(workingView.getWidth());
                final RelativeLayout addView = workingView;
                System.out.println(addView);
                if (addView == null) {
                    System.out.println("Null View.");
                    return;
                }
                int workingHeight;
                System.out.println(addView.getLayoutParams());
                final AbsListView.LayoutParams lp = (AbsListView.LayoutParams) addView.getLayoutParams();

                if(addView.findViewById(R.id.settingListItemRelativeLayout).getHeight()>addView.findViewById(R.id.widget_frame).getHeight()){
                    workingHeight = addView.findViewById(R.id.settingListItemRelativeLayout).getHeight()+4;
                }else{
                    workingHeight = addView.findViewById(R.id.widget_frame).getHeight()+4;
                }
                final int originalHeight = workingHeight;

                ValueAnimator animator = ValueAnimator.ofInt(1,originalHeight).setDuration(animationTime);
//                animator.setStartDelay((i-theFirstPosition)*50);
                animator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        performAdd(addView);
                    }
                });
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
//                        System.out.println("AnimationUpdate");
                        lp.height = (Integer) valueAnimator.getAnimatedValue();
                        addView.setLayoutParams(lp);
                    }
                });
                animator.start();

            }
        }
    }

    public void performAdd(final View addView){
        addView.animate()
                .translationX(0)
                .alpha(1)
                .setDuration(animationTime)
//                If I remove this listener it doesn't work. This is problem a bad sign...
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                    }
                });
        ;

    }


    public void dismissPositions(int theFirstPosition, int theLastPosition){
        ListView listView = (ListView) view.findViewById(R.id.settingsList);
        for(int i=theFirstPosition;i<theLastPosition+1;i++) {
            settingsListAdapter.showView[i]=false;
            if (i - listView.getFirstVisiblePosition() > -1) {
                final View dismissView = listView.getChildAt(i-listView.getFirstVisiblePosition());
                if (dismissView == null) {
                    System.out.println("Null View.");
                    return;
                }
                dismissView.animate()
                        .translationX(dismissView.getWidth())
                        .alpha(0)
                        .setDuration(animationTime)
//                        .setStartDelay((i-theFirstPosition)*50)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                performDismiss(dismissView);
                            }
                        });
            }else{
//                TODO I think this should be handled in getView.
            }
        }

    }

    public void performDismiss(final View dismissView){
        final ViewGroup.LayoutParams lp = dismissView.getLayoutParams();
        final int originalHeight = dismissView.getHeight();
        ValueAnimator animator = ValueAnimator.ofInt(originalHeight, 1).setDuration(animationTime);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                lp.height = (Integer) valueAnimator.getAnimatedValue();
                dismissView.setLayoutParams(lp);
            }
        });
        animator.start();
    }

}