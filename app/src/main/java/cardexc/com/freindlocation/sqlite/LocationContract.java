package cardexc.com.freindlocation.sqlite;

import android.provider.BaseColumns;

public class LocationContract {

    public static final class HistoryEntry implements BaseColumns {

        public static final String TABLE_NAME = "history";

        public static final String COLUMN_REQUEST_TIME = "request_time";
        public static final String COLUMN_PHONE = "phone";
        public static final String COLUMN_UUID  = "uuid";
        public static final String COLUMN_LATITUDE = "latitude";
        public static final String COLUMN_LONGITUDE = "longitude";

    }
}
