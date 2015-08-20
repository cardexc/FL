package cardexc.com.freindlocation.service;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.Map;

import cardexc.com.freindlocation.data.Constants;
import cardexc.com.freindlocation.http.Requests;
import cardexc.com.freindlocation.service.events.MessageContactListReceived;
import cardexc.com.freindlocation.service.events.ServiceEventsInterface;
import cardexc.com.freindlocation.sqlite.LocationContract;
import cardexc.com.freindlocation.sqlite.LocationDBHelper;
import de.greenrobot.event.EventBus;

public class ContactsUpdaterService extends Service {

    public Handler handler = null;
    public static Runnable runnable = null;
    private Boolean isWorkable = true;

    EventBus eventBus;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        eventBus = EventBus.getDefault();
        eventBus.register(this);

        Log.i(Constants.TAG, "onStartCommand CONTACTS Updater ");

        handler = new Handler();
        runnable = new Runnable() {
            public void run() {

                if (!isWorkable)
                    return;

                Log.i(Constants.TAG, "operation X ");

                Requests.getContactListFromServer(getApplicationContext());

                LocationDBHelper locationDBHelper = new LocationDBHelper(getApplicationContext());
                Cursor contactsToUpdateEntryCursor = locationDBHelper.getContactsToUpdateEntryCursor();

                while (contactsToUpdateEntryCursor.moveToNext()) {

                    String contactPhone = contactsToUpdateEntryCursor.getString(0);
                    Requests.putContactToServer(getApplicationContext(), contactPhone);
                }

                handler.postDelayed(runnable, Constants.CONTACTLIST_SECONDS_TO_UPDATE);

            }
        };

        handler.postDelayed(runnable, Constants.CONTACTLIST_SECONDS_TO_UPDATE);

        return START_NOT_STICKY;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        isWorkable = false;

        EventBus.getDefault().unregister(this);

        Log.i(Constants.TAG, "ContactsUpdaterService onDestroy ");
    }

    public void onEvent(ServiceEventsInterface event) {

        if (event instanceof MessageContactListReceived) {

            Log.i(Constants.TAG, "onEvent! get CONTACTS LIST");
            Map<String, Boolean> contacts = ((MessageContactListReceived) event).message;

            if (contacts.size() == 0)
                return;

            LocationDBHelper locationDBHelper = new LocationDBHelper(getApplicationContext());
            SQLiteDatabase writableDatabase = locationDBHelper.getWritableDatabase();

            for (Map.Entry<String, Boolean> contact : contacts.entrySet()) {

                ContentValues cv = new ContentValues();
                cv.put(LocationContract.ContactEntry.COLUMN_APPROVED, contact.getValue());

                writableDatabase.update(LocationContract.ContactEntry.TABLE_NAME,
                        cv,
                        LocationContract.ContactEntry.COLUMN_PHONE + " = ?",
                        new String[]{contact.getKey()});

            }


        }

    }

}
