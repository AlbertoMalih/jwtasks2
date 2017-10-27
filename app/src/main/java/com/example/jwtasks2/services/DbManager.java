package com.example.jwtasks2.services;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;

import com.example.jwtasks2.model.NoteDTO;

import java.util.Date;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;

public class DbManager extends SQLiteOpenHelper {
    //todo add transactions anywhere
    private static final String DATABASE_NAME = "NotesDb.db";
    private static final String NOTES_TABLE_NAME = "notes";
    private static final String NOTES_COLUMN_ID = "_id";

    private static final String NOTES_COLUMN_DESCRIPTION = "description";
    private static final String NOTES_COLUMN_DATE = "date";
    private static final String NOTES_COLUMN_TYPE = "type";

    public DbManager(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "create table " + NOTES_TABLE_NAME + " " +
                        "(" + NOTES_COLUMN_ID + " integer primary key, " + NOTES_COLUMN_DESCRIPTION +
                        " text," + NOTES_COLUMN_TYPE + " text," + NOTES_COLUMN_DATE + " integer)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS contacts");
        onCreate(db);
    }

    public boolean insertNote(NoteDTO note) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(NOTES_COLUMN_DESCRIPTION, note.getDescription());
        contentValues.put(NOTES_COLUMN_TYPE, Utils.getGroupNameForWorkWithDb(note.getType()));
        contentValues.put(NOTES_COLUMN_DATE, note.getDate().getTime());
        note.setId(db.insert(NOTES_TABLE_NAME, null, contentValues));
        db.close();
        return true;
    }

    public boolean updateNote(NoteDTO note) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(NOTES_COLUMN_DESCRIPTION, note.getDescription());
        contentValues.put(NOTES_COLUMN_TYPE, Utils.getGroupNameForWorkWithDb(note.getType()));
        contentValues.put(NOTES_COLUMN_DATE, note.getDate().getTime());
        note.setId(db.update(NOTES_TABLE_NAME, contentValues, NOTES_COLUMN_ID + " = ?", new String[]{Long.toString(note.getId())}));
        db.close();
        return true;
    }

    public Integer deleteNote(NoteDTO note) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(NOTES_TABLE_NAME,
                NOTES_COLUMN_ID + " = ?",
                new String[]{Long.toString(note.getId())});
        db.close();
        return result;
    }


    int deleteAllNotes() {
        SQLiteDatabase db = this.getWritableDatabase();
        int sizeDeletingElements = db.delete(NOTES_TABLE_NAME, null, null);
        db.close();
        return sizeDeletingElements;
    }

    int deleteAllNotesInSimpleGroup(String group) {
        SQLiteDatabase db = this.getWritableDatabase();
        int sizeDeletingElements = db.delete(NOTES_TABLE_NAME, NOTES_COLUMN_TYPE + " = ?", new String[]{group});
        db.close();
        return sizeDeletingElements;
    }

    public void installAllNotesInListener(Observer<NoteDTO> getterNotes) {
        Observable.create(new ObservableOnSubscribe<NoteDTO>() {
            @Override
            public void subscribe(ObservableEmitter<NoteDTO> e) throws Exception {
                new RequestAllUsers(e).execute();
            }
        }).subscribe(getterNotes);
    }


    private class RequestAllUsers extends AsyncTask<Void, NoteDTO, Void> {
        private ObservableEmitter<NoteDTO> senderNotes;

        RequestAllUsers(ObservableEmitter<NoteDTO> e) {
            this.senderNotes = e;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            SQLiteDatabase db = DbManager.this.getReadableDatabase();
            Cursor allData = db.rawQuery("select * from " + NOTES_TABLE_NAME, null);
            if (!allData.moveToFirst()) {
                return null;
            }
            int descriptionIndex = allData.getColumnIndex(NOTES_COLUMN_DESCRIPTION);
            int dateIndex = allData.getColumnIndex(NOTES_COLUMN_DATE);
            int typeIndex = allData.getColumnIndex(NOTES_COLUMN_TYPE);
            int idIndex = allData.getColumnIndex(NOTES_COLUMN_ID);
            do {
                NoteDTO currentNote = new NoteDTO(allData.getString(descriptionIndex), new Date(allData.getLong(dateIndex)), allData.getString(typeIndex), allData.getLong(idIndex));
                publishProgress(currentNote);
            } while (allData.moveToNext());
            allData.close();
            return null;
        }

        @Override
        protected void onProgressUpdate(NoteDTO... values) {
            super.onProgressUpdate(values);
            senderNotes.onNext(values[0]);
        }

        @Override
        protected void onPostExecute(Void v) {
            super.onPostExecute(v);
            senderNotes.onComplete();
        }
    }
}