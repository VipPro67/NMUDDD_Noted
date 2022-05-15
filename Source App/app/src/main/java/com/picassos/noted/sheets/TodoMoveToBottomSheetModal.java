package com.picassos.noted.sheets;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.picassos.noted.R;
import com.picassos.noted.adapters.TodoListAdapter;
import com.picassos.noted.databases.APP_DATABASE;
import com.picassos.noted.entities.TodosList;
import com.picassos.noted.listeners.TodoListListener;

import java.util.ArrayList;
import java.util.List;

public class TodoMoveToBottomSheetModal extends BottomSheetDialogFragment implements TodoListListener {

    public interface OnMoveListener {
        void onMoveListener(int requestCode, int identifier);
    }

    // Request Codes
    public static int REQUEST_MOVE_TASK_CODE = 1;

    OnMoveListener onMoveListener;

    private List<TodosList> todosLists;
    private TodoListAdapter todoListAdapter;

    public TodoMoveToBottomSheetModal() {

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            onMoveListener = (OnMoveListener) context;
        } catch (final ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement onMoveListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.todo_move_to_bottom_sheet_modal, container, false);

        // to-dos recyclerview
        RecyclerView todosRecyclerview = view.findViewById(R.id.todo_list_recyclerview);
        todosRecyclerview.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        // to-dos list, adapter
        todosLists = new ArrayList<>();
        todoListAdapter = new TodoListAdapter(todosLists, this);
        todosRecyclerview.setAdapter(todoListAdapter);

        requestTodoList();

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * request to-dos list from AsyncTask
     */
    private void requestTodoList() {

        @SuppressLint("StaticFieldLeak")
        class GetTodoListsTask extends AsyncTask<Void, Void, List<TodosList>> {

            @Override
            protected List<TodosList> doInBackground(Void... voids) {
                return APP_DATABASE.requestDatabase(getContext()).dao().request_todos_lists();
            }

            @Override
            protected void onPostExecute(List<TodosList> todo_list_inline) {
                super.onPostExecute(todo_list_inline);
                todosLists.addAll(todo_list_inline);
                todoListAdapter.notifyDataSetChanged();
            }

        }
        new GetTodoListsTask().execute();
    }

    @Override
    public void onTodoListClicked(TodosList todosList, int position) {
        onMoveListener.onMoveListener(REQUEST_MOVE_TASK_CODE, todosList.getTodo_list_identifier());
        Toast.makeText(getContext(), getString(R.string.todo_moved_to) + " " + todosList.getTodo_list_title(), Toast.LENGTH_SHORT).show();
        dismiss();
    }
}