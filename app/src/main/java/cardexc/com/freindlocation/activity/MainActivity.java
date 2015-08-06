package cardexc.com.freindlocation.activity;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import java.util.UUID;

import cardexc.com.freindlocation.R;
import cardexc.com.freindlocation.adapters.MyPagerAdapter;
import cardexc.com.freindlocation.data.Constants;
import cardexc.com.freindlocation.data.PhoneInit;
import cardexc.com.freindlocation.fragments.Devices;
import cardexc.com.freindlocation.fragments.History;
import cardexc.com.freindlocation.http.Requests;
import cardexc.com.freindlocation.service.UserService;
import cardexc.com.freindlocation.sqlite.HistoryProvider;

public class MainActivity extends AppCompatActivity
        implements
        Devices.OnFragmentInteractionListener,
        History.OnFragmentInteractionListener{

    private android.support.v4.view.ViewPager pager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(new MyPagerAdapter(getFragmentManager()));

    }

    @Override
    protected void onStart() {
        super.onStart();

        new PhoneInit().execute(this);
        //startService(new Intent(this, LocationService.class));

        Intent userServiceIntent = new Intent(this, UserService.class);
        userServiceIntent.putExtra("command", Constants.GetContactListCommand);
        startService(userServiceIntent);

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
        Toast.makeText(MainActivity.this, "Request was sent", Toast.LENGTH_SHORT).show();

    }
    @Override
    public void onFragmentInteraction(String phone, String IMEI, Boolean approved) {

        if (!approved) {
            Toast.makeText(MainActivity.this, "Contact not approved. Requesting location impossible", Toast.LENGTH_LONG).show();
            return;
        }

        String uuid = UUID.randomUUID().toString();

        Requests.getContactLocation(this, phone, IMEI, uuid);

        //////////

        HistoryProvider.getInstance().insertRecordHistoryTab(this, phone, IMEI, uuid);

        //////////

        Toast.makeText(MainActivity.this, "Request was sent", Toast.LENGTH_SHORT).show();

    }

    //History
    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}