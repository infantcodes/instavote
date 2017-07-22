package com.example.jama.selfievselfie;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.jama.selfievselfie.model.CircleTransform;
import com.example.jama.selfievselfie.model.Getters;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by JAMA on 3/27/2017.
 */

public class FindUserMessageList extends AppCompatActivity {

    ListView listView;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_following);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Following");

        listView = (ListView) findViewById(R.id.listView);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Following").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        FirebaseListAdapter<Getters> searchDetailsFirebaseListAdapter = new FirebaseListAdapter<Getters>(
                this,
                Getters.class,
                R.layout.search_layout,
                databaseReference
        ) {
            @Override
            protected void populateView(View v, final Getters model, final int position) {

                final String key = getRef(position).getKey().toString();

                TextView username = (TextView) v.findViewById(R.id.textViewUsername);
                username.setText(model.getUsername());
                TextView names = (TextView) v.findViewById(R.id.textViewName);
                names.setText(model.getName());
                ImageView profileImage = (ImageView) v.findViewById(R.id.imageViewProfile);
                Glide.with(FindUserMessageList.this).load(model.getProfileImage()).bitmapTransform(new CircleTransform(FindUserMessageList.this)).into(profileImage);

                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(FindUserMessageList.this,MessagingList.class);
                        Bundle bundle= new Bundle();
                        bundle.putString("key", key);
                        bundle.putString("username", model.getUsername());
                        bundle.putString("profileImage", model.getProfileImage());
                        intent.putExtras(bundle);
                        startActivity(intent);
                        finish();
                    }
                });
            }
        };

        listView.setAdapter(searchDetailsFirebaseListAdapter);
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}
