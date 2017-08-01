package com.example.jama.selfievselfie;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.algolia.instantsearch.helpers.Searcher;
import com.algolia.instantsearch.ui.InstantSearchHelper;

public class InstantSearchActivity extends AppCompatActivity {

    Searcher searcher;
    InstantSearchHelper helper;
    private static final String ALGOLIA_APP_ID = "CXR8DHPHLZ";
    private static final String ALGOLIA_SEARCH_API_KEY = "4286571d4648813b172ca5bbd0e63d94";
    private static final String ALGOLIA_INDEX_NAME = "test";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instant_search);

        searcher = new Searcher(ALGOLIA_APP_ID, ALGOLIA_SEARCH_API_KEY, ALGOLIA_INDEX_NAME);
        helper = new InstantSearchHelper(this, searcher);
        helper.search();

    }
}
