package com.picassos.noted.sheets;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.picassos.noted.R;
import com.picassos.noted.databases.APP_DATABASE;
import com.picassos.noted.entities.Category;

public class AddCategoryBottomSheetModal extends BottomSheetDialogFragment {

    public interface OnAddListener {
        void onAddListener(int requestCode);
    }

    // add category button
    private Button addCategory;

    // category title
    private EditText categoryTitle;

    OnAddListener onAddListener;

    private final int REQUEST_ADD_CATEGORY_CODE = 3;

    public AddCategoryBottomSheetModal() {

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            onAddListener = (OnAddListener) context;
        } catch (final ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement onAddListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_category_bottom_sheet_modal, container, false);

        // category title
        categoryTitle = view.findViewById(R.id.category_title);
        categoryTitle.addTextChangedListener(category_title_text_watcher);

        // add category
        addCategory = view.findViewById(R.id.add_category);
        addCategory.setOnClickListener(v -> {
            if (!categoryTitle.getText().toString().trim().isEmpty()) {
                saveCategory(categoryTitle.getText().toString());
            }
        });

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * text watcher enables add button
     * when note category is not empty
     */
    private final TextWatcher category_title_text_watcher = new TextWatcher() {
        @SuppressLint("SetTextI18n")
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            addCategory.setEnabled(!TextUtils.isEmpty(categoryTitle.getText().toString()));
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    /**
     * request to save category
     * @param title for category title
     */
    private void saveCategory(String title) {
        final Category category = new Category();

        category.setCategory_title(title);
        category.setCategory_is_primary(false);

        @SuppressLint("StaticFieldLeak")
        class SaveCategoryTask extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... voids) {
                APP_DATABASE.requestDatabase(getContext()).dao().request_insert_category(category);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);

                onAddListener.onAddListener(REQUEST_ADD_CATEGORY_CODE);

                dismiss();
            }
        }

        new SaveCategoryTask().execute();
    }

}
