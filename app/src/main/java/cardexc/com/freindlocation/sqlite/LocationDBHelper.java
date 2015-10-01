package cardexc.com.freindlocation.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;

import cardexc.com.freindlocation.sqlite.LocationContract.*;

public class LocationDBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 56;
    static final String DATABASE_NAME = "history.db";

    public LocationDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        sqLiteDatabase.execSQL(getStringCreation_HistoryTable());
        sqLiteDatabase.execSQL(getStringCreation_ContactTable());
        sqLiteDatabase.execSQL(getStringCreation_ContactsToUpdate());
        sqLiteDatabase.execSQL(getStringCreation_ContactsToDelete());

    }

    @NonNull
    private String getStringCreation_ContactTable() {
        return "CREATE TABLE " + ContactEntry.TABLE_NAME + " (" +
                ContactEntry._ID + " INTEGER PRIMARY KEY," +
                ContactEntry.COLUMN_PHONE + " TEXT NOT NULL, " +
                ContactEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                ContactEntry.COLUMN_APPROVED + " BOOLEAN, " +
                ContactEntry.COLUMN_CONTACTID + " TEXT " +
                " );";
    }

    @NonNull
    private String getStringCreation_HistoryTable() {
        return "CREATE TABLE " + HistoryEntry.TABLE_NAME + " (" +
                HistoryEntry._ID + " INTEGER PRIMARY KEY," +
                HistoryEntry.COLUMN_PHONE + " TEXT NOT NULL, " +
                HistoryEntry.COLUMN_UUID + " TEXT NOT NULL, " +
                HistoryEntry.COLUMN_LATITUDE + " DOUBLE, " +
                HistoryEntry.COLUMN_LONGITUDE + " DOUBLE, " +
                HistoryEntry.COLUMN_REQ_TYPE + " TEXT, " +
                HistoryEntry.COLUMN_REQUEST_TIME + " INT " +
                " );";
    }

    @NonNull
    private String getStringCreation_ContactsToUpdate() {
        return "CREATE TABLE " + ContactsToUpdateEntry.TABLE_NAME + " (" +
                ContactsToUpdateEntry._ID + " INTEGER PRIMARY KEY," +
                ContactsToUpdateEntry.COLUMN_PHONE + " TEXT NOT NULL" +
                " );";
    }

    @NonNull
    private String getStringCreation_ContactsToDelete() {
        return "CREATE TABLE " + ContactsToDeleteEntry.TABLE_NAME + " (" +
                ContactsToDeleteEntry._ID + " INTEGER PRIMARY KEY," +
                ContactsToDeleteEntry.COLUMN_PHONE + " TEXT NOT NULL" +
                " );";
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        final String SQL_DROP_TABLEHISTORY = "DROP TABLE IF EXISTS " + HistoryEntry.TABLE_NAME;
        final String SQL_DROP_TABLECONTACTS = "DROP TABLE IF EXISTS " + ContactEntry.TABLE_NAME;
        final String SQL_DROP_CONTACTTOUPDATE = "DROP TABLE IF EXISTS " + ContactsToUpdateEntry.TABLE_NAME;
        final String SQL_DROP_CONTACTTODELETE = "DROP TABLE IF EXISTS " + ContactsToDeleteEntry.TABLE_NAME;

        db.execSQL(SQL_DROP_TABLEHISTORY);
        db.execSQL(SQL_DROP_TABLECONTACTS);
        db.execSQL(SQL_DROP_CONTACTTOUPDATE);
        db.execSQL(SQL_DROP_CONTACTTODELETE);

        db.execSQL(getStringCreation_HistoryTable());
        db.execSQL(getStringCreation_ContactTable());
        db.execSQL(getStringCreation_ContactsToUpdate());
        db.execSQL(getStringCreation_ContactsToDelete());

    }

    /////////////////////////////////////////////////////////////////

    public Cursor getHistoryCursor() {

        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT *, " +
                " contactTable." + ContactEntry.COLUMN_NAME + " as contactName, " +
                " contactTable." + ContactEntry.COLUMN_CONTACTID + " as contactID " +
                " FROM  " + HistoryEntry.TABLE_NAME + " as historyTable " +
                " LEFT JOIN " + ContactEntry.TABLE_NAME + " as contactTable " +
                " on  historyTable." + HistoryEntry.COLUMN_PHONE + " = contactTable." + ContactEntry.COLUMN_PHONE +
                " ORDER BY " + HistoryEntry.COLUMN_REQUEST_TIME + " DESC", null);

        return cursor;
    }

    public Cursor getContactCursor() {

        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM  "
                + ContactEntry.TABLE_NAME
                + " ORDER BY " + HistoryEntry._ID + " DESC", null);

        return cursor;
    }

    public Cursor getContactsToUpdateEntryCursor() {

        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + ContactsToUpdateEntry.COLUMN_PHONE + " FROM  "
                + ContactsToUpdateEntry.TABLE_NAME
                , null);

        return cursor;
    }

    public Cursor getContactsToDeleteCursor() {

        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + ContactsToDeleteEntry.COLUMN_PHONE + " FROM  "
                + ContactsToDeleteEntry.TABLE_NAME
                , null);

        return cursor;
    }


    /////////////////////////////////////////////////////////////////


}
