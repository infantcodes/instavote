package com.example.jama.selfievselfie;

import android.support.v4.app.Fragment;
import android.util.Log;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.iid.zzd;

/**
 * Created by JAMA on 3/26/2017.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService{

    String TAG = "MyFirebaseInstanceID";

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        reference.child("Token").setValue(refreshedToken);

        Log.d(TAG, "Refreshed token: " + refreshedToken);
    }

}
