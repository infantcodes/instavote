package com.example.jama.selfievselfie;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.jama.selfievselfie.model.CircleTransform;
import com.example.jama.selfievselfie.model.Getters;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by JAMA on 5/20/2017.
 */

public class Share extends AppCompatActivity {

    DatabaseReference databaseReference;
    FirebaseListAdapter<Getters> listAdapter;
    ListView listView;
    Button invite;
    String Names, Username, ProfileImage, mAuth, postKey, uid;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_message);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Share Post");

        databaseReference = FirebaseDatabase.getInstance().getReference();

        mAuth = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Bundle bundle = getIntent().getExtras();
        postKey = bundle.getString("key");
        uid = bundle.getString("uid");

        DatabaseReference profileInfo = databaseReference.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("Profile Info");

        profileInfo.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, String> map = (Map)dataSnapshot.getValue();
                ProfileImage = map.get("profileImage");
                Username = map.get("username");
                Names = map.get("name");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        listView = (ListView) findViewById(R.id.listView);
        invite = (Button) findViewById(R.id.buttonInvite);

        DatabaseReference retrieveFollowing = databaseReference.child("Following").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        listAdapter = new FirebaseListAdapter<Getters>(
                this,
                Getters.class,
                R.layout.search_layout,
                retrieveFollowing
        ) {
            @Override
            protected void populateView(android.view.View v, final Getters model, int position) {

                String key = getRef(position).getKey();

                TextView username = (TextView) v.findViewById(R.id.textViewUsername);
                username.setText(model.getUsername());
                TextView names = (TextView) v.findViewById(R.id.textViewName);
                names.setText(model.getName());
                ImageView profileImage = (ImageView) v.findViewById(R.id.imageViewProfile);
                Glide.with(Share.this).load(model.getProfileImage()).bitmapTransform(new CircleTransform(Share.this)).centerCrop().into(profileImage);

                final DatabaseReference notification = FirebaseDatabase.getInstance().getReference().child("Notification")
                        .child(key);
                v.setOnClickListener(new android.view.View.OnClickListener() {
                    @Override
                    public void onClick(android.view.View v) {
                        Map map1 = new HashMap();
                        map1.put("username", Username);
                        map1.put("message", "shared a post");
                        map1.put("profileImage", ProfileImage);
                        map1.put("date", System.currentTimeMillis()/1000);
                        map1.put("pushKey", postKey);
                        map1.put("uid", mAuth);
                        map1.put("sharedUid", uid);
                        Toast.makeText(Share.this, "Post Shared", Toast.LENGTH_SHORT).show();
                        notification.push().setValue(map1);
                        finish();
                    }
                });
            }
        };
        listView.setAdapter(listAdapter);

        RelativeLayout noPosts = (RelativeLayout) findViewById(R.id.relativeLayout7);
        TextView textView = (TextView) findViewById(R.id.textView9);
        textView.setText("Follow People To Share Posts");
        listView.setEmptyView(noPosts);
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}
