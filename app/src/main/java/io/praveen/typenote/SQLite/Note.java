package io.praveen.typenote.SQLite;

public class Note {

    private int _id;
    private String _note;
    private String _date;
    private int _star;

    Note() {}

    public Note(int id, String note, String date, int star) {
        this._id = id;
        this._note = note;
        this._date = date;
        this._star = star;
    }

    public Note(String note, String date, int star) {
        this._note = note;
        this._date = date;
        this._star = star;
    }

    public int getID() {
        return this._id;
    }

    void setID(int id) {
        this._id = id;
    }

    public String getNote() {
        return this._note;
    }

    void setNote(String note) {
        this._note = note;
    }

    public String getDate() {
        return this._date;
    }

    void setDate(String date) {
        this._date = date;
    }

    public int getStar() {
        return this._star;
    }

    public void setStar(int _star) {
        this._star = _star;
    }
}