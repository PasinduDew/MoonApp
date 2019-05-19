package com.industrialmaster.notationchordslyrics;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class ViewProfileActivity extends AppCompatActivity {

    SharedPref sharedPref;
    private CircularNetworkImageView nivProfPic;
    private ImageLoader mImageLoader;

    ImageView btnBack;

    private String uploadURL = "http://pasindud.tk/myapp/user/uploadPic.php";

    Bitmap bitmap;

    JSONObject jsonObject;
    RequestQueue rQueue;
    private final int IMG_REQUEST = 1;

    private RequestQueue mRequestQueue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPref = new SharedPref(getApplicationContext());
        btnBack = (ImageView) findViewById(R.id.btnBack);





        if (sharedPref.getAuth()){

            setContentView(R.layout.activity_view_profile);
//            Toolbar toolbar = findViewById(R.id.toolbar);
//            setSupportActionBar(toolbar);

//            FloatingActionButton fab = findViewById(R.id.fabEditProfile);
//            fab.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
////                    Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
////                            .setAction("Action", null).show();
//
//                    Intent intent = new Intent(getApplicationContext(), EditProfileActivity.class);
//                    startActivity(intent);
//
//
//                }
//            });

            TextView tvProfUsername =(TextView) findViewById(R.id.tvProfUsername);
            tvProfUsername.setText(sharedPref.getUsername());
            TextView tvProfEmail =(TextView) findViewById(R.id.tvProfEmail);
            tvProfEmail.setText(sharedPref.getEmail());




//            ImageView profPic = (ImageView) findViewById(R.id.profPic);



            nivProfPic = (CircularNetworkImageView) findViewById(R.id.profPic);

            if (sharedPref.getImageValidity()){
                final String url = "http://pasindud.tk/myapp/user/profilePics/" + sharedPref.getId() + ".jpg";
                // Instantiate the RequestQueue.
                mImageLoader = CustomVolleyRequestQueue.getInstance(this.getApplicationContext()).getImageLoader();
//                mImageLoader.get(url, ImageLoader.getImageListener(nivProfPic,
//                        R.drawable.loader, R.drawable.user));
//                nivProfPic.setImageUrl(url, mImageLoader);

                mImageLoader.get(url, new ImageLoader.ImageListener() {
                    @Override
                    public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
//                        Toast.makeText(ViewProfileActivity.this, "I`m Here", Toast.LENGTH_SHORT).show();
                        bitmap = response.getBitmap();
                        nivProfPic.setImageBitmap(bitmap);
//                        nivProfPic.setBackgroundResource(0);
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        nivProfPic.setImageDrawable(getResources().getDrawable(R.drawable.user));
                    }
                });
            }




        }


    }

    public void editProfPic(View v){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMG_REQUEST);

        /*Another Method*/

//        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
//                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//            startActivityForResult(intent, IMG_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == this.RESULT_CANCELED) {
            return;
        }
        if (requestCode == IMG_REQUEST) {
            if (data != null) {
                Uri contentURI = data.getData();
                try {

                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);

                    nivProfPic.setImageBitmap(bitmap);
//                    Toast.makeText(getApplication(), "Image Selected", Toast.LENGTH_SHORT).show();


                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(ViewProfileActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void uploadImage(Bitmap bitmap){

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);

        String encodedImage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);

        try {
            jsonObject = new JSONObject();

            String imgname = String.valueOf(sharedPref.getId());
            jsonObject.put("name", imgname);
            //  Log.e("Image name", etxtUpload.getText().toString().trim());
            jsonObject.put("image", encodedImage);
            // jsonObject.put("aa", "aa");
        } catch (JSONException e) {
            Log.e("JSONObject Here", e.toString());
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, uploadURL, jsonObject,
                new Response.Listener<JSONObject>(){
                    @Override
                    public void onResponse(JSONObject jsonObject) {

                        try {
                            final String message = jsonObject.getString("message");
//                            Log.d(">>>>>>>>>>>>>>", message);
                            Toast.makeText(getApplication(), message, Toast.LENGTH_LONG).show();
                            rQueue.getCache().clear();

                        }
                        catch (Exception e){
                            e.printStackTrace();

                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(getApplication(), volleyError.toString(), Toast.LENGTH_LONG).show();
                Log.e("aaaaaaa", volleyError.toString());

            }
        });

        rQueue = Volley.newRequestQueue(ViewProfileActivity.this);
        rQueue.add(jsonObjectRequest);

    }
    public void editPassword(View v){
        Intent intent = new Intent(this, EditProfileActivity.class);
        intent.putExtra("edit", "password");
        startActivity(intent);

    }

    public void editEmail(View v){
        Intent intent = new Intent(this, EditProfileActivity.class);
        intent.putExtra("edit", "email");
        startActivity(intent);

    }

    public void viewMyPosts(View v){

        Intent intent = new Intent(this, MyPostsActivity.class);
        startActivity(intent);
    }

    public  void back(View v){

        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        uploadImage(bitmap);
    }

    public void editUsername(View v){

        Intent intent = new Intent(this, EditProfileActivity.class);
        intent.putExtra("edit", "username");
        startActivity(intent);
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
