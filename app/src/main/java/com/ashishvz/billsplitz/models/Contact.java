package com.ashishvz.billsplitz.models;

public class Contact {
    public String name;
    public String phoneNumber;
    public Boolean isSelected;

    public Contact() {
    }

    public Contact(String name, String phoneNumber, Boolean isSelected ) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.isSelected = isSelected;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setSelected(Boolean selected) {
        isSelected = selected;
    }

    public Boolean getSelected() {
        return isSelected;
    }
}
