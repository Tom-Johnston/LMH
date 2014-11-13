package com.johnston.lmhapp;


import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.File;
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
import java.util.Arrays;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;


public class MainActivity extends ActionBarActivity {
    public Handler handler;
    ActionBarDrawerToggle mDrawerToggle;
    String mTitle;
    ListView mDrawerList;
    DrawerLayout mDrawerLayout;
    CookieManager manager;
    byte Type;
    View view;
    int lastPosition = -1;
    SSLContext sslContext = null;
    DrawerAdapter mDrawerAdapter;
    Bitmap selectedCircle;
    Bitmap unselectedCircle;

    public void drawCircle(int r, int g, int b) {
        Fragment fragment1 = getFragmentManager().findFragmentById(R.id.Frame);
        ((SettingsFragment) fragment1).drawCircle(r, g, b);

    }

    public void toggleMealNotification(View v) {
        CheckBox button = (CheckBox) v;
        SharedPreferences mealsToNotifyFor = getSharedPreferences("mealsToNotifyFor", 0);
        SharedPreferences.Editor editor = mealsToNotifyFor.edit();
        editor.putBoolean(button.getText().toString(), button.isChecked());
//        Replace an existing notification..
        Intent intent = new Intent(this, NotificationsService.class);
        this.sendBroadcast(intent);
        SharedPreferences widgetEnabled = this.getSharedPreferences("widgetEnabled", 0);
//       Update the widget.
        if (widgetEnabled.getBoolean("widgetEnabled", false)) {
            Intent updateWidget = new Intent(this, MealMenuWidgetReceiver.class);
            this.sendBroadcast(updateWidget);
        }
        editor.commit();
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

    public void notificationVibrate(Handler passedhandler) {
        handler = passedhandler;
        VibrateSettings newFragment = VibrateSettings.newInstance();
        newFragment.show(getFragmentManager(), "vibrate");
    }


    public void Initialise() {
        if (Type == 1) {
            new GetEpos().execute(manager, view, handler);
        } else if (Type == 2) {
            new Battels().execute(sslContext, view, handler);
        } else if (Type == 3) {
            new NameGrabber().execute(sslContext, this.getApplicationContext(), handler);
        }
    }


    public void getInfo(View v, Handler passedHandler, byte passedType) {
        handler = passedHandler;
        view = v;
        Type = passedType;
        LogInView();

    }

    public void SaveAccount(View v) {

//        Save the account
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

//      Create the custom graphic.
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message message) {
                Bitmap bitmap = (Bitmap) message.obj;
                ((ImageView) findViewById(R.id.graphic)).setImageBitmap(bitmap);
            }
        };
        int sizex = (int) ((findViewById(R.id.graphic)).getWidth() * 1.1);
//        Make the sizex an even number.
        sizex = (sizex / 2) * 2;
        int sizey = sizex / 2;
        new ImageGenerator().execute(username, handler, sizex, sizey, this.getApplicationContext());
        Byte three = 3;
        final Handler nameHandler = new Handler() {
            @Override
            public void handleMessage(Message message) {
                String name = (String) message.obj;
                ((TextView) findViewById(R.id.name)).setText(name);
            }
        };
        TextView usernameTextView = (TextView) findViewById(R.id.username);
        usernameTextView.setText(username);
        getInfo(null, nameHandler, three);
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
        TextView Status = null;
        if (view != null) {
            Status = (TextView) view.findViewById(R.id.Status);
        }
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
            sslContext = context;
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
        mTitle = getResources().getString(R.string.title);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState!=null) {
            lastPosition = savedInstanceState.getInt("lastPosition");
        }


//
//
        File file = new File(getFilesDir(), "CustomGraphic.png");
        if (file != null) {
            ((ImageView) findViewById(R.id.graphic)).setImageBitmap(BitmapFactory.decodeFile(file.getAbsolutePath()));
        }
        TextView username = (TextView) findViewById(R.id.username);
        TextView name = (TextView) findViewById(R.id.name);
        SharedPreferences LogIn = getSharedPreferences("LogIn", 0);
        username.setText(LogIn.getString("Username", ""));
        name.setText(LogIn.getString("Name", ""));


        selectedCircle = drawCircle(getResources().getColor(R.color.colorPrimary2));
        unselectedCircle = drawCircle(Color.parseColor("#de000000"));


//      Cookie Manager

        manager = new CookieManager();
        manager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        CookieHandler.setDefault(manager);
//       Drawer/ActionBar
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
        setSupportActionBar(toolbar);
        String[] Options = getResources().getStringArray(R.array.options);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mDrawerAdapter = new DrawerAdapter(this, R.layout.drawer_list_item, Arrays.asList(Options), selectedCircle, unselectedCircle);
        mDrawerAdapter.selected = lastPosition;
        mDrawerList.setAdapter(mDrawerAdapter);
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                toolbar,  /* nav drawer icon to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description */
                R.string.drawer_close  /* "close drawer" description */
        ) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle(mTitle);
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };

        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

