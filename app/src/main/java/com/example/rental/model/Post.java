package com.example.rental.model;

import java.util.ArrayList;

public class Post {
    User user;
    String key;
    String title,description,address,contact,rent;
    boolean student,family,office;
    long timeStamp;
    ArrayList<String> images;

    public Post() {
    }

    public Post(User user, String key, String title, String description, String address, String contact, String rent, boolean student, boolean family, boolean office, long timeStamp, ArrayList<String> images) {
        this.user = user;
        this.key = key;
        this.title = title;
        this.description = description;
        this.address = address;
        this.contact = contact;
        this.rent = rent;
        this.student = student;
        this.family = family;
        this.office = office;
        this.timeStamp = timeStamp;
        this.images = images;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getRent() {
        return rent;
    }

    public void setRent(String rent) {
        this.rent = rent;
    }

    public boolean isStudent() {
        return student;
    }

    public void setStudent(boolean student) {
        this.student = student;
    }

    public boolean isFamily() {
        return family;
    }

    public void setFamily(boolean family) {
        this.family = family;
    }

    public boolean isOffice() {
        return office;
    }

    public void setOffice(boolean office) {
        this.office = office;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public ArrayList<String> getImages() {
        return images;
    }

    public void setImages(ArrayList<String> images) {
        this.images = images;
    }
}
