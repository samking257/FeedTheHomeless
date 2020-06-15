package com.example.feedthehomeless;

import com.google.firebase.firestore.GeoPoint;

public class User {
    public String accountType;
    public String companyName;
    public String address;
    public GeoPoint latLng;
    public String deliveryStatus;
    public Boolean activated;
    public int donations;
}
