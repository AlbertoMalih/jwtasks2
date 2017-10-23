package com.example.jwtasks2.services;

import com.example.jwtasks2.model.NoteDTO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

import static com.example.jwtasks2.services.Constants.ANOTHER_DATA_LIST;

public class ContainerNotes {
    private DbManager dbManager;
    private List<List<NoteDTO>> allGroupsNotes;
    private Comparator<NoteDTO> comparatorForSortNotes;
    private ArrayList<String> allTypesNotes;
    private String[] defaultTypes;
    private String[] defaultTypesDefLang;
    private ArrayList<OnGetAllDataListener> listenersPostGetAllNotes;
    private boolean isEmpty = true;

    public ContainerNotes(String[] defaultTypes, String[] defaultTypesDefLang, DbManager dbManager) {
        this.defaultTypes = defaultTypes;
        this.defaultTypesDefLang = defaultTypesDefLang;
        this.dbManager = dbManager;

        allGroupsNotes = new ArrayList<>();
        allTypesNotes = new ArrayList<>();
        for (String defaultType : defaultTypes) {
            allGroupsNotes.add(new ArrayList<NoteDTO>());
            allTypesNotes.add(defaultType);
        }
        listenersPostGetAllNotes = new ArrayList<>();
    }

    public void subscribeOnPostGetAllData(OnGetAllDataListener onGetAllDataListener) {
        listenersPostGetAllNotes.add(onGetAllDataListener);
    }

    public void removeNote(int position, int numberList) {
        dbManager.deleteNote(allGroupsNotes.get(numberList).get(position));
        allGroupsNotes.get(numberList).remove(position);
    }

    public void insertNote(NoteDTO note, int numberList) {
        dbManager.insertNote(note);
        allGroupsNotes.get(numberList).add(note);
        sortGroupNotes(allGroupsNotes.get(numberList));
    }

    public void updateNote(NoteDTO note, int position, int numberList) {
        dbManager.updateNote(note);
        allGroupsNotes.get(numberList).set(position, note);
        sortGroupNotes(allGroupsNotes.get(numberList));
    }

    public void deleteNotesInSimpleGroupAndType(String group) {
        dbManager.deleteAllNotesInSimpleGroup(group);
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
    }

    public void deleteAllNotes() {
        dbManager.deleteAllNotes();
        for (List<NoteDTO> simpleGroupNotes : allGroupsNotes) {
            simpleGroupNotes.clear();
        }
    }

    public void initAllGroupsNotes() {
        dbManager.installAllNotesInListener(
                new Observer<NoteDTO>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(NoteDTO currentNote) {
                        String currentType = currentNote.getType();
                        boolean isCustomType = true;
                        for (int i = 0; i < defaultTypesDefLang.length; i++) {
                            if (defaultTypesDefLang[i].equals(currentType)) {
                                isCustomType = false;
                                currentNote.setType(defaultTypes[i]);
                                allGroupsNotes.get(i).add(currentNote);
                                break;
                            }
                        }
                        if (isCustomType) {
                            allGroupsNotes.get(ANOTHER_DATA_LIST).add(currentNote);
                            if (!allTypesNotes.contains(currentType)) {
                                allTypesNotes.add(currentType);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onComplete() {
                        isEmpty = false;
                        sortAllGroupsNotes();
                        for (OnGetAllDataListener listenersPostGetAllNote : listenersPostGetAllNotes) {
                            listenersPostGetAllNote.onResponseAllData();
                        }
                    }
                }
        );
    }


    private void sortAllGroupsNotes() {
        for (List<NoteDTO> groupsNotes : allGroupsNotes) {
            Collections.sort(groupsNotes, comparatorForSortNotes);
        }
    }

    private void sortGroupNotes(List<NoteDTO> sortedList) {
        Collections.sort(sortedList, comparatorForSortNotes);
    }


    public List<NoteDTO> getGroupNotes(int position) {
        return allGroupsNotes.get(position);
    }

    public void setComparatorForSortNotes(Comparator<NoteDTO> comparatorForSortNotes) {
        this.comparatorForSortNotes = comparatorForSortNotes;
        sortAllGroupsNotes();
    }

    public Comparator<NoteDTO> getComparatorForSortNotes() {
        return comparatorForSortNotes;
    }

    public ArrayList<String> getAllTypesNotes() {
        return allTypesNotes;
    }
//
//    public boolean notEmpty() {
//        for (List<NoteDTO> allGroupsNote : allGroupsNotes) {
//            if (allGroupsNote.isEmpty())  return false;
//        }
//        return true;
//    }


    public boolean isEmpty() {
        return isEmpty;
    }

    public interface OnGetAllDataListener {
        void onResponseAllData();
    }
}
