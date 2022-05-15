package com.picassos.noted.fragments.home;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.picassos.noted.R;
import com.picassos.noted.activities.ViewTodoActivity;
import com.picassos.noted.adapters.TodosAdapter;
import com.picassos.noted.databases.APP_DATABASE;
import com.picassos.noted.entities.Todo;
import com.picassos.noted.listeners.TodosListener;
import com.picassos.noted.sheets.AddTodoBottomSheetModal;
import com.picassos.noted.sheets.TodoActionsBottomSheetModal;
import com.picassos.noted.sheets.TodoListsBottomSheetModal;
import com.picassos.noted.sheets.TodoMoreOptionsBottomSheetModal;
import com.picassos.noted.utils.Helper;

import java.util.ArrayList;
import java.util.List;

public class TodosFragment extends Fragment implements TodosListener {

    // Bundle
    Bundle bundle;

    // View view
    View view;

    private List<Todo> todos;
    private TodosAdapter todosAdapter;

    // Request Codes
    private final static int REQUEST_ADD_TODO_CODE = 1;
    private final static int REQUEST_VIEW_TODO_CODE = 2;
    private final static int REQUEST_ACTION_TODO_CODE = 3;
    private final static int REQUEST_MORE_OPTIONS_CODE = 4;
    private final static int REQUEST_TODO_LISTS_CODE = 5;

