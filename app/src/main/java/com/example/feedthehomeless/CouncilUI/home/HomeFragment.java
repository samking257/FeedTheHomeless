package com.example.feedthehomeless.CouncilUI.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.feedthehomeless.Donation;
import com.example.feedthehomeless.DonationMapper;
import com.example.feedthehomeless.R;
import com.example.feedthehomeless.User;
import com.example.feedthehomeless.UserMapper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HomeFragment extends Fragment {

    TextView lblShelter, lblRestaurant, lblDonations;
    private FirebaseFirestore mDatabase;
    UserMapper userMapper = new UserMapper();
    DonationMapper donationMapper = new DonationMapper();
    List<User> shelterList = new ArrayList<>();
    List<User> restaurantList = new ArrayList<>();
    List<Donation> donationsList = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.council_home, container, false);
        initialiseComponents(root);
        getUsers();
        getDonations();
        return root;
    }

    private void getDonations() {
        mDatabase.collection("donations")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        Map<String, Object> docData;
                        if(task.isSuccessful()){
                            for(QueryDocumentSnapshot doc : task.getResult()){
                                docData = doc.getData();
                                Donation donation = new Donation();
                                donationsList.add(donationMapper.DonationMapper(docData, donation));
                            }
                            //set text
                            lblDonations.setText("Total Donations: " + donationsList.size());
                        } else{
                            Log.d("MyTag", "Error getting documents", task.getException());
                        }
                    }
                });
    }

    private void getUsers() {
        mDatabase.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        Map<String, Object> docData;
                        if(task.isSuccessful()){
                            for(QueryDocumentSnapshot doc : task.getResult()){
                                docData = doc.getData();
                                if(docData.get("accountType").toString().equals("Shelter")){
                                    User user1 = new User();
                                    shelterList.add(userMapper.UserMapper(docData, user1));
                                } else if(docData.get("accountType").toString().equals("Restaurant")){
                                    User user1 = new User();
                                    restaurantList.add(userMapper.UserMapper(docData, user1));
                                }
                            }
                            //set text
                            lblShelter.setText("Total Shelter Users: " + shelterList.size());
                            lblRestaurant.setText("Total Restaurant Users: " + restaurantList.size());
                        } else{
                            Log.d("MyTag", "Error getting documents", task.getException());
                        }
                    }
                });
    }

    private void initialiseComponents(View root) {
        lblShelter = root.findViewById(R.id.lblTotalShelter);
        lblRestaurant = root.findViewById(R.id.lblTotalRestaur);
        lblDonations = root.findViewById(R.id.lblTotalDonations);
        mDatabase = FirebaseFirestore.getInstance();
    }
}