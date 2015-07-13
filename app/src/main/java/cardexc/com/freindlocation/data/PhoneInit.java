package cardexc.com.freindlocation.data;


import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.util.concurrent.TimeUnit;

import cardexc.com.freindlocation.http.Requests;


public class PhoneInit extends AsyncTask<Context, Void, Void> {

    @Override
    protected Void doInBackground(Context... params) {

        while (Constants.getPhonenum() == null || Constants.getIMEI() == null) {

            Log.i(Constants.TAG, "doInBackground begin");
            Constants.initializePhoneData(params[0]);

            Log.i(Constants.TAG, "IMEI/PHONE/ " + Constants.getIMEI() + " / " + Constants.getPhonenum());
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
        return null;
    }
}
