package com.example.feedthehomeless;

import com.google.firebase.firestore.GeoPoint;
import java.util.Map;

public class UserMapper {

    public User UserMapper(Map<String, Object> map, User user){
        user.accountType = map.get("accountType").toString();
        user.companyName = map.get("companyName").toString();
        user.address = map.get("address").toString();
        user.activated = (Boolean) map.get("activated");
        user.deliveryStatus = map.get("deliveryStatus").toString();
        user.donations = Integer.valueOf(map.get("donations").toString());
        user.latLng = (GeoPoint) map.get("latLng");
        return user;
    }
}
