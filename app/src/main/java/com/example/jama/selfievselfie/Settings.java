package com.example.jama.selfievselfie;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.*;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.appinvite.AppInviteInvitation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;

public class Settings extends AppCompatActivity {

    ListView listView;
    String[] values = new String[] { "Change Password", "Block Users", "Edit Profile" };
    ProgressDialog progressDialog;
    Switch privateAccount;
    DatabaseReference databaseReference;
    Button invite;
    int REQUEST_INVITE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        progressDialog = new ProgressDialog(this);
        databaseReference = FirebaseDatabase.getInstance().getReference();

        android.view.View header = android.view.View.inflate(Settings.this, R.layout.setting_header, null);
        android.view.View footer = android.view.View.inflate(Settings.this, R.layout.private_account_switch, null);

        listView = (ListView) findViewById(R.id.listView);
        invite = (Button) header.findViewById(R.id.buttonInvite);
        privateAccount = (Switch) footer.findViewById(R.id.privateAccountSwitch);

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

        DatabaseReference checkIfAccountIsPrivateOrPublic = databaseReference.child("Private Accounts");
        checkIfAccountIsPrivateOrPublic.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                    privateAccount.setChecked(true);
                }else {
                    privateAccount.setChecked(false);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Settings");

        privateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (privateAccount.isChecked()){
                    AlertDialog alertDialog = new AlertDialog.Builder(Settings.this).create();
                    alertDialog.setCancelable(false);
                    alertDialog.setTitle("Change Account To Private?");
                    alertDialog.setMessage("Only people you approved can only see your posts");
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    DatabaseReference setPrivateAccountOn = databaseReference.child("Private Accounts")
                                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                    setPrivateAccountOn.setValue(true);
                                    Toast.makeText(Settings.this, "account changed to private", Toast.LENGTH_SHORT).show();
                                }
                            });
                    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "CANCEL",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    privateAccount.setChecked(false);
                                }
                            });
                    alertDialog.show();
                }else {
                    AlertDialog alertDialog = new AlertDialog.Builder(Settings.this).create();
                    alertDialog.setCancelable(false);
                    alertDialog.setTitle("Change Account To Public?");
                    alertDialog.setMessage("Anyone can see you posts");
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    DatabaseReference setPrivateAccountOn = databaseReference.child("Private Accounts")
                                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                    setPrivateAccountOn.removeValue();
                                    Toast.makeText(Settings.this, "account changed to public", Toast.LENGTH_SHORT).show();
                                }
                            });
                    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "CANCEL",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    privateAccount.setChecked(true);
                                }
                            });
                    alertDialog.show();
                }
            }
        });

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                values
        );

        listView.setAdapter(adapter);
        listView.addHeaderView(header, null, false);
        listView.addFooterView(footer, null, false);
        
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 1){
                    forgotPasswordDialog();
                }else  if (position == 2){
                    Intent intent = new Intent(Settings.this, BlockedUsers.class);
                    startActivity(intent);
                }else {
                    Intent editProfile = new Intent(Settings.this, EditProfile.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("key", FirebaseAuth.getInstance().getCurrentUser().getUid());
                    editProfile.putExtras(bundle);
                    startActivity(editProfile);
                }
            }
        });

    }

    private void forgotPasswordDialog() {
        final ConstraintLayout constraintLayout = (ConstraintLayout) findViewById(R.id.layout);
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.password_reset_dialog, null);
        final EditText passwordReset = (EditText) alertLayout.findViewById(R.id.editTextPasswordReset);

        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Password Reset");
        alert.setMessage("Type Your Email Account, So We Can Send You A Link To Reset Your Password");
        alert.setView(alertLayout);
        alert.setCancelable(false);
        alert.setPositiveButton("RESET", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                progressDialog.setMessage("Sending Password Reset Link");
                progressDialog.show();
                String emailAddress = passwordReset.getText().toString().trim();
                FirebaseAuth auth = FirebaseAuth.getInstance();
                if (emailAddress.equals("")){
                    Snackbar snackbar = Snackbar
                            .make(constraintLayout, "Enter Your Email Address",Snackbar.LENGTH_LONG);
                    snackbar.show();
                    progressDialog.dismiss();
                }else {
                    auth.sendPasswordResetEmail(emailAddress)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (!task.isSuccessful()){
                                        Snackbar snackbar = Snackbar
                                                .make(constraintLayout, "Make Sure Email Is Typed Correct",Snackbar.LENGTH_LONG);
                                        snackbar.show();
                                        progressDialog.dismiss();
                                    }else {
                                        Snackbar snackbar = Snackbar
                                                .make(constraintLayout, "Password Reset Link Has Been Sent",Snackbar.LENGTH_LONG);
                                        snackbar.show();
                                        progressDialog.dismiss();
                                    }
                                }
                            });
                }

            }
        }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        AlertDialog dialog = alert.create();
        dialog.show();
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Log.d(TAG, "onActivityResult: requestCode=" + requestCode + ", resultCode=" + resultCode);

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
    }

}
