package com.example.jama.selfievselfie;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jama.selfievselfie.model.RoundedTransformation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class EditPost extends AppCompatActivity {

    String caption, profileImage1, profileImage2, image1, image2, username1, username2, date, pushKey;
    TextView Username1, Username2, Date;
    ImageView ProfileImage1, ProfileImage2, Image1, Image2;
    EditText Caption;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_post);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Edit Post");

        Bundle bundle = getIntent().getExtras();
        caption = bundle.getString("caption");
        profileImage1 = bundle.getString("profileImage1");
        profileImage2 = bundle.getString("profileImage2");
        username1 = bundle.getString("username1");
        username2 = bundle.getString("username2");
        image1 = bundle.getString("image1");
        image2 = bundle.getString("image2");
        date = bundle.getString("date");
        pushKey = bundle.getString("pushKey");

        Username1 = (TextView) findViewById(R.id.textViewUsername1);
        Username2 = (TextView) findViewById(R.id.textViewUsername2);
        Date = (TextView) findViewById(R.id.textViewDate);
        Image1 = (ImageView) findViewById(R.id.imageViewImage1);
        Image2 = (ImageView) findViewById(R.id.imageViewImage2);
        ProfileImage1 = (ImageView) findViewById(R.id.imageViewProfileImage1);
        ProfileImage2 = (ImageView) findViewById(R.id.imageViewProfileImage3);
        Caption = (EditText) findViewById(R.id.editTextCaption);

        Username2.setText(username1);
        Username1.setText(username2);
        Caption.setText(caption);
        Date.setText(date);

        Picasso.with(EditPost.this).load(profileImage1).transform(new RoundedTransformation(50, 4)).fit().into(ProfileImage2);
        Picasso.with(EditPost.this).load(profileImage2).transform(new RoundedTransformation(50, 4)).fit().into(ProfileImage1);
        Picasso.with(EditPost.this).load(image1).transform(new RoundedTransformation(50, 4)).fit().into(Image1);
        Picasso.with(EditPost.this).load(image2).transform(new RoundedTransformation(50, 4)).fit().into(Image2);

        Image1.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                Intent view = new Intent(EditPost.this, com.example.jama.selfievselfie.View.class);
                Bundle bundle = new Bundle();
                bundle.putString("image", image1);
                view.putExtras(bundle);
                startActivity(view);
            }
        });

        Image2.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                Intent view = new Intent(EditPost.this, com.example.jama.selfievselfie.View.class);
                Bundle bundle = new Bundle();
                bundle.putString("image", image2);
                view.putExtras(bundle);
                startActivity(view);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_post, menu);
        menu.findItem(R.id.action_edit_post).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                databaseReference = FirebaseDatabase.getInstance().getReference().child("Posts")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                Caption = (EditText) findViewById(R.id.editTextCaption);
                String updateCaption = Caption.getText().toString();
                Map map = new HashMap();
                map.put("caption", updateCaption);
                databaseReference.child(pushKey).updateChildren(map);
                Toast.makeText(EditPost.this, "Post Updated", Toast.LENGTH_SHORT).show();
                finish();
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}
