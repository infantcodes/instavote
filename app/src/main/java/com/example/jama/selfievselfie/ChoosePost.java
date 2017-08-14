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

    public static ChoosePost newInstance() {
        ChoosePost fragment = new ChoosePost();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootview =  inflater.inflate(R.layout.activity_choose_post, container, false);

        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Algoli");

        return rootview;
    }
}
