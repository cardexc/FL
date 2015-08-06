package cardexc.com.freindlocation.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import cardexc.com.freindlocation.R;
import cardexc.com.freindlocation.data.Constants;
import cardexc.com.freindlocation.data.Contact;
import cardexc.com.freindlocation.service.events.MessageContactListReceived;
import cardexc.com.freindlocation.service.events.ServiceEventsInterface;
import de.greenrobot.event.EventBus;

public class Devices extends Fragment {

    EventBus eventBus;

    private AdapterView.OnItemClickListener onItemClickListener;

    private OnFragmentInteractionListener mListener;

    ListView devices_list;
    ArrayList<Contact> loadedContacts = new ArrayList<Contact>();

    public Devices() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.i(Constants.TAG, "Devices onCreateView");

        onItemClickListener = new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Contact item = (Contact) parent.getItemAtPosition(position);
                mListener.onFragmentInteraction(item.getPhone(), item.getIMEI(),item.getApproved());
            }
        };

        return inflater.inflate(R.layout.fragment_devices, container, false);
    }

    @Override
    public void onDestroyView() {

        Log.i(Constants.TAG, "Devices onDestroyView");
        super.onDestroyView();

        EventBus.getDefault().unregister(this);

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        eventBus = EventBus.getDefault();
        eventBus.register(this);

        ////////////////////

        devices_list = (ListView) view.findViewById(R.id.devices_list);

        ////////////////////

        Log.i(Constants.TAG, "Devices onViewCreated");

        ////////////////////

        updateDeviceList();

    }

    public static Devices newInstance(String param1, String param2) {

        Devices fragment = new Devices();
        return fragment;

    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i(Constants.TAG, "Devices onStart");
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void onEvent(ServiceEventsInterface event) {

        if (event instanceof MessageContactListReceived) {

            //Log.i(Constants.TAG, ((MessageContactListReceived) event).message.toString());
            loadedContacts = ((MessageContactListReceived) event).message;
            updateDeviceList();
        }

    }

    class DeviceAdapter extends BaseAdapter {

        Context ctx;
        LayoutInflater lInflater;

        DeviceAdapter(Context ctx) {
            this.ctx = ctx;
            lInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return loadedContacts.size();
        }

        @Override
        public Object getItem(int position) {
            return loadedContacts.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View view = convertView;
            if (view == null) {
                view = lInflater.inflate(R.layout.devicestab_row, parent, false);
            }

            Contact item = (Contact) getItem(position);

            TextView label_phone = (TextView) view.findViewById(R.id.label_phone);

            label_phone.setText(item.getPhone());

            return view;

        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void updateDeviceList() {

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                DeviceAdapter deviceAdapter = new DeviceAdapter(getActivity());
                devices_list.setAdapter(deviceAdapter);
                devices_list.setOnItemClickListener(onItemClickListener);

            }
        });


    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(View view);
        void onFragmentInteraction(String phone, String IMEI, Boolean approved);
    }

}
