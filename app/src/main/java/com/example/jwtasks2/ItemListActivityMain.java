package com.example.jwtasks2;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.jwtasks2.model.NoteDTO;
import com.example.jwtasks2.services.DbManager;
import com.example.jwtasks2.services.Dialogs;
import com.example.jwtasks2.services.Utils;

import java.util.ArrayList;
import java.util.List;

import static com.example.jwtasks2.CreateChangeNoteActivity.CREATE_NOTE_CODE;
import static com.example.jwtasks2.CreateChangeNoteActivity.UPDATE_NOTE_CODE;

public class ItemListActivityMain extends AppCompatActivity implements DbManager.OnGetAllDataListener, Dialogs.OnDeleteTypesListener {
    //todo add firebase database and auth
    public static final String SHOWING_ELEMENT = "SHOWING_ELEMENT";
    public static final String TAG = "JWTASKS_2_TAG";
    public static final String SENT_ALL_ANOTHER_TYPES_KEY = "SENT_ALL_ANOTHER_TYPES_KEY";
    public static final int ANOTHER_DATA_LIST = 0;
    public static final int REQUEST_CODE_CREATE_CHANGE_NOTE = 42;
    public static final String CURRENT_NOTE_KEY = "CURRENT_NOTE_KEY";
    public static final String POSITION_UPDATING_TASK = "POSITION_UPDATING_TASK";
    public static final String CODE_WORK_WITH_NOTE = "function_code";

    private static String[] defaultTypes;
    private static String[] defaultTypesDefLang;

