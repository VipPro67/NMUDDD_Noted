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
import com.picassos.noted.entities.ArchiveNote;

import java.util.Objects;

public class ArchiveNoteActionsBottomSheetModal extends BottomSheetDialogFragment {

    public static int REQUEST_UNARCHIVE_NOTE = 1;

    private ArchiveNote archiveNote;

    public ArchiveNoteActionsBottomSheetModal() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.archive_note_actions_bottom_sheet_modal, container, false);

        archiveNote = (ArchiveNote) requireArguments().getSerializable("archive_note_data");

        // unarchive
        Button unarchive = view.findViewById(R.id.unarchive);
        unarchive.setOnClickListener(v -> requestDeleteArchiveNote(archiveNote));

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * request to delete a preset note
     * @param archive_note for class
     */
    private void requestDeleteArchiveNote(ArchiveNote archive_note) {
        if (archive_note != null) {
            @SuppressLint("StaticFieldLeak")
            class DeleteNoteTask extends AsyncTask<Void, Void, Void> {
                @Override
                protected Void doInBackground(Void... voids) {
                    APP_DATABASE.requestDatabase(getContext()).dao().request_delete_archive_note(archive_note);
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);
                    send_result(REQUEST_UNARCHIVE_NOTE);
                }
            }

            new DeleteNoteTask().execute();
        }
    }

    private void send_result(int REQUEST_CODE) {
        Intent intent = new Intent();
        Objects.requireNonNull(getTargetFragment()).onActivityResult(getTargetRequestCode(), REQUEST_CODE, intent);
        dismiss();
    }
}
