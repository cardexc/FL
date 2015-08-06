package cardexc.com.freindlocation.data;

import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import cardexc.com.freindlocation.sqlite.LocationDBHelper;

public class TestDB extends AndroidTestCase{

    public void testCareteDB() throws Throwable {

        LocationDBHelper dbHelper = new LocationDBHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

    }

}
