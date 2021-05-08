package com.guyson.kronos;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.guyson.kronos.enums.LecturerType;
import com.guyson.kronos.model.Lecture;
import com.guyson.kronos.model.Lecturer;
import com.guyson.kronos.model.MessageResponse;
import com.guyson.kronos.service.LecturerClient;
import com.guyson.kronos.service.RetrofitClientInstance;
import com.guyson.kronos.util.AuthHandler;
import com.guyson.kronos.util.ExtraUtilities;
import com.guyson.kronos.util.NavHandler;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddLecturerActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private ProgressDialog mProgressDialog;
    private Button button;
    private EditText firstNameEditText, lastNameEditText, emailEditText;
    private AutoCompleteTextView typeDropdown;

    private String token;

    //Lecturer Retrofit Client
    private final LecturerClient lecturerClient = RetrofitClientInstance.getRetrofitInstance().create(LecturerClient.class);

    //Dropdown attributes
    private List<String> types = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_lecturer);

        //Check if authorization token is valid
        AuthHandler.validate(AddLecturerActivity.this, "academic_admin");

        //Retrieve JWT Token
        SharedPreferences sharedPreferences = getSharedPreferences("auth_preferences", Context.MODE_PRIVATE);
        token = "Bearer "+sharedPreferences.getString("auth_token", null);

        //Setup toolbar
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        //Setup navigation drawer
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mNavigationView = findViewById(R.id.nav_view);

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

        mProgressDialog = new ProgressDialog(this);

        //Initialize edit texts
        firstNameEditText = findViewById(R.id.input_first_name);
        lastNameEditText = findViewById(R.id.input_last_name);
        emailEditText = findViewById(R.id.input_email);

        //Setup dropdown
        for (LecturerType lecturerType : LecturerType.values()) {
            types.add(lecturerType.getType());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        this,
                        R.layout.dropdown_menu_popup_item,
                        types);

        typeDropdown = findViewById(R.id.type_dropdown);
        typeDropdown.setAdapter(adapter);

        //Submit button
        button = findViewById(R.id.add_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleSubmit();
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        //Handle side drawer navigation
        NavHandler.handleAcademicAdminNav(item, AddLecturerActivity.this);

        //close navigation drawer
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    //Handle Add Lecturer
    private void handleSubmit() {

        String firstName = firstNameEditText.getText().toString().trim();
        String lastName = lastNameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String type = typeDropdown.getText().toString();

        //Validate user input
        if (TextUtils.isEmpty(firstName) || TextUtils.isEmpty(lastName) || TextUtils.isEmpty(email) || TextUtils.isEmpty(type)) {
            Toast.makeText(this, "Please enter valid input", Toast.LENGTH_SHORT).show();
        }else if (!ExtraUtilities.isEmailValid(email)) {
            Toast.makeText(this, "Email is not valid", Toast.LENGTH_SHORT).show();
        }else if (!types.contains(ExtraUtilities.capitalize(type.toLowerCase()))) {
            Toast.makeText(this, "Invalid lecturer type", Toast.LENGTH_SHORT).show();
        }else {
            //If user input is valid

            //Show progress
            mProgressDialog.setMessage("Adding lecturer...");
            mProgressDialog.show();

            Lecturer lecturer = new Lecturer();
            lecturer.setEmail(email);
            lecturer.setFirstName(firstName);
            lecturer.setLastName(lastName);
            lecturer.setType(type.toLowerCase());

            Call<ResponseBody> call = lecturerClient.addLecturer(token, lecturer);

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                    //Successfully added
                    if (response.code()==201) {
                        Toast.makeText(AddLecturerActivity.this, "Successfully added lecturer!", Toast.LENGTH_SHORT).show();

                        mProgressDialog.dismiss();

                        //Direct to ManageLecturersActivity
                        Intent intent = new Intent(AddLecturerActivity.this, ManageLecturersActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                    else {

                        try {

                            // Capture an display specific messages
                            JSONObject obj = new JSONObject(response.errorBody().string());
                            Toast.makeText(AddLecturerActivity.this, obj.getString("message"), Toast.LENGTH_SHORT).show();

                        }catch(Exception e) {
                            Toast.makeText(AddLecturerActivity.this, "An error occurred", Toast.LENGTH_SHORT).show();
                        }

                        mProgressDialog.dismiss();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(AddLecturerActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                    mProgressDialog.dismiss();
                }
            });
        }

    }
}