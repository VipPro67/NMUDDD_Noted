package com.picassos.noted.adapters;

import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.os.Looper;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class NotesTypeListAdapter extends RecyclerView.Adapter<NotesTypeListAdapter.NoteViewHolder> {

    private List<Note> notes;
    private final NotesListener notesListener;
    private Timer timer;
    private final List<Note> notesSource;

    public NotesTypeListAdapter(List<Note> notes, NotesListener notesListener) {
        this.notes = notes;
        this.notesListener = notesListener;
        notesSource = notes;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NoteViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_note_type_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        holder.setNote(notes.get(position));
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

        void setNote(Note note) {
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

    /**
     * request search notes added.
     * @param keyword for search keyword
     */
    public void requestSearchNotes(final String keyword) {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
              if (keyword.trim().isEmpty()) {
                  notes = notesSource;
              } else {
                  ArrayList<Note> temp = new ArrayList<>();
                  for (Note note : notesSource) {
                      if (note.getNote_title().toLowerCase().contains(keyword.toLowerCase())
                      || note.getNote_subtitle().toLowerCase().contains(keyword.toLowerCase())
                      || note.getNote_description().toLowerCase().contains(keyword.toLowerCase())) {
                          temp.add(note);
                      }
                  }
                  notes = temp;
              }
              new Handler(Looper.getMainLooper()).post(() -> notifyDataSetChanged());
            }
        }, 100);
    }

    /**
     * request cancel timer
     * if !equals to null
     */
    public void requestCancelTimer() {
        if (timer != null) {
            timer.cancel();
        }
    }

}
