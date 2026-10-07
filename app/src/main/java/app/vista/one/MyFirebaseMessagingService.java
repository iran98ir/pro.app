package app.vista.one;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import me.leolin.shortcutbadger.ShortcutBadger;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String CHANNEL_ID = "default";

    @Override
    public void onMessageReceived(RemoteMessage message) {
        super.onMessageReceived(message);

        if (!PrefsManager.isNotificationsEnabled(this)) return;

        String title = getString(R.string.notification_new);
        String body = "";
        String url = null;
        int badgeCount = 0;

        if (message.getNotification() != null) {
            if (message.getNotification().getTitle() != null) {
                title = message.getNotification().getTitle();
            }
            if (message.getNotification().getBody() != null) {
                body = message.getNotification().getBody();
            }
        }

        if (message.getData() != null) {
            String dataTitle = message.getData().get("title");
            String dataBody = message.getData().get("body");

            if (dataTitle != null) title = dataTitle;
            if (dataBody != null) body = dataBody;

            url = message.getData().get("url");

            String badgeStr = message.getData().get("badge_count");
            if (badgeStr != null) {
                try {
                    badgeCount = Integer.parseInt(badgeStr);
                } catch (NumberFormatException ignored) {
                }
            }
        }

        showNotification(title, body, url);
        updateBadge(badgeCount);
    }

    private void showNotification(String title, String body, String url) {
        createChannelIfNeeded();

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        if (url != null && !url.isEmpty()) {
            intent.setData(android.net.Uri.parse(url));
        }

        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            flags |= PendingIntent.FLAG_IMMUTABLE;
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(
            this,
            (int) System.currentTimeMillis(),
            intent,
            flags
        );

        NotificationCompat.Builder builder =
            new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setColor(getResources().getColor(R.color.primary))
                .setContentTitle(title)
                .setContentText(body)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(body))
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL);

        NotificationManager nm = (NotificationManager)
            getSystemService(Context.NOTIFICATION_SERVICE);

        if (nm != null) {
            nm.notify((int) System.currentTimeMillis(), builder.build());
        }
    }

    private void createChannelIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager nm = getSystemService(NotificationManager.class);
            if (nm == null) return;

            if (nm.getNotificationChannel(CHANNEL_ID) != null) return;

            NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                getString(R.string.notification_channel_default),
                NotificationManager.IMPORTANCE_HIGH
            );

            channel.setDescription(
                getString(R.string.notification_channel_default_desc)
            );
            channel.enableVibration(true);
            channel.enableLights(true);
            channel.setShowBadge(true);

            nm.createNotificationChannel(channel);
        }
    }

    private void updateBadge(int count) {
        try {
            if (count > 0) {
                ShortcutBadger.applyCount(this, count);
            } else {
                ShortcutBadger.removeCount(this);
            }
        } catch (Exception ignored) {
        }
    }

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        PrefsManager.setFcmToken(this, token);
    }
}
