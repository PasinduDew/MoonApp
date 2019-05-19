package com.industrialmaster.notationchordslyrics;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.TokenWatcher;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.github.chrisbanes.photoview.PhotoView;


public class ViewImageActivity extends AppCompatActivity {

    ImageLoader mImageLoader;
    PhotoView pvImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);

        Intent intent = getIntent();

        String imageUrl = intent.getStringExtra("imageUrl");

        pvImage = (PhotoView) findViewById(R.id.pvImage);



        mImageLoader = CustomVolleyRequestQueue.getInstance(this.getApplicationContext()).getImageLoader();
        mImageLoader.get(imageUrl, ImageLoader.getImageListener(pvImage,
                R.drawable.loading_animation, android.R.drawable
                        .ic_dialog_alert));


        mImageLoader.get(imageUrl, new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                Drawable drawable = new BitmapDrawable(getResources(), response.getBitmap());

                pvImage.setImageDrawable(drawable);

            }

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });


    }

    public void test(View v){

//        Toast.makeText(this, "Ayubowan", Toast.LENGTH_SHORT).show();
    }
}
