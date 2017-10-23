package com.example.jwtasks2.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.jwtasks2.ui.fragments.ItemDetailFragment;
import com.example.jwtasks2.R;
import com.example.jwtasks2.ui.adapter.SimpleItemRecyclerViewAdapter;
import com.example.jwtasks2.model.NoteDTO;
import com.example.jwtasks2.services.Comparators;
import com.example.jwtasks2.services.ContainerNotes;
import com.example.jwtasks2.services.DbManager;
import com.example.jwtasks2.services.Dialogs;
import com.example.jwtasks2.services.ResourcesAndSettings;
import com.example.jwtasks2.services.Utils;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;

import static com.example.jwtasks2.services.Constants.ANOTHER_DATA_LIST;
import static com.example.jwtasks2.services.Constants.CODE_WORK_WITH_NOTE;
import static com.example.jwtasks2.services.Constants.CREATE_NOTE_CODE;
import static com.example.jwtasks2.services.Constants.CURRENT_NOTE_KEY;
import static com.example.jwtasks2.services.Constants.POSITION_UPDATING_TASK;
import static com.example.jwtasks2.services.Constants.REQUEST_CODE_CREATE_CHANGE_NOTE;
import static com.example.jwtasks2.services.Constants.SHOWING_ELEMENT;
import static com.example.jwtasks2.services.Constants.UPDATE_NOTE_CODE;

public class ItemListActivityMain extends BaseActivity implements ContainerNotes.OnGetAllDataListener, Dialogs.OnDeleteTypesListener, Dialogs.OnSelectedComparatorListener {
    //todo add firebase database and auth
    private String[] defaultTypes;
    private boolean currentMachineIsTablet;
    private SimpleItemRecyclerViewAdapter notesAdapter;
    private List<NoteDTO> currentShowingNotes;
    private int positionCurrentShowingNotes;
    @Inject
    DbManager dbManager;
    @Inject
    ContainerNotes containerNotes;
    @Inject
    ResourcesAndSettings resourcesAndSettings;
    @BindView(R.id.tabs)
    TabLayout tabLayout;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.item_list)
    RecyclerView item_list;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initDefTypesAndForUtils();
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
                Dialogs.showChooseComparator(this, Comparators.getIdOnComparator(containerNotes.getComparatorForSortNotes()), this);
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
        dbManager.close();
    }


    @Override
    public void onDeleteAllTypes() {
        containerNotes.deleteAllNotes();
        notesAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDeleteNotesInSimpleGroupAndType(String group) {
        containerNotes.deleteNotesInSimpleGroupAndType(group);
        notesAdapter.notifyDataSetChanged();
    }

    @Override
    public void onSelectedComparator(int idOfComparator) {
        containerNotes.setComparatorForSortNotes(Comparators.getComparatorOnId(idOfComparator));
        resourcesAndSettings.writeComparator(idOfComparator);
        notesAdapter.notifyDataSetChanged();
    }


    private void initAllGroupsNotes() {
        if (!containerNotes.isEmpty()) {
            onResponseAllData();
            return;
        }
        containerNotes.subscribeOnPostGetAllData(this);
        containerNotes.initAllGroupsNotes();
    }

    private void initToolbar() {
        setSupportActionBar(toolbar);
        for (String defaultType : defaultTypes) {
            tabLayout.addTab(tabLayout.newTab().setText(defaultType));
        }
    }

    private void initDefTypesAndForUtils() {
        defaultTypes = resourcesAndSettings.getDefaultTypes();
        Utils.setDefaultTypesDefLang(resourcesAndSettings.getDefaultTypesDefLang());
        Utils.setDefaultTypes(defaultTypes);
    }

    @Override
    public void onResponseAllData() {
        currentShowingNotes = containerNotes.getGroupNotes(ANOTHER_DATA_LIST);
        item_list.setAdapter(notesAdapter = new SimpleItemRecyclerViewAdapter(this, currentShowingNotes));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                positionCurrentShowingNotes = tab.getPosition();
                notesAdapter.setValues(currentShowingNotes = containerNotes.getGroupNotes(tab.getPosition()));
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
                if (positionCurrentShowingNotes != 0)
                    sendNote.setType(defaultTypes[positionCurrentShowingNotes]);
                else sendNote.setType(defaultTypes[1]);

                startActivityForResult(new Intent(ItemListActivityMain.this, CreateChangeNoteActivity.class)
                        .putExtra(CODE_WORK_WITH_NOTE, CREATE_NOTE_CODE)
                        .putExtra(CURRENT_NOTE_KEY, sendNote), REQUEST_CODE_CREATE_CHANGE_NOTE);
            }
        });
    }


    public void deletingNoteOnPositionInCurrentListNotes(int positionDeletingNote) {
        containerNotes.removeNote(positionDeletingNote, positionCurrentShowingNotes);
        notesAdapter.notifyDataSetChanged();
    }

    private void insertNote(NoteDTO note) {
        containerNotes.insertNote(note, Utils.getListIdOfTypeNote(note));
        notesAdapter.notifyDataSetChanged();
    }

    private void updateNote(NoteDTO updatingNote, int position) {
        int positionListOfNote = Utils.getListIdOfTypeNote(updatingNote);
        if (positionCurrentShowingNotes == positionListOfNote) {
            containerNotes.updateNote(updatingNote, position, positionListOfNote);
        } else {
            containerNotes.removeNote(position, positionCurrentShowingNotes);
            containerNotes.insertNote(updatingNote, positionListOfNote);
        }
        notesAdapter.notifyDataSetChanged();
    }

    public int getPositionCurrentShowingNotes() {
        return positionCurrentShowingNotes;
    }

    public List<NoteDTO> getCurrentShowingNotes() {
        return currentShowingNotes;
    }

    public boolean isCurrentMachineIsTablet() {
        return currentMachineIsTablet;
    }
    //// TODO  вынести activities в пакет activity
}