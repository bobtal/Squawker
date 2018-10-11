package android.example.com.squawker.fcm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.example.com.squawker.MainActivity;
import android.example.com.squawker.R;
import android.example.com.squawker.provider.SquawkContract;
import android.example.com.squawker.provider.SquawkProvider;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

// COMPLETED (1) Make a new Service in the fcm package that extends from FirebaseMessagingService.
public class SquawkFirebaseMessagingService extends FirebaseMessagingService {

    private static final int NEW_MESSAGE_NOTIFICATION_ID = 123;
    private static final int MAX_NOTIFICATION_CHARACTERS = 30;

    private static final String JSON_KEY_AUTHOR = SquawkContract.COLUMN_AUTHOR;
    private static final String JSON_KEY_AUTHOR_KEY = SquawkContract.COLUMN_AUTHOR_KEY;
    private static final String JSON_KEY_MESSAGE = SquawkContract.COLUMN_MESSAGE;
    private static final String JSON_KEY_DATE = SquawkContract.COLUMN_DATE;

    // COMPLETED (2) As part of the new Service - Override onMessageReceived. This method will
    // be triggered whenever a squawk is received. You can get the data from the squawk
    // message using getData(). When you send a test message, this data will include the
    // following key/value pairs:
    // test: true
    // author: Ex. "TestAccount"
    // authorKey: Ex. "key_test"
    // message: Ex. "Hello world"
    // date: Ex. 1484358455343
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        // COMPLETED (3) As part of the new Service - If there is message data, get the data using
        // the keys and do two things with it :
        // 1. Display a notification with the first 30 character of the message
        // 2. Use the content provider to insert a new message into the local database
        // Hint: You shouldn't be doing content provider operations on the main thread.
        // If you don't know how to make notifications or interact with a content provider
        // look at the notes in the classroom for help.
        Map<String, String> data = remoteMessage.getData();

        if (data.size() > 0) {
            String author = data.get(JSON_KEY_AUTHOR);
            String authorKey = data.get(JSON_KEY_AUTHOR_KEY);
            String message = data.get(JSON_KEY_MESSAGE);
            String date = data.get(JSON_KEY_DATE);

            displayNotification(author, message);

            insertSquawkMessage(author, authorKey, message, date);
        }
    }

    private void displayNotification(String author, String message) {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                NEW_MESSAGE_NOTIFICATION_ID,
                intent,
                PendingIntent.FLAG_ONE_SHOT);

        // If the message is longer than the max number of characters we want in our
        // notification, truncate it and add the unicode character for ellipsis
        if (message.length() > MAX_NOTIFICATION_CHARACTERS) {
            message = message.substring(0, MAX_NOTIFICATION_CHARACTERS) + "\u2026";
        }

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.test)
                .setContentTitle(String.format(getString(R.string.notification_message), author))
                .setContentText(message)
                .setContentIntent(pendingIntent)
                .setSound(defaultSoundUri)
                .setAutoCancel(true);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(NEW_MESSAGE_NOTIFICATION_ID, notificationBuilder.build());
    }

    private void insertSquawkMessage(final String author, final String authorKey, final String message, final String date) {

        AsyncTask<Void, Void, Void> insertSquawk = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                ContentValues newMessage = new ContentValues();
                newMessage.put(SquawkContract.COLUMN_AUTHOR, author);
                newMessage.put(SquawkContract.COLUMN_AUTHOR_KEY, authorKey);
                newMessage.put(SquawkContract.COLUMN_MESSAGE, message);
                newMessage.put(SquawkContract.COLUMN_DATE, date);
                getContentResolver().insert(SquawkProvider.SquawkMessages.CONTENT_URI, newMessage);
                return null;
            }
        };

        insertSquawk.execute();
    }
}
