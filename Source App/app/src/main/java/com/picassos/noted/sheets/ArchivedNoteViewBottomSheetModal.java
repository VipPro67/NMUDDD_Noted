package com.picassos.noted.sheets;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ShareCompat;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.makeramen.roundedimageview.RoundedImageView;
import com.picassos.noted.R;
import com.picassos.noted.activities.ViewAttachedImageActivity;
import com.picassos.noted.activities.ViewAttachedVideoActivity;
import com.picassos.noted.entities.ArchiveNote;

public class ArchivedNoteViewBottomSheetModal extends BottomSheetDialogFragment {

    ArchiveNote presetNote;

    private static final int REQUEST_VIEW_NOTE_VIDEO = 6;

    public ArchivedNoteViewBottomSheetModal() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.archived_note_view_bottom_sheet_modal, container, false);

        // get preset note
        presetNote = (ArchiveNote) getArguments().getSerializable("archive_note_data");

        /*
         * set note data like note title,
         * description and subtitle...
         */
        TextView noteTitle = view.findViewById(R.id.note_title);
        TextView noteSubtitle = view.findViewById(R.id.note_subtitle);
        TextView noteDescription = view.findViewById(R.id.note_description);
        RoundedImageView noteImage = view.findViewById(R.id.note_image);
        RoundedImageView noteVideo = view.findViewById(R.id.note_video);

        noteTitle.setText(presetNote.getNote_title());
        noteSubtitle.setText(presetNote.getNote_subtitle());
        noteDescription.setText(presetNote.getNote_description());

        // check if image attachment is set
        if (presetNote.getNote_image_path() != null && !presetNote.getNote_image_path().trim().isEmpty()) {
            noteImage.setImageBitmap(BitmapFactory.decodeFile(presetNote.getNote_image_path()));
            noteImage.setOnClickListener(v -> {
                Intent intent = new Intent(getContext(), ViewAttachedImageActivity.class);
                intent.putExtra("image_path", presetNote.getNote_image_path());
                intent.putExtra("image_type", "view");
                startActivity(intent);
            });
            // share note image
            view.findViewById(R.id.note_image_share).setOnClickListener(v -> {
                if (presetNote.getNote_image_uri() != null && !TextUtils.isEmpty(presetNote.getNote_image_uri())) {
                    Intent share = ShareCompat.IntentBuilder.from(getActivity())
                            .setStream(Uri.parse(presetNote.getNote_image_uri()))
                            .setType("text/html")
                            .getIntent()
                            .setAction(Intent.ACTION_SEND)
                            .setDataAndType(Uri.parse(presetNote.getNote_image_uri()), "image/*")
                            .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivity(Intent.createChooser(share, getString(R.string.share_image)));
                }
            });
            // show image, image container
            view.findViewById(R.id.note_image_container).setVisibility(View.VISIBLE);
            noteImage.setVisibility(View.VISIBLE);
        } else {
            view.findViewById(R.id.note_image_container).setVisibility(View.GONE);
        }

        // check if video attachment is set
        if (presetNote.getNote_video_path() != null && !presetNote.getNote_video_path().trim().isEmpty()) {
            Bitmap video_thumbnail = ThumbnailUtils.createVideoThumbnail(presetNote.getNote_video_path(), MediaStore.Video.Thumbnails.FULL_SCREEN_KIND);
            noteVideo.setImageBitmap(video_thumbnail);
            noteVideo.setOnClickListener(v -> {
                Intent intent = new Intent(getContext(), ViewAttachedVideoActivity.class);
                intent.putExtra("video_path", presetNote.getNote_video_path());
                intent.putExtra("video_type", "view");
                startActivityForResult(intent, REQUEST_VIEW_NOTE_VIDEO);
            });
            noteVideo.setVisibility(View.VISIBLE);
            view.findViewById(R.id.note_video_container).setVisibility(View.VISIBLE);
        } else {
            view.findViewById(R.id.note_video_container).setVisibility(View.GONE);
        }

        // set note color
        GradientDrawable shape = (GradientDrawable) noteDescription.getBackground();
        shape.setColor(Color.parseColor(presetNote.getNote_color()));

        // note created at
        TextView noteCreatedAt = view.findViewById(R.id.note_created_at);
        noteCreatedAt.setText(presetNote.getNote_created_at());

        // note attached link
        TextView noteWebUrl = view.findViewById(R.id.note_web_url);
        // check if link attachment is set
        if (presetNote.getNote_web_link() != null && !presetNote.getNote_web_link().trim().isEmpty()) {
            noteWebUrl.setText(presetNote.getNote_web_link());
        } else {
            noteWebUrl.setVisibility(View.GONE);
        }

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

}
