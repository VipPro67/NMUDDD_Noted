package com.picassos.noted.sheets;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.picassos.noted.R;
import com.picassos.noted.databases.APP_DATABASE;

import java.util.Objects;

public class TodoMoreOptionsBottomSheetModal extends BottomSheetDialogFragment {

    // Request Codes
    public static final int REQUEST_DELETE_ALL_COMPLETED_TASKS_CODE = 1;
    public static final int CHOOSE_SORT_BY_A_TO_Z = 2;
    public static final int CHOOSE_SORT_BY_Z_TO_A = 3;
    public static final int CHOOSE_SORT_BY_DEFAULT = 4;

    private int completedTasks;

    public TodoMoreOptionsBottomSheetModal() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.todo_more_options_bottom_sheet_modal, container, false);

        // completed tasks count
        completedTasks = APP_DATABASE.requestDatabase(getContext()).dao().request_completed_todos_count();

        // sort a to z
        LinearLayout sortAToZ = view.findViewById(R.id.sort_a_to_z);
        sortAToZ.setOnClickListener(v -> {
            Dialog sortByDialog = new Dialog(requireContext());

            sortByDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

            sortByDialog.setContentView(R.layout.popup_sort_by);

            // enable dialog cancel
            sortByDialog.setCancelable(true);
            sortByDialog.setOnCancelListener(dialog -> sortByDialog.dismiss());

            // sort by default
            LinearLayout sortDefault = sortByDialog.findViewById(R.id.sort_by_default);
            sortDefault.setOnClickListener(v1 -> {
                sendResult(CHOOSE_SORT_BY_DEFAULT);
                sortByDialog.dismiss();
            });

            // sort by name a - z
            LinearLayout aToZ = sortByDialog.findViewById(R.id.sort_a_to_z);
            aToZ.setOnClickListener(v2 -> {
                sendResult(CHOOSE_SORT_BY_A_TO_Z);
                sortByDialog.dismiss();
            });

            // sort by name z - a
            LinearLayout zToA = sortByDialog.findViewById(R.id.sort_z_to_a);
            zToA.setOnClickListener(v3 -> {
                sendResult(CHOOSE_SORT_BY_Z_TO_A);
                sortByDialog.dismiss();
            });

            // confirm cancel
            LinearLayout confirmCancel = sortByDialog.findViewById(R.id.confirm_deny);
            confirmCancel.setOnClickListener(v4 -> sortByDialog.dismiss());

            if (sortByDialog.getWindow() != null) {
                sortByDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                sortByDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                sortByDialog.getWindow().getAttributes().windowAnimations = R.style.DetailAnimationFade;
            }

            sortByDialog.show();

            dismiss();
        });

        // delete all completed tasks
        LinearLayout deleteAllCompletedTasks = view.findViewById(R.id.delete_all_completed_tasks);
        if (completedTasks == 0) {
            deleteAllCompletedTasks.setVisibility(View.GONE);
        } else {
            deleteAllCompletedTasks.setVisibility(View.VISIBLE);
        }
        deleteAllCompletedTasks.setOnClickListener(v -> showDeleteAllCompletedTasks());

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * show delete all
     * completed to-dos, tasks
     */
    @SuppressLint("SetTextI18n")
    private void showDeleteAllCompletedTasks() {
        Dialog confirmDialog = new Dialog(requireContext());

        confirmDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        confirmDialog.setContentView(R.layout.popup_confirm);

        // enable dialog cancel
        confirmDialog.setCancelable(true);
        confirmDialog.setOnCancelListener(dialog -> confirmDialog.dismiss());

        // confirm header
        TextView confirmHeader = confirmDialog.findViewById(R.id.confirm_header);
        confirmHeader.setText(getString(R.string.delete_all_completed_tasks_header));

        // confirm text
        TextView confirmText = confirmDialog.findViewById(R.id.confirm_text);
        confirmText.setText(completedTasks + " " + getString(R.string.delete_all_completed_tasks_description));

        // confirm allow
        TextView confirmAllow = confirmDialog.findViewById(R.id.confirm_allow);
        confirmAllow.setText(getString(R.string.delete));
        confirmAllow.setOnClickListener(v1 -> {
            requestDeleteAllCompletedTasks();
            confirmDialog.dismiss();
        });

        // confirm cancel
        TextView confirmCancel = confirmDialog.findViewById(R.id.confirm_deny);
        confirmCancel.setOnClickListener(v2 -> confirmDialog.dismiss());

        if (confirmDialog.getWindow() != null) {
            confirmDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            confirmDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        }

        confirmDialog.show();
    }

    /**
     * request delete all
     * completed tasks, to-dos
     */
    private void requestDeleteAllCompletedTasks() {
        @SuppressLint("StaticFieldLeak")
        class DeleteTodosTask extends AsyncTask<Void, Void, Void> {
            @Override
            protected Void doInBackground(Void... voids) {
                APP_DATABASE.requestDatabase(getContext()).dao().request_delete_completed_todos();
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                sendResult(REQUEST_DELETE_ALL_COMPLETED_TASKS_CODE);
                dismiss();
            }
        }

        new DeleteTodosTask().execute();
    }

    private void sendResult(int REQUEST_CODE) {
        Intent intent = new Intent();
        Objects.requireNonNull(getTargetFragment()).onActivityResult(getTargetRequestCode(), TodoMoreOptionsBottomSheetModal.REQUEST_DELETE_ALL_COMPLETED_TASKS_CODE, intent);
        dismiss();
    }
}