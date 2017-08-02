package com.example.jama.selfievselfie;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.*;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.algolia.search.saas.AlgoliaException;
import com.algolia.search.saas.Client;
import com.algolia.search.saas.CompletionHandler;
import com.algolia.search.saas.Index;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by JAMA on 6/11/2017.
 */

public class SignUp2 extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    EditText editTextUsername;
    Button signUp;
    private ProgressDialog progressDialog;
    String Email, Password, Names;
    String ADMIN_API_KEY;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up2);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        Names = bundle.getString("names");
        Email = bundle.getString("email");
        Password = bundle.getString("password");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Almost Done");

        progressDialog = new ProgressDialog(this);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        editTextUsername = (EditText) findViewById(R.id.editTextUsername);
        signUp = (Button) findViewById(R.id.buttonSignUp);

        signUp.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {

                final String Username = editTextUsername.getText().toString();

                if (Username.equals("")){
                    Toast.makeText(SignUp2.this, "Please Fill In You Username", Toast.LENGTH_SHORT).show();
                }else {
                    Query query = FirebaseDatabase.getInstance().getReference().child("Search Users")
                            .orderByChild("username").equalTo(Username);
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getChildrenCount() > 0) {
                                Toast.makeText(SignUp2.this, "Username already taken, try another", Toast.LENGTH_SHORT).show();
                            }else {
                                progressDialog.setMessage("Signing you up...");
                                progressDialog.setCancelable(false);
                                progressDialog.show();

                                mAuth.createUserWithEmailAndPassword(Email, Password)
                                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                if (!task.isSuccessful()){
                                                    Toast.makeText(SignUp2.this, "Unable To Sign Up", Toast.LENGTH_SHORT).show();
                                                    progressDialog.dismiss();
                                                }else {

                                                    final  String profileImage = "https://firebasestorage.googleapis.com/v0/b/selfie-v-selfie.appspot.com/o/Default%20Image%2Fdownload.png?alt=media&token=31a245c2-50ba-4a73-8f95-30079ecbd7a2";
                                                    final  String bio = "";

                                                    DatabaseReference getSearchApiKey = FirebaseDatabase.getInstance().getReference().child("Algolia Credentials");
                                                    getSearchApiKey.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                            Map <String, String> map2 = (Map)dataSnapshot.getValue();
                                                            ADMIN_API_KEY = map2.get("Admin Api Key");
                                                            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                                                            Client client = new Client("CXR8DHPHLZ", ADMIN_API_KEY);

                                                            Index myIndex = client.initIndex("users");

                                                            JSONObject object = null;
                                                            try {
                                                                object = new JSONObject()
                                                                        .put("username", Username)
                                                                        .put("name", Names)
                                                            } catch (JSONException e) {
                                                                e.printStackTrace();
                                                            }
                                                            myIndex.addObjectAsync(object, uid, null);

                                                            Map map = new HashMap();
                                                            map.put("profileImage", profileImage);
                                                            map.put("username", Username);
                                                            map.put("name", Names);
                                                            map.put("email", Email);
                                                            map.put("bio", bio);
                                                            mDatabase.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Profile Info").setValue(map);

                                                            Map map1 = new HashMap();
                                                            map1.put("profileImage", profileImage);
                                                            map1.put("username", Username);
                                                            map1.put("name", Names);
                                                            mDatabase.child("Search Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(map1);

                                                            String refreshedToken = FirebaseInstanceId.getInstance().getToken();
                                                            DatabaseReference send = FirebaseDatabase.getInstance().getReference();

                                                            send.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                                    .child("Notification Token").child(refreshedToken).setValue(true);

                                                            userVerification();
                                                            progressDialog.dismiss();

                                                        }

                                                        @Override
                                                        public void onCancelled(DatabaseError databaseError) {

                                                        }
                                                    });
                                                }
                                            }
                                        });
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

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
                    final LinearLayout constraintLayout = (LinearLayout) findViewById(R.id.layout);
                    Snackbar snackbar = Snackbar
                            .make(constraintLayout, "Verification Link Has Been Sent To Your Account", Snackbar.LENGTH_INDEFINITE)
                            .setAction("OK", new android.view.View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent login = new Intent(SignUp2.this, SignIn.class);
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
