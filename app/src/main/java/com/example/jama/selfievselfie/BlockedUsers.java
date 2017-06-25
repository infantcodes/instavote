package com.example.jama.selfievselfie;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.*;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jama.selfievselfie.model.Getters;
import com.example.jama.selfievselfie.model.RoundedTransformation;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

/**
 * Created by JAMA on 5/30/2017.
 */

public class BlockedUsers extends AppCompatActivity {

    ListView listView;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_message);

        listView = (ListView) findViewById(R.id.listView);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Blocked Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Blocked Users");

        FirebaseListAdapter<Getters> gettersFirebaseListAdapter = new FirebaseListAdapter<Getters>(
                this,
                Getters.class,
                R.layout.search_layout,
                databaseReference
        ) {
            @Override
            protected void populateView(View v, final Getters model, int position) {
                TextView username = (TextView) v.findViewById(R.id.textViewUsername);
                username.setText(model.getUsername());
                TextView name = (TextView) v.findViewById(R.id.textViewName);
                name.setText(model.getName());
                ImageView profileImage = (ImageView) v.findViewById(R.id.imageViewProfile);
                Picasso.with(BlockedUsers.this).load(model.getProfileImage()).transform(new RoundedTransformation(50, 4)).centerCrop().fit().into(profileImage);

                Button unblock = (Button) v.findViewById(R.id.buttonUnblock);
                unblock.setVisibility(View.VISIBLE);
                unblock.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DatabaseReference unblockUser = FirebaseDatabase.getInstance().getReference();
                        unblockUser.child("Blocked Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(model.getUid()).removeValue();
                        Toast.makeText(BlockedUsers.this, "You Have Unblocked "+model.getUsername(), Toast.LENGTH_SHORT).show();
                    }
                });

                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(BlockedUsers.this, UserProfile.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("key", model.getUid());
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
