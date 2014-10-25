package com.johnston.lmhapp;

import android.os.AsyncTask;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

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

import javax.net.ssl.HttpsURLConnection;
//import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Tom on 05/06/2014.
 */
public class GetEpos extends AsyncTask<Object, String, String[]> {
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
            URL url = new URL("https://www.upay.co.uk/Shibboleth.sso/Login?entityID=https://registry.shibboleth.ox.ac.uk/idp&target=https://www.upay.co.uk/shib/sso.aspx ");
            System.out.println("Attempt");
            HttpsURLConnection urlc = (HttpsURLConnection) url.openConnection();
            System.out.println(urlc.getResponseCode());
            String newURL = urlc.getURL().toString();
            System.out.println(newURL);

            //LOTs to do here

            if (newURL.contains("login")) {
                publishProgress("SSO Confirmation");
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        urlc.getInputStream(), "UTF-8"));
                String inputLine;
                StringBuilder a = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    a.append(inputLine);
//                        System.out.println(inputLine);
                }
                in.close();
                urlc.disconnect();
                int Start = a.indexOf("https://idp.shibboleth.ox.ac.uk/idp");
                int End = a.indexOf("\"><span");
                GetCookie = a.substring(Start, End);
                System.out.println(GetCookie);
                URL Cookie = new URL(GetCookie);
                HttpsURLConnection Cookiec = (HttpsURLConnection) Cookie.openConnection();
                StringBuilder b = new StringBuilder();
                BufferedReader Cookier = new BufferedReader(new InputStreamReader(
                        Cookiec.getInputStream(), "UTF-8"));
                while ((inputLine = Cookier.readLine()) != null) {
                    System.out.println(inputLine);
                    b.append(inputLine);
                }
                Cookier.close();
                System.out.println(Cookiec.getURL());
                Cookiec.disconnect();
                int RSStart = b.indexOf("value=\"") + 7;
                int RSEnd = b.indexOf(">", RSStart);
                String RelayState = b.substring(RSStart, RSEnd - 2);
                Start = b.indexOf("value=\"", RSStart) + 7;
                End = b.indexOf(">", Start);
                String SAMLRespose = b.substring(Start, End - 2);
                SAMLRespose = SAMLRespose.replace("=", "%3D");
//                System.out.println(SAMLRespose.substring(SAMLRespose.length()-10));
//            Charset.forName("UTF-8").encode(SAMLRespose);
                String post = "RelayState=" + RelayState + "&SAMLResponse=" + SAMLRespose;
                post = post.replace("&#x3a;", "%3A");
                post = post.replace("+", "%2B");
                System.out.println(post.indexOf("#"));
//              post= URLEncoder.encode(post, "UTF-8");
                System.out.println(post);
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
                System.out.println("OutPutDone");
                System.out.println(connl.getResponseCode());
                System.out.println(connl.getURL());
//                conn.getResponseCode();
//                System.out.println(conn.getResponseCode());

                publishProgress("Getting .COOKIECASHLESS");
                URL Test = new URL("https://www.upay.co.uk/shib/sso.aspx");
                HttpsURLConnection conn2 = (HttpsURLConnection) Test.openConnection();
                conn2.setRequestProperty("User-Agent", UserAgent);
                conn2.setRequestProperty("Accept-Encoding", "identity");
                conn2.setInstanceFollowRedirects(true);
                System.out.println(conn2.getURL());
////                conn2.getResponseCode();
//                    CookieManager manager = (CookieManager) Objects[0];
//                    CookieStore cookieJar = manager.getCookieStore();
//                    cookieJar.add(new URI("cookie"), new HttpCookie("Test", "Testing"));
//                    List<HttpCookie> cookies =
//                            cookieJar.getCookies();
//                    for (int i = 0; i < cookies.size() - 1; i++) {
//
//                        HttpCookie cookie = cookies.get(i);
//                        System.out.println("CookieHandler retrieved cookie: " + cookie);
//                    }
//                String IGIVEUP = conn2.getHeaderField("Set-Cookie");
//                conn2.getResponseCode();
//                System.out.println(IGIVEUP);


                for (int i = 0; ; i++) {
                    String headerName = conn2.getHeaderFieldKey(i);
                    String headerValue = conn2.getHeaderField(i);
//                    System.out.println(headerName);
//                    System.out.println(headerValue);

//                        For some reason .COOKIECASHLESS isn't set. The cookie is therefore created manually.

                    if (headerName == null && headerValue == null) {
                        System.out.println("No more headers");
                        break;
                    } else {
                        System.out.println(headerName);
                        System.out.println(headerValue);
                        if (headerValue.contains(".COOKIECASHLESS=")) {
                            publishProgress("Creating .COOKIECASHLESS");
                            String cookieValue = headerValue.substring(16, headerValue.indexOf(";"));
                            System.out.println(cookieValue);
                            HttpCookie myCookie = new HttpCookie(".COOKIECASHLESS", cookieValue);
                            myCookie.setVersion(0);
                            myCookie.setPath("/");
                            cookieValue = myCookie.toString();
                            System.out.println(cookieValue);
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
            System.out.println(conn3.getURL());
            String inputLine;
            BufferedReader Reader3 = new BufferedReader(new InputStreamReader(
                    conn3.getInputStream(), "UTF-8"));
            while ((inputLine = Reader3.readLine()) != null) {
//                	System.out.println(inputLine);
                if (inputLine.contains("Account balance: &#163;")) {
                    int AccountBalanceStart = inputLine.indexOf("Account balance: &#163;") + 23;
                    int AccountBalanceEnd = inputLine.indexOf("<", AccountBalanceStart);
                    String AccountBalance = inputLine.substring(AccountBalanceStart, AccountBalanceEnd);
                    System.out.println("Account Balance: " + "\u00A3" + AccountBalance);
                    Amounts[0] = "£" + AccountBalance;

                }
                if (inputLine.contains("Available Token Balance:")) {
                    int TokenBalanceStart = inputLine.indexOf("Available Token Balance:") + 25;
                    int TokenBalanceEnd = inputLine.indexOf("<", TokenBalanceStart);
                    String TokenBalance = inputLine.substring(TokenBalanceStart, TokenBalanceEnd);
                    System.out.println("Token Balance: " + "\u00A3" + TokenBalance);
                    Amounts[1] = "£" + TokenBalance;

                }
                if (inputLine.contains("Last updated:")) {
                    int dateStart = inputLine.indexOf("Last updated:") + 14;
                    int dateEnd = inputLine.indexOf("<", dateStart);
                    String TokenBalance = inputLine.substring(dateStart, dateEnd);
                    System.out.println("Last updated: " + TokenBalance);
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
                    if (transaction.contains("&#163;")) {
                        transaction = "02   " + transaction.replace("&#163;", "£") + " " + meal;
                        transactions.add(transaction);
                    } else if (transaction.contains("/")) {
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
            AccountBalance.setText(strings[0]);
            TokenBalance.setText(strings[1]);
            DateBalance.setText(strings[2]);
        }

    }


}


