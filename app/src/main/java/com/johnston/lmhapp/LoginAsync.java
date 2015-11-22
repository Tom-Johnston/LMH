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
class LoginAsync extends AsyncTask<Object, String, Void> {
    private Handler handler;

    @Override
    protected Void doInBackground(Object[] Objects) {
        handler = (Handler) Objects[3];
        SSLContext context = (SSLContext) Objects[0];
        String args = (String) Objects[1];
        String args2 = (String) Objects[2];
        Handler loginHandler = (Handler) Objects[4];
        CookieManager manager = (CookieManager) Objects[5];
        publishProgress("Started");
        try {
            URL url = new URL("https://intranet.lmh.ox.ac.uk/mealmenus.asp");
            HttpsURLConnection urlc = (HttpsURLConnection) url.openConnection();
            urlc.setSSLSocketFactory(context.getSocketFactory());
            urlc.getResponseCode();
            if (urlc.getURL().toString().equals("https://intranet.lmh.ox.ac.uk/mealmenus.asp")) {
                publishProgress("Already Logged In");
                loginHandler.obtainMessage(1).sendToTarget();
                return null;
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
                    handler.obtainMessage(-1, "Error Logging In").sendToTarget();
                    handler.obtainMessage(MainActivity.STATUS_UPDATE, "Error Logging In").sendToTarget();
                    return null;
                }
                publishProgress("Successful Login");
                int start = a.indexOf("href=\"",a.indexOf("a class=\"go_button\""))+6;
                publishProgress("Getting Access Cookie");
                String Beast = a.substring(start, a.indexOf("\"", start));
                URL allow = new URL(Beast);
                HttpsURLConnection allowc = (HttpsURLConnection) allow.openConnection();
                allowc.setSSLSocketFactory(context.getSocketFactory());
                allowc.connect();
                allowc.getResponseCode();

//                No JavaScript continue.
//                Yes, this is just an adapted version from EPOSAsync. Yes, just adapting code is bad practice.
                StringBuilder b = new StringBuilder();
                BufferedReader continueReader = new BufferedReader(new InputStreamReader(
                        allowc.getInputStream(), "UTF-8"));
                while ((inputLine = continueReader.readLine()) != null) {
                    b.append(inputLine);
                }
                continueReader.close();
                allowc.disconnect();

                int RSStart = b.indexOf("value=\"") + 7;
                int RSEnd = b.indexOf(">", RSStart);
                String RelayState = b.substring(RSStart, RSEnd - 2);
                int Start = b.indexOf("value=\"", RSStart) + 7;
                int End = b.indexOf(">", Start);
                String SAMLRespose = b.substring(Start, End - 2);
                SAMLRespose = SAMLRespose.replace("=", "%3D");
//            Charset.forName("UTF-8").encode(SAMLRespose);
                post = "RelayState=" + RelayState + "&SAMLResponse=" + SAMLRespose;
                post = post.replace("&#x3a;", "%3A");
                post = post.replace("+", "%2B");
//              post= URLEncoder.encode(post, "UTF-8");
                publishProgress("No JavaScript Continue");
                URL Final = new URL("https://intranet.lmh.ox.ac.uk/Shibboleth.sso/SAML2/POST");
                HttpsURLConnection connl = (HttpsURLConnection) Final.openConnection();
                connl.setInstanceFollowRedirects(true);
                connl.setRequestMethod("POST");
                connl.addRequestProperty("REFERER", "https://idp.shibboleth.ox.ac.uk/idp/profile/SAML2/Redirect/SSO");
                String typel = "application/x-www-form-urlencoded";
                connl.setRequestProperty("Content-Type", typel);
//                connl.setRequestProperty("Content-Length", String.valueOf(post.length()));
//                When the above line is in the code it breaks. Something to do with expecting x bytes and receiving 0. No idea why it receives 0.
                connl.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
                connl.setAllowUserInteraction(true);
                connl.setDoOutput(true);
                connl.setDoInput(true);
                OutputStream osl = connl.getOutputStream();
                osl.write(post.getBytes());
                osl.flush();
                osl.close();
                connl.getResponseCode();

                publishProgress("Finished Logging In");
                loginHandler.obtainMessage(1).sendToTarget();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            handler.obtainMessage(-1, "Error logging in: MalformedURLException").sendToTarget();
            handler.obtainMessage(MainActivity.STATUS_UPDATE, "Error logging in: MalformedURLException").sendToTarget();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            handler.obtainMessage(-1, "Error logging in: UnsupportedEncodingException").sendToTarget();
            handler.obtainMessage(MainActivity.STATUS_UPDATE, "Error logging in: UnsupportedEncodingException").sendToTarget();
        } catch (ProtocolException e) {
            e.printStackTrace();
            handler.obtainMessage(-1, "Error logging in: ProtocolException").sendToTarget();
            handler.obtainMessage(MainActivity.STATUS_UPDATE, "Error logging in: ProtocolException").sendToTarget();
        } catch (IOException e) {
            handler.obtainMessage(-1, "Error logging in: IOExeption. Check your network connection").sendToTarget();
            handler.obtainMessage(MainActivity.STATUS_UPDATE, "Error logging in: IOExeption. Check your network connection").sendToTarget();
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(String... values) {
        handler.obtainMessage(MainActivity.STATUS_UPDATE, values[0]).sendToTarget();
    }
}

