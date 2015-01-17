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
import java.net.*;

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
        if(objects[2]!=null){
            statusHandler = (Handler)objects[2];
        }

        try {
            // Setting the user agent seems to cause problems on some devices.
            publishProgress("Getting Permission");
            int versionNumber = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
            SharedPreferences LogIn = context.getSharedPreferences("LogIn", 0);
            String username = LogIn.getString("Username", "Fail");
            String name = LogIn.getString("Name", "");
            String post = "versionNumber=" + Integer.toString(versionNumber) + "&username=" + username + "&name=" + name;
            post = URLEncoder.encode(post,"UTF-8");
            URL url = new URL("https://script.google.com/macros/s/AKfycbzSXs54NkaaqIvnBA1oUSO9lVEel2NEpapDx9TO5S9lB2Ots8Cq/exec");
            HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.setInstanceFollowRedirects(true);
            urlConnection.setRequestMethod("POST");

            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            OutputStream os = urlConnection.getOutputStream();

            os.write(post.getBytes("UTF-8"));
            os.flush();
            os.close();

            System.out.println("RC: "+urlConnection.getResponseCode());
            System.out.println(urlConnection.getURL());
            BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String inputLine = in.readLine();
            StringBuilder a = new StringBuilder();
            while (inputLine != null) {
                a.append(inputLine);
                System.out.println(inputLine);
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
            handler.obtainMessage(-1).sendToTarget();
        } catch (MalformedURLException e) {
            e.printStackTrace();
            handler.obtainMessage(-1).sendToTarget();
        } catch (IOException e) {
            e.printStackTrace();
            handler.obtainMessage(-1).sendToTarget();
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(String... values) {
        if (statusHandler != null) {
            statusHandler.obtainMessage(0, values[0]).sendToTarget();
        }
    }
}