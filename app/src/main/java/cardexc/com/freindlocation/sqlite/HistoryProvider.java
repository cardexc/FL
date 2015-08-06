package cardexc.com.freindlocation.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class HistoryProvider {

    private static HistoryProvider mInstance = new HistoryProvider();

    private HistoryProvider() {
    }

    public static HistoryProvider getInstance() {
        return mInstance;
    }

    public Cursor getHistoryCursor(Context context) {

        LocationDBHelper dbHelper = new LocationDBHelper(context);
        return dbHelper.getHistoryCursor();

    }

    public void insertRecordHistoryTab(Context context, String phone, String IMEI, String uuid){

        LocationDBHelper dbHelper = new LocationDBHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd/MM");
        String requestTime = sdf.format(calendar.getTime());

        ContentValues values = new ContentValues();
        values.put(LocationContract.HistoryEntry.COLUMN_PHONE, phone);
        values.put(LocationContract.HistoryEntry.COLUMN_UUID,  uuid);
        values.put(LocationContract.HistoryEntry.COLUMN_REQUEST_TIME,  requestTime);

        db.insert(LocationContract.HistoryEntry.TABLE_NAME, null, values);

    }

    public void updateLocationByRequestUUID(Context context, JSONObject response)  {

        LocationDBHelper dbHelper = new LocationDBHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String uuid = "";
        ContentValues contentValues = new ContentValues();

        try {
            uuid = response.getString("uuid");
            contentValues.put(LocationContract.HistoryEntry.COLUMN_LATITUDE, response.getString("latitude"));
            contentValues.put(LocationContract.HistoryEntry.COLUMN_LONGITUDE, response.getString("longitude"));
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
        }

        db.update(LocationContract.HistoryEntry.TABLE_NAME, contentValues, "UUID = ?", new String[]{uuid});

    }


}
