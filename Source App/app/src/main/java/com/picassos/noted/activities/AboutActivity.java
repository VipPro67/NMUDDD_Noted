package com.picassos.noted.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.picassos.noted.R;
import com.picassos.noted.utils.Helper;

public class AboutActivity extends AppCompatActivity {

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // OPTIONS
        Helper.dark_mode(this);
        Helper.fullscreen_mode(this);
        Helper.screen_state(this);

        setContentView(R.layout.activity_about);

        // return back and finish activity
        ImageView goBack = findViewById(R.id.go_back);
        goBack.setOnClickListener(v -> {
            startActivity(new Intent(AboutActivity.this, MainActivity.class));
            finish();
        });

        // app version
        TextView appVersion = findViewById(R.id.app_version);
        try {
            appVersion.setText("Version " + Helper.get_version_name(getApplicationContext()));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        // icons8
        TextView icons8 = findViewById(R.id.icons8);
        icons8.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("https://icons8.com"));
            startActivity(intent);
        });

        // copyright notice
        TextView copyrightNotice = findViewById(R.id.copyright_notice);
        copyrightNotice.setText(Helper.copyright(getApplicationContext()));
    }
}