package com.example.jwtasks2.services;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;

import com.example.jwtasks2.model.NoteDTO;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.example.jwtasks2.services.Constants.ANOTHER_DATA_LIST;

public class DbManager extends SQLiteOpenHelper {
    //todo add transactions anywhere
    private static final String DATABASE_NAME = "NotesDb.db";
    private static final String NOTES_TABLE_NAME = "notes";
    private static final String NOTES_COLUMN_ID = "_id";

    private static final String NOTES_COLUMN_DESCRIPTION = "description";
    private static final String NOTES_COLUMN_DATE = "date";
    private static final String NOTES_COLUMN_TYPE = "type";

    private ArrayList<String> allTypesNotes;
    private String[] defaultTypes;
    private String[] defaultTypesDefLang;


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
//
//    public long numberOfRows() {
//        SQLiteDatabase db = this.getReadableDatabase();
//        long numRows = DatabaseUtils.queryNumEntries(db, NOTES_TABLE_NAME);
//        db.close();
//        return numRows;
//    }

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

    public void installAllNotesInListener(OnGetAllDataListener onGetAllDataListener) {
        RequestAllUsers requestAllUsers = new RequestAllUsers(onGetAllDataListener);
        requestAllUsers.execute();
    }

    private List<List<NoteDTO>> getAllNotesInNowThread() {
        allTypesNotes = new ArrayList<>();
        List<List<NoteDTO>> resultAllData = new ArrayList<>();
        for (int i = 0; i < defaultTypes.length; i++) {
            allTypesNotes.add(defaultTypes[i]);
            resultAllData.add(i, new ArrayList<NoteDTO>());
        }

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor allData = db.rawQuery("select * from " + NOTES_TABLE_NAME, null);
        if (!allData.moveToFirst()) {
            return resultAllData;
        }
        int descriptionIndex = allData.getColumnIndex(NOTES_COLUMN_DESCRIPTION);
        int dateIndex = allData.getColumnIndex(NOTES_COLUMN_DATE);
        int typeIndex = allData.getColumnIndex(NOTES_COLUMN_TYPE);
        int idIndex = allData.getColumnIndex(NOTES_COLUMN_ID);
        do {
            NoteDTO currentNote = new NoteDTO(allData.getString(descriptionIndex), new Date(allData.getLong(dateIndex)), allData.getString(typeIndex), allData.getLong(idIndex));
            String currentType = currentNote.getType();
            boolean isCustomType = true;
            for (int i = 0; i < defaultTypesDefLang.length; i++) {
                if (defaultTypesDefLang[i].equals(currentType)) {
                    isCustomType = false;
                    currentNote.setType(defaultTypes[i]);
                    resultAllData.get(i).add(currentNote);
                    break;
                }
            }
            if (isCustomType) {
                resultAllData.get(ANOTHER_DATA_LIST).add(currentNote);
                if (!allTypesNotes.contains(currentType)) {
                    allTypesNotes.add(currentType);
                }
            }
        } while (allData.moveToNext());
        allData.close();
        return resultAllData;
    }

    public ArrayList<String> getAllTypesNotes() {
        return allTypesNotes;
    }

    public void setDefaultTypes(String[] defaultTypes) {
        this.defaultTypes = defaultTypes;
    }

    public void setDefaultTypesDefLang(String[] defaultTypesDefLang) {
        this.defaultTypesDefLang = defaultTypesDefLang;
    }

    private class RequestAllUsers extends AsyncTask<Void, Void, List<List<NoteDTO>>> {
        private OnGetAllDataListener onGetAllDataListener;

        RequestAllUsers(OnGetAllDataListener onGetAllDataListener) {
            this.onGetAllDataListener = onGetAllDataListener;
        }

        @Override
        protected List<List<NoteDTO>> doInBackground(Void... voids) {
            return DbManager.this.getAllNotesInNowThread();
        }

        @Override
        protected void onPostExecute(List<List<NoteDTO>> lists) {
            super.onPostExecute(lists);
            onGetAllDataListener.onResponseAllData(lists);
        }
    }

    public interface OnGetAllDataListener {
        void onResponseAllData(List<List<NoteDTO>> responseData);
    }

}