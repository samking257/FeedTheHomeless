package com.example.feedthehomeless.ShelterUI.account;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.feedthehomeless.R;
import com.example.feedthehomeless.ShelterActivity;
import com.example.feedthehomeless.User;
import com.example.feedthehomeless.UserMapper;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.io.IOException;
import java.util.List;

public class AccountFragment extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseFirestore mDatabase;
    private EditText name, donations, address;
    private CheckBox delivery, collection;
    private FloatingActionButton edit;
    private Button save;
    private String uid;
    User user = new User();
    UserMapper userMapper = new UserMapper();
    private LatLng shelterLatLng;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.shelter_account, container, false);
        ((ShelterActivity) getActivity())
                .setActionBarTitle("Account");
        initialiseComponents(root);
        return root;
    }

    private void initialiseComponents(View root) {
        name = root.findViewById(R.id.txtShelterName);
        donations = root.findViewById(R.id.txtShelterDonations);
        address = root.findViewById(R.id.txtShelterAddress);
        delivery = root.findViewById(R.id.cbShelterDelivery);
        collection = root.findViewById(R.id.cbShelterCollection);
        edit = root.findViewById(R.id.btnEdit);
        save = root.findViewById(R.id.btnShelterSave);
        mDatabase = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getCurrentUser().getUid();

        name.setEnabled(false);
        donations.setEnabled(false);
        address.setEnabled(false);

        getDocument();

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enableEdit();
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveEdits();

            }
        });
    }

    private void setUserInfo() {
        name.setText(user.companyName);
        address.setText(user.address);
        donations.setText(String.valueOf(user.donations));

        switch (user.deliveryStatus){
            case "delivery":
                delivery.setChecked(true);
                break;
            case "collection":
                collection.setChecked(true);
                break;
            case "both":
                delivery.setChecked(true);
                collection.setChecked(true);
                break;
        }
    }

    private void saveEdits() {
        checkAddress();
        save.setVisibility(View.GONE);
        name.setEnabled(false);
        address.setEnabled(false);
        delivery.setClickable(false);
        collection.setClickable(false);
        edit.setEnabled(true);

        GeoPoint geoPoint = new GeoPoint(shelterLatLng.latitude, shelterLatLng.longitude);
        DocumentReference documentReference = mDatabase.collection("users").document(uid);
        String deliveryStatus = getDeliveryStatus();

        documentReference
                .update("companyName", name.getText().toString(),
                        "address", address.getText().toString(), "latLng", geoPoint,
                        "deliveryStatus", deliveryStatus)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("MyTag", "DocumentSnapshot successfully updated!");
                        Toast.makeText(getActivity(), "Update Successful.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("MyTag", "Error updating document", e);
                    }
                });
        getDocument();
    }

    private void checkAddress() {
        String comAddress = address.getText().toString();
        List<Address> addressList;
        Geocoder geocoder = new Geocoder(getActivity());

        try {
            addressList = geocoder.getFromLocationName(comAddress, 6);

            if(addressList != null){
                Address userAddress = addressList.get(0);
                shelterLatLng = new LatLng(userAddress.getLatitude(), userAddress.getLongitude());
                Log.i("Tag", shelterLatLng.toString());
            } else{
                Toast.makeText(getActivity(), "Address not found", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    private void enableEdit() {
        save.setVisibility(View.VISIBLE);
        name.setEnabled(true);
        address.setEnabled(true);
        delivery.setClickable(true);
        collection.setClickable(true);
        edit.setEnabled(false);
    }

    public void getDocument() {
        String uid = mAuth.getCurrentUser().getUid();
        DocumentReference documentReference = mDatabase.collection("users").document(uid);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if(documentSnapshot.exists()){
                        if(documentSnapshot.getData()!=null){
                            user = userMapper.UserMapper(documentSnapshot.getData(), user);
                            setUserInfo();
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
}