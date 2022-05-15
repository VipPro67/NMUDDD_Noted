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
import com.picassos.noted.entities.Todo;

import java.util.Objects;

public class TodoActionsBottomSheetModal extends BottomSheetDialogFragment {

    public static int REQUEST_DELETE_TODO_CODE = -1;

    private Todo todo;

    public TodoActionsBottomSheetModal() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.todo_actions_bottom_sheet_modal, container, false);

        todo = (Todo) requireArguments().getSerializable("todo_data");

        // delete to-do
        Button deleteTodo = view.findViewById(R.id.todo_delete);
        deleteTodo.setOnClickListener(v -> requestDeleteNote(todo));

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * request to delete a preset to-do
     * @param todo for class
     */
    private void requestDeleteNote(Todo todo) {
        if (todo != null) {
            @SuppressLint("StaticFieldLeak")
            class DeleteNoteTask extends AsyncTask<Void, Void, Void> {
                @Override
                protected Void doInBackground(Void... voids) {
                    APP_DATABASE.requestDatabase(getContext()).dao().request_delete_todo(todo);
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);
                    sendResult(REQUEST_DELETE_TODO_CODE);
                }
            }

            new DeleteNoteTask().execute();
        }
    }

    private void sendResult(int REQUEST_CODE) {
        Intent intent = new Intent();
        Objects.requireNonNull(getTargetFragment()).onActivityResult(getTargetRequestCode(), REQUEST_CODE, intent);
        dismiss();
    }
}
