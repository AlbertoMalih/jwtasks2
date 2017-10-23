package com.example.jwtasks2.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.jwtasks2.R;
import com.example.jwtasks2.model.NoteDTO;
import com.example.jwtasks2.services.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.jwtasks2.services.Constants.SHOWING_ELEMENT;

public class ItemDetailFragment extends Fragment {
    public static final int CURRENT_LAYOUT = R.layout.fragment_note_details;
    @BindView(R.id.detail_output_date)
    TextView detailOutputDate;
    @BindView(R.id.detail_output_description)
    TextView detailOutputDescription;
    @BindView(R.id.detail_output_type)
    TextView detailOutputType;

    public ItemDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View parentView = inflater.inflate(CURRENT_LAYOUT, container, false);
        ButterKnife.bind(this, parentView);
        return parentView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getArguments() == null) {
            return;
        }
        NoteDTO showingNoteItem = getArguments().getParcelable(SHOWING_ELEMENT);
        detailOutputDate.setText(Utils.getStringFromDateStart(showingNoteItem.getDate()));
        detailOutputDescription.setText(showingNoteItem.getDescription());
        detailOutputType.setText(showingNoteItem.getType());
    }
}