package io.praveen.typenote.SQLite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "noteManager";
    private static final String TABLE_NOTES = "notes";
    private static final String KEY_ID = "id";
    private static final String KEY_NOTE = "note";
    private static final String KEY_DATE = "date";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_NOTES + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NOTE + " TEXT," + KEY_DATE + " TEXT);";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTES);
        onCreate(db);
    }

    public Note getNote(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NOTES, new String[] { KEY_ID, KEY_NOTE, KEY_DATE}, KEY_ID + "=?", new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null) cursor.moveToFirst();
        Note note = new Note(Integer.parseInt(cursor.getString(0)), cursor.getString(1), cursor.getString(2));
        return note;
    }

    public List<Note> getAllNotes() {
        List<Note> noteList = new ArrayList<Note>();
        String selectQuery = "SELECT  * FROM " + TABLE_NOTES;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                Note note = new Note();
                note.setID(Integer.parseInt(cursor.getString(0)));
                note.setNote(cursor.getString(1));
                note.setDate(cursor.getString(2));
                noteList.add(note);
            } while (cursor.moveToNext());
        }
        return noteList;
    }

    public int getNotesCount() {
        String countQuery = "SELECT  * FROM " + TABLE_NOTES;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();
        return cursor.getCount();
    }

    public int updateNote(Note note) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NOTE, note.getNote());
        values.put(KEY_DATE, note.getDate());
        return db.update(TABLE_NOTES, values, KEY_ID + " = ?", new String[] { String.valueOf(note.getID()) });
    }

    public void deleteNote(Note note) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NOTES, KEY_ID + " = ?", new String[] { String.valueOf(note.getID()) });
        db.close();
    }

    public void addNote(Note note) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NOTE, note.getNote());
        values.put(KEY_DATE, note.getDate());
        db.insert(TABLE_NOTES, null, values);
        db.close();
    }
}
