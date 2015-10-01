package cardexc.com.freindlocation.activity;

import android.content.Intent;
import android.database.Cursor;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.UUID;


import cardexc.com.freindlocation.R;
import cardexc.com.freindlocation.data.Constants;
import cardexc.com.freindlocation.fragments.ConfirmationDialogFragment;
import cardexc.com.freindlocation.fragments.GetLocationProgressDialog;
import cardexc.com.freindlocation.fragments.Map;
import cardexc.com.freindlocation.service.events.HistoryListUpdate;
import cardexc.com.freindlocation.service.events.MessageContactsUpdate;
import cardexc.com.freindlocation.service.events.ServiceEventsInterface;
import cardexc.com.freindlocation.sqlite.ContactProvider;
import cardexc.com.freindlocation.sqlite.HistoryProvider;
import cardexc.com.freindlocation.sqlite.LocationContract;
import cardexc.com.freindlocation.sqlite.LocationProvider;
import cardexc.com.freindlocation.sqlite.MapProvider;
import de.greenrobot.event.EventBus;

public class ContactActivity extends FragmentActivity
        implements ConfirmationDialogFragment.OnConfirmationDialogFragmentClick {

    private String phone;
    private Cursor contactCursor;
    private TextView contactActivity_textView_name;
    private TextView contactActivity_textView_phone;
    private TextView contactActivity_exclamationTextView;
    private ImageView contact_exclamation_mark;
    private Button contact_button_deleteFromContacts;
    private Button contact_button_getLocation;
    private Button contact_button_showLastLocationOnMap;
    private ImageView contactImage;

    private View.OnClickListener buttonsOnClickListener;

    private Boolean hasLocation = false;

    private GetLocationProgressDialog getLocationProgressDialog;
    private EventBus eventBus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        findViewsById();

        fillContactInfo();

        eventBus = EventBus.getDefault();
        eventBus.register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void findViewsById() {

        contactImage = (ImageView) findViewById(R.id.contactActivity_contact_image);
        contactActivity_textView_name = (TextView) findViewById(R.id.contactActivity_textView_name);
        contactActivity_textView_phone = (TextView) findViewById(R.id.contactActivity_textView_phone);
        contactActivity_exclamationTextView = (TextView) findViewById(R.id.contactActivity_exclamationTextView);
        contact_exclamation_mark = (ImageView) findViewById(R.id.contact_exclamation_mark);

        contact_button_deleteFromContacts = (Button) findViewById(R.id.contact_button_deleteFromContacts);
        contact_button_getLocation = (Button) findViewById(R.id.contact_button_getLocation);
        contact_button_showLastLocationOnMap = (Button) findViewById(R.id.contact_button_showLastLocationOnMap);

        buttonsOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                switch (v.getId()) {

                    case (R.id.contact_button_deleteFromContacts): {

                        Bundle bundle = new Bundle();
                        bundle.putString("title", getResources().getString(R.string.label_contactdelete_confirmation));
                        bundle.putString("content", getResources().getString(R.string.label_contactdelete_content));

                        ConfirmationDialogFragment confirmationDialogFragment = new ConfirmationDialogFragment();
                        confirmationDialogFragment.setArguments(bundle);
                        confirmationDialogFragment.show(getSupportFragmentManager(), null);

                        break;
                    }
                    case (R.id.contact_button_getLocation): {

                        ////////////////
                        String approved = contactCursor.getString(contactCursor.getColumnIndexOrThrow(LocationContract.ContactEntry.COLUMN_APPROVED));
                        if (!"1".equals(approved)) {
                            Toast.makeText(Constants.getApplicationContext(), getResources().getString(R.string.device_locationreq_not_allowed), Toast.LENGTH_LONG).show();
                            break;
                        }

                        ////////////////
                        //getting location...
                        LocationProvider.getInstance().getContactLocation(getApplicationContext(),
                                contactCursor.getString(contactCursor.getColumnIndexOrThrow(LocationContract.HistoryEntry.COLUMN_PHONE)),
                                String.valueOf(UUID.randomUUID()));

                        ////////////////
                        //show progress dialog to user
                        Bundle bundle = new Bundle();
                        bundle.putString("title", getResources().getString(R.string.label_getting_location));

                        getLocationProgressDialog = new GetLocationProgressDialog();

                        getLocationProgressDialog.setArguments(bundle);
                        getLocationProgressDialog.show(getSupportFragmentManager(), null);
                        ////////////////

                        break;
                    }
                    case (R.id.contact_button_showLastLocationOnMap): {

                        if (!hasLocation) {
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.contactActivity_noLocationHistory), Toast.LENGTH_LONG).show();
                            break;
                        }

                        Cursor generallMapCursor = MapProvider.getGenerallMapCursor(Constants.getApplicationContext(),
                                contactCursor.getString(contactCursor.getColumnIndexOrThrow(LocationContract.ContactEntry.COLUMN_PHONE)));

                        generallMapCursor.moveToNext();

                        Map.getInstance().setIsSingleModeChoice(true);
                        Map.getInstance().setMapSingleCursor(generallMapCursor);

                        Intent intent = ContactActivity.this.getIntent();
                        intent.putExtra("action", "GotoMapPage");
                        ContactActivity.this.setResult(RESULT_OK, intent);

                        finish();

                        break;
                    }
                    default:
                        break;

                }


            }
        };

        contact_button_deleteFromContacts.setOnClickListener(buttonsOnClickListener);
        contact_button_getLocation.setOnClickListener(buttonsOnClickListener);
        contact_button_showLastLocationOnMap.setOnClickListener(buttonsOnClickListener);

    }

    private void fillContactInfo() {

        Intent intent = getIntent();
        String contactDatabaseID = intent.getStringExtra("id");
        contactCursor = ContactProvider.getInstance().getContactCursorByDatabaseID(contactDatabaseID);

        if (contactCursor != null) {

            contactCursor.moveToNext();
            ContactProvider.setImageToView(contactImage, contactCursor.getString(contactCursor.getColumnIndexOrThrow(LocationContract.ContactEntry.COLUMN_CONTACTID)));

            contactActivity_textView_name.setText(contactCursor.getString(contactCursor.getColumnIndexOrThrow(LocationContract.ContactEntry.COLUMN_NAME)));

            phone = contactCursor.getString(contactCursor.getColumnIndexOrThrow(LocationContract.ContactEntry.COLUMN_PHONE));
            contactActivity_textView_phone.setText(phone);

            Log.i(Constants.TAG + "/APPROVED", "=" + contactCursor.getString(contactCursor.getColumnIndexOrThrow(LocationContract.ContactEntry.COLUMN_APPROVED)));

            if ("1".equals(contactCursor.getString(contactCursor.getColumnIndexOrThrow(LocationContract.ContactEntry.COLUMN_APPROVED)))) {
                contactActivity_exclamationTextView.setVisibility(View.GONE);
                contact_exclamation_mark.setVisibility(View.GONE);
            } else {
                contactActivity_exclamationTextView.setText(getResources().getString(R.string.contactActivity_locationNotAllowed));
            }

            Cursor historyCursor = HistoryProvider.getInstance().getHistoryCursor(phone);
            hasLocation = historyCursor.moveToNext();
            historyCursor.close();

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_contact, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void OnConfirmationDialogFragmentClick_OnClickYes() {
        ContactProvider.getInstance().ContactsToDeleteOnServer_insert(
                contactCursor.getString(contactCursor.getColumnIndexOrThrow(LocationContract.ContactEntry.COLUMN_PHONE)));

        finish();
    }

    @Override
    public void OnConfirmationDialogFragmentClick_OnClickCancel() {

    }

    public void onEvent(ServiceEventsInterface event) {

        if (event instanceof HistoryListUpdate) {
            if (getLocationProgressDialog.isAdded()) {
                getLocationProgressDialog.dismiss();

                Cursor historyCursor = HistoryProvider.getInstance().getHistoryCursor(phone);
                hasLocation = historyCursor.moveToNext();
                historyCursor.close();

                if (hasLocation) {

                    Cursor generallMapCursor = MapProvider.getGenerallMapCursor(Constants.getApplicationContext(),
                            contactCursor.getString(contactCursor.getColumnIndexOrThrow(LocationContract.ContactEntry.COLUMN_PHONE)));

                    generallMapCursor.moveToNext();

                    Map.getInstance().setIsSingleModeChoice(true);
                    Map.getInstance().setMapSingleCursor(generallMapCursor);

                    Intent intent = ContactActivity.this.getIntent();
                    intent.putExtra("action", "GotoMapPage");
                    ContactActivity.this.setResult(RESULT_OK, intent);

                    finish();

                }


            }
        }

    }
}
