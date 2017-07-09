package com.example.jama.selfievselfie;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.util.DiffUtil;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jama.selfievselfie.model.Getters;
import com.example.jama.selfievselfie.model.RoundedTransformation;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.NativeExpressAdView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
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
                LinearLayout linearLayoutSinglePost = (LinearLayout) v.findViewById(R.id.linearLayoutSinglePost);
                LinearLayout linearLayoutAds = (LinearLayout) v.findViewById(R.id.adLayout);

                if (position % 3 == 0){
                    linearLayoutAds.setVisibility(View.VISIBLE);

                    NativeExpressAdView mAdView = (NativeExpressAdView) v.findViewById(R.id.adView);
                    AdRequest adRequest = new AdRequest.Builder()
                            .addTestDevice("0EDB54D55A01A36E44405E501E1E77EA").build();
                    mAdView.loadAd(adRequest);

                    /*mAdView.setAdListener(new AdListener() {
                        @Override
                        public void onAdClosed() {
                            super.onAdClosed();
                        }

                        @Override
                        public void onAdFailedToLoad(int i) {
                            Toast.makeText(getActivity(), "Ad Failed To Load", Toast.LENGTH_SHORT).show();
                            super.onAdFailedToLoad(i);
                        }

                        @Override
                        public void onAdLeftApplication() {
                            super.onAdLeftApplication();
                        }

                        @Override
                        public void onAdOpened() {
                            super.onAdOpened();
                        }

                        @Override
                        public void onAdLoaded() {
                            Toast.makeText(getActivity(), "Ad Loaded", Toast.LENGTH_SHORT).show();
                            super.onAdLoaded();
                        }
                    });*/
                }else {
                    linearLayoutAds.setVisibility(View.GONE);
                }

                if (model.getImage2() != null){
                    linearLayoutPost.setVisibility(View.VISIBLE);
                    linearLayoutSinglePost.setVisibility(View.GONE);
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
                    Picasso.with(getContext()).load(model.getProfileImage2()).transform(new RoundedTransformation(50, 4)).fit()
                            .networkPolicy(NetworkPolicy.OFFLINE).into(profileImage1, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            Picasso.with(getContext()).load(model.getProfileImage2()).transform(new RoundedTransformation(50, 4)).fit().into(profileImage1);
                        }
                    });
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
                    Picasso.with(getContext()).load(model.getProfileImage()).transform(new RoundedTransformation(50, 4)).fit()
                            .networkPolicy(NetworkPolicy.OFFLINE).into(profileImage3, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            Picasso.with(getContext()).load(model.getProfileImage()).transform(new RoundedTransformation(50, 4)).fit().into(profileImage3);
                        }
                    });
                    final ImageView imageView1 = (ImageView) v.findViewById(R.id.imageViewImage1);
                    Picasso.with(getContext()).load(model.getImage1()).transform(new RoundedTransformation(50, 4)).fit()
                            .networkPolicy(NetworkPolicy.OFFLINE).into(imageView1, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            Picasso.with(getContext()).load(model.getImage1()).transform(new RoundedTransformation(50, 4)).fit().into(imageView1);
                        }
                    });
                    final ImageView imageView2 = (ImageView) v.findViewById(R.id.imageViewImage2);
                    Picasso.with(getContext()).load(model.getImage2()).transform(new RoundedTransformation(50, 4)).fit()
                            .networkPolicy(NetworkPolicy.OFFLINE).into(imageView2, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            Picasso.with(getContext()).load(model.getImage2()).transform(new RoundedTransformation(50, 4)).fit().into(imageView2);
                        }
                    });
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

                    // }
                }else {
                    linearLayoutPost.setVisibility(View.GONE);
                    linearLayoutSinglePost.setVisibility(View.VISIBLE);
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
                    Picasso.with(getContext()).load(model.getProfileImage2()).transform(new RoundedTransformation(50, 4)).fit()
                            .networkPolicy(NetworkPolicy.OFFLINE).into(profileImage1, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            Picasso.with(getContext()).load(model.getProfileImage2()).transform(new RoundedTransformation(50, 4)).fit().into(profileImage1);
                        }
                    });
                    final ImageView imageView1 = (ImageView) v.findViewById(R.id.imageViewSinglePostImage);
                    Picasso.with(getContext()).load(model.getImage1()).transform(new RoundedTransformation(50, 4)).fit()
                            .networkPolicy(NetworkPolicy.OFFLINE).into(imageView1, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            Picasso.with(getContext()).load(model.getImage1()).transform(new RoundedTransformation(50, 4)).fit().into(imageView1);
                        }
                    });
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
