package com.picassos.noted.sheets;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.picassos.noted.R;
import java.util.Objects;

public class HomeMoreOptionsBottomSheetModal extends BottomSheetDialogFragment {

    public static final int CHOOSE_OPTION_REQUEST_CODE = 1;
    public static final int CHOOSE_SORT_BY_A_TO_Z = 2;
    public static final int CHOOSE_SORT_BY_Z_TO_A = 3;
    public static final int CHOOSE_SORT_BY_DEFAULT = 4;

    public HomeMoreOptionsBottomSheetModal() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_more_options_bottom_sheet_modal, container, false);

        // select notes
        LinearLayout selectNotes = view.findViewById(R.id.select_notes);
        selectNotes.setOnClickListener(v -> send_result(CHOOSE_OPTION_REQUEST_CODE));

        // sort notes
        LinearLayout sortNotes = view.findViewById(R.id.sort_notes);
        sortNotes.setOnClickListener(v -> {
            Dialog sortByDialog = new Dialog(getContext());

            sortByDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

            sortByDialog.setContentView(R.layout.popup_sort_by);

            // enable dialog cancel
            sortByDialog.setCancelable(true);
            sortByDialog.setOnCancelListener(dialog -> sortByDialog.dismiss());

            // sort by default
            LinearLayout sortDefault = sortByDialog.findViewById(R.id.sort_by_default);
            sortDefault.setOnClickListener(v1 -> {
                send_result(CHOOSE_SORT_BY_DEFAULT);
                sortByDialog.dismiss();
            });

            // sort by name a - z
            LinearLayout aToZ = sortByDialog.findViewById(R.id.sort_a_to_z);
            aToZ.setOnClickListener(v2 -> {
                send_result(CHOOSE_SORT_BY_A_TO_Z);
                sortByDialog.dismiss();
            });

            // sort by name z - a
            LinearLayout zToA = sortByDialog.findViewById(R.id.sort_z_to_a);
            zToA.setOnClickListener(v3 -> {
                send_result(CHOOSE_SORT_BY_Z_TO_A);
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

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void send_result(int RESULT_OK) {
        Intent intent = new Intent();
        Objects.requireNonNull(getTargetFragment()).onActivityResult(getTargetRequestCode(), RESULT_OK, intent);
        dismiss();
    }

}
