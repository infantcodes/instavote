package com.example.jama.selfievselfie;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.*;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TextOnly extends AppCompatActivity {

    EditText caption, choice1, choice2, choice3, choice4, choice5;
    ImageView imageViewChoice3, imageViewChoice4, imageViewChoice5;
    LinearLayout linearLayout3, linearLayout4, linearLayout5;
    Button moreChoices;
    ArrayList<String> arrayList = new ArrayList<>();
    DatabaseReference databaseReference;
    String Choice1, Choice2, Choice3, Choice4, Choice5, Caption;
    String Username, ProfileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_only);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference profileInfo = databaseReference.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Profile Info");

        //GET PROFILE INFO
        profileInfo.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, String> map = (Map)dataSnapshot.getValue();
                Username = map.get("username");
                ProfileImage = map.get("profileImage");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        Toast.makeText(this, ""+arrayList.size(), Toast.LENGTH_SHORT).show();

        imageViewChoice3 = (ImageView) findViewById(R.id.imageViewAddOption3);
        imageViewChoice4 = (ImageView) findViewById(R.id.imageViewAddOption4);
        imageViewChoice5 = (ImageView) findViewById(R.id.imageViewAddOption5);

        linearLayout3 = (LinearLayout) findViewById(R.id.layoutChoice3);
        linearLayout4 = (LinearLayout) findViewById(R.id.layoutChoice4);
        linearLayout5 = (LinearLayout) findViewById(R.id.layoutChoice5);

        choice3 = (EditText) findViewById(R.id.editTextOption3);
        choice4 = (EditText) findViewById(R.id.editTextOption4);
        choice5 = (EditText) findViewById(R.id.editTextOption5);

        moreChoices = (Button) findViewById(R.id.buttonMoreChoices);
        moreChoices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (arrayList.size() == 0){
                    linearLayout3.setVisibility(View.VISIBLE);
                    arrayList.add(0, "choice3");
                    Toast.makeText(TextOnly.this, ""+arrayList.size(), Toast.LENGTH_SHORT).show();
                }else if (arrayList.size() == 1){
                    linearLayout4.setVisibility(View.VISIBLE);
                    arrayList.add(1, "choice4");
                    imageViewChoice3.setVisibility(View.GONE);
                    Toast.makeText(TextOnly.this, ""+arrayList.size(), Toast.LENGTH_SHORT).show();
                }else if (arrayList.size() == 2){
                    linearLayout5.setVisibility(View.VISIBLE);
                    arrayList.add(2, "choice5");
                    imageViewChoice4.setVisibility(View.GONE);
                    Toast.makeText(TextOnly.this, ""+arrayList.size(), Toast.LENGTH_SHORT).show();
                    moreChoices.setEnabled(false);
                }
            }
        });

        imageViewChoice3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linearLayout3.setVisibility(View.GONE);
                arrayList.remove(0);
                choice3.setText("");
            }
        });

        imageViewChoice4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linearLayout4.setVisibility(View.GONE);
                imageViewChoice3.setVisibility(View.VISIBLE);
                arrayList.remove(1);
                choice4.setText("");
            }
        });

        imageViewChoice5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linearLayout5.setVisibility(View.GONE);
                imageViewChoice4.setVisibility(View.VISIBLE);
                arrayList.remove(2);
                moreChoices.setEnabled(true);
                choice5.setText("");
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_edit_post, menu);
        menu.findItem(R.id.action_edit_post).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                caption = (EditText) findViewById(R.id.editTextCaption);
                choice1 = (EditText) findViewById(R.id.editTextOption1);
                choice2 = (EditText) findViewById(R.id.editTextOption2);
                choice3 = (EditText) findViewById(R.id.editTextOption3);
                choice4 = (EditText) findViewById(R.id.editTextOption4);
                choice5 = (EditText) findViewById(R.id.editTextOption5);

                Choice1 = choice1.getText().toString();
                Choice2 = choice2.getText().toString();
                Choice3 = choice3.getText().toString();
                Choice4 = choice4.getText().toString();
                Choice5 = choice5.getText().toString();
                Caption = caption.getText().toString();

                if (Choice1.equals("") || Choice2.equals("")){
                    Toast.makeText(TextOnly.this, "Add at least two choices to post", Toast.LENGTH_SHORT).show();
                }else {
                    DatabaseReference post = databaseReference.child("Posts").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    DatabaseReference allPosts = databaseReference.child("All Posts").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    String pushKey = post.push().getKey();

                    if (Caption.equals("")){
                        Toast.makeText(TextOnly.this, "Add a caption to post", Toast.LENGTH_SHORT).show();
                    }else {

                        if (Choice3.equals("")){
                            Choice3 = null;
                        }
                        if (Choice4.equals("")){
                            Choice4 = null;
                        }
                        if (Choice5.equals("")){
                            Choice5 = null;
                        }

                        Map map = new HashMap();
                        map.put("choice1", Choice1);
                        map.put("choice2", Choice2);
                        map.put("choice3", Choice3);
                        map.put("choice4", Choice4);
                        map.put("choice5", Choice5);
                        map.put("username2", Username);
                        map.put("profileImage2", ProfileImage);
                        map.put("date", System.currentTimeMillis()/1000);
                        map.put("caption", Caption);
                        map.put("uid2", FirebaseAuth.getInstance().getCurrentUser().getUid());
                        map.put("pushKey", pushKey);
                        allPosts.child(pushKey).setValue(map);
                        post.child(pushKey).setValue(map);
                    }
                }

                return false;
            }
        });

        return true;
    }
}
