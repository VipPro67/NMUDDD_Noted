package com.picassos.noted.adapters;

import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.picassos.noted.R;
import com.picassos.noted.entities.Note;
import com.picassos.noted.listeners.NotesListener;

import java.util.List;

public class ReminderNotesAdapter extends RecyclerView.Adapter<ReminderNotesAdapter.NoteViewHolder> {

    private final List<Note> notes;
    private final NotesListener notesListener;

    public ReminderNotesAdapter(List<Note> notes, NotesListener notesListener) {
        this.notes = notes;
        this.notesListener = notesListener;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NoteViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_note_type_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        holder.set_note(notes.get(position));
        holder.layout.setOnClickListener(v -> notesListener.onNoteClicked(notes.get(position), position));
        holder.layout.setOnLongClickListener(v -> {
            notesListener.onNoteLongClicked(notes.get(position), position);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    static class NoteViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout layout;
        LinearLayout noteContainer;
        LinearLayout reminderContainer;

        TextView noteTitle;
        TextView noteCreatedAt;
        TextView noteSubtitle;
        TextView noteReminder;

        RoundedImageView noteImage;

        FrameLayout noteLocker;

        NoteViewHolder(@NonNull View note_view) {
            super(note_view);

            layout = note_view.findViewById(R.id.item_note_layout);
            noteContainer = note_view.findViewById(R.id.item_note_container);
            reminderContainer = note_view.findViewById(R.id.item_note_reminder_container);

            noteTitle = note_view.findViewById(R.id.item_note_title);
            noteCreatedAt = note_view.findViewById(R.id.item_note_created_at);
            noteSubtitle = note_view.findViewById(R.id.item_note_subtitle);
            noteReminder = note_view.findViewById(R.id.item_note_reminder);

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

            if (!note.getNote_reminder().trim().isEmpty()) {
                reminderContainer.setVisibility(View.VISIBLE);
                noteReminder.setText(note.getNote_reminder());
            } else {
                reminderContainer.setVisibility(View.GONE);
                noteReminder.setText("");
            }

            // set note background
            GradientDrawable shape = (GradientDrawable) layout.getBackground();
            if (note.getNote_color() != null) {
                shape.setColor(Color.parseColor(note.getNote_color()));
            } else {
                shape.setColor(Color.parseColor("#ebebeb"));
            }

            // set note image
            if (!note.getNote_image_path().trim().isEmpty()) {
                noteImage.setImageBitmap(BitmapFactory.decodeFile(note.getNote_image_path()));
                noteImage.setVisibility(View.VISIBLE);
            } else {
                noteImage.setImageBitmap(null);
                noteImage.setVisibility(View.GONE);
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

    }

}
