package cardexc.com.freindlocation.sqlite;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import cardexc.com.freindlocation.fragments.History;
import cardexc.com.freindlocation.sqlite.LocationContract.HistoryEntry;

public class LocationDBHelper extends SQLiteOpenHelper{

    private static final int DATABASE_VERSION = 3;
    static final String DATABASE_NAME = "history.db";

    public LocationDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_HISTORY_TABLE = "CREATE TABLE " + HistoryEntry.TABLE_NAME + " (" +
                HistoryEntry._ID + " INTEGER PRIMARY KEY," +
                HistoryEntry.COLUMN_PHONE + " TEXT NOT NULL, " +
                HistoryEntry.COLUMN_UUID  + " TEXT NOT NULL, " +
                HistoryEntry.COLUMN_LATITUDE + " DOUBLE, " +
                HistoryEntry.COLUMN_LONGITUDE + " DOUBLE, " +
                HistoryEntry.COLUMN_REQUEST_TIME + " TEXT NOT NULL " +
                " );";

        sqLiteDatabase.execSQL(SQL_CREATE_HISTORY_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        final String SQL_DROP_TABLEHISTORY = "DROP TABLE IF EXISTS " + HistoryEntry.TABLE_NAME;
        db.execSQL(SQL_DROP_TABLEHISTORY);

        final String SQL_CREATE_HISTORY_TABLE = "CREATE TABLE " + HistoryEntry.TABLE_NAME + " (" +
                HistoryEntry._ID + " INTEGER PRIMARY KEY," +
                HistoryEntry.COLUMN_PHONE + " TEXT NOT NULL, " +
                HistoryEntry.COLUMN_UUID  + " TEXT NOT NULL, " +
                HistoryEntry.COLUMN_LATITUDE + " DOUBLE, " +
                HistoryEntry.COLUMN_LONGITUDE + " DOUBLE, " +
                HistoryEntry.COLUMN_REQUEST_TIME + " TEXT NOT NULL " +
                " );";

        db.execSQL(SQL_CREATE_HISTORY_TABLE);

    }

    /////////////////////////////////////////////////////////////////

    public Cursor getHistoryCursor(){

        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM  "
                + HistoryEntry.TABLE_NAME
                + " ORDER BY " + HistoryEntry._ID + " DESC", null);

        return cursor;
    }
}
