package cardexc.com.freindlocation.service;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import cardexc.com.freindlocation.data.Constants;
import cardexc.com.freindlocation.http.Requests;
import cardexc.com.freindlocation.sqlite.LocationDBHelper;
import de.greenrobot.event.EventBus;

public class HistoryUpdaterService extends Service {

    private Handler handler = null;
    private static Runnable runnable = null;
    private Boolean isWorkable = true;

    public HistoryUpdaterService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.i(Constants.TAG, "onStartCommand HistoryUpdaterService");

        handler = new Handler();
        runnable = new Runnable() {
            public void run() {

                if (!isWorkable)
                    return;

                Log.i(Constants.TAG, " update history requests...");

                Requests.getHistoryRequests(getApplicationContext());

                handler.postDelayed(runnable, Constants.HISTORY_REQUESTS_SECONDS_TO_UPDATE);

            }
        };

        handler.postDelayed(runnable, Constants.HISTORY_REQUESTS_SECONDS_TO_UPDATE);

        return START_NOT_STICKY;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        isWorkable = false;

        Log.i(Constants.TAG, "HistoryUpdaterService onDestroy ");
    }
}
