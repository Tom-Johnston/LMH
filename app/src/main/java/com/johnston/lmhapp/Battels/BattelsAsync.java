package com.johnston.lmhapp.Battels;

import android.os.AsyncTask;
import android.os.Handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;

/**
 * Created by Tom on 27/10/2014.
 */
public class BattelsAsync extends AsyncTask<Object, String, Void> {
    private Handler statusHandler;

    @Override
    protected Void doInBackground(Object[] objects) {
//        Going to need custom SSL again.

        try {
            SSLContext sslContext = (SSLContext) objects[0];
            statusHandler = (Handler) objects[1];
            Handler handler = (Handler) objects[2];

            publishProgress("Getting Account Information");
            URL battelsURL = new URL("https://intranet.lmh.ox.ac.uk/navbilling.asp");
            HttpsURLConnection battelsConn = (HttpsURLConnection) battelsURL.openConnection();
            battelsConn.setSSLSocketFactory(sslContext.getSocketFactory());
            battelsConn.setInstanceFollowRedirects(true);
            BufferedReader in = new BufferedReader(new InputStreamReader(battelsConn.getInputStream(), "UTF-8"));
            String inputLine;
            String Total = "£0.00";
            int start;
            int end;
            byte column = 0;
            ArrayList<String> entries = new ArrayList<>();
            entries.add("Getting Better Descriptions");
            Boolean inTable = false;
            while (true) {
                inputLine = in.readLine();
                if (inputLine == null) {
                    break;
                }
                if (inputLine.contains("</td>")) {
                    inTable = true;
                    end = inputLine.indexOf("</td>");
                    start = inputLine.lastIndexOf(">", end);
                    if (start + 1 != end) {
                        column++;
                        if (column == 5) {
                            column = 0;
                        }else if(column==4){
                            String s = inputLine.substring(start + 1, end).trim();
                            if (s.length()-s.lastIndexOf(".")==2){
                                s+="0";
                            }
                            entries.add(s);
                        } else {
                            entries.add(inputLine.substring(start + 1, end).trim());
                        }
                    }
                }
                if (inputLine.contains("</a>") && inTable) {
                    end = inputLine.indexOf("</a>");
                    start = inputLine.lastIndexOf(">", end);
                    if (start + 1 != end) {
                        column++;
                        if (column == 5) {
                            column = 0;
                        }
                        entries.add("¬" + inputLine.substring(start + 1, end).trim());
                    }
                }
                if (inputLine.contains("</b>") && inTable) {
                    end = inputLine.indexOf("</b>");
                    end = inputLine.indexOf("</b>", end + 1);
                    start = inputLine.lastIndexOf(">", end);
                    if (start + 1 != end) {
                        Total = inputLine.substring(start + 1, end).trim();
                        if(Total.length()-Total.lastIndexOf(".")==2){
                            Total+="0";
                        }
                    }
                }

                if (inputLine.contains("</table>")) {
                    inTable = false;
                }
            }

            entries.add("");
            entries.add("Total");
            entries.add("");
            entries.add(Total);
            handler.obtainMessage(0, entries).sendToTarget();
            publishProgress("Getting Better Descriptions");
            for (int i = 2; i < entries.size(); i += 4) {
                if (entries.get(i).contains("¬")) {
                    entries.set(i, entries.get(i).substring(1));
                    URL infoURL = new URL("https://intranet.lmh.ox.ac.uk/navbillingdetail.asp?invno=" + entries.get(i));
                    HttpsURLConnection infoConn = (HttpsURLConnection) infoURL.openConnection();
                    infoConn.setSSLSocketFactory(sslContext.getSocketFactory());
                    infoConn.setInstanceFollowRedirects(true);
                    in = new BufferedReader(new InputStreamReader(infoConn.getInputStream(), "UTF-8"));
                    while (true) {
                        inputLine = in.readLine();
                        if (inputLine == null) {
                            break;
                        }
                        if (inputLine.contains("</td>")) {

                            end = inputLine.indexOf("</td>");
                            start = inputLine.lastIndexOf(">", end);
                            if (start + 1 != end) {
                                entries.set(i + 1, inputLine.substring(start + 1, end).trim());
                                break;
                            }
                        }
                    }
                }

            }
            entries.set(0, "Finished");
            publishProgress("Finished");
            handler.obtainMessage(1, entries).sendToTarget();
        } catch (MalformedURLException e) {
            e.printStackTrace();
            statusHandler.obtainMessage(-1,"Error getting battels: MalformedURLException").sendToTarget();
        } catch (IOException e) {
            e.printStackTrace();
            statusHandler.obtainMessage(-1,"Error getting battels: IOException. Check your network connection.").sendToTarget();
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(String... values) {
        statusHandler.obtainMessage(0, values[0]).sendToTarget();
    }


}
