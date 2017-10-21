package com.example.jwtasks2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSpinner;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;

import com.example.jwtasks2.model.NoteDTO;
import com.example.jwtasks2.services.Dialogs;
import com.example.jwtasks2.services.Utils;

import java.util.ArrayList;
import java.util.Calendar;

public class CreateChangeNoteActivity extends AppCompatActivity implements Dialogs.OnCreateTypeListener {
    public static final int CREATE_NOTE_CODE = 21;
    public static final int UPDATE_NOTE_CODE = 22;

    private Calendar calendarDateCurrentNote = Calendar.getInstance();
    private NoteDTO currentNote;
    private Intent intent;
    private Button btnDate;
    private Button btnTime;
    private ArrayList<String> allTypesAnotherNotes;
    private ArrayAdapter adapterShowingCustomTypes;
    private AppCompatSpinner showerCustomTypes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_note);
        intent = getIntent();
        init();
    }

    private void init() {
        currentNote = getIntent().getParcelableExtra(ItemListActivityMain.CURRENT_NOTE_KEY);
        calendarDateCurrentNote.setTime(currentNote.getDate());
        btnDate = (Button) findViewById(R.id.btn_date);
        btnDate.setText(Utils.getStringFromDateStart(currentNote.getDate()));
        btnTime = (Button) findViewById(R.id.btn_time);
        btnTime.setText(Utils.getStringFromDateEnd(currentNote.getDate()));

        ((EditText) findViewById(R.id.output_description_activity_create)).setText(currentNote.getDescription());
        allTypesAnotherNotes = ItemListActivityMain.getAllTypesNotes();
        showerCustomTypes = (AppCompatSpinner) findViewById(R.id.types_note);
        adapterShowingCustomTypes = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, allTypesAnotherNotes);
        showerCustomTypes.setAdapter(adapterShowingCustomTypes);
        showerCustomTypes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                currentNote.setType(allTypesAnotherNotes.get(i));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });
            showerCustomTypes.setSelection(allTypesAnotherNotes.indexOf(currentNote.getType()));
    }

    public void onClickForTaskButtons(View view) {
        switch (view.getId()) {
            case R.id.btn_create:
                writeTask();
                break;
            case R.id.btn_date:
                Dialogs.showDatePickerDialog(this, calendarDateCurrentNote);
                break;
            case R.id.btn_time:
                Dialogs.showTimePickerDialog(this, calendarDateCurrentNote);
                break;
            case R.id.btn_create_type:
                Dialogs.showCreateType(this, this);
                break;
        }
    }

    private void writeTask() {
        currentNote.setDescription(((EditText) findViewById(R.id.output_description_activity_create)).getText().toString());
        currentNote.setDate(calendarDateCurrentNote.getTime());
        finish(RESULT_OK);
    }
    public void finish(int resultOperation) {
        setResult(resultOperation, new Intent(this.intent));
        super.finish();
    }

    @Override
    public void finish() {
        finish(RESULT_CANCELED);
    }

    public Button getBtnDate() {
        return btnDate;
    }

    public Button getBtnTime() {
        return btnTime;
    }

    @Override
    public void onTypeCreated(String type) {
        currentNote.setType(type);
        allTypesAnotherNotes.add(type);
        adapterShowingCustomTypes.notifyDataSetChanged();
        showerCustomTypes.setSelection(allTypesAnotherNotes.indexOf(type));
    }
}