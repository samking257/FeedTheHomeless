package com.example.feedthehomeless;

import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class UnitTests {

    public int getShelterTotal(List<String> list){
        int total = 0;
        for (String i: list){
            if(i.equals("Shelter")){
                total = total + 1;
            }
        }
        return total;
    }

    public int getRestaurantTotal(List<String> list){
        int total = 0;
        for (String i: list){
            if(i.equals("Restaurant")){
                total = total + 1;
            }
        }
        return total;
    }

    @Test
    public void test1_totalShelters(){
        List<String> test = Arrays.asList("Restaurant", "Shelter",
                "Shelter", "Shelter", "Restaurant", "Restaurant", "Shelter");

        int expected = 4;
        int result = getShelterTotal(test);

        assertEquals(expected, result);
    }

    @Test
    public void test2_totalShelters(){
        List<String> test = Arrays.asList("Shelter", "Restaurant", "Shelter", "Shelter", "Restaurant",
                "Restaurant", "Shelter", "Shelter", "Restaurant", "Shelter", "Shelter", "Shelter");

        int expected = 8;
        int result = getShelterTotal(test);

        assertEquals(expected, result);
    }

    @Test
    public void test1_totalRestaurants(){
        List<String> test = Arrays.asList("Restaurant", "Shelter",
                "Shelter", "Shelter", "Restaurant", "Restaurant", "Shelter");

        int expected = 3;
        int result = getRestaurantTotal(test);

        assertEquals(expected, result);
    }

    @Test
    public void test2_totalRestaurants(){
        List<String> test = Arrays.asList("Shelter", "Restaurant", "Shelter", "Shelter", "Restaurant",
                "Restaurant", "Shelter", "Shelter", "Restaurant", "Shelter", "Shelter", "Shelter");

        int expected = 4;
        int result = getRestaurantTotal(test);

        assertEquals(expected, result);
    }

    @Test
    public void test1_UserMapper(){
        User expected = new User();
        expected.accountType = "Shelter";
        expected.activated = false;
        expected.deliveryStatus = "Delivery";
        expected.latLng = null;
        expected.donations = 0;
        expected.address = "123 North Street";
        expected.companyName = "Test";

        Map<String, Object> map = new HashMap<>();
        map.put("accountType", "Shelter");
        map.put("activated", false);
        map.put("deliveryStatus", "Delivery");
        map.put("latLng", null);
        map.put("donations", 0);
        map.put("address", "123 North Street");
        map.put("companyName", "Test");

        UserMapper userMapper = new UserMapper();
        User user = new User();
        User results = userMapper.UserMapper(map, user);

        assertEquals(expected.accountType, results.accountType);
        assertEquals(expected.address, results.address);
        assertEquals(expected.activated, results.activated);
        assertEquals(expected.donations, results.donations);
    }

    @Test
    public void test2_UserMapper(){
        User expected = new User();
        expected.accountType = "Restaurant";
        expected.activated = true;
        expected.deliveryStatus = "Both";
        expected.latLng = null;
        expected.donations = 5;
        expected.address = "246 South Street";
        expected.companyName = "Test";

        Map<String, Object> map = new HashMap<>();
        map.put("accountType", "Restaurant");
        map.put("activated", true);
        map.put("deliveryStatus", "Both");
        map.put("latLng", null);
        map.put("donations", 5);
        map.put("address", "246 South Street");
        map.put("companyName", "Test");

        UserMapper userMapper = new UserMapper();
        User user = new User();
        User results = userMapper.UserMapper(map, user);

        assertEquals(expected.accountType, results.accountType);
        assertEquals(expected.address, results.address);
        assertEquals(expected.activated, results.activated);
        assertEquals(expected.donations, results.donations);
    }

    @Test
    public void test1_DonationMapper(){
        Donation expected = new Donation();
        expected.restaurant = "TestRest";
        expected.shelter = "TestShelter";
        expected.received = false;
        expected.dateTime = "30 March 2020 17:50:45";

        Map<String, Object> map = new HashMap<>();
        map.put("restaurant", "TestRest");
        map.put("shelter", "TestShelter");
        map.put("received", false);
        map.put("dateTime", "30 March 2020 17:50:45");

        DonationMapper donationMapper = new DonationMapper();
        Donation donation = new Donation();
        Donation results = donationMapper.DonationMapper(map, donation);

        assertEquals(expected.dateTime, results.dateTime);
        assertEquals(expected.restaurant, results.restaurant);
        assertEquals(expected.shelter, results.shelter);
        assertEquals(expected.received, results.received);
    }

    @Test
    public void test2_DonationMapper(){
        Donation expected = new Donation();
        expected.restaurant = "RestName";
        expected.shelter = "ShelterName";
        expected.received = true;
        expected.dateTime = "31 March 2020 19:30:15";

        Map<String, Object> map = new HashMap<>();
        map.put("restaurant", "RestName");
        map.put("shelter", "ShelterName");
        map.put("received", true);
        map.put("dateTime", "31 March 2020 19:30:15");

        DonationMapper donationMapper = new DonationMapper();
        Donation donation = new Donation();
        Donation results = donationMapper.DonationMapper(map, donation);

        assertEquals(expected.dateTime, results.dateTime);
        assertEquals(expected.restaurant, results.restaurant);
        assertEquals(expected.shelter, results.shelter);
        assertEquals(expected.received, results.received);
    }
}
