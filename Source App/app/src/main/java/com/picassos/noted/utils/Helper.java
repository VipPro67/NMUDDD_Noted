package com.picassos.noted.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.view.View;
import android.view.WindowManager;

import androidx.appcompat.content.res.AppCompatResources;

import com.picassos.noted.R;
import com.picassos.noted.sharedPreferences.SharedPref;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Helper {
    /**
     * a method returns app copyright
     * @param context for context
     * @return to return the copyright notice
     */
    public static String copyright(Context context) {
        Calendar calendar = Calendar.getInstance();
        // get current year
        int year = calendar.get(Calendar.YEAR);
        // return copyright
        return context.getString(R.string.app_name) + ". All rights reserved. ©" + year;
    }

    /**
     * a method that enables dark
     * mode when option is toggled
     * @param context for context
     */
    public static void dark_mode(Context context) {
        SharedPref sharedPref;
        sharedPref = new SharedPref(context);
        // check if night mode @dark_mode is
        // enabled by user.
        if (sharedPref.loadNightModeState()) {
            context.setTheme(R.style.DarkTheme);
        } else {
            context.setTheme(R.style.AppTheme);
        }
    }

    /**
     * a method that enables fullscreen
     * mode when option is toggled
     * @param context for context
     */
    public static void fullscreen_mode(Context context) {
        SharedPref sharedPref;
        sharedPref = new SharedPref(context);
        // check if fullscreen mode @fullscreen_mode is
        // enabled by user.
        if (sharedPref.loadFullscreenState()) {
            ((Activity) context).getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }

    /**
     * a method that enables screen
     * on mode when option is enabled
     * @param context for context
     */
    public static void screen_state(Context context) {
        SharedPref sharedPref;
        sharedPref = new SharedPref(context);
        // check if screen state @scree_on_state is
        // enabled by user.
        if (sharedPref.loadScreenState()) {
            ((Activity) context).getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

    /**
     * use @get_version_name to get the app version name
     * and it requires only a
     * @param context context
     */
    public static String get_version_name(Context context) throws PackageManager.NameNotFoundException {
        PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        return packageInfo.versionName;
    }

    /**
     * use @get_formatted_date to format date
     * @param date_time for date time
     * @return formatted date
     */
    public static String get_formatted_date(Long date_time) {
        SimpleDateFormat newFormat = new SimpleDateFormat("dd MMM yy hh:mm");
        return newFormat.format(new Date(date_time));
    }

    /**
     * set view background tint
     * @param view for view
     * @param resourceId for resourceId
     */
    public static void set_background_tint(Context context, View view, int resourceId) {
        view.setBackgroundTintList(AppCompatResources.getColorStateList(context, resourceId));
    }


    public static void hide_bottom(View view) {
        int moveY = 2 * view.getHeight();
        view.animate()
                .translationY(moveY)
                .setDuration(300)
                .start();
    }

    public static void show_bottom(View view) {
        view.animate()
                .translationY(0)
                .setDuration(300)
                .start();
    }
}
