package com.example.feedthehomeless;

import java.util.Map;

public class DonationMapper {

    public Donation DonationMapper(Map<String, Object> map, Donation donation){
        donation.dateTime = map.get("dateTime").toString();
        donation.received = (Boolean) map.get("received");
        donation.restaurant = map.get("restaurant").toString();
        donation.shelter = map.get("shelter").toString();
        return donation;
    }
}

