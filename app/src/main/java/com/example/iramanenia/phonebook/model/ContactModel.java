package com.example.iramanenia.phonebook.model;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class ContactModel extends RealmObject {
    @PrimaryKey
    private long id;

    private String firstName;
    private String lastName;
    private String phone;

    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
}