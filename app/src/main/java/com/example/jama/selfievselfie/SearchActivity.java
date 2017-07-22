package com.example.jama.selfievselfie;

import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.jama.selfievselfie.model.CircleTransform;
import com.example.jama.selfievselfie.model.Getters;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class SearchActivity extends AppCompatActivity {

    ListView searchList;
    DatabaseReference databaseReference;
    FirebaseListAdapter<Getters> listAdapter;
    String search;
    EditText editTextSearch;
    Query query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Search For People");

        searchList = (ListView) findViewById(R.id.listViewSearch);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Search Users");

        editTextSearch = (EditText) findViewById(R.id.editTextSearch);

        /*editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                search = editTextSearch.getText().toString();
                if (search.equals("")){
                    searchList.setAdapter(null);
                }else{
                    searchList.setAdapter(listAdapter);
                }
            }
        });*/

        //search = editTextSearch.getText().toString();

        listAdapter = new FirebaseListAdapter<Getters>(
                this,
                Getters.class,
                R.layout.search_layout,
                databaseReference
        ) {
            @Override
            protected void populateView(View v, final Getters model, int position) {

                final String postKey = getRef(position).getKey();

                TextView username = (TextView) v.findViewById(R.id.textViewUsername);
                username.setText(model.getUsername());
                TextView name = (TextView) v.findViewById(R.id.textViewName);
                name.setText(model.getName());
                ImageView profileImage = (ImageView) v.findViewById(R.id.imageViewProfile);
                Glide.with(SearchActivity.this).load(model.getProfileImage()).bitmapTransform(new CircleTransform(SearchActivity.this)).centerCrop().into(profileImage);

                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (postKey.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                            transaction.replace(R.id.content, Profile.newInstance());
                            transaction.commit();
                        }else {
                            Toast.makeText(SearchActivity.this, postKey + "", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(SearchActivity.this, UserProfile.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("key", postKey);
                            intent.putExtras(bundle);
                            startActivity(intent);
                            finish();
                        }
                    }
                });
            }
        };

        searchList.setAdapter(listAdapter);
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}
