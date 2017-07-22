package com.example.jama.selfievselfie;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.jama.selfievselfie.model.CircleTransform;
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
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EditProfile extends AppCompatActivity {

    ProgressDialog progressDialog;
    String Names, Username, Bio, ProfileImage, checkProfile;
    EditText editTextNames, editTextBio, editTextUsername;
    ImageView profileImage;
    private static final int CAMERA_REQUEST_CODE = 1;
    DatabaseReference databaseReference;
    StorageReference storageReference;
    private Uri downloadUri, uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Edit Profile");

        progressDialog = new ProgressDialog(this);

        profileImage = (ImageView) findViewById(R.id.imageView);
        editTextUsername = (EditText) findViewById(R.id.editTextUsername);
        editTextNames = (EditText) findViewById(R.id.editTextNames);
        editTextBio = (EditText) findViewById(R.id.editTextBio);

        progressDialog.setMessage("Loading ...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        databaseReference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();

        Bundle bundle = getIntent().getExtras();
        checkProfile = bundle.getString("key");

        DatabaseReference profieInfo = databaseReference.child("Users").child(checkProfile).child("Profile Info");

        profieInfo.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map <String, String> map = (Map)dataSnapshot.getValue();
                ProfileImage = map.get("profileImage");
                Username = map.get("username");
                Bio = map.get("bio");
                Names = map.get("name");

                editTextBio.setText(Bio);
                editTextUsername.setText(Username);
                editTextNames.setText(Names);
                Glide.with(EditProfile.this).load(ProfileImage).bitmapTransform(new CircleTransform(EditProfile.this)).into(profileImage);
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        //STARTS THE changePhoto() METHOD
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePhoto();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {

            uri = data.getData();

            CropImage.activity(uri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                progressDialog.setMessage("Uploading Your Image");
                progressDialog.show();
                progressDialog.setCancelable(false);

                Uri resultUri = result.getUri();

                StorageReference filepath = storageReference.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child("Photos").child("Profile Image").child(UUID.randomUUID()+uri.getLastPathSegment());


                filepath.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        downloadUri = taskSnapshot.getDownloadUrl();
                        databaseReference.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .child("Profile Info").child("profileImage").setValue(downloadUri.toString());
                        databaseReference.child("Search Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .child("profileImage").setValue(downloadUri.toString());
                        Toast.makeText(EditProfile.this, "Image Uploaded", Toast.LENGTH_SHORT).show();
                        Glide.with(EditProfile.this).load(downloadUri.toString()).bitmapTransform(new CircleTransform(EditProfile.this)).into(profileImage);
                        progressDialog.dismiss();
                    }
                });
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    private void changePhoto(){
        String [] items = new String[]{"Take Photo", "Choose From Gallery"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(EditProfile.this, android.R.layout.select_dialog_item, items);
        AlertDialog.Builder builder = new AlertDialog.Builder(EditProfile.this);
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
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_post, menu);
        menu.findItem(R.id.action_edit_post).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                final DatabaseReference updateProfile = FirebaseDatabase.getInstance().getReference().child("Users")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Profile Info");

                final DatabaseReference updateSearch = FirebaseDatabase.getInstance().getReference().child("Search Users")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

                editTextUsername = (EditText) findViewById(R.id.editTextUsername);
                editTextNames = (EditText) findViewById(R.id.editTextNames);
                editTextBio = (EditText) findViewById(R.id.editTextBio);

                String names = editTextNames.getText().toString();
                String username = editTextUsername.getText().toString();
                String bio = editTextBio.getText().toString();

                if (username.equals("") || names.equals("")){
                    Toast.makeText(EditProfile.this, "Please Fill In All Details", Toast.LENGTH_SHORT).show();
                }else {
                    if (bio.equals("")){
                        bio = "";
                    }
                    Map map = new HashMap();
                    map.put("name", names);
                    map.put("username", username);
                    map.put("bio", bio);
                    updateProfile.updateChildren(map);
                    updateSearch.updateChildren(map);
                    Toast.makeText(EditProfile.this, "Profile Updated", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    finish();
                }
                return false;
            }
        });
        return true;
    }
}
