package cardexc.com.freindlocation.sqlite;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import cardexc.com.freindlocation.data.Constants;
import cardexc.com.freindlocation.data.Contact;
import cardexc.com.freindlocation.fragments.History;
import cardexc.com.freindlocation.sqlite.LocationContract.*;

public class MapProvider {

    public static Cursor getGenerallMapCursor(Context context, String forPhone) {

        LocationDBHelper locationDBHelper = new LocationDBHelper(context);
        SQLiteDatabase wdb = locationDBHelper.getWritableDatabase();

        String phoneCondition = forPhone == null ? "" : " AND " + HistoryEntry.COLUMN_PHONE + " = '" + forPhone + "'";

        String query_str = "SELECT " +
                "h2." + HistoryEntry.COLUMN_LATITUDE + " as "  + HistoryEntry.COLUMN_LATITUDE  +", " +
                "h2." + HistoryEntry.COLUMN_LONGITUDE + " as " + HistoryEntry.COLUMN_LONGITUDE + ", " +
                "h2." + HistoryEntry.COLUMN_REQUEST_TIME + " as " + HistoryEntry.COLUMN_REQUEST_TIME + ", " +
                "c." + ContactEntry.COLUMN_CONTACTID + " as "  + ContactEntry.COLUMN_CONTACTID + ", " +
                "c." + ContactEntry.COLUMN_NAME + " as " + ContactEntry.COLUMN_NAME  + " " +
                "FROM (" +

                "SELECT" +
                " " + HistoryEntry.COLUMN_PHONE + " as phone, " +
                "MAX(" + HistoryEntry.COLUMN_REQUEST_TIME + ") as r_time " +
                "FROM " + HistoryEntry.TABLE_NAME + " " +

                "   WHERE NOT (" + HistoryEntry.COLUMN_LONGITUDE + " is null " +
                "   or " + HistoryEntry.COLUMN_LONGITUDE + " = 'null' " +
                "   or " + HistoryEntry.COLUMN_LATITUDE + " is null " +
                "   or " + HistoryEntry.COLUMN_LATITUDE + " = 'null' ) " +
                "   AND " + HistoryEntry.COLUMN_REQ_TYPE + " = '" + HistoryEntry.REQ_TYPE_OUT + "' " +

                phoneCondition +

                "GROUP BY " + HistoryEntry.COLUMN_PHONE + ")" +
                "as h1 " +

                "LEFT JOIN " + HistoryEntry.TABLE_NAME + " as h2 ON " +
                "h1." + HistoryEntry.COLUMN_PHONE + " = h2." + HistoryEntry.COLUMN_PHONE + " AND " +
                "h1.r_time = h2." + HistoryEntry.COLUMN_REQUEST_TIME + " " +

                "LEFT JOIN " + ContactEntry.TABLE_NAME + " as c " +
                "on c." + ContactEntry.COLUMN_PHONE + " = h1." + HistoryEntry.COLUMN_PHONE;

        Cursor cursor = wdb.rawQuery(query_str, null);

        return cursor;

    }


}
