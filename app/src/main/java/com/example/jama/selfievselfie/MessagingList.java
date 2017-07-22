package com.example.jama.selfievselfie;

import android.annotation.TargetApi;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.jama.selfievselfie.model.CircleTransform;
import com.example.jama.selfievselfie.model.Getters;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class MessagingList extends AppCompatActivity {

    FloatingActionButton floatingActionButton;
    EditText editTextMessage;
    ListView listView;
    String postKey, OtherUsername, Username, OtherProfileImage, ProfileImage;
    DatabaseReference databaseReference;
    StorageReference storageReference;
    FirebaseListAdapter<Getters> chatsFirebaseListAdapter;
    boolean scroll = true;
    String Names;
    CardView cardView;
    private Uri downloadUri, uri;
    private static final int CAMERA_REQUEST_CODE = 2;
    private static final int SECOND_MILLIS = 60;
    private static final int MINUTE_MILLIS = 1 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging_list);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle bundle = getIntent().getExtras();
        postKey = bundle.getString("key");
        final String usernameDisplay = bundle.getString("username");

        databaseReference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();

        final DatabaseReference deteleMeassage = databaseReference.child("Messages").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(postKey);

        DatabaseReference getProfileInfo = databaseReference.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("Profile Info");

        DatabaseReference getOtherProfileInfo = databaseReference.child("Users").child(postKey)
                .child("Profile Info");

        getProfileInfo.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, String> map = (Map) dataSnapshot.getValue();
                Username = map.get("username");
                ProfileImage = map.get("profileImage");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        getOtherProfileInfo.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, String> map = (Map) dataSnapshot.getValue();
                OtherUsername = map.get("username");
                OtherProfileImage = map.get("profileImage");
                Names = map.get("name");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        getSupportActionBar().setTitle(usernameDisplay);

        final DatabaseReference readMessages = databaseReference.child("Messages").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(postKey);

        readMessages.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                scroll = true;
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        editTextMessage = (EditText) findViewById(R.id.editTextMessage);
        floatingActionButton = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        listView = (ListView) findViewById(R.id.listView);

        editTextMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scroll = true;
            }
        });

        chatsFirebaseListAdapter = new FirebaseListAdapter<Getters>(
                this,
                Getters.class,
                R.layout.messaging_layout,
                readMessages
        ) {
            @Override
            protected void populateView(final View v, final Getters model, final int positions) {

                final  String messageKey = getRef(positions).getKey();


                if (!model.getUsername().equals(usernameDisplay)){

                    TextView username = (TextView) v.findViewById(R.id.textViewUsername1);
                    username.setText(model.getUsername());
                    final TextView message = (TextView) v.findViewById(R.id.textViewMessage1);
                    message.setText(model.getMessage());
                    //TextView date = (TextView) v.findViewById(R.id.textViewDate1);
                    ImageView image = (ImageView) v.findViewById(R.id.imageViewImage1);
                    if (!(model.getImage1() == null)){
                        image.setVisibility(View.VISIBLE);
                        Glide.with(MessagingList.this).load(model.getImage1()).centerCrop().into(image);
                    }
                    username.setText("You");

                    //TIME*********************************
                    TextView date = (TextView) v.findViewById(R.id.textViewDate1);
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

                    RelativeLayout owner = (RelativeLayout) v.findViewById(R.id.relativeLayoutOwner);
                    RelativeLayout other = (RelativeLayout) v.findViewById(R.id.relativeLayoutOther);
                    owner.setVisibility(View.VISIBLE);
                    other.setVisibility(View.GONE);
                    //cardView.setCardBackgroundColor(R.color.colorPrimaryDark);
                }else {

                    TextView username = (TextView) v.findViewById(R.id.textViewUsername);
                    username.setText(model.getUsername());
                    final TextView message = (TextView) v.findViewById(R.id.textViewMessage);
                    message.setText(model.getMessage());
                    ImageView profileImage = (ImageView) v.findViewById(R.id.imageViewProfile);
                    if (model.getProfileImage() == null) {
                        Glide.with(MessagingList.this).load(R.drawable.download).bitmapTransform(new CircleTransform(MessagingList.this)).into(profileImage);
                    } else {
                        Glide.with(MessagingList.this).load(model.getProfileImage()).bitmapTransform(new CircleTransform(MessagingList.this)).into(profileImage);
                    }
                    ImageView image = (ImageView) v.findViewById(R.id.imageViewImage);
                    if (!(model.getImage1() == null)){
                        image.setVisibility(View.VISIBLE);
                        Glide.with(MessagingList.this).load(model.getImage1()).centerCrop().into(image);
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

                    RelativeLayout owner = (RelativeLayout) v.findViewById(R.id.relativeLayoutOwner);
                    RelativeLayout other = (RelativeLayout) v.findViewById(R.id.relativeLayoutOther);
                    owner.setVisibility(View.GONE);
                    other.setVisibility(View.VISIBLE);
                }

                if (scroll){
                    listView.smoothScrollToPosition(chatsFirebaseListAdapter.getCount() - 1);
                    scroll = false;
                }else {
                    scroll = false;
                }

                v.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        String [] items = new String[]{"Copy", "Delete Message"};
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(MessagingList.this, android.R.layout.select_dialog_item, items);
                        AlertDialog.Builder builder = new AlertDialog.Builder(MessagingList.this);
                        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == 1){
                                    AlertDialog alertDialog = new AlertDialog.Builder(MessagingList.this).create();
                                    alertDialog.setMessage("Delete Message?");
                                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "DELETE",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    deteleMeassage.child(messageKey).removeValue();
                                                    if (getCount() == 1){
                                                        DatabaseReference clearChatMessage = databaseReference.child("Chats").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                                .child(postKey);
                                                        clearChatMessage.child("message").removeValue();
                                                    }
                                                    dialog.dismiss();
                                                    Toast.makeText(MessagingList.this, "Message Deleted", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "CANCEL",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                }
                                            });
                                    alertDialog.show();
                                }else {
                                    String text = model.getMessage().toString();
                                    ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                                    ClipData clip = ClipData.newPlainText("Copied Text", text);
                                    clipboard.setPrimaryClip(clip);
                                    Toast.makeText(MessagingList.this, "Copied To Clipboard", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        builder.show();
                        return false;
                    }
                });
            }
        };
        listView.setAdapter(chatsFirebaseListAdapter);

        final DatabaseReference chatsOwner = databaseReference.child("Chats").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(postKey);

        final DatabaseReference chatsOther = databaseReference.child("Chats").child(postKey)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        final DatabaseReference owner = databaseReference.child("Messages").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        final DatabaseReference otherOwner = databaseReference.child("Messages").child(postKey);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.M)
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                String message = editTextMessage.getText().toString();
                if (message.equals("")) {
                    Toast.makeText(MessagingList.this, "Type In A Message To Send", Toast.LENGTH_SHORT).show();
                } else {
                    //THE MESSAGES
                    Map map = new HashMap();
                    map.put("username", Username);
                    map.put("profileImage", ProfileImage);
                    map.put("date", System.currentTimeMillis()/1000);
                    map.put("message", message);
                    owner.child(postKey).push().setValue(map);
                    otherOwner.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).push()
                            .setValue(map);

                    Map map2 = new HashMap();
                    map2.put("profileImage", OtherProfileImage);
                    map2.put("username", OtherUsername);
                    chatsOwner.updateChildren(map2);

                    Map map3 = new HashMap();
                    map3.put("profileImage", ProfileImage);
                    map3.put("username", Username);
                    chatsOther.updateChildren(map3);

                    //LAST CHATS MESSAGES SENT
                    Map map1 = new HashMap();
                    map1.put("sender", Username);
                    map1.put("date", System.currentTimeMillis()/1000);
                    map1.put("message", message);
                    chatsOwner.updateChildren(map1);
                    chatsOther.updateChildren(map1);
                    editTextMessage.setText("");
                }
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_sign_in, menu);
        menu.findItem(R.id.action_addImage).setVisible(false);
        menu.findItem(R.id.action_menu).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                AlertDialog alertDialog = new AlertDialog.Builder(MessagingList.this).create();
                alertDialog.setMessage("Delete All Messages?");
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "DELETE",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                DatabaseReference deleteAllMessage = databaseReference.child("Messages").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .child(postKey);
                                DatabaseReference clearChatMessage = databaseReference.child("Chats").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .child(postKey);
                                clearChatMessage.child("message").removeValue();
                                deleteAllMessage.removeValue();
                                dialog.dismiss();
                                Toast.makeText(MessagingList.this, "All Messages Deleted", Toast.LENGTH_SHORT).show();
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

        menu.findItem(R.id.action_profile).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent intent = new Intent(MessagingList.this, UserProfile.class);
                Bundle bundle = new Bundle();
                bundle.putString("key", postKey);
                bundle.putString("profileImage", OtherProfileImage);
                bundle.putString("names", Names);
                bundle.putString("username", OtherUsername);
                intent.putExtras(bundle);
                startActivity(intent);
                Toast.makeText(MessagingList.this, Names+""+OtherUsername, Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        /*menu.findItem(R.id.action_addImage).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                takeImage();
                return false;
            }
        });*/
        return super.onCreateOptionsMenu(menu);
    }

    private void takeImage(){
        String [] items = new String[]{"Take Photo", "Choose From Gallery"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(MessagingList.this, android.R.layout.select_dialog_item, items);
        AlertDialog.Builder builder = new AlertDialog.Builder(MessagingList.this);
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0){
                    Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(camera, CAMERA_REQUEST_CODE);
                    dialog.cancel();
                }else {
                    Intent gallery = new Intent();
                    gallery.setAction(Intent.ACTION_GET_CONTENT);
                    gallery.setType("image/*");
                    startActivityForResult(gallery, CAMERA_REQUEST_CODE);
                    dialog.cancel();
                }
            }
        });
        builder.show();
    }

    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {

            uri = data.getData();

            StorageReference filepath = storageReference.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child("Photos").child("Message Photos").child(UUID.randomUUID()+uri.getLastPathSegment());

            filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    downloadUri = taskSnapshot.getDownloadUrl();
                    String image = downloadUri.toString();
                    String date  = new SimpleDateFormat("HH:mm dd/MM/yy").format(new Date());

                    final DatabaseReference chatsOwner = databaseReference.child("Chats").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .child(postKey);
                    final DatabaseReference chatsOther = databaseReference.child("Chats").child(postKey)
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    final DatabaseReference owner = databaseReference.child("Messages").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    final DatabaseReference otherOwner = databaseReference.child("Messages").child(postKey);

                    Map map = new HashMap();
                    map.put("username", Username);
                    map.put("profileImage", ProfileImage);
                    map.put("date", date);
                    map.put("image1", image);
                    owner.child(postKey).push().setValue(map);
                    otherOwner.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).push()
                            .setValue(map);

                    Map map2 = new HashMap();
                    map2.put("profileImage", OtherProfileImage);
                    map2.put("username", OtherUsername);
                    chatsOwner.updateChildren(map2);

                    Map map3 = new HashMap();
                    map3.put("profileImage", ProfileImage);
                    map3.put("username", Username);
                    chatsOther.updateChildren(map3);

                    //LAST CHATS MESSAGES SENT
                    Map map1 = new HashMap();
                    map1.put("sender", Username);
                    map1.put("date", date);
                    map1.put("message", "Photo");
                    chatsOwner.updateChildren(map1);
                    chatsOther.updateChildren(map1);

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(MessagingList.this, "Photo Could Not Be Posted", Toast.LENGTH_SHORT).show();
                }
            });

        }
    }*/
}
