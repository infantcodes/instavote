package com.example.jama.selfievselfie;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class SignIn extends AppCompatActivity {

    Button logIn;
    EditText editTextEmail, editTestPassword;
    TextView passwordReset;
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {

            Intent signedIn = new Intent(SignIn.this, MainTab.class);
            startActivity(signedIn);
            finish();

            /*if (user.isEmailVerified() == true){

            }else{
                Snackbar snackbar = Snackbar
                        .make(coordinatorLayout, "Account Is Not Verified", Snackbar.LENGTH_INDEFINITE)
                        .setAction("VERIFY", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                userVerification();
                            }
                        });
                snackbar.show();
            }*/
        } else {
            // No user is signed in
        }

        progressDialog = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signUp = new Intent(SignIn.this, SignUp.class);
                startActivity(signUp);
            }
        });

        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTestPassword = (EditText) findViewById(R.id.editTextPassword);
        passwordReset = (TextView) findViewById(R.id.txtForgotPassword);
        logIn = (Button) findViewById(R.id.buttonLogIn);

        passwordReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forgotPasswordDialog();
            }
        });

        logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editTextEmail.getText().toString().trim();
                String password = editTestPassword.getText().toString().trim();

                if (email.equals("") || password.equals("")){
                    Toast.makeText(SignIn.this, "Please Fill In All Details", Toast.LENGTH_SHORT).show();
                }else {
                    progressDialog.setMessage("Loading ....");
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (!task.isSuccessful()){
                                        progressDialog.dismiss();
                                        Toast.makeText(SignIn.this, "Unable To Log In", Toast.LENGTH_SHORT).show();
                                    }else {
                                        progressDialog.dismiss();
                                        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
                                        DatabaseReference send = FirebaseDatabase.getInstance().getReference();

                                        send.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                .child("Notification Token").child(refreshedToken).setValue(true);
                                        Intent signedIn = new Intent(SignIn.this, MainTab.class);
                                        startActivity(signedIn);
                                        finish();
                                    }
                                }
                            });
                }
            }

        });
    }

    /*private void userVerification(){
        final CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id.layout);
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (!task.isSuccessful()){
                    Snackbar snackbar1 = Snackbar
                            .make(coordinatorLayout, "Email Could Not Be Verified, Try Another", Snackbar.LENGTH_LONG);
                    snackbar1.show();
                }else {
                    Snackbar snackbar = Snackbar
                            .make(coordinatorLayout, "Verification Link Has Been Sent To Your Account", Snackbar.LENGTH_INDEFINITE);
                    snackbar.show();
                }
            }
        });
    }*/

    private void forgotPasswordDialog() {
        final CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id.layout);
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.password_reset_dialog, null);
        final EditText passwordReset = (EditText) alertLayout.findViewById(R.id.editTextPasswordReset);

        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Password Reset");
        alert.setMessage("Type Your Email Account, So We Can Send You A Link To Reset Your Password");
        alert.setView(alertLayout);
        alert.setCancelable(false);
        alert.setPositiveButton("RESET", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                progressDialog.setMessage("Sending Password Reset Link");
                progressDialog.show();
                String emailAddress = passwordReset.getText().toString().trim();
                FirebaseAuth auth = FirebaseAuth.getInstance();
                if (emailAddress.equals("")){
                    Snackbar snackbar = Snackbar
                            .make(coordinatorLayout, "Enter Your Email Address",Snackbar.LENGTH_LONG);
                    snackbar.show();
                    progressDialog.dismiss();
                }else {
                    auth.sendPasswordResetEmail(emailAddress)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (!task.isSuccessful()){
                                        Snackbar snackbar = Snackbar
                                                .make(coordinatorLayout, "Make Sure Email Is Typed Correct",Snackbar.LENGTH_LONG);
                                        snackbar.show();
                                        progressDialog.dismiss();
                                    }else {
                                        Snackbar snackbar = Snackbar
                                                .make(coordinatorLayout, "Password Reset Link Has Been Sent",Snackbar.LENGTH_LONG);
                                        snackbar.show();
                                        progressDialog.dismiss();
                                    }
                                }
                            });
                }

            }
        }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        AlertDialog dialog = alert.create();
        dialog.show();
    }
}