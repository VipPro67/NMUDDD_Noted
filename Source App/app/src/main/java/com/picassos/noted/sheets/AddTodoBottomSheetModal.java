package com.picassos.noted.sheets;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.picassos.noted.R;
import com.picassos.noted.databases.APP_DATABASE;
import com.picassos.noted.entities.Todo;
import com.picassos.noted.sharedPreferences.SharedPref;
import com.picassos.noted.utils.Helper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class AddTodoBottomSheetModal extends BottomSheetDialogFragment {

    // shared preferences
    private SharedPref sharedPref;

    // add to-do button
    private TextView addTodo;

    // to-do title
    private EditText todoTitle;

    // to-do priority
    private CheckBox priority;

    public AddTodoBottomSheetModal() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_todo_bottom_sheet_modal, container, false);

        // to-do title
        todoTitle = view.findViewById(R.id.todo_title);
        todoTitle.addTextChangedListener(todoTitleTextWatcher);
        todoTitle.requestFocus();
        // show keyboard
        InputMethodManager inputMethodManager = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        Objects.requireNonNull(inputMethodManager).toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        // on editor action listener
        todoTitle.setOnEditorActionListener((v, actionId, event) -> {
            if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                if (!TextUtils.isEmpty(todoTitle.getText().toString())) {
                    saveTodo(todoTitle.getText().toString());
                    Objects.requireNonNull(inputMethodManager).toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                } else {
                    Toast.makeText(getContext(), getString(R.string.todo_title_required), Toast.LENGTH_SHORT).show();
                }
            }
            return false;
        });

        // to-do priority
        priority = view.findViewById(R.id.todo_priority);
        applyCheckboxTint(priority, priority.isChecked());
        priority.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // apply tint checkbox
            applyCheckboxTint(priority, isChecked);
        });

        // add to-do
        addTodo = view.findViewById(R.id.add_todo);
        addTodo.setOnClickListener(v -> {
            if (!todoTitle.getText().toString().trim().isEmpty()) {
                saveTodo(todoTitle.getText().toString());
            }
        });

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        sharedPref = new SharedPref(requireContext());
        super.onCreate(savedInstanceState);
    }

    /**
     * text watcher enables add button
     * when to-do is not empty
     */
    private final TextWatcher todoTitleTextWatcher = new TextWatcher() {
        @SuppressLint("SetTextI18n")
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            addTodo.setEnabled(!TextUtils.isEmpty(todoTitle.getText().toString()));
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    /**
     * request to save to-do
     * @param title for to-do title
     */
    private void saveTodo(String title) {
        final Todo todo = new Todo();

        todo.setTodo_title(title);
        todo.setTodo_state(false);
        todo.setTodo_created_at(new SimpleDateFormat("MM.dd.yyyy, HH:mm a", Locale.getDefault()).format(new Date()));
        todo.setTodo_priority(priority.isChecked());
        todo.setTodo_list(1);

        @SuppressLint("StaticFieldLeak")
        class SaveTodoTask extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... voids) {
                APP_DATABASE.requestDatabase(getContext()).dao().request_insert_todo(todo);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);

                InputMethodManager inputMethodManager = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                Objects.requireNonNull(inputMethodManager).toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

                Intent intent = new Intent();
                intent.putExtra("is_added", true);
                Objects.requireNonNull(getTargetFragment()).onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);

                dismiss();
            }
        }

        new SaveTodoTask().execute();
    }

    /**
     * apply tint to checkbox
     * @param checkBox for checkbox
     * @param isChecked for state is checked
     */
    private void applyCheckboxTint(CheckBox checkBox, boolean isChecked) {
        if (isChecked) {
            Helper.set_background_tint(getContext(), checkBox, R.color.color_danger);
        } else {
            if (sharedPref.loadNightModeState()) {
                Helper.set_background_tint(getContext(), checkBox, R.color.color_white);
            } else {
                Helper.set_background_tint(getContext(), checkBox, R.color.color_dark);
            }
        }
    }

}
