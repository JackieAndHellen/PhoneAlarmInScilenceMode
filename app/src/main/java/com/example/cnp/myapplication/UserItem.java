package com.example.cnp.myapplication;

import java.io.Serializable;

/**
 * Created by cnp on 17-6-5.
 */

public class UserItem implements Serializable{
    public String userName;
    public String phoneNumber;
    public String musicPath;
    //public Uri musicUri;
    public UserItem(String userName, String phoneNumber, String musicPath){
        this.userName = userName;
        this.phoneNumber = phoneNumber;
        this.musicPath = musicPath;
        //this.musicUri = musicUri;
    }
}
