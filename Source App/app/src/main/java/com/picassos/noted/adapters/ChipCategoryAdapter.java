package com.picassos.noted.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.picassos.noted.R;
import com.picassos.noted.entities.Category;
import com.picassos.noted.listeners.ChipCategoryListener;

import java.util.List;

public class ChipCategoryAdapter extends RecyclerView.Adapter<ChipCategoryAdapter.CategoryViewHolder> {

    private final List<Category> categories;
    private final ChipCategoryListener chipCategoryListener;

    public ChipCategoryAdapter(List<Category> categories, ChipCategoryListener chipCategoryListener) {
        this.categories = categories;
        this.chipCategoryListener = chipCategoryListener;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CategoryViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category_filter, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        holder.set_category(categories.get(position));
        holder.layout.setOnClickListener(v -> chipCategoryListener.onCategoryClicked(categories.get(position), position));
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

        LinearLayout layout;
        TextView categoryTitle;

        CategoryViewHolder(@NonNull View category_view) {
            super(category_view);

            layout = category_view.findViewById(R.id.item_category_layout);
            categoryTitle = category_view.findViewById(R.id.item_category_title);
        }

        void set_category(Category category) {
            categoryTitle.setText(category.getCategory_title());
        }

    }

}
