package com.example.jama.selfievselfie;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jama.selfievselfie.model.Getters;
import com.example.jama.selfievselfie.model.RoundedTransformation;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by JAMA on 5/11/2017.
 */

public class ViewPosts extends AppCompatActivity {

    DatabaseReference databaseReference, votereference;
    String postKey, type, date, image1, image2, profileImage, profileImage2, pushKey, uid, uid2, username, username2, caption, mAuth;
    String Pusername;
    String Pimage;
    String Pnames;
    long Date;
    boolean like;
    long totalVotes1, totalVotes2;
    private static final int SECOND_MILLIS = 60;
    private static final int MINUTE_MILLIS = 1 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_post_layout);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle bundle = getIntent().getExtras();
        postKey = bundle.getString("pushKey");
        uid = bundle.getString("uid");
        type = bundle.getString("type");

        getSupportActionBar().setTitle(type);

        Date  = System.currentTimeMillis()/1000;
        mAuth = FirebaseAuth.getInstance().getCurrentUser().getUid();

        votereference = FirebaseDatabase.getInstance().getReference();

        /*DatabaseReference posts = FirebaseDatabase.getInstance().getReference().child("Posts").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(postKey);*/

        final DatabaseReference post = FirebaseDatabase.getInstance().getReference().child("All Posts").child(uid).child(postKey);

        final DatabaseReference profileInfo = FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("Profile Info");

        //GET THE USER'S PROFILE INFO
        profileInfo.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, String> map = (Map)dataSnapshot.getValue();
                Pusername = map.get("username");
                Pimage = map.get("profileImage");
                Pnames = map.get("name");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        post.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map <String, String> map = (Map)dataSnapshot.getValue();
                TextView txtcaption = (TextView) findViewById(R.id.textViewUserCaption);
                if (map.get("caption") == null){
                    txtcaption.setVisibility(android.view.View.GONE);
                }else {
                    caption = map.get("caption");
                    txtcaption.setText(caption);
                }
                image1 = map.get("image1");
                final ImageView imgImage1 = (ImageView) findViewById(R.id.imageViewImage1);
                Picasso.with(ViewPosts.this).load(image1).transform(new RoundedTransformation(50, 4)).fit().into(imgImage1);
                image2 = map.get("image2");
                final ImageView imgImage2 = (ImageView) findViewById(R.id.imageViewImage2);
                Picasso.with(ViewPosts.this).load(image2).transform(new RoundedTransformation(50, 4)).fit().into(imgImage2);
                username = map.get("username");
                TextView txtusername = (TextView) findViewById(R.id.textViewUsername1);
                txtusername.setText(username);
                username2 = map.get("username2");
                TextView txtusername2 = (TextView) findViewById(R.id.textViewUsername2);
                txtusername2.setText(username2);
                profileImage = map.get("profileImage");
                ImageView imgProfileImage = (ImageView) findViewById(R.id.imageViewProfileImage1);
                Picasso.with(ViewPosts.this).load(profileImage).transform(new RoundedTransformation(50, 4)).fit().into(imgProfileImage);
                profileImage2 = map.get("profileImage2");
                ImageView imgProfileImage2 = (ImageView) findViewById(R.id.imageViewProfileImage3);
                Picasso.with(ViewPosts.this).load(profileImage2).transform(new RoundedTransformation(50, 4)).fit().into(imgProfileImage2);
                uid = map.get("uid");
                uid2 = map.get("uid2");
                pushKey = map.get("pushKey");
                imgProfileImage.setOnClickListener(new android.view.View.OnClickListener() {
                    @Override
                    public void onClick(android.view.View v) {
                        Intent intent = new Intent(ViewPosts.this, UserProfile.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("key", uid);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                });

                //TIME*********************************
                TextView date = (TextView) findViewById(R.id.textViewDate);
                //long time = Long.parseLong(map.get("date"));
                long time = 1497369290;
                //TODO change time format
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

                final ImageView imageViewComment = (ImageView) findViewById(R.id.imageViewComment);
                final DatabaseReference Comments = FirebaseDatabase.getInstance().getReference().child("Comments");
                final DatabaseReference Likes = FirebaseDatabase.getInstance().getReference().child("Likes");
                final ImageView imageViewLike = (ImageView) findViewById(R.id.imageViewLike);
                final ImageView imageViewShare = (ImageView) findViewById(R.id.imageViewShare);

                imageViewShare.setOnClickListener(new android.view.View.OnClickListener() {
                    @Override
                    public void onClick(android.view.View v) {
                        Intent intent = new Intent(ViewPosts.this, Share.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("key", pushKey);
                        bundle.putString("uid", uid);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                });

                Comments.child(uid2).child(pushKey).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String commentCount = String.valueOf(dataSnapshot.getChildrenCount());
                        TextView comments = (TextView) findViewById(R.id.textViewAllComments);
                        comments.setText("View All "+commentCount+" Comments");
                        comments.setOnClickListener(new android.view.View.OnClickListener() {
                            @Override
                            public void onClick(android.view.View v) {
                                Intent likes = new Intent(ViewPosts.this, com.example.jama.selfievselfie.Comments.class);
                                Bundle bundle = new Bundle();
                                bundle.putString("key", pushKey);
                                bundle.putString("uid", uid2);
                                likes.putExtras(bundle);
                                startActivity(likes);
                            }
                        });

                        imageViewComment.setOnClickListener(new android.view.View.OnClickListener() {
                            @Override
                            public void onClick(android.view.View v) {
                                Intent comment = new Intent(ViewPosts.this, Comments.class);
                                Bundle bundle = new Bundle();
                                bundle.putString("key", pushKey);
                                bundle.putString("uid", uid2);
                                comment.putExtras(bundle);
                                startActivity(comment);
                            }
                        });

                        Likes.child(uid2).child(pushKey).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                String likeCount = String.valueOf(dataSnapshot.getChildrenCount());
                                TextView likes = (TextView) findViewById(R.id.textViewLikes);
                                likes.setText(likeCount+" Likes");
                                likes.setOnClickListener(new android.view.View.OnClickListener() {
                                    @Override
                                    public void onClick(android.view.View v) {
                                        Intent likes = new Intent(ViewPosts.this, com.example.jama.selfievselfie.Likes.class);
                                        Bundle bundle = new Bundle();
                                        bundle.putString("key", pushKey);
                                        bundle.putString("uid", uid2);
                                        likes.putExtras(bundle);
                                        startActivity(likes);
                                    }
                                });
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                        Likes.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.child(uid2).child(pushKey)
                                        .hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                    imageViewLike.setImageDrawable(getResources().getDrawable(R.drawable.like_red));
                                }else {
                                    imageViewLike.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite_black_24dp));
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                        imageViewLike.setOnClickListener(new android.view.View.OnClickListener() {
                            @Override
                            public void onClick(android.view.View v) {
                                if (like == true){
                                    Likes.child(uid2).child(pushKey).child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                            .removeValue();
                                    like = false;
                                }else {
                                    Map map = new HashMap();
                                    map.put("uid", FirebaseAuth.getInstance().getCurrentUser().toString());
                                    map.put("pushKey", pushKey);
                                    map.put("username", Pusername);
                                    map.put("profileImage", Pimage);
                                    map.put("date", Date);
                                    map.put("name", Pnames);
                                    Likes.child(uid2).child(pushKey).child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                            .setValue(map);
                                    like = true;
                                }
                            }
                        });

                        imgImage1.setOnClickListener(new android.view.View.OnClickListener() {
                            @Override
                            public void onClick(android.view.View v) {
                                Intent view = new Intent(ViewPosts.this, com.example.jama.selfievselfie.View.class);
                                Bundle bundle = new Bundle();
                                bundle.putString("image", image1);
                                view.putExtras(bundle);
                                startActivity(view);
                            }
                        });

                        imgImage2.setOnClickListener(new android.view.View.OnClickListener() {
                            @Override
                            public void onClick(android.view.View v) {
                                Intent view = new Intent(ViewPosts.this, com.example.jama.selfievselfie.View.class);
                                Bundle bundle = new Bundle();
                                bundle.putString("image", image2);
                                view.putExtras(bundle);
                                startActivity(view);
                            }
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                txtusername.setOnClickListener(new android.view.View.OnClickListener() {
                    @Override
                    public void onClick(android.view.View v) {
                        Intent intent = new Intent(ViewPosts.this, UserProfile.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("key", uid);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //VOTING ACTIVITY STARTS FROM HERE

        DatabaseReference vote1Numbers = votereference.child("Votes").child(mAuth).child(postKey).child("Votes 1");
        DatabaseReference vote2Numbers = votereference.child("Votes").child(mAuth).child(postKey).child("Votes 2");

        final Button vote1 = (Button) findViewById(R.id.buttonVote1);
        final Button vote2 = (Button) findViewById(R.id.buttonVote2);
        final TextView totalVotes = (TextView) findViewById(R.id.textViewTotalVotes);

        vote1Numbers.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                    vote1.setBackground(getResources().getDrawable(R.drawable.rounded_corner_white));
                    vote1.setTextColor(getResources().getColor(R.color.colorAccent));
                }else {
                    vote1.setBackground(getResources().getDrawable(R.drawable.rounded_corner_pink));
                    vote1.setTextColor(getResources().getColor(R.color.backGround));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        vote2Numbers.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                    vote2.setBackground(getResources().getDrawable(R.drawable.rounded_corner_white));
                    vote2.setTextColor(getResources().getColor(R.color.colorAccent));
                }else {
                    vote2.setBackground(getResources().getDrawable(R.drawable.rounded_corner_pink));
                    vote2.setTextColor(getResources().getColor(R.color.backGround));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        vote1Numbers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long s = (dataSnapshot.getChildrenCount());
                totalVotes1 = s;
                vote1.setText(s+"");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        vote2Numbers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long s = (dataSnapshot.getChildrenCount());
                vote2.setText(s+"");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        vote2.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                DatabaseReference vote2 = FirebaseDatabase.getInstance().getReference().child("Votes")
                        .child(mAuth).child(postKey).child("Votes 2");
                vote2.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue("voted");

                final DatabaseReference vote1 = FirebaseDatabase.getInstance().getReference().child("Votes")
                        .child(mAuth).child(postKey).child("Votes 1");
                vote1.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                            vote1.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });

        vote1.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                DatabaseReference vote1 = FirebaseDatabase.getInstance().getReference().child("Votes")
                        .child(mAuth).child(postKey).child("Votes 1");
                vote1.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue("voted");

                final DatabaseReference vote2 = FirebaseDatabase.getInstance().getReference().child("Votes")
                        .child(mAuth).child(postKey).child("Votes 2");
                vote1.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                            vote2.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}
