package com.industrialmaster.notationchordslyrics;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ViewPostActivity extends AppCompatActivity {

    TextView tvSongName, tvartistName, tvauthorName, tvNote;
    CustomNetworkImageView cnivContent;
    ImageView ivCategory;

    TextView tvLikeCount, tvDislikeCount;

    Button btnLike, btnDislike;
    Button btnAddToCollection;

    private ImageLoader mImageLoader;

    String songName;
    String artistName;
    String contributor;
    String category;
    String note;
    String imageUrl;
    int authorId;
    int likes;
    int disLikes;
    int id;
    int postId;

    int tempLike, tempDislike;
    boolean btnLiked, btnDisliked, btnState;

    Bitmap bitmap;


    SharedPref sharedPref;

    String operation, operationCollection;

    FloatingActionButton fabViewPost;

    AnimationDrawable anim;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_post);

        fabViewPost = (FloatingActionButton) findViewById(R.id.fabEditPost);



        sharedPref = new SharedPref(getApplicationContext());

        Intent intent = getIntent();
        postId = intent.getIntExtra("id", -1);
        songName = intent.getStringExtra("songName");
        artistName = intent.getStringExtra("artistName");
        contributor = intent.getStringExtra("authorName");
        category = intent.getStringExtra("category");
        note = intent.getStringExtra("note");
        imageUrl = intent.getStringExtra("imageurl");
        authorId = intent.getIntExtra("authorId", 0);
        likes = intent.getIntExtra("likes", 0);
        disLikes = intent.getIntExtra("dislikes", 0);
        id = intent.getIntExtra("id", -1);

//        Toast.makeText(this, songName, Toast.LENGTH_SHORT).show();

        tvSongName = (TextView) findViewById(R.id.tvSongName);
        tvartistName = (TextView) findViewById(R.id.tvArtistName);
        tvauthorName = (TextView) findViewById(R.id.tvPostAuthor);
        tvNote = (TextView) findViewById(R.id.tvPostNote);
        ivCategory = (ImageView) findViewById(R.id.cnivCatogary);
        cnivContent = (CustomNetworkImageView) findViewById(R.id.cnivContent);
        tvLikeCount = (TextView) findViewById(R.id.tvLikeCount);
        tvDislikeCount = (TextView) findViewById(R.id.tvDislikeCount);

        btnLike = (Button) findViewById(R.id.btnLike);
        btnDislike = (Button) findViewById(R.id.btnDisLike);
        btnAddToCollection = (Button) findViewById(R.id.btnAddToCollection);

        anim = (AnimationDrawable) cnivContent.getDrawable();


        tvSongName.setText(songName);
        tvartistName.setText(artistName);
        tvauthorName.setText(contributor);
        tvNote.setText(note);

        if (category.equals("Notation")){
            ivCategory.setImageResource(R.drawable.notaion_svg);
        }
        else if (category.equals("Chords")){
            ivCategory.setImageResource(R.drawable.chords);
        }
        else if (category.equals("Lyrics")){
            ivCategory.setImageResource(R.drawable.lyrics);
        }

        final String url = imageUrl;

//        Toast.makeText(this, imageUrl, Toast.LENGTH_LONG).show();
        // Instantiate the RequestQueue.
        anim.start();

        mImageLoader = CustomVolleyRequestQueue.getInstance(this.getApplicationContext()).getImageLoader();
        mImageLoader.get(url, ImageLoader.getImageListener(cnivContent,
                R.drawable.loading_animation, android.R.drawable
                        .ic_dialog_alert));
        cnivContent.setImageUrl(url, mImageLoader);
        anim.stop();




        tvLikeCount.setText(String.valueOf(likes));
        tvDislikeCount.setText(String.valueOf(disLikes));

        RequestQueue queue = Volley.newRequestQueue(this);

