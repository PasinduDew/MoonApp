package com.industrialmaster.notationchordslyrics;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MyPostsActivity extends AppCompatActivity {

    SharedPref sharedPref;


    private static ProgressDialog mProgressDialog;
    private ListView listView;
    ArrayList<DataModel> dataModelArrayList;
    private ListAdapter listAdapter;

    LinearLayout llEmpty;

    String[] songName;
    String[] artistName;
    String[] imageUrl;
    String[] authorName;
    String[] note;
    int[] authorId;
    int[] likes, dislikes;
    String[] category;
    int[] postId;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_posts);

        sharedPref = new SharedPref(getApplicationContext());
        listView = findViewById(R.id.lvPosts);


    }

    @Override
    protected void onResume() {
        super.onResume();
        getMyPosts();
    }

    private void getMyPosts() {

        String url = "http://pasindud.tk/myapp/posts/myPosts.php";

        showSimpleProgressDialog(this, "","Your Contribution Matters",false);

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

    private void setupListview(){
        removeSimpleProgressDialog();  //will remove progress dialog
        listAdapter = new ListAdapter(this, dataModelArrayList);

        try{
            listView.setAdapter(listAdapter);
        }
        catch (Exception e){

            listView.setVisibility(View.GONE);
            llEmpty = (LinearLayout) findViewById(R.id.llEmptyNote);
            llEmpty.setVisibility(View.VISIBLE);
            Toast.makeText(this, "Join the Community by your Contribution", Toast.LENGTH_SHORT).show();
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
}
