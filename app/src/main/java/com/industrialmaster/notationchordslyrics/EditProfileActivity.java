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
import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.util.LruCache;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    TextView tvText;
    EditText etText;
    EditText etPassword;
    EditText etPassConf;
    LinearLayout llPassConf;
    LinearLayout llPassword;


    String action;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        sharedPref = new SharedPref(getApplicationContext());

        Intent intent = getIntent();



        if (sharedPref.getAuth()){
            setContentView(R.layout.activity_edit_profile);
            action = intent.getStringExtra("edit");

            tvText = (TextView) findViewById(R.id.tvText);
            etText = (EditText) findViewById(R.id.etText);
            etPassConf = (EditText) findViewById(R.id.etPassConf);
            etPassword = (EditText) findViewById(R.id.etPassword);
            llPassConf = (LinearLayout) findViewById(R.id.llPassConf);
            llPassword = (LinearLayout) findViewById(R.id.llPassword);

            if (action.equals("username")){
                tvText.setText("Username");
                etText.setText(sharedPref.getUsername());

            }
            else if(action.equals("email")){
                tvText.setText("Email");
                etText.setText(sharedPref.getEmail());

            }
            else if(action.equals("password")){

                llPassword.setVisibility(View.VISIBLE);
                llPassConf.setVisibility(View.VISIBLE);
                etText.setVisibility(View.GONE);
                tvText.setVisibility(View.GONE);




            }



        }

    }

    public void saveChanges(View v){

        final String val;

        if (action.equals("username")){
            sharedPref.setUsername(etText.getText().toString());
            val = etText.getText().toString();

        }
        else if(action.equals("email")){
            sharedPref.setEmail(etText.getText().toString());
            val = etText.getText().toString();

        }
        else if(action.equals("password")){

            if (!(etPassword.getText().toString().equals(etPassConf.getText().toString()))){
                etPassword.setText("");
                etPassConf.setText("");
                Toast.makeText(this, "Passwords didn`t Match", Toast.LENGTH_SHORT).show();
                return;
            }

            val = etPassword.getText().toString();

        }
        else {
            val = "";
        }

        RequestQueue queue = Volley.newRequestQueue(EditProfileActivity.this);


        StringRequest request = new StringRequest(
                Request.Method.POST,
                "http://pasindud.tk/myapp/user/update.php",
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {

                                Toast.makeText(EditProfileActivity.this, "Updated Successfully", Toast.LENGTH_LONG).show();
//                                Snackbar.make(view1, "Replace with your own action", Snackbar.LENGTH_LONG)
//                                        .setAction("Action", null).show();
                        llPassConf.setVisibility(View.GONE);
                        llPassword.setVisibility(View.GONE);
                        etText.setVisibility(View.GONE);
                        tvText.setVisibility(View.GONE);
                        onBackPressed();
                    }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(EditProfileActivity.this, "ERROR", Toast.LENGTH_SHORT).show();
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String>  params = new HashMap<>();

                params.put("id", String.valueOf(sharedPref.getId()));
                params.put("edit", action);
                params.put("value", val);


                return params;
            }
        };


        queue.add(request);

    }

}
