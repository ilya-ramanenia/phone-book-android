package com.example.iramanenia.phonebook.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

import io.realm.OrderedCollectionChangeSet;
import io.realm.OrderedRealmCollectionChangeListener;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

import com.example.iramanenia.phonebook.model.ContactModel;

import java.util.Objects;

public class ContactListActivity extends AppCompatActivity {
    private RecyclerView contactsRecyclerView;
    private ContactAdapter contactAdapter;
    private RecyclerView.LayoutManager contactsLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startEditingContact(ContactListActivity.this, null);
            }
        });

        addContactsListener();

        contactsRecyclerView = findViewById(R.id.contact_recycler_view);
        contactsRecyclerView.setHasFixedSize(true);

        contactsLayoutManager = new LinearLayoutManager(this);
        contactsRecyclerView.setLayoutManager(contactsLayoutManager);

        Realm realm = Realm.getDefaultInstance();
        final RealmResults<ContactModel> contacts = realm.where(ContactModel.class).findAll();
        contactAdapter = new ContactAdapter(contacts);
        contactsRecyclerView.setAdapter(contactAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_contact_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_createtest) {
            createTestContact();
            return true;
        } else if (id == R.id.action_deleteall) {
            deleteAllContacts();
            return true;
        } else if (id == R.id.action_createnew) {
            startEditingContact(ContactListActivity.this, null);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Main
    static void startEditingContact(Context context, ContactModel contact) {
        Intent intent = new Intent(context, ContactCreateActivity.class);
        if (contact != null) {
            intent.putExtra(ContactCreateActivity.EXTRA_CONTACT_ID, contact.getId());
        }
        context.startActivity(intent);
    }

    // DB
    void addContactsListener() {
        Realm realm = Realm.getDefaultInstance();
        final RealmResults<ContactModel> contacts = realm.where(ContactModel.class).findAll();
        Log.v("DEBUG","Contacts count: " + contacts.size());

        // Listeners will be notified when data changes
        contacts.addChangeListener(new OrderedRealmCollectionChangeListener<RealmResults<ContactModel>>() {
            @Override
            public void onChange(RealmResults<ContactModel> results, OrderedCollectionChangeSet changeSet) {
                contactAdapter.updateDataset(results);
            }
        });
    }

    void createTestContact() {
        Realm realm = Realm.getDefaultInstance();

        final ContactModel lastContact = realm.where(ContactModel.class).sort("id", Sort.DESCENDING).findFirst();
        long newId = lastContact != null ? lastContact.getId() + 1 : 0;

        ContactModel testContact = new ContactModel();
        testContact.setId(newId);
        testContact.setFirstName("first " + newId);
        testContact.setLastName("last " + newId);
        testContact.setPhone(String.format("%0" + 5 + "d", 0).replace("0", Long.toString(newId))); // repeated ID

        // Persist your data in a transaction
        realm.beginTransaction();
        realm.copyToRealm(testContact);
        realm.commitTransaction();
    }

    void deleteAllContacts() {
        Realm realm = Realm.getDefaultInstance();

        final RealmResults<ContactModel> contacts = realm.where(ContactModel.class).findAll();

        // Persist your data in a transaction
        realm.beginTransaction();
        contacts.deleteAllFromRealm();
        realm.commitTransaction();
    }


    public static class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> {
        private RealmResults<ContactModel> contactsDataset;

        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder
        public static class ViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            public TextView idTextView;
            public TextView firstNameTextView;
            public TextView lastNameTextView;
            public TextView phoneTextView;

            public ViewHolder(View v) {
                super(v);
                idTextView = v.findViewById(R.id.idView);
                firstNameTextView = v.findViewById(R.id.firstNameTextView);
                lastNameTextView = v.findViewById(R.id.lastNameTextView);
                phoneTextView = v.findViewById(R.id.phoneTextView);
            }
        }

        // Provide a suitable constructor (depends on the kind of dataset)
        public ContactAdapter(RealmResults<ContactModel> dataset) {
            contactsDataset = dataset;
        }

        public void updateDataset(RealmResults<ContactModel> dataset) {
            contactsDataset = dataset;
            notifyDataSetChanged();
        }

        // Create new views (invoked by the layout manager)
        @Override
        public ContactAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                            int viewType) {
            // create a new view
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.activity_contact_list_item, parent, false);
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element
            ContactModel contact = contactsDataset.get(position);
            holder.idTextView.setText(Long.toString(contact.getId()));
            holder.firstNameTextView.setText(contact.getFirstName());
            holder.lastNameTextView.setText(contact.getLastName());
            holder.phoneTextView.setText(contact.getPhone());

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startEditingContact(v.getContext(), contactsDataset.get(position));
                }
            });
        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return contactsDataset.size();
        }
    }
}