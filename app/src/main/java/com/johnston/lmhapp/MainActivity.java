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
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.johnston.lmhapp.Battels.BattelsAsync;
import com.johnston.lmhapp.Battels.BattelsFragment;
import com.johnston.lmhapp.EPOS.EPOSAsync;
import com.johnston.lmhapp.EPOS.EPOSFragment;
import com.johnston.lmhapp.Formal.FormalAsync;
import com.johnston.lmhapp.Formal.FormalFragment;
import com.johnston.lmhapp.Home.HomeFragment;
import com.johnston.lmhapp.LaundryView.LaundryViewFragment;
import com.johnston.lmhapp.MealMenus.DownloadNewMenuAsync;
import com.johnston.lmhapp.MealMenus.MenuFragment;
import com.johnston.lmhapp.Settings.SettingsFragment;

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
    //    Display information on the progress of the Async Tasks
    public static TextView Status = null;
//    private final Handler statusHandler = new Handler() {
//        @Override
//        public void handleMessage(Message message) {
//            if (message.what == -1) {
//                handler.obtainMessage(-1).sendToTarget();
//            }
//            String update = (String) message.obj;
//            if (Status != null) {
//                Status.setText(update);
//            }
//        }
//    };


    //    Navigation Drawer
    private ActionBarDrawerToggle mDrawerToggle;
    private String mTitle;
    private ListView mDrawerList;
    private DrawerLayout mDrawerLayout;
    private int lastPosition = -99;
    private DrawerAdapter mDrawerAdapter;
    //    Default images for the navigation drawer
    private Bitmap selectedCircle;
    private Bitmap unselectedCircle;

    //    Internet Stuff
    private CookieManager manager;
    private SSLContext sslContext = null;

    //  Things for the spinning refresh icon.
    private MenuItem item;
    private Animation an;
    private int refreshSpinRequestFragment = -99;
    private ImageView actionRefreshView;


    //    Handle clicking on the refresh button
    public void Refresh(MenuItem item) {
        Fragment fragment1 = getFragmentManager().findFragmentById(R.id.Frame);
        String fragmentType = fragment1.getTag();
        String[] Options = getResources().getStringArray(R.array.options);
        if (fragmentType.equals(Options[1])) {
            LaundryViewFragment fragment = (LaundryViewFragment) fragment1;
            fragment.LoadStatus();
        } else if (fragmentType.equals(Options[3])) {
            EPOSFragment fragment = (EPOSFragment) fragment1;
            fragment.GetEpos();
        } else if (fragmentType.equals(Options[5])) {
            MenuFragment fragment = (MenuFragment) fragment1;
            fragment.checkForPermission();
        } else if (fragmentType.equals(Options[0])) {
            HomeFragment fragment = (HomeFragment) fragment1;
            fragment.loadTweeterFeed();
        } else if (fragmentType.equals(Options[2])) {
            BattelsFragment fragment = (BattelsFragment) fragment1;
            fragment.LoadBattels();
        } else if (fragmentType.equals(Options[4])) {
            FormalFragment fragment = (FormalFragment) fragment1;
            fragment.GetTheData();
        }
    }

    public void startRefresh(int i) {
//        Start the spinning of the refresh icon.
        refreshSpinRequestFragment = i; // Note the fragment that has requested the spinning

        Handler startHandler = new Handler();
        Runnable startRunnable = new Runnable() {

            @Override
            public void run() {
                if (actionRefreshView == null) {
                    LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    actionRefreshView = (ImageView) inflater.inflate(R.layout.action_refresh, null);
                } else {
                    actionRefreshView.setVisibility(View.VISIBLE);
                }
                an.setRepeatCount(Animation.INFINITE);
                an.setDuration(1000);
                an.start();
                actionRefreshView.setAnimation(an);
                actionRefreshView.getAnimation().setAnimationListener(null);
                if (item != null) {
                    item.setActionView(actionRefreshView);
                }
            }
        };
        startHandler.post(startRunnable);

    }


    public void stopRefresh(final int i) {
        final View actionRefreshView2 = this.actionRefreshView;
//        Only cancel the spinning if nothing is null and the request to stop spinning is from the last fragment to request the spinning or -1 to stop all.
        if ((refreshSpinRequestFragment == i || i == -1) && item != null && item.getActionView() != null && item.getActionView().getAnimation() != null) {
            refreshSpinRequestFragment = -99;
            item.getActionView().getAnimation().setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    item.setActionView(null);
                    actionRefreshView2.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
            if (i == -1) {
                item.getActionView().getAnimation().setDuration(0);
            }
            item.getActionView().getAnimation().setRepeatCount(0);
        }
    }

    public void formalButtonClick(View v) {
        ((FormalFragment) getFragmentManager().findFragmentById(R.id.Frame)).showListofPeopleGoing(Integer.parseInt(v.getTag().toString()));
    }


    //The method called at the initial request for information.
    public void getInfo(View v, final Handler passedHandler, byte passedType) {
        if (v != null) {
            Status = (TextView) v.findViewById(R.id.Status);
        } else {
            Status = null;
        }
        final Handler statusHandler = new Handler() {
            @Override
            public void handleMessage(Message message) {
                if (message.what == -1) {
                    passedHandler.obtainMessage(-1).sendToTarget();
                }
                String update = (String) message.obj;
                if (Status != null) {
                    Status.setText(update);
                }
            }
        };
        if (passedType == 3) {
//            This is a request to get the name of the current log in. Hence we need to remove the previous cookie to logout any previous accounts.
            manager.getCookieStore().removeAll();
        }
        LogInView(statusHandler,passedHandler,passedType);

    }

    //Called to check for permission to attempt to get the information
    void LogInView(final Handler statusHandler, final Handler passedHandler,final byte passedType) {
        Handler permissionHandler = new Handler() {
            @Override
            public void handleMessage(Message message) {
                if (message.what == 0) {
//                Success!
                    LogIn(statusHandler,passedHandler,passedType);
                } else if (message.what == 1) {
//                Failure
                    passedHandler.obtainMessage(-1).sendToTarget();
                    PermissionFailedDialog newFragment = PermissionFailedDialog.newInstance((String) message.obj);
                    newFragment.show(getFragmentManager(), "PERMISSION DENIED");
                }else if(message.what==2){
                    new DownloadNewMenuAsync().execute(this, false, null );
                } else {
//                Something has gone wrong checking.
                    passedHandler.obtainMessage(-1).sendToTarget();
                }
            }
        };

        new PermissionAsync().execute(this.getApplicationContext(), permissionHandler,statusHandler,Byte.toString(passedType));
    }

    //    This logs in to the intranet.
    void LogIn(final Handler statusHandler, final Handler passedHandler, final byte passedType) {
        SharedPreferences LogIn = getSharedPreferences("LogIn", 0);
        if (LogIn.contains("Username") && LogIn.contains("Password")) {
            String username = LogIn.getString("Username", "Fail");
            String password = LogIn.getString("Password", "Fail");
            SSLContext context = createTrustManager();
            Handler loginHandler =  new Handler(){
                @Override
                public void handleMessage(Message message) {
                    if(message.what==1){
                       Initialise(statusHandler,passedHandler,passedType);
                    }
                }
            };
            new LoginAsync().execute(context, username, password, statusHandler, loginHandler, manager);
        } else {
            Status.setText("Please input username and password");
        }
    }

    //    Start the appropriate Async to get the information.
    public void Initialise(Handler statusHandler, Handler passedHandler, Byte passedType) {
        if (passedType == 1) {
            new EPOSAsync().execute(manager, statusHandler, passedHandler);
        } else if (passedType == 2) {
            new BattelsAsync().execute(sslContext, statusHandler, passedHandler);
        } else if (passedType == 3) {
            new NameGrabberAsync().execute(sslContext, this.getApplicationContext(), passedHandler);
        } else if (passedType == 4) {
            new FormalAsync().execute(sslContext, passedHandler, statusHandler);
        }
    }

    //    This creates a custom SSLContext as the certificates of the intranet are not trusted by default.
    SSLContext createTrustManager() {
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

    //   Handles the click events for the list in settings. Need to check how necessary this is with the RecyclerView.
    public void itemClicked(View v) {
        ((SettingsFragment) getFragmentManager().findFragmentById(R.id.Frame)).itemClicked(v);
    }


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTitle = getResources().getString(R.string.title);
        an = AnimationUtils.loadAnimation(this, R.anim.rotate_animation);
        setContentView(R.layout.activity_main);
        if (savedInstanceState != null) {
//            Return to the last position.
            lastPosition = savedInstanceState.getInt("lastPosition");
        }

        Status = null;
//
//      Set the persons information.
        File file = new File(getFilesDir(), "CustomGraphic.png");
        if (file.exists()) {
            ((ImageView) findViewById(R.id.graphic)).setImageBitmap(BitmapFactory.decodeFile(file.getAbsolutePath()));
            TextView username = (TextView) findViewById(R.id.username);
            TextView name = (TextView) findViewById(R.id.name);
            SharedPreferences LogIn = getSharedPreferences("LogIn", 0);
            username.setText(LogIn.getString("Username", ""));
            name.setText(LogIn.getString("Name", ""));
        }else{
//            Generate a plain blue background for the user image.
            Bitmap.Config conf = Bitmap.Config.ARGB_8888;
            Bitmap bmp = Bitmap.createBitmap(2, 1, conf);
            Canvas c = new Canvas(bmp);
            c.drawColor(getResources().getColor(R.color.colorPrimary2));
            ((ImageView) findViewById(R.id.graphic)).setImageBitmap(bmp);
            TextView username = (TextView) findViewById(R.id.username);
            TextView name = (TextView) findViewById(R.id.name);
            SharedPreferences LogIn = getSharedPreferences("LogIn", 0);
            username.setText(LogIn.getString("Username", "Please Log In"));
            name.setText(LogIn.getString("Name", "Welcome to the LMH App"));

        }

//      Generate the default drawer items.
        selectedCircle = drawCircle(getResources().getColor(R.color.colorPrimary2));
        unselectedCircle = drawCircle(Color.parseColor("#de000000"));


//      Set up the Cookie Manager
        manager = (CookieManager) CookieHandler.getDefault();
        if (manager == null) {
            manager = new CookieManager();
        }
        manager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        CookieHandler.setDefault(manager);

//       Set up the Drawer/ActionBar
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

        };

        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

