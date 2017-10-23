package com.example.jwtasks2.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.jwtasks2.ui.activities.CreateChangeNoteActivity;
import com.example.jwtasks2.ui.activities.ItemDetailActivity;
import com.example.jwtasks2.ui.fragments.ItemDetailFragment;
import com.example.jwtasks2.ui.activities.ItemListActivityMain;
import com.example.jwtasks2.R;
import com.example.jwtasks2.model.NoteDTO;
import com.example.jwtasks2.services.Utils;

import java.util.List;

import static com.example.jwtasks2.services.Constants.CODE_WORK_WITH_NOTE;
import static com.example.jwtasks2.services.Constants.CURRENT_NOTE_KEY;
import static com.example.jwtasks2.services.Constants.POSITION_UPDATING_TASK;
import static com.example.jwtasks2.services.Constants.REQUEST_CODE_CREATE_CHANGE_NOTE;
import static com.example.jwtasks2.services.Constants.SHOWING_ELEMENT;
import static com.example.jwtasks2.services.Constants.UPDATE_NOTE_CODE;

public class SimpleItemRecyclerViewAdapter extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {
    private List<NoteDTO> noteValues;
    private ItemListActivityMain containThisActivity;

    public SimpleItemRecyclerViewAdapter(ItemListActivityMain containThisActivity, List<NoteDTO> items) {
        noteValues = items;
        this.containThisActivity = containThisActivity;
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
        if (containThisActivity.getPositionCurrentShowingNotes() == 0) {
            typeShower.setVisibility(View.VISIBLE);
            typeShower.setText(currentNote.getType());
        } else {
            typeShower.setVisibility(View.INVISIBLE);
        }
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                PopupMenu popup = new PopupMenu(containThisActivity, holder.itemView);
                popup.inflate(R.menu.actions_with_note_menu);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.menu_item_delete_note:
                                containThisActivity.deletingNoteOnPositionInCurrentListNotes(position);
                                break;
                            case R.id.menu_item_edit_note:
                                containThisActivity.startActivityForResult(new Intent(containThisActivity, CreateChangeNoteActivity.class)
                                                .putExtra(CODE_WORK_WITH_NOTE, UPDATE_NOTE_CODE)
                                                .putExtra(CURRENT_NOTE_KEY, containThisActivity.getCurrentShowingNotes().get(position))
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
                if (containThisActivity.isCurrentMachineIsTablet()) {
                    Bundle arguments = new Bundle();
                    arguments.putParcelable(SHOWING_ELEMENT, noteValues.get(position));
                    ItemDetailFragment fragment = new ItemDetailFragment();
                    fragment.setArguments(arguments);
                    containThisActivity.getSupportFragmentManager().beginTransaction()
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
        private TextView noteShortDescription;
        private TextView noteDate;

        ViewHolder(View view) {
            super(view);
            mView = view;
            noteShortDescription = view.findViewById(R.id.content_short_description);
            noteDate = view.findViewById(R.id.content_date);
        }
    }
}