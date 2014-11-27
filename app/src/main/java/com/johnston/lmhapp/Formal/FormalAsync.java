package com.johnston.lmhapp.Formal;

import android.os.AsyncTask;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;
import com.johnston.lmhapp.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;

/**
 * Created by Tom on 11/11/2014.
 */
public class FormalAsync extends AsyncTask<Object,String,Void> {
TextView Status;

    @Override
    protected Void doInBackground(Object[] params) {
        try {
            SSLContext sslContext = (SSLContext)params[0];
            Handler handler = (Handler)params[1];
            Status = (TextView) ((View) params[2]).findViewById(R.id.Status);
            ArrayList<String> entries =new ArrayList<String>();
            URL formalHome = new URL("https://intranet.lmh.ox.ac.uk/mealbookings.asp");
            HttpsURLConnection formalHomec = (HttpsURLConnection) formalHome.openConnection();
            formalHomec.setSSLSocketFactory(sslContext.getSocketFactory());
            formalHomec.setInstanceFollowRedirects(true);
            BufferedReader in = new BufferedReader(new InputStreamReader(formalHomec.getInputStream(), "UTF-8"));
            String inputLine;
            StringBuilder a = new StringBuilder();
            Boolean inTheBody = false;
            while(true){
                inputLine = in.readLine();
                if(inputLine==null){
                    break;
                }
                if(inputLine.contains("Meal Bookings Administration")){
                    inTheBody=true;
                }
                if(inTheBody){
                    a.append(inputLine);
                }
            }
            formalHomec.disconnect();
            String substring;
            String id;
            String result = a.toString();
            int end=result.indexOf("</tr>");
            int start;
            Boolean firstButton = true;
            while(true){
                end = result.indexOf("</td>",end+1);
                if(end>-1) {
                    start = result.lastIndexOf("<td>", end)+4;
                    substring = result.substring(start, end).trim();
                    if (substring.contains("<FORM")) {
                        if(firstButton){
                        start = substring.indexOf("name='book'");
                        start = substring.indexOf("'",start+11);
                        substring = substring.substring(start+1,substring.indexOf("'",start+1));
                        }
                        firstButton^=true;
                    }else if(substring.equals("-")){
                        firstButton^=true;
                    }
                        entries.add(substring);

                }else{
                    break;
                }
            }
            publishProgress("Finished");
            handler.obtainMessage(0,entries).sendToTarget();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return null;
    }

    @Override
    protected void onProgressUpdate(String... values) {
        Status.setText(values[0]);
    }

    @Override
    protected void onPostExecute(Void v) {
        if (Status.getText().toString().equals("Finished")) {
            Status.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Status.setText("");
                }
            }, 3000);
        }
    }
}
