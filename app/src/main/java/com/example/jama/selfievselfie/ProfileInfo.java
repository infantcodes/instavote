package com.example.jama.selfievselfie;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by JAMA on 3/13/2017.
 */

public class ProfileInfo extends AppCompatActivity {

    ListView listView;
    Button editProfile;
    ImageView imageViewProfile;
    TextView txtUsername, txtBio, txtPosts, txtFollowing, txtFollowers, txtNames;
    DatabaseReference databaseReference;
    ProgressDialog progressDialog;
    String Names;
    String Username;
    String Bio;
    String ProfileImage;
    long Date;
    FirebaseListAdapter<Getters> postsFirebaseListAdapter;
    boolean like;
    private static final int SECOND_MILLIS = 60;
    private static final int MINUTE_MILLIS = 1 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;

    public ProfileInfo() {
        postsFirebaseListAdapter = null;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_profile);

        View header = View.inflate(ProfileInfo.this, R.layout.profile_detail_layout, null);

        Date  = System.currentTimeMillis()/1000;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        //INITIALIZING
        listView = (ListView) findViewById(R.id.listView);
        editProfile = (Button) header.findViewById(R.id.buttonEditProfile);
        editProfile.setText("EDIT PROFILE");
        txtBio = (TextView) header.findViewById(R.id.txtBio);
        txtFollowing = (TextView) header.findViewById(R.id.txtFollowing);
        txtFollowers = (TextView) header.findViewById(R.id.txtFollowers);
        txtPosts = (TextView) header.findViewById(R.id.txtPosts);
        txtUsername = (TextView) header.findViewById(R.id.txtUsername);
        txtNames = (TextView) header.findViewById(R.id.txtNames);
        imageViewProfile = (ImageView) header.findViewById(R.id.imageViewProfile);

