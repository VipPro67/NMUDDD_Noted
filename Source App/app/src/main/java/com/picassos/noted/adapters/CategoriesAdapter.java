package com.picassos.noted.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.picassos.noted.R;
import com.picassos.noted.entities.Category;
import com.picassos.noted.listeners.CategoriesListener;

import java.util.List;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.CategoryViewHolder> {

    private final List<Category> categories;
    private final CategoriesListener categoriesListener;

    public CategoriesAdapter(List<Category> categories, CategoriesListener categoriesListener) {
        this.categories = categories;
        this.categoriesListener = categoriesListener;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CategoryViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        holder.set_category(categories.get(position));

        // delete category on click
        holder.deleteCategory.setOnClickListener(v -> categoriesListener.onCategoryDeleteClicked(categories.get(position), position));

        // edit category on click
        holder.editCategory.setOnClickListener(v -> categoriesListener.onCategoryEditClicked(categories.get(position), position));
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout layout;

        ImageView deleteCategory;
        ImageView editCategory;

        TextView categoryTitle;

        CategoryViewHolder(@NonNull View category_view) {
            super(category_view);

            layout = category_view.findViewById(R.id.item_category_layout);
            deleteCategory = category_view.findViewById(R.id.item_category_delete);
            editCategory = category_view.findViewById(R.id.item_category_edit);
            categoryTitle = category_view.findViewById(R.id.item_category_title);
        }

        void set_category(Category category) {
            categoryTitle.setText(category.getCategory_title());

            if (category.isCategory_is_primary()) {
                deleteCategory.setVisibility(View.GONE);
                editCategory.setVisibility(View.GONE);
            }
        }

    }

}
