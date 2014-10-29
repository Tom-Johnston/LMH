package com.johnston.lmhapp;


import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;


public class MainActivity extends ActionBarActivity {
    ActionBarDrawerToggle mDrawerToggle;
    String mTitle = "LMH";
    ListView mDrawerList;
    DrawerLayout mDrawerLayout;
    CookieManager manager;
    byte Type;
    View view;
    Handler handler;
    int lastPosition = -1;

    public void drawCircle(int r, int g, int b) {
        Fragment fragment1 = getFragmentManager().findFragmentById(R.id.Frame);
        ((SetupLogIn) fragment1).drawCircle(r, g, b);

    }

    public void notificationSound(View v) {
        SharedPreferences NotificationSound = getSharedPreferences("NotificationSound", 0);
        Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Tone");
        if (NotificationSound.contains("SoundURI")) {
            Uri uri = Uri.parse(NotificationSound.getString("SoundURI", "This is irrelevant"));
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, uri);
        } else {
            Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, uri);
        }
        this.startActivityForResult(intent, 5);
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent intent) {
        if (resultCode == Activity.RESULT_OK && requestCode == 5) {
            Uri uri = intent.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
            if (uri != null) {
                SharedPreferences NotificationSound = getSharedPreferences("NotificationSound", 0);
                SharedPreferences.Editor editor = NotificationSound.edit();
                editor.putString("SoundURI", uri.toString());
                editor.commit();
            } else {
                SharedPreferences NotificationSound = getSharedPreferences("NotificationSound", 0);
                SharedPreferences.Editor editor = NotificationSound.edit();
                editor.putString("SoundURI", "None");
                editor.commit();
            }
        }
    }

    public void notificationVibrate() {
        VibrateSettings newFragment = VibrateSettings.newInstance();
        newFragment.show(getFragmentManager(), "vibrate");
    }

    public void toggleNotifications(View view) {
        boolean on = ((Switch) view).isChecked();
        RelativeLayout notification = (RelativeLayout) this.findViewById(R.id.notificationLayout);
        if (on) {
            SharedPreferences Notifications = getSharedPreferences("Notifications", 0);
            SharedPreferences.Editor editor = Notifications.edit();
            editor.putBoolean("toggle", true);
            editor.commit();
            Intent newIntent = new Intent(this, NotificationsService.class);
            sendBroadcast(newIntent);
            notification.setVisibility(LinearLayout.VISIBLE);

        } else {
            SharedPreferences Notifications = getSharedPreferences("Notifications", 0);
            SharedPreferences.Editor editor = Notifications.edit();
            editor.putBoolean("toggle", false);
            editor.commit();
            notification.setVisibility(LinearLayout.GONE);
        }
    }

    public void Initialise() {
        if (Type == 1) {
            new GetEpos().execute(manager, view, handler);
        }
    }

    public void GetBalance(View v, Handler passedHandler) {
//        System.out.println(manager.getCookieStore().getCookies().toString());
        handler = passedHandler;
        view = v;
        Type = 1;
        LogInView();

    }

    public void SaveAccount(View v) {
        EditText passwordView = (EditText) findViewById(R.id.Password);
        String password = passwordView.getText().toString();
        EditText usernameView = (EditText) findViewById(R.id.Username);
        String username = usernameView.getText().toString();
        SharedPreferences LogIn = getSharedPreferences("LogIn", 0);
        SharedPreferences.Editor editor = LogIn.edit();
        editor.putString("Username", username);
        editor.putString("Password", password);
        Toast toast = Toast.makeText(this, "Details Saved.", Toast.LENGTH_SHORT);
        toast.show();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        editor.commit();
    }

    public void showPassword(View v) {
        EditText editText = (EditText) findViewById(R.id.Password);

        if (((CheckBox) v).isChecked()) {
            editText.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);

        } else {
            editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        }
    }

    public void LedSettings(View v) {
        LedColorDialog newFragment = LedColorDialog.newInstance();
        newFragment.show(getFragmentManager(), "missiles");
    }

    public String[] returnLogIn() {
        String[] LogInDetails = new String[2];
        SharedPreferences LogIn = getSharedPreferences("LogIn", 0);
        if (LogIn.contains("Username") && LogIn.contains("Password")) {
            LogInDetails[0] = LogIn.getString("Username", "Fail");
            LogInDetails[1] = LogIn.getString("Password", "Fail");
            return LogInDetails;
        } else {
            return null;
        }
    }

    public void LogInView() {
        SharedPreferences LogIn = getSharedPreferences("LogIn", 0);
        TextView Status = (TextView) view.findViewById(R.id.Status);
        if (LogIn.contains("Username") && LogIn.contains("Password")) {
            String username = LogIn.getString("Username", "Fail");
            String password = LogIn.getString("Password", "Fail");
            SSLContext context = createTrustManager();
            new LogInTask().execute(context, username, password, Status, this, manager);
        } else {
            Status.setText("Please input username and password");
        }


    }

    public SSLContext createTrustManager() {
        try {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            InputStream fis1 = (getResources().openRawResource(R.raw.ca));
            InputStream fis5 = (getResources().openRawResource(R.raw.ca5));
            InputStream caInput = new BufferedInputStream(fis1);
            InputStream caInput5 = new BufferedInputStream(fis5);
            Certificate ca;
            Certificate ca5;
            ca = cf.generateCertificate(caInput);
            ca5 = cf.generateCertificate(caInput5);
            System.out.println("ca=" + ((X509Certificate) ca).getSubjectDN());
            System.out.println("ca=" + ((X509Certificate) ca5).getSubjectDN());
            caInput.close();
            caInput5.close();


// Create a KeyStore containing our trusted CAs
            String keyStoreType = KeyStore.getDefaultType();
            KeyStore keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", ca);
            keyStore.setCertificateEntry("ca5", ca5);

// Create a TrustManager that trusts the CAs in our KeyStore
            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            tmf.init(keyStore);

// Create an SSLContext that uses our TrustManager
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, tmf.getTrustManagers(), null);
            System.out.println("Success!");
            return context;

        } catch (CertificateException e) {
            e.printStackTrace();
            return null;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (KeyManagementException e) {
            e.printStackTrace();
            return null;
        } catch (KeyStoreException e) {
            e.printStackTrace();
            return null;
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        manager = new CookieManager();
        manager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        CookieHandler.setDefault(manager);
        String[] Options = getResources().getStringArray(R.array.options);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        // Set the adapter for the list view
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, Options));
        // Set the list's click listener
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer icon to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description */
                R.string.drawer_close  /* "close drawer" description */
        ) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getActionBar().setTitle(mTitle);
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getActionBar().setTitle("LMH");
            }
        };

        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        Intent intent = getIntent();
        Boolean LaunchMenu = intent.getBooleanExtra("Launch", false);
        if (LaunchMenu && savedInstanceState == null) {
            mDrawerList.performItemClick(mDrawerList.getAdapter().getView(3, null, null), 3, mDrawerList.getAdapter().getItemId(3));

        } else if (savedInstanceState == null) {
            mDrawerList.performItemClick(mDrawerList.getAdapter().getView(0, null, null), 0, mDrawerList.getAdapter().getItemId(0));

        } else {
//            This is if savedIntanceState is not null
            mTitle = savedInstanceState.getString("mTitle");
        }


    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("mTitle", mTitle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        System.out.println("Should be setting the title");
        getActionBar().setTitle(mTitle);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle your other action bar items...

        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public void Refresh(MenuItem item) {
        Fragment fragment1 = getFragmentManager().findFragmentById(R.id.Frame);
        String fragmentType = fragment1.getTag();
        String[] Options = getResources().getStringArray(R.array.options);
        if (fragmentType.equals(Options[1])) {
            LaundryView fragment = (LaundryView) fragment1;
            fragment.LoadStatus();
        } else if (fragmentType.equals(Options[2])) {
            EPOS fragment = (EPOS) fragment1;
            fragment.GetEpos();
        } else if (fragmentType.equals(Options[3])) {
            MenuFragment fragment = (MenuFragment) fragment1;
            fragment.downloadNewMenu();
        } else if (fragmentType.equals(Options[0])) {
            HomeFragment fragment = (HomeFragment) fragment1;
            fragment.loadTweeterFeed();
        }

    }

    @Override
    public void onBackPressed() {
        if (lastPosition == 0) {
            this.finish();
        } else {
            System.out.println("BackPressed");
            mDrawerList.performItemClick(mDrawerList.getAdapter().getView(0, null, null), 0, mDrawerList.getAdapter().getItemId(0));
        }
    }

    public class DrawerItemClickListener implements ListView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            System.out.println("Clicked: " + position);
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(parent.getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            System.out.println(mDrawerList.getCheckedItemPosition());
            if (lastPosition == position) {
                mDrawerLayout.closeDrawers();
                return;
            }
            lastPosition = position;
            String[] Options = getResources().getStringArray(R.array.options);
            mTitle = Options[position];
            System.out.println("position" + position);
            Fragment newFragment;
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            newFragment = getFragmentManager().findFragmentByTag(Options[position]);
            if (newFragment != null) {

            } else if (position == 0) {
                newFragment = new HomeFragment();
                transaction.addToBackStack(Options[position]);
            } else if (position == 2) {
                newFragment = new EPOS();
                transaction.addToBackStack(Options[position]);
            } else if (position == 1) {
                newFragment = new LaundryView();
                transaction.addToBackStack(Options[position]);
            } else if (position == 3) {
                newFragment = new MenuFragment();
                transaction.addToBackStack(Options[position]);
            } else if (position == 4) {
                newFragment = new SetupLogIn();
                transaction.addToBackStack(Options[position]);
            }
            newFragment.setRetainInstance(true);
            transaction.replace(R.id.Frame, newFragment, Options[position]);
            transaction.commit();
            System.out.println(":::::::::::::::::::::" + getFragmentManager().getBackStackEntryCount());
            mDrawerLayout.closeDrawers();

        }
    }
}

