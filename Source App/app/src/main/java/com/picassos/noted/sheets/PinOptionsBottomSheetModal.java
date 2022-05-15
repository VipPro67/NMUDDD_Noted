package com.picassos.noted.sheets;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.picassos.noted.R;
import com.picassos.noted.activities.CreatePinActivity;
import com.picassos.noted.databases.APP_DATABASE;
import com.picassos.noted.sharedPreferences.SharedPref;

public class PinOptionsBottomSheetModal extends BottomSheetDialogFragment {

    private SharedPref sharedPref;

    public interface OnRemoveListener {
        void onRemoveListener(int requestCode);
    }

    OnRemoveListener onRemoveListener;

    public static final int REQUEST_REMOVE_PIN_CODE = 1;
    public static final int REQUEST_UPDATE_PIN_CODE = 2;

    public PinOptionsBottomSheetModal() {

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            onRemoveListener = (OnRemoveListener) context;
        } catch (final ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement onRemoveListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.pin_options_bottom_sheet_modal, container, false);

        // update, remove pin
        Button updatePin = view.findViewById(R.id.update_pin);
        Button removePin = view.findViewById(R.id.remove_pin);

        // check if pin code is not set
        if (sharedPref.loadNotePinCode() == 0) {
            removePin.setVisibility(View.GONE);
        } else {
            removePin.setVisibility(View.VISIBLE);
            removePin.setOnClickListener(v -> requestPinCodeDialog(REQUEST_REMOVE_PIN_CODE));
        }

        // update pin
        updatePin.setOnClickListener(v -> {
            if (sharedPref.loadNotePinCode() == 0) {
                startActivity(new Intent(getContext(), CreatePinActivity.class));
                dismiss();
            } else {
                requestPinCodeDialog(REQUEST_UPDATE_PIN_CODE);
            }
        });

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        sharedPref = new SharedPref(requireContext());
        super.onCreate(savedInstanceState);
    }

    /**
     * request pin code dialog
     * to verify preset pin code
     */
    private void requestPinCodeDialog(int request) {
        Dialog pinCodeDialog = new Dialog(requireContext());

        pinCodeDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        pinCodeDialog.setContentView(R.layout.pin_code_dialog);

        // enable dialog cancel
        pinCodeDialog.setCancelable(true);
        pinCodeDialog.setOnCancelListener(dialog -> pinCodeDialog.dismiss());

        // pin code field
        TextView length = pinCodeDialog.findViewById(R.id.length);
        EditText pinCode = pinCodeDialog.findViewById(R.id.pin_code);
        pinCode.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        pinCode.setTypeface(Typeface.create("sans-serif", Typeface.BOLD));
        pinCode.addTextChangedListener(new TextWatcher() {
            @SuppressLint("SetTextI18n")
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                length.setText(s.length() + "/8");
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Set length while text changing
                length.setText(s.length() + "/8");

                if (!TextUtils.isEmpty(pinCode.getText().toString())) {
                    if (sharedPref.loadNotePinCode() == Integer.parseInt(pinCode.getText().toString())) {
                        switch (request) {
                            case REQUEST_REMOVE_PIN_CODE:
                                // reset, remove note pin code
                                sharedPref.setNotePinCode(0);
                                requestRemovePin();
                                break;
                            case REQUEST_UPDATE_PIN_CODE:
                                // update pin code
                                startActivity(new Intent(getContext(), CreatePinActivity.class));
                                break;
                        }

                        pinCodeDialog.dismiss();
                        dismiss();
                    }
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (pinCodeDialog.getWindow() != null) {
            pinCodeDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            pinCodeDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        }

        pinCodeDialog.show();
    }

    /**
     * request remove pin code
     * from all notes, this hides
     * the lock icon from notes
     */
    private void requestRemovePin() {
        @SuppressLint("StaticFieldLeak")
        class UpdateNotesTask extends AsyncTask<Void, Void, Integer> {

            @Override
            protected Integer doInBackground(Void... voids) {
                return APP_DATABASE.requestDatabase(getContext()).dao().request_remove_lock();
            }

            @Override
            protected void onPostExecute(Integer notes_inline) {
                super.onPostExecute(notes_inline);
                onRemoveListener.onRemoveListener(REQUEST_REMOVE_PIN_CODE);
            }

        }
        new UpdateNotesTask().execute();
    }

}
