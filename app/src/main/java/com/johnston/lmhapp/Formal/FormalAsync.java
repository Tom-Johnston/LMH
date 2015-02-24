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
public class FormalAsync extends AsyncTask<Object, String, Void> {
    private Handler statusHandler;

    @Override
    protected Void doInBackground(Object[] params) {
        try {
            SSLContext sslContext = (SSLContext) params[0];
            Handler handler = (Handler) params[1];
            statusHandler = (Handler) params[2];
            ArrayList<String> entries = new ArrayList<>();
            URL formalHome = new URL("https://intranet.lmh.ox.ac.uk/mealbookings.asp");
            HttpsURLConnection formalHomec = (HttpsURLConnection) formalHome.openConnection();
            formalHomec.setSSLSocketFactory(sslContext.getSocketFactory());
            formalHomec.setInstanceFollowRedirects(true);
            BufferedReader in = new BufferedReader(new InputStreamReader(formalHomec.getInputStream(), "UTF-8"));
            String inputLine;
            StringBuilder a = new StringBuilder();
            Boolean inTheBody = false;
            while (true) {
                inputLine = in.readLine();
                if (inputLine == null) {
                    break;
                }
                if (inputLine.contains("Meal Bookings Administration")) {
                    inTheBody = true;
                }
                if (inTheBody) {
                    a.append(inputLine);
                }
            }
            formalHomec.disconnect();
            String substring;
            String result = a.toString();
            int end = result.indexOf("</tr>");
            int start;
            Boolean firstButton = true;
            while (true) {
                end = result.indexOf("</td>", end + 1);
                if (end > -1) {
                    start = result.lastIndexOf("<td>", end) + 4;
                    substring = result.substring(start, end).trim();
                    if (substring.contains("<FORM")) {
                        if (firstButton) {
                            start = substring.indexOf("name='book'");
                            start = substring.indexOf("'", start + 11);
                            substring = substring.substring(start + 1, substring.indexOf("'", start + 1));
                        }
                        firstButton ^= true;
                    } else if (substring.trim().equals("-")) {
                        firstButton ^= true;
                    }
                    entries.add(substring);

                } else {
                    break;
                }
            }
            publishProgress("Getting Formal Details");

            ArrayList<String> listOfMeals = new ArrayList<>();
            ArrayList<ArrayList<String>> listOfListsOfPeople = new ArrayList<>();
            for (int i = 5; i < entries.size(); i += 7) {
                String post = "mealbookingState=viewattendees&book=" + URLEncoder.encode(entries.get(i), "UTF-8");
                post = post.replaceAll(" ", "+");
                URL url = new URL("https://intranet.lmh.ox.ac.uk/mealbookings.asp");
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

                in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"));
                a = new StringBuilder();
                while (true) {
                    inputLine = in.readLine();
                    if (inputLine == null) {
                        break;
                    }
                    a.append(inputLine);
                }
                String meals = "";
                result = a.toString();
                urlConnection.disconnect();
                start = result.indexOf("Additional information: ");
                end = start;
                int startOfTable = result.indexOf("<table", start);
                while (true) {
                    start = result.indexOf(">", end);
                    if (start > -1) {
                        if (start > startOfTable) {
                            break;
                        }
                        end = result.indexOf("<", start + 1);
                        if (result.substring(start, end).length() > 1)
                            meals = meals + "\n" + result.substring(start + 1, end).trim();
                    } else {
                        break;
                    }
                }
                if(meals.length()>0) {
                    meals = meals.substring(1);
                }
                ArrayList<String> listOfNames = new ArrayList<>();
                start = result.indexOf("<tr>", startOfTable);
                start = result.indexOf("<tr>", start + 1);
                int endOfTable = result.indexOf("</table", startOfTable);

//            Ignore the first line of the list/table;
                while (true) {

                    if (start < 0 || endOfTable < start) {
                        break;
                    }
                    start = result.indexOf("<td>", start);
                    if (start < 0 || endOfTable < start) {
                        break;
                    }
                    end = result.indexOf("</td>", start);
                    listOfNames.add(result.substring(start + 4, end));

                    start = result.indexOf("<td>", end);
                    if (start < 0 || endOfTable < start) {
                        break;
                    }
                    end = result.indexOf("</td>", start);
                    listOfNames.add(result.substring(start + 4, end));
                    start = result.indexOf("</tr>", start + 1);
//                This part has been hardcoded as I only want the first two entries and the table has titles for more.
//                Not that they have ever been used...

                }
                listOfMeals.add(meals);
                listOfListsOfPeople.add(listOfNames);
            }
            handler.obtainMessage(0, entries).sendToTarget();
            handler.obtainMessage(1, listOfMeals).sendToTarget();
            handler.obtainMessage(2, listOfListsOfPeople).sendToTarget();
            publishProgress("Finished");
        } catch (MalformedURLException e) {
            statusHandler.obtainMessage(-1).sendToTarget();
            e.printStackTrace();
        } catch (IOException e) {
            statusHandler.obtainMessage(-1).sendToTarget();
            e.printStackTrace();
        }


        return null;
    }

    @Override
    protected void onProgressUpdate(String... values) {
        statusHandler.obtainMessage(0, values[0]).sendToTarget();
    }
}
