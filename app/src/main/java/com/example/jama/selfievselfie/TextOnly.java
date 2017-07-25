package com.example.jama.selfievselfie;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.Image;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TextOnly extends AppCompatActivity {

    EditText caption, choice1, choice2, choice3, choice4, choice5;
    ImageView imageCloseChoice3, imageCloseChoice4, imageCloseChoice5;
    LinearLayout linearLayout3, linearLayout4, linearLayout5;
    Button moreChoices;
    ImageView imageChoice1, imageChoice2, imageChoice3, imageChoice4, imageChoice5;
    ArrayList<String> arrayList = new ArrayList<>();
    DatabaseReference databaseReference;
    String Choice1, Choice2, Choice3, Choice4, Choice5, Caption;
    String Username, ProfileImage;
    private Uri downloadUri1, downloadUri2, downloadUri3, downloadUri4, downloadUri5, uri;
    private static final int CAMERA_PERMISSION = 3;
    private static final int CAMERA_REQUEST_CODE = 1;
    StorageReference storageReference;
    int choiceType = 0;
    boolean isChoice1Null = true, isChoice2Null = true, isChoice3Null = true, isChoice4Null = true, isChoice5Null = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_only);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();
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

        Toast.makeText(this, ""+arrayList.size(), Toast.LENGTH_SHORT).show();

        imageChoice1 = (ImageView) findViewById(R.id.imageViewChoice1);
        imageChoice2 = (ImageView) findViewById(R.id.imageViewChoice2);
        imageChoice3 = (ImageView) findViewById(R.id.imageViewChoice3);
        imageChoice4 = (ImageView) findViewById(R.id.imageViewChoice4);
        imageChoice5 = (ImageView) findViewById(R.id.imageViewChoice5);

        imageChoice1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choiceType = 1;
                takeImage();
            }
        });

        imageChoice2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choiceType = 2;
                takeImage();
            }
        });

        imageChoice3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choiceType = 3;
                takeImage();
            }
        });

        imageChoice4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choiceType = 4;
                takeImage();
            }
        });

        imageChoice5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choiceType = 5;
                takeImage();
            }
        });

        imageCloseChoice3 = (ImageView) findViewById(R.id.imageViewAddOption3);
        imageCloseChoice4 = (ImageView) findViewById(R.id.imageViewAddOption4);
        imageCloseChoice5 = (ImageView) findViewById(R.id.imageViewAddOption5);

        linearLayout3 = (LinearLayout) findViewById(R.id.layoutChoice3);
        linearLayout4 = (LinearLayout) findViewById(R.id.layoutChoice4);
        linearLayout5 = (LinearLayout) findViewById(R.id.layoutChoice5);

        choice3 = (EditText) findViewById(R.id.editTextOption3);
        choice4 = (EditText) findViewById(R.id.editTextOption4);
        choice5 = (EditText) findViewById(R.id.editTextOption5);

        moreChoices = (Button) findViewById(R.id.buttonMoreChoices);
        moreChoices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (arrayList.size() == 0){
                    linearLayout3.setVisibility(View.VISIBLE);
                    arrayList.add(0, "choice3");
                }else if (arrayList.size() == 1){
                    linearLayout4.setVisibility(View.VISIBLE);
                    arrayList.add(1, "choice4");
                    imageCloseChoice3.setVisibility(View.GONE);
                }else if (arrayList.size() == 2){
                    linearLayout5.setVisibility(View.VISIBLE);
                    arrayList.add(2, "choice5");
                    imageCloseChoice4.setVisibility(View.GONE);
                    moreChoices.setEnabled(false);
                }
            }
        });

        imageCloseChoice3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linearLayout3.setVisibility(View.GONE);
                arrayList.remove(0);
                choice3.setText("");
            }
        });

        imageCloseChoice4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linearLayout4.setVisibility(View.GONE);
                imageCloseChoice3.setVisibility(View.VISIBLE);
                arrayList.remove(1);
                choice4.setText("");
            }
        });

        imageCloseChoice5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linearLayout5.setVisibility(View.GONE);
                imageCloseChoice4.setVisibility(View.VISIBLE);
                arrayList.remove(2);
                moreChoices.setEnabled(true);
                choice5.setText("");
            }
        });

    }

