package com.example.jama.selfievselfie;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateUtils;
import android.view.*;
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

import java.util.HashMap;
import java.util.Map;

/**
 * Created by JAMA on 4/14/2017.
 */

public class PendingRequests extends AppCompatActivity {

    DatabaseReference databaseReference;
    FirebaseListAdapter<Getters> listAdapter;
    ListView listView;
    String postKey, Username, ProfileImage;
    String mAuth, test;
    private static final int SECOND_MILLIS = 60;
    private static final int MINUTE_MILLIS = 1 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_message);

        mAuth = FirebaseAuth.getInstance().getCurrentUser().getUid();
        test = "jama";

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Pending Activities");

        databaseReference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference profileInfo = databaseReference.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("Profile Info");

        profileInfo.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, String> map = (Map)dataSnapshot.getValue();
                ProfileImage = map.get("profileImage");
                Username = map.get("username");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        listView = (ListView) findViewById(R.id.listView);

        DatabaseReference retrivePendingRequests = databaseReference.child("Pending Requests").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        listAdapter = new FirebaseListAdapter<Getters>(
                this,
                Getters.class,
                R.layout.pending_request_layout,
                retrivePendingRequests
        ) {
            @Override
            protected void populateView(android.view.View v, final Getters model, int position) {

                TextView username = (TextView) v.findViewById(R.id.textViewUsername);
                username.setText(model.getUsername());
                TextView status = (TextView) v.findViewById(R.id.textViewStatus);
                status.setText(model.getStatus());
                ImageView profileImage = (ImageView) v.findViewById(R.id.imageViewProfile);
                ImageView image1 = (ImageView) v.findViewById(R.id.imageViewimage1);
                Picasso.with(PendingRequests.this).load(model.getImage1()).transform(new RoundedTransformation(50, 4)).centerCrop().fit().into(image1);
                image1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent view = new Intent(PendingRequests.this, com.example.jama.selfievselfie.View.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("image", model.getImage1());
                        view.putExtras(bundle);
                        startActivity(view);
                    }
                });
                ImageView image2 = (ImageView) v.findViewById(R.id.imageViewimage2);
                Picasso.with(PendingRequests.this).load(model.getProfileImage()).transform(new RoundedTransformation(50, 4)).centerCrop().fit().into(profileImage);
                Button post = (Button) v.findViewById(R.id.buttonPost);
                final Button sendReminder = (Button) v.findViewById(R.id.buttonSendReminder);

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

                final Button delete = (Button) v.findViewById(R.id.buttonDelete);
                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog alertDialog = new AlertDialog.Builder(PendingRequests.this).create();
                        alertDialog.setMessage("Delete Request?");
                        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "DELETE",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        final DatabaseReference deletePendingRequest = FirebaseDatabase.getInstance().getReference().child("Pending Requests");
                                        final DatabaseReference deleteRequest = FirebaseDatabase.getInstance().getReference().child("Requests");
                                        StorageReference deleteImage1 = FirebaseStorage.getInstance().getReferenceFromUrl(model.getImage1());
                                        deleteImage1.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                if (!(model.getImage2() == null)){
                                                    StorageReference deleteImage2 = FirebaseStorage.getInstance().getReferenceFromUrl(model.getImage2());
                                                    deleteImage2.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                        }
                                                    });
                                                }
                                                deletePendingRequest.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(model.getPushKey()).removeValue();
                                                deleteRequest.child(model.getUid()).child(model.getPushKey()).removeValue();
                                                Toast.makeText(PendingRequests.this, "Request Deleted", Toast.LENGTH_SHORT).show();
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
                });

                //for (; position < getCount(); position++){
                    if (model.getStatus().equals("Request has been accepted")){
                        //post.setText("POST");
                        sendReminder.setEnabled(false);
                        Picasso.with(PendingRequests.this).load(model.getImage2()).transform(new RoundedTransformation(50, 4)).centerCrop().fit().into(image2);
                        image2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent view = new Intent(PendingRequests.this, com.example.jama.selfievselfie.View.class);
                                Bundle bundle = new Bundle();
                                bundle.putString("image", model.getImage2());
                                view.putExtras(bundle);
                                startActivity(view);
                            }
                        });
                        post.setOnClickListener(new android.view.View.OnClickListener() {
                            @Override
                            public void onClick(android.view.View v) {
                                Intent intent = new Intent(PendingRequests.this, ViewPendingPosts.class);
                                Bundle bundle = new Bundle();
                                bundle.putString("username", model.getUsername());
                                bundle.putString("username2", model.getUsername2());
                                bundle.putString("profileImage", model.getProfileImage());
                                bundle.putString("profileImage2", model.getProfileImage2());
                                bundle.putString("image1", model.getImage1());
                                bundle.putString("image2", model.getImage2());
                                bundle.putString("uid", model.getUid());
                                bundle.putString("uid2", FirebaseAuth.getInstance().getCurrentUser().getUid());
                                bundle.putString("pushKey", model.getPushKey());
                                intent.putExtras(bundle);
                                startActivity(intent);
                            }
                        });
                    }else {
                        image2.setVisibility(android.view.View.GONE);
                        post.setEnabled(false);
                        sendReminder.setEnabled(true);
                        sendReminder.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                AlertDialog alertDialog = new AlertDialog.Builder(PendingRequests.this).create();
                                alertDialog.setMessage("Send Reminder?");
                                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "SEND",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                DatabaseReference sendReminderRef = FirebaseDatabase.getInstance().getReference().child("Notification")
                                                        .child(model.getUid());
                                                Map map = new HashMap();
                                                map.put("date", System.currentTimeMillis()/1000);
                                                map.put("message", "sent a request reminder");
                                                map.put("profileImage", ProfileImage);
                                                map.put("username", Username);
                                                map.put("uid", FirebaseAuth.getInstance().getCurrentUser().getUid().toString());
                                                sendReminderRef.push().setValue(map);
                                                Toast.makeText(PendingRequests.this, "Reminder Sent", Toast.LENGTH_SHORT).show();
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
                        });
                    }
                }
            //}
        };
        listView.setAdapter(listAdapter);

        RelativeLayout noPosts = (RelativeLayout) findViewById(R.id.relativeLayout7);
        TextView textView = (TextView) findViewById(R.id.textView9);
        textView.setText("You Have No Pending Requests");
        listView.setEmptyView(noPosts);

    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}