//        Start Menu Fragment if clicked through from widget
        Intent intent = getIntent();
        final Boolean LaunchMenu = intent.getBooleanExtra("Launch", false);
        Handler startHandler = new Handler();
        Runnable startRunnable = new Runnable() {
            @Override
            public void run() {
                if (LaunchMenu && savedInstanceState == null) {
                    mDrawerList.smoothScrollToPosition(5);
                    View layout = mDrawerList.getChildAt(5 - mDrawerList.getFirstVisiblePosition());
                    mDrawerList.performItemClick(layout, 5, mDrawerList.getAdapter().getItemId(5));
                    String[] Options = getResources().getStringArray(R.array.options);
                    mTitle = Options[5];
                    getSupportActionBar().setTitle(mTitle);
                } else if (savedInstanceState == null) {
                    mDrawerList.smoothScrollToPosition(0);
                    View layout = mDrawerList.getChildAt(0 - mDrawerList.getFirstVisiblePosition());
                    mDrawerList.performItemClick(layout, 0, mDrawerList.getAdapter().getItemId(0));
                    String[] Options = getResources().getStringArray(R.array.options);
                    mTitle = Options[0];
                    getSupportActionBar().setTitle(mTitle);
                } else {
                    mTitle = savedInstanceState.getString("mTitle");
                    getSupportActionBar().setTitle(mTitle);
                }
            }
        };
        startHandler.post(startRunnable);
    }

    //  Generates the default drawer item images.
    Bitmap drawCircle(int color) {
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

    public void goToSettings() {
        mDrawerList.smoothScrollToPosition(6);
        View layout = mDrawerList.getChildAt(6 - mDrawerList.getFirstVisiblePosition());
        mDrawerList.performItemClick(layout, 6, mDrawerList.getAdapter().getItemId(6));
        String[] Options = getResources().getStringArray(R.array.options);
        mTitle = Options[6];
        getSupportActionBar().setTitle(mTitle);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("lastPosition", lastPosition);
        outState.putString("mTitle",mTitle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        return mDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);

    }

    //TODO Check this.
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        item = menu.getItem(0);
        if (refreshSpinRequestFragment != -99) {
            startRefresh(refreshSpinRequestFragment);
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    //    Override the back button press so clicking it when not on the home fragment goes back to the home fragment.
    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawers();
            return;
        }
        if (lastPosition == 0) {
            this.finish();
        } else if (lastPosition == -1) {
            mDrawerList.smoothScrollToPosition(4);
            View layout = mDrawerList.getChildAt(4 - mDrawerList.getFirstVisiblePosition());
            mDrawerList.performItemClick(layout, 4, mDrawerList.getAdapter().getItemId(4));
            String[] Options = getResources().getStringArray(R.array.options);
            mTitle = Options[4];
            getSupportActionBar().setTitle(mTitle);
        } else {
            mDrawerList.smoothScrollToPosition(0);
            View layout = mDrawerList.getChildAt(0 - mDrawerList.getFirstVisiblePosition());
            mDrawerList.performItemClick(layout, 0, mDrawerList.getAdapter().getItemId(0));
            String[] Options = getResources().getStringArray(R.array.options);
            mTitle = Options[0];
            getSupportActionBar().setTitle(mTitle);
        }
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

            if (lastPosition == -1) {
                lastPosition = 4;
            }
            View layout = parent.getChildAt(lastPosition - parent.getFirstVisiblePosition());
            if (layout != null) {
                ((TextView) layout.findViewById(R.id.text1)).setTextColor(Color.parseColor("#de000000"));
                ImageView imageView = (ImageView) layout.findViewById(R.id.profilePicture);
                if (iconNames[lastPosition].equals("Circle")) {
                    imageView.setImageBitmap(unselectedCircle);
                } else {

                    int drawableId = getResources().getIdentifier(iconNames[lastPosition], "drawable", "com.johnston.lmhapp");
                    imageView.setImageDrawable(getResources().getDrawable(drawableId));
                }
            }
            if (view != null) {
                TextView tv = (TextView) view.findViewById(R.id.text1);
                ImageView imgv = (ImageView) view.findViewById(R.id.profilePicture);
                if (iconNames[position].equals("Circle")) {
                    imgv.setImageBitmap(selectedCircle);
                } else {

                    int drawableId = getResources().getIdentifier(iconNames[position] + "_blue", "drawable", "com.johnston.lmhapp");
                    imgv.setImageDrawable(getResources().getDrawable(drawableId));
                }
                tv.setTextColor(getResources().getColor(R.color.colorAccent));
            }
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
            } else if (position == 1) {
                newFragment = new LaundryViewFragment();
                transaction.addToBackStack(Options[position]);
            } else if (position == 2) {
                newFragment = new BattelsFragment();
                transaction.addToBackStack(Options[position]);
            } else if (position == 3) {
                newFragment = new EPOSFragment();
                transaction.addToBackStack(Options[position]);
            } else if (position == 4) {
                newFragment = new FormalFragment();
                transaction.addToBackStack(Options[position]);
            } else if (position == 5) {
                newFragment = new MenuFragment();
                transaction.addToBackStack(Options[position]);
            } else if (position == 6) {
                newFragment = new SettingsFragment();
                transaction.addToBackStack(Options[position]);
            }
            stopRefresh(-1);
            newFragment.setRetainInstance(true);
            transaction.replace(R.id.Frame, newFragment, Options[position]);
            transaction.commit();


        }
    }
}

