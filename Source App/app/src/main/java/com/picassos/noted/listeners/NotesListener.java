package com.picassos.noted.listeners;

import com.picassos.noted.entities.Note;

public interface NotesListener {
    void onNoteClicked(Note note, int position);

    void onNoteLongClicked(Note note, int position);
}
