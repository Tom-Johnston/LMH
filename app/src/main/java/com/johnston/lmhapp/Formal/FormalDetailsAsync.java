package com.johnston.lmhapp.Formal;

import android.os.AsyncTask;
import android.os.Handler;

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
public class FormalDetailsAsync extends AsyncTask<Object,Void,Void> {
    @Override
    protected Void doInBackground(Object[] params) {
        SSLContext sslContext = (SSLContext)params[0];
        Handler handler = (Handler)params[2];

        try {
            params[1]= URLEncoder.encode((String)params[1], "UTF-8");
            String post="mealbookingState=viewattendees&book="+params[1];
            post = post.replaceAll(" ","+");
            System.out.println(post);
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
                System.out.println(inputLine);
                    a.append(inputLine);
            }
            String meals="";
            String result = a.toString();
            urlConnection.disconnect();
            int start = result.indexOf("Additional information: ");
            int end=start;
            int startOfTable=result.indexOf("<table",start);
            while(true){
                start = result.indexOf(">",end);
                if(start>-1){
                    if(start>startOfTable){
                        break;
                    }
                    end = result.indexOf("<", start + 1);
                    if(result.substring(start,end).length()>1)
                    meals=meals+"\n"+result.substring(start+1,end).trim();
                }else{
                    break;
                }
            }
            meals=meals.substring(1);
            ArrayList<String> listOfNames = new ArrayList<String>();
            start = result.indexOf("<tr>",startOfTable);
            start = result.indexOf("<tr>",start+1);
            int endOfTable = result.indexOf("</table",startOfTable);

//            Ignore the first line of the list/table;
            while(true){

                System.out.println(start);
                if(start<0||endOfTable<start){
                  break;
                 }
                start=result.indexOf("<td>",start);
                if(start<0||endOfTable<start){
                    break;
                }
                end=result.indexOf("</td>",start);
                listOfNames.add(result.substring(start+4,end));

                start=result.indexOf("<td>",end);
                if(start<0||endOfTable<start){
                    break;
                }
                end=result.indexOf("</td>",start);
                listOfNames.add(result.substring(start+4,end));
                start = result.indexOf("</tr>",start+1);
//                This part has been hardcoded as I only want the first two entries and the table has titles for more.
//                Not that they have ever been used...

            }
            System.out.println("meals"+meals);
            System.out.println("listOfName"+listOfNames);
            handler.obtainMessage(0,meals).sendToTarget();
            handler.obtainMessage(1,listOfNames).sendToTarget();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
