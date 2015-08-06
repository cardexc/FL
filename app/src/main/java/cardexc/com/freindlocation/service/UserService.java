package cardexc.com.freindlocation.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import cardexc.com.freindlocation.data.Constants;
import cardexc.com.freindlocation.fragments.Devices;
import cardexc.com.freindlocation.http.Requests;

public class UserService extends Service {

    ExecutorService executors;

    public UserService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Log.i(Constants.TAG, "UserService onCreate");
        executors = Executors.newFixedThreadPool(1);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        executors.shutdownNow();
        executors = null;

        System.gc();

        Log.v(Constants.TAG, "UserService onDestroy");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.i(Constants.TAG, "UserService onStartCommand");

        String command = intent.getStringExtra("command");
        Runnable runedTask;

        switch (command) {


            case Constants.GetContactListCommand:

                runedTask = new GetContactList(startId);
                executors.execute(runedTask);

                break;

        }


        return START_NOT_STICKY;

    }


    class GetContactList implements Runnable {

        private int startId;

        public GetContactList(int startId) {
            this.startId = startId;
        }

        @Override
        public void run() {

            while (true) {

                String mysqlid = Constants.getInstance(UserService.this).getMYSQLID();
                if (mysqlid != null) {
                    Requests.getContactListFromServer(UserService.this);
                    break;
                } else
                    Requests.getMySqlIdFromServer(UserService.this);

                try {
                    TimeUnit.SECONDS.sleep(3);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            stop();
        }

        void stop() {
            Log.i(Constants.TAG, "UserService stopSelf / " + this.startId);
            stopSelf(startId);
        }

    }


}
