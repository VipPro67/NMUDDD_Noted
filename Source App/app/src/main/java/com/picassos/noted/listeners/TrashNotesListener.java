package com.picassos.noted.listeners;

import com.picassos.noted.entities.TrashNote;

public interface TrashNotesListener {
    void onNoteClicked(TrashNote trashNote, int position);

    void onNoteLongClicked(TrashNote trashNote, int position);
}
