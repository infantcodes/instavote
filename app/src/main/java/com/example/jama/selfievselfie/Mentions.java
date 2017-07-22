package com.example.jama.selfievselfie;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.jama.selfievselfie.model.CircleTransform;
import com.example.jama.selfievselfie.model.Getters;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class Mentions extends AppCompatActivity {

    DatabaseReference databaseReference, votereference;
    ListView listView;
    FirebaseListAdapter<Getters> postsFirebaseListAdapter;
    String postKey;
    String Username;
    String Pusername;
    String Pnames;
    String Pimage;
    boolean like;
    long totalVotes1, totalVotes2;
    private static final int SECOND_MILLIS = 60;
    private static final int MINUTE_MILLIS = 1 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_profile);

        listView = (ListView) findViewById(R.id.listView);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Mentions");

        votereference = FirebaseDatabase.getInstance().getReference();

        DatabaseReference profilePosts = FirebaseDatabase.getInstance().getReference().child("Mentions").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        final DatabaseReference profileInfo = FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
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

        DatabaseReference t  = FirebaseDatabase.getInstance().getReference();
        Query query = t.child("All Posts").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).orderByChild("uid")
                .equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid());

        postsFirebaseListAdapter = new FirebaseListAdapter<Getters>(
                this,
                Getters.class,
                R.layout.post_layout,
                query
        ) {
            @Override
            protected void populateView(final android.view.View v, final Getters model, int position) {

                LinearLayout linearLayoutSinglePost = (LinearLayout) v.findViewById(R.id.linearLayoutSinglePost);
                linearLayoutSinglePost.setVisibility(android.view.View.GONE);
                LinearLayout lineartwo = (LinearLayout) v.findViewById(R.id.linearLayoutPost);
                lineartwo.setVisibility(android.view.View.VISIBLE);

                ImageView profileImage1 = (ImageView) v.findViewById(R.id.imageViewProfileImage1);
                profileImage1.setOnClickListener(new android.view.View.OnClickListener() {
                    @Override
                    public void onClick(android.view.View v) {
                        Intent intent = new Intent(Mentions.this, UserProfile.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("key", model.getUid2());
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                });
                Glide.with(Mentions.this).load(model.getProfileImage2()).bitmapTransform(new CircleTransform(Mentions.this)).into(profileImage1);
                ImageView profileImage3 = (ImageView) v.findViewById(R.id.imageViewProfileImage3);
                Glide.with(Mentions.this).load(model.getProfileImage()).bitmapTransform(new CircleTransform(Mentions.this)).into(profileImage3);
                ImageView imageView1 = (ImageView) v.findViewById(R.id.imageViewImage1);
                Glide.with(Mentions.this).load(model.getImage1()).into(imageView1);
                ImageView imageView2 = (ImageView) v.findViewById(R.id.imageViewImage2);
                Glide.with(Mentions.this).load(model.getImage2()).into(imageView2);
                TextView username1 = (TextView) v.findViewById(R.id.textViewUsername1);
                username1.setText(model.getUsername2());
                TextView username2 = (TextView) v.findViewById(R.id.textViewUsername2);
                username2.setText(model.getUsername());
                final ImageView imageViewComment = (ImageView) v.findViewById(R.id.imageViewComment);
                final DatabaseReference Comments = FirebaseDatabase.getInstance().getReference().child("Comments");
                final DatabaseReference Likes = FirebaseDatabase.getInstance().getReference().child("Likes");
                final ImageView imageViewLike = (ImageView) v.findViewById(R.id.imageViewLike);
                final ImageView imageViewShare = (ImageView) v.findViewById(R.id.imageViewShare);
                final ImageView imageViewOptions = (ImageView) v.findViewById(R.id.imageViewOptions);
                imageViewOptions.setVisibility(android.view.View.GONE);

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

                TextView caption = (TextView) v.findViewById(R.id.textViewUserCaption);

                //for (; position<getCount(); position++) {
                    if (model.getCaption() == null) {
                        caption.setVisibility(android.view.View.GONE);
                    } else {
                        caption.setText(model.getUsername2() + " " + model.getCaption());
                    }
               // }

                Comments.child(model.getUid2()).child(model.getPushKey()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String commentCount = String.valueOf(dataSnapshot.getChildrenCount());
                        TextView comments = (TextView) v.findViewById(R.id.textViewAllComments);
                        comments.setText("View All "+commentCount+" Comments");
                        comments.setOnClickListener(new android.view.View.OnClickListener() {
                            @Override
                            public void onClick(android.view.View v) {
                                Intent likes = new Intent(Mentions.this, com.example.jama.selfievselfie.Comments.class);
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

                imageView1.setOnClickListener(new android.view.View.OnClickListener() {
                    @Override
                    public void onClick(android.view.View v) {
                        Intent view = new Intent(Mentions.this, com.example.jama.selfievselfie.View.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("image", model.getImage1());
                        view.putExtras(bundle);
                        startActivity(view);
                    }
                });

                imageView2.setOnClickListener(new android.view.View.OnClickListener() {
                    @Override
                    public void onClick(android.view.View v) {
                        Intent view = new Intent(Mentions.this, com.example.jama.selfievselfie.View.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("image", model.getImage2());
                        view.putExtras(bundle);
                        startActivity(view);
                    }
                });

                imageViewComment.setOnClickListener(new android.view.View.OnClickListener() {
                    @Override
                    public void onClick(android.view.View v) {
                        Intent comment = new Intent(Mentions.this, Comments.class);
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
                        likes.setOnClickListener(new android.view.View.OnClickListener() {
                            @Override
                            public void onClick(android.view.View v) {
                                Intent likes = new Intent(Mentions.this, com.example.jama.selfievselfie.Likes.class);
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

                imageViewLike.setOnClickListener(new android.view.View.OnClickListener() {
                    @Override
                    public void onClick(android.view.View v) {
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
                            map.put("date", System.currentTimeMillis()/1000);
                            map.put("name", Pnames);
                            Likes.child(model.getUid2()).child(model.getPushKey()).child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(map);
                            like = true;
                        }
                    }
                });

                imageViewShare.setOnClickListener(new android.view.View.OnClickListener() {
                    @Override
                    public void onClick(android.view.View v) {
                        Intent intent = new Intent(Mentions.this, Share.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("key", model.getPushKey());
                        bundle.putString("uid", model.getUid2());
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                });

                username1.setOnClickListener(new android.view.View.OnClickListener() {
                    @Override
                    public void onClick(android.view.View v) {
                        Intent intent = new Intent(Mentions.this, UserProfile.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("key", model.getUid2());
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                });

                //VOTING ACTIVITY STARTS FROM HERE

                DatabaseReference vote1Numbers = votereference.child("Votes").child(model.getUid2()).child(model.getPushKey()).child("Votes 1");
                DatabaseReference vote2Numbers = votereference.child("Votes").child(model.getUid2()).child(model.getPushKey()).child("Votes 2");

                final Button vote1 = (Button) v.findViewById(R.id.buttonVote1);
                final Button vote2 = (Button) v.findViewById(R.id.buttonVote2);
                final TextView totalVotes = (TextView) v.findViewById(R.id.textViewTotalVotes);

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

        listView.setAdapter(postsFirebaseListAdapter);
        RelativeLayout noPosts = (RelativeLayout) findViewById(R.id.relativeLayout7);
        noPosts.setVisibility(android.view.View.GONE);
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}
