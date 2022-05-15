package com.picassos.noted.listeners;

import com.picassos.noted.entities.Category;

public interface CategoriesListener {
    void onCategoryDeleteClicked(Category category, int position);

    void onCategoryEditClicked(Category category, int position);
}
