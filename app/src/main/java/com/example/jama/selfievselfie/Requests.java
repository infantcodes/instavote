package com.example.jama.selfievselfie;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jama.selfievselfie.model.Getters;
import com.example.jama.selfievselfie.model.RoundedTransformation;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
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
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;

/**
 * Created by JAMA on 4/14/2017.
 */

public class Requests extends Fragment {

    DatabaseReference databaseReference;
    ListView listView;
    private Uri downloadUri, uri;
    StorageReference storageReference;
    ProgressDialog progressDialog;
    String PushKey, Username, Uid, ProfileImage, mAuth;
    int anInt = 0, test = 0;
    private static final int CAMERA_REQUEST_CODE = 2;
    private static final int CAMERA_PERMISSION = 3;
    Fragment fragment = this;
    private static final int SECOND_MILLIS = 60;
    private static final int MINUTE_MILLIS = 1 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;

    public static Requests newInstance() {
        Requests fragment = new Requests();
        return fragment;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_request, container, false);

        //final View header = View.inflate(getActivity(), R.layout.no_data, null);

        mAuth = FirebaseAuth.getInstance().getCurrentUser().getUid();

        databaseReference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();
        progressDialog = new ProgressDialog(getActivity());
/*
        DatabaseReference profieInfo = databaseReference.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("Profile Info");

        profieInfo.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, String> map = (Map) dataSnapshot.getValue();
                ProfileImage = map.get("profileImage");
                Username = map.get("username");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/

        listView = (ListView) rootview.findViewById(R.id.listView);

        final DatabaseReference retriveRequests = databaseReference.child("Requests").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        final FirebaseListAdapter<Getters> listAdapter = new FirebaseListAdapter<Getters>(
                getActivity(),
                Getters.class,
                R.layout.request_layout,
                retriveRequests
        ) {
            @Override
            protected void populateView(View v, final Getters model, int position) {

                final String[] postKey = {getRef(position).getKey()};

                TextView username = (TextView) v.findViewById(R.id.textViewUsername);
                username.setText(model.getUsername());
                username.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), UserProfile.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("key", model.getUid());
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                });
                TextView requestMessage = (TextView) v.findViewById(R.id.textViewRequestMessage);
                requestMessage.setText(model.getRequestMessage());
                final ImageView profileImage = (ImageView) v.findViewById(R.id.imageViewProfile);
                profileImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), UserProfile.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("key", model.getUid());
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                });
                Picasso.with(getContext()).load(model.getProfileImage()).fit().transform(new RoundedTransformation(50, 4))
                        .networkPolicy(NetworkPolicy.OFFLINE).into(profileImage, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        Picasso.with(getContext()).load(model.getProfileImage()).fit().transform(new RoundedTransformation(50, 4)).into(profileImage);
                    }
                });
                final ImageView image = (ImageView) v.findViewById(R.id.imageViewImage);
                Picasso.with(getContext()).load(model.getImage1()).fit().centerCrop()
                        .networkPolicy(NetworkPolicy.OFFLINE).into(image, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        Picasso.with(getContext()).load(model.getImage1()).fit().centerCrop().into(image);
                    }
                });
                image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent view = new Intent(getActivity(), com.example.jama.selfievselfie.View.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("image", model.getImage1());
                        view.putExtras(bundle);
                        startActivity(view);
                    }
                });
                Button acceptRequest = (Button) v.findViewById(R.id.buttonAccept);
                acceptRequest.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ProfileImage = model.getProfileImage();
                        Username = model.getUsername();
                        PushKey = model.getPushKey();
                        Uid = model.getUid();
                        //if ()
                        Toast.makeText(getActivity(), "" + PushKey, Toast.LENGTH_SHORT).show();
                        takeImage();
                    }
                });

                //TIME*********************************
                TextView date = (TextView) v.findViewById(R.id.textViewDate);
                long time = model.getDate();
                long now = System.currentTimeMillis() / 1000;
                long diff = now - time;
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

                Button declineRequest = (Button) v.findViewById(R.id.buttonDecline);
                declineRequest.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                        alertDialog.setMessage("Decline Request?");
                        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "YES",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        StorageReference deleteImage = FirebaseStorage.getInstance().getReferenceFromUrl(model.getImage1());
                                        deleteImage.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                DatabaseReference declineRequest = databaseReference.child("Requests")
                                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(postKey[0]);
                                                declineRequest.removeValue();
                                                DatabaseReference declineRequestOther = databaseReference.child("Pending Requests").child(model.getUid().toString())
                                                        .child(postKey[0]);
                                                declineRequestOther.removeValue();
                                                Snackbar snackbar = Snackbar
                                                        .make(container, "You Have Declined A Request", Snackbar.LENGTH_SHORT);
                                                snackbar.show();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(getActivity(), "Unable to decline request", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                });
                        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "NO",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        alertDialog.show();
                    }
                });
            }
        };

        //listView.addHeaderView(header, null, false);
        listView.setAdapter(listAdapter);
        listView.setNestedScrollingEnabled(true);
        RelativeLayout txtBio = (RelativeLayout) rootview.findViewById(R.id.relativeLayout7);
        listView.setEmptyView(txtBio);
        setHasOptionsMenu(true);

        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("InstaVote");

        return rootview;
    }

    private void camera() {
        Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(camera, CAMERA_REQUEST_CODE);
        //dialog.cancel();
    }

    private void takeImage() {
        String[] items = new String[]{"Take Photo", "Choose From Gallery"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.select_dialog_item, items);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (which == 0) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (getActivity().checkSelfPermission(android.Manifest.permission.CAMERA)
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {

            uri = data.getData();

            CropImage.activity(uri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(9, 16)
                    .start(getActivity());
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                progressDialog.setMessage("Processing Image ...");
                progressDialog.show();
                progressDialog.setCancelable(false);

                Uri resultUri = result.getUri();

                StorageReference filepath = storageReference.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child("Photos").child("Posts").child(UUID.randomUUID() + uri.getLastPathSegment());

                filepath.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        downloadUri = taskSnapshot.getDownloadUrl();
                        String image = downloadUri.toString();
                        Toast.makeText(getActivity(), "Image sent", Toast.LENGTH_SHORT).show();
                        //THE PERSON HOW SENT THE REQUEST (PENDING REQUESTS)
                        Map map = new HashMap();
                        map.put("image2", image);
                        map.put("profileImage2", ProfileImage);
                        map.put("username2", Username);
                        map.put("date", System.currentTimeMillis()/1000);
                        map.put("status", "Request has been accepted");

                        Map map1 = new HashMap();
                        map1.put("uid2", Uid);

                        databaseReference.child("Pending Requests").child(Uid)
                                .child(PushKey).updateChildren(map);
                        databaseReference.child("Pending Requests").child(Uid)
                                .child(PushKey).updateChildren(map1);
                        databaseReference.child("Requests").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .child(PushKey).removeValue();
                        progressDialog.dismiss();
                        //finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "image not posted", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                });
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(getActivity(), "did not work" + error, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == CAMERA_PERMISSION
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            camera();
        }

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main_tab, menu);
        menu.findItem(R.id.action_new_message).setVisible(false);
        menu.findItem(R.id.add_user).setVisible(false);
        menu.findItem(R.id.action_Mentions).setVisible(false);
        menu.findItem(R.id.action_pending_request).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent intent = new Intent(getActivity(), PendingRequests.class);
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

