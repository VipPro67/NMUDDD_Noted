package com.picassos.noted.sheets;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.picassos.noted.R;
import com.picassos.noted.databases.APP_DATABASE;
import com.picassos.noted.entities.Note;
import com.picassos.noted.entities.TrashNote;

import java.util.Objects;

public class NoteActionsBottomSheetModal extends BottomSheetDialogFragment {

    public static int REQUEST_DELETE_NOTE_CODE = 3;
    public static int REQUEST_DISCARD_NOTE_CODE = 4;

    private Note note;

    public NoteActionsBottomSheetModal() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.note_actions_bottom_sheet_modal, container, false);

        note = (Note) requireArguments().getSerializable("note_data");

        // move to trash
        Button moveToTrash = view.findViewById(R.id.move_to_trash);
        moveToTrash.setOnClickListener(v -> requestPresetTrashNote());

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * request save data from note class
     * to trash note class
     */
    private void requestPresetTrashNote() {
        if (note != null) {
            final TrashNote preset_trash_note = new TrashNote();
            preset_trash_note.setNote_category_id(note.getNote_id());
            preset_trash_note.setNote_title(note.getNote_title());
            preset_trash_note.setNote_created_at(note.getNote_created_at());
            preset_trash_note.setNote_subtitle(note.getNote_subtitle());
            preset_trash_note.setNote_description(note.getNote_description());
            preset_trash_note.setNote_image_path(note.getNote_image_path());
            preset_trash_note.setNote_color(note.getNote_color());
            preset_trash_note.setNote_web_link(note.getNote_web_link());
            preset_trash_note.setNote_category_id(note.getNote_category_id());
            preset_trash_note.setNote_reminder(note.getNote_reminder());
            preset_trash_note.setNote_locked(note.isNote_locked());

            requestMoveNoteToTrash(preset_trash_note);
        } else {
            sendResult(REQUEST_DISCARD_NOTE_CODE);
        }
    }

    /** request move the preset note
     * to trash before delete
     * @param trash_note for class
     */
    private void requestMoveNoteToTrash(TrashNote trash_note) {
        if (note != null) {
            @SuppressLint("StaticFieldLeak")
            class MoveNoteTask extends AsyncTask<Void, Void, Void> {
                @Override
                protected Void doInBackground(Void... voids) {
                    APP_DATABASE.requestDatabase(getContext()).dao().request_insert_trash_note(trash_note);
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);
                    requestDeleteNote(note);
                    dismiss();
                }
            }

            new MoveNoteTask().execute();
        }
    }

    /**
     * request to delete a preset note
     * @param note for class
     */
    private void requestDeleteNote(Note note) {
        if (note != null) {
            @SuppressLint("StaticFieldLeak")
            class DeleteNoteTask extends AsyncTask<Void, Void, Void> {
                @Override
                protected Void doInBackground(Void... voids) {
                    APP_DATABASE.requestDatabase(getContext()).dao().request_delete_note(note);
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);
                    sendResult(REQUEST_DELETE_NOTE_CODE);
                }
            }

            new DeleteNoteTask().execute();
        } else {
            sendResult(REQUEST_DISCARD_NOTE_CODE);
        }
    }

    private void sendResult(int REQUEST_CODE) {
        Intent intent = new Intent();
        Objects.requireNonNull(getTargetFragment()).onActivityResult(getTargetRequestCode(), REQUEST_CODE, intent);
        dismiss();
    }
}
