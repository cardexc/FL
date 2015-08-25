package cardexc.com.freindlocation.fragments;


import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import cardexc.com.freindlocation.R;
import cardexc.com.freindlocation.activity.FragmentActivityClass;
import cardexc.com.freindlocation.activity.MainActivity;

public class Map extends Fragment{

    private static GoogleMap mMap;

    public static Map newInstance() {
        Map fragment = new Map();
        return fragment;
    }

    private static void setUpMap() {
        // For showing a move to my loction button
        mMap.setMyLocationEnabled(true);
        // For dropping a marker at a point on the Map
        mMap.addMarker(new MarkerOptions().position(new LatLng(50.443988, 30.493717)).title("My Home").snippet("Home Address"));
        // For zooming automatically to the Dropped PIN Location
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(50.443988,
                30.493717), 12.0f));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_maps, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        // TODO Auto-generated method stub
        if (mMap != null)
            setUpMap();

        if (mMap == null) {

            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) MainActivity.fragmentManager.findFragmentById(R.id.map)).getMap(); // getMap is deprecated

           // Check if we were successful in obtaining the map.
            if (mMap != null)
                setUpMap();
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (mMap != null) {
            MainActivity.fragmentManager.beginTransaction()
                    .remove(FragmentActivityClass.getInstance().getMySupportFragmentManager().findFragmentById(R.id.map)).commit();
            mMap = null;
        }

    }
}
