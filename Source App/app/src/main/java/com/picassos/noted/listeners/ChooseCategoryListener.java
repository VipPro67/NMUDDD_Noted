package com.picassos.noted.listeners;

import com.picassos.noted.entities.Category;

public interface ChooseCategoryListener {
    void onCategoryClicked(Category category, int position);
}
