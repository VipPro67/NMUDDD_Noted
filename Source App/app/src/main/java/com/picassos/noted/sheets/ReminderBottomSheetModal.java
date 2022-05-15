package com.picassos.noted.sheets;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.picassos.noted.R;

import java.util.Objects;

public class ReminderBottomSheetModal extends BottomSheetDialogFragment {

    public interface OnAddListener {
        void onAddListener(int requestCode);
    }

    public interface OnRemoveListener {
        void onRemoveListener(int requestCode);
    }

    public static int REQUEST_ADD_REMINDER = 1;
    public static int REQUEST_REMOVE_REMINDER = 2;

    OnAddListener onAddListener;
    OnRemoveListener onRemoveListener;

    public ReminderBottomSheetModal() {

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            onAddListener = (OnAddListener) context;
        } catch (final ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement onAddListener");
        }

        try {
            onRemoveListener = (OnRemoveListener) context;
        } catch (final ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement onRemoveListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.reminder_bottom_sheet_modal, container, false);

        // add reminder
        Button addReminder = view.findViewById(R.id.add_reminder);
        addReminder.setOnClickListener(v -> {
            dismiss();
            onAddListener.onAddListener(REQUEST_ADD_REMINDER);
        });

        // remove reminder
        Button removeReminder = view.findViewById(R.id.remove_reminder);
        if (Objects.requireNonNull(requireArguments().getString("REMINDER_SET")).trim().isEmpty()) {
            removeReminder.setVisibility(View.GONE);
        } else {
            removeReminder.setVisibility(View.VISIBLE);
        }

        removeReminder.setOnClickListener(v -> {
            dismiss();
            onRemoveListener.onRemoveListener(REQUEST_REMOVE_REMINDER);
        });

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

}
