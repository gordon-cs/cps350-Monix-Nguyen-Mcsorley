package com.example.roommatefinder;

public class UserProfile {
    public String userName;
    public String userEmail;
    public String userClass;
    public String userGender;

    //constructor, need this because of setter and getter
    public UserProfile(){

    }
    public UserProfile(String userName, String userEmail, String userClass, String userGender)
    {
        this.userName = userName;
        this.userEmail = userEmail;
        this.userClass = userClass;
        this.userGender = userGender;
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

    public void setUserGender(String userGender) {
        this.userGender = userGender;
    }
}
