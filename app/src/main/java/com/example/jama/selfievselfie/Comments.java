package com.example.jama.selfievselfie;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
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
 * Created by JAMA on 5/3/2017.
 */

public class Comments extends AppCompatActivity {
    ListView listView;
    DatabaseReference databaseReference;
    FirebaseListAdapter<Getters> listAdapter;
    String postKey, uid, comments;
    String Names, Username, ProfileImage, mAuth;
    EditText editTextComment;
    Button buttonComment;
    boolean scroll = true;
    private static final int SECOND_MILLIS = 60;
    private static final int MINUTE_MILLIS = 1 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Comments");

        mAuth = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Bundle bundle = getIntent().getExtras();
        postKey = bundle.getString("key");
        uid = bundle.getString("uid");

        listView = (ListView) findViewById(R.id.listViewSearch);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Comments");
        final DatabaseReference notification = FirebaseDatabase.getInstance().getReference().child("Notification")
                .child(uid);

        editTextComment = (EditText) findViewById(R.id.editTextSearch);
        buttonComment = (Button) findViewById(R.id.buttonComment);
        buttonComment.setVisibility(View.VISIBLE);

        DatabaseReference viewComments = databaseReference.child(uid).child(postKey);
        final DatabaseReference postComment = databaseReference.child(uid).child(postKey);
        DatabaseReference profileInfo = FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
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

        listAdapter = new FirebaseListAdapter<Getters>(
                this,
                Getters.class,
                R.layout.notification_layout,
                viewComments
        ) {
            @Override
            protected void populateView(android.view.View v, final Getters model, int position) {
                TextView username = (TextView) v.findViewById(R.id.textViewUsername);
                username.setText(model.getUsername());
                TextView comment = (TextView) v.findViewById(R.id.textViewName);
                comment.setText(model.getMessage());
                ImageView profileImage = (ImageView) v.findViewById(R.id.imageViewProfile);
                Glide.with(Comments.this).load(model.getProfileImage()).bitmapTransform(new CircleTransform(Comments.this)).into(profileImage);

                //TIME*********************************
                //iuugjbjm
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

                v.setOnClickListener(new android.view.View.OnClickListener() {
                    @Override
                    public void onClick(android.view.View v) {
                            Intent intent = new Intent(Comments.this, UserProfile.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("key", model.getUid());
                            intent.putExtras(bundle);
                            startActivity(intent);
                    }
                });

                if (scroll){
                    listView.smoothScrollToPosition(listAdapter.getCount() - 1);
                    scroll = false;
                }else {
                    scroll = false;
                }
            }
        };
        listView.setAdapter(listAdapter);

        editTextComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scroll = true;
            }
        });

        buttonComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                comments = editTextComment.getText().toString();
                if (comments.equals("")){
                    Toast.makeText(Comments.this, "Type In A Comment To Post", Toast.LENGTH_SHORT).show();
                    //Toast.makeText(Comments.this, "commented".substring(0, 9), Toast.LENGTH_SHORT).show();
                }else {
                    Map map = new HashMap();
                    map.put("uid", mAuth);
                    map.put("pushKey", postKey);
                    map.put("username", Username);
                    map.put("profileImage", ProfileImage);
                    map.put("date", System.currentTimeMillis()/1000);
                    map.put("message", comments);
                    postComment.push().setValue(map);

                    if (!uid.equals(mAuth)){
                        Map map1 = new HashMap();
                        map1.put("username", Username);
                        map1.put("message", "commented: "+comments);
                        map1.put("profileImage", ProfileImage);
                        map1.put("date", System.currentTimeMillis()/1000);
                        map1.put("pushKey", postKey);
                        map1.put("uid", mAuth);
                        notification.push().setValue(map1);
                    }
                    editTextComment.setText("");
                    scroll = true;
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
