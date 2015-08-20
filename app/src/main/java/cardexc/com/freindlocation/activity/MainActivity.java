package cardexc.com.freindlocation.activity;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.accountswitcher.AccountHeader;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import java.util.UUID;

import cardexc.com.freindlocation.R;
import cardexc.com.freindlocation.adapters.MyPagerAdapter;
import cardexc.com.freindlocation.data.Constants;
import cardexc.com.freindlocation.data.PhoneInit;
import cardexc.com.freindlocation.fragments.Devices;
import cardexc.com.freindlocation.fragments.History;
import cardexc.com.freindlocation.layouts.SlidingTabLayout;
import cardexc.com.freindlocation.service.ContactsUpdaterService;
import cardexc.com.freindlocation.service.HistoryUpdaterService;
import cardexc.com.freindlocation.service.LocationService;
import cardexc.com.freindlocation.service.events.MessageContactListReceived;
import cardexc.com.freindlocation.service.events.MessageContactsUpdate;
import cardexc.com.freindlocation.sqlite.ContactProvider;
import cardexc.com.freindlocation.sqlite.HistoryProvider;
import cardexc.com.freindlocation.sqlite.LocationContract;
import cardexc.com.freindlocation.sqlite.LocationProvider;
import de.greenrobot.event.EventBus;

public class MainActivity extends AppCompatActivity
        implements
        Devices.OnFragmentInteractionListener,
        History.OnFragmentInteractionListener{

    private android.support.v4.view.ViewPager pager;
    Drawer.Result drawerResult;
    Toolbar toolbar;
    SlidingTabLayout tabs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        new PhoneInit().execute(this);

        startService(new Intent(this, ContactsUpdaterService.class));
        startService(new Intent(this, LocationService.class));
        startService(new Intent(this,HistoryUpdaterService.class));


    }

    @Override
    protected void onPause() {
        super.onPause();
        stopService(new Intent(this, ContactsUpdaterService.class));
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

    //Devices
    @Override
    public void onFragmentInteraction(View view) {


        //Requests.getContactLocation();
        //Toast.makeText(MainActivity.this, "Request was sent", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onFragmentInteraction(Cursor cursor) {

        //ContactProvider.getInstance().deleteContact(this, cursor);

        LocationProvider.getInstance().getContactLocation(getApplicationContext(),
                cursor.getString(cursor.getColumnIndexOrThrow(LocationContract.HistoryEntry.COLUMN_PHONE)),
                String.valueOf(UUID.randomUUID()));

    }

    //History
    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onBackPressed() {
        if (drawerResult != null && drawerResult.isDrawerOpen()) {
            drawerResult.closeDrawer();
        }else {
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
                .withIcon(R.drawable.common_signin_btn_icon_dark),

                new PrimaryDrawerItem()
                        .withName(R.string.label_map)
                        .withIdentifier(1)
                        .withIcon(R.drawable.common_signin_btn_icon_dark),

                new PrimaryDrawerItem()
                        .withName(R.string.label_History)
                        .withIdentifier(1)
                        .withIcon(R.drawable.common_signin_btn_icon_dark)}
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
                        Toast.makeText(getApplicationContext(), "Pressed", Toast.LENGTH_SHORT).show();
                    }
                })
                .build();
    }

    /////////////////////////////////////////////////////////////////////////

    private void findViewsById() {

        toolbar = (Toolbar) findViewById(R.id.tool_bar);

        pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(new MyPagerAdapter(getFragmentManager(), Constants.getPagesTitle(this)));

        tabs = (SlidingTabLayout) findViewById(R.id.tabs);
        tabs.setDistributeEvenly(true);
        tabs.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0){
                    EventBus.getDefault().post(new MessageContactsUpdate());
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