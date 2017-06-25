package com.example.jama.selfievselfie;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.*;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.jama.selfievselfie.model.Getters;
import com.example.jama.selfievselfie.model.RoundedTransformation;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

/**
 * Created by JAMA on 5/4/2017.
 */

public class Likes extends AppCompatActivity {
    String postKey, uid;
    DatabaseReference databaseReference;
    ListView listView;
    private static final int SECOND_MILLIS = 60;
    private static final int MINUTE_MILLIS = 1 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_profile);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Likes");

        Bundle bundle = getIntent().getExtras();
        postKey = bundle.getString("key");
        uid = bundle.getString("uid");
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Likes").child(uid).child(postKey);

        listView = (ListView) findViewById(R.id.listView);

        FirebaseListAdapter<Getters> gettersFirebaseListAdapter = new FirebaseListAdapter<Getters>(
                this,
                Getters.class,
                R.layout.search_layout,
                databaseReference
        ) {
            @Override
            protected void populateView(View v, Getters model, int position) {

                final String userId = getRef(position).getKey();

                TextView username = (TextView) v.findViewById(R.id.textViewUsername);
                username.setText(model.getUsername());
                TextView name = (TextView) v.findViewById(R.id.textViewName);
                name.setText(model.getName());
                ImageView profileImage = (ImageView) v.findViewById(R.id.imageViewProfile);
                if (model.getProfileImage() == null){
                    username.setText("Unknown User");
                    Picasso.with(Likes.this).load(R.drawable.download).transform(new RoundedTransformation(50, 4)).fit().into(profileImage);
                }else {
                    Picasso.with(Likes.this).load(model.getProfileImage()).fit().transform(new RoundedTransformation(50, 4)).into(profileImage);
                }

                //TIME*********************************
                TextView date = (TextView) v.findViewById(R.id.textViewDate);
                long time = model.getDate();
                long now  = System.currentTimeMillis()/1000;
                long diff = now-time;
                if (diff < MINUTE_MILLIS) {
                    date.setText("just now");
                } else if (diff < 2 * MINUTE_MILLIS) {
                    date.setText("a minute ago");
                } else if (diff < 50 * MINUTE_MILLIS) {
                    date.setText(diff / MINUTE_MILLIS + " minutes ago");
                } else if (diff < 90 * MINUTE_MILLIS) {
                    date.setText("an hour ago");
                } else if (diff < 24 * HOUR_MILLIS) {
                    date.setText(diff / HOUR_MILLIS + " hours ago");
                } else if (diff < 48 * HOUR_MILLIS) {
                    date.setText("yesterday");
                } else {
                    date.setText(diff / DAY_MILLIS + " days ago");
                }
                //**************************************

                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Likes.this, UserProfile.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("key", userId);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                });
            }
        };

        listView.setAdapter(gettersFirebaseListAdapter);

        RelativeLayout noPosts = (RelativeLayout) findViewById(R.id.relativeLayout7);
        noPosts.setVisibility(View.GONE);
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}
