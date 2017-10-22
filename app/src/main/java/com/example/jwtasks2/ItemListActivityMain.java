package com.example.jwtasks2;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
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
import com.example.jwtasks2.services.Comparators;
import com.example.jwtasks2.services.DbManager;
import com.example.jwtasks2.services.Dialogs;
import com.example.jwtasks2.services.ResourcesAndSettings;
import com.example.jwtasks2.services.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.inject.Inject;
import butterknife.BindView;
import static com.example.jwtasks2.services.Constants.*;

public class ItemListActivityMain extends BaseActivity implements Dialogs.OnSelectedComparatorListener, DbManager.OnGetAllDataListener, Dialogs.OnDeleteTypesListener {
    //todo add firebase database and auth
    private String[] defaultTypes;
    private boolean currentMachineIsTablet;
    private ArrayList<String> allTypesNotes;
    private SimpleItemRecyclerViewAdapter notesAdapter;
    private List<List<NoteDTO>> allGroupsNotes;
    private List<NoteDTO> currentShowingNotes;
    private int positionCurrentShowingNotes;
    @Inject
    DbManager dbManager;
    @Inject
    ResourcesAndSettings resourcesAndSettings;
    @BindView(R.id.tabs)
    TabLayout tabLayout;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.item_list)
    RecyclerView item_list;
    private Comparator<NoteDTO> comparatorForSortNotes;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initDefTypes();
        comparatorForSortNotes = resourcesAndSettings.readComparator();
        if (findViewById((R.id.item_detail_container)) != null) {
            currentMachineIsTablet = true;
        }
        initToolbar();
        initAllGroupsNotes();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_item_list;
    }

    @Override
    public void injectDependencies() {
        getActivityComponent().inject(this);
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
            case R.id.select_comparator_main_menu:
                Dialogs.showChooseComparator(this, Comparators.getIdOnComparator(comparatorForSortNotes), this);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
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


    @Override
    public void onDeleteAllTypes() {
        for (List<NoteDTO> simpleGroupNotes : allGroupsNotes) {
            simpleGroupNotes.clear();
        }
        notesAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDeleteTypesSimpleGroup(String group) {
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

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return allGroupsNotes;
    }

    private void initAllGroupsNotes() {
        //todo with static allGroupsNotes
//        if (allGroupsNotes != null) {
//            onResponseAllData(allGroupsNotes);
//            return;
//        }
        dbManager.installAllNotesInListener(this);
    }

    private void initToolbar() {
        setSupportActionBar(toolbar);

        for (String defaultType : defaultTypes) {
            tabLayout.addTab(tabLayout.newTab().setText(defaultType));
        }
    }

    private void initDefTypes() {
        dbManager.setDefaultTypes(defaultTypes = resourcesAndSettings.getDefaultTypes());
        String[] defaultTypesDefLang = resourcesAndSettings.getDefaultTypesDefLang();
        dbManager.setDefaultTypesDefLang(defaultTypesDefLang);
        Utils.setDefaultTypesDefLang(defaultTypesDefLang);
        Utils.setDefaultTypes(defaultTypes);
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView, List<NoteDTO> data) {
        recyclerView.setAdapter(notesAdapter = new SimpleItemRecyclerViewAdapter(currentShowingNotes = data));
    }

    @Override
    public void onResponseAllData(List<List<NoteDTO>> responseData) {
        allGroupsNotes = responseData;
        allTypesNotes = dbManager.getAllTypesNotes();
        sortAllGroupsNotes();
        setupRecyclerView(item_list, allGroupsNotes.get(ANOTHER_DATA_LIST));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                positionCurrentShowingNotes = tab.getPosition();
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
                sendNote.setType(defaultTypes[positionCurrentShowingNotes]);
                startActivityForResult(new Intent(ItemListActivityMain.this, CreateChangeNoteActivity.class)
                        .putExtra(SENT_ALL_ANOTHER_TYPES_KEY, allTypesNotes)
                        .putExtra(CODE_WORK_WITH_NOTE, CREATE_NOTE_CODE)
                        .putExtra(CURRENT_NOTE_KEY, sendNote), REQUEST_CODE_CREATE_CHANGE_NOTE);
            }
        });
    }


    @Override
    public void onSelectedComparator(int idOfComparator) {
        resourcesAndSettings.writeComparator(idOfComparator);
        comparatorForSortNotes = Comparators.getComparatorOnId(idOfComparator);
        sortAllGroupsNotes();
        notesAdapter.notifyDataSetChanged();
    }

    public void sortAllGroupsNotes() {
        for (List<NoteDTO> groupsNotes : allGroupsNotes) {
            Collections.sort(groupsNotes, comparatorForSortNotes);
        }
    }

    public void sortGroupNotes(List<NoteDTO> sortedList) {
        Collections.sort(sortedList, comparatorForSortNotes);
    }

    private void deletingNoteOnPositionInCurrentListNotes(int positionDeletingNote) {
        dbManager.deleteNote(currentShowingNotes.get(positionDeletingNote));
        currentShowingNotes.remove(positionDeletingNote);
        notesAdapter.notifyDataSetChanged();
    }

    private void insertNote(NoteDTO note) {
        dbManager.insertNote(note);
        List<NoteDTO> listContainCurrentNote = allGroupsNotes.get(Utils.getListIdOfTypeNote(note));
        listContainCurrentNote.add(note);
        sortGroupNotes(listContainCurrentNote);
        notesAdapter.notifyDataSetChanged();
    }

    private void updateNote(NoteDTO updatingNote, int position) {
        List<NoteDTO> updatingNoteGroupList = allGroupsNotes.get(Utils.getListIdOfTypeNote(updatingNote));
        if (currentShowingNotes == updatingNoteGroupList) {
            currentShowingNotes.set(position, updatingNote);
            sortGroupNotes(currentShowingNotes);
        } else {
            updatingNoteGroupList.add(updatingNote);
            sortGroupNotes(updatingNoteGroupList);
            currentShowingNotes.remove(position);
        }
        dbManager.updateNote(updatingNote);
        notesAdapter.notifyDataSetChanged();
    }

    class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private List<NoteDTO> noteValues;

        SimpleItemRecyclerViewAdapter(List<NoteDTO> items) {
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
            NoteDTO currentNote = noteValues.get(position);
            holder.noteDate.setText(Utils.getStringFromDateAll(currentNote.getDate()));
            String shortDescription = currentNote.getDescription();
            try {
                shortDescription = shortDescription.substring(0, 20);
            } catch (Exception e) {/*empty*/}
            holder.noteShortDescription.setText(shortDescription);
            TextView typeShower = holder.itemView.findViewById(R.id.content_type);
            if (positionCurrentShowingNotes == 0) {
                typeShower.setVisibility(View.VISIBLE);
                typeShower.setText(currentNote.getType());
            } else {
                typeShower.setVisibility(View.INVISIBLE);
            }
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

        void setValues(List<NoteDTO> mValues) {
            this.noteValues = mValues;
            notifyDataSetChanged();
        }

        @Override
        public int getItemCount() {
            return noteValues.size();
        }


        class ViewHolder extends RecyclerView.ViewHolder {
            private View mView;
            private TextView noteShortDescription;
            private TextView noteDate;

            ViewHolder(View view) {
                super(view);
                mView = view;
                noteShortDescription = view.findViewById(R.id.content_short_description);
                noteDate = view.findViewById(R.id.content_date);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + noteShortDescription.getText() + "'";
            }
        }
    }
}