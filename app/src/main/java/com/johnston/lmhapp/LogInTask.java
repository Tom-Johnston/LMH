package com.johnston.lmhapp;

import android.os.AsyncTask;
import android.widget.TextView;

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
public class LogInTask extends AsyncTask<Object, String, Boolean> {
    TextView Status;
    MainActivity Main;

    @Override
    protected Boolean doInBackground(Object[] Objects) {
        Status = (TextView) Objects[3];
        SSLContext context = (SSLContext) Objects[0];
        String args = (String) Objects[1];
        String args2 = (String) Objects[2];
        Main = (MainActivity) Objects[4];
        CookieManager manager = (CookieManager) Objects[5];
        try {
            URL url = new URL("https://intranet.lmh.ox.ac.uk/mealmenus.asp");
            System.out.println("Attempt");
            HttpsURLConnection urlc = (HttpsURLConnection) url.openConnection();
            urlc.setSSLSocketFactory(context.getSocketFactory());
            System.out.println(urlc.getResponseCode());
            System.out.println(urlc.getURL());
            if (urlc.getURL().toString().equals("https://intranet.lmh.ox.ac.uk/mealmenus.asp")) {
                System.out.println("Already Logged In");
                publishProgress("Already Logged In");
                return true;
            } else {
                System.out.println("Redirected to:" + urlc.getURL());
                publishProgress("Redirected");
                URL regUrl = new URL("https://webauth.ox.ac.uk/login");
                System.out.println("Logging in");

                String nurl = urlc.getURL().toString();
                int end = nurl.indexOf(";");
                String RT = nurl.substring(nurl.indexOf("=") + 1, end);
                String ST = nurl.substring(end + 4, nurl.indexOf(";", end + 1));
                args2 = (URLEncoder.encode(args2, "UTF-8"));
//                System.out.println(args2);
                String post = "RT=" + RT + "&ST=" + ST + "&LC=&login=yes&username=" + args + "&password=" + args2 + "&Submit=Login";
//                System.out.println(new String(post.getBytes("UTF-8"), "ISO-8859-1"));
                String type = "application/x-www-form-urlencoded";
//                System.out.println("POST:" + post);
                HttpsURLConnection conn = (HttpsURLConnection) regUrl.openConnection();
//            System.out.println(conn.getResponseCode());
                conn.setSSLSocketFactory(context.getSocketFactory());
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", type);
                conn.setRequestProperty("charset", "UTF-8");
                conn.setRequestProperty("Content-Length", String.valueOf(post.length()));
//	        System.out.println(String.valueOf(post.length()));
                conn.setDoOutput(true);
                conn.setDoInput(true);
//            System.out.println(conn.getResponseCode());
                OutputStream os = conn.getOutputStream();
                os.write(post.getBytes("UTF-8"));
                os.flush();
                os.close();
                System.out.println(conn.getResponseCode());
                publishProgress("Logging in");
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        conn.getInputStream(), "UTF-8"));
                String inputLine;
                StringBuilder a = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    a.append(inputLine);
                }
                in.close();
                System.out.println(a.toString());
                if (a.indexOf("Error") > 0) {
                    System.out.println("Error");
                    publishProgress("Error");
                    manager.getCookieStore().removeAll();
                    return false;
                }
                System.out.println("Successful Login");
                publishProgress("Successful Login");
                int start = a.indexOf("https://intranet.lmh.ox.ac.uk/mealmenus.asp?WEBAUTHR");
                System.out.println("Getting Access Cookie");
                publishProgress("Getting Access Cookie");
                String Beast = a.substring(start, a.indexOf(";", start));
//	        System.out.println(Beast);
                URL allow = new URL(Beast);
                System.out.println(allow);
                HttpsURLConnection allowc = (HttpsURLConnection) allow.openConnection();
                allowc.setSSLSocketFactory(context.getSocketFactory());

                allowc.connect();
                System.out.println(allowc.getResponseCode());
                publishProgress("Finished Logging In");
                return true;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            publishProgress("Something went wrong");
            System.out.println("ME");
            return false;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            publishProgress("Something went wrong");
            System.out.println("UEE");
            return false;
        } catch (ProtocolException e) {
            e.printStackTrace();
            publishProgress("Something went wrong");
            System.out.println("PE");
            return false;
        } catch (IOException e) {
            publishProgress("Network Error?");
            e.printStackTrace();
            return false;
        }


    }

    @Override
    protected void onProgressUpdate(String... values) {
        if (Status != null) {
        Status.setText(values[0]);
        }
    }

    @Override
    protected void onPostExecute(Boolean passed) {
        if (passed) {
            Main.Initialise();
        }
    }

}
