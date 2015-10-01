package cardexc.com.freindlocation.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import cardexc.com.freindlocation.R;
import cardexc.com.freindlocation.data.Constants;
import cardexc.com.freindlocation.fragments.FakeLocationActivityFragment;

public class FakeLocationActivity extends FragmentActivity {

    private FakeLocationActivityFragment fakeLocationActivityFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fake_location);

        fakeLocationActivityFragment = new FakeLocationActivityFragment();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fake_location_container, fakeLocationActivityFragment)
                .commit();

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.v(Constants.TAG, "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");

        fakeLocationActivityFragment.getProductsList();

        // Pass on the activity result to the helper for handling
        /*if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
            // not handled, so handle it ourselves (here's where you'd
            // perform any handling of activity results not related to in-app
            // billing...
            super.onActivityResult(requestCode, resultCode, data);
        }
        else {
            Log.v(Constants.TAG, "onActivityResult handled by IABUtil.");
        }*/

    }
}
