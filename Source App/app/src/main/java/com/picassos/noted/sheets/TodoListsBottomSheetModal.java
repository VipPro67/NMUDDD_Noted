package com.picassos.noted.sheets;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.picassos.noted.R;
import com.picassos.noted.activities.TodoCreateListActivity;
import com.picassos.noted.activities.TodoListActivity;
import com.picassos.noted.adapters.TodoListAdapter;
import com.picassos.noted.databases.APP_DATABASE;
import com.picassos.noted.entities.TodosList;
import com.picassos.noted.listeners.TodoListListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TodoListsBottomSheetModal extends BottomSheetDialogFragment implements TodoListListener {

    // Request Codes
    public static final int REQUEST_ADD_LIST_CODE = 1;
    public static final int REQUEST_UPDATE_LIST_CODE = 2;

    private List<TodosList> todosLists;
    private TodoListAdapter todoListAdapter;

    public TodoListsBottomSheetModal() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.todo_lists_bottom_sheet_modal, container, false);

        // to-dos recyclerview
        RecyclerView todosRecyclerview = view.findViewById(R.id.todo_list_recyclerview);
        todosRecyclerview.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        // to-dos list, adapter
        todosLists = new ArrayList<>();
        todoListAdapter = new TodoListAdapter(todosLists, this);
        todosRecyclerview.setAdapter(todoListAdapter);

        requestTodoList();

        // create a new list
        TextView createNewList = view.findViewById(R.id.create_list);
        createNewList.setOnClickListener(v -> startActivityForResult(new Intent(getContext(), TodoCreateListActivity.class), REQUEST_ADD_LIST_CODE));

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
        Intent intent = new Intent(getContext(), TodoListActivity.class);
        intent.putExtra("todo_list", todosList);
        startActivityForResult(intent, REQUEST_UPDATE_LIST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case TodoCreateListActivity.REQUEST_CREATE_LIST_CODE:
                if (data != null) {
                    if (data.getBooleanExtra("is_added", false)) {
                        todosLists.clear();
                        todoListAdapter.notifyDataSetChanged();
                        requestTodoList();
                    }
                }
                break;
            case REQUEST_UPDATE_LIST_CODE:
                if (data != null) {
                    if (data.getBooleanExtra("is_updated", false)) {
                        dismiss();
                        Intent intent = new Intent();
                        Objects.requireNonNull(getTargetFragment()).onActivityResult(getTargetRequestCode(), REQUEST_UPDATE_LIST_CODE, intent);
                    }
                }
        }
    }
}