package com.example.jwtasks2.services;

import android.util.Log;

import com.example.jwtasks2.ItemListActivityMain;
import com.example.jwtasks2.model.NoteDTO;

import java.util.ArrayList;
import java.util.Comparator;

public class Comparators {
    public static final int DATE_COMPARATOR_CODE = 0;
    public static final int TYPE_COMPARATOR_CODE = 1;
    public static final int DESCRIPTION_COMPARATOR_CODE = 2;
    private static ArrayList<Comparator<NoteDTO>> comparators = new ArrayList<>();

    static {
        comparators.add(new Comparator<NoteDTO>() {
            @Override
            public int compare(NoteDTO lhs, NoteDTO rhs) {
                return lhs.getDate().compareTo(rhs.getDate());
            }
        });
        comparators.add(new Comparator<NoteDTO>() {
            @Override
            public int compare(NoteDTO lhs, NoteDTO rhs) {
                return lhs.getType().compareTo(rhs.getType());
            }
        });
        comparators.add(new Comparator<NoteDTO>() {
            @Override
            public int compare(NoteDTO lhs, NoteDTO rhs) {
                return lhs.getDescription().compareTo(rhs.getDescription());
            }
        });
    }

    public static Comparator<NoteDTO> getComparatorOnId(int idComparator) {
        return comparators.get(idComparator);
    }


    public static int getIdOnComparator(Comparator<NoteDTO> idComparator) {
        return comparators.indexOf(idComparator);
    }
}
