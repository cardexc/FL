package cardexc.com.freindlocation.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.melnykov.fab.FloatingActionButton;

import java.io.File;

import cardexc.com.freindlocation.R;
import cardexc.com.freindlocation.data.Constants;
import cardexc.com.freindlocation.data.Contact;
import cardexc.com.freindlocation.service.events.MessageContactsUpdate;
import cardexc.com.freindlocation.service.events.ServiceEventsInterface;
import cardexc.com.freindlocation.sqlite.ContactProvider;
import cardexc.com.freindlocation.sqlite.LocationContract;
import de.greenrobot.event.EventBus;

public class Devices extends Fragment {

    EventBus eventBus;

    private AdapterView.OnItemClickListener onItemClickListener;
    private OnFragmentInteractionListener mListener;
    private FloatingActionButton fab;

    ListView devices_list;

    @Override
    public void onResume() {
        super.onResume();
        updateDeviceList();
    }

    public Devices() {
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_devices, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /*eventBus = EventBus.getDefault();
        eventBus.register(this);*/

        findViewsById(view);

        createListeners();

        updateDeviceList();

    }

    @Override
    public void onDestroyView() {

        super.onDestroyView();

        //EventBus.getDefault().unregister(this);

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        eventBus = EventBus.getDefault();
        eventBus.register(this);

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

        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.REQUEST_CODE_PICK_CONTACTS && resultCode == -1) {

            Uri contactUri = data.getData();
            Contact.addContactToLocalDB(getActivity(), contactUri);

            updateDeviceList();
        }
    }

    public void onEvent(ServiceEventsInterface event) {

        if (event instanceof MessageContactsUpdate)
            updateDeviceList();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void findViewsById(View view) {

        devices_list = (ListView) view.findViewById(R.id.devices_list);

        fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.attachToListView(devices_list);

        //////////////////////////////////

    }

    private void createListeners() {

        onItemClickListener = new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                mListener.onFragmentInteraction(cursor);
            }
        };

        View.OnClickListener fab_listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseTheContact();
            }
        };
        fab.setOnClickListener(fab_listener);
    }

    private void chooseTheContact() {
        startActivityForResult(new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI), Constants.REQUEST_CODE_PICK_CONTACTS);
    }

    public void updateDeviceList() {

        Cursor contactCursor = ContactProvider.getInstance().getContactCursor(getActivity());
        ContactCursorAdapter contactCursorAdapter = new ContactCursorAdapter(getActivity(), contactCursor, 0);

        devices_list.setAdapter(contactCursorAdapter);
        devices_list.setOnItemClickListener(onItemClickListener);

    }

    public static Devices newInstance(String param1, String param2) {

        Devices fragment = new Devices();
        return fragment;

    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(View view);

        void onFragmentInteraction(Cursor cursor);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public class ContactCursorAdapter extends android.support.v4.widget.CursorAdapter {

        int count = 0;

        public ContactCursorAdapter(Context context, Cursor c, int flags) {
            super(context, c, flags);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            return LayoutInflater.from(context).inflate(R.layout.devicestab_row, parent, false);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {

            TextView contact_label_phone = (TextView) view.findViewById(R.id.contact_label_phone);
            TextView contact_label_contactName = (TextView) view.findViewById(R.id.contact_label_contactName);
            ImageView contact_image = (ImageView) view.findViewById(R.id.contact_image);
            ImageView contact_exclamation_mark = (ImageView) view.findViewById(R.id.contact_exclamation_mark);

            String phone = cursor.getString(cursor.getColumnIndexOrThrow(LocationContract.ContactEntry.COLUMN_PHONE));
            String contactName = cursor.getString(cursor.getColumnIndexOrThrow(LocationContract.ContactEntry.COLUMN_NAME));
            String contactId = cursor.getString(cursor.getColumnIndexOrThrow(LocationContract.ContactEntry.COLUMN_CONTACTID));
            Boolean approved = cursor.getInt(cursor.getColumnIndexOrThrow(LocationContract.ContactEntry.COLUMN_APPROVED)) != 0;

            ContactProvider.setImageToView(context, contact_image, contactId);

            contact_label_phone.setText(phone);
            contact_label_contactName.setText(contactName);

            contact_exclamation_mark.setVisibility(approved ? View.INVISIBLE : View.VISIBLE);

        }



    }
}
