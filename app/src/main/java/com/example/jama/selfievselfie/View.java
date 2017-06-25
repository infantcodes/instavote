package com.example.jama.selfievselfie;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 * Created by JAMA on 4/16/2017.
 */

public class View extends Activity {

    String Image;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_layout);

        Bundle bundle = getIntent().getExtras();
        Image = bundle.getString("image");

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int width = displayMetrics.widthPixels;
        int height  = displayMetrics.heightPixels;

        getWindow().setLayout((int) (width*.8), (int) (height*.9));

        ImageView imageView = (ImageView) findViewById(R.id.imageViewView);
        Picasso.with(View.this).load(Image).fit().into(imageView);
        imageView.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                finish();
            }
        });
    }
}
