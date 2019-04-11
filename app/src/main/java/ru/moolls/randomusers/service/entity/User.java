package ru.moolls.randomusers.service.entity;

import android.graphics.Bitmap;

import java.io.Serializable;

public class User implements Serializable {
    private String firstName;
    private String lastName;
    private String city;
    private String address;
    private String email;
    private String phone;
    private String national;
    private String photoUrl;
    private transient Bitmap photoBitmap;


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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }


    public String fullInfo() {
        return "First Name: " + firstName +
                ", Last Name: " + lastName +
                ", City: " + city +
                ", Address: " + address +
                ", Email: " + email +
                ", Phone: " + phone +
                ", National: " + national;
    }

    @Override
    public String toString() {
        return firstName + " " + lastName;
    }

    public String getNational() {
        return national;
    }

    public void setNational(String national) {
        this.national = national;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Bitmap getPhotoBitmap() {
        return photoBitmap;
    }

    public void setPhotoBitmap(Bitmap photoBitmap) {
        this.photoBitmap = photoBitmap;
    }
}
