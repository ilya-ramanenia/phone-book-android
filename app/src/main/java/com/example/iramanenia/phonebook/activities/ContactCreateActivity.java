package com.example.iramanenia.phonebook.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.iramanenia.phonebook.model.ContactModel;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class ContactCreateActivity extends AppCompatActivity {
    public ContactCreateActivity() {
        super();
    }

    public static final String EXTRA_CONTACT_ID = "EXTRA_CONTACT_ID";

    TextView idTextView;
    EditText firstNameEditText, lastNameEditText, phoneEditText;
    Button saveButton;
    ContactModel contact;

    Boolean creatingNewContact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_create);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        idTextView = findViewById(R.id.idTextView);
        firstNameEditText = findViewById(R.id.firstNameEditText);
        lastNameEditText = findViewById(R.id.lastNameEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        saveButton = findViewById(R.id.saveButton);

        Realm realm = Realm.getDefaultInstance();
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(EXTRA_CONTACT_ID)) {
            long contactId = intent.getLongExtra(EXTRA_CONTACT_ID, 0);
            contact = realm.where(ContactModel.class).equalTo("id", contactId).findFirst();

            idTextView.setText("ID: " + Long.toString(contact.getId()));
            firstNameEditText.setText(contact.getFirstName());
            lastNameEditText.setText(contact.getLastName());
            phoneEditText.setText((contact.getPhone()));

            creatingNewContact = false;
        }
        else {
            final ContactModel lastContact = realm.where(ContactModel.class).sort("id", Sort.DESCENDING).findFirst();
            long newId = lastContact != null ? lastContact.getId() + 1 : 0;

            contact = new ContactModel();
            contact.setId(newId);
            idTextView.setText("ID: " + Long.toString(newId));

            creatingNewContact = true;
        }

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Realm realm = Realm.getDefaultInstance();
                realm.beginTransaction();

                contact.setFirstName(firstNameEditText.getText().toString());
                contact.setLastName(lastNameEditText.getText().toString());
                contact.setPhone(phoneEditText.getText().toString());

                realm.copyToRealmOrUpdate(contact);
                realm.commitTransaction();

                finish();
            }
        });
    }
}
