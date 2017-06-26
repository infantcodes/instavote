package com.example.jama.selfievselfie;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by JAMA on 3/23/2017.
 */

public class UserProfile extends AppCompatActivity{

    Button follow_unfollow;
    DatabaseReference databaseReference, votereference;
    String postKey, Username, ProfileImage, Bio, Names, Pusername, Pnames, Pimage;
    long Date;
    ImageView imageViewProfile;
    ListView listView;
    TextView txtUsername, txtBio, txtPosts, txtFollowing, txtFollowers, txtNames;
    boolean processFollowing, processFollower, like, check_if_user_is_blocked;
    FirebaseListAdapter<Getters> chatsFirebaseListAdapter;
    private static final int SECOND_MILLIS = 60;
    private static final int MINUTE_MILLIS = 1 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_profile);

        View header = View.inflate(UserProfile.this, R.layout.profile_detail_layout, null);
        final View noPostFooter = View.inflate(UserProfile.this, R.layout.no_post_layout, null);
        final View blockFooter = View.inflate(UserProfile.this, R.layout.user_blocked, null);
        final View privateAccount = View.inflate(UserProfile.this, R.layout.private_account_layout, null);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Date  = System.currentTimeMillis()/1000;

        //RECIEVING BUNDLEDATA FROM PREVIOUS ACTIVITY
        Bundle bundle = getIntent().getExtras();
        postKey = bundle.getString("key");

        //CREATING DATABASE REFERENCES
        databaseReference = FirebaseDatabase.getInstance().getReference();
        votereference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference retrieveProfileInfo = databaseReference.child("Users").child(postKey)
                .child("Profile Info");
        DatabaseReference retrievePosts = databaseReference.child("Posts").child(postKey);
        final DatabaseReference retreiveFollowers = databaseReference.child("Followers").child(postKey);
        DatabaseReference retrieveFollowing = databaseReference.child("Following").child(postKey);
        final DatabaseReference profileInfo = databaseReference.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("Profile Info");

        //GET THE USER'S PROFILE INFO
        profileInfo.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map <String, String> map = (Map)dataSnapshot.getValue();
                Pusername = map.get("username");
                Pimage = map.get("profileImage");
                Pnames = map.get("name");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //INITIALIZE VARIABLES
        listView = (ListView) findViewById(R.id.listView);
        follow_unfollow = (Button) header.findViewById(R.id.buttonEditProfile);
        txtBio = (TextView) header.findViewById(R.id.txtBio);
        txtFollowing = (TextView) header.findViewById(R.id.txtFollowing);
        txtFollowers = (TextView) header.findViewById(R.id.txtFollowers);
        txtPosts = (TextView) header.findViewById(R.id.txtPosts);
        txtUsername = (TextView) header.findViewById(R.id.txtUsername);
        txtNames = (TextView) header.findViewById(R.id.txtNames);
        imageViewProfile = (ImageView) header.findViewById(R.id.imageViewProfile);

        retrieveProfileInfo.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map <String, String> map = (Map)dataSnapshot.getValue();
                Names = map.get("name");
                txtNames.setText(Names);
                Username = map.get("username");
                txtUsername.setText(Username);
                Bio = map.get("bio");
                txtBio.setText(Bio);
                ProfileImage = map.get("profileImage");
                Picasso.with(UserProfile.this).load(ProfileImage).fit().transform(new RoundedTransformation(50, 4)).centerCrop().into(imageViewProfile);
                getSupportActionBar().setTitle(Username);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //ACTICN TO FOLLOW AND UNFOLLOW USERS
        final DatabaseReference following = databaseReference.child("Following").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        final DatabaseReference blocked_user_check = databaseReference.child("Blocked Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        final DatabaseReference isPrivateAccount = databaseReference.child("Private Accounts");
        final DatabaseReference followers = databaseReference.child("Followers").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        if (!postKey.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){

            following.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild(postKey)) {
                        follow_unfollow.setText("FOLLOWING");
                    } else {
                        follow_unfollow.setText("FOLLOW");
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            retreiveFollowers.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                        isPrivateAccount.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.hasChild(postKey)){
                                    listView.addFooterView(privateAccount, null, false);
                                    listView.setAdapter(null);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                        /*Toast.makeText(UserProfile.this, "unfollow", Toast.LENGTH_SHORT).show();
                        listView.setAdapter(null);*/
                    }else {
                        Toast.makeText(UserProfile.this, "follow", Toast.LENGTH_SHORT).show();
                        //listView.addFooterView(null);
                        listView.setAdapter(chatsFirebaseListAdapter);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            /*//CHECK IF USER IS FOLLOWING



            blocked_user_check.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild(postKey)){
                        follow_unfollow.setEnabled(false);
                        follow_unfollow.setText("USER IS BLOCKED");
                        listView.addFooterView(blockFooter, null, false);
                        listView.setAdapter(null);
                    }else {

                        retreiveFollowers.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (!dataSnapshot.hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                    isPrivateAccount.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.hasChild(postKey)){
                                                listView.addFooterView(privateAccount, null, false);
                                                listView.setAdapter(null);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                                }else {

                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                        follow_unfollow.setEnabled(true);
                        following.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.hasChild(postKey)) {
                                    follow_unfollow.setText("FOLLOWING");
                                } else {
                                    follow_unfollow.setText("FOLLOW");
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });*/

            //WHAT HAPPENS WHEN follow_unfollow BUTTON IS CLICKED
            follow_unfollow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    follow_unfollow();
                }
            });
        }else {
            Intent intent = new Intent(UserProfile.this, ProfileInfo.class);
            startActivity(intent);
            finish();
        }

        //OPEN OTHER USER'S FOLLOWERS AND FOLLOWING
        txtFollowers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserProfile.this, UserFollowers.class);
                Bundle bundle = new Bundle();
                bundle.putString("key", postKey);
                bundle.putString("username", Username);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        txtFollowing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserProfile.this, UserFollowing.class);
                Bundle bundle = new Bundle();
                bundle.putString("key", postKey);
                bundle.putString("username", Username);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        //RETREIVING DATA FROM THE DATABASE AND INITIALIZING TO THE TEXTVIEWS AND IMGEVIEW
        retreiveFollowers.addValueEventListener(new ValueEventListener() {
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

        retrieveFollowing.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String s = String.valueOf(dataSnapshot.getChildrenCount());
                if (s == null) {
                    txtFollowing.setText("0");
                }else {
                    txtFollowing.setText(s);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        retrievePosts.addValueEventListener(new ValueEventListener() {
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

        DatabaseReference profilePosts = databaseReference = FirebaseDatabase.getInstance().getReference().child("Posts");

        profilePosts.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChild(postKey)){
                    Toast.makeText(UserProfile.this, "no posts", Toast.LENGTH_SHORT).show();
                    listView.addHeaderView(noPostFooter, null, false);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        chatsFirebaseListAdapter = new FirebaseListAdapter<Getters>(
                this,
                Getters.class,
                R.layout.post_layout,
                profilePosts.child(postKey)
        ) {
            @Override
            protected void populateView(final View v, final Getters model, int position) {
                ImageView profileImage1 = (ImageView) v.findViewById(R.id.imageViewProfileImage1);
                Picasso.with(UserProfile.this).load(model.getProfileImage2()).transform(new RoundedTransformation(50, 4)).fit().into(profileImage1);
                ImageView profileImage3 = (ImageView) v.findViewById(R.id.imageViewProfileImage3);
                Picasso.with(UserProfile.this).load(model.getProfileImage()).transform(new RoundedTransformation(50, 4)).fit().into(profileImage3);
                ImageView imageView1 = (ImageView) v.findViewById(R.id.imageViewImage1);
                Picasso.with(UserProfile.this).load(model.getImage1()).transform(new RoundedTransformation(50, 4)).fit().into(imageView1);
                ImageView imageView2 = (ImageView) v.findViewById(R.id.imageViewImage2);
                Picasso.with(UserProfile.this).load(model.getImage2()).transform(new RoundedTransformation(50, 4)).fit().into(imageView2);
                TextView username1 = (TextView) v.findViewById(R.id.textViewUsername1);
                username1.setText(model.getUsername2());
                TextView username2 = (TextView) v.findViewById(R.id.textViewUsername2);
                username2.setText(model.getUsername());
                final ImageView imageViewLike = (ImageView) v.findViewById(R.id.imageViewLike);

                //TIME*********************************
                TextView date = (TextView) v.findViewById(R.id.textViewDate);
                long time = model.getDate();
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

                username2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(UserProfile.this, UserProfile.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("key", model.getUid());
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                });

                profileImage3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(UserProfile.this, UserProfile.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("key", model.getUid());
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                });

                TextView caption = (TextView) v.findViewById(R.id.textViewUserCaption);

                for (; position < getCount(); position++) {
                    if (model.getCaption() == null || model.getCaption().equals("")) {
                        caption.setVisibility(View.GONE);
                    } else {
                        caption.setText(model.getUsername2() + ": " + model.getCaption());
                    }
                }

                final DatabaseReference Likes = FirebaseDatabase.getInstance().getReference().child("Likes");
                final ImageView imageViewComment = (ImageView) v.findViewById(R.id.imageViewComment);
                final DatabaseReference Comments = FirebaseDatabase.getInstance().getReference().child("Comments");
                final ImageView imageViewShare = (ImageView) v.findViewById(R.id.imageViewShare);

                imageViewShare.setOnClickListener(new android.view.View.OnClickListener() {
                    @Override
                    public void onClick(android.view.View v) {
                        Intent intent = new Intent(UserProfile.this, Share.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("key", model.getPushKey());
                        bundle.putString("uid", model.getUid2());
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                });

                Comments.child(model.getUid2()).child(model.getPushKey()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String commentCount = String.valueOf(dataSnapshot.getChildrenCount());
                        TextView comments = (TextView) v.findViewById(R.id.textViewAllComments);
                        comments.setText("View All "+commentCount+" Comments");
                        comments.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent likes = new Intent(UserProfile.this, com.example.jama.selfievselfie.Comments.class);
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
                        Intent view = new Intent(UserProfile.this, com.example.jama.selfievselfie.View.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("image", model.getImage1());
                        view.putExtras(bundle);
                        startActivity(view);
                    }
                });

                imageView2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent view = new Intent(UserProfile.this, com.example.jama.selfievselfie.View.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("image", model.getImage2());
                        view.putExtras(bundle);
                        startActivity(view);
                    }
                });

                imageViewComment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent comment = new Intent(UserProfile.this, Comments.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("key", model.getPushKey());
                        bundle.putString("uid", model.getUid2());
                        comment.putExtras(bundle);
                        startActivity(comment);
                    }
                });

                Likes.child(model.getUid2()).child(model.getPushKey()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String likeCount = String.valueOf(dataSnapshot.getChildrenCount());
                        TextView likes = (TextView) v.findViewById(R.id.textViewLikes);
                        likes.setText(likeCount+" Likes");
                        likes.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent likes = new Intent(UserProfile.this, com.example.jama.selfievselfie.Likes.class);
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
                            Likes.child(model.getUid2()).child(model.getPushKey()).child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .removeValue();
                            like = false;
                        }else {
                            Map map = new HashMap();
                            map.put("uid", FirebaseAuth.getInstance().getCurrentUser().toString());
                            map.put("pushKey", model.getPushKey());
                            map.put("username", Pusername);
                            map.put("profileImage", Pimage);
                            map.put("date", Date);
                            map.put("name", Pnames);
                            Likes.child(model.getUid2()).child(model.getPushKey()).child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(map);
                            like = true;
                        }
                    }
                });

                //VOTING ACTIVITY STARTS FROM HERE

                DatabaseReference vote1Numbers = votereference.child("Votes").child(model.getUid2()).child(model.getPushKey()).child("Votes 1");
                DatabaseReference vote2Numbers = votereference.child("Votes").child(model.getUid2()).child(model.getPushKey()).child("Votes 2");

                final Button vote1 = (Button) v.findViewById(R.id.buttonVote1);
                final Button vote2 = (Button) v.findViewById(R.id.buttonVote2);
                final TextView totalVotes = (TextView) v.findViewById(R.id.textViewTotalVotes);

                vote1Numbers.addValueEventListener(new ValueEventListener() {
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
                        //totalVotes1 = s;
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
                                .child(model.getUid2()).child(model.getPushKey()).child("Votes 2");
                        vote2.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue("voted");

                        final DatabaseReference vote1 = FirebaseDatabase.getInstance().getReference().child("Votes")
                                .child(model.getUid2()).child(model.getPushKey()).child("Votes 1");
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
                                .child(model.getUid2()).child(model.getPushKey()).child("Votes 1");
                        vote1.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue("voted");

                        final DatabaseReference vote2 = FirebaseDatabase.getInstance().getReference().child("Votes")
                                .child(model.getUid2()).child(model.getPushKey()).child("Votes 2");
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
        };
        listView.addHeaderView(header, null, false);
        listView.setAdapter(chatsFirebaseListAdapter);
        listView.setNestedScrollingEnabled(true);

        RelativeLayout noPosts = (RelativeLayout) findViewById(R.id.relativeLayout7);
        noPosts.setVisibility(View.GONE);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.user_profile, menu);
        menu.findItem((R.id.action_block_user)).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                DatabaseReference blockUser = FirebaseDatabase.getInstance().getReference();
                final DatabaseReference following = FirebaseDatabase.getInstance().getReference().child("Following").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                final DatabaseReference follower = FirebaseDatabase.getInstance().getReference().child("Followers").child(postKey);
                Map map = new HashMap();
                map.put("username", Username);
                map.put("profileImage", ProfileImage);
                map.put("name", Names);
                map.put("uid", postKey);
                blockUser.child("Blocked Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child(postKey).setValue(map);
                follower.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue();
                following.child(postKey).removeValue();
                Toast.makeText(UserProfile.this, "You Have Blocked "+Username, Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        menu.findItem(R.id.action_send_message).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent intent = new Intent(UserProfile.this, MessagingList.class);
                Bundle bundle = new Bundle();
                bundle.putString("key", postKey);
                bundle.putString("username", Username);
                intent.putExtras(bundle);
                startActivity(intent);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    private void follow_unfollow(){
        processFollowing = true;
        processFollower = true;

        final DatabaseReference following = FirebaseDatabase.getInstance().getReference().child("Following").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        final DatabaseReference follower = FirebaseDatabase.getInstance().getReference().child("Followers").child(postKey);
        final DatabaseReference notification = FirebaseDatabase.getInstance().getReference().child("Notification")
                .child(postKey);

        follower.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (processFollower){
                    if (dataSnapshot.hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                        follower.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue();
                        processFollower = false;
                    }else {
                        long Date  = System.currentTimeMillis()/1000;
                        Map map = new HashMap();
                        map.put("username", Pusername);
                        map.put("name", Pnames);
                        map.put("profileImage", Pimage);
                        map.put("uid", postKey);
                        follower.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .updateChildren(map);
                        Map map1 = new HashMap();
                        map1.put("username", Pusername);
                        map1.put("message", "started following you");
                        map1.put("profileImage", Pimage);
                        map1.put("date", Date);
                        map1.put("uid", FirebaseAuth.getInstance().getCurrentUser().getUid());
                        notification.push().setValue(map1);
                        processFollower = false;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        following.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (processFollowing) {
                    if (dataSnapshot.hasChild(postKey)) {
                        following.child(postKey).removeValue();
                        processFollowing = false;
                    } else {
                        Map map = new HashMap();
                        map.put("username", Username);
                        map.put("name", Names);
                        map.put("profileImage", ProfileImage);
                        map.put("uid", postKey);
                        following.child(postKey).updateChildren(map);
                        processFollowing = false;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
