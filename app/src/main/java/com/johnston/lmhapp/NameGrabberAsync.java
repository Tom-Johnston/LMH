package com.johnston.lmhapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;

/**
 * Created by Tom on 07/11/2014.
 */
public class NameGrabberAsync extends AsyncTask<Object, Void, Void> {
    @Override
    protected Void doInBackground(Object[] objects) {

        try {
            SSLContext sslContext = (SSLContext) objects[0];
            Context context = (Context) objects[1];
            Handler handler = (Handler) objects[2];
            URL nameURL = new URL("https://intranet.lmh.ox.ac.uk/mydetails.asp");
            HttpsURLConnection nameConn = (HttpsURLConnection) nameURL.openConnection();
            nameConn.setSSLSocketFactory(sslContext.getSocketFactory());
            nameConn.setInstanceFollowRedirects(true);
            BufferedReader in = new BufferedReader(new InputStreamReader(nameConn.getInputStream(), "UTF-8"));
            String inputLine;
            String name = "";
            String nameSegment;
            int end;
            while (true) {
                inputLine = in.readLine();
                if (inputLine == null) {
                    break;
                }

                if (inputLine.contains("Firstname") || inputLine.contains("Lastname")) {
                    in.readLine();
                    inputLine = in.readLine();
                    end = inputLine.indexOf("</td>");
                    name = name + inputLine.substring(inputLine.lastIndexOf("<td>", end) + 4, end) + " ";
                }
            }
            SharedPreferences LogIn = context.getSharedPreferences("LogIn", 0);
            SharedPreferences.Editor editor = LogIn.edit();
            editor.putString("Name", name.trim());
            editor.commit();
            handler.obtainMessage(0, name.trim()).sendToTarget();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return null;
    }
}
