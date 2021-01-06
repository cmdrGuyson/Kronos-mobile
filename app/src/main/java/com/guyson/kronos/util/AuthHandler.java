package com.guyson.kronos.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.auth0.android.jwt.JWT;
import com.guyson.kronos.LoginActivity;

public class AuthHandler {

    public static String validate(Context context, String userRole){
        SharedPreferences sharedPreferences = context.getSharedPreferences("auth_preferences", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("auth_token", null);
        String role = sharedPreferences.getString("role", null);

        Log.i("USER_ROLE", userRole);
        if(token != null )Log.i("TOKEN", token);
        if(role != null) Log.i("ROLE", role);


        if(token!=null){
            JWT jwt = new JWT(token);
            boolean isExpired = jwt.isExpired(10);

            //Check if JWT has expired or user role mismatch
            if(isExpired || !role.equals(userRole)){

                if(isExpired) {
                    AuthHandler.logout(context);

                    Intent intent = new Intent(context, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    context.startActivity(intent);
                }else if(userRole.equals("all")) {

                    return role;

                }else{
                    Intent intent = new Intent(context, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    context.startActivity(intent);
                }


            }

        }else{
            Intent intent = new Intent(context, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(intent);
        }

        return null;
    }

    public static void logout(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("auth_preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("auth_token", null);
        editor.putString("role", null);
        editor.apply();

        Intent accountIntent = new Intent(context, LoginActivity.class);
        accountIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(accountIntent);
        ((Activity)context).finish();
    }

}
