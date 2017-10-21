package jp.co.intec.demoapp;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

/**
 * プッシュ通知を受け取るサービスです。
 * <p/>
 * Created by Shirai on 2016/08/05.
 */
public class FcmTestFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        // 通知設定
        Map<String, String> data = remoteMessage.getData();
        String title = data.get("title");
        String body = data.get("body");
        NotificationCompat.Builder notificationCompatBuilder = new NotificationCompat.Builder(this);
        notificationCompatBuilder.setSmallIcon(R.mipmap.ic_launcher);
        notificationCompatBuilder.setContentTitle((title != null) ? title : "");
        notificationCompatBuilder.setContentText((body != null) ? body : "");
        notificationCompatBuilder.setDefaults(Notification.DEFAULT_ALL);
        notificationCompatBuilder.setAutoCancel(true);
        // タップ時の動作設定
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        notificationCompatBuilder.setContentIntent(pendingIntent);
        notificationCompatBuilder.setFullScreenIntent(pendingIntent, false);
        // 通知表示
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(346, notificationCompatBuilder.build());
    }

}