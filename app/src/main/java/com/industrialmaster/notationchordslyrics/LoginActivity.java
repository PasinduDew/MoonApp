package com.industrialmaster.notationchordslyrics;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    SharedPref sharedPref;
    private static ProgressDialog mProgressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPref = new SharedPref(getApplicationContext());


        if(!sharedPref.getAuth()){
            setContentView(R.layout.activity_login);
            Toast.makeText(LoginActivity.this, "Ayubowan", Toast.LENGTH_SHORT).show();

        }
        else{
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        }

    }

    public void login(View view){

        EditText etEmail = (EditText)findViewById(R.id.etEmail);
        EditText etPassword = (EditText)findViewById(R.id.etPassword);

        if(etEmail.getText().toString().equals("")){
            etEmail.setError("Enter Your Email!");
//            Toast.makeText(LoginActivity.this, "Please Enter the Email", Toast.LENGTH_SHORT).show();
        }
        else if(etPassword.getText().toString().equals("")){
            etPassword.setError("Enter Your Password!");
//            Toast.makeText(LoginActivity.this, "Please Enter Your Password", Toast.LENGTH_SHORT).show();
        }
        else if (!(etEmail.getText().toString().contains("@"))){
            etEmail.setError("Enter Valid Email!");
        }
        else{

            showSimpleProgressDialog(this, "","Loading...",false);

            RequestQueue queue = Volley.newRequestQueue(this);

            final String email = etEmail.getText().toString();
            final String password = etPassword.getText().toString();
            String url = "http://pasindud.tk/myapp/user/login.php";

//            Toast.makeText(LoginActivity.this, "I`m Here " + email, Toast.LENGTH_SHORT).show();


            StringRequest request = new StringRequest(
                    Request.Method.POST,
                    "http://pasindud.tk/myapp/user/login.php",
                    new Response.Listener<String>() {

                        @Override
                        public void onResponse(String response) {
//                            Toast.makeText(LoginActivity.this, response, Toast.LENGTH_SHORT).show();
                            try{
                                JSONObject user = new JSONObject(response);
                                if(user.getBoolean("auth")){

//                                    Toast.makeText(LoginActivity.this, "Inside JSON", Toast.LENGTH_SHORT).show();

                                    sharedPref.saveLoginDetails(user.getInt("id"), user.getString("username"), email, user.getBoolean("auth"), user.getBoolean("imageValid"), user.getString("imageurl"));
//                                    Toast.makeText(LoginActivity.this, sharedPref.getUsername(), Toast.LENGTH_SHORT).show();
                                    if (sharedPref.getAuth()){
                                        removeSimpleProgressDialog();
                                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                        startActivity(intent);
                                    }

                                }
                                else{
                                    removeSimpleProgressDialog();
                                    Toast.makeText(LoginActivity.this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
                                }
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
                            removeSimpleProgressDialog();
                            Toast.makeText(LoginActivity.this, "ERROR", Toast.LENGTH_SHORT).show();
                        }
                    }
            ){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String>  params = new HashMap<>();
                    params.put("email", email);
                    params.put("password", password);


                    return params;
                }
            };

            queue.add(request);

        }


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



    public void register(View view){
        Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
        startActivity(intent);
    }


}
