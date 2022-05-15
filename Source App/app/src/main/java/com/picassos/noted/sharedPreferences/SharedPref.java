/*
 * Mint APP, Mint Console, Design (except icons and some open-source libraries) and Donut API is copyright of Rixhion, inc. - © Rixhion, inc 2019. All rights reserved.
 *
 * Any redistribution or reproduction of part or all of the contents in any form is prohibited other than the following:
 *  - YOU MAY USE, EDIT DATA INSIDE MINT ONLY.
 *
 * You may not, except with our express written permission, distribute or commercially exploit the content. Nor may you transmit it or store it in any other website & app or other form of electronic retrieval system.
 *
 */

package com.picassos.noted.sharedPreferences;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPref {

    private SharedPreferences sharedPreferences;

    /*---------------- Shared Pref ---------------*/
    public SharedPref(Context context) {

        sharedPreferences = context.getSharedPreferences("Notes Shared Preferences", Context.MODE_PRIVATE);

    }

    /*---------------- Set NightMode ---------------*/
    public void setNightModeState(Boolean state) {

        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putBoolean("NightMode", state);

        editor.apply();

    }


    /*---------------- Load NightMode ---------------*/
    public Boolean loadNightModeState() {

        return sharedPreferences.getBoolean("NightMode", false);

    }


    /*---------------- Set Fullscreen ---------------*/
    public void setFullscreenState(Boolean state) {

        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putBoolean("Fullscreen", state);

        editor.apply();

    }


    /*---------------- Load Fullscreen ---------------*/
    public Boolean loadFullscreenState() {

        return sharedPreferences.getBoolean("Fullscreen", false);

    }


    /*---------------- Set Screen On ---------------*/
    public void setScreenState(Boolean state) {

        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putBoolean("ScreenOn", state);

        editor.apply();

    }


    /*---------------- Load Screen On ---------------*/
    public Boolean loadScreenState() {

        return sharedPreferences.getBoolean("ScreenOn", false);

    }

    /*---------------- Set Finger Print Option ---------------*/
    public void setFingerprintOption(Boolean state) {

        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putBoolean("FingerPrintLogin", state);

        editor.apply();

    }


    /*---------------- Load Finger Print Option ---------------*/
    public Boolean loadFingerprintOption() {

        return sharedPreferences.getBoolean("FingerPrintLogin", false);

    }

    /*---------------- Set First Time Categories ---------------*/
    public void setFirstTimeCategories(Boolean state) {

        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putBoolean("FirstTimeCategories", state);

        editor.apply();

    }

    /*---------------- Load First Time Categories ---------------*/
    public Boolean loadFirstTimeCategories() {

        return sharedPreferences.getBoolean("FirstTimeCategories", true);

    }

    /*---------------- Set FCM Register ID ---------------*/
    public void setFCMRregisterID(String state) {

        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("FCMRegisterID", state);

        editor.apply();

    }


    /*---------------- Load FCM Register ID ---------------*/
    public String loadFCMRegisterID() {

        return sharedPreferences.getString("FCMRegisterID", "");

    }

    /*---------------- Set Need Register ---------------*/
    public void setNeedRegister(Boolean state) {

        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putBoolean("NeedRegister", state);

        editor.apply();

    }


    /*---------------- Load Need Register  ---------------*/
    public Boolean loadNeedRegister() {

        return sharedPreferences.getBoolean("NeedRegister", false);

    }

    /*---------------- Set Subscribe Notifications ---------------*/
    public void setSubscribeNotifications(Boolean state) {

        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putBoolean("SubscribeNotifications", state);

        editor.apply();

    }


    /*---------------- Load Subscribe Notifications ---------------*/
    public Boolean loadSubscribeNotifications() {

        return sharedPreferences.getBoolean("SubscribeNotifications", false);

    }

    /*---------------- Set Note Pin ---------------*/
    public void setNotePinCode(int state) {

        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putInt("NotePinCode", state);

        editor.apply();

    }


    /*---------------- Load Note Pin ---------------*/
    public int loadNotePinCode() {

        return sharedPreferences.getInt("NotePinCode", 0);

    }

}
