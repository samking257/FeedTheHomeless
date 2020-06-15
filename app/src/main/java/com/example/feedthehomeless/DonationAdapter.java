package com.example.feedthehomeless;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DonationAdapter extends RecyclerView.Adapter<DonationAdapter.ViewHolder> {

    private static Context context;
    private static FirebaseFirestore mDatabase;
    private static List<Donation> donationsList = new ArrayList<>();
    private static Donation donation = new Donation();

    public DonationAdapter(Context context, List<Donation> donationsList) {
        this.context = context;
        this.donationsList = donationsList;
        this.mDatabase = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.activity_donation_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        donation = donationsList.get(position);
        holder.lblName.setText("Name: " + donation.restaurant);
        holder.lblTime.setText("Time: " + donation.dateTime);

        if(donation.received){
            holder.cbReceived.setChecked(true);
            holder.cbReceived.setClickable(false);
        }else{
            holder.cbReceived.setChecked(false);
            holder.cbReceived.setClickable(true);
        }
    }

    @Override
    public int getItemCount() {
        return donationsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView lblName, lblTime;
        CheckBox cbReceived;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            lblName = itemView.findViewById(R.id.lblRestaurant);
            lblTime = itemView.findViewById(R.id.lblTime);
            cbReceived = itemView.findViewById(R.id.cbReceived);

            cbReceived.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){
                        updateReceived();
                        cbReceived.setClickable(false);
                        Toast.makeText(context, "Donation Completed", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    private static void updateReceived(){
        DocumentReference documentReference = mDatabase.collection("donations").document(donation.dateTime);
        documentReference
                .update("received", true)
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
}
