package com.example.feedthehomeless;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    Button btnLogin, newAccount;
    EditText userEmail, userPassword;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mDatabase;
    UserMapper userMapper = new UserMapper();
    User user = new User();

    @Override
    public void onBackPressed() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btnLogin = findViewById(R.id.btnLogin2);
        userEmail = findViewById(R.id.txtusername);
        userPassword = findViewById(R.id.txtpassword);
        newAccount = findViewById(R.id.btnRegister);
        mDatabase = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInUser();

            }
        });

        newAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    private void signInUser() {
        String email = userEmail.getText().toString();
        String password = userPassword.getText().toString();

        if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password)){
            Toast.makeText(this, "Please fill fields above.", Toast.LENGTH_LONG).show();
        }
        else {
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(LoginActivity.this, "Authentication Successful.",
                                        Toast.LENGTH_SHORT).show();
                                getDocument();
                            } else {
                                String message = task.getException().toString();
                                Toast.makeText(LoginActivity.this, "Authentication failed. " + message,
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    public void startCorrectActivity() {
        switch (user.accountType) {
            case "Restaurant":
                Intent intent = new Intent(LoginActivity.this, RestaurantActivity.class);
                startActivity(intent);
                break;
            case "Shelter":
                Intent intent1 = new Intent(LoginActivity.this, ShelterActivity.class);
                startActivity(intent1);
                break;
        }
    }

    public void getDocument() {
        String uid = mAuth.getCurrentUser().getUid();
        final DocumentReference documentReference = mDatabase.collection("users").document(uid);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if(documentSnapshot.exists()){
                        user = userMapper.UserMapper(documentSnapshot.getData(), user);
                        startCorrectActivity();
                    }else{
                        //exception
                        Intent intent2 = new Intent(LoginActivity.this, CouncilActivity.class);
                        startActivity(intent2);
                    }
                }
            }
        });
    }
}
