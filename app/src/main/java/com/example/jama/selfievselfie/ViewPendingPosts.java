package com.example.jama.selfievselfie;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.jama.selfievselfie.model.CircleTransform;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by JAMA on 5/11/2017.
 */

public class ViewPendingPosts extends AppCompatActivity {

    DatabaseReference databaseReference;
    String postKey, image1, image2, profileImage, profileImage2, pushKey, uid, uid2, username, username2, Caption;
    Button buttonPost;
    EditText caption;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_pending_post_layout);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        databaseReference = FirebaseDatabase.getInstance().getReference();

        final DatabaseReference sendPost = databaseReference.child("Posts").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        final DatabaseReference sendAllPost = databaseReference.child("All Posts").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        final DatabaseReference deletePreviousPendingPost = databaseReference.child("Pending Requests").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        //final DatabaseReference mentions = databaseReference.child("Mentions");

        Bundle bundle = getIntent().getExtras();
        postKey = bundle.getString("pushKey");
        username = bundle.getString("username");
        username2 = bundle.getString("username2");
        image1 = bundle.getString("image1");
        image2 = bundle.getString("image2");
        profileImage = bundle.getString("profileImage");
        profileImage2 = bundle.getString("profileImage2");
        uid = bundle.getString("uid");
        uid2 = bundle.getString("uid2");

        getSupportActionBar().setTitle("Pending Requests");

        final ImageView imgImage2 = (ImageView) findViewById(R.id.imageViewImage2);
        Glide.with(ViewPendingPosts.this).load(image1).bitmapTransform(new CircleTransform(ViewPendingPosts.this)).into(imgImage2);
        TextView txtusername = (TextView) findViewById(R.id.textViewUsername1);
        txtusername.setText(username);
        TextView txtusername2 = (TextView) findViewById(R.id.textViewUsername2);
        txtusername2.setText(username2);
        ImageView imgProfileImage = (ImageView) findViewById(R.id.imageViewProfileImage1);
        Glide.with(ViewPendingPosts.this).load(profileImage).bitmapTransform(new CircleTransform(ViewPendingPosts.this)).into(imgProfileImage);
        ImageView imgProfileImage2 = (ImageView) findViewById(R.id.imageViewProfileImage3);
        Glide.with(ViewPendingPosts.this).load(profileImage2).bitmapTransform(new CircleTransform(ViewPendingPosts.this)).into(imgProfileImage2);
        ImageView imgImage1 = (ImageView) findViewById(R.id.imageViewImage1);
        Glide.with(ViewPendingPosts.this).load(image2).bitmapTransform(new CircleTransform(ViewPendingPosts.this)).into(imgImage1);

        buttonPost = (Button) findViewById(R.id.buttonPost);
        caption = (EditText) findViewById(R.id.editTextCaption);
        buttonPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Caption = caption.getText().toString();
                if (Caption.equals("")){
                    Caption = "";
                }
                Map map = new HashMap();
                map.put("username", username);
                map.put("username2", username2);
                map.put("profileImage", profileImage);
                map.put("profileImage2", profileImage2);
                map.put("image1", image1);
                map.put("image2", image2);
                map.put("uid", uid);
                map.put("uid2", FirebaseAuth.getInstance().getCurrentUser().getUid());
                map.put("pushKey", postKey);
                map.put("date", System.currentTimeMillis()/1000);
                map.put("caption", Caption);
                sendPost.child(postKey).setValue(map);
                sendAllPost.child(postKey).setValue(map);
                //mentions.child(uid).child(postKey).setValue(map);
                deletePreviousPendingPost.child(postKey).removeValue();
                finish();
                Snackbar snackbar = Snackbar
                        .make(v, "Post has been sent", Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}
