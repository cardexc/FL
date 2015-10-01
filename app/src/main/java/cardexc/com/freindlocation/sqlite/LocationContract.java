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
        public static final String COLUMN_REQ_TYPE  = "request_type";

        public static final String REQ_TYPE_IN  = "in";
        public static final String REQ_TYPE_OUT  = "out";

    }

    public static final class ContactEntry implements BaseColumns {

        public static final String TABLE_NAME = "Contacts";

        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_PHONE = "phone";
        public static final String COLUMN_APPROVED = "approved";
        public static final String COLUMN_CONTACTID = "contact_id";

    }

    public static final class ContactsToUpdateEntry implements BaseColumns {

        public static final String TABLE_NAME = "ContactsToUpdateOnServer";

        public static final String COLUMN_PHONE = "phone";

    }

    public static final class ContactsToDeleteEntry implements BaseColumns {

        public static final String TABLE_NAME = "ContactsToDeleteOnServer";

        public static final String COLUMN_PHONE = "phone";

    }



}