//    ADDING IMAGES TO THE CHOICES
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
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            uri = data.getData();
            CropImage.activity(uri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(this);
        }

        if (choiceType == 1){
            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK) {

                    final Uri resultUri = result.getUri();

                    if (isChoice1Null == false){
                        final StorageReference filepath = storageReference.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .child("Photos").child("Choice Posts").child(UUID.randomUUID() + uri.getLastPathSegment());
                        StorageReference deletePreviousImage = FirebaseStorage.getInstance().getReferenceFromUrl(downloadUri1.toString());
                        deletePreviousImage.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                filepath.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        downloadUri1 = taskSnapshot.getDownloadUrl();
                                        Glide.with(TextOnly.this).load(downloadUri1.toString()).into(imageChoice1);
                                        isChoice1Null = false;
                                        Toast.makeText(TextOnly.this, "Image changed", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(TextOnly.this, "Something went wrong, check connection", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(TextOnly.this, "Something went wrong, check connection", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }else {
                        StorageReference filepath = storageReference.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .child("Photos").child("Choice Posts").child(UUID.randomUUID() + uri.getLastPathSegment());
                        filepath.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                downloadUri1 = taskSnapshot.getDownloadUrl();
                                Glide.with(TextOnly.this).load(downloadUri1.toString()).into(imageChoice1);
                                isChoice1Null = false;
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(TextOnly.this, "Something went wrong, check connection", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                }else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Exception error = result.getError();
                    Toast.makeText(this, "did not work" + error, Toast.LENGTH_SHORT).show();
                }
            }
        }else if (choiceType == 2){
            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK) {

                    final Uri resultUri = result.getUri();

                    if (isChoice2Null == false){
                        final StorageReference filepath = storageReference.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .child("Photos").child("Choice Posts").child(UUID.randomUUID() + uri.getLastPathSegment());
                        StorageReference deletePreviousImage = FirebaseStorage.getInstance().getReferenceFromUrl(downloadUri2.toString());
                        deletePreviousImage.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                filepath.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        downloadUri2 = taskSnapshot.getDownloadUrl();
                                        Glide.with(TextOnly.this).load(downloadUri2.toString()).into(imageChoice2);
                                        isChoice2Null = false;
                                        Toast.makeText(TextOnly.this, "Image changed", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(TextOnly.this, "Something went wrong, check connection", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(TextOnly.this, "Something went wrong, check connection", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }else {
                        StorageReference filepath = storageReference.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .child("Photos").child("Choice Posts").child(UUID.randomUUID() + uri.getLastPathSegment());
                        filepath.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                downloadUri2 = taskSnapshot.getDownloadUrl();
                                Glide.with(TextOnly.this).load(downloadUri2.toString()).into(imageChoice2);
                                isChoice2Null = false;
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(TextOnly.this, "Something went wrong, check connection", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                }else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Exception error = result.getError();
                    Toast.makeText(this, "did not work" + error, Toast.LENGTH_SHORT).show();
                }
            }
        }else if (choiceType == 3){
            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK) {

                    final Uri resultUri = result.getUri();

                    if (isChoice3Null == false){
                        final StorageReference filepath = storageReference.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .child("Photos").child("Choice Posts").child(UUID.randomUUID() + uri.getLastPathSegment());
                        StorageReference deletePreviousImage = FirebaseStorage.getInstance().getReferenceFromUrl(downloadUri3.toString());
                        deletePreviousImage.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                filepath.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        downloadUri3 = taskSnapshot.getDownloadUrl();
                                        Glide.with(TextOnly.this).load(downloadUri3.toString()).into(imageChoice3);
                                        isChoice3Null = false;
                                        Toast.makeText(TextOnly.this, "Image changed", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(TextOnly.this, "Something went wrong, check connection", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(TextOnly.this, "Something went wrong, check connection", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }else {
                        StorageReference filepath = storageReference.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .child("Photos").child("Choice Posts").child(UUID.randomUUID() + uri.getLastPathSegment());
                        filepath.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                downloadUri3 = taskSnapshot.getDownloadUrl();
                                Glide.with(TextOnly.this).load(downloadUri3.toString()).into(imageChoice3);
                                isChoice3Null = false;
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(TextOnly.this, "Something went wrong, check connection", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                }else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Exception error = result.getError();
                    Toast.makeText(this, "did not work" + error, Toast.LENGTH_SHORT).show();
                }
            }
        }else if (choiceType == 4){
            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK) {

                    final Uri resultUri = result.getUri();

                    if (isChoice4Null == false){
                        final StorageReference filepath = storageReference.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .child("Photos").child("Choice Posts").child(UUID.randomUUID() + uri.getLastPathSegment());
                        StorageReference deletePreviousImage = FirebaseStorage.getInstance().getReferenceFromUrl(downloadUri4.toString());
                        deletePreviousImage.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                filepath.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        downloadUri4 = taskSnapshot.getDownloadUrl();
                                        Glide.with(TextOnly.this).load(downloadUri4.toString()).into(imageChoice4);
                                        isChoice4Null = false;
                                        Toast.makeText(TextOnly.this, "Image changed", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(TextOnly.this, "Something went wrong, check connection", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(TextOnly.this, "Something went wrong, check connection", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }else {
                        StorageReference filepath = storageReference.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .child("Photos").child("Choice Posts").child(UUID.randomUUID() + uri.getLastPathSegment());
                        filepath.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                downloadUri4 = taskSnapshot.getDownloadUrl();
                                Glide.with(TextOnly.this).load(downloadUri4.toString()).into(imageChoice4);
                                isChoice4Null = false;
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(TextOnly.this, "Something went wrong, check connection", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                }else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Exception error = result.getError();
                    Toast.makeText(this, "did not work" + error, Toast.LENGTH_SHORT).show();
                }
            }
        }else if (choiceType == 5){
            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK) {

                    final Uri resultUri = result.getUri();

                    if (isChoice5Null == false){
                        final StorageReference filepath = storageReference.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .child("Photos").child("Choice Posts").child(UUID.randomUUID() + uri.getLastPathSegment());
                        StorageReference deletePreviousImage = FirebaseStorage.getInstance().getReferenceFromUrl(downloadUri5.toString());
                        deletePreviousImage.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                filepath.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        downloadUri5 = taskSnapshot.getDownloadUrl();
                                        Glide.with(TextOnly.this).load(downloadUri5.toString()).into(imageChoice5);
                                        isChoice5Null = false;
                                        Toast.makeText(TextOnly.this, "Image changed", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(TextOnly.this, "Something went wrong, check connection", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(TextOnly.this, "Something went wrong, check connection", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }else {
                        StorageReference filepath = storageReference.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .child("Photos").child("Choice Posts").child(UUID.randomUUID() + uri.getLastPathSegment());
                        filepath.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                downloadUri5 = taskSnapshot.getDownloadUrl();
                                Glide.with(TextOnly.this).load(downloadUri5.toString()).into(imageChoice5);
                                isChoice5Null = false;
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(TextOnly.this, "Something went wrong, check connection", Toast.LENGTH_SHORT).show();
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
                choice1 = (EditText) findViewById(R.id.editTextOption1);
                choice2 = (EditText) findViewById(R.id.editTextOption2);
                choice3 = (EditText) findViewById(R.id.editTextOption3);
                choice4 = (EditText) findViewById(R.id.editTextOption4);
                choice5 = (EditText) findViewById(R.id.editTextOption5);

                Choice1 = choice1.getText().toString();
                Choice2 = choice2.getText().toString();
                Choice3 = choice3.getText().toString();
                Choice4 = choice4.getText().toString();
                Choice5 = choice5.getText().toString();
                Caption = caption.getText().toString();

                if (Choice1.equals("") || Choice2.equals("")){
                    Toast.makeText(TextOnly.this, "Add at least two choices to post", Toast.LENGTH_SHORT).show();
                }else {
                    DatabaseReference post = databaseReference.child("Posts").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    DatabaseReference allPosts = databaseReference.child("All Posts").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    String pushKey = post.push().getKey();

                    if (isChoice1Null == true || isChoice2Null == true){
                        Toast.makeText(TextOnly.this, "Please add a photo", Toast.LENGTH_SHORT).show();
                    }else {

                        if (Choice3.equals("")){
                            Choice3 = null;
                        }
                        if (Choice4.equals("")){
                            Choice4 = null;
                        }
                        if (Choice5.equals("")){
                            Choice5 = null;
                        }

                        Map map = new HashMap();
                        if (isChoice3Null == false){
                            map.put("choice3Image", downloadUri3.toString());
                        }
                        if (isChoice4Null == false){
                            map.put("choice4Image", downloadUri4.toString());
                        }
                        if (isChoice5Null == false){
                            map.put("choice5Image", downloadUri5.toString());
                        }
                        map.put("choice1Image", downloadUri1.toString());
                        map.put("choice2Image", downloadUri2.toString());
                        map.put("choice1", Choice1);
                        map.put("choice2", Choice2);
                        map.put("choice3", Choice3);
                        map.put("choice4", Choice4);
                        map.put("choice5", Choice5);
                        map.put("username2", Username);
                        map.put("profileImage2", ProfileImage);
                        map.put("date", System.currentTimeMillis()/1000);
                        map.put("caption", Caption);
                        map.put("uid2", FirebaseAuth.getInstance().getCurrentUser().getUid());
                        map.put("pushKey", pushKey);
                        allPosts.child(pushKey).setValue(map);
                        post.child(pushKey).setValue(map);
                    }
                }
                return false;
            }
        });

        return true;
    }
}
