package com.guyson.kronos;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.guyson.kronos.model.ChangePasswordRequest;
import com.guyson.kronos.service.RetrofitClientInstance;
import com.guyson.kronos.service.UserClient;
import com.guyson.kronos.util.AuthHandler;
import com.guyson.kronos.util.NavHandler;

import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private ProgressDialog mProgressDialog;
    private Button button;
    private EditText oldPasswordEditText, newPasswordEditText, confirmPasswordEditText;

    private String role, token;

    private UserClient userClient = RetrofitClientInstance.getRetrofitInstance().create(UserClient.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //Check if authorization token is valid
        role = AuthHandler.validate(SettingsActivity.this, "all");

        //Retrieve JWT Token
        SharedPreferences sharedPreferences = getSharedPreferences("auth_preferences", Context.MODE_PRIVATE);
        token = "Bearer "+sharedPreferences.getString("auth_token", null);

        //Setup toolbar
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        //Setup navigation drawer
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mNavigationView = findViewById(R.id.nav_view);

        mNavigationView.getMenu().clear();
        if(role.equals("admin")){
            mNavigationView.inflateMenu(R.menu.nav_menu_academic_admin);
        }else{
            mNavigationView.inflateMenu(R.menu.nav_menu);
        }

        mProgressDialog = new ProgressDialog(this);

        ActionBarDrawerToggle mActionBarDrawerToggle = new ActionBarDrawerToggle(
                this,
                mDrawerLayout,
                mToolbar,
                R.string.open_nav_drawer,
                R.string.close_nav_drawer
        );

        mDrawerLayout.addDrawerListener(mActionBarDrawerToggle);
        mActionBarDrawerToggle.syncState();
        mNavigationView.setNavigationItemSelectedListener(this);

        oldPasswordEditText = findViewById(R.id.input_old_password);
        newPasswordEditText = findViewById(R.id.input_new_password);
        confirmPasswordEditText = findViewById(R.id.input_confirm_password);
        button = findViewById(R.id.change_button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleChangePassword();
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if(role.equals("admin")){

            //Handle side drawer navigation
            NavHandler.handleAdminNav(item, SettingsActivity.this);
        }else{
            NavHandler.handleStudentNav(item, SettingsActivity.this);
        }

        //close navigation drawer
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    //Method to handle password change
    private void handleChangePassword() {

        //Show progress
        mProgressDialog.setMessage("Changing password...");


        //Get user input
        String oldPassword = oldPasswordEditText.getText().toString();
        String newPassword = newPasswordEditText.getText().toString();
        final String confirmPassword = confirmPasswordEditText.getText().toString();

        //Validate user input
        if(TextUtils.isEmpty(oldPassword) || TextUtils.isEmpty(newPassword) || TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(this, "Please enter valid input", Toast.LENGTH_SHORT).show();
        } else if (!newPassword.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords don't match", Toast.LENGTH_SHORT).show();
        }
        //Valid user input
        else {

            ChangePasswordRequest request = new ChangePasswordRequest(oldPassword, newPassword);

            Call<ResponseBody> call = userClient.changePassword(token, request);

            mProgressDialog.show();

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                    //Successfully added
                    if (response.code()==200) {
                        Toast.makeText(SettingsActivity.this, "Successfully changed password!", Toast.LENGTH_SHORT).show();

                        mProgressDialog.dismiss();

                        //Clear fields
                        oldPasswordEditText.setText("");
                        newPasswordEditText.setText("");
                        confirmPasswordEditText.setText("");
                    }
                    else {

                        try {

                            // Capture an display specific messages
                            JSONObject obj = new JSONObject(response.errorBody().string());
                            Toast.makeText(SettingsActivity.this, obj.getString("message"), Toast.LENGTH_SHORT).show();

                        }catch(Exception e) {
                            Toast.makeText(SettingsActivity.this, "An error occurred", Toast.LENGTH_SHORT).show();
                        }

                        mProgressDialog.dismiss();
                    }

                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(SettingsActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                    mProgressDialog.dismiss();
                }
            });


        }

    }
}