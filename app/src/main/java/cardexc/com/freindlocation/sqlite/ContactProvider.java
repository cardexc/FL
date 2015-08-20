package cardexc.com.freindlocation.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import cardexc.com.freindlocation.R;
import cardexc.com.freindlocation.data.Constants;
import cardexc.com.freindlocation.http.Requests;

public class ContactProvider {

    private static ContactProvider mInstance = new ContactProvider();

    private ContactProvider() {

    }

    public static ContactProvider getInstance() {
        return mInstance;
    }

    public Cursor getContactCursor(Context context) {

        LocationDBHelper dbHelper = new LocationDBHelper(context);
        return dbHelper.getContactCursor();

    }

    public Boolean userPhoneExists(Context context, String phone) {

        LocationDBHelper dbHelper = new LocationDBHelper(context);

        SQLiteDatabase readableDatabase = dbHelper.getReadableDatabase();

        Cursor cursor = readableDatabase.query(LocationContract.ContactEntry.TABLE_NAME,
                                                new String[] {LocationContract.ContactEntry.COLUMN_PHONE},
                                                LocationContract.ContactEntry.COLUMN_PHONE + " = ?",
                                                new String[]{phone},
                                                null, null, null, null);
        return
                (cursor == null || cursor.getCount() == 0) ? false : true;


    }

    public void addContactNumberToContacts(Context context, String phone, String name, String contactID){

        LocationDBHelper dbHelper = new LocationDBHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(LocationContract.ContactEntry.COLUMN_PHONE, phone);
        values.put(LocationContract.ContactEntry.COLUMN_NAME,  name);
        values.put(LocationContract.ContactEntry.COLUMN_CONTACTID,  contactID);

        db.insert(LocationContract.ContactEntry.TABLE_NAME, null, values);

    }

    ////////////////////////////////////////////////////////////////

    public void ContactsToUpdateOnServer_insert(Context context, String contactPhone) {

        Log.i(Constants.TAG, "ContactsToUpdateOnServer_insert ");

        ContentValues contentValues = new ContentValues();
        contentValues.put(LocationContract.ContactsToUpdateEntry.COLUMN_PHONE, contactPhone);

        LocationDBHelper dbHelper = new LocationDBHelper(context);
        SQLiteDatabase writableDatabase = dbHelper.getWritableDatabase();
        writableDatabase.insert(LocationContract.ContactsToUpdateEntry.TABLE_NAME, null, contentValues);

    }

    public void ContactsToUpdateOnServer_delete(Context context, String contactPhone) {

        Log.i(Constants.TAG, "ContactsToUpdateOnServer_delete ");

        LocationDBHelper dbHelper = new LocationDBHelper(context);
        SQLiteDatabase writableDatabase = dbHelper.getWritableDatabase();

        writableDatabase.delete(LocationContract.ContactsToUpdateEntry.TABLE_NAME,
                LocationContract.ContactsToUpdateEntry.COLUMN_PHONE + " = '" + contactPhone + "'",
                null);

    }

    public void deleteContact(Context context, Cursor cursor) {

        LocationDBHelper dbHelper = new LocationDBHelper(context);
        SQLiteDatabase writableDatabase = dbHelper.getWritableDatabase();

        String _ID = cursor.getString(cursor.getColumnIndexOrThrow(LocationContract.ContactEntry._ID));
        String contactPhone = cursor.getString(cursor.getColumnIndexOrThrow(LocationContract.ContactEntry.COLUMN_PHONE));

        writableDatabase.delete(LocationContract.ContactEntry.TABLE_NAME,
                "_ID = ?",
                new String[]{_ID});

        Requests.deleteContactFromServer(context, contactPhone);
    }

    public static void setImageToView(Context context, ImageView contact_image, String contactId) {

        if (contactId == null) {
            contact_image.setImageResource(Constants.ANONYMOUS_ICON);
        }

        String path = context.getApplicationInfo().dataDir
                + Constants.CONTACTS_DIR + contactId + Constants.CONTACT_ICON_FORMAT;

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

}
