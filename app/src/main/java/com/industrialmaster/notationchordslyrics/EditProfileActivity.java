package com.industrialmaster.notationchordslyrics;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.util.LruCache;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;

public class EditProfileActivity extends AppCompatActivity {

    private SharedPref sharedPref;
    private CustomNetworkImageView nivProfPic;
    private String uploadURL = "http://pasindud.tk/myapp/user/uploadPic.php";

    Bitmap bitmap;

    JSONObject jsonObject;
    RequestQueue rQueue;
    private final int IMG_REQUEST = 1;

    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPref = new SharedPref(getApplicationContext());

        if (sharedPref.getAuth()){
            setContentView(R.layout.activity_edit_profile);

            EditText etUsername = (EditText) findViewById(R.id.etEditUsername);
            EditText etEmail = (EditText) findViewById(R.id.etEditEmail);
            EditText etCurPass = (EditText) findViewById(R.id.etCurPass);
            EditText etNewPass = (EditText) findViewById(R.id.etNewPass);
            nivProfPic = (CustomNetworkImageView) findViewById(R.id.editProfPic);

            etUsername.setText(sharedPref.getUsername());
            etEmail.setText(sharedPref.getEmail());

            int i = R.drawable.user;

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


//        mRequestQueue = Volley.newRequestQueue(getApplicationContext());
//        mImageLoader = new ImageLoader(mRequestQueue, new ImageLoader.ImageCache() {
//            private final LruCache<String, Bitmap> mCache = new LruCache<String, Bitmap>(10);
//            public void putBitmap(String url, Bitmap bitmap) {
//                mCache.put(url, bitmap);
//            }
//            public Bitmap getBitmap(String url) {
//                return mCache.get(url);
//            }
//        });
    }

    public void selectImg(View v){
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

                    nivProfPic.setLocalImageBitmap(bitmap);
                    Toast.makeText(getApplication(), "Image Selected", Toast.LENGTH_SHORT).show();


                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(EditProfileActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
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

        rQueue = Volley.newRequestQueue(EditProfileActivity.this);
        rQueue.add(jsonObjectRequest);

    }

    private void  requestMultiplePermissions(){
        Dexter.withActivity(this)
                .withPermissions(

                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            Toast.makeText(getApplicationContext(), "All permissions are granted by user!", Toast.LENGTH_SHORT).show();
                        }

                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // show alert dialog navigating to Settings

                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).
                withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError error) {
                        Toast.makeText(getApplicationContext(), "Some Error! ", Toast.LENGTH_SHORT).show();
                    }
                })
                .onSameThread()
                .check();
    }

    public void saveChanges(View v){

        uploadImage(bitmap);


    }

}
