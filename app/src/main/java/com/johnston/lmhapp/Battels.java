package com.johnston.lmhapp;

import android.os.AsyncTask;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

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
public class Battels extends AsyncTask<Object, String, Void> {
    TextView Status;

    @Override
    protected Void doInBackground(Object[] objects) {
//        Going to need custom SSL again.

        try {
            SSLContext sslContext = (SSLContext) objects[0];
            Status = (TextView) ((View) objects[1]).findViewById(R.id.Status);
            publishProgress("Getting Account Information");
            Handler handler = (Handler) objects[2];
            URL battelsURL = new URL("https://intranet.lmh.ox.ac.uk/navbilling.asp");
            HttpsURLConnection battelsConn = (HttpsURLConnection) battelsURL.openConnection();
            battelsConn.setSSLSocketFactory(sslContext.getSocketFactory());
            battelsConn.setInstanceFollowRedirects(true);
            BufferedReader in = new BufferedReader(new InputStreamReader(battelsConn.getInputStream(), "UTF-8"));
            String inputLine;
            int start;
            int end;
            byte column = 0;
            ArrayList<String> Entries = new ArrayList<String>();
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
                        } else {
                            Entries.add(inputLine.substring(start + 1, end));
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
                        Entries.add("¬" + inputLine.substring(start + 1, end));
                    }
                }
                if (inputLine.contains("</table>")) {
                    inTable = false;
                }
            }
            publishProgress("Getting Better Descriptions");
            for (int i = 1; i < Entries.size(); i += 4) {
                System.out.println(Entries.get(i));
                if (Entries.get(i).contains("¬")) {
                    URL infoURL = new URL("https://intranet.lmh.ox.ac.uk/navbillingdetail.asp?invno=" + Entries.get(i).substring(1));
                    System.out.println(infoURL);
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
                                System.out.println(inputLine.substring(start + 1, end));
                                Entries.set(i + 1, inputLine.substring(start + 1, end));
                                break;
                            }
                        }
                    }
                }

            }
            publishProgress("Finished");
            handler.obtainMessage(0, Entries).sendToTarget();
            System.out.println(Entries);
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
}
