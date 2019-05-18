package com.industrialmaster.notationchordslyrics;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.AnimatedImageDrawable;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.SearchView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    SharedPref sharedPref;
    CircularNetworkImageView nav_userImg;
    private ImageLoader mImageLoader;

    ImageView btnHam;

    private static ProgressDialog mProgressDialog;
    private ListView listView;
    ArrayList<DataModel> dataModelArrayList;
    private ListAdapter listAdapter;

    SearchView.SearchAutoComplete searchAutoComplete;

    LinearLayout llEmpty;

    FloatingActionButton fab;

    String[] songName;
    String[] artistName;
    String[] imageUrl;
    String[] authorName;
    String[] note;
    int[] authorId;
    int[] likes, dislikes;
    String[] category;
    int[] postId;

    int menuItem;
    boolean btnHamState;
    boolean btnSearchState;

    ImageView ivLoader;

    AutoCompleteTextView actvSearch;
    LinearLayout llSearch;
    TextView tvMoon;

    DrawerLayout drawer;

    String[] searchTags;

    AnimationDrawable anim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPref = new SharedPref(getApplicationContext());
        listView = findViewById(R.id.lvPosts);
        btnHam = (ImageView) findViewById(R.id.btnHam);
        btnHamState = false;

        actvSearch = (AutoCompleteTextView) findViewById(R.id.actvSearch);
        llSearch = (LinearLayout) findViewById(R.id.llSearch);
        tvMoon = (TextView) findViewById(R.id.tvMoon);
        actvSearch.setVisibility(View.GONE);

        btnSearchState = false;

        menuItem = 0;


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = (FloatingActionButton) findViewById(R.id.fab_main);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), AddPostActivity.class);
                startActivity(intent);
            }
        });



        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close){

//            Event When the Navigation Drawer is Opened
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
//                Toast.makeText(getApplicationContext(), "I`m Here", Toast.LENGTH_SHORT).show();
                TextView navUsername = (TextView) findViewById(R.id.nav_username);
                navUsername.setText("Hi! " + sharedPref.getUsername().toString());
//                TextView navEmail = (TextView) findViewById(R.id.nav_email);
//                navEmail.setText(sharedPref.getEmail().toString());
                nav_userImg = (CircularNetworkImageView) findViewById(R.id.nav_userImg);

                anim = (AnimationDrawable) nav_userImg.getBackground();
                anim.start();


                if (sharedPref.getImageValidity()) {

                    final String url = "http://pasindud.tk/myapp/user/profilePics/" + sharedPref.getId() + ".jpg";
                    // Instantiate the RequestQueue.
                    mImageLoader = CustomVolleyRequestQueue.getInstance(MainActivity.this).getImageLoader();
                    mImageLoader.get(url, ImageLoader.getImageListener(nav_userImg, R.drawable.loading_animation, R.drawable.user));
                    nav_userImg.setImageUrl(url, mImageLoader);

                    anim.stop();


                }
                else{

                }
            }
        };
        drawer.addDrawerListener(toggle);


        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);








    }



    public  void onClickHam(View v){

        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }
        else {
            drawer.openDrawer(GravityCompat.START);
        }
    }

    public  void onClickSearch(View v){

        if(btnSearchState){
            actvSearch.setVisibility(View.GONE);
            tvMoon.setVisibility(View.VISIBLE);
            btnSearchState = false;
            if (!(actvSearch.getText().toString().equals(""))) {
                onResume();
            }

        }
        else {
            actvSearch.setVisibility(View.VISIBLE);
            tvMoon.setVisibility(View.GONE);
            btnSearchState = true;
            actvSearch.setText("");

            actvSearch.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
//                    Toast.makeText(MainActivity.this, s.toString(), Toast.LENGTH_SHORT).show();
                    if (!(actvSearch.getText().toString().equals(""))){

                        searchBySongName(s.toString());
                    }

                }

                @Override
                public void afterTextChanged(Editable s) {

                    if (actvSearch.getText().toString().equals("")){

                        onResume();
                    }
                    else {

                        searchBySongName(s.toString());
                    }


                }
            });


        }
    }

    public void searchBySongName(final String songname){

        String url = "http://pasindud.tk/myapp/posts/searchSongByName.php";

//        showSimpleProgressDialog(this, "Loading...","Fetching Json",false);

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        String tempStr = actvSearch.getText().toString();

                        if (response.equals("[]") && !(tempStr.equals(""))){
//                            Toast.makeText(MainActivity.this, "No Matching Found!", Toast.LENGTH_SHORT).show();
                            listView.setVisibility(View.GONE);
//                            onResume();

                        }
                        else if (response.equals("[]") && tempStr.equals("")){
//                            Toast.makeText(MainActivity.this, "Second Part", Toast.LENGTH_SHORT).show();
                            onResume();

                        }
                        else {
                            listView.setVisibility(View.VISIBLE);
                            try {

                                JSONArray jsonArray = new JSONArray(response);

                                songName =new String[jsonArray.length()];
                                artistName=new String[jsonArray.length()];
                                authorName =new String[jsonArray.length()];
                                authorId =new int[jsonArray.length()];
                                note =new String[jsonArray.length()];
                                imageUrl =new String[jsonArray.length()];
                                likes =new int[jsonArray.length()];
                                dislikes =new int[jsonArray.length()];
                                category =new String[jsonArray.length()];
                                postId = new int[jsonArray.length()];
//                            JSONObject obj = new JSONObject(jsonArray);
                                dataModelArrayList = new ArrayList<>();
                                /* JSONArray dataArray  = obj.getJSONArray("data"); */

//                            Toast.makeText(MainActivity.this, jsonArray.toString(), Toast.LENGTH_LONG).show();
                                for (int i = 0; i < jsonArray.length(); i++) {

                                    DataModel postModel = new DataModel();
                                    JSONObject dataobj = jsonArray.getJSONObject(i);

//                                Toast.makeText(MainActivity.this, dataobj.getString("song_name"), Toast.LENGTH_LONG).show();

                                    songName[i] = dataobj.getString("song_name");
                                    artistName[i] = dataobj.getString("song_artist");
                                    authorName[i] = dataobj.getString("author_name");
                                    category[i] = dataobj.getString("category");
                                    postId[i] = dataobj.getInt("id");
                                    authorId[i] = dataobj.getInt("author_id");
                                    imageUrl[i] = dataobj.getString("imageurl");
                                    likes[i] = dataobj.getInt("likes");
                                    dislikes[i] = dataobj.getInt("dislikes");
                                    note[i] = dataobj.getString("note");

                                    postModel.setSongName(songName[i]);
                                    postModel.setArtistName(artistName[i]);
                                    postModel.setContributor(authorName[i]);
                                    postModel.setLikes(likes[i]);
                                    postModel.setDislikes(dislikes[i]);

                                    if(dataobj.getString("category").equals("Notation")){
                                        postModel.setImgURL("http://pasindud.tk/myapp/posts/category/notation.png");
                                    }
                                    else if(dataobj.getString("category").equals("Chords")){
                                        postModel.setImgURL("http://pasindud.tk/myapp/posts/category/chords.png");
                                    }
                                    else if(dataobj.getString("category").equals("Lyrics")){
                                        postModel.setImgURL("http://pasindud.tk/myapp/posts/category/lyrics.png");
                                    }
                                    else{

                                    }

//                                postModel.setImgURL(dataobj.getString("imgurl"));


//                                Toast.makeText(MainActivity.this, ">>>>>>>>>>>>", Toast.LENGTH_SHORT).show();
                                    dataModelArrayList.add(postModel);


                                }


                                setupListview();



                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //displaying the error in toast if occurrs
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }){

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("songName", songname);

                return params;
            }


        };

        // request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        requestQueue.add(stringRequest);



    }



    private void retrieveAll() {

        String url = "http://pasindud.tk/myapp/posts/list.php";

        showSimpleProgressDialog(this, "","Loading...",false);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        Log.d("strrrrr", ">>" + response);


                        try {

                            songName =new String[response.length()];
                            artistName=new String[response.length()];
                            authorName =new String[response.length()];
                            authorId =new int[response.length()];
                            note =new String[response.length()];
                            imageUrl =new String[response.length()];
                            likes =new int[response.length()];
                            dislikes =new int[response.length()];
                            category =new String[response.length()];
                            postId = new int[response.length()];
//                            JSONObject obj = new JSONObject(response);
                            dataModelArrayList = new ArrayList<>();
                            /* JSONArray dataArray  = obj.getJSONArray("data"); */

//                            Toast.makeText(MainActivity.this, response.toString(), Toast.LENGTH_LONG).show();
                            for (int i = 0; i < response.length(); i++) {

                                DataModel postModel = new DataModel();
                                JSONObject dataobj = response.getJSONObject(i);

//                                Toast.makeText(MainActivity.this, dataobj.getString("song_name"), Toast.LENGTH_LONG).show();

                                songName[i] = dataobj.getString("song_name");
                                artistName[i] = dataobj.getString("song_artist");
                                authorName[i] = dataobj.getString("author_name");
                                category[i] = dataobj.getString("category");
                                postId[i] = dataobj.getInt("id");
                                authorId[i] = dataobj.getInt("author_id");
                                imageUrl[i] = dataobj.getString("imageurl");
                                likes[i] = dataobj.getInt("likes");
                                dislikes[i] = dataobj.getInt("dislikes");
                                note[i] = dataobj.getString("note");


                                postModel.setSongName(songName[i]);
                                postModel.setArtistName(artistName[i]);
                                postModel.setContributor(authorName[i]);
                                postModel.setLikes(likes[i]);
                                postModel.setDislikes(dislikes[i]);

                                if(dataobj.getString("category").equals("Notation")){
                                    postModel.setImgURL("http://pasindud.tk/myapp/posts/category/notation.png");
                                }
                                else if(dataobj.getString("category").equals("Chords")){
                                    postModel.setImgURL("http://pasindud.tk/myapp/posts/category/chords.png");
                                }
                                else if(dataobj.getString("category").equals("Lyrics")){
                                    postModel.setImgURL("http://pasindud.tk/myapp/posts/category/lyrics.png");
                                }
                                else{

                                }

//                                if (category.equals("Notation")){
//                                    ivCategory
//                                }
//                                else if (category.equals("Chords")){
//                                    ivCategory.setImageResource(R.drawable.chords);
//                                }
//                                else if (category.equals("Lyrics")){
//                                    ivCategory.setImageResource(R.drawable.lyrics);
//                                }

//                                postModel.setImgURL(dataobj.getString("imgurl"));


//                                Toast.makeText(MainActivity.this, ">>>>>>>>>>>>", Toast.LENGTH_SHORT).show();
                                dataModelArrayList.add(postModel);


                            }


                            setupListview();

                            searchTagFill();



                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //displaying the error in toast if occurrs
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        // request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        requestQueue.add(jsonArrayRequest);


    }

    private void retrieveCollection() {

        String url = "http://pasindud.tk/myapp/posts/collection.php";

        showSimpleProgressDialog(this, "Loading...","Fetching Json",false);

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.d("strrrrr", ">>" + response);


                        try {

                            JSONArray jsonArray = new JSONArray(response);

                            songName =new String[jsonArray.length()];
                            artistName=new String[jsonArray.length()];
                            authorName =new String[jsonArray.length()];
                            authorId =new int[jsonArray.length()];
                            note =new String[jsonArray.length()];
                            imageUrl =new String[jsonArray.length()];
                            likes =new int[jsonArray.length()];
                            dislikes =new int[jsonArray.length()];
                            category =new String[jsonArray.length()];
                            postId = new int[jsonArray.length()];
//                            JSONObject obj = new JSONObject(jsonArray);
                            dataModelArrayList = new ArrayList<>();
                            /* JSONArray dataArray  = obj.getJSONArray("data"); */

//                            Toast.makeText(MainActivity.this, jsonArray.toString(), Toast.LENGTH_LONG).show();
                            for (int i = 0; i < jsonArray.length(); i++) {

                                DataModel postModel = new DataModel();
                                JSONObject dataobj = jsonArray.getJSONObject(i);

//                                Toast.makeText(MainActivity.this, dataobj.getString("song_name"), Toast.LENGTH_LONG).show();

                                songName[i] = dataobj.getString("song_name");
                                artistName[i] = dataobj.getString("song_artist");
                                authorName[i] = dataobj.getString("author_name");
                                category[i] = dataobj.getString("category");
                                postId[i] = dataobj.getInt("id");
                                authorId[i] = dataobj.getInt("author_id");
                                imageUrl[i] = dataobj.getString("imageurl");
                                likes[i] = dataobj.getInt("likes");
                                dislikes[i] = dataobj.getInt("dislikes");
                                note[i] = dataobj.getString("note");

                                postModel.setSongName(songName[i]);
                                postModel.setArtistName(artistName[i]);
                                postModel.setContributor(authorName[i]);
                                postModel.setLikes(likes[i]);
                                postModel.setDislikes(dislikes[i]);

                                if(dataobj.getString("category").equals("Notation")){
                                    postModel.setImgURL("http://pasindud.tk/myapp/posts/category/notation.png");
                                }
                                else if(dataobj.getString("category").equals("Chords")){
                                    postModel.setImgURL("http://pasindud.tk/myapp/posts/category/chords.png");
                                }
                                else if(dataobj.getString("category").equals("Lyrics")){
                                    postModel.setImgURL("http://pasindud.tk/myapp/posts/category/lyrics.png");
                                }
                                else{

                                }

//                                postModel.setImgURL(dataobj.getString("imgurl"));


//                                Toast.makeText(MainActivity.this, ">>>>>>>>>>>>", Toast.LENGTH_SHORT).show();
                                dataModelArrayList.add(postModel);


                            }


                            setupListview();



                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //displaying the error in toast if occurrs
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }){

                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put("userId", String.valueOf(sharedPref.getId()));

                        return params;
                    }


        };

        // request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        requestQueue.add(stringRequest);


    }

    private void retrieveNotations() {

        String url = "http://pasindud.tk/myapp/posts/notations.php";

        showSimpleProgressDialog(this, "Loading...","Fetching Json",false);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        Log.d("strrrrr", ">>" + response);


                        try {

                            songName =new String[response.length()];
                            artistName=new String[response.length()];
                            authorName =new String[response.length()];
                            authorId =new int[response.length()];
                            note =new String[response.length()];
                            imageUrl =new String[response.length()];
                            likes =new int[response.length()];
                            dislikes =new int[response.length()];
                            category =new String[response.length()];
                            postId = new int[response.length()];
//                            JSONObject obj = new JSONObject(response);
                            dataModelArrayList = new ArrayList<>();
                            /* JSONArray dataArray  = obj.getJSONArray("data"); */

//                            Toast.makeText(MainActivity.this, response.toString(), Toast.LENGTH_LONG).show();
                            for (int i = 0; i < response.length(); i++) {

                                DataModel postModel = new DataModel();
                                JSONObject dataobj = response.getJSONObject(i);

//                                Toast.makeText(MainActivity.this, dataobj.getString("song_name"), Toast.LENGTH_LONG).show();

                                songName[i] = dataobj.getString("song_name");
                                artistName[i] = dataobj.getString("song_artist");
                                authorName[i] = dataobj.getString("author_name");
                                category[i] = dataobj.getString("category");
                                postId[i] = dataobj.getInt("id");
                                authorId[i] = dataobj.getInt("author_id");
                                imageUrl[i] = dataobj.getString("imageurl");
                                likes[i] = dataobj.getInt("likes");
                                dislikes[i] = dataobj.getInt("dislikes");
                                note[i] = dataobj.getString("note");

                                postModel.setSongName(songName[i]);
                                postModel.setArtistName(artistName[i]);
                                postModel.setContributor(authorName[i]);
                                postModel.setLikes(likes[i]);
                                postModel.setDislikes(dislikes[i]);

                                if(dataobj.getString("category").equals("Notation")){
                                    postModel.setImgURL("http://pasindud.tk/myapp/posts/category/notation.png");
                                }
                                else if(dataobj.getString("category").equals("Chords")){
                                    postModel.setImgURL("http://pasindud.tk/myapp/posts/category/chords.png");
                                }
                                else if(dataobj.getString("category").equals("Lyrics")){
                                    postModel.setImgURL("http://pasindud.tk/myapp/posts/category/lyrics.png");
                                }
                                else{

                                }

//                                postModel.setImgURL(dataobj.getString("imgurl"));


//                                Toast.makeText(MainActivity.this, ">>>>>>>>>>>>", Toast.LENGTH_SHORT).show();
                                dataModelArrayList.add(postModel);


                            }


                            setupListview();



                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //displaying the error in toast if occurrs
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        // request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        requestQueue.add(jsonArrayRequest);


    }

    private void retrieveChords() {

        String url = "http://pasindud.tk/myapp/posts/chords.php";

        showSimpleProgressDialog(this, "Loading...","Fetching Json",false);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        Log.d("strrrrr", ">>" + response);


                        try {

                            songName =new String[response.length()];
                            artistName=new String[response.length()];
                            authorName =new String[response.length()];
                            authorId =new int[response.length()];
                            note =new String[response.length()];
                            imageUrl =new String[response.length()];
                            likes =new int[response.length()];
                            dislikes =new int[response.length()];
                            category =new String[response.length()];
                            postId = new int[response.length()];
//                            JSONObject obj = new JSONObject(response);
                            dataModelArrayList = new ArrayList<>();
                            /* JSONArray dataArray  = obj.getJSONArray("data"); */

//                            Toast.makeText(MainActivity.this, response.toString(), Toast.LENGTH_LONG).show();
                            for (int i = 0; i < response.length(); i++) {

                                DataModel postModel = new DataModel();
                                JSONObject dataobj = response.getJSONObject(i);

//                                Toast.makeText(MainActivity.this, dataobj.getString("song_name"), Toast.LENGTH_LONG).show();

                                songName[i] = dataobj.getString("song_name");
                                artistName[i] = dataobj.getString("song_artist");
                                authorName[i] = dataobj.getString("author_name");
                                category[i] = dataobj.getString("category");
                                postId[i] = dataobj.getInt("id");
                                authorId[i] = dataobj.getInt("author_id");
                                imageUrl[i] = dataobj.getString("imageurl");
                                likes[i] = dataobj.getInt("likes");
                                dislikes[i] = dataobj.getInt("dislikes");
                                note[i] = dataobj.getString("note");

                                postModel.setSongName(songName[i]);
                                postModel.setArtistName(artistName[i]);
                                postModel.setContributor(authorName[i]);
                                postModel.setLikes(likes[i]);
                                postModel.setDislikes(dislikes[i]);

                                if(dataobj.getString("category").equals("Notation")){
                                    postModel.setImgURL("http://pasindud.tk/myapp/posts/category/notation.png");
                                }
                                else if(dataobj.getString("category").equals("Chords")){
                                    postModel.setImgURL("http://pasindud.tk/myapp/posts/category/chords.png");
                                }
                                else if(dataobj.getString("category").equals("Lyrics")){
                                    postModel.setImgURL("http://pasindud.tk/myapp/posts/category/lyrics.png");
                                }
                                else{

                                }

//                                postModel.setImgURL(dataobj.getString("imgurl"));


//                                Toast.makeText(MainActivity.this, ">>>>>>>>>>>>", Toast.LENGTH_SHORT).show();
                                dataModelArrayList.add(postModel);


                            }


                            setupListview();



                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //displaying the error in toast if occurrs
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        // request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        requestQueue.add(jsonArrayRequest);


    }

    private void retrieveLyrics() {

        String url = "http://pasindud.tk/myapp/posts/lyrics.php";

        showSimpleProgressDialog(this, "Loading...","Fetching Json",false);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        Log.d("strrrrr", ">>" + response);


                        try {

                            songName =new String[response.length()];
                            artistName=new String[response.length()];
                            authorName =new String[response.length()];
                            authorId =new int[response.length()];
                            note =new String[response.length()];
                            imageUrl =new String[response.length()];
                            likes =new int[response.length()];
                            dislikes =new int[response.length()];
                            category =new String[response.length()];
                            postId = new int[response.length()];
//                            JSONObject obj = new JSONObject(response);
                            dataModelArrayList = new ArrayList<>();
                            /* JSONArray dataArray  = obj.getJSONArray("data"); */

//                            Toast.makeText(MainActivity.this, response.toString(), Toast.LENGTH_LONG).show();
                            for (int i = 0; i < response.length(); i++) {

                                DataModel postModel = new DataModel();
                                JSONObject dataobj = response.getJSONObject(i);

//                                Toast.makeText(MainActivity.this, dataobj.getString("song_name"), Toast.LENGTH_LONG).show();

                                songName[i] = dataobj.getString("song_name");
                                artistName[i] = dataobj.getString("song_artist");
                                authorName[i] = dataobj.getString("author_name");
                                category[i] = dataobj.getString("category");
                                postId[i] = dataobj.getInt("id");
                                authorId[i] = dataobj.getInt("author_id");
                                imageUrl[i] = dataobj.getString("imageurl");
                                likes[i] = dataobj.getInt("likes");
                                dislikes[i] = dataobj.getInt("dislikes");
                                note[i] = dataobj.getString("note");

                                postModel.setSongName(songName[i]);
                                postModel.setArtistName(artistName[i]);
                                postModel.setContributor(authorName[i]);
                                postModel.setLikes(likes[i]);
                                postModel.setDislikes(dislikes[i]);

                                if(dataobj.getString("category").equals("Notation")){
                                    postModel.setImgURL("http://pasindud.tk/myapp/posts/category/notation.png");
                                }
                                else if(dataobj.getString("category").equals("Chords")){
                                    postModel.setImgURL("http://pasindud.tk/myapp/posts/category/chords.png");
                                }
                                else if(dataobj.getString("category").equals("Lyrics")){
                                    postModel.setImgURL("http://pasindud.tk/myapp/posts/category/lyrics.png");
                                }
                                else{

                                }

//                                postModel.setImgURL(dataobj.getString("imgurl"));


//                                Toast.makeText(MainActivity.this, ">>>>>>>>>>>>", Toast.LENGTH_SHORT).show();
                                dataModelArrayList.add(postModel);


                            }


                            setupListview();



                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //displaying the error in toast if occurrs
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        // request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        requestQueue.add(jsonArrayRequest);


    }

    private void setupListview(){
        removeSimpleProgressDialog();  //will remove progress dialog
        listAdapter = new ListAdapter(this, dataModelArrayList);

        try{
            listView.setAdapter(listAdapter);
        }
        catch (Exception e){
            fab.hide();
            listView.setVisibility(View.GONE);
            llEmpty = (LinearLayout) findViewById(R.id.llEmptyNote);
            llEmpty.setVisibility(View.VISIBLE);
            Toast.makeText(this, "Add Your Favorites to the Collection.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

//                TextView tvTest = (TextView) view.findViewById(R.id.tvSongName);
//                String test = tvTest.getText().toString();
//                Toast.makeText(MainActivity.this, String.valueOf(position), Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(getApplicationContext(), ViewPostActivity.class);

                intent.putExtra("songName", songName[position] );
                intent.putExtra("artistName", artistName[position] );
                intent.putExtra("authorName", authorName[position] );
                intent.putExtra("authorId", authorId[position] );
                intent.putExtra("note", note[position]);
                intent.putExtra("category", category[position] );
                intent.putExtra("imageurl", imageUrl[position] );
                intent.putExtra("likes", likes[position] );
                intent.putExtra("dislikes", dislikes[position] );
                intent.putExtra("id", postId[position]);

                startActivity(intent);
            }
        });
    }

    public static void removeSimpleProgressDialog() {
        try {
            if (mProgressDialog != null) {
                if (mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                    mProgressDialog = null;
                }
            }
        } catch (IllegalArgumentException ie) {
            ie.printStackTrace();

        } catch (RuntimeException re) {
            re.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void showSimpleProgressDialog(Context context, String title,
                                                String msg, boolean isCancelable) {
        try {
            if (mProgressDialog == null) {
                mProgressDialog = ProgressDialog.show(context, title, msg);
                mProgressDialog.setCancelable(isCancelable);
            }

            if (!mProgressDialog.isShowing()) {
                mProgressDialog.show();
            }

        } catch (IllegalArgumentException ie) {
            ie.printStackTrace();
        } catch (RuntimeException re) {
            re.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void searchTagFill(){

        ArrayAdapter<String> adapter = new ArrayAdapter<String> (this, android.R.layout.select_dialog_item, songName);
        actvSearch.setThreshold(1);//will start working from first character
        actvSearch.setAdapter(adapter);//setting the adapter data into the AutoCompleteTextView
    }

    @Override
    protected void onResume() {
        super.onResume();

        listView.setVisibility(View.VISIBLE);

        switch (menuItem){
            case 0 : retrieveAll();
            break;
            case 1 : retrieveCollection();
            break;
            case 2 : retrieveNotations();
            break;
            case 3 : retrieveChords();
                break;
            case 4 : retrieveLyrics();
                break;
            default: retrieveAll();
            break;
        }







    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_profile) {
            // Handle the camera action
            Intent intent = new Intent(this, ViewProfileActivity.class);
//            intent.putExtra("id", sharedPref.getId());
            startActivity(intent);
        } else if (id == R.id.nav_home) {
            menuItem = 0;
            fab.show();
            listView.setVisibility(View.VISIBLE);
            llEmpty = (LinearLayout) findViewById(R.id.llEmptyNote);
            llEmpty.setVisibility(View.GONE);
            onResume();

        } else if (id == R.id.nav_collection) {
            menuItem = 1;
            fab.show();
            listView.setVisibility(View.VISIBLE);
            llEmpty = (LinearLayout) findViewById(R.id.llEmptyNote);
            llEmpty.setVisibility(View.GONE);
            onResume();

        } else if (id == R.id.nav_notations) {

            menuItem = 2;
            fab.show();
            listView.setVisibility(View.VISIBLE);
            llEmpty = (LinearLayout) findViewById(R.id.llEmptyNote);
            llEmpty.setVisibility(View.GONE);
            onResume();

        } else if (id == R.id.nav_chords) {
            menuItem = 3;
            fab.show();
            listView.setVisibility(View.VISIBLE);
            llEmpty = (LinearLayout) findViewById(R.id.llEmptyNote);
            llEmpty.setVisibility(View.GONE);
            onResume();

        } else if (id == R.id.nav_lyrics) {
            menuItem = 4;
            fab.show();
            listView.setVisibility(View.VISIBLE);
            llEmpty = (LinearLayout) findViewById(R.id.llEmptyNote);
            llEmpty.setVisibility(View.GONE);
            onResume();

        } else if (id == R.id.nav_settings) {

        }
        else if (id == R.id.nav_share) {

        }
        else if (id == R.id.nav_aboutme) {

        }
        else if (id == R.id.nav_logout){

            sharedPref.logout();
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
