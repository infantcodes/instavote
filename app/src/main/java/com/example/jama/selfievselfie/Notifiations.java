package com.example.jama.selfievselfie;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.jama.selfievselfie.model.CircleTransform;
import com.example.jama.selfievselfie.model.Getters;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by JAMA on 4/27/2017.
 */

public class Notifiations extends AppCompatActivity {

    ListView listView;
    DatabaseReference databaseReference;
    String postKey, uid;
    private static final int SECOND_MILLIS = 60;
    private static final int MINUTE_MILLIS = 1 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_message);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Notifications");

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Notification")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        listView = (ListView) findViewById(R.id.listView);

        FirebaseListAdapter<Getters> gettersFirebaseListAdapter = new FirebaseListAdapter<Getters>(
                this,
                Getters.class,
                R.layout.notification_layout,
                databaseReference
        ) {
            @Override
            protected void populateView(View v, final Getters model, int position) {
                final TextView username = (TextView) v.findViewById(R.id.textViewUsername);
                username.setText(model.getUsername());
                TextView notification = (TextView) v.findViewById(R.id.textViewName);
                notification.setText(model.getMessage());
                ImageView profileImage = (ImageView) v.findViewById(R.id.imageViewProfile);
                if (model.getProfileImage() == null){
                    username.setText("Unknown User");
                    Glide.with(Notifiations.this).load(R.drawable.download).bitmapTransform(new CircleTransform(Notifiations.this)).into(profileImage);
                }else {
                    Glide.with(Notifiations.this).load(model.getProfileImage()).transform(new CircleTransform(Notifiations.this)).into(profileImage);
                }

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

                username.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Notifiations.this, UserProfile.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("key", model.getUid());
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                });

                profileImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Notifiations.this, UserProfile.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("key", model.getUid());
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                });

                notification.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (model.getMessage().substring(0, 9).equals("commented")){
                            Intent intent = new Intent(Notifiations.this, ViewPosts.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("pushKey", model.getPushKey());
                            bundle.putString("uid", model.getUid());
                            bundle.putString("type", "Commented");
                            bundle.putString("sharedUid", model.getSharedUid());
                            intent.putExtras(bundle);
                            startActivity(intent);
                        }else if (model.getMessage().substring(0, 5).equals("liked")){
                            Intent intent = new Intent(Notifiations.this, ViewPosts.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("pushKey", model.getPushKey());
                            bundle.putString("uid", model.getUid());
                            bundle.putString("type", "Liked");
                            bundle.putString("sharedUid", model.getSharedUid());
                            intent.putExtras(bundle);
                            startActivity(intent);
                        }else if (model.getMessage().substring(0, 6).equals("shared")){
                            Intent intent = new Intent(Notifiations.this, ViewPosts.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("pushKey", model.getPushKey());
                            bundle.putString("uid", model.getUid());
                            bundle.putString("type", "Shared");
                            bundle.putString("sharedUid", model.getSharedUid());
                            intent.putExtras(bundle);
                            startActivity(intent);
                        }else {
                            Intent intent = new Intent(Notifiations.this, UserProfile.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("key", model.getUid());
                            intent.putExtras(bundle);
                            startActivity(intent);
                        }
                    }
                });
            }
        };
        listView.setAdapter(gettersFirebaseListAdapter);

        RelativeLayout noPosts = (RelativeLayout) findViewById(R.id.relativeLayout7);
        noPosts.setVisibility(View.GONE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_notification, menu);
        menu.findItem(R.id.action_clear).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                AlertDialog alertDialog = new AlertDialog.Builder(Notifiations.this).create();
                alertDialog.setMessage("Clear All Notifications");
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "CLEAR",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.constraint);
                                databaseReference = FirebaseDatabase.getInstance().getReference().child("Notification")
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                databaseReference.removeValue();
                                Snackbar snackbar = Snackbar.make(relativeLayout, "All notifications deleted", Snackbar.LENGTH_LONG);
                                snackbar.show();
                            }
                        });
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "CANCEL",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
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
}
