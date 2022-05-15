package com.picassos.noted.sheets;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.picassos.noted.R;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class AttachImageBottomSheetModal extends BottomSheetDialogFragment {

    // Bundle
    Bundle bundle;

    String currentPhotoPath;

    // Permissions
    String[] permissions = new String[] {
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    // REQUEST CODES
    private static final int REQUEST_STORAGE_PERMISSION_CODE = 1;
    private static final int REQUEST_CAMERA_PERMISSION_CODE = 2;
    private static final int REQUEST_SELECT_IMAGE_CODE = 3;
    public static final int REQUEST_SELECT_IMAGE_FROM_GALLERY_CODE = 4;
    public static final int REQUEST_CAMERA_IMAGE_CODE = 5;
    public static final int REQUEST_SELECT_VIDEO_FROM_GALLERY_CODE = 6;

    public interface OnChooseImageListener {
        void onChooseImageListener(int requestCode, Bitmap bitmap, Uri uri);
    }

    public interface OnChooseVideoListener {
        void onChooseVideoListener(int requestCode, Uri uri);
    }

    OnChooseImageListener onChooseImageListener;
    OnChooseVideoListener onChooseVideoListener;

    public AttachImageBottomSheetModal() {

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            onChooseImageListener = (OnChooseImageListener) context;
        } catch (final ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement onChooseImageListener");
        }

        try {
            onChooseVideoListener = (OnChooseVideoListener) context;
        } catch (final ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement onChooseVideoListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.attach_image_bottom_sheet_modal, container, false);

        // Bundle
        bundle = new Bundle();

        // take a photo
        LinearLayout takePhoto = view.findViewById(R.id.take_photo);
        takePhoto.setOnClickListener(v -> {
            if (checkPermissions()) {
                requestCaptureImage();
            }
        });

        // select image from gallery
        LinearLayout selectImageFromGallery = view.findViewById(R.id.select_image_from_gallery);
        selectImageFromGallery.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(requireActivity(), new String[] { Manifest.permission.READ_EXTERNAL_STORAGE }, REQUEST_STORAGE_PERMISSION_CODE);
            } else {
                requestSelectImage();
            }
        });

        // select video from gallery
        LinearLayout selectVideoFromGallery = view.findViewById(R.id.select_video_from_gallery);
        selectVideoFromGallery.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(requireActivity(), new String[] { Manifest.permission.READ_EXTERNAL_STORAGE }, REQUEST_STORAGE_PERMISSION_CODE);
            } else {
                requestSelectVideo();
            }
        });

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * request to open file chooser,
     * allow for images only as attachment
     */
    private void requestSelectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        if (intent.resolveActivity(requireContext().getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_SELECT_IMAGE_CODE);
        }
    }

    /**
     * request to open file chooser,
     * allow for videos only as attachment
     */
    private void requestSelectVideo() {
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Video"), REQUEST_SELECT_VIDEO_FROM_GALLERY_CODE);
    }

    /**
     * request to open image capture,
     * allow for images only as attachment
     */
    private void requestCaptureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (intent.resolveActivity(requireContext().getPackageManager()) != null) {
            File file = null;

            try {
                file = createImageFile();
            } catch (IOException e) {
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            if (file != null) {
                Uri uri = FileProvider.getUriForFile(getContext(), getContext().getPackageName() + ".provider", file);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                if (uri != null) {
                    bundle.putString(MediaStore.EXTRA_OUTPUT, uri.toString());
                }
                try {
                    intent.putExtra("return_data", true);
                    startActivityForResult(intent, REQUEST_CAMERA_IMAGE_CODE);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    /**
     * create image file
     * @return image for image
     * @throws IOException for exception
     */
    private File createImageFile() throws IOException {
        // create image file name
        @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";

        File image = new File(Environment.getExternalStorageDirectory() + "/" + requireContext().getPackageName() + "/",  imageFileName + ".jpg");
        // create directory
        File dir = new File(Environment.getExternalStorageDirectory() + "/" + getContext().getPackageName() + "/");
        if (!dir.exists()) {
            dir.mkdir();
        }

        // save the file
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_STORAGE_PERMISSION_CODE && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                requestSelectImage();
            } else {
                Toast.makeText(getContext(), getString(R.string.permission_denied), Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == REQUEST_CAMERA_PERMISSION_CODE && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                requestCaptureImage();
            } else {
                Toast.makeText(getContext(), getString(R.string.permission_denied), Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == 100) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                requestCaptureImage();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_SELECT_IMAGE_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                Uri selected_image_uri = data.getData();

                if (selected_image_uri != null) {
                    try {
                        InputStream inputStream = requireContext().getContentResolver().openInputStream(selected_image_uri);
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                        onChooseImageListener.onChooseImageListener(REQUEST_SELECT_IMAGE_FROM_GALLERY_CODE, bitmap, selected_image_uri);
                        dismiss();
                    } catch (Exception exception) {
                        Toast.makeText(getContext(), exception.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        } else if (requestCode == REQUEST_CAMERA_IMAGE_CODE && resultCode == RESULT_OK) {
            if (bundle != null) {
                Uri selected_image_uri = Uri.parse(bundle.getString(MediaStore.EXTRA_OUTPUT));

                if (selected_image_uri != null) {
                    try {
                        InputStream inputStream = requireContext().getContentResolver().openInputStream(selected_image_uri);
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                        onChooseImageListener.onChooseImageListener(REQUEST_CAMERA_IMAGE_CODE, bitmap, selected_image_uri);
                        dismiss();
                    } catch (Exception exception) {
                        Toast.makeText(getContext(), exception.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        } else if (requestCode == REQUEST_SELECT_VIDEO_FROM_GALLERY_CODE && resultCode == RESULT_OK) {
            Uri selected_video_uri = data.getData();

            if (selected_video_uri != null) {
                onChooseVideoListener.onChooseVideoListener(REQUEST_SELECT_VIDEO_FROM_GALLERY_CODE, selected_video_uri);
                dismiss();
            }
        }
    }

    /**
     * check multiple permissions
     * @return result
     */
    private boolean checkPermissions() {
        int result;

        List<String> list_permissions_needed = new ArrayList<>();
        for (String p : permissions) {
            result = ContextCompat.checkSelfPermission(requireContext(), p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                list_permissions_needed.add(p);
            }
        }
        if (!list_permissions_needed.isEmpty()) {
            ActivityCompat.requestPermissions(requireActivity(), list_permissions_needed.toArray(new String[list_permissions_needed.size()]), 100);
            return false;
        }
        return true;
    }
}