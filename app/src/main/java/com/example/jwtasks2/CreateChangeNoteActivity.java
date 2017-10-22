package com.example.jwtasks2;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSpinner;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;

import com.example.jwtasks2.model.NoteDTO;
import com.example.jwtasks2.services.DbManager;
import com.example.jwtasks2.services.Dialogs;
import com.example.jwtasks2.services.Utils;

import java.util.ArrayList;
import java.util.Calendar;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

import static com.example.jwtasks2.services.Constants.CURRENT_NOTE_KEY;

public class CreateChangeNoteActivity extends BaseActivity implements Dialogs.OnCreateTypeListener {
    private Calendar calendarDateCurrentNote = Calendar.getInstance();
    private NoteDTO currentNote;
    private Intent intent;
    private ArrayList<String> allTypesNotes;
    private ArrayAdapter adapterShowingCustomTypes;
    @BindView(R.id.types_note)
    AppCompatSpinner types_note;
    @BindView(R.id.btn_date)
    Button btn_date;
    @BindView(R.id.btn_time)
    Button btn_time;
    @BindView(R.id.output_description_activity_create)
    EditText output_description_activity_create;
    @Inject
    DbManager dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        intent = getIntent();
        init();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_create_note;
    }

    @Override
    public void injectDependencies() {
        getActivityComponent().inject(this);
    }

    private void init() {
        currentNote = getIntent().getParcelableExtra(CURRENT_NOTE_KEY);
        allTypesNotes = dbManager.getAllTypesNotes();
        calendarDateCurrentNote.setTime(currentNote.getDate());
        btn_date.setText(Utils.getStringFromDateStart(currentNote.getDate()));
        btn_time.setText(Utils.getStringFromDateEnd(currentNote.getDate()));

        output_description_activity_create.setText(currentNote.getDescription());
        adapterShowingCustomTypes = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, allTypesNotes);
        types_note.setAdapter(adapterShowingCustomTypes);
        types_note.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                currentNote.setType(allTypesNotes.get(i));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        types_note.setSelection(allTypesNotes.indexOf(currentNote.getType()));
    }

    @OnClick({R.id.btn_create, R.id.btn_date, R.id.btn_time, R.id.btn_create_type})
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
        currentNote.setDescription(output_description_activity_create.getText().toString());
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
        return btn_date;
    }

    public Button getBtnTime() {
        return btn_time;
    }

    @Override
    public void onTypeCreated(String type) {
        currentNote.setType(type);
        allTypesNotes.add(type);
        adapterShowingCustomTypes.notifyDataSetChanged();
        types_note.setSelection(allTypesNotes.indexOf(type));
    }
}