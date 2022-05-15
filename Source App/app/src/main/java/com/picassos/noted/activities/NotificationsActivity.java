package com.picassos.noted.activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.picassos.noted.R;
import com.picassos.noted.adapters.NotificationsAdapter;
import com.picassos.noted.databases.APP_DATABASE;
import com.picassos.noted.databases.DAO;
import com.picassos.noted.entities.Notification;
import com.picassos.noted.listeners.NotificationListener;
import com.picassos.noted.utils.Helper;

import java.util.ArrayList;
import java.util.List;

public class NotificationsActivity extends AppCompatActivity implements NotificationListener {

    // adapter & notification model
    private NotificationsAdapter adapter;
    private List<Notification> notifications;

    private DAO dao;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // OPTIONS
        Helper.dark_mode(this);
        Helper.fullscreen_mode(this);
        Helper.screen_state(this);

        setContentView(R.layout.activity_notifications);

        // initialize database
        dao = APP_DATABASE.requestDatabase(this).dao();

        // return back and finish activity
        ImageView goBack = findViewById(R.id.go_back);
        goBack.setOnClickListener(v -> finish());

        // delete all notifications
        ImageView deleteAllNotifications = findViewById(R.id.delete_all_notifications);
        deleteAllNotifications.setOnClickListener(v -> {
            dao.requestDeleteAllNotification();
            notifications.clear();
            adapter.notifyDataSetChanged();
            requestNotifications();
        });

        // notifications list initialize
        RecyclerView recyclerView = findViewById(R.id.notifications_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        // notifications list, adapter
        notifications = new ArrayList<>();
        adapter = new NotificationsAdapter(notifications, this);
        recyclerView.setAdapter(adapter);

        requestNotifications();
    }

    /**
     * request notifications from
     * DAO, notifications entity
     */
    private void requestNotifications() {
        @SuppressLint("StaticFieldLeak")
        class GetNotificationsTask extends AsyncTask<Void, Void, List<Notification>> {

            @Override
            protected List<Notification> doInBackground(Void... voids) {
                return APP_DATABASE.requestDatabase(getApplicationContext()).dao().requestAllNotifications();
            }

            @Override
            protected void onPostExecute(List<Notification> notifications_inline) {
                super.onPostExecute(notifications_inline);
                notifications.addAll(notifications_inline);
                adapter.notifyDataSetChanged();

                if (adapter.getItemCount() == 0) {
                    findViewById(R.id.no_items).setVisibility(View.VISIBLE);
                } else {
                    findViewById(R.id.no_items).setVisibility(View.GONE);
                }
            }

        }
        new GetNotificationsTask().execute();
    }

    @Override
    public void onNotificationClicked(Notification notification, int position) {
        Dialog previewNotification = new Dialog(this);

        previewNotification.requestWindowFeature(Window.FEATURE_NO_TITLE);

        previewNotification.setContentView(R.layout.preview_notification_dialog);

        // enable dialog cancel
        previewNotification.setCancelable(true);
        previewNotification.setOnCancelListener(dialog -> previewNotification.dismiss());

        // notification title
        TextView notificationTitle = previewNotification.findViewById(R.id.notification_title);
        notificationTitle.setText(notification.title);

        // notification message
        TextView notificationMessage = previewNotification.findViewById(R.id.notification_description);
        notificationMessage.setText(notification.content);

        TextView notificationDate = previewNotification.findViewById(R.id.notification_date);
        notificationDate.setText(Helper.get_formatted_date(notification.created_at));

        // confirm allow
        TextView confirmAllow = previewNotification.findViewById(R.id.confirm_allow);
        confirmAllow.setOnClickListener(v1 -> {
            notification.read = true;
            dao.requestInsertNotification(notification);
            // refresh notifications
            notifications.clear();
            adapter.notifyDataSetChanged();
            requestNotifications();
            // dismiss dialog
            previewNotification.dismiss();
        });

        if (previewNotification.getWindow() != null) {
            previewNotification.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            previewNotification.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        }

        previewNotification.show();
    }
}