package com.example.feedthehomeless.ShelterUI.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.feedthehomeless.Donation;
import com.example.feedthehomeless.DonationAdapter;
import com.example.feedthehomeless.DonationMapper;
import com.example.feedthehomeless.R;
import com.example.feedthehomeless.User;
import com.example.feedthehomeless.UserMapper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HomeFragment extends Fragment {

    Switch activate;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mDatabase;
    UserMapper userMapper = new UserMapper();
    DonationMapper donationMapper = new DonationMapper();
    List<Donation> donationsList = new ArrayList<>();
    User user = new User();
    String uid;
    RecyclerView recyclerView;
    DonationAdapter donationAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.shelter_home, container, false);
        initialiseComponents(root);
        getDocument();
        setUpRecycler();
        activate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                updateActivation(isChecked);
            }
        });
        return root;
    }

    private void initialiseComponents(View root) {
        activate = root.findViewById(R.id.switchToggle);
        mDatabase = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getCurrentUser().getUid();
        recyclerView = root.findViewById(R.id.rvShelterDonations);
    }

    private void setUpRecycler() {
        mDatabase.collection("donations")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        Map<String, Object> docData;
                        if(task.isSuccessful()){
                            for(QueryDocumentSnapshot doc : task.getResult()){
                                docData = doc.getData();
                                String shelterName = docData.get("shelter").toString();
                                if(shelterName.equals(user.companyName)){
                                    Donation donation1 = new Donation();
                                    donationsList.add(donationMapper.DonationMapper(docData, donation1));
                                }
                            }
                            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                            recyclerView.setHasFixedSize(true);
                            donationAdapter = new DonationAdapter(getActivity(), donationsList);
                            recyclerView.setAdapter(donationAdapter);
                        } else{
                            Log.d("MyTag", "Error getting documents", task.getException());
                        }
                    }
                });
    }

    private void updateActivation(boolean isChecked) {
        DocumentReference documentReference = mDatabase.collection("users").document(uid);
        documentReference
                .update("activated", isChecked)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("MyTag", "DocumentSnapshot successfully updated!");
                        getDocument();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("MyTag", "Error updating document", e);
                    }
                });

    }

    private void setToggle() {
        if(user.activated){
            activate.setChecked(true);
        } else{
            activate.setChecked(false);
        }
    }

    private void getDocument() {
        final DocumentReference documentReference = mDatabase.collection("users").document(uid);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if(documentSnapshot.exists()){
                        user = userMapper.UserMapper(documentSnapshot.getData(), user);
                        setToggle();
                        Toast.makeText(getActivity(), "Updated.", Toast.LENGTH_SHORT).show();
                    }else{
                        //exception
                    }
                }
            }
        });
    }
}