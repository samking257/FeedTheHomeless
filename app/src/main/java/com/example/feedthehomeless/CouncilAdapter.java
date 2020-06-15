package com.example.feedthehomeless;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class CouncilAdapter extends RecyclerView.Adapter<CouncilAdapter.ViewHolder>{

    private static Context context;
    private static List<Donation> donationsList = new ArrayList<>();
    private static Donation donation = new Donation();

    public CouncilAdapter(Context context, List<Donation> donationsList) {
        this.context = context;
        this.donationsList = donationsList;
    }

    @NonNull
    @Override
    public CouncilAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.activity_council_layout, parent, false);
        return new CouncilAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final CouncilAdapter.ViewHolder holder, int position) {
        donation = donationsList.get(position);
        holder.lblRestaurant.setText("Restaurant: " + donation.restaurant);
        holder.lblTime.setText("Time: " + donation.dateTime);
        holder.lblShelter.setText("Shelter: " + donation.shelter);

        if(donation.received){
            holder.lblReceived.setText("Received");
        } else {
            holder.lblReceived.setText("In Progress");
        }
    }

    @Override
    public int getItemCount() {
        return donationsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView lblShelter, lblTime, lblRestaurant, lblReceived;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            lblRestaurant = itemView.findViewById(R.id.lblCouncilRestaurant);
            lblTime = itemView.findViewById(R.id.lblCouncilTime);
            lblShelter = itemView.findViewById(R.id.lblCouncilShelter);
            lblReceived = itemView.findViewById(R.id.lblCouncilReceived);
        }
    }
}
