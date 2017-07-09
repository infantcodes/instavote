package com.example.jama.selfievselfie;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseArray;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainTab2 extends AppCompatActivity {

    int INT_CONSTANT_FOR_HOME_FRAGMENT = 1;
    int INT_CONSTANT_FOR_REQUESTS_FRAGMENT = 2;
    int INT_CONSTANT_FOR_PROFILE_FRAGMENT = 3;
    int INT_CONSTANT_FOR_MESSAGE_FRAGMENT = 4;
    int INT_CONSTANT_FOR_POST_FRAGMENT = 5;

    private TextView mTextMessage;
    SparseArray<Fragment> fragmentSparseArray;
    Fragment selectedFragment = null;
    DatabaseReference databaseReference;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    selectedFragment = fragmentSparseArray.get(INT_CONSTANT_FOR_HOME_FRAGMENT);
                    if (selectedFragment == null) {
                        selectedFragment = Home.newInstance();
                        fragmentSparseArray.put(INT_CONSTANT_FOR_HOME_FRAGMENT, selectedFragment);
                    }
                    break;
                case R.id.navigation_requests:
                    selectedFragment = fragmentSparseArray.get(INT_CONSTANT_FOR_REQUESTS_FRAGMENT);
                    if (selectedFragment == null) {
                        selectedFragment = Requests.newInstance();
                        fragmentSparseArray.put(INT_CONSTANT_FOR_REQUESTS_FRAGMENT, selectedFragment);
                    }
                    break;
                case R.id.navigation_post:
                    selectedFragment = fragmentSparseArray.get(INT_CONSTANT_FOR_POST_FRAGMENT);
                    if (selectedFragment == null) {
                        selectedFragment = ChoosePost.newInstance();
                        fragmentSparseArray.put(INT_CONSTANT_FOR_POST_FRAGMENT, selectedFragment);
                    }
                    break;
                case R.id.navigation_message:
                    selectedFragment = fragmentSparseArray.get(INT_CONSTANT_FOR_MESSAGE_FRAGMENT);
                    if (selectedFragment == null) {
                        selectedFragment = Message.newInstance();
                        fragmentSparseArray.put(INT_CONSTANT_FOR_MESSAGE_FRAGMENT, selectedFragment);
                    }
                    break;
                case R.id.navigation_profile:
                    selectedFragment = fragmentSparseArray.get(INT_CONSTANT_FOR_PROFILE_FRAGMENT);
                    if (selectedFragment == null) {
                        selectedFragment = Profile.newInstance();
                        fragmentSparseArray.put(INT_CONSTANT_FOR_PROFILE_FRAGMENT, selectedFragment);
                    }
                    break;
            }
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.content, selectedFragment);
            transaction.commit();
            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_tab2);

        final FrameLayout frameLayout = (FrameLayout) findViewById(R.id.content);

        fragmentSparseArray = new SparseArray<Fragment>();

        selectedFragment = fragmentSparseArray.get(INT_CONSTANT_FOR_HOME_FRAGMENT);
        selectedFragment = Home.newInstance();
        fragmentSparseArray.put(INT_CONSTANT_FOR_HOME_FRAGMENT, selectedFragment);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content, selectedFragment);
        transaction.commit();

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        databaseReference = FirebaseDatabase.getInstance().getReference();

        DatabaseReference checkRequests = databaseReference.child("Requests").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        checkRequests.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Snackbar snackbar = Snackbar.make(frameLayout, "You have a new request", Snackbar.LENGTH_LONG);
                snackbar.show();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Fragment uploadType = getSupportFragmentManager().findFragmentById(R.id.content);

        if (uploadType != null) {
            uploadType.onActivityResult(requestCode, resultCode, data);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
