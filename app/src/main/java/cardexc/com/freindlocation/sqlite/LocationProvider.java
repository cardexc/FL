package cardexc.com.freindlocation.sqlite;

import android.content.Context;

import cardexc.com.freindlocation.http.Requests;

public class LocationProvider {

    private static LocationProvider mInstance = new LocationProvider();

    private LocationProvider() {

    }

    public static LocationProvider getInstance() {
        return mInstance;
    }

    public void getContactLocation(Context context, String phone, String uuid) {

        HistoryProvider.getInstance().insertRecordHistoryTab(context, phone, uuid);
        Requests.getContactLocation(context, phone, uuid);

    }



}
