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
//        EditText red = (EditText)view.findViewById(R.id.red);
//        EditText green = (EditText)view.findViewById(R.id.green);
//        EditText blue = (EditText)view.findViewById(R.id.blue);
//        red.setText(Integer.toString(r));
//        green.setText(Integer.toString(g));
//        blue.setText(Integer.toString(b));
//        TextWatcher redWatcher = new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//                try {
//                    int nr=Integer.parseInt(editable.toString());
//                    if(nr<0||nr>255){
//                        Toast toast = Toast.makeText(getActivity().getApplicationContext(),"Integer must be between 0 and 255", Toast.LENGTH_SHORT);
//                        toast.show();
//                    }else{
//                        r=nr;
//                        drawCircle();
//                    }
//
//                } catch (NumberFormatException nfe) {
//                    Toast toast = Toast.makeText(getActivity().getApplicationContext(),"Unable to parse Integer", Toast.LENGTH_SHORT);
//                    toast.show();
//                }
//            }
//        };
//        TextWatcher greenWatcher = new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//                try {
//                    int nr=Integer.parseInt(editable.toString());
//                    if(nr<0||nr>255){
//                        Toast toast = Toast.makeText(getActivity().getApplicationContext(),"Integer must be between 0 and 255", Toast.LENGTH_SHORT);
//                        toast.show();
//                    }else{
//                        g=nr;
//                        drawCircle();
//                    }
//
//                } catch (NumberFormatException nfe) {
//                    Toast toast = Toast.makeText(getActivity().getApplicationContext(),"Unable to parse Integer", Toast.LENGTH_SHORT);
//                    toast.show();
//                }
//            }
//        };
//        TextWatcher blueWatcher = new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//                try {
//                    int nr=Integer.parseInt(editable.toString());
//                    if(nr<0||nr>255){
//                        Toast toast = Toast.makeText(getActivity().getApplicationContext(),"Integer must be between 0 and 255", Toast.LENGTH_SHORT);
//                        toast.show();
//                    }else{
//                        b=nr;
//                        drawCircle();
//                    }
//
//                } catch (NumberFormatException nfe) {
//                    Toast toast = Toast.makeText(getActivity().getApplicationContext(),"Unable to parse Integer", Toast.LENGTH_SHORT);
//                    toast.show();
//                }
//            }
//        };
//        red.addTextChangedListener(redWatcher);
//        green.addTextChangedListener(greenWatcher);
//        blue.addTextChangedListener(blueWatcher);
        drawCircle(r, g, b);
        System.out.println(toggle);
        tb.setChecked(toggle);
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
//        img.setBackground(new BitmapDrawable(bmp));
    }

}