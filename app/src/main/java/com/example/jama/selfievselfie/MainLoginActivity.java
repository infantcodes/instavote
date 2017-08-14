package com.example.jama.selfievselfie;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.*;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.TwitterAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class MainLoginActivity extends AppCompatActivity {

    Button goolge;
    TwitterLoginButton twitterbtn;
    LoginButton facebookbtn;
    int RC_SIGN_IN = 1;
    CallbackManager mCallbackManager;
    GoogleApiClient mGoogleApiClient;
    DatabaseReference databaseReference;
    Button logIn;
    EditText editTextEmail, editTestPassword;
    TextView passwordReset, signUp;
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Twitter.initialize(this);
        setContentView(R.layout.activity_main_login);

        checkIfUserIsSignedIn();

        mAuth = FirebaseAuth.getInstance();

        goolge = (Button) findViewById(R.id.buttonGoogleSignIn);
        twitterbtn = (TwitterLoginButton) findViewById(R.id.buttonTwitter);

        progressDialog = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();

        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        signUp = (TextView) findViewById(R.id.textViewSignUp);
        editTestPassword = (EditText) findViewById(R.id.editTextPassword);
        passwordReset = (TextView) findViewById(R.id.txtForgotPassword);
        logIn = (Button) findViewById(R.id.buttonLogIn);

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainLoginActivity.this, SignUp.class);
                startActivity(intent);
            }
        });

        passwordReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forgotPasswordDialog();
            }
        });

        logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editTextEmail.getText().toString();
                String password = editTestPassword.getText().toString();

                if (email.equals("") || password.equals("")){
                    Toast.makeText(MainLoginActivity.this, "Please Fill In All Details", Toast.LENGTH_SHORT).show();
                }else {
                    progressDialog.setMessage("Signing you in....");
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (!task.isSuccessful()){
                                        progressDialog.dismiss();
                                        Toast.makeText(MainLoginActivity.this, "Unable To Log In", Toast.LENGTH_SHORT).show();
                                    }else {
                                        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
                                        DatabaseReference send = FirebaseDatabase.getInstance().getReference();

                                        send.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                .child("Notification Token").child(refreshedToken).setValue(true);
                                        checkIfUserIsSignedIn();
                                        progressDialog.dismiss();
                                        finish();
                                    }
                                }
                            });
                }
            }

        });

//        ********************FACEBOOK SIGN IN***************************

        mCallbackManager = CallbackManager.Factory.create();
        facebookbtn = (LoginButton) findViewById(R.id.buttonFacebook);
        facebookbtn.setReadPermissions("email");

        facebookbtn.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });

//        ********************TWITTER LOGIN*******************
        twitterbtn.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                handleTwitterSession(result.data);
            }

            @Override
            public void failure(TwitterException exception) {

            }
        });

//        ****************************************************

//         ********************GOOGLE SIGN IN******************
         // Configure Google Sign In
         GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Toast.makeText(MainLoginActivity.this, "Error with google sign in", Toast.LENGTH_SHORT).show();
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        goolge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

//        *************************************************************
    }

    private void handleFacebookAccessToken(AccessToken accessToken) {
        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
//                    Check if user exists
                    checkIfUserIsSignedIn();
                }else {
                    Toast.makeText(MainLoginActivity.this, ""+task.getException(), Toast.LENGTH_SHORT).show();
                    Log.d("test fb", "handleFacebookAccessToken:" + task.getException());
                }
            }
        });
    }

    private void handleTwitterSession(TwitterSession session) {

        AuthCredential credential = TwitterAuthProvider.getCredential(
                session.getAuthToken().token,
                session.getAuthToken().secret);

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //Check if user exists
                            checkIfUserIsSignedIn();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(MainLoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }



    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //Check if user exists
                            checkIfUserIsSignedIn();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(MainLoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                // Google Sign In failed, update UI appropriately
                // ...
                Toast.makeText(this, "sign in failed", Toast.LENGTH_SHORT).show();
            }
        }else {
            twitterbtn.onActivityResult(requestCode, resultCode, data);
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void forgotPasswordDialog() {
        final ConstraintLayout coordinatorLayout = (ConstraintLayout) findViewById(R.id.layout);
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

//    CHEK IF USER EXISTS IN THE DATABASE
    private void checkIfUserExists(final String uid, final String email, final String photoUrl, final String names){
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                    Intent intent = new Intent(MainLoginActivity.this,SignUp2.class);
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("boolean", true);
                    bundle.putString("email", email);
                    bundle.putString("photoUrl", photoUrl);
                    bundle.putString("names", names);
                    bundle.putString("uid", uid);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }else {
                    checkIfUserIsSignedIn();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void checkIfUserIsSignedIn(){
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            final String userid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.hasChild(userid)){
                        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        String names = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
                        String photoUrl = FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl().toString();
                        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();

                        Toast.makeText(MainLoginActivity.this, "Looks like you have not setup you username," +
                                " lets try to do that now", Toast.LENGTH_LONG).show();

                        checkIfUserExists(uid, email, photoUrl, names);
                    }else {
                        Intent signedIn = new Intent(MainLoginActivity.this, MainTab2.class);
                        startActivity(signedIn);
                        finish();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } else {
            // No user is signed in
            Toast.makeText(this, "user is not signed in", Toast.LENGTH_SHORT).show();
        }
    }

}