    boolean navigationHide = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_todos, container, false);

        // Bundle
        bundle = new Bundle();

        // add to-do
        CardView addTodo = view.findViewById(R.id.add_todo);
        addTodo.setOnClickListener(v -> {
            AddTodoBottomSheetModal addTodoBottomSheetModal = new AddTodoBottomSheetModal();
            addTodoBottomSheetModal.setArguments(bundle);
            addTodoBottomSheetModal.setTargetFragment(this, REQUEST_ADD_TODO_CODE);
            addTodoBottomSheetModal.show(requireFragmentManager(), "TAG");
        });

        // more options
        CardView moreOptions = view.findViewById(R.id.more_options);
        moreOptions.setOnClickListener(v -> {
            TodoMoreOptionsBottomSheetModal todoMoreOptionsBottomSheetModal = new TodoMoreOptionsBottomSheetModal();
            todoMoreOptionsBottomSheetModal.setTargetFragment(this, REQUEST_MORE_OPTIONS_CODE);
            todoMoreOptionsBottomSheetModal.show(requireFragmentManager(), "TAG");
        });

        // to-do lists
        CardView todoLists = view.findViewById(R.id.todo_lists);
        todoLists.setOnClickListener(v -> {
            TodoListsBottomSheetModal todoListsBottomSheetModal = new TodoListsBottomSheetModal();
            todoListsBottomSheetModal.setTargetFragment(this, REQUEST_TODO_LISTS_CODE);
            todoListsBottomSheetModal.show(requireFragmentManager(), "TAG");
        });

        // to-dos recyclerview
        RecyclerView todosRecyclerview = view.findViewById(R.id.todos_recyclerview);
        todosRecyclerview.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        // to-dos list, adapter
        todos = new ArrayList<>();
        todosAdapter = new TodosAdapter(todos, this);
        todosRecyclerview.setAdapter(todosAdapter);

        requestTodos("todo_id");

        // nested scrollview
        NestedScrollView nestedScrollView = view.findViewById(R.id.nested_scrollview);
        nestedScrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (scrollY >= oldScrollY) { // down
                if (navigationHide) return;
                Helper.hide_bottom(moreOptions);
                Helper.hide_bottom(todoLists);
                navigationHide = true;
            } else {
                if (!navigationHide) return;
                Helper.show_bottom(moreOptions);
                Helper.show_bottom(todoLists);
                navigationHide = false;
            }
        });

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * request uncompleted to-dos from AsyncTask
     */
    private void requestTodos(String sortBy) {

        @SuppressLint("StaticFieldLeak")
        class GetTodosTask extends AsyncTask<Void, Void, List<Todo>> {

            @Override
            protected List<Todo> doInBackground(Void... voids) {
                return APP_DATABASE.requestDatabase(getContext()).dao().request_todos(sortBy);
            }

            @Override
            protected void onPostExecute(List<Todo> todos_inline) {
                super.onPostExecute(todos_inline);
                todos.addAll(todos_inline);
                todosAdapter.notifyDataSetChanged();
                if (todosAdapter.getItemCount() == 0) {
                    view.findViewById(R.id.todos_empty_placeholder).setVisibility(View.VISIBLE);
                    view.findViewById(R.id.todos_empty_placeholder).setOnClickListener(v -> {
                        AddTodoBottomSheetModal addTodoBottomSheetModal = new AddTodoBottomSheetModal();
                        addTodoBottomSheetModal.setArguments(bundle);
                        addTodoBottomSheetModal.setTargetFragment(TodosFragment.this, REQUEST_ADD_TODO_CODE);
                        addTodoBottomSheetModal.show(requireFragmentManager(), "TAG");
                    });
                } else {
                    view.findViewById(R.id.todos_empty_placeholder).setVisibility(View.GONE);
                }
            }

        }
        new GetTodosTask().execute();
    }

    @Override
    public void onTodoClicked(Todo todo, int position) {
        Intent intent = new Intent(getContext(), ViewTodoActivity.class);
        intent.putExtra("modifier", true);
        intent.putExtra("todo", todo);
        startActivityForResult(intent, REQUEST_VIEW_TODO_CODE);
    }

    @Override
    public void onTodoLongClicked(Todo todo, int position) {
        bundle.putSerializable("todo_data", todo);
        TodoActionsBottomSheetModal todoActionsBottomSheetModal = new TodoActionsBottomSheetModal();
        todoActionsBottomSheetModal.setArguments(bundle);
        todoActionsBottomSheetModal.setTargetFragment(this, REQUEST_ACTION_TODO_CODE);
        todoActionsBottomSheetModal.show(requireFragmentManager(), "TAG");
    }

    @Override
    public void onTodoStateCLicked(Todo todo, int position, boolean checked) {
        todo.setTodo_state(checked);
        requestUpdateTodo(todo);
    }

    private void requestUpdateTodo(Todo todo) {
        @SuppressLint("StaticFieldLeak")
        class UpdateTodoTask extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... voids) {
                APP_DATABASE.requestDatabase(getContext()).dao().request_insert_todo(todo);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
            }
        }

        new UpdateTodoTask().execute();
    }

    /**
     * refresh todos list
     */
    private void refreshTodos(String sortBy) {
        todos.clear();
        todosAdapter.notifyDataSetChanged();
        requestTodos(sortBy);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_ADD_TODO_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    refreshTodos("todo_id");
                }
                break;
            case REQUEST_VIEW_TODO_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    if (data != null) {
                        if (data.getIntExtra("requestCode", 0) == 1
                                || data.getIntExtra("requestCode", 0) == 2
                                || data.getIntExtra("requestCode", 0) == 3) {
                            refreshTodos("todo_id");
                        }
                    }
                }
                break;
            case REQUEST_ACTION_TODO_CODE:
                if (resultCode == TodoActionsBottomSheetModal.REQUEST_DELETE_TODO_CODE){
                    refreshTodos("todo_id");
                }
                break;
            case REQUEST_MORE_OPTIONS_CODE:
                switch (resultCode) {
                    case TodoMoreOptionsBottomSheetModal.REQUEST_DELETE_ALL_COMPLETED_TASKS_CODE:
                    case TodoMoreOptionsBottomSheetModal.CHOOSE_SORT_BY_DEFAULT:
                        refreshTodos("todo_id");
                        break;
                    case TodoMoreOptionsBottomSheetModal.CHOOSE_SORT_BY_A_TO_Z:
                        refreshTodos("a_z");
                        break;
                    case TodoMoreOptionsBottomSheetModal.CHOOSE_SORT_BY_Z_TO_A:
                        refreshTodos("z_a");
                        break;
                }
                break;
            case REQUEST_TODO_LISTS_CODE:
                if (resultCode == TodoListsBottomSheetModal.REQUEST_UPDATE_LIST_CODE) {
                    refreshTodos("todo_id");
                }
        }
    }
}