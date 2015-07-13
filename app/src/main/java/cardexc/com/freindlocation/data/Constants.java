package cardexc.com.freindlocation.data;


import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.google.android.gms.location.LocationRequest;

public class Constants {

    private static Constants mInstance;

    public static final int LOCATION_REQUEST_PRIORITY_UPDATES = LocationRequest.PRIORITY_HIGH_ACCURACY;
    public static final int LOCATION_REQUEST_INTERVAL_MILLISECONDS = 10000;

    public static final String TAG = "myapp";

    public String MYSQLID = null;
    public String phonenum = null;
    public String IMEI = null;

    public static final String SERVHTTP_GETCONTACTLIST  = "http://585475.cardexc.web.hosting-test.net/frloc/frloc_getContacstList.php"; //?id=%s";
    public static final String SERVHTTP_USERPHONEEXISTS = "http://585475.cardexc.web.hosting-test.net/frloc/frloc_checkUser.php?&param1=%s&param2=%s";
    public static final String SERVHTTP_USERPHONEADD    = "http://585475.cardexc.web.hosting-test.net/frloc/frloc_addUser.php";
    public static final String SERVHTTP_SETLOCATION     = "http://585475.cardexc.web.hosting-test.net/frloc/frloc_setLocation.php?&id=%s&lat=%s&long=%s";

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

            setPhonenum(line1Number);
            setIMEI(IMEI);

        }

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
}
