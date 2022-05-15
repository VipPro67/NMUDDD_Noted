package com.picassos.noted.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.makeramen.roundedimageview.RoundedImageView;
import com.picassos.noted.R;
import com.picassos.noted.utils.Helper;

public class ViewAttachedImageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // OPTIONS
        Helper.dark_mode(this);
        Helper.fullscreen_mode(this);
        Helper.screen_state(this);

        setContentView(R.layout.activity_view_attached_image);

        // go back
        findViewById(R.id.go_back).setOnClickListener(v -> finish());

        // request note image path
        RoundedImageView noteImage = findViewById(R.id.note_image);
        if (getIntent().getStringExtra("image_path") != null) {
            if (!getIntent().getStringExtra("image_path").equals("")) {
                noteImage.setImageBitmap(BitmapFactory.decodeFile(getIntent().getStringExtra("image_path")));
            }
        }

        // request remove image
        findViewById(R.id.note_image_remove).setOnClickListener(v -> {
            if (getIntent().getStringExtra("image_type") == null) {
                Intent intent = new Intent();
                intent.putExtra("request", "remove_image");
                setResult(Activity.RESULT_OK, intent);
            }
            finish();
        });

        // check image request type
        if (getIntent().getStringExtra("image_type") != null) {
            if (getIntent().getStringExtra("image_type").equals("view")) {
                findViewById(R.id.note_image_remove).setVisibility(View.GONE);
            }
        }

        // remove image tooltip
        findViewById(R.id.note_image_remove).setOnLongClickListener(v -> {
            Toast.makeText(this, getString(R.string.remove_image), Toast.LENGTH_SHORT).show();
            return true;
        });
    }
}