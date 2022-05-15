package com.picassos.noted.listeners;

import com.picassos.noted.entities.Notification;

public interface NotificationListener {
    void onNotificationClicked(Notification notification, int position);
}
