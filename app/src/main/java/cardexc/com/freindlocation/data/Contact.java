package cardexc.com.freindlocation.data;


import android.content.ContentUris;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import cardexc.com.freindlocation.sqlite.ContactProvider;

public class Contact {

    public String getPhone() {
        return phone;
    }

    public Boolean getApproved() {
        return approved;
    }

    private String name;
    private String IMEI;
    private String id;
    private String phone;
    private Boolean approved;

    public interface ContactNameIsNull {
        void ContactNameIsNull();
    }

    public static void addContactToLocalDB(Uri contactUri, Object callingObject) {

        String contactName = retrieveContactName(contactUri);
        Map userData = retrieveContactNumber(contactUri);

        String contactPhone = null;
        if (userData.get("contactPhone") != null)
            contactPhone = String.valueOf(userData.get("contactPhone")).replace("+", "");
        else {
            ((ContactNameIsNull) callingObject).ContactNameIsNull();
            return;
        }

        String contactID = String.valueOf(userData.get("contactID"));

        retrieveContactPhoto(contactID);

        if (!ContactProvider.getInstance().userPhoneExists(contactPhone)) {
            ContactProvider.getInstance().addContactNumberToContacts(contactPhone, contactName, contactID);
        }

        ContactProvider.getInstance().ContactsToUpdateOnServer_insert(contactPhone);

    }

    private static String retrieveContactName(Uri contactUri) {

        String contactName = null;

        Cursor cursor = Constants.getApplicationContext().getContentResolver().query(contactUri, null, null, null, null);

        if (cursor.moveToFirst()) {
            contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
        }

        cursor.close();

        return contactName;
    }

    private static Map<String, String> retrieveContactNumber(Uri contactUri) {

        HashMap<String, String> data = new HashMap<>();
        data.put("contactPhone", null);
        data.put("contactID", null);

        // getting contacts ID
        Cursor cursorID = Constants.getApplicationContext().getContentResolver().query(contactUri,
                new String[]{ContactsContract.Contacts._ID},
                null, null, null);

        if (cursorID.moveToFirst()) {

            data.put("contactID", cursorID.getString(cursorID.getColumnIndex(ContactsContract.Contacts._ID)));
        }

        cursorID.close();

        Cursor cursorPhone = Constants.getApplicationContext().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER},

                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?  AND (" +
                        ContactsContract.CommonDataKinds.Phone.TYPE + " = " + ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE + " OR " +
                        ContactsContract.CommonDataKinds.Phone.TYPE + " = " + ContactsContract.CommonDataKinds.Phone.TYPE_MAIN + ")",

                new String[]{data.get("contactID")},
                null);

        if (cursorPhone.moveToFirst()) {

            String temp_phone = cursorPhone.getString(cursorPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            data.put("contactPhone", setContactNumberToNeedFormat(temp_phone));

        }

        cursorPhone.close();

        return data;

    }

    private static void retrieveContactPhoto(String contactID) {

        Bitmap photo = null;

        try {
            InputStream inputStream = ContactsContract.Contacts.openContactPhotoInputStream(Constants.getApplicationContext().getContentResolver(),
                    ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, new Long(contactID)), true);

            if (inputStream != null) {

                byte[] stream_bytes = new byte[inputStream.available()];
                inputStream.read(stream_bytes);


                String path = Constants.getApplicationContext().getApplicationInfo().dataDir
                        + Constants.CONTACTS_DIR;

                File directory = new File(path);
                if (!directory.exists())
                    new File(path).mkdir();

                path += contactID + Constants.CONTACT_ICON_FORMAT;

                File iconFile = new File(path);
                if (!iconFile.exists())
                    iconFile.createNewFile();

                FileOutputStream out = new FileOutputStream(new File(path));

                out.write(stream_bytes);
                out.close();

            }

            if (inputStream != null)
                inputStream.close();

        } catch (IOException e) {
            Log.i(Constants.TAG, "retrieveContactPhoto ERROR / " + e.getMessage());
        }


    }

    public static String setContactNumberToNeedFormat(String number) {

        number = number.replace(" ", "").replace("+", "");

        if (number.length() > 1) {
            if ("38".equals(number.substring(0, 2))) {
                number = number.substring(2, number.length());
            }
        }

        return number;

    }

}
