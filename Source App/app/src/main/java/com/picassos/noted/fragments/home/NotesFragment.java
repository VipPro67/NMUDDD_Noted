package com.picassos.noted.fragments.home;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.picassos.noted.R;
import com.picassos.noted.activities.AddNoteActivity;
import com.picassos.noted.activities.EditCategoryActivity;
import com.picassos.noted.activities.FilteredNotesActivity;
import com.picassos.noted.activities.MainActivity;
import com.picassos.noted.activities.SearchActivity;
import com.picassos.noted.adapters.ChipCategoryAdapter;
import com.picassos.noted.adapters.NotesAdapter;
import com.picassos.noted.databases.APP_DATABASE;
import com.picassos.noted.entities.Category;
import com.picassos.noted.entities.Note;
import com.picassos.noted.listeners.ChipCategoryListener;
import com.picassos.noted.listeners.NotesActionListener;
import com.picassos.noted.listeners.NotesListener;
import com.picassos.noted.sharedPreferences.SharedPref;
import com.picassos.noted.sheets.HomeMoreOptionsBottomSheetModal;
import com.picassos.noted.sheets.NoteActionsBottomSheetModal;
import com.picassos.noted.sheets.PasswordBottomSheetModal;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class NotesFragment extends Fragment implements NotesListener,
        NotesActionListener, ChipCategoryListener {

    // BUNDLE
    Bundle bundle;

    SharedPref sharedPref;

    // REQUEST CODES
    private final int REQUEST_CODE_ADD_NOTE_OK = 1;
    private final int REQUEST_CODE_UPDATE_NOTE_OK = 2;
    private final int REQUEST_CODE_VIEW_NOTE_OK = 3;
    private final int REQUEST_CODE_UNLOCK_NOTE = 10;

    // View view
    View view;

    private RecyclerView notesRecyclerview;
    private List<Note> notes;
    private NotesAdapter notesAdapter;

    private List<Category> categories;
    private ChipCategoryAdapter categoryAdapter;

    private int noteClickedPosition = -1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_notes, container, false);

        // initialize bundle
        bundle = new Bundle();

        // search bar
        TextView searchBar = view.findViewById(R.id.search_bar);
        searchBar.setOnClickListener(v -> startActivity(new Intent(getContext(), SearchActivity.class)));

        // notes recyclerview
        notesRecyclerview = view.findViewById(R.id.notes_recyclerview);
        notesRecyclerview.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));

        // notes list, adapter
        notes = new ArrayList<>();
        notesAdapter = new NotesAdapter(getContext(),notes, this, this);
        notesRecyclerview.setAdapter(notesAdapter);

        requestNotes(REQUEST_CODE_VIEW_NOTE_OK, "note_id", false);

        // more options from MainActivity.class
        ((MainActivity) requireActivity()).moreOptions.setOnClickListener(v -> {
            HomeMoreOptionsBottomSheetModal homeMoreOptionsBottomSheetModal = new HomeMoreOptionsBottomSheetModal();
            homeMoreOptionsBottomSheetModal.setTargetFragment(this, 1);
            homeMoreOptionsBottomSheetModal.show(requireFragmentManager(), "TAG");
        });

        // fab, add notes
        CardView addNote = view.findViewById(R.id.add_note);
        addNote.setOnClickListener(v -> {
            startActivityForResult(new Intent(getContext(), AddNoteActivity.class), REQUEST_CODE_ADD_NOTE_OK);

        });

        // categories recyclerview
        RecyclerView categoriesRecyclerview = view.findViewById(R.id.categories_recyclerview);
        categoriesRecyclerview.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        // categories list, adapter
        categories = new ArrayList<>();

        categoryAdapter = new ChipCategoryAdapter(categories, this);
        categoriesRecyclerview.setAdapter(categoryAdapter);

        requestCategories();

        // manage categories
        view.findViewById(R.id.add_category).setOnClickListener(v -> {
            startActivity(new Intent(getContext(), EditCategoryActivity.class));
            ((MainActivity) requireContext()).finish();
        });

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        sharedPref = new SharedPref(getContext());
        super.onCreate(savedInstanceState);
    }

    /**
     * request notes from AsyncTask
     * @param requestCode for request code
     * @param sortBy for sort type
     * @param isDeleted for delete status
     */
    private void requestNotes(final int requestCode, final String sortBy, final boolean isDeleted) {

        @SuppressLint("StaticFieldLeak")
        class GetNotesTask extends AsyncTask<Void, Void, List<Note>> {

            @Override
            protected List<Note> doInBackground(Void... voids) {
                return APP_DATABASE.requestDatabase(getContext()).dao().request_notes(sortBy);
            }

            @Override
            protected void onPostExecute(List<Note> notes_inline) {
                super.onPostExecute(notes_inline);
                if (requestCode == REQUEST_CODE_VIEW_NOTE_OK) {
                    notes.addAll(notes_inline);
                    notesAdapter.notifyDataSetChanged();
                } else if (requestCode == REQUEST_CODE_ADD_NOTE_OK) {
                    notes.add(0, notes_inline.get(0));
                    notesAdapter.notifyItemInserted(0);
                    notesRecyclerview.smoothScrollToPosition(0);
                } else if (requestCode == REQUEST_CODE_UPDATE_NOTE_OK) {
                    notes.remove(noteClickedPosition);
                    if (isDeleted) {
                        notesAdapter.notifyItemRemoved(noteClickedPosition);
                    } else {
                        notes.add(noteClickedPosition, notes_inline.get(noteClickedPosition));
                        notesAdapter.notifyItemChanged(noteClickedPosition);
                    }
                }
                if (notesAdapter.getItemCount() == 0) {
                    view.findViewById(R.id.notes_empty_placeholder).setVisibility(View.VISIBLE);
                    view.findViewById(R.id.notes_empty_placeholder).setOnClickListener(v -> startActivityForResult(new Intent(getContext(), AddNoteActivity.class), REQUEST_CODE_ADD_NOTE_OK));
                } else {
                    view.findViewById(R.id.notes_empty_placeholder).setVisibility(View.GONE);
                }
            }

        }
        new GetNotesTask().execute();
    }

    private void requestCategories() {
        @SuppressLint("StaticFieldLeak")
        class GetCategoriesTask extends AsyncTask<Void, Void, List<Category>> {

            @Override
            protected List<Category> doInBackground(Void... voids) {
                return APP_DATABASE.requestDatabase(getContext()).dao().request_categories();
            }

            @Override
            protected void onPostExecute(List<Category> categories_inline) {
                super.onPostExecute(categories_inline);
                categories.addAll(categories_inline);
                categoryAdapter.notifyDataSetChanged();
            }

        }
        new GetCategoriesTask().execute();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_ADD_NOTE_OK && resultCode == RESULT_OK) {
            requestNotes(REQUEST_CODE_ADD_NOTE_OK, "note_id", false);
        } else if (requestCode == REQUEST_CODE_UPDATE_NOTE_OK && resultCode == RESULT_OK) {
            if (data != null) {
                requestNotes(REQUEST_CODE_UPDATE_NOTE_OK, "note_id", data.getBooleanExtra("is_note_removed", false));
            }
        } else if (requestCode == NoteActionsBottomSheetModal.REQUEST_DELETE_NOTE_CODE) {
            requestNotes(REQUEST_CODE_UPDATE_NOTE_OK, "note_id", true);
            Toast.makeText(getContext(), getString(R.string.note_moved_to_trash), Toast.LENGTH_SHORT).show();
        } else if (requestCode == NoteActionsBottomSheetModal.REQUEST_DISCARD_NOTE_CODE) {
            Toast.makeText(getContext(), getString(R.string.note_discarded), Toast.LENGTH_SHORT).show();
        } else if (resultCode == HomeMoreOptionsBottomSheetModal.CHOOSE_OPTION_REQUEST_CODE) {
            initializeToolbarSelector();
        } else if (resultCode == HomeMoreOptionsBottomSheetModal.CHOOSE_SORT_BY_A_TO_Z) {
            notes.clear();
            notesAdapter.notifyDataSetChanged();
            requestNotes(REQUEST_CODE_VIEW_NOTE_OK, "a_z", false);
        } else if (resultCode == HomeMoreOptionsBottomSheetModal.CHOOSE_SORT_BY_Z_TO_A) {
            notes.clear();
            notesAdapter.notifyDataSetChanged();
            requestNotes(REQUEST_CODE_VIEW_NOTE_OK, "z_a", false);
        } else if (resultCode == HomeMoreOptionsBottomSheetModal.CHOOSE_SORT_BY_DEFAULT) {
            notes.clear();
            notesAdapter.notifyDataSetChanged();
            requestNotes(REQUEST_CODE_VIEW_NOTE_OK, "note_id", false);
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

    @SuppressLint("SetTextI18n")
    private void initializeToolbarSelector() {
        ((MainActivity) requireActivity()).toolbarSelector.setVisibility(View.VISIBLE);
        // close toolbar selector
        ((MainActivity) requireActivity()).toolbarSelectorClose.setOnClickListener(v -> {
            ((MainActivity) requireActivity()).toolbarSelectorSelectedItems.setText("0 " + getString(R.string.selected));
            notesAdapter.clearSelection();
            ((MainActivity) requireActivity()).toolbarSelector.setVisibility(View.GONE);
        });
        // request delete selected notes
        ((MainActivity) requireActivity()).toolbarSelectorDeleteNotes.setOnClickListener(v -> {
            /*Dialog confirm_dialog = new Dialog(Objects.requireNonNull(getContext()));

            confirm_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

            confirm_dialog.setContentView(R.layout.popup_confirm);

            // enable dialog cancel
            confirm_dialog.setCancelable(true);
            confirm_dialog.setOnCancelListener(dialog -> confirm_dialog.dismiss());

            // confirm header
            TextView confirm_header = confirm_dialog.findViewById(R.id.confirm_header);
            confirm_header.setText(getString(R.string.delete_all_notes));

            // confirm text
            TextView confirm_text = confirm_dialog.findViewById(R.id.confirm_text);
            confirm_text.setText(getString(R.string.delete_all_notes_description));

            // confirm allow
            TextView confirm_allow = confirm_dialog.findViewById(R.id.confirm_allow);
            confirm_allow.setOnClickListener(v1 -> {
                List<Integer> selected_item_positions = notes_adapter.get_selected_items();
                for (int i = selected_item_positions.size() - 1; i >= 0; i--) {
                    notes_adapter.remove_data(selected_item_positions.get(i));
                }
                notes_adapter.notifyDataSetChanged();
                ((MainActivity) getActivity()).toolbar_selector_selected_items.setText("0 " + getString(R.string.selected));
                ((MainActivity) getActivity()).toolbar_selector.setVisibility(View.GONE);
                confirm_dialog.dismiss();
            });

            // confirm cancel
            TextView confirm_cancel = confirm_dialog.findViewById(R.id.confirm_deny);
            confirm_cancel.setOnClickListener(v2 -> confirm_dialog.dismiss());

            if (confirm_dialog.getWindow() != null) {
                confirm_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                confirm_dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
                confirm_dialog.getWindow().getAttributes().windowAnimations = R.style.DetailAnimation;
                Window window = confirm_dialog.getWindow();
                WindowManager.LayoutParams WLP = window.getAttributes();
                WLP.gravity = Gravity.BOTTOM;
                window.setAttributes(WLP);
            }

            confirm_dialog.show();*/
            Toast.makeText(getContext(), "This feature is under development.", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onNoteClicked(Note note, int position) {
        // default note clicked action
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onNoteAction(Note note, int position, boolean isSelected) {
        if (((MainActivity) requireActivity()).toolbarSelector.getVisibility() == View.VISIBLE) {
            requestToggleSelection(position);
        } else if (((MainActivity) requireActivity()).toolbarSelector.getVisibility() == View.GONE) {
            if (notesAdapter.getSelectedItemCount() > 0) {
                requestToggleSelection(position);
            } else {
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
                        passwordBottomSheetModal.setTargetFragment(NotesFragment.this, REQUEST_CODE_UNLOCK_NOTE);
                        passwordBottomSheetModal.show(requireFragmentManager(), "TAG");
                    } else {
                        Intent intent = new Intent(getContext(), AddNoteActivity.class);
                        intent.putExtra("modifier", true);
                        intent.putExtra("note", note);
                        startActivityForResult(intent, REQUEST_CODE_UPDATE_NOTE_OK);
                    }
                }
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

    @SuppressLint("SetTextI18n")
    private void requestToggleSelection(int position) {
        notesAdapter.toggleSelection(position);
        int count = notesAdapter.getSelectedItemCount();
        if (count == 0) {
            ((MainActivity) requireActivity()).toolbarSelectorSelectedItems.setText("0 " + getString(R.string.selected));
        } else {
            ((MainActivity) requireActivity()).toolbarSelectorSelectedItems.setText(count + " " + getString(R.string.selected));
        }
    }

    @Override
    public void onCategoryClicked(Category category, int position) {
        noteClickedPosition = position;

        Intent intent = new Intent(getContext(), FilteredNotesActivity.class);
        intent.putExtra("identifier", category.getCategory_id());
        intent.putExtra("title", category.getCategory_title());
        startActivity(intent);
    }
}