package com.example.jama.selfievselfie;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.*;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

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

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SinglePost extends AppCompatActivity {

    ImageView image;
    EditText caption;
    DatabaseReference databaseReference;
    String Username, ProfileImage, ImagePost;
    boolean isImageNull = true;
    private static final int CAMERA_REQUEST_CODE = 1;
    private Uri downloadUri, uri;
    StorageReference storageReference;
    private static final int CAMERA_PERMISSION = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_post);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();
        DatabaseReference profileInfo = databaseReference.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Profile Info");

        image = (ImageView) findViewById(R.id.imageViewImagePost);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takeImage();
            }
        });

        //GET PROFILE INFO
        profileInfo.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, String> map = (Map)dataSnapshot.getValue();
                Username = map.get("username");
                ProfileImage = map.get("profileImage");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void takeImage() {
        String[] items = new String[]{"Take Photo", "Choose From Gallery"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_item, items);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (which == 0) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (checkSelfPermission(android.Manifest.permission.CAMERA)
                                != PackageManager.PERMISSION_GRANTED) {
                            requestPermissions(new String[]{android.Manifest.permission.CAMERA},
                                    CAMERA_PERMISSION);
                        } else {
                            camera();
                        }
                    }
                } else {
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

    private void camera() {
        Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(camera, CAMERA_REQUEST_CODE);
        //dialog.cancel();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_edit_post, menu);
        menu.findItem(R.id.action_edit_post).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                caption = (EditText) findViewById(R.id.editTextCaption);

                if (isImageNull == true){
                    Toast.makeText(SinglePost.this, "Add an image to post", Toast.LENGTH_SHORT).show();
                }else {
                    DatabaseReference post = databaseReference.child("Posts").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    DatabaseReference allPosts = databaseReference.child("All Posts").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    String pushKey = post.push().getKey();
                    String Caption = caption.getText().toString();

                    if (Caption.equals("")){
                        Caption = "";
                    }

                    Map map = new HashMap();
                    map.put("username2", Username);
                    map.put("profileImage2", ProfileImage);
                    map.put("date", System.currentTimeMillis()/1000);
                    map.put("caption", Caption);
                    map.put("image1", ImagePost);
                    map.put("pushKey", pushKey);
                    map.put("uid2", FirebaseAuth.getInstance().getCurrentUser().getUid());
                    allPosts.child(pushKey).setValue(map);
                    post.child(pushKey).setValue(map);
                }

                return false;
            }
        });

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            uri = data.getData();
            CropImage.activity(uri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(16, 9)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                Uri resultUri = result.getUri();

                StorageReference filepath = storageReference.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child("Photos").child("Posts").child(UUID.randomUUID() + uri.getLastPathSegment());
                filepath.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        downloadUri = taskSnapshot.getDownloadUrl();
                        Picasso.with(SinglePost.this).load(downloadUri.toString()).fit().into(image);
                        ImagePost = downloadUri.toString();
                        isImageNull = false;
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

            }else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(this, "did not work" + error, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == CAMERA_PERMISSION
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            camera();
        }
    }
}
