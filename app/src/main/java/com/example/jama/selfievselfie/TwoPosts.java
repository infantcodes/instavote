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
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TwoPosts extends AppCompatActivity {

    ImageView image1,image2;
    EditText caption;
    private static final int CAMERA_PERMISSION = 3;
    private static final int CAMERA_REQUEST_CODE = 1;
    boolean choseImage1, isImage1Null, isImage2Null;
    private Uri downloadUri1, downloadUri2, uri;
    DatabaseReference databaseReference;
    StorageReference storageReference;
    ProgressBar progressBar1, progressBar2;
    String Username, ProfileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_two_posts);

        isImage1Null = true;
        isImage2Null = true;

        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference profileInfo = databaseReference.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Profile Info");

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

        image2 = (ImageView) findViewById(R.id.imageViewImage2);
        image1 = (ImageView) findViewById(R.id.imageViewImage1);
        progressBar1 = (ProgressBar) findViewById(R.id.progressBar1);
        progressBar2 = (ProgressBar) findViewById(R.id.progressBar2);
        progressBar1.setIndeterminate(true);
        progressBar2.setIndeterminate(true);
        progressBar1.setVisibility(View.INVISIBLE);
        progressBar2.setVisibility(View.INVISIBLE);

        image2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takeImage2();
                choseImage1 = false;
            }
        });

        image1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takeImage1();
                choseImage1 = true;
            }
        });

    }

    private void takeImage1() {
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
    }

    private void takeImage2() {
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            uri = data.getData();
            CropImage.activity(uri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(9, 16)
                    .start(this);
        }

        if (choseImage1 == true){
            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK) {

                    progressBar1.setVisibility(View.VISIBLE);

                    final Uri resultUri = result.getUri();

                    if (isImage1Null == false){
                        final StorageReference filepath = storageReference.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .child("Photos").child("Posts").child(UUID.randomUUID() + uri.getLastPathSegment());
                        StorageReference deletePreviousImage = FirebaseStorage.getInstance().getReferenceFromUrl(downloadUri1.toString());
                        deletePreviousImage.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                filepath.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        downloadUri1 = taskSnapshot.getDownloadUrl();
                                        Glide.with(TwoPosts.this).load(downloadUri1.toString()).into(image1);
                                        isImage1Null = false;
                                        progressBar1.setVisibility(View.INVISIBLE);
                                        Toast.makeText(TwoPosts.this, "Image changed", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(TwoPosts.this, "Something went wrong, check connection", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(TwoPosts.this, "Something went wrong, check connection", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }else {
                        StorageReference filepath = storageReference.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .child("Photos").child("Posts").child(UUID.randomUUID() + uri.getLastPathSegment());
                        filepath.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                downloadUri1 = taskSnapshot.getDownloadUrl();
                                Glide.with(TwoPosts.this).load(downloadUri1.toString()).into(image1);
                                isImage1Null = false;
                                progressBar1.setVisibility(View.INVISIBLE);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(TwoPosts.this, "Something went wrong, check connection", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                }else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Exception error = result.getError();
                    Toast.makeText(this, "did not work" + error, Toast.LENGTH_SHORT).show();
                }
            }
        }else {
            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK) {

                    progressBar2.setVisibility(View.VISIBLE);

                    final Uri resultUri = result.getUri();

                    if (isImage2Null == false){
                        final StorageReference filepath = storageReference.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .child("Photos").child("Posts").child(UUID.randomUUID() + uri.getLastPathSegment());
                        StorageReference deletePreviousImage = FirebaseStorage.getInstance().getReferenceFromUrl(downloadUri2.toString());
                        deletePreviousImage.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                filepath.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        downloadUri2 = taskSnapshot.getDownloadUrl();
                                        Glide.with(TwoPosts.this).load(downloadUri2.toString()).into(image2);
                                        isImage2Null = false;
                                        progressBar2.setVisibility(View.INVISIBLE);
                                        Toast.makeText(TwoPosts.this, "Image changed", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(TwoPosts.this, "Something went wrong, check connection", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(TwoPosts.this, "Something went wrong, check connection", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }else {
                        StorageReference filepath = storageReference.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .child("Photos").child("Posts").child(UUID.randomUUID() + uri.getLastPathSegment());
                        filepath.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                downloadUri2 = taskSnapshot.getDownloadUrl();
                                Glide.with(TwoPosts.this).load(downloadUri2.toString()).into(image2);
                                isImage2Null = false;
                                progressBar2.setVisibility(View.INVISIBLE);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(TwoPosts.this, "Something went wrong, check connection", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                }else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Exception error = result.getError();
                    Toast.makeText(this, "did not work" + error, Toast.LENGTH_SHORT).show();
                }
            }
        }

    }

    private void deleteBothImages(){
        if (isImage1Null == false){
            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setMessage("Discard Post?");
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "DISCARD",
                    new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface dialog, int which) {
                            StorageReference deleteImage1 = FirebaseStorage.getInstance().getReferenceFromUrl(downloadUri1.toString());
                            deleteImage1.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    if (isImage2Null == false){
                                        final StorageReference deleteImage2 = FirebaseStorage.getInstance().getReferenceFromUrl(downloadUri2.toString());
                                        deleteImage2.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(TwoPosts.this, "Post Discarded", Toast.LENGTH_SHORT).show();
                                                dialog.dismiss();
                                                finish();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(TwoPosts.this, "Something went wrong, try again", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }else {
                                        Toast.makeText(TwoPosts.this, "Post Discarded", Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                        finish();
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(TwoPosts.this, "Post could not be discarded," +
                                            "check connection", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "CANCEL",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
        }else if (isImage2Null == false){
            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setMessage("Discard Post?");
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "DISCARD",
                    new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface dialog, int which) {
                            final StorageReference deleteImage2 = FirebaseStorage.getInstance().getReferenceFromUrl(downloadUri2.toString());
                            deleteImage2.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    if (isImage1Null == false){
                                        final StorageReference deleteImage1 = FirebaseStorage.getInstance().getReferenceFromUrl(downloadUri1.toString());
                                        deleteImage1.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(TwoPosts.this, "Post Discarded", Toast.LENGTH_SHORT).show();
                                                dialog.dismiss();
                                                finish();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(TwoPosts.this, "Something went wrong, try again", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }else {
                                        Toast.makeText(TwoPosts.this, "Post Discarded", Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                        finish();
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(TwoPosts.this, "Post could not be discarded," +
                                            "check connection", Toast.LENGTH_SHORT).show();
                                }
                            });
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
            finish();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == CAMERA_PERMISSION
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            camera();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_edit_post, menu);
        menu.findItem(R.id.action_edit_post).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                caption = (EditText) findViewById(R.id.editTextCaption);

                if (isImage1Null == true || isImage2Null == true){
                    Toast.makeText(TwoPosts.this, "Add two images to post", Toast.LENGTH_SHORT).show();
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
                    map.put("image1", downloadUri1.toString());
                    map.put("image2", downloadUri2.toString());
                    map.put("pushKey", pushKey);
                    map.put("uid2", FirebaseAuth.getInstance().getCurrentUser().getUid());
                    allPosts.child(pushKey).setValue(map);
                    post.child(pushKey).setValue(map);
                    finish();
                }
                return false;
            }
        });

        return true;
    }

    @Override
    public void onBackPressed() {
        deleteBothImages();
    }
}
