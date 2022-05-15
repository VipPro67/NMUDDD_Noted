package com.picassos.noted.listeners;

import com.picassos.noted.entities.ArchiveNote;

public interface ArchiveNotesListener {
    void onNoteClicked(ArchiveNote archive_note, int position);

    void onNoteLongClicked(ArchiveNote archive_note, int position);
}
