package com.example.feedthehomeless.CouncilUI.data;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.feedthehomeless.CouncilActivity;
import com.example.feedthehomeless.CouncilAdapter;
import com.example.feedthehomeless.Donation;
import com.example.feedthehomeless.DonationMapper;
import com.example.feedthehomeless.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DataFragment extends Fragment {

    private FirebaseFirestore mDatabase;
    DonationMapper donationMapper = new DonationMapper();
    List<Donation> donationsList = new ArrayList<>();
    RecyclerView recyclerView;
    CouncilAdapter councilAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.council_data, container, false);
        ((CouncilActivity) getActivity())
                .setActionBarTitle("Data");
        initialiseComponents(root);
        setUpRecycler();
        return root;
    }

    private void initialiseComponents(View root) {
        mDatabase = FirebaseFirestore.getInstance();
        recyclerView = root.findViewById(R.id.rvCouncilDonations);
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
                                Donation donation1 = new Donation();
                                donationsList.add(donationMapper.DonationMapper(docData, donation1));
                            }
                            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                            recyclerView.setHasFixedSize(true);
                            councilAdapter = new CouncilAdapter(getActivity(), donationsList);
                            recyclerView.setAdapter(councilAdapter);
                        } else{
                            Log.d("MyTag", "Error getting documents", task.getException());
                        }
                    }
                });
    }
}