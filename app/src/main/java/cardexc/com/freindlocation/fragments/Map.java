package cardexc.com.freindlocation.fragments;


import android.app.Fragment;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import cardexc.com.freindlocation.R;
import cardexc.com.freindlocation.activity.MainActivity;
import cardexc.com.freindlocation.data.CircleImageView;
import cardexc.com.freindlocation.data.Constants;
import cardexc.com.freindlocation.sqlite.ContactProvider;
import cardexc.com.freindlocation.sqlite.LocationContract;
import cardexc.com.freindlocation.sqlite.MapProvider;

public class Map extends Fragment {

    private static GoogleMap mMap;
    private static Map mInstance;
    private Cursor mapCursor;
    private List<Marker> markers = new ArrayList<>();
    private CircleImageView contact_map_image;

    public void setIsSingleModeChoice(Boolean isSingleModeChoice) {
        this.isSingleModeChoice = isSingleModeChoice;
    }

    private Boolean isSingleModeChoice = false;

    public void setMapSingleCursor(Cursor mapCursor) {

        this.mapCursor = mapCursor;

        markers.clear();
        mMap.clear();

        addMarker();

        this.mapCursor = null;

    }

    public static Map getInstance() {

        if (mInstance == null) {
            mInstance = new Map();
        }
        

        return mInstance;
    }

    private void addMarker() {

        if (mapCursor == null)
            return;

        /////////////////////////////////
        String contactId = mapCursor.getString(mapCursor.getColumnIndexOrThrow(LocationContract.ContactEntry.COLUMN_CONTACTID));
        ContactProvider.setImageToView(contact_map_image, contactId);

        String name = mapCursor.getString(mapCursor.getColumnIndexOrThrow(LocationContract.ContactEntry.COLUMN_NAME));

        String latitude  = mapCursor.getString(mapCursor.getColumnIndexOrThrow(LocationContract.HistoryEntry.COLUMN_LATITUDE));
        String longitude = mapCursor.getString(mapCursor.getColumnIndexOrThrow(LocationContract.HistoryEntry.COLUMN_LONGITUDE));
        String timedate  = mapCursor.getString(mapCursor.getColumnIndexOrThrow(LocationContract.HistoryEntry.COLUMN_REQUEST_TIME));

        if ("null".equals(latitude) || latitude == null
                || "null".equals(longitude) || longitude == null)
            return;

        Marker marker = mMap.addMarker(
                new MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromBitmap(contact_map_image.getmBitmap()))
                        .flat(false)
                        .position(new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude)))
                        .title(name)
                        .snippet(getTimeFromStr(timedate))
        );

        markers.add(marker);

        if (isSingleModeChoice)
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude)), 12.0f));

    }

    public void setUpMap() {

        if (isSingleModeChoice) {
            isSingleModeChoice = false;
            return;
        }

        mapCursor = null;
        mapCursor = MapProvider.getGenerallMapCursor(Constants.getApplicationContext(), null);

        markers.clear();

        while (mapCursor.moveToNext()) {
            addMarker();
        }

        updateCameraPositionByMarkers();

    }

    private void updateCameraPositionByMarkers() {

        if (markers.size() == 0)
            return;

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Marker marker : markers) {
            builder.include(marker.getPosition());
        }
        LatLngBounds bounds = builder.build();

        int padding = 100; //
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);

        mMap.animateCamera(cu); // OR moveCamera(cu);
    }

    private String getTimeFromStr(String timeStr) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(timeStr));

        String day = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
        day = day.length() == 1 ? "0" + day : day;

        String month = String.valueOf(calendar.get(Calendar.MONTH) + 1);
        month = month.length() == 1 ? "0" + month : month;

        String hour = String.valueOf(calendar.get(Calendar.HOUR_OF_DAY));
        hour = hour.length() == 1 ? "0" + hour : hour;

        String minute = String.valueOf(calendar.get(Calendar.MINUTE));
        minute = minute.length() == 1 ? "0" + minute : minute;

        return day + "/" + month + " " + hour + ":" + minute;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_maps, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        contact_map_image = (CircleImageView) view.findViewById(R.id.contact_map_image);

        if (mMap == null) {

            Log.d(Constants.TAG, "mMap = null");

            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) MainActivity.fragmentManager.findFragmentById(R.id.map)).getMap(); // getMap is deprecated

            Log.d(Constants.TAG, "mMap = " + mMap);

        }

    }

    @Override
    public void onStop() {
        super.onStop();

        try {
            if (mMap != null) {
                MainActivity.fragmentManager.beginTransaction()
                        .remove(MainActivity.fragmentManager.findFragmentById(R.id.map)).commit();
                mMap = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
