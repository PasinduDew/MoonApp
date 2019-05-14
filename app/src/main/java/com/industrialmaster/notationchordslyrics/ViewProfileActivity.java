package com.industrialmaster.notationchordslyrics;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.NetworkImageView;

import de.hdodenhof.circleimageview.CircleImageView;

public class ViewProfileActivity extends AppCompatActivity {

    SharedPref sharedPref;
    private CircularNetworkImageView nivProfPic;
    private ImageLoader mImageLoader;

    ImageView btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPref = new SharedPref(getApplicationContext());
        btnBack = (ImageView) findViewById(R.id.btnBack);





        if (sharedPref.getAuth()){

            setContentView(R.layout.activity_view_profile);
//            Toolbar toolbar = findViewById(R.id.toolbar);
//            setSupportActionBar(toolbar);

            FloatingActionButton fab = findViewById(R.id.fabEditProfile);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                            .setAction("Action", null).show();

                    Intent intent = new Intent(getApplicationContext(), EditProfileActivity.class);
                    startActivity(intent);


                }
            });

            TextView tvProfUsername =(TextView) findViewById(R.id.profUsername);
            tvProfUsername.setText(sharedPref.getUsername());
            TextView tvProfEmail =(TextView) findViewById(R.id.profEmail);
            tvProfEmail.setText(sharedPref.getEmail());

            TextView tvProfNotCount =(TextView) findViewById(R.id.profNotationCount);
            TextView tvProfChordCount =(TextView) findViewById(R.id.profChordCount);
            TextView tvProfLyricCount =(TextView) findViewById(R.id.profLyricCount);

            ProgressBar pbConLvl = (ProgressBar) findViewById(R.id.profProgress);
//            ImageView profPic = (ImageView) findViewById(R.id.profPic);



            nivProfPic = (CircularNetworkImageView) findViewById(R.id.profPic);

            if (sharedPref.getImageValidity()){
                final String url = "http://pasindud.tk/myapp/user/profilePics/" + sharedPref.getId() + ".jpg";
                // Instantiate the RequestQueue.
                mImageLoader = CustomVolleyRequestQueue.getInstance(this.getApplicationContext()).getImageLoader();
                mImageLoader.get(url, ImageLoader.getImageListener(nivProfPic,
                        R.drawable.loader, android.R.drawable
                                .ic_dialog_alert));
                nivProfPic.setImageUrl(url, mImageLoader);
            }




        }


    }

    public  void back(View v){

        onBackPressed();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Instantiate the RequestQueue.
//        mImageLoader = CustomVolleyRequestQueue.getInstance(this.getApplicationContext()).getImageLoader();
//        //Image URL - This can point to any image file supported by Android
//        final String url = "http://pasindud.tk/myapp/user/profPics/" + sharedPref.getId() + ".jpg";
//
//        mImageLoader.get(url, ImageLoader.getImageListener(nivProfPic, R.drawable.user, android.R.drawable.ic_dialog_alert));
//        nivProfPic.setImageUrl(url, mImageLoader);


    }
}
