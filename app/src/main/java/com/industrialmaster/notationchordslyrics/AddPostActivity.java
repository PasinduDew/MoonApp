package com.industrialmaster.notationchordslyrics;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AddPostActivity extends AppCompatActivity {
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        postCat = (Spinner) findViewById(R.id.spPostCat);
        String[] postCategoryList = {"-Select Categary-", "Notation", "Chords", "Lyrics"};
        adapterPostCat = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item, postCategoryList);
        postCat.setAdapter(adapterPostCat);

        etSongName = (EditText) findViewById(R.id.etSongName);
        etArtistName = (EditText) findViewById(R.id.etArtist);
        etNote = (EditText) findViewById(R.id.etNote);

        cnivAdd = (CustomNetworkImageView) findViewById(R.id.cnivAddImage);

        sharedPref = new SharedPref(getApplicationContext());

    }

    @Override
    protected void onStart() {
        super.onStart();
        lock = 0;
    }

    public void save(View v){
        if(etSongName.getText().toString().equals("")){
            etSongName.setError("Enter the Song Name");

        }
        else if(etSongName.getText().toString().length() < 3){
            etSongName.setError("Song Name should be minimum of 3 characters long.");
        }
        else if(etArtistName.getText().toString().equals("")){
            etArtistName.setError("Enter the Artist Name");
//            Toast.makeText(LoginActivity.this, "Please Enter the Email", Toast.LENGTH_SHORT).show();
        }
        else if(etArtistName.getText().toString().length() < 3){
            etArtistName.setError("Artist Name should be minimum of 3 characters long.");
        }
        else if(postCat.getSelectedItem().toString().equals("-Select Category-")){
            Toast.makeText(this, "Select Category", Toast.LENGTH_SHORT).show();
        }
        else{

            uploadImage(bitmap);


        }

    }

    public void selectImg(View v) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMG_REQUEST);
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

                    cnivAdd.setBackgroundResource(0);

                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);

                    cnivAdd.setLocalImageBitmap(bitmap);
//                    Toast.makeText(getApplication(), "Image Selected", Toast.LENGTH_SHORT).show();
//                    lock = 1;

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(AddPostActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public void saveExtend(){

        final String songName = etSongName.getText().toString();
        final String artistName = etArtistName.getText().toString();
        final String note = etNote.getText().toString();
        final String postCategory = postCat.getSelectedItem().toString();

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("yyyy / MM / dd ");
        final String strDate = mdformat.format(calendar.getTime());

        RequestQueue queue = Volley.newRequestQueue(this);
//        Toast.makeText(this, String.valueOf(isImageUploaded), Toast.LENGTH_SHORT).show();


        StringRequest request = new StringRequest(
                Request.Method.POST,
                "http://pasindud.tk/myapp/posts/add.php",
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(AddPostActivity.this, "Thank You :)", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(AddPostActivity.this, MainActivity.class);
                        startActivity(intent);
                    }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(AddPostActivity.this, "ERROR", Toast.LENGTH_SHORT).show();
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
                params.put("authorId", String.valueOf(sharedPref.getId()));
                params.put("date", strDate);
                params.put("authorName", sharedPref.getUsername());
                params.put("imageurl", "http://pasindud.tk/myapp/posts/uploads/" + imgname + ".jpg");

                return params;
            }
        };

        queue.add(request);





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
//                            Toast.makeText(AddPostActivity.this, message, Toast.LENGTH_LONG).show();
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

        rQueue = Volley.newRequestQueue(AddPostActivity.this);
        rQueue.add(jsonObjectRequest);

    }


}
