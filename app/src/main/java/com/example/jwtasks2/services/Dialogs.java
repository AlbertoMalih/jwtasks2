package com.example.jwtasks2.services;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import com.example.jwtasks2.CreateChangeNoteActivity;
import com.example.jwtasks2.ItemListActivityMain;
import com.example.jwtasks2.R;

import java.util.Calendar;

public class Dialogs {

    public static void showChooseDeleteAllNotes(Activity activityThis, final DbManager dbManager, final OnDeleteTypesListener listenerTypeDelete ) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activityThis);
        builder.setTitle(activityThis.getString(R.string.choose_type_delete));
        builder.setView(View.inflate(activityThis, R.layout.dialog_create_custom_type, null));

        builder.setPositiveButton(R.string.delete_all_notes_str,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                       dbManager.deleteAllNotes();
                        listenerTypeDelete.onDeleteAllTypes();
                    }
                }
        );

        builder.setNeutralButton(R.string.delete_notes_from_writing_group_str, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String nameSimpleGroupForDeleteNotes = ((EditText) (Dialog.class.cast(dialogInterface).findViewById(R.id.input_name_new_custom_type))).getText().toString();
                if (nameSimpleGroupForDeleteNotes.isEmpty()){
                    return;
                }
                Log.d(ItemListActivityMain.TAG, " select new custom type -    " + nameSimpleGroupForDeleteNotes);
                dbManager.deleteAllNotesInSimpleGroup(Utils.getGroupNameForWorkWithDb(nameSimpleGroupForDeleteNotes));
                listenerTypeDelete.onDeleteTypesSimpleGroup(nameSimpleGroupForDeleteNotes);
            }
        });
        builder.create().show();
    }

    public static void showCreateType(CreateChangeNoteActivity activityThis, final OnCreateTypeListener listenerTypeCreate) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activityThis);
        builder.setTitle(activityThis.getString(R.string.type));

        builder.setView(View.inflate(activityThis, R.layout.dialog_create_custom_type, null));
        builder.setPositiveButton(R.string.create_type,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String nameNewCustomType = ((EditText) (Dialog.class.cast(dialogInterface).findViewById(R.id.input_name_new_custom_type))).getText().toString();
                        if (nameNewCustomType.isEmpty()){
                            return;
                        }
                        Log.d(ItemListActivityMain.TAG, " create new custom type -  " + nameNewCustomType);
                        listenerTypeCreate.onTypeCreated(nameNewCustomType);
                    }
                }
        );
        builder.create().show();
    }

    public static void showDatePickerDialog(CreateChangeNoteActivity activityThis, Calendar noteDate) {
        DatePickerFragment dialogFragment = new DatePickerFragment();
        dialogFragment.setDate(noteDate);
        dialogFragment.setBtnDate(activityThis.getBtnDate());
        dialogFragment.show(activityThis.getFragmentManager(), activityThis.getString(R.string.date));
    }

    public static void showTimePickerDialog(CreateChangeNoteActivity activityThis, Calendar noteDate) {
        TimePickerFragment dialogFragment = new TimePickerFragment();
        dialogFragment.setDate(noteDate);
        dialogFragment.setBtnTime(activityThis.getBtnTime());
        dialogFragment.show(activityThis.getFragmentManager(), activityThis.getString(R.string.time));
    }

    public static void showChooseComparator(final Activity activityThis, int selectedId, final OnSelectedComparatorListener onSelectedComparatorListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activityThis);
        builder.setTitle(activityThis.getString(R.string.select_type_sort_str));
        builder.setSingleChoiceItems(R.array.types_comparators, selectedId, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int item) {
                onSelectedComparatorListener.onSelectedComparator(item);
            }
        });
        builder.create().show();
    }

    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
        private Calendar date;
        private Button btnDate;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return new DatePickerDialog(getActivity(), this, date.get(Calendar.YEAR), date.get(Calendar.MONTH),
                    date.get(Calendar.DATE));
        }

        @Override
        public void onDateSet(DatePicker view, int year, int month, int dateDay) {
            date.set(year, month, dateDay);
            Log.d(ItemListActivityMain.TAG, date.getTime() + "  seting time and datte");
            btnDate.setText(Utils.getStringFromDateStart(date.getTime()));
        }

        public void setDate(Calendar date) {
            this.date = date;
        }

        public void setBtnDate(Button btnDate) {
            this.btnDate = btnDate;
        }
    }

    public static class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {
        private Calendar date;
        private Button btnTime;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return new TimePickerDialog(getActivity(), this, date.get(Calendar.HOUR_OF_DAY), date.get(Calendar.MINUTE), true);
        }

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            date.set(Calendar.HOUR_OF_DAY, hourOfDay);
            date.set(Calendar.MINUTE, minute);
            Log.d(ItemListActivityMain.TAG, date.getTime() + "  seting time and datte");
            btnTime.setText(Utils.getStringFromDateEnd(date.getTime()));

        }

        public void setDate(Calendar date) {
            this.date = date;
        }

        public void setBtnTime(Button btnTime) {
            this.btnTime = btnTime;
        }
    }

    public interface OnCreateTypeListener{
        void onTypeCreated(String type);
    }

    public interface OnDeleteTypesListener {
        void onDeleteAllTypes();
        void onDeleteTypesSimpleGroup(String group);
    }

    public interface OnSelectedComparatorListener {
        void onSelectedComparator(int idOfComparator);
    }
}
