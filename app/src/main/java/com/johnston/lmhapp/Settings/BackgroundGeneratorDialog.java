package com.johnston.lmhapp.Settings;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.johnston.lmhapp.ImageGeneratorAsync;
import com.johnston.lmhapp.R;

import java.io.IOException;

/**
 * Created by Tom on 18/11/2014.
 */
public class BackgroundGeneratorDialog extends DialogFragment {
    View view;


    static BackgroundGeneratorDialog newInstance() {
        BackgroundGeneratorDialog f = new BackgroundGeneratorDialog();
        Bundle args = new Bundle();
        f.setArguments(args);
        return f;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        view = inflater.inflate(R.layout.set_background_dialog, null);
        builder.setView(view)
                .setTitle("Change the Wallpaper")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
                        Display display = wm.getDefaultDisplay();
                        int width;
                        int height;
                        if (android.os.Build.VERSION.SDK_INT >= 13) {
                            Point size = new Point();
                            display.getSize(size);
                            height = size.y;
                        } else {
                            height = display.getHeight();
                        }
                        final Activity activity = getActivity();
                        Handler handler = new Handler() {
                            @Override
                            public void handleMessage(Message message) {
                                Bitmap bmp = (Bitmap) message.obj;
                                WallpaperManager wallpaperManager = WallpaperManager.getInstance(activity.getApplicationContext());
                                try {
                                    wallpaperManager.setBitmap(bmp);
                                    Toast toast = Toast.makeText(activity.getApplicationContext(), "Wallpaper Set.", Toast.LENGTH_SHORT);
                                    toast.show();
                                } catch (IOException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                    Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Something has gone wrong.", Toast.LENGTH_SHORT);
                                    toast.show();
                                }
                            }
                        };
                        width = height * 2;
                        SharedPreferences LogIn = getActivity().getSharedPreferences("LogIn", 0);
                        String username = LogIn.getString("Username", "Fail");
                        new ImageGeneratorAsync().execute(username, handler, width, height, getActivity().getApplicationContext(), true);


                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        return builder.create();
    }


}