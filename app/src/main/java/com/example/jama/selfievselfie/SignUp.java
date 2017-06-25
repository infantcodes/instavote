package com.example.jama.selfievselfie;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.jama.selfievselfie.model.RegisterDetails;
import com.example.jama.selfievselfie.model.RegisterDetails;
import com.google.android.gms.common.data.Freezable;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;
import java.util.Map;

public class SignUp extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    EditText editTextName, editTextPassword, editTextCpassword, editTextUsername, editTextEmail;
    Button signUp;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Sign Up");

        progressDialog = new ProgressDialog(this);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextCpassword = (EditText) findViewById(R.id.editTextCpassword);
        editTextName = (EditText) findViewById(R.id.editTextName);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        editTextUsername = (EditText) findViewById(R.id.editTextUsername);
        signUp = (Button) findViewById(R.id.buttonSignUp);

        //SIGN UP NEW USER
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = editTextEmail.getText().toString();
                final String username = editTextUsername.getText().toString();
                String cPassword = editTextCpassword.getText().toString();
                final String password = editTextPassword.getText().toString();
                final String name = editTextName.getText().toString();
                final  String profileImage = "https://firebasestorage.googleapis.com/v0/b/selfie-v-selfie.appspot.com/o/Default%20Image%2Fdownload.png?alt=media&token=31a245c2-50ba-4a73-8f95-30079ecbd7a2";
                final  String bio = "";

                if (email.equals("") || password.equals("") || username.equals("") || name.equals("")){
                    Toast.makeText(SignUp.this, "Please Fill In All Details", Toast.LENGTH_SHORT).show();
                }else if (!(cPassword.equals(password))){
                    Toast.makeText(SignUp.this, "Please Confirm Password", Toast.LENGTH_SHORT).show();
                }else {
                    progressDialog.setMessage("Loading ....");
                    progressDialog.setCancelable(false);
                    progressDialog.show();

                    mAuth.createUserWithEmailAndPassword(email,password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (!task.isSuccessful()){
                                        Toast.makeText(SignUp.this, "Unable To Sign Up", Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();
                                    }else {

                                        Map map = new HashMap();
                                        map.put("profileImage", profileImage);
                                        map.put("username", username);
                                        map.put("name", name);
                                        map.put("email", email);
                                        map.put("bio", bio);
                                        mDatabase.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Profile Info").setValue(map);
                                        mDatabase.child("Search Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(map);

                                        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
                                        DatabaseReference send = FirebaseDatabase.getInstance().getReference();

                                        send.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                .child("Notification Token").child(refreshedToken).setValue(true);

                                        userVerification();
                                        progressDialog.dismiss();
                                    }
                                }
                            });
                }
            }
        });

    }
    //SEND VERIFICATION LINK TO NEW USER
    private void userVerification(){
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    final ConstraintLayout constraintLayout = (ConstraintLayout) findViewById(R.id.layout);
                    Snackbar snackbar = Snackbar
                            .make(constraintLayout, "Verification Link Has Been Sent To Your Account", Snackbar.LENGTH_INDEFINITE)
                            .setAction("OK", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent login = new Intent(SignUp.this, SignIn.class);
                                    startActivity(login);
                                    progressDialog.dismiss();
                                    finish();
                                }
                            });
                    snackbar.show();
                }
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

}
