package cardexc.com.freindlocation.data;


import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.google.android.gms.location.LocationRequest;

import cardexc.com.freindlocation.R;

public class Constants {

    private static Constants mInstance;

    public static Context APPLICATION_CONTEXT;

    public static final int TABS_COUNT = 3;
    public static final int CONTACTLIST_SECONDS_TO_UPDATE      = 5 * 1000;
    public static final int HISTORY_REQUESTS_SECONDS_TO_UPDATE = 5 * 1000;

    public static final int LOCATION_REQUEST_PRIORITY_UPDATES = LocationRequest.PRIORITY_HIGH_ACCURACY;
    public static final int LOCATION_REQUEST_INTERVAL_MILLISECONDS = 10000;
    public static final int REQUEST_CODE_PICK_CONTACTS  = 1;
    public static final int ANONYMOUS_ICON  = R.drawable.anonymous_contact;

    public static final String TAG = "myapp";
    public static final String CONTACTS_DIR = "/Contacts/";
    public static final String CONTACTS_DIR_TEST   = "mnt/storage/sdcard0/contactsTesting/";
    public static final String CONTACT_ICON_FORMAT = ".png";

    public String MYSQLID = null;
    public String phonenum = null;
    public String IMEI = null;

    public static final String SERVHTTP  = "http://585475.cardexc.web.hosting-test.net/frloc/";
    public static final String SERVHTTP_GETCONTACTLIST  = SERVHTTP + "frloc_getContactList.php?userId=%s";
    public static final String SERVHTTP_USERPHONEEXISTS = SERVHTTP + "frloc_checkUser.php?&phone=%s&imei=%s";
    public static final String SERVHTTP_USERPHONEADD    = SERVHTTP + "frloc_addUser.php?&param1=%s&param2=%s";
    public static final String SERVHTTP_SETLOCATION     = SERVHTTP + "frloc_setLocation.php?&id=%s&lat=%s&long=%s";
    public static final String SERVHTTP_GETCONTACTLOCATION = SERVHTTP + "frloc_getLocation.php?&userid=%s&imei=%s&target_phone=%s&uuid=%s&requested_time=%s";
    public static final String SERVHTTP_ADDCONTACT      = SERVHTTP + "frloc_addContact.php?&userId=%s&contactPhone=%s";
    public static final String SERVHTTP_DELETECONTACT   = SERVHTTP + "frloc_deleteContact.php?&userId=%s&contactPhone=%s&imei=%s";
    public static final String SERVHTTP_GETHISTORY      = SERVHTTP + "frloc_getHistoryRequests.php?&userid=%s&imei=%s";
    public static final String SERVHTTP_SETHISTORY_UPDATED = SERVHTTP + "frloc_setHistoryUpdated.php?&uuid=%s";

    public static final String GetContactListCommand = "GetContactListCommand";

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private Constants(Context context) {
        initializePhoneData(context);
    }

    public static Constants getInstance(Context context) {
        if (mInstance == null) {
            synchronized (Constants.class) {
                if (mInstance == null)
                    mInstance = new Constants(context);
            }
        }

        return mInstance;
    }

    public void initializePhoneData(Context context) {

        if (getIMEI() == null || getPhonenum() == null) {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);

            String line1Number = telephonyManager.getLine1Number();
            String IMEI = telephonyManager.getDeviceId();

            setPhonenum(Contact.setContactNumberToNeedFormat(line1Number));
            setIMEI(IMEI);

        }

    }

    public static CharSequence[] getPagesTitle(Context context) {

        CharSequence titles[] = {
            context.getResources().getString(R.string.label_contacts),
                    context.getResources().getString(R.string.label_map),
                    context.getResources().getString(R.string.label_History),
        };

        return titles;

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public String getMYSQLID() {
        return this.MYSQLID;
    }

    public void setMYSQLID(String MYSQLID) {
        this.MYSQLID = MYSQLID;
        Log.i(TAG, "ID set:" + MYSQLID);
    }

    public String getPhonenum() {
        return this.phonenum;
    }

    public void setPhonenum(String phonenum) {
        this.phonenum = phonenum.replace("+","");
        Log.i(TAG, "Phonenum set:" + phonenum);
    }

    public String getIMEI() {
        return this.IMEI;
    }

    public void setIMEI(String IMEI) {
        this.IMEI = IMEI;
        Log.i(TAG, "IMEI set:" + IMEI);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static Context getApplicationContext() {
        return APPLICATION_CONTEXT;
    }

    public static void setApplicationContext(Context applicationContext) {

        if (APPLICATION_CONTEXT == null)
            APPLICATION_CONTEXT = applicationContext;
    }

}
