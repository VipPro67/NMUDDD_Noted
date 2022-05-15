package com.picassos.noted.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.picassos.noted.R;
import com.picassos.noted.adapters.CategoriesAdapter;
import com.picassos.noted.databases.APP_DATABASE;
import com.picassos.noted.entities.Category;
import com.picassos.noted.listeners.CategoriesListener;
import com.picassos.noted.sheets.AddCategoryBottomSheetModal;
import com.picassos.noted.sheets.EditCategoryBottomSheetModal;
import com.picassos.noted.utils.Helper;

import java.util.ArrayList;
import java.util.List;

public class EditCategoryActivity extends AppCompatActivity implements CategoriesListener, AddCategoryBottomSheetModal.OnAddListener, EditCategoryBottomSheetModal.OnEditListener {

    // bundle
    Bundle bundle;

    private List<Category> categories;
    private CategoriesAdapter categoriesAdapter;

    private int categoryClickedPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // OPTIONS
        Helper.dark_mode(this);
        Helper.fullscreen_mode(this);
        Helper.screen_state(this);

        setContentView(R.layout.activity_edit_category);

        // Bundle
        bundle = new Bundle();

        // return back and finish activity
        ImageView goBack = findViewById(R.id.go_back);
        goBack.setOnClickListener(v -> {
            startActivity(new Intent(EditCategoryActivity.this, MainActivity.class));
            finish();
        });

        // add category
        Button addCategory = findViewById(R.id.add_category);
        addCategory.setOnClickListener(v -> {
            AddCategoryBottomSheetModal addCategoryBottomSheetModal = new AddCategoryBottomSheetModal();
            addCategoryBottomSheetModal.show(getSupportFragmentManager(), "TAG");
        });

        // categories recyclerview
        RecyclerView categoriesRecyclerview = findViewById(R.id.categories_recyclerview);
        categoriesRecyclerview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        // categories list, adapter
        categories = new ArrayList<>();
        categoriesAdapter = new CategoriesAdapter(categories, this);
        categoriesRecyclerview.setAdapter(categoriesAdapter);

        requestCategories();
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
                return APP_DATABASE.requestDatabase(getApplicationContext()).dao().request_categories();
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

    /**
     * request to delete a preset category
     * @param category for class
     */
    private void requestDeleteCategory(Category category) {
        @SuppressLint("StaticFieldLeak")
        class DeleteCategoryTask extends AsyncTask<Void, Void, Void> {
            @Override
            protected Void doInBackground(Void... voids) {
                APP_DATABASE.requestDatabase(getApplicationContext()).dao().request_delete_category(category);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                categories.remove(categoryClickedPosition);
                categoriesAdapter.notifyItemRemoved(categoryClickedPosition);

                Toast.makeText(EditCategoryActivity.this, getString(R.string.category_deleted), Toast.LENGTH_SHORT).show();
            }
        }

        new DeleteCategoryTask().execute();
    }

    @Override
    public void onCategoryDeleteClicked(Category category, int position) {
        categoryClickedPosition = position;

        // request delete category
        requestDeleteCategory(category);
    }

    @Override
    public void onCategoryEditClicked(Category category, int position) {
        bundle.putSerializable("preset_category", category);
        categoryClickedPosition = position;

        // request edit category
        EditCategoryBottomSheetModal editCategoryBottomSheetModal = new EditCategoryBottomSheetModal();
        editCategoryBottomSheetModal.setArguments(bundle);
        editCategoryBottomSheetModal.show(getSupportFragmentManager(), "TAG");
    }

    @Override
    public void onAddListener(int requestCode) {
        int REQUEST_ADD_CATEGORY_CODE = 3;

        if (requestCode == REQUEST_ADD_CATEGORY_CODE) {
            categories.clear();
            categoriesAdapter.notifyDataSetChanged();
            requestCategories();

            Toast.makeText(this, getString(R.string.category_added), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onEditListener(int requestCode) {
        int REQUEST_EDIT_CATEGORY_CODE = 4;

        if (requestCode == REQUEST_EDIT_CATEGORY_CODE) {
            categories.clear();
            categoriesAdapter.notifyDataSetChanged();
            requestCategories();

            Toast.makeText(this, getString(R.string.category_edited), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(EditCategoryActivity.this, MainActivity.class));
        finish();
    }
}