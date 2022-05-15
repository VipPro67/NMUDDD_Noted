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
import com.picassos.noted.entities.ArchiveNote;
import com.picassos.noted.listeners.ArchiveNotesListener;

import java.util.List;

public class ArchiveNotesAdapter extends RecyclerView.Adapter<ArchiveNotesAdapter.NoteViewHolder> {

    private final List<ArchiveNote> archiveNotes;
    private final ArchiveNotesListener archiveNotesListener;

    public ArchiveNotesAdapter(List<ArchiveNote> trash_notes, ArchiveNotesListener archiveNotesListener) {
        this.archiveNotes = trash_notes;
        this.archiveNotesListener = archiveNotesListener;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NoteViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_note_type_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        holder.set_note(archiveNotes.get(position));
        holder.layout.setOnClickListener(v -> archiveNotesListener.onNoteClicked(archiveNotes.get(position), position));
        holder.layout.setOnLongClickListener(v -> {
            archiveNotesListener.onNoteLongClicked(archiveNotes.get(position), position);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return archiveNotes.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    static class NoteViewHolder extends RecyclerView.ViewHolder {

        public RelativeLayout layout;
        public LinearLayout noteContainer;

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

        void set_note(ArchiveNote note) {
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
