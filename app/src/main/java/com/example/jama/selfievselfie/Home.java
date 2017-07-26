package com.example.jama.selfievselfie;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.jama.selfievselfie.model.CircleTransform;
import com.example.jama.selfievselfie.model.Getters;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.NativeExpressAdView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by JAMA on 3/13/2017.
 */

public class Home extends Fragment {

    //Test on the github repo

    ListView listView;
    DatabaseReference databaseReference, checkAllPosts, votereference;
    boolean like, processVote1, processVote2;
    String Names, Username, ProfileImage, mAuth;
    private static final int CAMERA_REQUEST_CODE = 1;
    FirebaseListAdapter<Getters> chatsFirebaseListAdapter;
    private static final int SECOND_MILLIS = 60;
    private static final int MINUTE_MILLIS = 1 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;
    int top1, index1;
    int listP = 10;

    public static Home newInstance() {
        Home fragment = new Home();
        return fragment;
    }

    public Home() {
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        final View rootview =  inflater.inflate(R.layout.activity_home, container, false);

        listView = (ListView) rootview.findViewById(R.id.listView);

        votereference = FirebaseDatabase.getInstance().getReference();

        final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        mAuth = FirebaseAuth.getInstance().getCurrentUser().getUid();

        databaseReference = FirebaseDatabase.getInstance().getReference().child("All Posts").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        DatabaseReference profileInfo = FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("Profile Info");
        databaseReference.keepSynced(true);

        profileInfo.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map <String, String> map = (Map)dataSnapshot.getValue();
                ProfileImage = map.get("profileImage");
                Username = map.get("username");
                Names = map.get("name");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        chatsFirebaseListAdapter = new FirebaseListAdapter<Getters>(
                getActivity(),
                Getters.class,
                R.layout.post_layout,
                databaseReference
        ) {
            @Override
            protected void populateView(final View v, final Getters model, int position) {

                LinearLayout linearLayoutPost = (LinearLayout) v.findViewById(R.id.linearLayoutPost);
                LinearLayout linearLayoutTextOnly = (LinearLayout) v.findViewById(R.id.linearLayoutTextOnly);
                LinearLayout linearLayoutSinglePost = (LinearLayout) v.findViewById(R.id.linearLayoutSinglePost);
                final RelativeLayout linearLayoutAds = (RelativeLayout) v.findViewById(R.id.adLayout);
                linearLayoutAds.setVisibility(View.GONE);

                if (position % 8 == 2){
                    linearLayoutAds.setVisibility(View.VISIBLE);

                    NativeExpressAdView mAdView = (NativeExpressAdView) v.findViewById(R.id.adView);
                    AdRequest adRequest = new AdRequest.Builder()
                            .addTestDevice("0EDB54D55A01A36E44405E501E1E77EA").build();
                    mAdView.loadAd(adRequest);
                }

                if (model.getUid() != null){
                    linearLayoutPost.setVisibility(View.VISIBLE);
                    linearLayoutSinglePost.setVisibility(View.GONE);
                    linearLayoutTextOnly.setVisibility(View.GONE);
                    final String postKey = getRef(position).getKey();
                    final ImageView profileImage1 = (ImageView) v.findViewById(R.id.imageViewProfileImage1);
                    profileImage1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getActivity(), UserProfile.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("key", model.getUid2());
                            intent.putExtras(bundle);
                            startActivity(intent);
                        }
                    });
                    Glide.with(getContext()).load(model.getProfileImage2()).bitmapTransform(new CircleTransform(getActivity()))
                            .diskCacheStrategy(DiskCacheStrategy.RESULT).into(profileImage1);

                    final ImageView profileImage3 = (ImageView) v.findViewById(R.id.imageViewProfileImage3);
                    profileImage3.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getActivity(), UserProfile.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("key", model.getUid());
                            intent.putExtras(bundle);
                            startActivity(intent);
                        }
                    });
                    Glide.with(getContext()).load(model.getProfileImage()).bitmapTransform(new CircleTransform(getActivity()))
                            .diskCacheStrategy(DiskCacheStrategy.RESULT).into(profileImage3);

                    final ImageView imageView1 = (ImageView) v.findViewById(R.id.imageViewImage1);
                    Glide.with(getContext()).load(model.getImage1()).diskCacheStrategy(DiskCacheStrategy.RESULT).into(imageView1);

                    final ImageView imageView2 = (ImageView) v.findViewById(R.id.imageViewImage2);
                    Glide.with(getContext()).load(model.getImage2()).diskCacheStrategy(DiskCacheStrategy.RESULT).into(imageView2);

                    TextView username1 = (TextView) v.findViewById(R.id.textViewUsername1);
                    username1.setText(model.getUsername2());
                    TextView username2 = (TextView) v.findViewById(R.id.textViewUsername2);
                    username2.setText(model.getUsername());
                    final ImageView imageViewLike = (ImageView) v.findViewById(R.id.imageViewLike);
                    final ImageView imageViewShare = (ImageView) v.findViewById(R.id.imageViewShare);
                    final ImageView imageViewOptions = (ImageView) v.findViewById(R.id.imageViewOptions);
                    final ImageView imageViewComment = (ImageView) v.findViewById(R.id.imageViewComment);
                    final DatabaseReference Likes = FirebaseDatabase.getInstance().getReference().child("Likes");
                    final DatabaseReference Comments = FirebaseDatabase.getInstance().getReference().child("Comments");
                    final DatabaseReference Notification = FirebaseDatabase.getInstance().getReference().child("Notification");

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

                    for (; position < getCount(); position++) {
                        if (model.getCaption() == null || model.getCaption().equals("")) {
                            caption.setVisibility(View.GONE);
                        } else {
                            caption.setText(model.getUsername2() + ": " + model.getCaption());
                        }
                    }

                    imageView1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent view = new Intent(getActivity(), com.example.jama.selfievselfie.View.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("image", model.getImage1());
                            view.putExtras(bundle);
                            startActivity(view);
                        }
                    });

                    imageView2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent view = new Intent(getActivity(), com.example.jama.selfievselfie.View.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("image", model.getImage2());
                            view.putExtras(bundle);
                            startActivity(view);
                        }
                    });

                    imageViewComment.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent comment = new Intent(getActivity(), Comments.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("key", postKey);
                            bundle.putString("uid", model.getUid2());
                            comment.putExtras(bundle);
                            startActivity(comment);
                        }
                    });

                    //GET THE NUMBER OF VOTES
                    Comments.child(model.getUid2()).child(model.getPushKey()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String commentCount = String.valueOf(dataSnapshot.getChildrenCount());
                            TextView comments = (TextView) v.findViewById(R.id.textViewAllComments);
                            comments.setText("View All " + commentCount + " Comments");
                            comments.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent comments = new Intent(getActivity(), com.example.jama.selfievselfie.Comments.class);
                                    Bundle bundle = new Bundle();
                                    bundle.putString("key", model.getPushKey());
                                    bundle.putString("uid", model.getUid2());
                                    comments.putExtras(bundle);
                                    startActivity(comments);
                                }
                            });
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    //GET THE NUMBER OF LIKES
                    Likes.child(model.getUid2()).child(model.getPushKey()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String likeCount = String.valueOf(dataSnapshot.getChildrenCount());
                            TextView likes = (TextView) v.findViewById(R.id.textViewLikes);
                            likes.setText(likeCount + " Likes");
                            likes.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent likes = new Intent(getActivity(), com.example.jama.selfievselfie.Likes.class);
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

                    //LIKES TRIGGERS
                    Likes.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.child(model.getUid2()).child(postKey)
                                    .hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                imageViewLike.setImageResource(R.drawable.like_red);
                            } else {
                                imageViewLike.setImageResource(R.drawable.ic_favorite_black_24dp);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    //REMOVE OR DELETE LIKES
                    imageViewLike.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (like == true) {
                                Likes.child(model.getUid2()).child(model.getPushKey()).child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .removeValue();
                                like = false;
                            } else {
                                Map map = new HashMap();
                                map.put("uid", mAuth);
                                map.put("pushKey", model.getPushKey());
                                map.put("username", Username);
                                map.put("profileImage", ProfileImage);
                                map.put("date", System.currentTimeMillis()/1000);
                                map.put("name", Names);

                                Likes.child(model.getUid2()).child(model.getPushKey()).child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .setValue(map);
                                like = true;

                                if (!model.getUid2().equals(mAuth)) {
                                    Map map1 = new HashMap();
                                    map1.put("username", Username);
                                    map1.put("message", "liked your post");
                                    map1.put("profileImage", ProfileImage);
                                    map1.put("date", System.currentTimeMillis()/1000);
                                    map1.put("pushKey", model.getPushKey());
                                    map1.put("uid", mAuth);
                                    Notification.child(model.getUid2()).push().setValue(map1);
                                }
                            }
                        }
                    });

                    username2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getActivity(), UserProfile.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("key", model.getUid());
                            intent.putExtras(bundle);
                            startActivity(intent);
                        }
                    });

                    username1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getActivity(), UserProfile.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("key", model.getUid2());
                            intent.putExtras(bundle);
                            startActivity(intent);
                        }
                    });

                    imageViewOptions.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            menuOptions();
                        }
                    });

                    imageViewShare.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getActivity(), Share.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("key", model.getPushKey());
                            bundle.putString("uid", model.getUid2());
                            intent.putExtras(bundle);
                            startActivity(intent);
                        }
                    });

                    //VOTING ACTIVITY STARTS FROM HERE

                    final DatabaseReference vote1Numbers = votereference.child("Votes").child(model.getUid2()).child(model.getPushKey()).child("Votes 1");
                    final DatabaseReference vote2Numbers = votereference.child("Votes").child(model.getUid2()).child(model.getPushKey()).child("Votes 2");

                    final Button vote1 = (Button) v.findViewById(R.id.buttonVote1);
                    final Button vote2 = (Button) v.findViewById(R.id.buttonVote2);
                    final TextView totalVotes = (TextView) v.findViewById(R.id.textViewTotalVotes);

                    vote1Numbers.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            final long vote1no = (dataSnapshot.getChildrenCount());
                            vote1.setText("  "+vote1no);

                            if (dataSnapshot.hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                vote1.setBackgroundResource(R.drawable.rounded_corner_white);
                                vote1.setTextColor(Color.parseColor("#E91E63"));
                            }else {
                                vote1.setBackgroundResource(R.drawable.rounded_corner_pink);
                                vote1.setTextColor(Color.parseColor("#ffffff"));
                            }

                            vote2Numbers.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    long vote2no = (dataSnapshot.getChildrenCount());
                                    vote2.setText("  "+vote2no);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    vote2Numbers.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            final long vote2no = (dataSnapshot.getChildrenCount());
                            vote2.setText("  "+vote2no);

                            if (dataSnapshot.hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                vote2.setBackgroundResource(R.drawable.rounded_corner_white);
                                vote2.setTextColor(Color.parseColor("#E91E63"));
                            }else {
                                vote2.setBackgroundResource(R.drawable.rounded_corner_pink);
                                vote2.setTextColor(Color.parseColor("#ffffff"));
                            }

                            vote1Numbers.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    long vote1no = (dataSnapshot.getChildrenCount());
                                    vote1.setText("  "+vote1no);


                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    vote2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
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

                    vote1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
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

                    // }
                }else {
                    if (model.getChoice1() == null){
                        linearLayoutPost.setVisibility(View.GONE);
                        linearLayoutSinglePost.setVisibility(View.VISIBLE);
                        linearLayoutTextOnly.setVisibility(View.GONE);
                        final String postKey = getRef(position).getKey();
                        final ImageView profileImage1 = (ImageView) v.findViewById(R.id.imageViewProfileImageSinglePost);
                        profileImage1.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(getActivity(), UserProfile.class);
                                Bundle bundle = new Bundle();
                                bundle.putString("key", model.getUid2());
                                intent.putExtras(bundle);
                                startActivity(intent);
                            }
                        });
                        Glide.with(getContext()).load(model.getProfileImage2()).bitmapTransform(new CircleTransform(getActivity()))
                                .diskCacheStrategy(DiskCacheStrategy.RESULT).into(profileImage1);

                        final ImageView imageView1 = (ImageView) v.findViewById(R.id.imageViewSinglePostImage);

                        Glide.with(getContext()).load(model.getImage1()).diskCacheStrategy(DiskCacheStrategy.RESULT).into(imageView1);

                        TextView username1 = (TextView) v.findViewById(R.id.textViewUsernameSinglePost);
                        username1.setText(model.getUsername2());
                        final ImageView imageViewLike = (ImageView) v.findViewById(R.id.imageViewLikeSinglePost);
                        final ImageView imageViewShare = (ImageView) v.findViewById(R.id.imageViewShareSinglePost);
                        final ImageView imageViewOptions = (ImageView) v.findViewById(R.id.imageViewOptionsSinglePost);
                        final ImageView imageViewComment = (ImageView) v.findViewById(R.id.imageViewCommentSinglePost);
                        final DatabaseReference Likes = FirebaseDatabase.getInstance().getReference().child("Likes");
                        final DatabaseReference Comments = FirebaseDatabase.getInstance().getReference().child("Comments");
                        final DatabaseReference Notification = FirebaseDatabase.getInstance().getReference().child("Notification");

                        //TIME*********************************
                        TextView date = (TextView) v.findViewById(R.id.textViewSinglePostDate);
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

                        TextView caption = (TextView) v.findViewById(R.id.textViewSinglePostCaption);

                        for (; position < getCount(); position++) {
                            if (model.getCaption() == null || model.getCaption().equals("")) {
                                caption.setVisibility(View.GONE);
                            } else {
                                caption.setText(model.getUsername2() + ": " + model.getCaption());
                            }
                        }

                        imageViewComment.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent comment = new Intent(getActivity(), Comments.class);
                                Bundle bundle = new Bundle();
                                bundle.putString("key", postKey);
                                bundle.putString("uid", model.getUid2());
                                comment.putExtras(bundle);
                                startActivity(comment);
                            }
                        });

                        //GET THE NUMBER OF VOTES
                        Comments.child(model.getUid2()).child(model.getPushKey()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                String commentCount = String.valueOf(dataSnapshot.getChildrenCount());
                                TextView comments = (TextView) v.findViewById(R.id.textViewSimglePostComments);
                                comments.setText("View All " + commentCount + " Comments");
                                comments.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent comments = new Intent(getActivity(), com.example.jama.selfievselfie.Comments.class);
                                        Bundle bundle = new Bundle();
                                        bundle.putString("key", model.getPushKey());
                                        bundle.putString("uid", model.getUid2());
                                        comments.putExtras(bundle);
                                        startActivity(comments);
                                    }
                                });
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                        //GET THE NUMBER OF LIKES
                        Likes.child(model.getUid2()).child(model.getPushKey()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                String likeCount = String.valueOf(dataSnapshot.getChildrenCount());
                                TextView likes = (TextView) v.findViewById(R.id.textViewSinglePostLikes);
                                likes.setText(likeCount + " Likes");
                                likes.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent likes = new Intent(getActivity(), com.example.jama.selfievselfie.Likes.class);
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

                        //LIKES TRIGGERS
                        Likes.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.child(model.getUid2()).child(postKey)
                                        .hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                    imageViewLike.setImageResource(R.drawable.like_red);
                                } else {
                                    imageViewLike.setImageResource(R.drawable.ic_favorite_black_24dp);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                        //REMOVE OR DELETE LIKES
                        imageViewLike.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (like == true) {
                                    Likes.child(model.getUid2()).child(model.getPushKey()).child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                            .removeValue();
                                    like = false;
                                } else {
                                    Map map = new HashMap();
                                    map.put("uid", mAuth);
                                    map.put("pushKey", model.getPushKey());
                                    map.put("username", Username);
                                    map.put("profileImage", ProfileImage);
                                    map.put("date", System.currentTimeMillis()/1000);
                                    map.put("name", Names);

                                    Likes.child(model.getUid2()).child(model.getPushKey()).child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                            .setValue(map);
                                    like = true;

                                    if (!model.getUid2().equals(mAuth)) {
                                        Map map1 = new HashMap();
                                        map1.put("username", Username);
                                        map1.put("message", "liked your post");
                                        map1.put("profileImage", ProfileImage);
                                        map1.put("date", System.currentTimeMillis()/1000);
                                        map1.put("pushKey", model.getPushKey());
                                        map1.put("uid", mAuth);
                                        Notification.child(model.getUid2()).push().setValue(map1);
                                    }
                                }
                            }
                        });

                        username1.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(getActivity(), UserProfile.class);
                                Bundle bundle = new Bundle();
                                bundle.putString("key", model.getUid2());
                                intent.putExtras(bundle);
                                startActivity(intent);
                            }
                        });

                        imageViewOptions.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                menuOptions();
                            }
                        });

                        imageViewShare.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(getActivity(), Share.class);
                                Bundle bundle = new Bundle();
                                bundle.putString("key", model.getPushKey());
                                bundle.putString("uid", model.getUid2());
                                intent.putExtras(bundle);
                                startActivity(intent);
                            }
                        });

                        //VOTING ACTIVITY STARTS FROM HERE

                        final DatabaseReference vote1Numbers = votereference.child("Votes").child(model.getUid2()).child(model.getPushKey()).child("Votes 1");
                        final DatabaseReference vote2Numbers = votereference.child("Votes").child(model.getUid2()).child(model.getPushKey()).child("Votes 2");

                        final Button vote1 = (Button) v.findViewById(R.id.buttonSinglePost1);
                        final Button vote2 = (Button) v.findViewById(R.id.buttonSinglePost2);
                        final TextView totalVotes = (TextView) v.findViewById(R.id.textViewSinglePostTotalVotes);

                        vote1Numbers.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                final long vote1no = (dataSnapshot.getChildrenCount());
                                vote1.setText(vote1no+"");

                                if (dataSnapshot.hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                    vote1.setBackgroundResource(R.drawable.rounded_corner_white);
                                    vote1.setTextColor(Color.parseColor("#E91E63"));

                                }else {
                                    vote1.setBackgroundResource(R.drawable.rounded_corner_pink);
                                    vote1.setTextColor(Color.parseColor("#ffffff"));
                                }

                                vote2Numbers.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        long vote2no = (dataSnapshot.getChildrenCount());
                                        vote2.setText(vote2no+"");
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                        vote2Numbers.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                final long vote2no = (dataSnapshot.getChildrenCount());
                                vote2.setText(vote2no+"");

                                if (dataSnapshot.hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                    vote2.setBackgroundResource(R.drawable.rounded_corner_white);
                                    vote2.setTextColor(Color.parseColor("#E91E63"));
                                }else {
                                    vote2.setBackgroundResource(R.drawable.rounded_corner_pink);
                                    vote2.setTextColor(Color.parseColor("#ffffff"));
                                }

                                vote1Numbers.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        long vote1no = (dataSnapshot.getChildrenCount());
                                        vote1.setText(vote1no+"");


                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                        vote2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
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

                        vote1.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
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
                    }else {
                        linearLayoutPost.setVisibility(View.GONE);
                        linearLayoutSinglePost.setVisibility(View.GONE);
                        linearLayoutTextOnly.setVisibility(View.VISIBLE);

                        final String postKey = getRef(position).getKey();
                        final ImageView profileImage1 = (ImageView) v.findViewById(R.id.imageViewProfileImageTextOnly);
                        profileImage1.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(getActivity(), UserProfile.class);
                                Bundle bundle = new Bundle();
                                bundle.putString("key", model.getUid2());
                                intent.putExtras(bundle);
                                startActivity(intent);
                            }
                        });

                        Glide.with(getContext()).load(model.getProfileImage2()).bitmapTransform(new CircleTransform(getActivity()))
                                .diskCacheStrategy(DiskCacheStrategy.RESULT).into(profileImage1);


                        TextView username1 = (TextView) v.findViewById(R.id.textViewUsernameTextOnly);
                        username1.setText(model.getUsername2());
                        final ImageView imageViewLike = (ImageView) v.findViewById(R.id.imageViewLikeTextOnly);
                        final ImageView imageViewShare = (ImageView) v.findViewById(R.id.imageViewShareTextOnly);
                        final ImageView imageViewOptions = (ImageView) v.findViewById(R.id.imageViewOptionsTextOnly);
                        final ImageView imageViewComment = (ImageView) v.findViewById(R.id.imageViewCommentTextOnly);
                        final DatabaseReference Likes = FirebaseDatabase.getInstance().getReference().child("Likes");
                        final DatabaseReference Comments = FirebaseDatabase.getInstance().getReference().child("Comments");
                        final DatabaseReference Notification = FirebaseDatabase.getInstance().getReference().child("Notification");

                        //TIME*********************************
                        TextView date = (TextView) v.findViewById(R.id.textViewTextOnlyDate);
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

                        TextView caption = (TextView) v.findViewById(R.id.textViewTextOnlyCaption);
                        if (model.getCaption() == ""){
                            caption.setVisibility(View.GONE);
                        }else {
                            caption.setVisibility(View.VISIBLE);
                            caption.setText(model.getCaption());
                        }

                        imageViewComment.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent comment = new Intent(getActivity(), Comments.class);
                                Bundle bundle = new Bundle();
                                bundle.putString("key", postKey);
                                bundle.putString("uid", model.getUid2());
                                comment.putExtras(bundle);
                                startActivity(comment);
                            }
                        });

                        //GET THE NUMBER OF COMMENTS
                        Comments.child(model.getUid2()).child(model.getPushKey()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                String commentCount = String.valueOf(dataSnapshot.getChildrenCount());
                                TextView comments = (TextView) v.findViewById(R.id.textViewTextOnlyComments);
                                comments.setText("View All " + commentCount + " Comments");
                                comments.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent comments = new Intent(getActivity(), com.example.jama.selfievselfie.Comments.class);
                                        Bundle bundle = new Bundle();
                                        bundle.putString("key", model.getPushKey());
                                        bundle.putString("uid", model.getUid2());
                                        comments.putExtras(bundle);
                                        startActivity(comments);
                                    }
                                });
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                        //GET THE NUMBER OF LIKES
                        Likes.child(model.getUid2()).child(model.getPushKey()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                String likeCount = String.valueOf(dataSnapshot.getChildrenCount());
                                TextView likes = (TextView) v.findViewById(R.id.textViewTextOnlyLikes);
                                likes.setText(likeCount + " Likes");
                                likes.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent likes = new Intent(getActivity(), com.example.jama.selfievselfie.Likes.class);
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

                        //LIKES TRIGGERS
                        Likes.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.child(model.getUid2()).child(postKey)
                                        .hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                    imageViewLike.setImageResource(R.drawable.like_red);
                                } else {
                                    imageViewLike.setImageResource(R.drawable.ic_favorite_black_24dp);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                        //REMOVE OR DELETE LIKES
                        imageViewLike.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (like == true) {
                                    Likes.child(model.getUid2()).child(model.getPushKey()).child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                            .removeValue();
                                    like = false;
                                } else {
                                    Map map = new HashMap();
                                    map.put("uid", mAuth);
                                    map.put("pushKey", model.getPushKey());
                                    map.put("username", Username);
                                    map.put("profileImage", ProfileImage);
                                    map.put("date", System.currentTimeMillis()/1000);
                                    map.put("name", Names);

                                    Likes.child(model.getUid2()).child(model.getPushKey()).child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                            .setValue(map);
                                    like = true;

                                    if (!model.getUid2().equals(mAuth)) {
                                        Map map1 = new HashMap();
                                        map1.put("username", Username);
                                        map1.put("message", "liked your post");
                                        map1.put("profileImage", ProfileImage);
                                        map1.put("date", System.currentTimeMillis()/1000);
                                        map1.put("pushKey", model.getPushKey());
                                        map1.put("uid", mAuth);
                                        Notification.child(model.getUid2()).push().setValue(map1);
                                    }
                                }
                            }
                        });

                        username1.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(getActivity(), UserProfile.class);
                                Bundle bundle = new Bundle();
                                bundle.putString("key", model.getUid2());
                                intent.putExtras(bundle);
                                startActivity(intent);
                            }
                        });

                        imageViewOptions.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                menuOptions();
                            }
                        });

                        imageViewShare.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(getActivity(), Share.class);
                                Bundle bundle = new Bundle();
                                bundle.putString("key", model.getPushKey());
                                bundle.putString("uid", model.getUid2());
                                intent.putExtras(bundle);
                                startActivity(intent);
                            }
                        });

                        //VOTING ACTIVITY STARTS HERE

                        final Button choice1 = (Button) v.findViewById(R.id.buttonTextOnly1);
                        final Button choice2 = (Button) v.findViewById(R.id.buttonTextOnly2);
                        final Button choice3 = (Button) v.findViewById(R.id.buttonTextOnly3);
                        final Button choice4 = (Button) v.findViewById(R.id.buttonTextOnly4);
                        final Button choice5 = (Button) v.findViewById(R.id.buttonTextOnly5);

                        ImageView imageView1 = (ImageView) v.findViewById(R.id.imageViewChoice1);
                        ImageView imageView2 = (ImageView) v.findViewById(R.id.imageViewChoice2);
                        ImageView imageView3 = (ImageView) v.findViewById(R.id.imageViewChoice3);
                        ImageView imageView4 = (ImageView) v.findViewById(R.id.imageViewChoice4);
                        ImageView imageView5 = (ImageView) v.findViewById(R.id.imageViewChoice5);

                        RelativeLayout relativeLayout1 = (RelativeLayout) v.findViewById(R.id.relativeLayoutChoice1);
                        RelativeLayout relativeLayout2 = (RelativeLayout) v.findViewById(R.id.relativeLayoutChoice2);
                        RelativeLayout relativeLayout3 = (RelativeLayout) v.findViewById(R.id.relativeLayoutChoice3);
                        RelativeLayout relativeLayout4 = (RelativeLayout) v.findViewById(R.id.relativeLayoutChoice4);
                        RelativeLayout relativeLayout5 = (RelativeLayout) v.findViewById(R.id.relativeLayoutChoice5);

                        if (model.getChoice1Image() != null){
                            imageView1.setVisibility(View.VISIBLE);
                            Glide.with(getActivity()).load(model.getChoice1Image()).into(imageView1);
                        }
                        if (model.getChoice2Image() != null){
                            imageView2.setVisibility(View.VISIBLE);
                            Glide.with(getActivity()).load(model.getChoice2Image()).into(imageView2);
                        }
                        if (model.getChoice3Image() != null){
                            imageView3.setVisibility(View.VISIBLE);
                            Glide.with(getActivity()).load(model.getChoice3Image()).into(imageView3);
                        }
                        if (model.getChoice4Image() != null){
                            imageView4.setVisibility(View.VISIBLE);
                            Glide.with(getActivity()).load(model.getChoice4Image()).into(imageView4);
                        }
                        if (model.getChoice5Image() != null){
                            imageView5.setVisibility(View.VISIBLE);
                            Glide.with(getActivity()).load(model.getChoice5Image()).into(imageView5);
                        }

                        if (model.getChoice1() == null){
                            relativeLayout1.setVisibility(View.GONE);
                        }
                        if (model.getChoice2() == null){
                            relativeLayout2.setVisibility(View.GONE);
                        }
                        if (model.getChoice3() == null){
                            relativeLayout3.setVisibility(View.GONE);
                        }
                        if (model.getChoice4() == null){
                            relativeLayout4.setVisibility(View.GONE);
                        }
                        if (model.getChoice5() == null){
                            relativeLayout5.setVisibility(View.GONE);
                        }

                        DatabaseReference choiceRef1 = FirebaseDatabase.getInstance().getReference().child("Votes").child(model.getUid2())
                                .child(model.getPushKey()).child("option1");
                        DatabaseReference choiceRef2 = FirebaseDatabase.getInstance().getReference().child("Votes").child(model.getUid2())
                                .child(model.getPushKey()).child("option2");
                        DatabaseReference choiceRef3 = FirebaseDatabase.getInstance().getReference().child("Votes").child(model.getUid2())
                                .child(model.getPushKey()).child("option3");
                        DatabaseReference choiceRef4 = FirebaseDatabase.getInstance().getReference().child("Votes").child(model.getUid2())
                                .child(model.getPushKey()).child("option4");
                        DatabaseReference choiceRef5 = FirebaseDatabase.getInstance().getReference().child("Votes").child(model.getUid2())
                                .child(model.getPushKey()).child("option5");

                        //GET NUMBER OF VOTES
                        choiceRef1.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                long s = (dataSnapshot.getChildrenCount());
                                //totalVotes1 = s;
                                choice1.setText(" "+s+" "+ model.getChoice1());
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                        choiceRef2.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                long s = (dataSnapshot.getChildrenCount());
                                //totalVotes1 = s;
                                choice2.setText(" "+s+" "+ model.getChoice2());
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                        choiceRef3.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                long s = (dataSnapshot.getChildrenCount());
                                //totalVotes1 = s;
                                choice3.setText(" "+s+" "+ model.getChoice3());
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                        choiceRef4.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                long s = (dataSnapshot.getChildrenCount());
                                //totalVotes1 = s;
                                choice4.setText(" "+s+" "+ model.getChoice4());
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                        choiceRef5.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                long s = (dataSnapshot.getChildrenCount());
                                //totalVotes1 = s;
                                choice5.setText(" "+s+" "+ model.getChoice5());
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                        choiceRef1.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                    choice1.setBackgroundResource(R.drawable.rounded_corner_white);
                                    choice1.setTextColor(Color.parseColor("#E91E63"));
                                }else {
                                    choice1.setBackgroundResource(R.drawable.rounded_corner_pink);
                                    choice1.setTextColor(Color.parseColor("#ffffff"));
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                        choiceRef2.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                    choice2.setBackgroundResource(R.drawable.rounded_corner_white);
                                    choice2.setTextColor(Color.parseColor("#E91E63"));
                                }else {
                                    choice2.setBackgroundResource(R.drawable.rounded_corner_pink);
                                    choice2.setTextColor(Color.parseColor("#ffffff"));
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                        choiceRef3.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                    choice3.setBackgroundResource(R.drawable.rounded_corner_white);
                                    choice3.setTextColor(Color.parseColor("#E91E63"));
                                }else {
                                    choice3.setBackgroundResource(R.drawable.rounded_corner_pink);
                                    choice3.setTextColor(Color.parseColor("#ffffff"));
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                        choiceRef4.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                    choice4.setBackgroundResource(R.drawable.rounded_corner_white);
                                    choice4.setTextColor(Color.parseColor("#E91E63"));
                                }else {
                                    choice4.setBackgroundResource(R.drawable.rounded_corner_pink);
                                    choice4.setTextColor(Color.parseColor("#ffffff"));
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                        choiceRef5.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                    choice5.setBackgroundResource(R.drawable.rounded_corner_white);
                                    choice5.setTextColor(Color.parseColor("#E91E63"));
                                }else {
                                    choice5.setBackgroundResource(R.drawable.rounded_corner_pink);
                                    choice5.setTextColor(Color.parseColor("#ffffff"));
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                        //SET CLICK VOTES
                        choice1.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                DatabaseReference refchoice1 = FirebaseDatabase.getInstance().getReference().child("Votes")
                                        .child(model.getUid2()).child(model.getPushKey()).child("option1");
                                refchoice1.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue("voted");

                                DatabaseReference choiceVotes = FirebaseDatabase.getInstance().getReference().child("Votes")
                                        .child(model.getUid2()).child(model.getPushKey());

                                final DatabaseReference choice2 = choiceVotes.child("option2");
                                final DatabaseReference choice3 = choiceVotes.child("option3");
                                final DatabaseReference choice4 = choiceVotes.child("option4");
                                final DatabaseReference choice5 = choiceVotes.child("option5");

                                choice2.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                            choice2.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue();
                                        }
                                    }
                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                    }
                                });

                                choice3.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                            choice3.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue();
                                        }
                                    }
                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                    }
                                });

                                choice4.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                            choice4.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue();
                                        }
                                    }
                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                    }
                                });

                                choice5.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                            choice5.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue();
                                        }
                                    }
                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                    }
                                });
                            }
                        });

                        choice2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                DatabaseReference refchoice2 = FirebaseDatabase.getInstance().getReference().child("Votes")
                                        .child(model.getUid2()).child(model.getPushKey()).child("option2");
                                refchoice2.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue("voted");

                                DatabaseReference choiceVotes = FirebaseDatabase.getInstance().getReference().child("Votes")
                                        .child(model.getUid2()).child(model.getPushKey());

                                final DatabaseReference choice1 = choiceVotes.child("option1");
                                final DatabaseReference choice3 = choiceVotes.child("option3");
                                final DatabaseReference choice4 = choiceVotes.child("option4");
                                final DatabaseReference choice5 = choiceVotes.child("option5");

                                choice1.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                            choice1.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue();
                                        }
                                    }
                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                    }
                                });

                                choice3.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                            choice3.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue();
                                        }
                                    }
                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                    }
                                });

                                choice4.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                            choice4.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue();
                                        }
                                    }
                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                    }
                                });

                                choice5.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                            choice5.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue();
                                        }
                                    }
                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                    }
                                });
                            }
                        });

                        choice3.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                DatabaseReference refchoice3 = FirebaseDatabase.getInstance().getReference().child("Votes")
                                        .child(model.getUid2()).child(model.getPushKey()).child("option3");
                                refchoice3.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue("voted");

                                DatabaseReference choiceVotes = FirebaseDatabase.getInstance().getReference().child("Votes")
                                        .child(model.getUid2()).child(model.getPushKey());

                                final DatabaseReference choice2 = choiceVotes.child("option2");
                                final DatabaseReference choice1 = choiceVotes.child("option1");
                                final DatabaseReference choice4 = choiceVotes.child("option4");
                                final DatabaseReference choice5 = choiceVotes.child("option5");

                                choice2.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                            choice2.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue();
                                        }
                                    }
                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                    }
                                });

                                choice1.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                            choice1.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue();
                                        }
                                    }
                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                    }
                                });

                                choice4.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                            choice4.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue();
                                        }
                                    }
                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                    }
                                });

                                choice5.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                            choice5.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue();
                                        }
                                    }
                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                    }
                                });
                            }
                        });

                        choice4.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                DatabaseReference refchoice4 = FirebaseDatabase.getInstance().getReference().child("Votes")
                                        .child(model.getUid2()).child(model.getPushKey()).child("option4");
                                refchoice4.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue("voted");

                                DatabaseReference choiceVotes = FirebaseDatabase.getInstance().getReference().child("Votes")
                                        .child(model.getUid2()).child(model.getPushKey());

                                final DatabaseReference choice2 = choiceVotes.child("option2");
                                final DatabaseReference choice3 = choiceVotes.child("option3");
                                final DatabaseReference choice1 = choiceVotes.child("option1");
                                final DatabaseReference choice5 = choiceVotes.child("option5");

                                choice2.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                            choice2.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue();
                                        }
                                    }
                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                    }
                                });

                                choice3.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                            choice3.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue();
                                        }
                                    }
                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                    }
                                });

                                choice1.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                            choice1.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue();
                                        }
                                    }
                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                    }
                                });

                                choice5.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                            choice5.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue();
                                        }
                                    }
                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                    }
                                });
                            }
                        });

                        choice5.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                DatabaseReference refchoice5 = FirebaseDatabase.getInstance().getReference().child("Votes")
                                        .child(model.getUid2()).child(model.getPushKey()).child("option5");
                                refchoice5.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue("voted");

                                DatabaseReference choiceVotes = FirebaseDatabase.getInstance().getReference().child("Votes")
                                        .child(model.getUid2()).child(model.getPushKey());

                                final DatabaseReference choice2 = choiceVotes.child("option2");
                                final DatabaseReference choice3 = choiceVotes.child("option3");
                                final DatabaseReference choice4 = choiceVotes.child("option4");
                                final DatabaseReference choice1 = choiceVotes.child("option1");

                                choice2.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                            choice2.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue();
                                        }
                                    }
                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                    }
                                });

                                choice3.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                            choice3.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue();
                                        }
                                    }
                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                    }
                                });

                                choice4.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                            choice4.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue();
                                        }
                                    }
                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                    }
                                });

                                choice1.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                            choice1.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue();
                                        }
                                    }
                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                    }
                                });
                            }
                        });

                    }
                }
            }
        };

        listView.setAdapter(chatsFirebaseListAdapter);

        //listView.setNestedScrollingEnabled(true);
        RelativeLayout noPosts = (RelativeLayout) rootview.findViewById(R.id.relativeLayout7);
        listView.setEmptyView(noPosts);
        setHasOptionsMenu(true);

        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("InstaVote");

        //Toast.makeText(getActivity(), ""+savedInstanceState.getString("name"), Toast.LENGTH_SHORT).show();

        return rootview;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void menuOptions(){
        String [] items = new String[]{"Report Post"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.select_dialog_item, items);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0){
                    Toast.makeText(getActivity(), "test", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.show();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main_tab, menu);
        menu.findItem(R.id.action_pending_request).setVisible(false);
        menu.findItem(R.id.action_new_message).setVisible(false);
        menu.findItem(R.id.action_Mentions).setVisible(false);
        menu.findItem(R.id.add_user).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                startActivity(intent);
                return false;
            }
        });
        menu.findItem(R.id.action_notification).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Toast.makeText(getActivity(), ""+top1, Toast.LENGTH_SHORT).show();
                return false;
            }
        });
        menu.findItem(R.id.action_settings).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent intent = new Intent(getActivity(), Settings.class);
                startActivity(intent);
                return false;
            }
        });
        menu.findItem(R.id.action_Logout).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                Intent intent = new Intent(getActivity(), SignIn.class);
                startActivity(intent);
                FirebaseAuth.getInstance().signOut();
                String refreshedToken = FirebaseInstanceId.getInstance().getToken();
                DatabaseReference send = FirebaseDatabase.getInstance().getReference();
                send.child("Users").child(uid).child("Notification Token").child(refreshedToken).removeValue();
                getActivity().finish();
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }
}
