package com.example.iramanenia.phonebook;

import android.app.Application;
import io.realm.Realm;

public class PhoneBookApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // Initialize Realm (just once per application)
        Realm.init(getApplicationContext());
    }

}
