package com.johnston.lmhapp.Battels;

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
 * Created by Tom on 27/10/2014.
 */
public class BattelsAsync extends AsyncTask<Object, String, Void> {
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
            String Total = "£0.00";
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
                            Entries.add(inputLine.substring(start + 1, end).trim());
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
                        Entries.add("¬" + inputLine.substring(start + 1, end).trim());
                    }
                }
                if (inputLine.contains("</b>") && inTable) {
                    end = inputLine.indexOf("</b>");
                    end = inputLine.indexOf("</b>", end + 1);
                    start = inputLine.lastIndexOf(">", end);
                    if (start + 1 != end) {
                        Total = inputLine.substring(start + 1, end).trim();
                    }
                }

                if (inputLine.contains("</table>")) {
                    inTable = false;
                }
            }
            publishProgress("Getting Better Descriptions");
            for (int i = 1; i < Entries.size(); i += 4) {
                if (Entries.get(i).contains("¬")) {
                    Entries.set(i,Entries.get(i).substring(1));
                    URL infoURL = new URL("https://intranet.lmh.ox.ac.uk/navbillingdetail.asp?invno=" + Entries.get(i));
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
                                Entries.set(i + 1, inputLine.substring(start + 1, end).trim());
                                break;
                            }
                        }
                    }
                }

            }

            Entries.add("");
            Entries.add("Total");
            Entries.add("");
            Entries.add(Total);
            publishProgress("Finished");
            handler.obtainMessage(0, Entries).sendToTarget();
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
            Status.setVisibility(View.GONE);
//            TODO Sort out this. Maybe an animation to hide it but it should scroll with the list.
            Status.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Status.setText("");

                }
            }, 3000);

        }
    }

}
