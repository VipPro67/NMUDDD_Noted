package com.picassos.noted.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;



import com.google.android.material.navigation.NavigationView;
import com.picassos.noted.R;
import com.picassos.noted.fragments.ArchiveFragment;
import com.picassos.noted.fragments.HomeFragment;
import com.picassos.noted.fragments.RemindersFragment;
import com.picassos.noted.fragments.TrashFragment;
import com.picassos.noted.sheets.GoogleAdmobNativeAdBottomSheetModal;
import com.picassos.noted.utils.Helper;

import java.util.Objects;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    // Drawer Layout
    private DrawerLayout drawer;

    // toolbar buttons
    public Button extraAction;
    public ImageView moreOptions;
    public TextView toolbarTitle;

    // toolbar selector
    public RelativeLayout toolbarSelector;
    public ImageView toolbarSelectorClose;
    public ImageView toolbarSelectorDeleteNotes;
    public TextView toolbarSelectorSelectedItems;

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // OPTIONS
        Helper.dark_mode(this);
        Helper.fullscreen_mode(this);
        Helper.screen_state(this);

        setContentView(R.layout.activity_main);

        // initialize drawer
        drawer = findViewById(R.id.drawer_layout);

        // navigation view (navigation drawer)
        NavigationView navigationView = findViewById(R.id.navigation_drawer);
        navigationView.setNavigationItemSelectedListener(this);
        // hide menu widget if android version is below oreo
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O) {
            navigationView.getMenu().findItem(R.id.menu_widget).setVisible(false);
        }

        // more options
        moreOptions = findViewById(R.id.more_options);

        // toolbar title
        toolbarTitle = findViewById(R.id.toolbar_title);

        // toolbar selector
        toolbarSelector = findViewById(R.id.toolbar_selector);
        toolbarSelectorClose = findViewById(R.id.go_back);
        toolbarSelectorDeleteNotes = findViewById(R.id.delete_note);
        toolbarSelectorSelectedItems = findViewById(R.id.selected_items);

        // toggle navigation drawer
        findViewById(R.id.open_navigation_drawer).setOnClickListener(v -> drawer.openDrawer(Gravity.START));

        if (savedInstanceState == null) {
            if (getIntent().getStringExtra("fragment") != null) {
                if (Objects.equals(getIntent().getStringExtra("fragment"), "reminders")) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new RemindersFragment()).commit();
                    navigationView.setCheckedItem(R.id.menu_reminders);
                } else {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
                    navigationView.setCheckedItem(R.id.menu_home);
                }
            } else {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
                navigationView.setCheckedItem(R.id.menu_home);
            }
        }

    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_trash) {
            findViewById(R.id.extra_action).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.extra_action).setVisibility(View.GONE);
        }

        if (item.getItemId() == R.id.menu_home) {
            findViewById(R.id.more_options).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.more_options).setVisibility(View.GONE);
        }

        switch (item.getItemId()) {
            case R.id.menu_home:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
                break;
            case R.id.menu_reminders:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new RemindersFragment()).commit();
                break;
            case R.id.menu_archive:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ArchiveFragment()).commit();
                break;
            case R.id.menu_trash:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new TrashFragment()).commit();
                enableDeleteAll();
                break;
            case R.id.menu_notification:
                startActivity(new Intent(MainActivity.this, NotificationsActivity.class));
                break;
            case R.id.menu_widget:
                startActivity(new Intent(MainActivity.this, WidgetActivity.class));
                break;
            case R.id.menu_settings:
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                finish();
                break;
            case R.id.menu_rate:
                rateApp();
                break;
            case R.id.menu_about:
                startActivity(new Intent(MainActivity.this, AboutActivity.class));
                break;
        }

        toolbarTitle.setText(getString(R.string.app_name));
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * rate application by opening the link
     * of the app on the Google Play Store
     */
    private void rateApp() {
        /* get package name */
        String appPackageName = getApplicationContext().getPackageName();

        /* handle link of the Google Play Store */
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (ActivityNotFoundException errorException) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }

    /**
     * add delete all trash notes
     */
    private void enableDeleteAll() {
        extraAction = findViewById(R.id.extra_action);
        extraAction.setVisibility(View.VISIBLE);
        extraAction.setText(getString(R.string.delete_all));
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            GoogleAdmobNativeAdBottomSheetModal googleAdmobNativeAdBottomSheetModal = new GoogleAdmobNativeAdBottomSheetModal();
            googleAdmobNativeAdBottomSheetModal.show(getSupportFragmentManager(), "TAG");
        }

    }
}
