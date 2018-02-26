package io.praveen.typenote.SQLite;

/**
 * Created by srikarn on 26-02-2018.
 */
import android.provider.BaseColumns;

public class DatabaseContract {

    public static final class DatabaseEntry implements BaseColumns{
        public static final String TABLE_NOTES = "notes";
        public static final String KEY_ID = "id";
        public static final String KEY_NOTE = "note";
        public static final String KEY_DATE = "date";
    }

}
