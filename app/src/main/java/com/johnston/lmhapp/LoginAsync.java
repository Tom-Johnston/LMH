package com.johnston.lmhapp;

import android.os.AsyncTask;
import android.os.Handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.CookieManager;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;

/**
 * Created by Tom on 28/05/2014.
 */
class LoginAsync extends AsyncTask<Object, String, Boolean> {
    private Handler statusHandler;
    private MainActivity Main;

    @Override
    protected Boolean doInBackground(Object[] Objects) {
        statusHandler = (Handler) Objects[3];
        SSLContext context = (SSLContext) Objects[0];
        String args = (String) Objects[1];
        String args2 = (String) Objects[2];
        Main = (MainActivity) Objects[4];
        CookieManager manager = (CookieManager) Objects[5];
        publishProgress("Started");
        try {
            URL url = new URL("https://intranet.lmh.ox.ac.uk/mealmenus.asp");
            HttpsURLConnection urlc = (HttpsURLConnection) url.openConnection();
            urlc.setSSLSocketFactory(context.getSocketFactory());
            urlc.getResponseCode();
            if (urlc.getURL().toString().equals("https://intranet.lmh.ox.ac.uk/mealmenus.asp")) {
                publishProgress("Already Logged In");
                return true;
            } else {
                manager.getCookieStore().removeAll();
                urlc = (HttpsURLConnection) url.openConnection();
                urlc.setSSLSocketFactory(context.getSocketFactory());
                urlc.getResponseCode();

                publishProgress("Redirected");
                URL regUrl = new URL("https://webauth.ox.ac.uk/login");

                String nurl = urlc.getURL().toString();
                int end = nurl.indexOf(";");
                String RT = nurl.substring(nurl.indexOf("=") + 1, end);
                String ST = nurl.substring(end + 4, nurl.indexOf(";", end + 1));
                args2 = (URLEncoder.encode(args2, "UTF-8"));
                String post = "RT=" + RT + "&ST=" + ST + "&LC=&login=yes&username=" + args + "&password=" + args2 + "&Submit=Login";
                String type = "application/x-www-form-urlencoded";
                HttpsURLConnection conn = (HttpsURLConnection) regUrl.openConnection();
                conn.setSSLSocketFactory(context.getSocketFactory());
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", type);
                conn.setRequestProperty("charset", "UTF-8");
                conn.setRequestProperty("Content-Length", String.valueOf(post.length()));
                conn.setDoOutput(true);
                conn.setDoInput(true);
                OutputStream os = conn.getOutputStream();
                os.write(post.getBytes("UTF-8"));
                os.flush();
                os.close();
                conn.getResponseCode();
                publishProgress("Logging in");
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        conn.getInputStream(), "UTF-8"));
                String inputLine;
                StringBuilder a = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    a.append(inputLine);
                }
                in.close();
                if (a.indexOf("Error") > 0) {
                    manager.getCookieStore().removeAll();
                    statusHandler.obtainMessage(-1,"Error Logging In").sendToTarget();
                    return false;
                }
                publishProgress("Successful Login");
                int start = a.indexOf("https://intranet.lmh.ox.ac.uk/mealmenus.asp?WEBAUTHR");
                publishProgress("Getting Access Cookie");
                String Beast = a.substring(start, a.indexOf(";", start));
                URL allow = new URL(Beast);
                HttpsURLConnection allowc = (HttpsURLConnection) allow.openConnection();
                allowc.setSSLSocketFactory(context.getSocketFactory());

                allowc.connect();
                allowc.getResponseCode();
                publishProgress("Finished Logging In");
                return true;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            statusHandler.obtainMessage(-1,"Error logging in: MalformedURLException").sendToTarget();
            return false;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            statusHandler.obtainMessage(-1,"Error logging in: UnsupportedEncodingException").sendToTarget();
            return false;
        } catch (ProtocolException e) {
            e.printStackTrace();
            statusHandler.obtainMessage(-1,"Error logging in: ProtocolException").sendToTarget();
            return false;
        } catch (IOException e) {
            statusHandler.obtainMessage(-1,"Error logging in: IOExeption. Check your network connection").sendToTarget();
            e.printStackTrace();
            return false;
        }


    }

    @Override
    protected void onProgressUpdate(String... values) {
        statusHandler.obtainMessage(0, values[0]).sendToTarget();
    }

    @Override
    protected void onPostExecute(Boolean passed) {
        if (passed) {
            Main.Initialise();
        }
    }

}