    private boolean currentMachineIsTablet;
    private ArrayList<String> allTypesNotes;
    private SimpleItemRecyclerViewAdapter notesAdapter;
    private List<List<NoteDTO>> allGroupsNotes;
    private List<NoteDTO> currentShowingNotes;
    private DbManager dbManager;
    private TabLayout tabLayout;
    private RecyclerView showerCurrentNotes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);
        initNameTypes();
        dbManager = new DbManager(this.getApplicationContext());
        showerCurrentNotes = (RecyclerView) findViewById(R.id.item_list);
        if (findViewById(R.id.item_detail_container) != null) {
            currentMachineIsTablet = true;
        }
        initToolbar();
        initAllGroupsNotes();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_all_main_menu:
                Dialogs.showChooseDeleteAllNotes(this, dbManager, this);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initNameTypes() {
        Resources resources = getResources();
        defaultTypes = resources.getStringArray(R.array.default_types);
        defaultTypesDefLang = resources.getStringArray(R.array.default_types_def_lang);
    }

    private void initAllGroupsNotes() {
        //with static allGroupsNotes
//        if (allGroupsNotes != null) {
//            onResponseAllData(allGroupsNotes);
//            return;
//        }
        dbManager.installAllNotesInListener(this);
    }

    private void initToolbar() {
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        tabLayout = (TabLayout) findViewById(R.id.tabs);

        for (int i = 0; i < defaultTypes.length; i++) {
            tabLayout.addTab(tabLayout.newTab().setText(defaultTypes[i]));
        }
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return allGroupsNotes;
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView, List<NoteDTO> data) {
        recyclerView.setAdapter(notesAdapter = new SimpleItemRecyclerViewAdapter(currentShowingNotes = data));
    }

    @Override
    public void onResponseAllData(List<List<NoteDTO>> responseData) {
        allGroupsNotes = responseData;
        allTypesNotes = dbManager.getAllAnotherTypesNotes();
        setupRecyclerView(showerCurrentNotes, allGroupsNotes.get(ANOTHER_DATA_LIST));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                notesAdapter.setValues(currentShowingNotes = allGroupsNotes.get(tab.getPosition()));
                if (currentShowingNotes.size() > 0) {
                    if (currentMachineIsTablet) {
                        Bundle arguments = new Bundle();
                        arguments.putParcelable(SHOWING_ELEMENT, currentShowingNotes.get(0));
                        ItemDetailFragment fragment = new ItemDetailFragment();
                        fragment.setArguments(arguments);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.item_detail_container, fragment)
                                .commit();
                    }
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
        findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NoteDTO sendNote = new NoteDTO();
                sendNote.setType(defaultTypes[allGroupsNotes.indexOf(currentShowingNotes)]);
                startActivityForResult(new Intent(ItemListActivityMain.this, CreateChangeNoteActivity.class)
                        .putExtra(SENT_ALL_ANOTHER_TYPES_KEY, allTypesNotes)
                        .putExtra(CODE_WORK_WITH_NOTE, CREATE_NOTE_CODE)
                        .putExtra(CURRENT_NOTE_KEY, sendNote), REQUEST_CODE_CREATE_CHANGE_NOTE);
            }
        });
    }

    private void deletingNoteOnPositionInCurrentListNotes(int positionDeletingNote) {
        dbManager.deleteNote(currentShowingNotes.get(positionDeletingNote));
        currentShowingNotes.remove(positionDeletingNote);
        notesAdapter.notifyDataSetChanged();
    }

    private void insertNote(NoteDTO note) {
        dbManager.insertNote(note);
        allGroupsNotes.get(Utils.getListIdOfTypeNote(note)).add(note);
        notesAdapter.notifyDataSetChanged();
    }

    private void updateNote(NoteDTO updatingNote, int position) {
        List<NoteDTO> updatingNoteGroupList = allGroupsNotes.get(Utils.getListIdOfTypeNote(updatingNote));
        if (currentShowingNotes == updatingNoteGroupList) {
            currentShowingNotes.set(position, updatingNote);
        } else {
            updatingNoteGroupList.add(updatingNote);
            currentShowingNotes.remove(position);
        }
        dbManager.updateNote(updatingNote);
        notesAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CREATE_CHANGE_NOTE) {
            if (resultCode == RESULT_OK) {
                switch (data.getExtras().getInt(CODE_WORK_WITH_NOTE)) {
                    case CREATE_NOTE_CODE:
                        insertNote((NoteDTO) data.getExtras().getParcelable(CURRENT_NOTE_KEY));
                        break;
                    case UPDATE_NOTE_CODE:
                        updateNote((NoteDTO) data.getParcelableExtra(CURRENT_NOTE_KEY), data.getIntExtra(POSITION_UPDATING_TASK, -1));
                        break;
                }
                notesAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbManager != null) {
            dbManager.close();
        } else {
            dbManager = null;
        }
    }


    public static String[] getDefaultTypes() {
        return defaultTypes;
    }

    public static String[] getDefaultTypesDefLang() {
        return defaultTypesDefLang;
    }

    @Override
    public void onDeleteAllTypes() {
        for (List<NoteDTO> simpleGroupNotes : allGroupsNotes) {
            simpleGroupNotes.clear();
        }
        notesAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDeleteTypesSimpleGroup(String group) {
        //todo
        boolean isDefaultType = false;
        for (int i = 0; i < defaultTypes.length; i++) {
            if (defaultTypes[i].equals(group)) {
                isDefaultType = true;
                allGroupsNotes.get(i).clear();
            }
        }

        if (!isDefaultType) {
            List<NoteDTO> anotherGroups = allGroupsNotes.get(ANOTHER_DATA_LIST);
            for (int i = 0; i < anotherGroups.size(); i++) {
                if (group.equals(anotherGroups.get(i).getType())) {
                    anotherGroups.remove(i);
                    i--;
                }
            }
        }
        notesAdapter.notifyDataSetChanged();
    }


    class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private List<NoteDTO> noteValues;

        public SimpleItemRecyclerViewAdapter(List<NoteDTO> items) {
            noteValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(
                    LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.item_list_content, parent, false)
            );
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            holder.noteDate.setText(Utils.getStringFromDateAll(noteValues.get(position).getDate()));
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    PopupMenu popup = new PopupMenu(ItemListActivityMain.this, holder.itemView);
                    popup.inflate(R.menu.actions_with_note_menu);
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.menu_item_delete_note:
                                    deletingNoteOnPositionInCurrentListNotes(position);
                                    break;
                                case R.id.menu_item_edit_note:
                                    startActivityForResult(new Intent(ItemListActivityMain.this, CreateChangeNoteActivity.class)
                                                    .putExtra(SENT_ALL_ANOTHER_TYPES_KEY, allTypesNotes)
                                                    .putExtra(CODE_WORK_WITH_NOTE, UPDATE_NOTE_CODE)
                                                    .putExtra(CURRENT_NOTE_KEY, currentShowingNotes.get(position))
                                                    .putExtra(POSITION_UPDATING_TASK, position),
                                            REQUEST_CODE_CREATE_CHANGE_NOTE);
                                    break;
                            }
                            return false;
                        }
                    });
                    popup.show();
                    return true;
                }
            });
            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (currentMachineIsTablet) {
                        Bundle arguments = new Bundle();
                        arguments.putParcelable(SHOWING_ELEMENT, noteValues.get(position));
                        ItemDetailFragment fragment = new ItemDetailFragment();
                        fragment.setArguments(arguments);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.item_detail_container, fragment)
                                .commit();
                    } else {
                        Context context = v.getContext();
                        context.startActivity(
                                new Intent(context, ItemDetailActivity.class).putExtra(SHOWING_ELEMENT, noteValues.get(position))
                        );
                    }
                }
            });
        }

        public void setValues(List<NoteDTO> mValues) {
            this.noteValues = mValues;
            notifyDataSetChanged();
        }

        @Override
        public int getItemCount() {
            return noteValues.size();
        }


        class ViewHolder extends RecyclerView.ViewHolder {
            private View mView;
            private TextView noteDate;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                noteDate = view.findViewById(R.id.content);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + noteDate.getText() + "'";
            }
        }
    }
}