package com.example.jama.selfievselfie;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jama.selfievselfie.model.Getters;
import com.example.jama.selfievselfie.model.RoundedTransformation;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.gms.appinvite.AppInviteInvitation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class Following extends AppCompatActivity {

    DatabaseReference databaseReference;
    FirebaseListAdapter<Getters> listAdapter;
    ListView listView;
    Button invite;
    int REQUEST_INVITE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_following);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Following");

        databaseReference = FirebaseDatabase.getInstance().getReference();

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

                final String postKey = getRef(position).getKey();

                TextView username = (TextView) v.findViewById(R.id.textViewUsername);
                username.setText(model.getUsername());
                TextView names = (TextView) v.findViewById(R.id.textViewName);
                names.setText(model.getName());
                ImageView profileImage = (ImageView) v.findViewById(R.id.imageViewProfile);
                Picasso.with(Following.this).load(model.getProfileImage()).transform(new RoundedTransformation(50, 4)).centerCrop().fit().into(profileImage);

                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(Following.this, postKey + "", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Following.this, UserProfile.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("key", postKey);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                });
            }
        };
        listView.setAdapter(listAdapter);
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
