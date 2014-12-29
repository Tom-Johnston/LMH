package com.johnston.lmhapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Johnston on 29/12/2014.
 */
public class PermissionAsync extends AsyncTask<Object, String, Void> {
    Handler statusHandler;

    @Override
    protected Void doInBackground(Object[] objects) {
        Context context = (Context) objects[0];
        Handler handler = (Handler) objects[1];
        statusHandler = (Handler)objects[2];
        try {
            publishProgress("Getting Permission");
            int versionNumber = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
            SharedPreferences LogIn = context.getSharedPreferences("LogIn", 0);
            String username = LogIn.getString("Username", "Fail");
            String name = LogIn.getString("Name", "");
            String post = "versionNumber=" + Integer.toString(versionNumber) + "&username=" + username + "&=name" + name;


            URL url = new URL("https://script.google.com/macros/s/AKfycbzSXs54NkaaqIvnBA1oUSO9lVEel2NEpapDx9TO5S9lB2Ots8Cq/exec");
            HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Length", String.valueOf(post.length()));
            urlConnection.setRequestProperty("User-Agent", "LMH App");
            OutputStream os = urlConnection.getOutputStream();
            os.write(post.getBytes("UTF-8"));
            os.flush();
            os.close();
            urlConnection.getResponseCode();
            BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"));
            String inputLine = in.readLine();
            StringBuilder a = new StringBuilder();
            while (inputLine != null) {
                a.append(inputLine);
                inputLine = in.readLine();
            }
            in.close();
            String result = a.toString();
            if (result.contains("Success")) {
                publishProgress("Permission Granted");
                handler.obtainMessage(0).sendToTarget();
            } else {
                publishProgress("Permission Denied");
                handler.obtainMessage(1, result).sendToTarget();
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(String... values) {
        statusHandler.obtainMessage(0, values[0]).sendToTarget();
    }
}