package com.picassos.noted.adapters;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.picassos.noted.R;
import com.picassos.noted.entities.Note;
import com.picassos.noted.listeners.NotesActionListener;
import com.picassos.noted.listeners.NotesListener;

import java.util.ArrayList;
import java.util.List;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NoteViewHolder> {

    private final Context context;
    private final List<Note> notes;
    private final NotesListener notesListener;
    private final NotesActionListener notesActionListener;

    private final SparseBooleanArray selectedItems;
    private int currentSelectedIndex = -1;

    public NotesAdapter(Context context, List<Note> notes, NotesListener notesListener, NotesActionListener notesActionListener) {
        this.context = context;
        this.notes = notes;
        this.notesListener = notesListener;
        this.notesActionListener = notesActionListener;
        selectedItems = new SparseBooleanArray();
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NoteViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_note, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        holder.set_note(notes.get(position));
        holder.layout.setActivated(selectedItems.get(position, false));
        holder.layout.setOnClickListener(v -> notesListener.onNoteClicked(notes.get(position), position));
        holder.bindNote(notes.get(position), position);
        holder.layout.setOnLongClickListener(v -> {
            notesListener.onNoteLongClicked(notes.get(position), position);
            return true;
        });

        // toggle checked item
        toggleCheckedItem(holder, notes.get(position), position);
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    class NoteViewHolder extends RecyclerView.ViewHolder {

        public RelativeLayout layout;
        public RelativeLayout noteContainer;

        TextView noteTitle;
        TextView noteCreatedAt;
        TextView noteSubtitle;

        RoundedImageView noteImage;

        FrameLayout noteLocker;

        NoteViewHolder(@NonNull View note_view) {
            super(note_view);

            layout = note_view.findViewById(R.id.item_note_layout);
            noteContainer = note_view.findViewById(R.id.item_note_container);

            noteTitle = note_view.findViewById(R.id.item_note_title);
            noteCreatedAt = note_view.findViewById(R.id.item_note_created_at);
            noteSubtitle = note_view.findViewById(R.id.item_note_subtitle);

            noteImage = note_view.findViewById(R.id.item_note_image);

            noteLocker = note_view.findViewById(R.id.item_note_locker);
        }

        void set_note(Note note) {
            noteTitle.setText(note.getNote_title());
            noteCreatedAt.setText(note.getNote_created_at());

            if (note.getNote_subtitle() != null) {
                if (note.getNote_subtitle().trim().isEmpty()) {
                    noteSubtitle.setVisibility(View.GONE);
                } else {
                    noteSubtitle.setText(note.getNote_subtitle());
                }
            } else {
                noteSubtitle.setVisibility(View.GONE);
            }

            // set note image
            if (!note.getNote_image_path().trim().isEmpty()) {
                noteImage.setImageBitmap(BitmapFactory.decodeFile(note.getNote_image_path()));
                noteImage.setVisibility(View.VISIBLE);
            } else {
                noteImage.setImageBitmap(null);
                noteImage.setVisibility(View.GONE);
            }

            // set note selected
            if (note.isNote_selected()) {
                layout.setBackgroundResource(R.drawable.note_background_border);
            } else {
                layout.setBackgroundResource(R.drawable.note_background);
            }

            // set note background
            GradientDrawable shape = (GradientDrawable) layout.getBackground();
            if (note.getNote_color() != null) {
                shape.setColor(Color.parseColor(note.getNote_color()));
            } else {
                shape.setColor(Color.parseColor("#ebebeb"));
            }

            if (note.isNote_locked()) {
                noteContainer.setVisibility(View.GONE);
                noteLocker.setVisibility(View.VISIBLE);
            } else {
                noteLocker.setVisibility(View.GONE);
                noteContainer.setVisibility(View.VISIBLE);
            }

            // set note background
            GradientDrawable lockerShape = (GradientDrawable) noteLocker.getBackground();
            if (note.getNote_color() != null) {
                lockerShape.setColor(Color.parseColor(note.getNote_color()));
            } else {
                lockerShape.setColor(Color.parseColor("#ebebeb"));
            }
        }

        void bindNote(final Note note, int position) {
            layout.setOnClickListener(v -> notesActionListener.onNoteAction(note, position, false));
        }

    }

    /**
     * toggle selection for items
     * @param position for int position
     */
    public void toggleSelection(int position) {
        currentSelectedIndex = position;
        if (selectedItems.get(position, false)) {
            selectedItems.delete(position);
        } else {
            selectedItems.put(position, true);
        }
        notifyItemChanged(position);
    }

    /**
     * clear selection for selected items
     */
    public void clearSelection() {
        selectedItems.clear();
        notifyDataSetChanged();
    }

    /**
     * get selected item count (notes).
     * @return selected items (size).
     */
    public int getSelectedItemCount() {
        return selectedItems.size();
    }

    /**
     * get selected items (notes).
     * @return items selected.
     */
    public List<Integer> getSelectedItems() {
        List<Integer> items = new ArrayList<>(selectedItems.size());
        for (int i = 0; i < selectedItems.size(); i++) {
            items.add(selectedItems.keyAt(i));
        }
        return items;
    }

    /**
     * request remove data
     * @param position for int position
     */
    public void removeData(int position) {
        notes.remove(position);
        clearSelection();
        resetCurrentIndex();
    }

    /**
     * reset current selected item index
     */
    private void resetCurrentIndex() {
        currentSelectedIndex = -1;
    }

    private void toggleCheckedItem(NoteViewHolder holder, Note note, int position) {
        // apply border to item
        if (selectedItems.get(position, false)) {
            holder.layout.setBackgroundResource(R.drawable.note_background_border);
        } else {
            holder.layout.setBackgroundResource(R.drawable.note_background);
        }
        // check if note has background color
        GradientDrawable shape = (GradientDrawable) holder.layout.getBackground();
        if (note.getNote_color() != null) {
            shape.setColor(Color.parseColor(note.getNote_color()));
        } else {
            shape.setColor(Color.parseColor("#ebebeb"));
        }
        // reset current index (-1).
        if (currentSelectedIndex == position) {
            resetCurrentIndex();
        }
    }

}
