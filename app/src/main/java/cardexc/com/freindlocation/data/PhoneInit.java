package cardexc.com.freindlocation.data;


import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.util.concurrent.TimeUnit;


public class PhoneInit extends AsyncTask<Context, Void, Void> {

    @Override
    protected Void doInBackground(Context... params) {

        Log.i(Constants.TAG, "Getting phone number & IMEI...");

        Constants.getInstance();

        Log.i(Constants.TAG, "IMEI & PHONE were set up: " + Constants.getInstance().getIMEI() + " / " + Constants.getInstance().getPhonenum());

        return null;

    }
}
