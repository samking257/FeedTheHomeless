package com.example.feedthehomeless.RestaurantUI.settings;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.feedthehomeless.LoginActivity;
import com.example.feedthehomeless.R;
import com.example.feedthehomeless.RestaurantActivity;
import com.example.feedthehomeless.User;
import com.example.feedthehomeless.UserMapper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class SettingsFragment extends Fragment {

    private Button btnResetPassword, btnDeleteAccount, btnLogout;
    private FirebaseAuth mAuth;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.restaurant_settings, container, false);
        ((RestaurantActivity) getActivity())
                .setActionBarTitle("Settings");
        initialiseComponents(root);
        return root;
    }

    private void initialiseComponents(View root) {
        btnResetPassword = root.findViewById(R.id.btnRestaurantResetPassword);
        btnDeleteAccount = root.findViewById(R.id.btnRestaurantDeleteAccount);
        btnLogout = root.findViewById(R.id.btnRestaurantLoginOut);
        mAuth = FirebaseAuth.getInstance();

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUserOut();
            }
        });
        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPassword();
            }
        });
        btnDeleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeleteAccount();
            }
        });
    }

    private void DeleteAccount() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        mAuth.getCurrentUser().delete();
                        signUserOut();
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        dialog.dismiss();
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("Are you sure you want to delete this Account?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

    private void resetPassword() {
        String emailAddress = mAuth.getCurrentUser().getEmail();
        mAuth.sendPasswordResetEmail(emailAddress)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getActivity(), "Email sent.",
                                    Toast.LENGTH_SHORT).show();
                        } else{
                            Toast.makeText(getActivity(), "Reset failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void signUserOut() {
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);
    }
}