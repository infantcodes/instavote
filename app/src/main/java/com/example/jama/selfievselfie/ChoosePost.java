package com.example.jama.selfievselfie;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.*;
import android.view.View;
import android.widget.LinearLayout;

public class ChoosePost extends Fragment {

    LinearLayout singlePost, twoPosts, withAFriend;

    public static ChoosePost newInstance() {
        ChoosePost fragment = new ChoosePost();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootview =  inflater.inflate(R.layout.activity_choose_post, container, false);

        singlePost = (LinearLayout) rootview.findViewById(R.id.linearLayoutSinglePost);
        twoPosts = (LinearLayout) rootview.findViewById(R.id.linearLayoutTwoPosts);
        withAFriend = (LinearLayout) rootview.findViewById(R.id.linearLayoutWithAFriend);

        withAFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SendRequests.class);
                startActivity(intent);
            }
        });

        singlePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SinglePost.class);
                startActivity(intent);
            }
        });

        ((AppCompatActivity)getActivity()).getSupportActionBar().show();
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("InstaVote");

        return rootview;
    }
}
