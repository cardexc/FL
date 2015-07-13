package cardexc.com.freindlocation.data;


import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.google.android.gms.location.LocationRequest;

public class Constants {

    public final static int LOCATION_REQUEST_PRIORITY_UPDATES = LocationRequest.PRIORITY_HIGH_ACCURACY;
    public final static int LOCATION_REQUEST_INTERVAL_MILLISECONDS = 10000;

    public final static String TAG = "myapp";

    public static String MYSQLID = null;
    public static String phonenum = null;
    public static String IMEI = null;

    public static final String SERVHTTP_GETCONTACTLIST  = "http://585475.cardexc.web.hosting-test.net/frloc/frloc_getContacstList.php"; //?id=%s";
    public static final String SERVHTTP_USERPHONEEXISTS = "http://585475.cardexc.web.hosting-test.net/frloc/frloc_checkUser.php?&param1=%s&param2=%s";
    public static final String SERVHTTP_USERPHONEADD    = "http://585475.cardexc.web.hosting-test.net/frloc/frloc_addUser.php";
    public static final String SERVHTTP_SETLOCATION     = "http://585475.cardexc.web.hosting-test.net/frloc/frloc_setLocation.php?&id=%s&lat=%s&long=%s";

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static void initializePhoneData(Context context) {

        if (getIMEI() == null || getPhonenum() == null) {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);

            String line1Number = telephonyManager.getLine1Number();
            String IMEI = telephonyManager.getDeviceId();

            setPhonenum(line1Number);
            setIMEI(IMEI);

        }

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    public static String getMYSQLID() {
        return MYSQLID;
    }

    public static void setMYSQLID(String MYSQLID) {
        Constants.MYSQLID = MYSQLID;
        Log.i(TAG, "ID set:" + MYSQLID);
    }

    public static String getPhonenum() {
        return phonenum;
    }

    public static void setPhonenum(String phonenum) {
        Constants.phonenum = phonenum.replace("+","");
        Log.i(TAG, "Phonenum set:" + phonenum);
    }

    public static String getIMEI() {
        return IMEI;
    }

    public static void setIMEI(String IMEI) {
        Constants.IMEI = IMEI;
        Log.i(TAG, "IMEI set:" + IMEI);
    }
}
