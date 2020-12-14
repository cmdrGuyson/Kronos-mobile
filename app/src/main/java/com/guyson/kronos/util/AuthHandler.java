package com.guyson.kronos.util;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.auth0.android.jwt.JWT;
import com.guyson.kronos.LoginActivity;
import com.guyson.kronos.MainActivity;
import com.guyson.kronos.ManageLecturersActivity;

public class AuthHandler {

    public static void validate(Context context, String userRole){
        SharedPreferences sharedPreferences = context.getSharedPreferences("auth_preferences", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("auth_token", null);
        String role = sharedPreferences.getString("role", null);

        if(token!=null){
            JWT jwt = new JWT(token);
            boolean isExpired = jwt.isExpired(10);

            //Check if JWT has expired
            if(isExpired || !role.equals(userRole)){

                if(isExpired) {
                    AuthHandler.logout(context);
                }

                Intent intent = new Intent(context, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(intent);
            }

        }else{
            Intent intent = new Intent(context, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(intent);
        }
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
    }

}
