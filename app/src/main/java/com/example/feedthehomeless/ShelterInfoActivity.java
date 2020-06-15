package com.example.feedthehomeless;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Locale;

public class ShelterInfoActivity extends AppCompatActivity {

    Button btnDonate;
    TextView name, address;
    String shelterUID;
    String RestaurantName;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mDatabase;
    User shelterUser = new User();
    User restaurantUser = new User();
    UserMapper userMapper = new UserMapper();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shelter_info);

        initialiseComponents();
        getRestaurantName();
        getDocument();
    }

    private void initialiseComponents() {
        btnDonate = findViewById(R.id.btnDonate);
        name = findViewById(R.id.lblInfoName);
        address = findViewById(R.id.lblInfoAddress);
        mDatabase = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        shelterUID = getIntent().getStringExtra("SHELTER_UID");
        btnDonate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createDonation();
            }
        });
    }

    private void createDonation() {
        Donation donation = new Donation();
        String currentDateTimeString = java.text.DateFormat.getDateTimeInstance().format(new Date());

        donation.shelter = shelterUser.companyName;
        donation.dateTime = currentDateTimeString;
        donation.received = false;
        donation.restaurant = RestaurantName;

        mDatabase.collection("donations").document(currentDateTimeString)
                .set(donation)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(ShelterInfoActivity.this, "Donation in Progress.", Toast.LENGTH_SHORT).show();
                                            }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ShelterInfoActivity.this, "Donation failed. " + e, Toast.LENGTH_SHORT).show();
                        Log.w("MyTag", "Error writing document", e);
                    }
                });
        UpdateRestaurant();
        UpdateShelter();
    }

    private void UpdateShelter() {
        int totalDonations = shelterUser.donations + 1;
        DocumentReference documentReference = mDatabase.collection("users").document(shelterUID);
        documentReference
                .update("donations", totalDonations)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("MyTag", "DocumentSnapshot successfully updated!");

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("MyTag", "Error updating document", e);
                    }
                });
    }

    private void UpdateRestaurant() {
        int totalDonations = restaurantUser.donations + 1;
        DocumentReference documentReference = mDatabase.collection("users").document(mAuth.getUid());
        documentReference
                .update("donations", totalDonations)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("MyTag", "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("MyTag", "Error updating document", e);
                    }
                });
    }

    private void getRestaurantName() {
        String uid = mAuth.getCurrentUser().getUid();
        final DocumentReference documentReference = mDatabase.collection("users").document(uid);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if(documentSnapshot.exists()){
                        restaurantUser = userMapper.UserMapper(documentSnapshot.getData(), restaurantUser);
                        RestaurantName = restaurantUser.companyName;
                    }else{
                        //exception
                    }
                }
            }
        });
    }

    private void getDocument() {
        DocumentReference documentReference = mDatabase.collection("users").document(shelterUID);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if(documentSnapshot.exists()){
                        if(!documentSnapshot.getData().equals(null)){
                            shelterUser = userMapper.UserMapper(documentSnapshot.getData(), shelterUser);
                            setInfo();
                        } else{
                            Log.d("MyTag", "No Data");
                        }
                    }else{
                        Log.d("MyTag", "No such document");
                    }
                } else {
                    Log.d("MyTag", "get failed with ", task.getException());
                }
            }
        });
    }

    private void setInfo() {
        name.setText(shelterUser.companyName);
        address.setText(shelterUser.address);
    }
}
