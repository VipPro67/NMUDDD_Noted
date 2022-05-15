package com.picassos.noted.adapters;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.picassos.noted.R;
import com.picassos.noted.entities.Todo;
import com.picassos.noted.listeners.TodosListener;

import java.util.List;

public class TodosAdapter extends RecyclerView.Adapter<TodosAdapter.TodoViewHolder> {

    private final List<Todo> todos;
    private final TodosListener todosListener;

    public TodosAdapter(List<Todo> todos, TodosListener todosListener) {
        this.todos = todos;
        this.todosListener = todosListener;
    }

    @NonNull
    @Override
    public TodoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TodoViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_todo, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull TodoViewHolder holder, int position) {
        holder.set_todo(todos.get(position));

        // to-do on click
        holder.layout.setOnClickListener(v -> todosListener.onTodoClicked(todos.get(position), position));

        // to-do on long click
        holder.layout.setOnLongClickListener(v -> {
            todosListener.onTodoLongClicked(todos.get(position), position);
            return true;
        });

        // to-do state on click
        holder.todoState.setOnCheckedChangeListener((buttonView, isChecked) -> todosListener.onTodoStateCLicked(todos.get(position), position, isChecked));
    }

    @Override
    public int getItemCount() {
        return todos.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    static class TodoViewHolder extends RecyclerView.ViewHolder {

        CardView layout;

        TextView todoTitle, todoDetails;
        CheckBox todoState;
        ImageView todoImportant;

        TodoViewHolder(@NonNull View todo_view) {
            super(todo_view);

            layout = todo_view.findViewById(R.id.item_todo_layout);
            todoTitle = todo_view.findViewById(R.id.item_todo_title);
            todoDetails = todo_view.findViewById(R.id.item_todo_details);
            todoState = todo_view.findViewById(R.id.item_todo_checkbox);
            todoImportant = todo_view.findViewById(R.id.item_todo_important);
        }

        void set_todo(Todo todo) {
            todoTitle.setText(todo.getTodo_title());
            if (TextUtils.isEmpty(todo.getTodo_details())) {
                todoDetails.setVisibility(View.GONE);
            } else {
                todoDetails.setVisibility(View.VISIBLE);
                todoDetails.setText(todo.getTodo_details());
            }
            todoState.setChecked(todo.isTodo_state());
            if (todo.isTodo_priority()) {
                todoImportant.setVisibility(View.VISIBLE);
            } else {
                todoImportant.setVisibility(View.GONE);
            }
        }

    }

}
