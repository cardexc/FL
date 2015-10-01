package cardexc.com.freindlocation.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.ImageView;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import java.io.File;

import cardexc.com.freindlocation.data.Constants;
import cardexc.com.freindlocation.http.Requests;
import cardexc.com.freindlocation.service.events.MessageContactsUpdate;
import cardexc.com.freindlocation.sqlite.LocationContract.*;
import de.greenrobot.event.EventBus;


public class ContactProvider {

    private static ContactProvider mInstance = new ContactProvider();

    private ContactProvider() {

    }

    public static ContactProvider getInstance() {
        return mInstance;
    }

    public Cursor getContactCursor() {

        LocationDBHelper dbHelper = new LocationDBHelper(Constants.getApplicationContext());
        return dbHelper.getContactCursor();

    }

    public Cursor getContactCursorByDatabaseID(String databaseID) {

        LocationDBHelper dbHelper = new LocationDBHelper(Constants.getApplicationContext());

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor cursor = db.query(ContactEntry.TABLE_NAME,
                new String[]{ContactEntry._ID, ContactEntry.COLUMN_CONTACTID, ContactEntry.COLUMN_NAME, ContactEntry.COLUMN_PHONE, ContactEntry.COLUMN_APPROVED},
                ContactEntry._ID + " = ?",
                new String[]{databaseID},
                null, null, null);


        return cursor;

    }


    public Boolean userPhoneExists(String phone) {

        LocationDBHelper dbHelper = new LocationDBHelper(Constants.getApplicationContext());

        SQLiteDatabase readableDatabase = dbHelper.getReadableDatabase();

        Cursor cursor = readableDatabase.query(LocationContract.ContactEntry.TABLE_NAME,
                                                new String[] {LocationContract.ContactEntry.COLUMN_PHONE},
                                                LocationContract.ContactEntry.COLUMN_PHONE + " = ?",
                                                new String[]{phone},
                                                null, null, null, null);
        return
                (cursor == null || cursor.getCount() == 0) ? false : true;


    }

    public void addContactNumberToContacts(String phone, String name, String contactID){

        LocationDBHelper dbHelper = new LocationDBHelper(Constants.getApplicationContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(LocationContract.ContactEntry.COLUMN_PHONE, phone);
        values.put(LocationContract.ContactEntry.COLUMN_NAME,  name);
        values.put(LocationContract.ContactEntry.COLUMN_CONTACTID,  contactID);

        db.insert(LocationContract.ContactEntry.TABLE_NAME, null, values);

    }

    ////////////////////////////////////////////////////////////////

    public void ContactsToUpdateOnServer_insert(String contactPhone) {

        Log.i(Constants.TAG, "ContactsToUpdateOnServer_insert ");

        ContentValues contentValues = new ContentValues();
        contentValues.put(LocationContract.ContactsToUpdateEntry.COLUMN_PHONE, contactPhone);

        LocationDBHelper dbHelper = new LocationDBHelper(Constants.getApplicationContext());
        SQLiteDatabase writableDatabase = dbHelper.getWritableDatabase();
        writableDatabase.insert(LocationContract.ContactsToUpdateEntry.TABLE_NAME, null, contentValues);

    }

    public void ContactsToDeleteOnServer_insert(String contactPhone) {

        Log.i(Constants.TAG, "ContactsToDeleteOnServer_insert ");

        ContentValues contentValues = new ContentValues();
        contentValues.put(LocationContract.ContactsToDeleteEntry.COLUMN_PHONE, contactPhone);

        LocationDBHelper dbHelper = new LocationDBHelper(Constants.getApplicationContext());
        SQLiteDatabase writableDatabase = dbHelper.getWritableDatabase();
        writableDatabase.insert(LocationContract.ContactsToDeleteEntry.TABLE_NAME, null, contentValues);

        //////////////////

        deleteContact(contactPhone);
        EventBus.getDefault().post(new MessageContactsUpdate());
    }


    public void ContactsToUpdateOnServer_delete(String contactPhone) {

        Log.i(Constants.TAG, "ContactsToUpdateOnServer_delete ");

        LocationDBHelper dbHelper = new LocationDBHelper(Constants.getApplicationContext());
        SQLiteDatabase writableDatabase = dbHelper.getWritableDatabase();

        writableDatabase.delete(LocationContract.ContactsToUpdateEntry.TABLE_NAME,
                LocationContract.ContactsToUpdateEntry.COLUMN_PHONE + " = '" + contactPhone + "'",
                null);

    }

    public void ContactsToDeleteOnServer_delete(String contactPhone) {

        Log.i(Constants.TAG, "ContactsToDeleteOnServer_delete ");

        LocationDBHelper dbHelper = new LocationDBHelper(Constants.getApplicationContext());
        SQLiteDatabase writableDatabase = dbHelper.getWritableDatabase();

        writableDatabase.delete(LocationContract.ContactsToDeleteEntry.TABLE_NAME,
                LocationContract.ContactsToDeleteEntry.COLUMN_PHONE + " = '" + contactPhone + "'",
                null);

        //////////////

    }

    public void deleteContact(String contactPhone) {

        LocationDBHelper dbHelper = new LocationDBHelper(Constants.getApplicationContext());
        SQLiteDatabase writableDatabase = dbHelper.getWritableDatabase();

        writableDatabase.delete(LocationContract.ContactEntry.TABLE_NAME,
                ContactEntry.COLUMN_PHONE + " = ?",
                new String[]{contactPhone});

    }

    public static void setImageToView(ImageView contact_image, String contactId) {

        if (contactId == null) {
            contact_image.setImageResource(Constants.ANONYMOUS_ICON);
        }

        String path = getContactImagePath(contactId);

        File accPicFile = new File(path);
        if (!accPicFile.exists()) {
            contact_image.setImageResource(Constants.ANONYMOUS_ICON);
        } else {

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferQualityOverSpeed = true;

            Bitmap accountPicture = BitmapFactory.decodeFile(accPicFile.getAbsolutePath(), options);
            contact_image.setImageBitmap(accountPicture);

        }
    }

    public static Bitmap getContactImage(String contactId) {

        if (contactId == null) {
            BitmapDescriptorFactory.fromResource(Constants.ANONYMOUS_ICON);
        }

        String path = getContactImagePath(contactId);

        File accPicFile = new File(path);
        if (!accPicFile.exists()) {

            return BitmapFactory.decodeResource(Constants.getApplicationContext().getResources(), Constants.ANONYMOUS_ICON);

        } else {

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferQualityOverSpeed = true;

            Bitmap accountPicture = BitmapFactory.decodeFile(accPicFile.getAbsolutePath(), options);
            return accountPicture;
        }

    }

    @NonNull
    public static String getContactImagePath(String contactId) {
        return Constants.getApplicationContext().getApplicationInfo().dataDir
                    + Constants.CONTACTS_DIR + contactId + Constants.CONTACT_ICON_FORMAT;
    }


}
