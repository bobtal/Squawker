package android.example.com.squawker.fcm;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.iid.zzd;

// COMPLETED (1) Make a new package for your FCM service classes called "fcm"
// COMPLETED (2) Create a new Service class that extends FirebaseInstanceIdService.
// You'll need to implement the onTokenRefresh method. Simply have it print out
// the new token.
public class SquawkFirebaseInstanceService extends FirebaseInstanceIdService {

    private static final String TAG = SquawkFirebaseInstanceService.class.getSimpleName();

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token has been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(refreshedToken);
    }

    /**
     * Persist token to third-party servers.
     * <p>
     *     Modify this method to associate the user's FCM InstanceID token with any server-side
     *     account maintained by your application.
     * </p>
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        // This method is blank, but if you were to build a server that stored users token
        // information, this is where you'd send the token to the server.
    }
}
