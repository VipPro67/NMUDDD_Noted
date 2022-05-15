package com.picassos.noted.listeners;

import com.picassos.noted.entities.TodosList;

public interface TodoListListener {
    void onTodoListClicked(TodosList todosList, int position);
}
