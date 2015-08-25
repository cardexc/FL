package cardexc.com.freindlocation.service;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cardexc.com.freindlocation.data.Constants;
import cardexc.com.freindlocation.http.Requests;

public class LocationService extends Service implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    public static Boolean isRunning;
    public static GoogleApiClient mGoogleApiClient;
    private ExecutorService executors;

    static{
        isRunning = false;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        executors = Executors.newFixedThreadPool(1);
    }

    @Override
    public void onDestroy() {
        isRunning = false;
        executors.shutdownNow();
        executors = null;
        System.gc();
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Constants.setApplicationContext(getApplicationContext());

        Log.i(Constants.TAG, "onStartCommand LocationService");

        if (!isRunning) {

            Requests.getMySqlIdFromServer(this);

            isRunning = true;

            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            mGoogleApiClient.connect();

        } else Log.i(Constants.TAG, "onStartCommand, service already started");

        return START_REDELIVER_INTENT;
    }

    @Override
    public void onConnected(Bundle bundle) {

        if (mGoogleApiClient == null) {
            Log.i(Constants.TAG, "onConnected G API client // mGoogleApiClient is NULL!");
            return;
        }

        Log.i(Constants.TAG, "onConnected G API client");

        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(Constants.LOCATION_REQUEST_INTERVAL_MILLISECONDS);
        locationRequest.setPriority(Constants.LOCATION_REQUEST_PRIORITY_UPDATES);

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(Constants.TAG, "onConnectionSuspended G API client");
    }

    @Override
    public void onLocationChanged(Location location) {
        String mysqlid = Constants.getInstance(this).getMYSQLID();

        if (mysqlid == null) {
            Requests.getMySqlIdFromServer(this);

            return;
        }

        if (location != null) {

            Log.i(Constants.TAG, "location latt = // " + String.valueOf(location.getLatitude()));
            Log.i(Constants.TAG, "location long = // " + String.valueOf(location.getLongitude()));

            //Requests.setLocationToServer(location, this);
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(Constants.TAG, "onConnectionFailed G API client");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