//        Start Menu Fragment if clicked through from widget
        Intent intent = getIntent();
        Boolean LaunchMenu = intent.getBooleanExtra("Launch", false);
        if (LaunchMenu && savedInstanceState == null) {
            mDrawerList.performItemClick(mDrawerList.getAdapter().getView(4, null, null), 4, mDrawerList.getAdapter().getItemId(4));
        } else if (savedInstanceState == null) {
            mDrawerList.performItemClick(mDrawerList.getAdapter().getView(0, null, null), 0, mDrawerList.getAdapter().getItemId(0));
        } else {
            mTitle = savedInstanceState.getString("mTitle");
        }


    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("mTitle", mTitle);
        outState.putInt("lastPosition",lastPosition);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        getSupportActionBar().setTitle(mTitle);
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
        } else if (fragmentType.equals(Options[3])) {
            EPOS fragment = (EPOS) fragment1;
            fragment.GetEpos();
        } else if (fragmentType.equals(Options[4])) {
            MenuFragment fragment = (MenuFragment) fragment1;
            fragment.downloadNewMenu();
        } else if (fragmentType.equals(Options[0])) {
            HomeFragment fragment = (HomeFragment) fragment1;
            fragment.loadTweeterFeed();
        }else if(fragmentType.equals(Options[2])){
            BattelsFragment fragment = (BattelsFragment) fragment1;
            fragment.LoadBattels();
        }

    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawers();
            return;
        }
        if (lastPosition == 0) {
            this.finish();
        } else {
            mDrawerList.performItemClick(mDrawerList.getAdapter().getView(0, null, null), 0, mDrawerList.getAdapter().getItemId(0));
            String[] Options = getResources().getStringArray(R.array.options);
            mTitle = Options[0];
            getSupportActionBar().setTitle(mTitle);
        }
    }

    public Bitmap drawCircle(int color) {
        Bitmap.Config conf = Bitmap.Config.ARGB_8888;
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int size = (int) (24 * metrics.density);
        Bitmap bmp = Bitmap.createBitmap(size, size, conf);
        Canvas c = new Canvas(bmp);
        int radius = size / 2;
        Paint paint = new Paint();
        paint.setColor(color);
        paint.setAntiAlias(true);
        c.drawCircle(radius, radius, radius, paint);
        return bmp;
    }

    public class DrawerItemClickListener implements ListView.OnItemClickListener {


        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {

            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(parent.getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            mDrawerLayout.closeDrawers();
            if (lastPosition == position) {
                return;
            }
            String[] iconNames = getResources().getStringArray(R.array.iconNames);
            if (lastPosition >= parent.getFirstVisiblePosition()) {
                View layout = parent.getChildAt(lastPosition - parent.getFirstVisiblePosition());
                ((TextView) layout.findViewById(R.id.text1)).setTextColor(Color.parseColor("#de000000"));
                ImageView imageView = (ImageView) layout.findViewById(R.id.imageView);
                if (iconNames[lastPosition].equals("Circle")) {
                    imageView.setImageBitmap(unselectedCircle);
                } else {

                    int drawableId = getResources().getIdentifier(iconNames[lastPosition], "drawable", "com.johnston.lmhapp");
                    imageView.setImageDrawable(getResources().getDrawable(drawableId));
                }
            }
            TextView tv = (TextView) view.findViewById(R.id.text1);
            ImageView imgv = (ImageView) view.findViewById(R.id.imageView);
            if (iconNames[position].equals("Circle")) {
                imgv.setImageBitmap(selectedCircle);
            } else {

                int drawableId = getResources().getIdentifier(iconNames[position] + "_blue", "drawable", "com.johnston.lmhapp");
                imgv.setImageDrawable(getResources().getDrawable(drawableId));
            }
            tv.setTextColor(getResources().getColor(R.color.colorAccent));
            lastPosition = position;
            mDrawerAdapter.selected = position;
            String[] Options = getResources().getStringArray(R.array.options);
            mTitle = Options[position];
            Fragment newFragment;
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            newFragment = getFragmentManager().findFragmentByTag(Options[position]);
            if (newFragment != null) {

            } else if (position == 0) {
                newFragment = new HomeFragment();
                transaction.addToBackStack(Options[position]);
            } else if (position == 3) {
                newFragment = new EPOS();
                transaction.addToBackStack(Options[position]);
            } else if (position == 1) {
                newFragment = new LaundryView();
                transaction.addToBackStack(Options[position]);
            } else if (position == 4) {
                newFragment = new MenuFragment();
                transaction.addToBackStack(Options[position]);
            } else if (position == 5) {
                newFragment = new SettingsFragment();
                transaction.addToBackStack(Options[position]);
            } else if (position == 2) {
                newFragment = new BattelsFragment();
                transaction.addToBackStack(Options[position]);
            }
            newFragment.setRetainInstance(true);
            transaction.replace(R.id.Frame, newFragment, Options[position]);
            transaction.commit();


        }
    }
}

