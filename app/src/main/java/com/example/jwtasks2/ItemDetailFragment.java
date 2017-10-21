package com.example.jwtasks2;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.jwtasks2.model.NoteDTO;
import com.example.jwtasks2.services.Utils;

public class ItemDetailFragment extends Fragment {

    public ItemDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_note_details, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getArguments() == null) {
            return;
        }
        NoteDTO showingNoteItem = getArguments().getParcelable(ItemListActivityMain.SHOWING_ELEMENT);
        ((TextView) getView().findViewById(R.id.detail_output_date)).setText(Utils.getStringFromDateStart(showingNoteItem.getDate()));
        ((TextView) getView().findViewById(R.id.detail_output_description)).setText(showingNoteItem.getDescription());
        ((TextView) getView().findViewById(R.id.detail_output_type)).setText(showingNoteItem.getType());
    }
}