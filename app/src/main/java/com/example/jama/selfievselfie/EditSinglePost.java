package com.example.jama.selfievselfie;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.*;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EditSinglePost extends AppCompatActivity {

    ImageView image;
    EditText caption;
    String PushKey, Caption, Image;
    ProgressBar progressBar;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_post);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        PushKey = bundle.getString("pushKey");
        Caption = bundle.getString("caption");
        Image = bundle.getString("image1");

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        textView = (TextView) findViewById(R.id.textView13);
        textView.setVisibility(View.GONE);
        image = (ImageView) findViewById(R.id.imageViewImagePost);
        Glide.with(this).load(Image).into(image);
        caption = (EditText) findViewById(R.id.editTextCaption);
        caption.setText(Caption);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_post, menu);
        menu.findItem(R.id.action_edit_post).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                caption = (EditText) findViewById(R.id.editTextCaption);
                String editedCaption = caption.getText().toString();
                DatabaseReference allPosts = FirebaseDatabase.getInstance().getReference().child("All Posts")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                DatabaseReference posts = FirebaseDatabase.getInstance().getReference().child("Posts")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

                allPosts.child(PushKey).child("caption").setValue(editedCaption);
                posts.child(PushKey).child("caption").setValue(editedCaption);

                Toast.makeText(EditSinglePost.this, "Post Edited", Toast.LENGTH_SHORT).show();
                finish();
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }
}
