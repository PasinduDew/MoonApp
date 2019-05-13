package com.industrialmaster.notationchordslyrics;

import android.content.Intent;
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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
    }


    public void login(View view){
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
    }

    public void register(View view){
        EditText etUsername = findViewById(R.id.etUsername);
        EditText etEmail = findViewById(R.id.etEmail);
        EditText etPassword = findViewById(R.id.etPassword);
        EditText etConfPas = findViewById(R.id.etConfPassword);

        if(etUsername.getText().toString().equals("")){
            etUsername.setError("Enter a Username");

        }
        else if(etUsername.getText().toString().length() < 3){
            etUsername.setError("Username should be minimum of 3 characters long.");
        }
        else if(etEmail.getText().toString().equals("")){
            etEmail.setError("Enter Your Email!");
//            Toast.makeText(LoginActivity.this, "Please Enter the Email", Toast.LENGTH_SHORT).show();
        }
        else if (!(etEmail.getText().toString().contains("@") && etEmail.getText().toString().contains("."))){
            etEmail.setError("Enter Valid Email!");
        }
        else if(etPassword.getText().toString().equals("")){
            etPassword.setError("Enter Your Password!");
//            Toast.makeText(LoginActivity.this, "Please Enter Your Password", Toast.LENGTH_SHORT).show();
        }
        else if(!(etPassword.getText().toString().equals("")) && etConfPas.getText().toString().equals("")){
            etConfPas.setError("Re Enter Your Password");
//            Toast.makeText(LoginActivity.this, "Please Enter Your Password", Toast.LENGTH_SHORT).show();
        }
        else if(!(etPassword.getText().toString().equals(etConfPas.getText().toString()))){

            Toast.makeText(RegisterActivity.this, "Passwords Differ", Toast.LENGTH_SHORT).show();
            etPassword.setError("");
            etConfPas.setError("");
            etPassword.setText("");
            etConfPas.setText("");
        }
        else{

            final String username = etUsername.getText().toString();
            final String email = etEmail.getText().toString();
            final String password = etPassword.getText().toString();

            RequestQueue queue = Volley.newRequestQueue(this);

            StringRequest request = new StringRequest(
                    Request.Method.POST,
                    "http://pasindud.tk/myapp/user/add.php",
                    new Response.Listener<String>() {

                        @Override
                        public void onResponse(String response) {
                            Toast.makeText(RegisterActivity.this, "Saved!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                            startActivity(intent);
                        }

                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(RegisterActivity.this, "ERROR", Toast.LENGTH_SHORT).show();
                        }
                    }
            ){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String>  params = new HashMap<>();
                    params.put("username", username);
                    params.put("email", email);
                    params.put("password", password);
//                params.put("session_id", ""+id);

                    return params;
                }
            };

            queue.add(request);


        }




    }
}
