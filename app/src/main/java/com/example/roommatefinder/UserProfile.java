package com.example.roommatefinder;

public class UserProfile {
    public String userName;
    public String userEmail;
    public String userClass;
    public String userGender;

    //constructor
    public UserProfile(String userName, String userEmail, String userClass, String userGender)
    {
        this.userName = userName;
        this.userEmail = userEmail;
        this.userClass = userClass;
        this.userGender = userGender;
    }
}
