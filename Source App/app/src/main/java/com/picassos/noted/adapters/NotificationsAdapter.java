package com.picassos.noted.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.picassos.noted.R;
import com.picassos.noted.entities.Notification;
import com.picassos.noted.listeners.NotificationListener;

import java.util.List;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.NotificationViewHolder> {

    private final List<Notification> notifications;
    private final NotificationListener notificationListener;

    public NotificationsAdapter(List<Notification> notifications, NotificationListener notificationListener) {
        this.notifications = notifications;
        this.notificationListener = notificationListener;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NotificationViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        holder.set_notification(notifications.get(position));
        holder.layout.setOnClickListener(v -> notificationListener.onNotificationClicked(notifications.get(position), position));
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    static class NotificationViewHolder extends RecyclerView.ViewHolder {

        LinearLayout layout;

        View isRead;
        TextView title, description;

        NotificationViewHolder(@NonNull View notification) {
            super(notification);

            layout = notification.findViewById(R.id.item_notification_layout);
            isRead = notification.findViewById(R.id.notification_is_read);
            title = notification.findViewById(R.id.notification_title);
            description = notification.findViewById(R.id.notification_description);
        }

        void set_notification(Notification data) {
            title.setText(data.title);
            description.setText(data.content);
            // check if is read
            if (!data.read) {
                isRead.setVisibility(View.VISIBLE);
            } else {
                isRead.setVisibility(View.GONE);
            }
        }

    }

}
