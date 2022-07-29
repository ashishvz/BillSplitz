package com.ashishvz.billsplitz.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ashishvz.billsplitz.R;
import com.ashishvz.billsplitz.models.Contact;
import com.google.android.material.textview.MaterialTextView;

import java.util.List;

public class ContactListAdapter extends RecyclerView.Adapter<ContactListAdapter.ContactViewHolder> {
    private List<Contact> contacts;
    private final Context context;


    public ContactListAdapter(Activity activity, List<Contact> contacts) {
        this.context = activity;
        this.contacts = contacts;
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ContactViewHolder(LayoutInflater.from(context).inflate(R.layout.contact_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        Contact contact = contacts.get(position);
        holder.nameText.setText(contact.getName());
        holder.contactText.setText(contact.getPhoneNumber());
        holder.contactCB.setChecked(contact.getSelected());
        holder.contactCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                contacts.get(holder.getAdapterPosition()).setSelected(b);
            }
        });

    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    public List<Contact> getSelectedContacts() {
        return contacts;
    }

    public static class ContactViewHolder extends RecyclerView.ViewHolder {
        MaterialTextView nameText;
        MaterialTextView contactText;
        CheckBox contactCB;

        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.contactName);
            contactText = itemView.findViewById(R.id.contactNumber);
            contactCB = itemView.findViewById(R.id.contactCheckBox);
        }
    }

}
