package com.picassos.noted.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.picassos.noted.R;
import com.picassos.noted.activities.AddNoteActivity;
import com.picassos.noted.adapters.ReminderNotesAdapter;
import com.picassos.noted.databases.APP_DATABASE;
import com.picassos.noted.entities.Note;
import com.picassos.noted.listeners.NotesListener;
import com.picassos.noted.sharedPreferences.SharedPref;
import com.picassos.noted.sheets.NoteActionsBottomSheetModal;
import com.picassos.noted.sheets.PasswordBottomSheetModal;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class RemindersFragment extends Fragment implements NotesListener {

    // Bundle
    Bundle bundle;

    SharedPref sharedPref;

    // REQUEST CODES
    private final int REQUEST_CODE_ADD_NOTE_OK = 1;
    private final int REQUEST_CODE_UPDATE_NOTE_OK = 2;
    private final int REQUEST_CODE_VIEW_NOTE_OK = 3;
    private final int REQUEST_CODE_UNLOCK_NOTE = 10;

    private RecyclerView reminderNotesRecyclerview;

    // View view
    View view;

    private List<Note> notes;
    private ReminderNotesAdapter reminderNotesAdapter;

    private int noteClickedPosition = -1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_reminders, container, false);

        // initialize bundle
        bundle = new Bundle();

        // notes recyclerview
        reminderNotesRecyclerview = view.findViewById(R.id.notes_recyclerview);
        reminderNotesRecyclerview.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        // notes list, adapter
        notes = new ArrayList<>();
        reminderNotesAdapter = new ReminderNotesAdapter(notes, this);
        reminderNotesRecyclerview.setAdapter(reminderNotesAdapter);

        requestReminderNotes(REQUEST_CODE_VIEW_NOTE_OK,false);

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        sharedPref = new SharedPref(requireContext());
        super.onCreate(savedInstanceState);
    }

    /**
     * request notes from AsyncTask
     * @param requestCode for request code
     * @param isDeleted for delete status
     */
    private void requestReminderNotes(final int requestCode, final boolean isDeleted) {
        @SuppressLint("StaticFieldLeak")
        class GetNotesTask extends AsyncTask<Void, Void, List<Note>> {

            @Override
            protected List<Note> doInBackground(Void... voids) {
                return APP_DATABASE.requestDatabase(getContext()).dao().request_reminder_notes();
            }

            @Override
            protected void onPostExecute(List<Note> notes_inline) {
                super.onPostExecute(notes_inline);
                if (requestCode == REQUEST_CODE_VIEW_NOTE_OK) {
                    notes.addAll(notes_inline);
                    reminderNotesAdapter.notifyDataSetChanged();
                } else if (requestCode == REQUEST_CODE_ADD_NOTE_OK) {
                    notes.add(0, notes_inline.get(0));
                    reminderNotesAdapter.notifyItemInserted(0);
                    reminderNotesRecyclerview.smoothScrollToPosition(0);
                } else if (requestCode == REQUEST_CODE_UPDATE_NOTE_OK) {
                    notes.remove(noteClickedPosition);
                    if (isDeleted) {
                        reminderNotesAdapter.notifyItemRemoved(noteClickedPosition);
                    } else {
                        notes.add(noteClickedPosition, notes_inline.get(noteClickedPosition));
                        reminderNotesAdapter.notifyItemChanged(noteClickedPosition);
                    }
                }

                if (reminderNotesAdapter.getItemCount() == 0) {
                    view.findViewById(R.id.no_items).setVisibility(View.VISIBLE);
                } else {
                    view.findViewById(R.id.no_items).setVisibility(View.GONE);
                }
            }

        }
        new GetNotesTask().execute();
    }

    @Override
    public void onNoteClicked(Note note, int position) {
        noteClickedPosition = position;

        if (sharedPref.loadNotePinCode() == 0) {
            Intent intent = new Intent(getContext(), AddNoteActivity.class);
            intent.putExtra("modifier", true);
            intent.putExtra("note", note);
            startActivityForResult(intent, REQUEST_CODE_UPDATE_NOTE_OK);
        } else {
            if (note.isNote_locked()) {
                bundle.putSerializable("data", note);
                PasswordBottomSheetModal passwordBottomSheetModal = new PasswordBottomSheetModal();
                passwordBottomSheetModal.setArguments(bundle);
                passwordBottomSheetModal.setTargetFragment(RemindersFragment.this, REQUEST_CODE_UNLOCK_NOTE);
                passwordBottomSheetModal.show(requireFragmentManager(), "TAG");
            } else {
                Intent intent = new Intent(getContext(), AddNoteActivity.class);
                intent.putExtra("modifier", true);
                intent.putExtra("note", note);
                startActivityForResult(intent, REQUEST_CODE_UPDATE_NOTE_OK);
            }
        }
    }

    @Override
    public void onNoteLongClicked(Note note, int position) {
        noteClickedPosition = position;
        // add note bundle
        bundle.putSerializable("note_data", note);

        NoteActionsBottomSheetModal noteActionsBottomSheetModal = new NoteActionsBottomSheetModal();
        noteActionsBottomSheetModal.setArguments(bundle);
        noteActionsBottomSheetModal.setTargetFragment(this, 3);
        noteActionsBottomSheetModal.show(requireFragmentManager(), "TAG");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_ADD_NOTE_OK && resultCode == RESULT_OK) {
            requestReminderNotes(REQUEST_CODE_ADD_NOTE_OK, false);
        } else if (requestCode == REQUEST_CODE_UPDATE_NOTE_OK && resultCode == RESULT_OK) {
            if (data != null) {
                requestReminderNotes(REQUEST_CODE_UPDATE_NOTE_OK, data.getBooleanExtra("is_note_removed", false));
            }
        } else if (requestCode == NoteActionsBottomSheetModal.REQUEST_DELETE_NOTE_CODE) {
            requestReminderNotes(REQUEST_CODE_UPDATE_NOTE_OK, true);
            Toast.makeText(getContext(), getString(R.string.note_moved_to_trash), Toast.LENGTH_SHORT).show();
        } else if (requestCode == REQUEST_CODE_UNLOCK_NOTE) {
            if (data != null) {
                Note note = (Note) data.getSerializableExtra("data");
                Intent intent = new Intent(getContext(), AddNoteActivity.class);
                intent.putExtra("modifier", true);
                intent.putExtra("note", note);
                startActivityForResult(intent, REQUEST_CODE_UPDATE_NOTE_OK);
            }
        }
    }
}
