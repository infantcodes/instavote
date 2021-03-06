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
import java.util.jar.Attributes;

public class SignUp extends AppCompatActivity {

    EditText editTextName, editTextPassword, editTextCpassword, editTextUsername, editTextEmail;
    Button signUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Sign Up");

        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextCpassword = (EditText) findViewById(R.id.editTextCpassword);
        editTextName = (EditText) findViewById(R.id.editTextName);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        signUp = (Button) findViewById(R.id.buttonSignUp);

        //SIGN UP NEW USER
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = editTextEmail.getText().toString();
                String cPassword = editTextCpassword.getText().toString();
                final String password = editTextPassword.getText().toString();
                final String name = editTextName.getText().toString();
                final  String profileImage = "https://firebasestorage.googleapis.com/v0/b/selfie-v-selfie.appspot.com/o/Default%20Image%2Fdownload.png?alt=media&token=31a245c2-50ba-4a73-8f95-30079ecbd7a2";
                final  String bio = "";

                if (email.equals("") || password.equals("") || name.equals("")){
                    Toast.makeText(SignUp.this, "Please Fill In All Details", Toast.LENGTH_SHORT).show();
                }else if (!(cPassword.equals(password))){
                    Toast.makeText(SignUp.this, "Please Confirm Password", Toast.LENGTH_SHORT).show();
                }else {
                    Intent intent = new Intent(SignUp.this, SignUp2.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("names", name);
                    bundle.putString("email", email);;
                    bundle.putString("password", password);
                    bundle.putBoolean("boolean", false);
                    intent.putExtras(bundle);
                    startActivity(intent);
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
                                    Intent login = new Intent(SignUp.this, MainLoginActivity.class);
                                    startActivity(login);
                                    //progressDialog.dismiss();
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
