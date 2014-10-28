package com.johnston.lmhapp;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;

/**
 * Created by Tom on 08/08/2014.
 */
public class SetupLogIn extends Fragment {
    View view;
    MainActivity Main;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(null, null, savedInstanceState);
        view = inflater.inflate(R.layout.login, container, false);
        System.out.println("SetUpLogIn");
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
        SharedPreferences LEDSettings = this.getActivity().getSharedPreferences("LEDSettings", 0);
        int r = LEDSettings.getInt("redValue", 0);
        int g = LEDSettings.getInt("greenValue", 33);
        int b = LEDSettings.getInt("blueValue", 71);
        drawCircle(r, g, b);
        System.out.println(toggle);
        tb.setChecked(toggle);

        RelativeLayout notification = (RelativeLayout) view.findViewById(R.id.notificationLayout);
        if (toggle){
            notification.setVisibility(LinearLayout.VISIBLE);
        }else{
            notification.setVisibility(LinearLayout.INVISIBLE);
        }

        return view;
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