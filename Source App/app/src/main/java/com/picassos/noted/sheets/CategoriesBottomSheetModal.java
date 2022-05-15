package com.picassos.noted.sheets;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.picassos.noted.R;
import com.picassos.noted.activities.EditCategoryActivity;
import com.picassos.noted.adapters.ChooseCategoryAdapter;
import com.picassos.noted.databases.APP_DATABASE;
import com.picassos.noted.entities.Category;
import com.picassos.noted.listeners.ChooseCategoryListener;

import java.util.ArrayList;
import java.util.List;

public class CategoriesBottomSheetModal extends BottomSheetDialogFragment implements ChooseCategoryListener {

    public interface OnChooseListener {
        void onChooseListener(int requestCode, Category category);
    }

    OnChooseListener onChooseListener;

    private List<Category> categories;
    private ChooseCategoryAdapter categoriesAdapter;

    public CategoriesBottomSheetModal() {

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            onChooseListener = (OnChooseListener) context;
        } catch (final ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnChooseListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.categories_bottom_sheet_modal, container, false);

        RecyclerView categoriesRecyclerview = view.findViewById(R.id.categories_recyclerview);
        categoriesRecyclerview.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        // add new category
        view.findViewById(R.id.add_category).setOnClickListener(v -> {
            startActivity(new Intent(getContext(), EditCategoryActivity.class));
            dismiss();
        });

        // categories list, adapter
        categories = new ArrayList<>();
        categoriesAdapter = new ChooseCategoryAdapter(categories, this);
        categoriesRecyclerview.setAdapter(categoriesAdapter);

        requestCategories();

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * request to show categories
     * inside edit category activity
     */
    private void requestCategories() {
        @SuppressLint("StaticFieldLeak")
        class GetCategoriesTask extends AsyncTask<Void, Void, List<Category>> {

            @Override
            protected List<Category> doInBackground(Void... voids) {
                return APP_DATABASE.requestDatabase(getContext()).dao().request_categories();
            }

            @Override
            protected void onPostExecute(List<Category> categories_inline) {
                super.onPostExecute(categories_inline);
                categories.addAll(categories_inline);
                categoriesAdapter.notifyDataSetChanged();
            }

        }
        new GetCategoriesTask().execute();
    }

    @Override
    public void onCategoryClicked(Category category, int position) {
        int REQUEST_CHOOSE_CATEGORY_CODE = 5;
        onChooseListener.onChooseListener(REQUEST_CHOOSE_CATEGORY_CODE, category);

        dismiss();
    }
}
