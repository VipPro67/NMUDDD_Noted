package com.picassos.noted.listeners;

import com.picassos.noted.entities.Note;

public interface NotesActionListener {
    void onNoteAction(Note note, int position, boolean isSelected);
}
