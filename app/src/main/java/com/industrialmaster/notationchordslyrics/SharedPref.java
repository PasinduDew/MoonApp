package com.industrialmaster.notationchordslyrics;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPref {

    Context context;
    SharedPreferences sharedPreferences;


    SharedPref(Context context) {
        this.sharedPreferences = context.getSharedPreferences("com.industrialmaster.noatationchordslyrics.userlogin", Context.MODE_PRIVATE);
    }

    public void saveLoginDetails(int id, String username, String email, boolean auth, boolean imageValid, String imageUrl) {

        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putInt("id", id);
        editor.putString("username", username);
        editor.putString("email", email);
        editor.putBoolean("auth", auth);
        editor.putBoolean("imageValid", imageValid);
        editor.putString("imageurl", imageUrl);

        editor.commit();
    }

    public String getImageUrl(){

        return sharedPreferences.getString("imageurl", "null");
    }

    public String getEmail() {
//        SharedPreferences sharedPreferences = context.getSharedPreferences("com.industrialmaster.noatationchordslyrics.userlogin", Context.MODE_PRIVATE);
        return sharedPreferences.getString("email", "null");
    }

    public String getUsername(){
//        SharedPreferences sharedPreferences = context.getSharedPreferences("com.industrialmaster.noatationchordslyrics.userlogin", Context.MODE_PRIVATE);
        return sharedPreferences.getString("username", "null");
    }

    public int getId(){
//        SharedPreferences sharedPreferences = context.getSharedPreferences("com.industrialmaster.noatationchordslyrics.userlogin", Context.MODE_PRIVATE);
        return sharedPreferences.getInt("id",0);
    }

    public boolean getAuth(){
//        SharedPreferences sharedPreferences = context.getSharedPreferences("com.industrialmaster.noatationchordslyrics.userlogin", Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean("auth",false);
    }

    public boolean getImageValidity(){
//        SharedPreferences sharedPreferences = context.getSharedPreferences("com.industrialmaster.noatationchordslyrics.userlogin", Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean("imageValid",false);
    }



    public boolean isUserLogedOut() {
//        SharedPreferences sharedPreferences = context.getSharedPreferences("com.industrialmaster.noatationchordslyrics.userlogin", Context.MODE_PRIVATE);
        boolean isEmailEmpty = sharedPreferences.getString("Email", "").isEmpty();
        boolean isAuthGrant = sharedPreferences.getBoolean("auth", false);
        return isEmailEmpty || isAuthGrant;
    }

    public void logout(){

//        SharedPreferences sharedPreferences = context.getSharedPreferences("com.industrialmaster.noatationchordslyrics.userlogin", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.remove("id"); // will delete key name
        editor.remove("username"); // will delete key email
        editor.remove("email"); // will delete key email
        editor.remove("auth"); // will delete key email
        editor.remove("imageValid"); // will delete key email
        editor.remove("imageurl");

        editor.commit(); // commit changes

    }
}