        //ON CLICK TO OPEN FOLLOWERS ACTIVITY
        txtFollowers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileInfo.this, Followers.class);
                startActivity(intent);
            }
        });

        //ON CLICK TO OPEN FOLLOWING ACTIVITY
        txtFollowing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileInfo.this, Following.class);
                startActivity(intent);
            }
        });

        //PROGRESS DIALOG
        progressDialog = new ProgressDialog(ProfileInfo.this);
        progressDialog.setMessage("Loading ...");
        progressDialog.setCancelable(false);
        progressDialog.show();


        listView.addHeaderView(header, null, false);

        //DATABASE REFERENCES TO RETRIEVE PROFILE INFO
        databaseReference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference followers = databaseReference.child("Followers").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        DatabaseReference posts = databaseReference.child("Posts").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        DatabaseReference profieInfo = databaseReference.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("Profile Info");
        DatabaseReference following = databaseReference.child("Following").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        followers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String s = String.valueOf(dataSnapshot.getChildrenCount());
                if (s == null){
                    txtFollowers.setText("0");
                }else {
                    txtFollowers.setText(s);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        following.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String s = String.valueOf(dataSnapshot.getChildrenCount());
                if (s == null){
                    txtFollowing.setText("0");
                }else {
                    txtFollowing.setText(s);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        posts.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String s = String.valueOf(dataSnapshot.getChildrenCount());
                if (s == null){
                    txtPosts.setText("0");
                }else {
                    txtPosts.setText(s);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        profieInfo.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map <String, String> map = (Map)dataSnapshot.getValue();
                ProfileImage = map.get("profileImage");
                Picasso.with(ProfileInfo.this).load(ProfileImage).fit().transform(new RoundedTransformation(50, 4)).into(imageViewProfile);
                Username = map.get("username");
                txtUsername.setText(Username.toString());
                Bio = map.get("bio");
                txtBio.setText(Bio.toString());
                Names = map.get("name");
                txtNames.setText(Names.toString());
                getSupportActionBar().setTitle(Username.toString());
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //ON CLICK TO OPEN EDIT ACTIVITY TO EDIT PROFILE
        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent editProfile = new Intent(ProfileInfo.this, EditProfile.class);
                Bundle bundle = new Bundle();
                bundle.putString("key", FirebaseAuth.getInstance().getCurrentUser().getUid());
                editProfile.putExtras(bundle);
                startActivity(editProfile);
            }
        });

        DatabaseReference profilePosts = databaseReference = FirebaseDatabase.getInstance().getReference().child("Posts").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        postsFirebaseListAdapter = new FirebaseListAdapter<Getters>(
                this,
                Getters.class,
                R.layout.post_layout,
                profilePosts
        ) {
            @Override
            protected void populateView(final View v, final Getters model, int position) {

                ImageView profileImage1 = (ImageView) v.findViewById(R.id.imageViewProfileImage1);
                Picasso.with(ProfileInfo.this).load(model.getProfileImage2()).transform(new RoundedTransformation(50, 4)).fit().into(profileImage1);
                ImageView profileImage3 = (ImageView) v.findViewById(R.id.imageViewProfileImage3);
                Picasso.with(ProfileInfo.this).load(model.getProfileImage()).transform(new RoundedTransformation(50, 4)).fit().into(profileImage3);
                ImageView imageView1 = (ImageView) v.findViewById(R.id.imageViewImage1);
                Picasso.with(ProfileInfo.this).load(model.getImage1()).transform(new RoundedTransformation(50, 4)).fit().into(imageView1);
                ImageView imageView2 = (ImageView) v.findViewById(R.id.imageViewImage2);
                Picasso.with(ProfileInfo.this).load(model.getImage2()).transform(new RoundedTransformation(50, 4)).fit().into(imageView2);
                TextView username1 = (TextView) v.findViewById(R.id.textViewUsername1);
                username1.setText(model.getUsername2());
                TextView username2 = (TextView) v.findViewById(R.id.textViewUsername2);
                username2.setText(model.getUsername());
                final ImageView imageViewLike = (ImageView) v.findViewById(R.id.imageViewLike);
                final ImageView imageViewComment = (ImageView) v.findViewById(R.id.imageViewComment);
                final DatabaseReference Comments = FirebaseDatabase.getInstance().getReference().child("Comments");

                //TIME*********************************
                TextView date = (TextView) v.findViewById(R.id.textViewDate);
                long time = model.getDate();
                long now  = System.currentTimeMillis()/1000;
                long diff = now-time;
                if (diff < MINUTE_MILLIS) {
                    date.setText("just now"+diff);
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

                Comments.child(model.getUid2()).child(model.getPushKey()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String commentCount = String.valueOf(dataSnapshot.getChildrenCount());
                        TextView comments = (TextView) v.findViewById(R.id.textViewAllComments);
                        comments.setText("View All "+commentCount+" Comments");
                        comments.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent likes = new Intent(ProfileInfo.this, com.example.jama.selfievselfie.Comments.class);
                                Bundle bundle = new Bundle();
                                bundle.putString("key", model.getPushKey());
                                bundle.putString("uid", model.getUid2());
                                likes.putExtras(bundle);
                                startActivity(likes);
                            }
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                imageView1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent view = new Intent(ProfileInfo.this, com.example.jama.selfievselfie.View.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("image", model.getImage1());
                        view.putExtras(bundle);
                        startActivity(view);
                    }
                });

                imageView2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent view = new Intent(ProfileInfo.this, com.example.jama.selfievselfie.View.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("image", model.getImage2());
                        view.putExtras(bundle);
                        startActivity(view);
                    }
                });

                imageViewComment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent comment = new Intent(ProfileInfo.this, Comments.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("key", model.getPushKey());
                        bundle.putString("uid", model.getUid2());
                        comment.putExtras(bundle);
                        startActivity(comment);
                    }
                });

                username2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(ProfileInfo.this, UserProfile.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("key", model.getUid());
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                });

                final ImageView imageViewShare = (ImageView) v.findViewById(R.id.imageViewShare);
                imageViewShare.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(ProfileInfo.this, Share.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("key", model.getPushKey());
                        bundle.putString("uid", model.getUid());
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                });

                final DatabaseReference Likes = FirebaseDatabase.getInstance().getReference().child("Likes");

                //NUMBER OF LIKES
                Likes.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(model.getPushKey()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String likeCount = String.valueOf(dataSnapshot.getChildrenCount());
                        TextView likes = (TextView) v.findViewById(R.id.textViewLikes);
                        likes.setText("Likes "+likeCount);
                        likes.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent likes = new Intent(ProfileInfo.this, com.example.jama.selfievselfie.Likes.class);
                                Bundle bundle = new Bundle();
                                bundle.putString("key", model.getPushKey());
                                bundle.putString("uid", model.getUid2());
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
                        if (dataSnapshot.child(model.getUid2()).child(model.getPushKey())
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

                imageViewLike.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (like == true){
                            Likes.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(model.getPushKey())
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue();
                            like = false;
                        }else {
                            Map map = new HashMap();
                            map.put("uid", FirebaseAuth.getInstance().getCurrentUser().toString());
                            map.put("pushKey", model.getPushKey());
                            map.put("username", Username);
                            map.put("profileImage", ProfileImage);
                            map.put("date", Date);
                            map.put("name", Names);
                            Likes.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(model.getPushKey())
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(map);
                            like = true;
                        }
                    }
                });
            }
        };

        listView.setAdapter(postsFirebaseListAdapter);
        listView.setNestedScrollingEnabled(true);

        RelativeLayout noPosts = (RelativeLayout) findViewById(R.id.relativeLayout7);
        noPosts.setVisibility(View.GONE);
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}
