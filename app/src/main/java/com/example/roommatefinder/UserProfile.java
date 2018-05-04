package com.example.roommatefinder;

import android.net.Uri;

import java.net.URI;

public class UserProfile {
    private String userName;
    private String userEmail;
    private String userClass;
    private String userGender;
    private Uri userPhoto;

    //constructor, need this because of setter and getter
    public UserProfile(){

    }
    public UserProfile(String userName, String userEmail, String userClass, String userGender,Uri uri)
    {
        this.userName = userName;
        this.userEmail = userEmail;
        this.userClass = userClass;
        this.userGender = userGender;
        this.userPhoto = uri;
    }

    //5th step: create getter and setter
    //
    //
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserClass() {
        return userClass;
    }

    public void setUserClass(String userClass) {
        this.userClass = userClass;
    }

    public String getUserGender() {
        return userGender;
    }

    public void setUserGender(String userGender) { this.userGender = userGender; }

    public void setUserPhoto(Uri userPhoto){ this.userPhoto = userPhoto; }

    public Uri getUserPhoto() {return this.userPhoto;}
}
