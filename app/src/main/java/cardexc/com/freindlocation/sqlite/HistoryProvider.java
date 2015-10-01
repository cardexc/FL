package cardexc.com.freindlocation.sqlite;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cardexc.com.freindlocation.data.Constants;
import cardexc.com.freindlocation.http.Requests;
import cardexc.com.freindlocation.service.events.HistoryListUpdate;
import de.greenrobot.event.EventBus;

public class HistoryProvider {

    private static HistoryProvider mInstance = new HistoryProvider();

    private HistoryProvider() {
    }

    public static HistoryProvider getInstance() {
        return mInstance;
    }

    public Cursor getHistoryCursor() {

        LocationDBHelper dbHelper = new LocationDBHelper(Constants.getApplicationContext());
        return dbHelper.getHistoryCursor();

    }

    public Cursor getHistoryCursor(String phone) {

        LocationDBHelper dbHelper = new LocationDBHelper(Constants.getApplicationContext());
        SQLiteDatabase readableDatabase = dbHelper.getReadableDatabase();

        Cursor cursor = readableDatabase.rawQuery("Select * FROM " + LocationContract.HistoryEntry.TABLE_NAME
                + " WHERE " + LocationContract.HistoryEntry.COLUMN_PHONE + " = ?"
                , new String[]{phone});

        return cursor;

    }


    public void insertRecordHistoryTab(String phone, String uuid) {

        LocationDBHelper dbHelper = new LocationDBHelper(Constants.getApplicationContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(LocationContract.HistoryEntry.COLUMN_REQ_TYPE, LocationContract.HistoryEntry.REQ_TYPE_OUT);
        values.put(LocationContract.HistoryEntry.COLUMN_PHONE, phone);
        values.put(LocationContract.HistoryEntry.COLUMN_UUID, uuid);
        values.put(LocationContract.HistoryEntry.COLUMN_REQUEST_TIME, System.currentTimeMillis());

        db.insert(LocationContract.HistoryEntry.TABLE_NAME, null, values);

        db.close();
    }

    public void updateLocationByRequestedUUID(JSONObject response) {

        LocationDBHelper dbHelper = new LocationDBHelper(Constants.getApplicationContext());
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
        db.close();

        EventBus.getDefault().post(new HistoryListUpdate());
    }

    public void historyRequests_Analyze(JSONObject response) {

        try {

            int count = response.getInt("count");

            List<HistoryEntity> records = new ArrayList<HistoryEntity>();

            for (int i = 0; i < count; i++) {

                JSONObject record = response.getJSONObject(String.valueOf(i));

                HistoryEntity history_record = new HistoryEntity();

                history_record.uuid = record.getString("uuid");
                history_record.requested_time = record.getString("requested_time");
                history_record.phone = record.getString("phone");

                records.add(history_record);

            }

            if (records.size() > 0) {
                historyRequests_Proceed(records);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void historyRequests_Proceed(List<HistoryEntity> records) {

        LocationDBHelper locationDBHelper = new LocationDBHelper(Constants.getApplicationContext());
        SQLiteDatabase wdb = locationDBHelper.getWritableDatabase();

        for (HistoryEntity record : records) {

            Cursor cursor = wdb.rawQuery("Select uuid from " + LocationContract.HistoryEntry.TABLE_NAME + " WHERE uuid = ?", new String[]{record.uuid});
            if (cursor.moveToNext()) {
                continue;
            }

            ContentValues cv = new ContentValues();
            cv.put(LocationContract.HistoryEntry.COLUMN_REQ_TYPE, LocationContract.HistoryEntry.REQ_TYPE_IN);

            cv.put(LocationContract.HistoryEntry.COLUMN_UUID, record.uuid);
            cv.put(LocationContract.HistoryEntry.COLUMN_REQUEST_TIME, record.requested_time);
            cv.put(LocationContract.HistoryEntry.COLUMN_PHONE, record.phone);

            wdb.insert(LocationContract.HistoryEntry.TABLE_NAME, null, cv);

        }

        wdb.close();

        for (HistoryEntity recorD : records) {
            Requests.historyRequestsReceived_updateByUUID(recorD.uuid);
        }

        EventBus.getDefault().post(new HistoryListUpdate());


    }

    public class HistoryEntity {

        String phone;
        String uuid;
        String requested_time;

    }

}
