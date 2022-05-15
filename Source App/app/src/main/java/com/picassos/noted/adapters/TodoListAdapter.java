package com.picassos.noted.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.picassos.noted.R;
import com.picassos.noted.entities.TodosList;
import com.picassos.noted.listeners.TodoListListener;

import java.util.List;

public class TodoListAdapter extends RecyclerView.Adapter<TodoListAdapter.TodoViewHolder> {

    private final List<TodosList> todosLists;
    private final TodoListListener todoListListener;

    public TodoListAdapter(List<TodosList> todosLists, TodoListListener todoListListener) {
        this.todosLists = todosLists;
        this.todoListListener = todoListListener;
    }

    @NonNull
    @Override
    public TodoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TodoViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_todo_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull TodoViewHolder holder, int position) {
        holder.set_todo(todosLists.get(position));

        // to-do list on click
        holder.layout.setOnClickListener(v -> todoListListener.onTodoListClicked(todosLists.get(position), position));
    }

    @Override
    public int getItemCount() {
        return todosLists.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    static class TodoViewHolder extends RecyclerView.ViewHolder {

        LinearLayout layout;
        TextView todoListTitle;

        TodoViewHolder(@NonNull View todo_view) {
            super(todo_view);

            layout = todo_view.findViewById(R.id.item_todo_list_layout);
            todoListTitle = todo_view.findViewById(R.id.item_todo_list_title);
        }

        void set_todo(TodosList todosList) {
            todoListTitle.setText(todosList.getTodo_list_title());
        }

    }

}