//            Toast.makeText(LoginActivity.this, "I`m Here " + email, Toast.LENGTH_SHORT).show();


        StringRequest request = new StringRequest(
                Request.Method.POST,
                "http://pasindud.tk/myapp/posts/isliked.php",
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
//                        Toast.makeText(ViewPostActivity.this, response, Toast.LENGTH_SHORT).show();
                        try{
                            JSONObject result = new JSONObject(response);

                            if (result.getString("status").equals("Liked")){
                                btnLike.setBackgroundResource(R.drawable.ic_liked);
                                btnDislike.setBackgroundResource(R.drawable.ic_dislike);
                                tempLike = 1;
                                tempDislike = 0;
                                btnLiked = true;
                                btnDisliked = false;
                                operation = "update";
                            }
                            else if (result.getString("status").equals("Disliked")){
                                btnDislike.setBackgroundResource(R.drawable.ic_disliked);
                                btnLike.setBackgroundResource(R.drawable.ic_like);
                                tempLike = 0;
                                tempDislike = 1;
                                btnLiked = false;
                                btnDisliked = true;
                                operation = "update";
                            }
                            else if (result.getString("status").equals("deleted")){
                                btnDislike.setBackgroundResource(R.drawable.ic_dislike);
                                btnLike.setBackgroundResource(R.drawable.ic_like);
                                tempLike = 0;
                                tempDislike = 0;
                                btnLiked = false;
                                btnDisliked = false;
                                operation = "update";

                            }
                            else{
                                operation = "insert";
                            }

//                            Toast.makeText(ViewPostActivity.this, "status " + result.getString("status"), Toast.LENGTH_SHORT).show();

                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }
//                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//                            startActivity(intent);
                    }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ViewPostActivity.this, "ERROR", Toast.LENGTH_SHORT).show();
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String>  params = new HashMap<>();
                params.put("userId", String.valueOf(sharedPref.getId()));
                params.put("postId", String.valueOf(postId));


                return params;
            }
        };

        queue.add(request);

        //Request To Chek Whether that Post is added to User`s Collection

        StringRequest request2 = new StringRequest(
                Request.Method.POST,
                "http://pasindud.tk/myapp/posts/isSaved.php",
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
//                        Toast.makeText(ViewPostActivity.this, response, Toast.LENGTH_SHORT).show();
                        try{
                            JSONObject result = new JSONObject(response);

                            if (result.getString("status").equals("true")){

                                btnAddToCollection.setBackgroundResource(R.drawable.ic_saved);
                                btnState = true;
                                operationCollection = "update";

                            }
                            else if (result.getString("status").equals("false")){
                                btnAddToCollection.setBackgroundResource(R.drawable.ic_plus_b);
                                btnState = false;
                                operationCollection = "update";
                            }
                            else if (result.getString("status").equals("null")){
                                btnAddToCollection.setBackgroundResource(R.drawable.ic_plus_b);
                                btnState = false;
                                operationCollection = "insert";

                            }
                            else{
                                operationCollection = "insert";
                            }

//                            Toast.makeText(ViewPostActivity.this, "status " + result.getString("status"), Toast.LENGTH_SHORT).show();

                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }
//                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//                            startActivity(intent);
                    }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ViewPostActivity.this, "ERROR", Toast.LENGTH_SHORT).show();
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String>  params = new HashMap<>();
                params.put("userId", String.valueOf(sharedPref.getId()));
                params.put("postId", String.valueOf(postId));


                return params;
            }
        };

        queue.add(request2);

        if (sharedPref.getId() != authorId){
            fabViewPost.hide();
        }


        fabViewPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                    Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                            .setAction("Action", null).show();

                Intent intent = new Intent(getApplicationContext(), EditPostActivity.class);
                intent.putExtra("songName", songName );
                intent.putExtra("artistName", artistName );
                intent.putExtra("authorName", contributor);
                intent.putExtra("authorId", authorId );
                intent.putExtra("note", note);
                intent.putExtra("category", category);
                intent.putExtra("imageurl", imageUrl );
                intent.putExtra("id", postId);
                startActivity(intent);


            }
        });


    }

    public  void clickedAddToCollection(View v){
        if (btnState){
            btnAddToCollection.setBackgroundResource(R.drawable.ic_plus_b);
            btnState = false;

        }
        else{
            btnAddToCollection.setBackgroundResource(R.drawable.ic_saved);
            btnState = true;

        }

        addToCollection();
    }

    public void addToCollection(){

//        Toast.makeText(ViewPostActivity.this, "I`m Here", Toast.LENGTH_SHORT).show();

        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest request = new StringRequest(
                Request.Method.POST,
                "http://pasindud.tk/myapp/posts/addToCollection.php",
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
//                        Toast.makeText(ViewPostActivity.this, response, Toast.LENGTH_SHORT).show();
                        try{
//                            JSONObject result = new JSONObject(response);


                            Toast.makeText(ViewPostActivity.this, response, Toast.LENGTH_SHORT).show();

                        }
                        catch (Exception e){
                            e.printStackTrace();
                            Log.e(">>>>>>>>>", e.toString());
                            Toast.makeText(ViewPostActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                        }
//                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//                            startActivity(intent);
                    }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ViewPostActivity.this, "ERROR", Toast.LENGTH_SHORT).show();
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String>  params = new HashMap<>();
                params.put("userId", String.valueOf(sharedPref.getId()));
                params.put("postId", String.valueOf(postId));
                params.put("operation", operationCollection);
                params.put("status", String.valueOf(btnState));


                return params;
            }
        };

        queue.add(request);




    }

    public void viewImage(View v){

        Intent intent = new Intent(getApplicationContext(), ViewImageActivity.class);

        intent.putExtra("imageUrl", imageUrl);

        startActivity(intent);



    }

    public void clickedLike(View v){

        if (!btnLiked){
            btnLike.setBackgroundResource(R.drawable.ic_liked);
            btnLiked = true;
            if(tempLike < 1){
                tempLike++;
                likes++;
                tvLikeCount.setText(String.valueOf(likes));

            }

            if (btnDisliked){
                btnDislike.setBackgroundResource(R.drawable.ic_dislike);
                btnDisliked = false;
                if(tempDislike == 1){
                    tempDislike--;
                    if (disLikes > 0){
                        disLikes--;
                    }

                    tvDislikeCount.setText(String.valueOf(disLikes));
                }
//                Toast.makeText(ViewPostActivity.this, String.valueOf(tempDislike), Toast.LENGTH_SHORT).show();
            }

//            Toast.makeText(ViewPostActivity.this, String.valueOf(tempLike), Toast.LENGTH_SHORT).show();

        }
        else {
            btnLike.setBackgroundResource(R.drawable.ic_like);
            btnLiked = false;
            if(tempLike == 1){
                tempLike--;
                if(likes > 0){
                    likes--;
                }
                tvLikeCount.setText(String.valueOf(likes));
            }


//            Toast.makeText(ViewPostActivity.this, String.valueOf(tempLike), Toast.LENGTH_SHORT).show();
        }






    }

    public void clickedDislike(View v){

        if (!btnDisliked){
            btnDislike.setBackgroundResource(R.drawable.ic_disliked);
            btnDisliked = true;
            if(tempDislike < 1){
                tempDislike++;
                disLikes++;
                tvDislikeCount.setText(String.valueOf(disLikes));
            }

            if (btnLiked){

                btnLike.setBackgroundResource(R.drawable.ic_like);
                btnLiked = false;
                if(tempLike == 1){
                    tempLike--;
                    if (likes > 0){
                        likes--;
                    }

                    tvLikeCount.setText(String.valueOf(likes));

                }

            }

//            Toast.makeText(ViewPostActivity.this, String.valueOf(tempDislike), Toast.LENGTH_SHORT).show();

        }
        else {
            btnDislike.setBackgroundResource(R.drawable.ic_dislike);
            btnDisliked = false;
            if(tempDislike == 1){
                tempDislike--;
                if (disLikes > 0){
                    disLikes--;
                }
                tvDislikeCount.setText(String.valueOf(disLikes));
            }
//            Toast.makeText(ViewPostActivity.this, String.valueOf(tempDislike), Toast.LENGTH_SHORT).show();
        }




    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        final String status;

//        Toast.makeText(ViewPostActivity.this, operation + tempDislike + tempLike, Toast.LENGTH_SHORT).show();

        if (!(operation.equals("insert") && tempLike == 0 && tempDislike == 0 )){

            if (tempLike == 1){
                status = "Liked";
            }
            else if (tempDislike == 1){
                status = "Disliked";
            }
            else{

                status = "deleted";
            }

//            Toast.makeText(ViewPostActivity.this, status, Toast.LENGTH_SHORT).show();

            RequestQueue queue = Volley.newRequestQueue(this);


            StringRequest request = new StringRequest(
                    Request.Method.POST,
                    "http://pasindud.tk/myapp/posts/updateLikeStatus.php",
                    new Response.Listener<String>() {

                        @Override
                        public void onResponse(String response) {
//                            Toast.makeText(ViewPostActivity.this, response, Toast.LENGTH_LONG).show();
//                            Intent intent = new Intent(AddPostActivity.this, MainActivity.class);
//                            startActivity(intent);
                        }

                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(ViewPostActivity.this, "ERROR", Toast.LENGTH_SHORT).show();
                        }
                    }
            ){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String>  params = new HashMap<>();
                    params.put("operation", operation);
                    params.put("status", status);
                    params.put("like", String.valueOf(likes));
                    params.put("dislike", String.valueOf(disLikes));
                    params.put("user_id", String.valueOf(sharedPref.getId()));
                    params.put("post_id", String.valueOf(id));


                    return params;
                }
            };

            queue.add(request);


        }



    }




}
