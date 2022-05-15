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

public class EditCategoryBottomSheetModal extends BottomSheetDialogFragment {

    public interface OnEditListener {
        void onEditListener(int requestCode);
    }

    // edit category button
    private Button editCategory;

    // category title
    private EditText categoryTitle;

    // preset category
    private Category presetCategory;

    OnEditListener onEditListener;

    private final int REQUEST_EDIT_CATEGORY_CODE = 4;

    public EditCategoryBottomSheetModal() {

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            onEditListener = (OnEditListener) context;
        } catch (final ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement onEditListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.edit_category_bottom_sheet_modal, container, false);

        // preset category
        presetCategory = (Category) requireArguments().getSerializable("preset_category");

        // a preset category title
        categoryTitle = view.findViewById(R.id.category_title);
        categoryTitle.addTextChangedListener(categoryTitleTextWatcher);

        // edit category
        editCategory = view.findViewById(R.id.edit_category);
        editCategory.setOnClickListener(v -> {
            if (!categoryTitle.getText().toString().trim().isEmpty()) {
                save_category(categoryTitle.getText().toString());
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
    private final TextWatcher categoryTitleTextWatcher = new TextWatcher() {
        @SuppressLint("SetTextI18n")
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            editCategory.setEnabled(!TextUtils.isEmpty(categoryTitle.getText().toString()));
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    /**
     * request to save the preset category
     * @param title for category title
     */
    private void save_category(String title) {
        final Category category = new Category();

        if (presetCategory != null) {
            category.setCategory_id(presetCategory.getCategory_id());
        }

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

                onEditListener.onEditListener(REQUEST_EDIT_CATEGORY_CODE);

                dismiss();
            }
        }

        new SaveCategoryTask().execute();
    }

}
