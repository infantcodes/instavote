package com.example.jama.selfievselfie;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.*;
import android.view.View;
import android.widget.LinearLayout;

public class ChoosePost extends AppCompatActivity {

    LinearLayout singlePost, twoPosts, withAFriend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_post);

        singlePost = (LinearLayout) findViewById(R.id.linearLayoutSinglePost);
        twoPosts = (LinearLayout) findViewById(R.id.linearLayoutTwoPosts);
        withAFriend = (LinearLayout) findViewById(R.id.linearLayoutWithAFriend);

        withAFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChoosePost.this, SendRequests.class);
                startActivity(intent);
            }
        });

        singlePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChoosePost.this, SinglePost.class);
                startActivity(intent);
            }
        });

    }
}
