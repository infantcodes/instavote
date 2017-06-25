package com.example.jama.selfievselfie.model;

/**
 * Created by JAMA on 5/4/2017.
 */

public class Getters {

    String message, sender, email, bio, image1, requestMessage, caption,
            status, pushKey, image2, profileImage2, uid2, username2,username, name, profileImage, uid;
    long date;

    public Getters() {
    }

    public Getters(String message, String sender, String email, String bio, String image1, String requestMessage, String caption, String status, String pushKey, String image2, String profileImage2, String uid2, String username2, String username, String name, String profileImage, String uid, long date) {
        this.message = message;
        this.sender = sender;
        this.email = email;
        this.bio = bio;
        this.image1 = image1;
        this.requestMessage = requestMessage;
        this.caption = caption;
        this.status = status;
        this.pushKey = pushKey;
        this.image2 = image2;
        this.profileImage2 = profileImage2;
        this.uid2 = uid2;
        this.username2 = username2;
        this.username = username;
        this.name = name;
        this.profileImage = profileImage;
        this.uid = uid;
        this.date = date;
    }

    public String getMessage() {
        return message;
    }

    public String getSender() {
        return sender;
    }

    public String getEmail() {
        return email;
    }

    public String getBio() {
        return bio;
    }

    public String getImage1() {
        return image1;
    }

    public String getRequestMessage() {
        return requestMessage;
    }

    public String getStatus() {
        return status;
    }

    public String getPushKey() {
        return pushKey;
    }

    public String getImage2() {
        return image2;
    }

    public String getProfileImage2() {
        return profileImage2;
    }

    public String getUid2() {
        return uid2;
    }

    public String getUsername2() {
        return username2;
    }

    public String getUsername() {
        return username;
    }

    public String getName() {
        return name;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public String getUid() {
        return uid;
    }

    public String getCaption() {
        return caption;
    }

    public long getDate() {
        return date;
    }
}
