package com.guyson.kronos;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.guyson.kronos.model.LoginCredentials;
import com.guyson.kronos.model.User;
import com.guyson.kronos.service.RetrofitClientInstance;
import com.guyson.kronos.service.UserClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText mUsernameEditText, mPasswordEditText;
    private MaterialButton mLoginButton;
    private ProgressDialog mProgressDialog;
    private TextView mContactTextView;

    private SharedPreferences sharedPrefs;

    private UserClient userClient = RetrofitClientInstance.getRetrofitInstance().create(UserClient.class);

    private int CALL_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        checkRole();

        //Initialize views
        mUsernameEditText = findViewById(R.id.input_username);
        mPasswordEditText = findViewById(R.id.input_password);
        mLoginButton = findViewById(R.id.login_button);
        mProgressDialog = new ProgressDialog(this);

        //When login button is clicked
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginUser();
            }
        });

        //Handle contact admin
        mContactTextView = findViewById(R.id.tv_contact_admin);
        mContactTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makeCall();
            }
        });
    }

    private void loginUser() {

        String username = mUsernameEditText.getText().toString();
        String password = mPasswordEditText.getText().toString();

        //If there are empty fields
        if(TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter valid data!", Toast.LENGTH_SHORT).show();
        }else{

            //Create login credentials
            LoginCredentials loginCredentials = new LoginCredentials(username, password);
            Call<User> call = userClient.login(loginCredentials);

            //Show progress
            mProgressDialog.setMessage("Getting things ready...");
            mProgressDialog.show();

            call.enqueue(new Callback<User>() {

                @Override
                public void onResponse(Call<User> call, Response<User> response) {

                    mProgressDialog.dismiss();

                    //200 status code
                    if(response.isSuccessful()){

                        Toast.makeText(LoginActivity.this, "Logged in successfully!", Toast.LENGTH_SHORT).show();

                        //Save authorization token and role
                        SharedPreferences.Editor editor = sharedPrefs.edit();
                        editor.putString("auth_token", response.body().getToken());
                        editor.putString("role", response.body().getRole());
                        editor.apply();

                        //Direct user to respective home page
                        String role = response.body().getRole();

                        Intent homePageIntent;

                        if(role.equals("student")){
                            homePageIntent = new Intent(LoginActivity.this, MainActivity.class);
                        }else {
                            homePageIntent = new Intent(LoginActivity.this, ManageLecturersActivity.class);
                        }
                        homePageIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(homePageIntent);
                        finish();
                    }
                    // Invalid password
                    else if(response.code() == 403){
                        Toast.makeText(LoginActivity.this, "Invalid username or password!", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(LoginActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    mProgressDialog.dismiss();
                    Toast.makeText(LoginActivity.this, "Invalid username or password!", Toast.LENGTH_SHORT).show();
                }
            });

        }
    }

    private void checkRole() {
        sharedPrefs = LoginActivity.this.getSharedPreferences("auth_preferences",Context.MODE_PRIVATE);
        String role = sharedPrefs.getString("role", null);

        if(role!=null){
            if(role.equals("student")){
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }else if(role.equals("academic_admin")){
                Intent intent = new Intent(LoginActivity.this, ManageLecturersActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }else if(role.equals("admin")){
                Intent intent = new Intent(LoginActivity.this, ManageStudentsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        }
    }

    @Override
    public void onBackPressed() {
    // super.onBackPressed();
    // Not calling **super**, disables back button in current screen.
    }

    //Handle Contact Administrator
    private void makeCall() {
        String number = "0999217356";

        if(ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(LoginActivity.this, new String[]{Manifest.permission.CALL_PHONE}, CALL_REQUEST);
        }else{
            String dial = "tel:"+number;
            startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == CALL_REQUEST){
            if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                makeCall();
            }else{
                Toast.makeText(this, "You need to give permissions to make a call", Toast.LENGTH_SHORT).show();
            }
        }

    }
}