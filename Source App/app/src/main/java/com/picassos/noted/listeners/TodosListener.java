package com.picassos.noted.listeners;

import com.picassos.noted.entities.Todo;

public interface TodosListener {
    void onTodoClicked(Todo todo, int position);

    void onTodoLongClicked(Todo todo, int position);

    void onTodoStateCLicked(Todo todo, int position, boolean checked);
}
