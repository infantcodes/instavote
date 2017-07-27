package com.example.jama.selfievselfie;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

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
    String Names;
    String Username;
    String Bio;
    String ProfileImage;
    FirebaseListAdapter<Getters> postsFirebaseListAdapter;
    boolean like;
    long totalVotes1, totalVotes2;
    private static final int SECOND_MILLIS = 60;
    private static final int MINUTE_MILLIS = 1 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;
    int top1, index1;

    public static Profile newInstance() {
        Profile fragment = new Profile();
        return fragment;
    }

    public Profile () {
        postsFirebaseListAdapter = null;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootview =  inflater.inflate(R.layout.activity_profile, container, false);

        View header = View.inflate(getActivity(), R.layout.profile_detail_layout, null);

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
                Glide.with(getContext()).load(ProfileImage).bitmapTransform(new CircleTransform(getActivity()))
                        .diskCacheStrategy(DiskCacheStrategy.RESULT).into(imageViewProfile);

                Username = map.get("username");
                txtUsername.setText(Username.toString());
                Bio = map.get("bio");
                txtBio.setText(Bio.toString());
                Names = map.get("name");
                txtNames.setText(Names.toString());
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

                LinearLayout linearLayoutPost = (LinearLayout) v.findViewById(R.id.linearLayoutPost);
                LinearLayout linearLayoutSinglePost = (LinearLayout) v.findViewById(R.id.linearLayoutSinglePost);
                LinearLayout linearLayoutTextOnly = (LinearLayout) v.findViewById(R.id.linearLayoutTextOnly);

                if (model.getUid() != null){
                    linearLayoutPost.setVisibility(View.VISIBLE);
                    linearLayoutSinglePost.setVisibility(View.GONE);
                    linearLayoutTextOnly.setVisibility(View.GONE);
                    final ImageView profileImage1 = (ImageView) v.findViewById(R.id.imageViewProfileImage1);
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
                                imageViewLike.setImageResource(R.drawable.like_red);
                            }else {
                                imageViewLike.setImageResource(R.drawable.ic_favorite_black_24dp);
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
                                map.put("date", System.currentTimeMillis()/1000);
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
                                                        final DatabaseReference deleteAllPost = FirebaseDatabase.getInstance().getReference().child("All Posts");
                                                        final DatabaseReference deleteComments = FirebaseDatabase.getInstance().getReference().child("Comments");
                                                        final DatabaseReference deleteVotes = FirebaseDatabase.getInstance().getReference().child("Votes");
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
                                                                        deleteVotes.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(model.getPushKey()).removeValue();
                                                                        deleteAllPost.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(model.getPushKey()).removeValue();
                                                                        Toast.makeText(getActivity(), "Post Deleted", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                });
                                                            }
                                                        }).addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Toast.makeText(getActivity(), "Discard not successfull, check connection", Toast.LENGTH_SHORT).show();
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
                                vote1.setBackgroundResource(R.drawable.rounded_corner_white);
                                vote1.setTextColor(Color.parseColor("#E91E63"));
                            }else {
                                vote1.setBackgroundResource(R.drawable.rounded_corner_pink);
                                vote1.setTextColor(Color.parseColor("#ffffff"));
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
                                vote2.setBackgroundResource(R.drawable.rounded_corner_white);
                                vote2.setTextColor(Color.parseColor("#E91E63"));
                            }else {
                                vote2.setBackgroundResource(R.drawable.rounded_corner_pink);
                                vote2.setTextColor(Color.parseColor("#ffffff"));
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
                }else {
                    if (model.getChoice1() == null){
                        linearLayoutPost.setVisibility(View.GONE);
                        linearLayoutSinglePost.setVisibility(View.VISIBLE);
                        linearLayoutTextOnly.setVisibility(View.GONE);
                        final ImageView imageViewUsername = (ImageView) v.findViewById(R.id.imageViewProfileImageSinglePost);
                        final ImageView imageViewImage = (ImageView) v.findViewById(R.id.imageViewSinglePostImage);
                        ImageView imageViewOptionMenu = (ImageView) v.findViewById(R.id.imageViewOptionsSinglePost);
                        final ImageView imageViewLikes = (ImageView) v.findViewById(R.id.imageViewLikeSinglePost);
                        ImageView imageViewComments = (ImageView) v.findViewById(R.id.imageViewCommentSinglePost);
                        //ImageView imageViewShare = (ImageView) v.findViewById(R.id.imageViewShareSinglePost);
                        TextView textViewUsername = (TextView) v.findViewById(R.id.textViewUsernameSinglePost);
                        //TextView textViewLikes = (TextView) v.findViewById(R.id.textViewSinglePostLikes);
                        final TextView textViewComments = (TextView) v.findViewById(R.id.textViewSimglePostComments);
                        TextView textViewCaption = (TextView) v.findViewById(R.id.textViewSinglePostCaption);
                        TextView textViewDate = (TextView) v.findViewById(R.id.textViewSinglePostDate);
                        TextView textViewTotalVotes = (TextView) v.findViewById(R.id.textViewSinglePostTotalVotes);

                        if (model.getCaption().equals("")){
                            textViewCaption.setVisibility(View.GONE);
                        }else {
                            textViewCaption.setVisibility(View.VISIBLE);
                            textViewCaption.setText(model.getUsername2()+": "+model.getCaption());
                        }

                        Glide.with(getContext()).load(model.getProfileImage2()).bitmapTransform(new CircleTransform(getActivity()))
                                .diskCacheStrategy(DiskCacheStrategy.RESULT).into(imageViewUsername);

                        Glide.with(getContext()).load(model.getImage1()).diskCacheStrategy(DiskCacheStrategy.RESULT).into(imageViewImage);

                        textViewUsername.setText(model.getUsername2());
                        textViewDate.setText(model.getDate()+"");

                        final DatabaseReference Comments = FirebaseDatabase.getInstance().getReference().child("Comments");
                        Comments.child(model.getUid2()).child(model.getPushKey()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                String commentCount = String.valueOf(dataSnapshot.getChildrenCount());
                                textViewComments.setText("View All "+commentCount+" Comments");
                                textViewComments.setOnClickListener(new View.OnClickListener() {
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

                        final DatabaseReference Likes = FirebaseDatabase.getInstance().getReference().child("Likes");

                        //NUMBER OF LIKES
                        Likes.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(model.getPushKey()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                String likeCount = String.valueOf(dataSnapshot.getChildrenCount());
                                TextView likes = (TextView) v.findViewById(R.id.textViewSinglePostLikes);
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
                                    imageViewLikes.setImageResource(R.drawable.like_red);
                                }else {
                                    imageViewLikes.setImageResource(R.drawable.ic_favorite_black_24dp);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                        imageViewLikes.setOnClickListener(new View.OnClickListener() {
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
                                    map.put("date", System.currentTimeMillis()/1000);
                                    map.put("name", Names);
                                    Likes.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(model.getPushKey())
                                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(map);
                                    like = true;
                                }
                            }
                        });

                        final ImageView imageViewShare = (ImageView) v.findViewById(R.id.imageViewShareSinglePost);
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



                        imageViewOptionMenu.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String [] items = new String[]{"Edit", "Delete"};
                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.select_dialog_item, items);
                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (which == 0){
                                            Intent intent = new Intent(getActivity(), EditSinglePost.class);
                                            Bundle bundle = new Bundle();
                                            bundle.putString("image1", model.getImage1());
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
                                                            final DatabaseReference deleteAllPost = FirebaseDatabase.getInstance().getReference().child("All Posts");
                                                            final DatabaseReference deleteLikes = FirebaseDatabase.getInstance().getReference().child("Likes");
                                                            final DatabaseReference deleteComments = FirebaseDatabase.getInstance().getReference().child("Comments");
                                                            final DatabaseReference deleteVotes = FirebaseDatabase.getInstance().getReference().child("Votes");
                                                            final StorageReference deleteImage1 = FirebaseStorage.getInstance().getReferenceFromUrl(model.getImage1());

                                                            if (model.getImage2() == null){
                                                                deleteImage1.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {
                                                                        deletePost.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(model.getPushKey()).removeValue();
                                                                        deleteAllPost.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(model.getPushKey()).removeValue();
                                                                        deleteLikes.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(model.getPushKey()).removeValue();
                                                                        deleteComments.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(model.getPushKey()).removeValue();
                                                                        deleteVotes.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(model.getPushKey()).removeValue();
                                                                        Toast.makeText(getActivity(), "Post Deleted", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                });
                                                            }else {
                                                                deleteImage1.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {
                                                                        StorageReference deleteImage2 = FirebaseStorage.getInstance().getReferenceFromUrl(model.getImage2());
                                                                        deleteImage2.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                            @Override
                                                                            public void onSuccess(Void aVoid) {
                                                                                deletePost.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(model.getPushKey()).removeValue();
                                                                                deleteAllPost.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(model.getPushKey()).removeValue();
                                                                                deleteLikes.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(model.getPushKey()).removeValue();
                                                                                deleteComments.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(model.getPushKey()).removeValue();
                                                                                deleteVotes.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(model.getPushKey()).removeValue();
                                                                                Toast.makeText(getActivity(), "Post Deleted", Toast.LENGTH_SHORT).show();
                                                                            }
                                                                        });
                                                                    }
                                                                });
                                                            }
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


                        //VOTING ACTIVITY STARTS FROM HERE

                        DatabaseReference vote1Numbers = votereference.child("Votes").child(model.getUid2()).child(model.getPushKey()).child("Votes 1");
                        DatabaseReference vote2Numbers = votereference.child("Votes").child(model.getUid2()).child(model.getPushKey()).child("Votes 2");

                        final Button vote1 = (Button) v.findViewById(R.id.buttonSinglePost1);
                        final Button vote2 = (Button) v.findViewById(R.id.buttonSinglePost2);
                        final TextView totalVotes = (TextView) v.findViewById(R.id.textViewSinglePostTotalVotes);

                        vote1Numbers.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                    vote1.setBackgroundResource(R.drawable.rounded_corner_white);
                                    vote1.setTextColor(Color.parseColor("#E91E63"));
                                }else {
                                    vote1.setBackgroundResource(R.drawable.rounded_corner_pink);
                                    vote1.setTextColor(Color.parseColor("#ffffff"));
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
                                    vote2.setBackgroundResource(R.drawable.rounded_corner_white);
                                    vote2.setTextColor(Color.parseColor("#E91E63"));
                                }else {
                                    vote2.setBackgroundResource(R.drawable.rounded_corner_pink);
                                    vote2.setTextColor(Color.parseColor("#ffffff"));
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
                                    map.put("uid", FirebaseAuth.getInstance().getCurrentUser().getUid());
                                    map.put("pushKey", model.getPushKey());
                                    map.put("username", Username);
                                    map.put("profileImage", ProfileImage);
                                    map.put("date", System.currentTimeMillis()/1000);
                                    map.put("name", Names);

                                    Likes.child(model.getUid2()).child(model.getPushKey()).child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                            .setValue(map);
                                    like = true;

                                    if (!model.getUid2().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                        Map map1 = new HashMap();
                                        map1.put("username", Username);
                                        map1.put("message", "liked your post");
                                        map1.put("profileImage", ProfileImage);
                                        map1.put("date", System.currentTimeMillis()/1000);
                                        map1.put("pushKey", model.getPushKey());
                                        map1.put("uid", FirebaseAuth.getInstance().getCurrentUser().getUid());
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
                                String [] items = new String[]{"Edit", "Delete"};
                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.select_dialog_item, items);
                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (which == 0){
                                            //TODO edit for text only
                                        }else {
                                            AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                                            alertDialog.setMessage("Delete Post?");
                                            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "DELETE",
                                                    new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            final DatabaseReference deletePost = FirebaseDatabase.getInstance().getReference().child("Posts");
                                                            final DatabaseReference deleteAllPost = FirebaseDatabase.getInstance().getReference().child("All Posts");
                                                            final DatabaseReference deleteLikes = FirebaseDatabase.getInstance().getReference().child("Likes");
                                                            final DatabaseReference deleteComments = FirebaseDatabase.getInstance().getReference().child("Comments");
                                                            final DatabaseReference deleteVotes = FirebaseDatabase.getInstance().getReference().child("Votes");

                                                            deletePost.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(model.getPushKey()).removeValue();
                                                            deleteAllPost.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(model.getPushKey()).removeValue();
                                                            deleteLikes.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(model.getPushKey()).removeValue();
                                                            deleteComments.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(model.getPushKey()).removeValue();
                                                            deleteVotes.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(model.getPushKey()).removeValue();
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

                        DatabaseReference choiceRef1 = FirebaseDatabase.getInstance().getReference().child("Votes").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .child(model.getPushKey()).child("option1");
                        DatabaseReference choiceRef2 = FirebaseDatabase.getInstance().getReference().child("Votes").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .child(model.getPushKey()).child("option2");
                        DatabaseReference choiceRef3 = FirebaseDatabase.getInstance().getReference().child("Votes").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .child(model.getPushKey()).child("option3");
                        DatabaseReference choiceRef4 = FirebaseDatabase.getInstance().getReference().child("Votes").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .child(model.getPushKey()).child("option4");
                        DatabaseReference choiceRef5 = FirebaseDatabase.getInstance().getReference().child("Votes").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
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
        RelativeLayout noPosts = (RelativeLayout) rootview.findViewById(R.id.relativeLayout7);
        //listView.setEmptyView(noPosts);
        noPosts.setVisibility(View.GONE);
        setHasOptionsMenu(true);

        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("InstaVote");

        return rootview;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main_tab, menu);
        menu.findItem(R.id.action_pending_request).setVisible(false);
        menu.findItem(R.id.add_user).setVisible(false);
        menu.findItem(R.id.action_new_message).setVisible(false);
        menu.findItem(R.id.action_Mentions).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent intent = new Intent(getActivity(), Mentions.class);
                startActivity(intent);
                return false;
            }
        });
        menu.findItem(R.id.action_notification).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent intent = new Intent(getActivity(), Notifiations.class);
                startActivity(intent);
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
