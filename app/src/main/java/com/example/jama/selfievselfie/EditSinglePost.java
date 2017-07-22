package com.example.jama.selfievselfie;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EditSinglePost extends AppCompatActivity {

    ImageView image;
    EditText caption;
    String PushKey, Caption, Image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_post);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        PushKey = bundle.getString("pushKey");
        Caption = bundle.getString("caption");
        Image = bundle.getString("image1");

        image = (ImageView) findViewById(R.id.imageViewImagePost);
        Glide.with(this).load(Image).into(image);
        caption = (EditText) findViewById(R.id.editTextCaption);
        caption.setText(Caption);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_notification, menu);
        menu.findItem(R.id.action_clear).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                caption = (EditText) findViewById(R.id.editTextCaption);
                String editedCaption = caption.getText().toString();
                DatabaseReference databaseReferencePosts = FirebaseDatabase.getInstance().getReference().child("Posts");
                databaseReferencePosts.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(PushKey)
                        .child("caption").setValue(editedCaption);
                Toast.makeText(EditSinglePost.this, "Post Edited", Toast.LENGTH_SHORT).show();
                finish();
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }
}
