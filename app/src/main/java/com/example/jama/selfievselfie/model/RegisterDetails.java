package com.example.jama.selfievselfie.model;

import android.support.annotation.Keep;

/**
 * Created by JAMA on 3/12/2017.
 */

public class RegisterDetails {

    String email, username, name, profileImage, bio;

    @Keep
    public RegisterDetails() {
    }

    public RegisterDetails(String email, String username, String name, String profileImage, String bio) {
        this.email = email;
        this.username = username;
        this.name = name;
        this.profileImage = profileImage;
        this.bio = bio;
    }
}
