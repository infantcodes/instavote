package com.example.jama.selfievselfie;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jama.selfievselfie.model.Getters;
import com.example.jama.selfievselfie.model.RoundedTransformation;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by JAMA on 3/13/2017.
 */

public class Profile extends Fragment {

    ListView listView;
    Button editProfile;
    ImageView imageViewProfile;
    TextView txtUsername, txtBio, txtPosts, txtFollowing, txtFollowers, txtNames;
    DatabaseReference databaseReference, votereference;
    ProgressDialog progressDialog;
    String Names;
    String Username;
    String Bio;
    String ProfileImage;
    long Date;
    FirebaseListAdapter<Getters> postsFirebaseListAdapter;
    boolean like;
    private AdView mAdView;
    long totalVotes1, totalVotes2;
    private static final int SECOND_MILLIS = 60;
    private static final int MINUTE_MILLIS = 1 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;

    public static Profile newInstance() {
        Profile fragment = new Profile();
        return fragment;
    }

    public Profile () {
        postsFirebaseListAdapter = null;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootview =  inflater.inflate(R.layout.activity_profile, container, false);

        View header = View.inflate(getActivity(), R.layout.profile_detail_layout, null);

        AdView adView = new AdView(getActivity());
        adView.setAdSize(AdSize.BANNER);
        adView.setAdUnitId("ca-app-pub-3940256099942544/6300978111");

        mAdView = (AdView) rootview.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        Date  = System.currentTimeMillis()/1000;

        votereference = FirebaseDatabase.getInstance().getReference();

        //INITIALIZING
        listView = (ListView) rootview.findViewById(R.id.listView);
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
                Intent intent = new Intent(getActivity(), Followers.class);
                startActivity(intent);
            }
        });

        //ON CLICK TO OPEN FOLLOWING ACTIVITY
        txtFollowing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Following.class);
                startActivity(intent);
            }
        });

        //PROGRESS DIALOG
        progressDialog = new ProgressDialog(getActivity());
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
                Picasso.with(getContext()).load(ProfileImage).fit().transform(new RoundedTransformation(50, 4)).into(imageViewProfile);
                Username = map.get("username");
                txtUsername.setText(Username.toString());
                Bio = map.get("bio");
                txtBio.setText(Bio.toString());
                Names = map.get("name");
                txtNames.setText(Names.toString());
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
                Intent editProfile = new Intent(getActivity(), EditProfile.class);
                Bundle bundle = new Bundle();
                bundle.putString("key", FirebaseAuth.getInstance().getCurrentUser().getUid());
                editProfile.putExtras(bundle);
                startActivity(editProfile);
            }
        });

        DatabaseReference profilePosts = databaseReference = FirebaseDatabase.getInstance().getReference().child("Posts").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        postsFirebaseListAdapter = new FirebaseListAdapter<Getters>(
                getActivity(),
                Getters.class,
                R.layout.post_layout,
                profilePosts
        ) {
            @Override
            protected void populateView(final View v, final Getters model, int position) {

                ImageView profileImage1 = (ImageView) v.findViewById(R.id.imageViewProfileImage1);
                Picasso.with(getContext()).load(model.getProfileImage2()).transform(new RoundedTransformation(50, 4)).fit().into(profileImage1);
                ImageView profileImage3 = (ImageView) v.findViewById(R.id.imageViewProfileImage3);
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
                Picasso.with(getContext()).load(model.getProfileImage()).transform(new RoundedTransformation(50, 4)).fit().into(profileImage3);
                ImageView imageView1 = (ImageView) v.findViewById(R.id.imageViewImage1);
                Picasso.with(getContext()).load(model.getImage1()).transform(new RoundedTransformation(50, 4)).fit().into(imageView1);
                ImageView imageView2 = (ImageView) v.findViewById(R.id.imageViewImage2);
                Picasso.with(getContext()).load(model.getImage2()).transform(new RoundedTransformation(50, 4)).fit().into(imageView2);
                TextView username1 = (TextView) v.findViewById(R.id.textViewUsername1);
                username1.setText(model.getUsername2());
                TextView username2 = (TextView) v.findViewById(R.id.textViewUsername2);
                username2.setText(model.getUsername());

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

                final ImageView imageViewLike = (ImageView) v.findViewById(R.id.imageViewLike);
                final ImageView imageViewComment = (ImageView) v.findViewById(R.id.imageViewComment);
                final DatabaseReference Comments = FirebaseDatabase.getInstance().getReference().child("Comments");
                final ImageView imageViewOptions = (ImageView) v.findViewById(R.id.imageViewOptions);

                TextView caption = (TextView) v.findViewById(R.id.textViewUserCaption);
                //for (; position<getCount(); position++) {
                    if (model.getCaption() == null) {
                        caption.setVisibility(View.GONE);
                    } else {
                        caption.setText(model.getUsername2() + ": " + model.getCaption());
                    }
                //}

                Comments.child(model.getUid2()).child(model.getPushKey()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String commentCount = String.valueOf(dataSnapshot.getChildrenCount());
                        TextView comments = (TextView) v.findViewById(R.id.textViewAllComments);
                        comments.setText("View All "+commentCount+" Comments");
                        comments.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent likes = new Intent(getActivity(), com.example.jama.selfievselfie.Comments.class);
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
                        bundle.putString("key", model.getPushKey());
                        bundle.putString("uid", model.getUid2());
                        comment.putExtras(bundle);
                        startActivity(comment);
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

                final DatabaseReference Likes = FirebaseDatabase.getInstance().getReference().child("Likes");

                //NUMBER OF LIKES
                Likes.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(model.getPushKey()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String likeCount = String.valueOf(dataSnapshot.getChildrenCount());
                        TextView likes = (TextView) v.findViewById(R.id.textViewLikes);
                        likes.setText(likeCount+" Likes");
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
                            map.put("uid", FirebaseAuth.getInstance().getCurrentUser().getUid());
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

                final String deletePostKey = getRef(position).getKey();

                imageViewOptions.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String [] items = new String[]{"Edit", "Delete"};
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.select_dialog_item, items);
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == 0){
                                    Intent intent = new Intent(getActivity(), EditPost.class);
                                    Bundle bundle = new Bundle();
                                    bundle.putString("username1", model.getUsername());
                                    bundle.putString("username2", model.getUsername2());
                                    bundle.putString("image1", model.getImage1());
                                    bundle.putString("image2", model.getImage2());
                                    bundle.putString("profileImage1", model.getProfileImage());
                                    bundle.putString("profileImage2", model.getProfileImage2());
                                    bundle.putLong("date", model.getDate());
                                    bundle.putString("caption", model.getCaption());
                                    bundle.putString("pushKey", model.getPushKey());
                                    intent.putExtras(bundle);
                                    startActivity(intent);
                                }else {
                                    AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                                    alertDialog.setMessage("Delete Post?");
                                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "DELETE",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    final DatabaseReference deletePost = FirebaseDatabase.getInstance().getReference().child("Posts");
                                                    final DatabaseReference deleteLikes = FirebaseDatabase.getInstance().getReference().child("Likes");
                                                    final DatabaseReference deleteComments = FirebaseDatabase.getInstance().getReference().child("Comments");
                                                    final StorageReference deleteImage1 = FirebaseStorage.getInstance().getReferenceFromUrl(model.getImage1());
                                                    deleteImage1.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            StorageReference deleteImage2 = FirebaseStorage.getInstance().getReferenceFromUrl(model.getImage2());
                                                            deleteImage2.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    deletePost.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(model.getPushKey()).removeValue();
                                                                    deleteLikes.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(model.getPushKey()).removeValue();
                                                                    deleteComments.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(model.getPushKey()).removeValue();
                                                                    Toast.makeText(getActivity(), "Post Deleted", Toast.LENGTH_SHORT).show();
                                                                }
                                                            });
                                                        }
                                                    });
                                                    dialog.dismiss();
                                                }
                                            });
                                    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "CANCEL",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                }
                                            });
                                    alertDialog.show();
                                }
                            }
                        });
                        builder.show();
                    }
                });

                final ImageView imageViewShare = (ImageView) v.findViewById(R.id.imageViewShare);
                imageViewShare.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), Share.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("key", model.getPushKey());
                        bundle.putString("uid", model.getUid());
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
        };

        DatabaseReference profileNoPosts = databaseReference = FirebaseDatabase.getInstance().getReference().child("Posts");
        final View noPostFooter = View.inflate(getActivity(), R.layout.no_post_layout, null);

        profileNoPosts.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                    //Toast.makeText(getActivity(), "no posts", Toast.LENGTH_SHORT).show();
                    listView.addHeaderView(noPostFooter, null, false);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        listView.setAdapter(postsFirebaseListAdapter);
        listView.setNestedScrollingEnabled(true);
        RelativeLayout noPosts = (RelativeLayout) rootview.findViewById(R.id.relativeLayout7);
        //listView.setEmptyView(noPosts);
        noPosts.setVisibility(View.GONE);
        return rootview;
    }
}
