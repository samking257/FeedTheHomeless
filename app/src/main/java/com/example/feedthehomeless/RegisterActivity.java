package com.example.feedthehomeless;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    RadioGroup rgAccountType;
    RadioButton radioButton;
    Button btnRegister;
    EditText userEmail, userPassword, companyName, companyAddress;
    CheckBox delivery, collection;
    ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mDatabase;
    String accountType = null;
    private LatLng userLatLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        rgAccountType = findViewById(R.id.rgAccountType);
        btnRegister = findViewById(R.id.btnCreateAccount);
        userEmail = findViewById(R.id.txtusernameReg);
        userPassword = findViewById(R.id.txtpasswordReg);
        companyName = findViewById(R.id.txtCompanyName);
        companyAddress = findViewById(R.id.txtAddress);
        delivery = findViewById(R.id.cbDelivery);
        collection = findViewById(R.id.cbCollection);
        progressBar = findViewById(R.id.pbLoading);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseFirestore.getInstance();

        rgAccountType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                radioButton = findViewById(rgAccountType.getCheckedRadioButtonId());
                String type = radioButton.getText().toString();

                if(type.equals("Council")){
                    companyName.setVisibility(View.GONE);
                    companyAddress.setVisibility(View.GONE);
                    delivery.setVisibility(View.GONE);
                    collection.setVisibility(View.GONE);
                } else{
                    companyName.setVisibility(View.VISIBLE);
                    companyAddress.setVisibility(View.VISIBLE);
                    delivery.setVisibility(View.VISIBLE);
                    collection.setVisibility(View.VISIBLE);
                }
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if fields empty
                checkAddress();
                CreateNewAccount();
            }
        });
    }

    private void checkAddress() {
        String address = companyAddress.getText().toString();
        List<Address> addressList;
        Geocoder geocoder = new Geocoder(this);

        try {
            addressList = geocoder.getFromLocationName(address, 6);
            if(addressList != null){
                Address userAddress = addressList.get(0);
                userLatLng = new LatLng(userAddress.getLatitude(), userAddress.getLongitude());
                Log.i("Tag", userLatLng.toString());
            } else{
                Toast.makeText(this, "Address not found", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void CreateNewAccount() {

        String email = userEmail.getText().toString();
        String password = userPassword.getText().toString();

        if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password)){
            Toast.makeText(this, "Please fill fields above.", Toast.LENGTH_LONG).show();
        }
        else{
            progressBar.setVisibility(View.VISIBLE);
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                radioButton = findViewById(rgAccountType.getCheckedRadioButtonId());
                                accountType = radioButton.getText().toString();
                                if(!accountType.equals("Council")){
                                    addUserToDatabase();
                                } else{
                                    Intent intent2 = new Intent(RegisterActivity.this, CouncilActivity.class);
                                    startActivity(intent2);
                                }
                            } else{
                                String message = task.getException().toString();
                                Toast.makeText(RegisterActivity.this, "Authentication failed. " + message, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    public void startCorrectActivity() {
        switch (accountType) {
            case "Restaurant":
                Intent intent = new Intent(RegisterActivity.this, RestaurantActivity.class);
                startActivity(intent);
                break;
            case "Shelter":
                Intent intent1 = new Intent(RegisterActivity.this, ShelterActivity.class);
                startActivity(intent1);
                break;
        }
    }

    private void addUserToDatabase() {
        String name = companyName.getText().toString();
        String address = companyAddress.getText().toString();
        String uid = mAuth.getCurrentUser().getUid();
        String deliveryStatus = getDeliveryStatus();
        GeoPoint geoPoint = new GeoPoint(userLatLng.latitude, userLatLng.longitude);

        User user1 = new User();
        user1.latLng = geoPoint;
        user1.donations = 0;
        user1.deliveryStatus = deliveryStatus;
        user1.activated = false;
        user1.address = address;
        user1.companyName = name;
        user1.accountType = accountType;

        // Add a new document with a generated ID
        mDatabase.collection("users").document(uid)
                .set(user1)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(RegisterActivity.this, "Account created.", Toast.LENGTH_SHORT).show();
                        startCorrectActivity();                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(RegisterActivity.this, "User details failed. " + e, Toast.LENGTH_SHORT).show();
                        Log.w("MyTag", "Error writing document", e);
                    }
                });
    }

    private String getDeliveryStatus() {
        String status = null;

        if(delivery.isChecked()){
            status = "delivery";
        } else if(collection.isChecked()){
            status = "collection";
        } else if (delivery.isChecked() && collection.isChecked()){
            status = "both";
        }
        return status;
    }
}
