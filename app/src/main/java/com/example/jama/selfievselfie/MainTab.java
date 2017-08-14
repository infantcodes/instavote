package com.example.jama.selfievselfie;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class MainTab extends AppCompatActivity {

    DatabaseReference databaseReference;
    boolean showMentions, showAddUser, showPendingRequests, showMessage;

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private String uid;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_tab);

        showAddUser = false;
        showMentions = false;
        showMessage = false;
        showPendingRequests = false;

        databaseReference = FirebaseDatabase.getInstance().getReference();

        DatabaseReference checkRequests = databaseReference.child("Requests").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        checkRequests.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Snackbar snackbar = Snackbar.make(mViewPager, "You have a new request", Snackbar.LENGTH_LONG);
                snackbar.show();
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

        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                animateFab(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                animateFab(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mViewPager.setOffscreenPageLimit(4);

    }

    private void animateFab(int position) {
        switch (position) {
            case 0:
                showMentions = true;
                showMessage = true;
                showPendingRequests = true;
                invalidateOptionsMenu();
            case 1:
                showAddUser = true;
                showMentions = true;
                showMessage = true;
                showPendingRequests = false;
                invalidateOptionsMenu();
                break;
            case 2:
                showAddUser = true;
                showMentions = true;
                showMessage = false;
                showPendingRequests = true;
                invalidateOptionsMenu();
                break;
            case 3:
                showAddUser = true;
                showMentions = false;
                showMessage = true;
                showPendingRequests = true;
                invalidateOptionsMenu();
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_tab, menu);

        menu.findItem(R.id.add_user).setVisible(true);

        if (showPendingRequests == false){
            menu.findItem(R.id.action_Mentions).setVisible(false);
            menu.findItem(R.id.action_pending_request).setVisible(true);
            menu.findItem(R.id.action_new_message).setVisible(false);
        }if (showMessage == false){
            menu.findItem(R.id.action_Mentions).setVisible(false);
            menu.findItem(R.id.action_pending_request).setVisible(false);
            menu.findItem(R.id.action_new_message).setVisible(true);
        }if (showMentions == false){
            menu.findItem(R.id.action_Mentions).setVisible(true);
            menu.findItem(R.id.action_pending_request).setVisible(false);
            menu.findItem(R.id.action_new_message).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id){
            case R.id.action_settings:
                // User chose the "Settings" item, show the app settings UI...
                Intent intent = new Intent(MainTab.this, Settings.class);
                startActivity(intent);
                return true;

            case R.id.action_notification:
                Intent search = new Intent(MainTab.this, Notifiations.class);
                startActivity(search);
                return true;

            case R.id.action_Logout:
                FirebaseAuth.getInstance().signOut();
                Intent logout = new Intent(MainTab.this, MainLoginActivity.class);
                startActivity(logout);
                String refreshedToken = FirebaseInstanceId.getInstance().getToken();
                DatabaseReference send = FirebaseDatabase.getInstance().getReference();
                send.child("Users").child(uid).child("Notification Token").child(refreshedToken).removeValue();
                finish();
                return true;

            case R.id.action_Mentions:
                Intent mentions = new Intent(MainTab.this, Mentions.class);
                startActivity(mentions);
                return true;

            case R.id.add_user:
                Intent addUser = new Intent(MainTab.this, SearchActivity.class);
                startActivity(addUser);
                return true;

            case R.id.action_new_message:
                /*Intent message = new Intent(MainTab.this, FindUserMessageList.class);
                startActivity(message);*/
                Intent addUser2 = new Intent(MainTab.this, FindUserMessageList.class);
                startActivity(addUser2);
                return true;

            case R.id.action_pending_request:
                Intent pending = new Intent(MainTab.this, PendingRequests.class);
                startActivity(pending);
                return true;
        }
        //noinspection SimplifiableIfStatement
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    Home home = new Home();
                    return home;
                case 1:
                    Requests requests = new Requests();
                    return requests;
                case 2:
                    Message message = new Message();
                    return message;
                case 3:
                    Profile profile = new Profile();
                    return profile;
            }
            return null;
        }

        @Override
        public int getCount() {
            // Show 4 total pages.
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "HOME";
                case 1:
                    return "REQUESTS";
                case 2:
                    return "MESSAGING";
                case 3:
                    return "PROFILE";
            }
            return null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }
}
