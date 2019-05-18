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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class EditPostActivity extends AppCompatActivity {

    Spinner postCat;
    ArrayAdapter<String> adapterPostCat;

    EditText etSongName;
    EditText etArtistName;
    EditText etNote;


    SharedPref sharedPref;

    CustomNetworkImageView cnivAdd;
    JSONObject jsonObject;
    RequestQueue rQueue;
    private final int IMG_REQUEST = 1;
    private String uploadURL = "http://pasindud.tk/myapp/posts/uploadPost.php";
    Bitmap bitmap;
    int lock;
    String imgname;

    boolean isImageUploaded;

    String songName;
    String artistName;
    String auhtorName;
    String category;
    String note;
    String imageUrl;
    int authorId;
    int id;
    int postId;

    ImageLoader mImageLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_edit_post);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Intent intent = getIntent();

        int pos;

        songName = intent.getStringExtra("songName");
        artistName = intent.getStringExtra("artistName");
        auhtorName = intent.getStringExtra("authorName"); //author = user in Here
        authorId = intent.getIntExtra("auhtorId", -1);
        category = intent.getStringExtra("category");
        imageUrl = intent.getStringExtra("imageurl");
        note = intent.getStringExtra("note");
        postId = intent.getIntExtra("id", -1);

        postCat = (Spinner) findViewById(R.id.spPostCat);
        String[] postCategoryList = {"-Select Categary-", "Notation", "Chords", "Lyrics"};
        adapterPostCat = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item, postCategoryList);
        postCat.setAdapter(adapterPostCat);


        etSongName = (EditText) findViewById(R.id.etSongName);
        etArtistName = (EditText) findViewById(R.id.etArtist);
        etNote = (EditText) findViewById(R.id.etNote);

        cnivAdd = (CustomNetworkImageView) findViewById(R.id.cnivAddImage);

        sharedPref = new SharedPref(getApplicationContext());

        final String url = imageUrl;
        // Instantiate the RequestQueue.
        mImageLoader = CustomVolleyRequestQueue.getInstance(this.getApplicationContext()).getImageLoader();
//        mImageLoader.get(url, ImageLoader.getImageListener(cnivAdd, R.drawable.loader, android.R.drawable.ic_dialog_alert));
//        cnivAdd.setImageUrl(url, mImageLoader);
        mImageLoader.get(url, new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                Toast.makeText(EditPostActivity.this, "I`m Here", Toast.LENGTH_SHORT).show();
                bitmap = response.getBitmap();
                cnivAdd.setLocalImageBitmap(bitmap);
                cnivAdd.setBackgroundResource(0);
            }

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final View view1 = view;

                RequestQueue queue = Volley.newRequestQueue(EditPostActivity.this);
//                Toast.makeText(this, String.valueOf(isImageUploaded), Toast.LENGTH_SHORT).show();


                StringRequest request = new StringRequest(
                        Request.Method.POST,
                        "http://pasindud.tk/myapp/posts/delete.php",
                        new Response.Listener<String>() {

                            @Override
                            public void onResponse(String response) {

//                                Toast.makeText(EditPostActivity.this, response, Toast.LENGTH_LONG).show();
//                                Snackbar.make(view1, "Replace with your own action", Snackbar.LENGTH_LONG)
//                                        .setAction("Action", null).show();
                                Intent intent = new Intent(EditPostActivity.this, MainActivity.class);
                                startActivity(intent);
                            }

                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(EditPostActivity.this, "ERROR", Toast.LENGTH_SHORT).show();
                            }
                        }
                ){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String>  params = new HashMap<>();

                        params.put("id", String.valueOf(postId));


                        return params;
                    }
                };

                queue.add(request);

            }
        });

        etSongName.setText(songName);
        etArtistName.setText(artistName);
        etNote.setText(note);

        if (category.equals("Notation")){
            pos = 1;
        }
        else if (category.equals("Chords")){
            pos = 2;
        }
        else if (category.equals("Lyrics")){
            pos = 3;
        }
        else{
            pos = 0;
        }

        postCat.setSelection(pos);



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
                    cnivAdd.setLocalImageBitmap(bitmap);
                    Toast.makeText(getApplication(), "Image Selected", Toast.LENGTH_SHORT).show();


                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(EditPostActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void uploadImage(Bitmap bitmap){

        Toast.makeText(this, "Image Uploading....", Toast.LENGTH_LONG).show();

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);

        String encodedImage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);

        try {
            jsonObject = new JSONObject();

            imgname = String.format("id%ssn%san%scat%s", String.valueOf(sharedPref.getId()), etSongName.getText().toString(), etArtistName.getText().toString(), postCat.getSelectedItem().toString());
            imgname = imgname.replaceAll("\\s", "");
            jsonObject.put("name", imgname);
//            Toast.makeText(this, imgname, Toast.LENGTH_LONG).show();
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
//                            Toast.makeText(EditPostActivity.this, message, Toast.LENGTH_LONG).show();
                            saveExtend();
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

        rQueue = Volley.newRequestQueue(EditPostActivity.this);
        rQueue.add(jsonObjectRequest);

    }

    public void saveChanges(View v){

        uploadImage(bitmap);


    }

    public void saveExtend(){

        final String songName = etSongName.getText().toString();
        final String artistName = etArtistName.getText().toString();
        final String note = etNote.getText().toString();
        final String postCategory = postCat.getSelectedItem().toString();


        RequestQueue queue = Volley.newRequestQueue(this);
//        Toast.makeText(this, String.valueOf(isImageUploaded), Toast.LENGTH_SHORT).show();


        StringRequest request = new StringRequest(
                Request.Method.POST,
                "http://pasindud.tk/myapp/posts/updatePost.php",
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
//                        Toast.makeText(EditPostActivity.this, response, Toast.LENGTH_LONG).show();
                            onBackPressed();
                    }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(EditPostActivity.this, "ERROR", Toast.LENGTH_SHORT).show();
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String>  params = new HashMap<>();
                params.put("songName", songName);
                params.put("artistName", artistName);
                params.put("note", note);
                params.put("category", postCategory);
                params.put("postId", String.valueOf(postId));
                params.put("imageurl", "http://pasindud.tk/myapp/posts/uploads/" + imgname + ".jpg");

                return params;
            }
        };

        queue.add(request);





    }

}
