package com.example.jama.selfievselfie;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;


/**
 * Created by JAMA on 7/7/2017.
 */

public class InstaVote extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
