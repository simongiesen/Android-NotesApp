package io.praveen.typenote.SQLite;

public class Note {

    private int _id;
    private String _note;
    private String _date;

    public Note() {
    }

    public Note(int id, String note, String date) {
        this._id = id;
        this._note = note;
        this._date = date;
    }

    public Note(String note, String date) {
        this._note = note;
        this._date = date;
    }

    public int getID() {
        return this._id;
    }

    public void setID(int id) {
        this._id = id;
    }

    public String getNote() {
        return this._note;
    }

    public void setNote(String note) {
        this._note = note;
    }

    public String getDate() {
        return this._date;
    }

    public void setDate(String date) {
        this._date = date;
    }

}