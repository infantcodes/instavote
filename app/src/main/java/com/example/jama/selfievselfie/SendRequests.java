package com.example.jama.selfievselfie;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jama.selfievselfie.model.Getters;
import com.example.jama.selfievselfie.model.RoundedTransformation;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.gms.appinvite.AppInviteInvitation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by JAMA on 4/13/2017.
 */

public class SendRequests extends AppCompatActivity {

    DatabaseReference databaseReference;
    FirebaseListAdapter<Getters> listAdapter;
    ListView listView;
    Button invite;
    private static final int CAMERA_REQUEST_CODE = 1;
    private Uri downloadUri, uri;
    StorageReference storageReference;
    String uidPostKey;
    String OtherUsername, OtherProfileImage;
    String Username, ProfileImage;
    long Date;
    ProgressDialog progressDialog;
    int REQUEST_INVITE = 2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_following);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Choose A Competitor");

        DatabaseReference profieInfo = databaseReference.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("Profile Info");

        progressDialog = new ProgressDialog(this);

        Date  = System.currentTimeMillis()/1000;

        listView = (ListView) findViewById(R.id.listView);
        invite = (Button) findViewById(R.id.buttonInvite);

        invite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new AppInviteInvitation.IntentBuilder("Instavote App")
                        .setMessage("Download instavote")
                        .setDeepLink(Uri.parse("https://by92n.app.goo.gl/mxGI"))
                        .setEmailHtmlContent("<html>\n" +
                                "            <style>\n" +
                                "                h1{\n" +
                                "                    color: green;\n" +
                                "                }\n" +
                                "            </style>\n" +
                                "            <body>\n" +
                                "                <h1>Jama</h1>\n" +
                                "                <a href=\"%%APPINVITE_LINK_PLACEHOLDER%%\">Click</a>\n" +
                                "            </body>\n" +
                                "        </html>")
                        .setEmailSubject("Try It")
                        //.setCustomImage(Uri.parse(getString(R.string.invitation_deep_link)))
                        .build();
                startActivityForResult(intent, REQUEST_INVITE);
            }
        });


        DatabaseReference retrieveFollowing = databaseReference.child("Following").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        listAdapter = new FirebaseListAdapter<Getters>(
                this,
                Getters.class,
                R.layout.search_layout,
                retrieveFollowing
        ) {
            @Override
            protected void populateView(View v, final Getters model, int position) {

                TextView username = (TextView) v.findViewById(R.id.textViewUsername);
                username.setText(model.getUsername());
                TextView names = (TextView) v.findViewById(R.id.textViewName);
                names.setText(model.getName());
                ImageView profileImage = (ImageView) v.findViewById(R.id.imageViewProfile);
                Picasso.with(SendRequests.this).load(model.getProfileImage()).transform(new RoundedTransformation(50, 4)).centerCrop().fit().into(profileImage);

                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    choosePhoto();
                    OtherUsername = model.getUsername().toString();
                    OtherProfileImage = model.getProfileImage().toString();
                    uidPostKey = model.getUid().toString();
                    }
                });
            }
        };
        listView.setAdapter(listAdapter);

        profieInfo.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map <String, String> map = (Map)dataSnapshot.getValue();
                ProfileImage = map.get("profileImage");
                Username = map.get("username");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void choosePhoto(){
        String [] items = new String[]{"Take Photo", "Choose From Gallery"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(SendRequests.this, android.R.layout.select_dialog_item, items);
        AlertDialog.Builder builder = new AlertDialog.Builder(SendRequests.this);
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

    @Override
    protected void onActivityResult(final int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_INVITE) {
            if (resultCode == RESULT_OK) {
                // Get the invitation IDs of all sent messages
                String[] ids = AppInviteInvitation.getInvitationIds(resultCode, data);
                for (String id : ids) {
                    //Log.d(TAG, "onActivityResult: sent invitation " + id);
                }
            } else {
                // Sending failed or it was canceled, show failure message to the user
                // ...
            }
        }

        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {

            uri = data.getData();
            CropImage.activity(uri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(9, 16)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            final CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                LayoutInflater inflater = getLayoutInflater();
                View alertLayout = inflater.inflate(R.layout.password_reset_dialog, null);
                final EditText passwordReset = (EditText) alertLayout.findViewById(R.id.editTextPasswordReset);

                final AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setTitle("Request Message");
                alert.setMessage("Send A Message To "+OtherUsername);
                alert.setView(alertLayout);
                alert.setCancelable(false);
                alert.setPositiveButton("SEND REQUEST", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String requestMessage = passwordReset.getText().toString().trim();
                        if (requestMessage.equals("")){
                            requestMessage = "Try to top that!!";
                        }

                        Uri resultUri = result.getUri();

                        StorageReference filepath = storageReference.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .child("Photos").child("Posts").child(UUID.randomUUID()+uri.getLastPathSegment());

                        final String finalRequestMessage = requestMessage;
                        filepath.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                downloadUri = taskSnapshot.getDownloadUrl();
                                String image = downloadUri.toString();

                                String pushKey = databaseReference.child("Pending Requests").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .push().getKey();

                                //THE PERSON HOW SENT THE REQUEST (PENDING REQUESTS)
                                Map map = new HashMap();
                                map.put("image1", image);
                                map.put("pushKey", pushKey);
                                map.put("requestMessage", finalRequestMessage);
                                map.put("profileImage", OtherProfileImage);
                                map.put("username", OtherUsername);
                                map.put("date", Date);
                                map.put("uid", uidPostKey);
                                map.put("status", "Request is still pending");

                                //THE PERSON WHO WAS SENT THE REQUEST
                                Map map1 = new HashMap();
                                map1.put("image1", image);
                                map1.put("pushKey", pushKey);
                                map1.put("requestMessage", finalRequestMessage);
                                map1.put("profileImage", ProfileImage);
                                map1.put("username", Username);
                                map1.put("date", Date);
                                map1.put("uid", FirebaseAuth.getInstance().getCurrentUser().getUid().toString());

                                databaseReference.child("Pending Requests").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .child(pushKey).setValue(map);
                                databaseReference.child("Requests").child(uidPostKey)
                                        .child(pushKey).setValue(map1);
                                Toast.makeText(SendRequests.this, "Request Has Been Sent", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(SendRequests.this, "Request Failed, Try Again", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
                AlertDialog dialog = alert.create();
                dialog.show();

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}
