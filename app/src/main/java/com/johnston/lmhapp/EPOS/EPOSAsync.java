package com.johnston.lmhapp.EPOS;

import android.os.AsyncTask;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import com.johnston.lmhapp.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;
//import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Tom on 05/06/2014.
 */
public class EPOSAsync extends AsyncTask<Object, String, String[]> {
    final String UserAgent = "Mozilla/5.0 (Linux; Android 4.1.1; Nexus 7 Build/JRO03D) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.166  Safari/535.19";
    View view;
    TextView Status;
    Handler handler;

    @Override
    protected String[] doInBackground(Object[] Objects) {
        String[] Amounts = new String[3];
        String GetCookie;
        view = (View) Objects[1];
        Status = (TextView) view.findViewById(R.id.Status);
        handler = (Handler) Objects[2];
        try {
            String inputLine;
            URL testLogIn = new URL("https://www.upay.co.uk/balance.aspx");
            HttpsURLConnection testconn = (HttpsURLConnection) testLogIn.openConnection();
            testconn.setRequestProperty("User-Agent", UserAgent);
            testconn.setInstanceFollowRedirects(true);
            testconn.getResponseCode();
            if (!testLogIn.equals(testconn.getURL())) {


                URL url = new URL("https://www.upay.co.uk/Shibboleth.sso/Login?entityID=https://registry.shibboleth.ox.ac.uk/idp&target=https://www.upay.co.uk/shib/sso.aspx ");
                HttpsURLConnection urlc = (HttpsURLConnection) url.openConnection();
                urlc.getResponseCode();
                String newURL = urlc.getURL().toString();
                //LOTs to do here
                HttpsURLConnection Cookiec;
                if (newURL.contains("login")) {
                    publishProgress("SSO Confirmation");
                    BufferedReader in = new BufferedReader(new InputStreamReader(
                            urlc.getInputStream(), "UTF-8"));

                    StringBuilder a = new StringBuilder();
                    while ((inputLine = in.readLine()) != null) {
                        a.append(inputLine);
                    }
                    in.close();
                    urlc.disconnect();
                    int Start = a.indexOf("https://idp.shibboleth.ox.ac.uk/idp");
                    int End = a.indexOf("\"><span");
                    GetCookie = a.substring(Start, End);
                    URL Cookie = new URL(GetCookie);
                    Cookiec = (HttpsURLConnection) Cookie.openConnection();
                } else {
                    Cookiec = urlc;
                }
                StringBuilder b = new StringBuilder();
                BufferedReader Cookier = new BufferedReader(new InputStreamReader(
                        Cookiec.getInputStream(), "UTF-8"));
                while ((inputLine = Cookier.readLine()) != null) {
                    b.append(inputLine);
                }
                Cookier.close();
                Cookiec.disconnect();
                int RSStart = b.indexOf("value=\"") + 7;
                int RSEnd = b.indexOf(">", RSStart);
                String RelayState = b.substring(RSStart, RSEnd - 2);
                int Start = b.indexOf("value=\"", RSStart) + 7;
                int End = b.indexOf(">", Start);
                String SAMLRespose = b.substring(Start, End - 2);
                SAMLRespose = SAMLRespose.replace("=", "%3D");
//            Charset.forName("UTF-8").encode(SAMLRespose);
                String post = "RelayState=" + RelayState + "&SAMLResponse=" + SAMLRespose;
                post = post.replace("&#x3a;", "%3A");
                post = post.replace("+", "%2B");
//              post= URLEncoder.encode(post, "UTF-8");
                publishProgress("No JavaScript Continue");
                URL Final = new URL("https://www.upay.co.uk/Shibboleth.sso/SAML2/POST");
                HttpsURLConnection connl = (HttpsURLConnection) Final.openConnection();
                connl.setInstanceFollowRedirects(true);
                connl.setRequestMethod("POST");
                connl.addRequestProperty("REFERER", "https://idp.shibboleth.ox.ac.uk/idp/profile/SAML2/Redirect/SSO");
                String typel = "application/x-www-form-urlencoded";
                connl.setRequestProperty("Content-Type", typel);
//                connl.setRequestProperty("Content-Length", String.valueOf(post.length()));
//                When the above line is in the code it breaks. Something to do with expecting x bytes and receiving 0. No idea why it receives 0.
                connl.setRequestProperty("User-Agent", UserAgent);
                connl.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
                connl.setAllowUserInteraction(true);
                connl.setDoOutput(true);
                connl.setDoInput(true);
                OutputStream osl = connl.getOutputStream();
                osl.write(post.getBytes());
                osl.flush();
                osl.close();
                connl.getResponseCode();

                publishProgress("Getting .COOKIECASHLESS");
                URL Test = new URL("https://www.upay.co.uk/shib/sso.aspx");
                HttpsURLConnection conn2 = (HttpsURLConnection) Test.openConnection();
                conn2.setRequestProperty("User-Agent", UserAgent);
                conn2.setRequestProperty("Accept-Encoding", "identity");
                conn2.setInstanceFollowRedirects(true);

                for (int i = 0; ; i++) {
                    String headerName = conn2.getHeaderFieldKey(i);
                    String headerValue = conn2.getHeaderField(i);

//                        For some reason .COOKIECASHLESS isn't set. The cookie is therefore created manually.

                    if (headerName == null && headerValue == null) {
                        break;
                    } else {
                        if (headerValue.contains(".COOKIECASHLESS=")) {
                            publishProgress("Creating .COOKIECASHLESS");
                            String cookieValue = headerValue.substring(16, headerValue.indexOf(";"));
                            HttpCookie myCookie = new HttpCookie(".COOKIECASHLESS", cookieValue);
                            myCookie.setVersion(0);
                            myCookie.setPath("/");
                            myCookie.setMaxAge(890000);
//                            890,000 is 14 minutes 50 seconds. I believe the cookie should be suitable for 15 minutes.
                            cookieValue = myCookie.toString();
                            CookieManager cookieManager = (CookieManager) Objects[0];
                            CookieHandler.setDefault(cookieManager);
                            CookieStore CookieJar = cookieManager.getCookieStore();
                            URI Test5 = new URI("https://www.upay.co.uk");
                            CookieJar.add(Test5, myCookie);
                        }
                    }
                }
            }
            URL Test2 = new URL("https://www.upay.co.uk/balance.aspx");
            publishProgress("Getting the balance");
            HttpsURLConnection conn3 = (HttpsURLConnection) Test2.openConnection();
            conn3.setRequestProperty("User-Agent", UserAgent);
            conn3.setInstanceFollowRedirects(true);
            conn3.getResponseCode();
//            String inputLine;
            BufferedReader Reader3 = new BufferedReader(new InputStreamReader(
                    conn3.getInputStream(), "UTF-8"));
            while ((inputLine = Reader3.readLine()) != null) {
                if (inputLine.contains("Account balance: &#163;")) {
                    int AccountBalanceStart = inputLine.indexOf("Account balance: &#163;") + 23;
                    int AccountBalanceEnd = inputLine.indexOf("<", AccountBalanceStart);
                    String AccountBalance = inputLine.substring(AccountBalanceStart, AccountBalanceEnd);
                    Amounts[0] = "£" + AccountBalance;

                }
                if (inputLine.contains("Available Token Balance:")) {
                    int TokenBalanceStart = inputLine.indexOf("Available Token Balance:") + 25;
                    int TokenBalanceEnd = inputLine.indexOf("<", TokenBalanceStart);
                    String TokenBalance = inputLine.substring(TokenBalanceStart, TokenBalanceEnd);
                    Amounts[1] = "£" + TokenBalance;

                }
                if (inputLine.contains("Last updated:")) {
                    int dateStart = inputLine.indexOf("Last updated:") + 14;
                    int dateEnd = inputLine.indexOf("<", dateStart);
                    String TokenBalance = inputLine.substring(dateStart, dateEnd);
                    Amounts[2] = TokenBalance;
                }

            }

            Reader3.close();
            conn3.disconnect();
            publishProgress("Getting last ten transactions");
            URL recentTransactions = new URL("https://www.upay.co.uk/FavouriteLastItems.aspx?Page=2");
            HttpsURLConnection transactionConnection = (HttpsURLConnection) recentTransactions.openConnection();
            transactionConnection.setRequestProperty("User-Agent", UserAgent);
            BufferedReader transactionReader = new BufferedReader(new InputStreamReader(
                    transactionConnection.getInputStream(), "UTF-8"));
            int transactionStart;
            String transaction;
            String lastDate = "null";
            String lastTime = "null";
            String meal = "null";
            Boolean sameMeal = true;
            ArrayList<String> transactions = new ArrayList<String>();
            while (true) {
                inputLine = transactionReader.readLine();
                if (inputLine == null) {
                    break;
                }
                if (inputLine.contains("<td align=\"left\">")) {
                    transactionStart = inputLine.indexOf("<td align=\"left\">") + 17;
                    transaction = inputLine.substring(transactionStart, inputLine.indexOf("<", transactionStart));
                    Pattern date = Pattern.compile(".*\\d{2}/\\d{2}/\\d{2,4}.*");
                    if (transaction.contains("&#163;")) {
                        transaction = "02   " + transaction.replace("&#163;", "£") + " " + meal;
                        transactions.add(transaction);
                    } else if (date.matcher(transaction).matches()) {
                        if (!lastDate.equals(transaction)) {
                            sameMeal = false;
                            lastDate = transaction;
                        }
                    } else if (transaction.contains(":")) {
                        if (!lastTime.equals(transaction)) {
                            sameMeal = false;
                            lastTime = transaction;
                        }
                        if (!sameMeal) {
                            transactions.add("12" + lastTime + " on " + lastDate);
                            sameMeal = true;
                        }

                    } else {
                        meal = transaction;
                    }

                }

            }
            handler.obtainMessage(0, transactions).sendToTarget();
            publishProgress("Finished");

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return Amounts;
    }

    @Override
    protected void onProgressUpdate(String... values) {
        Status.setText(values[0]);
    }

    @Override
    protected void onPostExecute(String[] strings) {
        if (!isCancelled()) {
            TextView AccountBalance = (TextView) view.findViewById(R.id.AccountBalance);
            TextView TokenBalance = (TextView) view.findViewById(R.id.TokenBalance);
            TextView DateBalance = (TextView) view.findViewById(R.id.DateBalance);
            TextView DateBalance2 = (TextView) view.findViewById(R.id.DateBalance2);
            AccountBalance.setText(strings[0]);
            TokenBalance.setText(strings[1]);
            DateBalance.setText(strings[2]);
            DateBalance2.setText(strings[2]);
        }

        Status.postDelayed(new Runnable() {
            @Override
            public void run() {
                Status.setText("");
            }
        }, 3000);


    }


}


