package cardexc.com.freindlocation.activity;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.accountswitcher.AccountHeader;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import cardexc.com.freindlocation.R;
import cardexc.com.freindlocation.adapters.MyPagerAdapter;
import cardexc.com.freindlocation.data.Constants;
import cardexc.com.freindlocation.data.PhoneInit;
import cardexc.com.freindlocation.fragments.Devices;
import cardexc.com.freindlocation.fragments.History;
import cardexc.com.freindlocation.fragments.Map;
import cardexc.com.freindlocation.layouts.SlidingTabLayout;
import cardexc.com.freindlocation.service.ContactsUpdaterService;
import cardexc.com.freindlocation.service.HistoryUpdaterService;
import cardexc.com.freindlocation.service.LocationService;
import cardexc.com.freindlocation.service.events.MessageContactsUpdate;
import cardexc.com.freindlocation.sqlite.LocationContract;
import de.greenrobot.event.EventBus;

public class MainActivity extends AppCompatActivity
        implements
        Devices.OnFragmentInteractionListener,
        History.OnFragmentInteractionListener {

    private android.support.v4.view.ViewPager pager;
    private static final int OPEN_CONTACT_DETAILS_REQUESTCODE = 121;
    private static final int RQS_GooglePlayServices = 122;
    Drawer.Result drawerResult;
    Toolbar toolbar;
    SlidingTabLayout tabs;

    public static FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        Constants.setApplicationContext(getApplicationContext());

        setContentView(R.layout.activity_main);

        fragmentManager = getSupportFragmentManager();

        findViewsById();

        setSupportActionBar(toolbar);

        initializeNavigationDrawer();


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStart() {

        super.onStart();

        new PhoneInit().execute(Constants.getApplicationContext());

    }

    @Override
    protected void onPause() {
        super.onPause();
        stopService(new Intent(Constants.getApplicationContext(), ContactsUpdaterService.class));
        stopService(new Intent(Constants.getApplicationContext(), HistoryUpdaterService.class));
    }

    @Override
    protected void onResume() {
        super.onResume();
        startService(new Intent(Constants.getApplicationContext(), ContactsUpdaterService.class));
        startService(new Intent(Constants.getApplicationContext(), HistoryUpdaterService.class));

        checkGooglePlayServicesAvailable();

    }

    private void checkGooglePlayServicesAvailable() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(Constants.getApplicationContext());
        if (resultCode == ConnectionResult.SUCCESS){

            startService(new Intent(Constants.getApplicationContext(), LocationService.class));

        }else{
            Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(resultCode, this, RQS_GooglePlayServices);
            errorDialog.show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == OPEN_CONTACT_DETAILS_REQUESTCODE && data != null) {
            String action = data.getStringExtra("action");
            if ("GotoMapPage".equals(action)) {
                pager.setCurrentItem(1);
            }
        }
    }

    @Override  //Devices //History
    public void onFragmentInteraction(Cursor cursor, Boolean isDevice, Boolean isHistory) {

        if (isHistory) {

            String latitude = cursor.getString(cursor.getColumnIndexOrThrow(LocationContract.HistoryEntry.COLUMN_LATITUDE));
            String longitude = cursor.getString(cursor.getColumnIndexOrThrow(LocationContract.HistoryEntry.COLUMN_LATITUDE));

            if ("null".equals(latitude) || latitude == null
                    || "null".equals(longitude) || longitude == null) {
                Toast.makeText(Constants.getApplicationContext(), getResources().getString(R.string.history_no_coordinates), Toast.LENGTH_SHORT).show();
                return;
            }

            Map.getInstance().setIsSingleModeChoice(true);
            Map.getInstance().setMapSingleCursor(cursor);

            pager.setCurrentItem(1);

        } else if (isDevice) {

            Intent intent = new Intent(Constants.getApplicationContext(), ContactActivity.class);

            intent.putExtra("id", cursor.getString(cursor.getColumnIndexOrThrow(LocationContract.ContactEntry._ID)));

            startActivityForResult(intent, OPEN_CONTACT_DETAILS_REQUESTCODE);

        }

    }

    @Override
    public void onBackPressed() {
        if (drawerResult != null && drawerResult.isDrawerOpen()) {
            drawerResult.closeDrawer();
        } else {
            super.onBackPressed();
        }

    }

    /////////////////////////////////////////////////////////////////////////
    //Navigation drawer
    private AccountHeader.Result createAccountHeader() {

        return new AccountHeader()
                .withActivity(this)
                .withHeaderBackground(R.drawable.drawer_back_short)
                .build();
    }

    @NonNull
    private IDrawerItem[] initializeDrawerItems() {
        return new IDrawerItem[]{new PrimaryDrawerItem()
                .withName(R.string.label_contacts)
                .withIdentifier(1)
                .withIcon(R.drawable.contacts)
                .withTextColor(getResources().getColor(R.color.icon_border)),

                new PrimaryDrawerItem()
                        .withName(R.string.label_map)
                        .withIdentifier(1)
                        .withIcon(R.drawable.map)
                        .withTextColor(getResources().getColor(R.color.icon_border)),

                new PrimaryDrawerItem()
                        .withName(R.string.label_History)
                        .withIdentifier(1)
                        .withIcon(R.drawable.history)
                        .withTextColor(getResources().getColor(R.color.icon_border)),

                new DividerDrawerItem(),

                new PrimaryDrawerItem()
                        .withName(R.string.label_fake_location)
                        .withIdentifier(1)
                        .withIcon(R.drawable.fake)
                        //.withTypeface(Typeface.create("normal", 1))
                        .withTextColor(getResources().getColor(R.color.icon_border)),

                new DividerDrawerItem(),

                new PrimaryDrawerItem()
                        .withName(R.string.label_about_application)
                        .withIdentifier(1)
                        .withIcon(R.drawable.about)
                        .withTextColor(getResources().getColor(R.color.icon_border))

        }

                ;
    }

    private void initializeNavigationDrawer() {

        AccountHeader.Result accHeaderResult = createAccountHeader();

        drawerResult = new Drawer()
                .withActivity(this)
                .withToolbar(toolbar)
                .withDisplayBelowToolbar(false)
                .withActionBarDrawerToggleAnimated(true)
                .withAccountHeader(accHeaderResult)
                .addDrawerItems(initializeDrawerItems())
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                   @Override
                   public void onItemClick(AdapterView<?> adapterView, View view, int i, long l, IDrawerItem iDrawerItem) {

                       switch (i) {
                           case 0: {
                               pager.setCurrentItem(0);
                               break;
                           }
                           case 1: {
                               pager.setCurrentItem(1);
                               break;
                           }
                           case 2: {
                               pager.setCurrentItem(2);
                               break;
                           }
                           case 4: {
                               Intent intent = new Intent(Constants.getApplicationContext(), FakeLocationActivity.class);
                               startActivity(intent);
                               break;
                           }
                           case 6: {
                               Intent intent = new Intent(Constants.getApplicationContext(), AboutActivity.class);
                               startActivity(intent);
                               break;
                           }


                       }

                   }


                                               }
                )
                .build();



    }

    /////////////////////////////////////////////////////////////////////////

    private void findViewsById() {

        toolbar = (Toolbar) findViewById(R.id.tool_bar);

        pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(new MyPagerAdapter(getFragmentManager(), Constants.getPagesTitle(Constants.getApplicationContext())));

        tabs = (SlidingTabLayout) findViewById(R.id.tabs);
        tabs.setDistributeEvenly(true);
        tabs.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    EventBus.getDefault().post(new MessageContactsUpdate());
                } else if (position == 1) {
                    Map.getInstance().setUpMap();
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.tabsScrollColor);
            }
        });
        tabs.setViewPager(pager);

    }

    /////////////////////////////////////////////////////////////////////////

}