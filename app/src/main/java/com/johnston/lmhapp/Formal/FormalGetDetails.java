package com.johnston.lmhapp.Formal;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;

/**
 * Created by Tom on 11/11/2014.
 */
public class FormalGetDetails extends AsyncTask<Object,Void,Void> {
    @Override
    protected Void doInBackground(Object[] params) {
        SSLContext sslContext = (SSLContext)params[0];

        try {
            String post="mealbookingState=viewattendees&book="+params[1];
            post = post.replaceAll(" ","+");
            post= URLEncoder.encode(post, "UTF-8");
            URL url =  new URL("https://intranet.lmh.ox.ac.uk/mealbookings.asp");
            HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.setSSLSocketFactory(sslContext.getSocketFactory());
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            urlConnection.setRequestProperty("charset", "UTF-8");
            urlConnection.setRequestProperty("Content-Length", String.valueOf(post.length()));
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            OutputStream os = urlConnection.getOutputStream();
            os.write(post.getBytes("UTF-8"));
            os.flush();
            os.close();
            urlConnection.getResponseCode();
            BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"));
            String inputLine;
            StringBuilder a = new StringBuilder();
            while(true){
                inputLine = in.readLine();
                if(inputLine==null){
                    break;
                }
                    a.append(inputLine);
            }
            ArrayList<String> meals = new ArrayList<String>();
            String result = a.toString();
            urlConnection.disconnect();
            int start = result.indexOf("Additional information: ");
            int end;
            int startOfTable=result.indexOf("<table",start);
            while(true){
                start = result.indexOf(">",start);
                if(start>-1){
                    if(start>startOfTable){
                        break;
                    }
                    end = result.indexOf("<", start + 1);
                    if(result.substring(start,end).length()>1)
                  meals.add(result.substring(start,end).trim());
                }else{
                    break;
                }
            }
            ArrayList<String> listOfNames = new ArrayList<String>();
            start = result.indexOf("<tr>",startOfTable);
            start = result.indexOf("<tr>",start);
            int endOfTable = result.indexOf("</table",startOfTable);
//            Ignore the first line of the list/table;
            while(true){
                start = result.indexOf("<tr>",start);
                if(start<0||endOfTable<start){
                  break;
                }

                start=result.indexOf("<td>",start);
                end=result.indexOf("</td>",start);
                listOfNames.add(result.substring(start+4,end));

                start=result.indexOf("<td>",start);
                end=result.indexOf("</td>",start);
                listOfNames.add(result.substring(start+4,end));

//                This part has been hardcoded as I only want the first two entries and the table has titles for more.

            }





        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return null;
    }
}
