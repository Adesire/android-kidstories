package com.project.android_kidstories.DataStore;

import android.content.SharedPreferences;

import androidx.lifecycle.MutableLiveData;

import com.project.android_kidstories.Model.User;
import com.project.android_kidstories.db.DbUserClass;

import static android.content.Context.MODE_PRIVATE;

public class UserRepository {

    private static UserRepository instance;

    public static UserRepository getInstance(){
        if(instance == null){
            instance = new UserRepository();
        }
        return instance;
    }

//    public MutableLiveData<User> getUser(){
//        SharedPreferences prefs = ;
//        String name = prefs.getString("name", "No name defined");//"No name defined" is the default value.
//        int idName = prefs.getInt("idName", 0); //0 is the default value.
//
//        MutableLiveData<User> data = new MutableLiveData<>();
//        data.setValue(user);
//        return data;
//    }
}
