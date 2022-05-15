package com.picassos.noted.sheets;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.picassos.noted.R;
import com.picassos.noted.entities.Note;
import com.picassos.noted.sharedPreferences.SharedPref;

import java.util.Objects;

public class PasswordBottomSheetModal extends BottomSheetDialogFragment {

    private SharedPref sharedPref;

    private Note note;
    private EditText pinCode;

    public PasswordBottomSheetModal() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.password_bottom_sheet_modal, container, false);

        // get note data
        if (getArguments() != null) {
            note = (Note) requireArguments().getSerializable("data");
        }

        // pin code
        pinCode = view.findViewById(R.id.pin_code);
        pinCode.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        pinCode.setTypeface(Typeface.create("sans-serif", Typeface.BOLD));
        pinCode.addTextChangedListener(pinCodeTextWatcher);

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        sharedPref = new SharedPref(requireContext());
        super.onCreate(savedInstanceState);
    }

    /**
     * text watcher checks password
     */
    private final TextWatcher pinCodeTextWatcher = new TextWatcher() {
        @SuppressLint("SetTextI18n")
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (!TextUtils.isEmpty(pinCode.getText().toString())) {
                if (sharedPref.loadNotePinCode() == Integer.parseInt(pinCode.getText().toString())) {
                    Intent intent = new Intent();
                    if (note != null) {
                        intent.putExtra("data", note);
                    }
                    Objects.requireNonNull(getTargetFragment()).onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
                    dismiss();
                }
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

}
