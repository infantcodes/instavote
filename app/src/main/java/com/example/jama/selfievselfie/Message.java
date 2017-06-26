package com.example.jama.selfievselfie;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jama.selfievselfie.model.Getters;
import com.example.jama.selfievselfie.model.RoundedTransformation;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

/**
 * Created by JAMA on 3/13/2017.
 */

public class Message extends Fragment {

    ListView listView;
    DatabaseReference databaseReference, usernameref;
    String Username;
    private static final int SECOND_MILLIS = 60;
    private static final int MINUTE_MILLIS = 1 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;

    public static Message newInstance() {
        Message fragment = new Message()    ;
        return fragment;
    }

    public Message() {
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootview =  inflater.inflate(R.layout.fragment_message, container, false);
        listView = (ListView) rootview.findViewById(R.id.listView);

        //RECIEVE LAST CHAT MESSAGES SENT
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Chats").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        usernameref = FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("Profile Info");


        FirebaseListAdapter<Getters> chatsFirebaseListAdapter = new FirebaseListAdapter<Getters>(
                getActivity(),
                Getters.class,
                R.layout.search_layout,
                databaseReference
        ) {
            @Override
            protected void populateView(View v, final Getters model, int position) {

                final String key = getRef(position).getKey();

                TextView username = (TextView) v.findViewById(R.id.textViewUsername);
                username.setText(model.getUsername());
                TextView name = (TextView) v.findViewById(R.id.textViewName);
                name.setText(model.getMessage());
                ImageView profileImage = (ImageView) v.findViewById(R.id.imageViewProfile);
                if (model.getProfileImage() == null){
                    username.setText("Unknown User");
                    Picasso.with(getContext()).load(R.drawable.download).transform(new RoundedTransformation(50, 4)).fit().into(profileImage);
                }else {
                    Picasso.with(getContext()).load(model.getProfileImage()).fit().transform(new RoundedTransformation(50, 4)).into(profileImage);
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

                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), MessagingList.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("key", key);
                        bundle.putString("username", model.getUsername());
                        bundle.putString("profileImage", model.getProfileImage());
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                });

                v.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        String [] items = new String[]{"Delete Chat"};
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.select_dialog_item, items);
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == 0){
                                    AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                                    alertDialog.setMessage("Delete Chat?");
                                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "DELETE",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    DatabaseReference deleteChat = FirebaseDatabase.getInstance().getReference();
                                                    deleteChat.child("Chats").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                            .child(key).removeValue();
                                                    DatabaseReference deleteMessages = deleteChat;
                                                    deleteMessages.child("Messages").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                            .child(key).removeValue();
                                                    dialog.dismiss();
                                                    Toast.makeText(getActivity(), "Chat Deleted", Toast.LENGTH_SHORT).show();
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
                        return false;
                    }
                });

            }
        };

        listView.setNestedScrollingEnabled(true);
        RelativeLayout noPosts = (RelativeLayout) rootview.findViewById(R.id.relativeLayout7);
        listView.setEmptyView(noPosts);
        listView.setAdapter(chatsFirebaseListAdapter);

        return rootview;
    }

    private void isNetworkAvailable() {
        ConnectivityManager conMan = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo.State mobile = conMan.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
        NetworkInfo.State wifi = conMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();

        if ((mobile == NetworkInfo.State.CONNECTED || mobile == NetworkInfo.State.CONNECTING)) {
            Toast.makeText(getActivity(), "no connection", Toast.LENGTH_SHORT).show(

            );
        } else if ((wifi == NetworkInfo.State.CONNECTED || wifi == NetworkInfo.State.CONNECTING)) {
            //wifi
            Toast.makeText(getActivity(), "no wifi ", Toast.LENGTH_LONG).show();
        }
    }

}
