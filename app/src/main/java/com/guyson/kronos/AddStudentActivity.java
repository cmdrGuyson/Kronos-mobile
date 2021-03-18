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
import com.guyson.kronos.model.Class;
import com.guyson.kronos.model.Lecturer;
import com.guyson.kronos.model.User;
import com.guyson.kronos.service.ClassClient;
import com.guyson.kronos.service.RetrofitClientInstance;
import com.guyson.kronos.service.UserClient;
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

public class AddStudentActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private ProgressDialog mProgressDialog;
    private Button button;
    private EditText firstNameEditText, lastNameEditText, usernameEditText;
    private AutoCompleteTextView classDropdown;

    private final UserClient userClient = RetrofitClientInstance.getRetrofitInstance().create(UserClient.class);
    private final ClassClient classClient = RetrofitClientInstance.getRetrofitInstance().create(ClassClient.class);

    private String token;
    private boolean dataLoaded;

    //Dropdown attributes
    private List<String> classes = new ArrayList<>();
    private List<Integer> ids = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_student);

        //Check if authorization token is valid
        AuthHandler.validate(AddStudentActivity.this, "admin");

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
        usernameEditText = findViewById(R.id.input_username);
        classDropdown = findViewById(R.id.class_dropdown);

        //Initialize list of classes
        setupDropdown();

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
        NavHandler.handleAdminNav(item, AddStudentActivity.this);

        //close navigation drawer
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    //Configuration of classes dropdown
    private void setupDropdown() {

        Call<List<Class>> call = classClient.getClasses(token);

        //Show progress
        mProgressDialog.setMessage("Setting up form...");
        mProgressDialog.show();

        call.enqueue(new Callback<List<Class>>() {
            @Override
            public void onResponse(Call<List<Class>> call, Response<List<Class>> response) {
                List<Class> classList = response.body();
                if(classList!=null){

                    //Configure drop down
                    for (Class c : classList) {
                        classes.add("Class "+c.getClassID());
                        ids.add(c.getClassID());
                    }

                    //Set adapter for dropdown
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(
                            AddStudentActivity.this,
                            R.layout.dropdown_menu_popup_item,
                            classes);

                    classDropdown.setAdapter(adapter);

                    dataLoaded = true;

                }else{
                    Toast.makeText(AddStudentActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                }
                mProgressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<List<Class>> call, Throwable t) {
                Toast.makeText(AddStudentActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                mProgressDialog.dismiss();
            }
        });
    }

    //Handle submit button click
    private void handleSubmit() {

        if(dataLoaded) {

            //Get user input
            String firstName = firstNameEditText.getText().toString().trim();
            String lastName = lastNameEditText.getText().toString().trim();
            String username = usernameEditText.getText().toString().trim();
            String classSelection = classDropdown.getText().toString();

            //Validate user input
            if (TextUtils.isEmpty(firstName) || TextUtils.isEmpty(lastName) || TextUtils.isEmpty(username) || TextUtils.isEmpty(classSelection)) {
                Toast.makeText(this, "Please enter valid input", Toast.LENGTH_SHORT).show();
            }else if (!classes.contains(ExtraUtilities.capitalize(classSelection.toLowerCase()))) {
                Toast.makeText(this, "Invalid class. Please select from dropdown", Toast.LENGTH_SHORT).show();
            }else {
                //If user input is valid

                //Show progress
                mProgressDialog.setMessage("Adding student...");
                mProgressDialog.show();

                //Create student object
                User student = new User();
                student.setClassID(ids.get(classes.indexOf(classSelection)));
                student.setFirstName(firstName);
                student.setLastName(lastName);
                student.setUsername(username);

                Call<ResponseBody> call = userClient.addStudent(token, student);

                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                        //Successfully added
                        if (response.code() == 201) {
                            Toast.makeText(AddStudentActivity.this, "Successfully added student!", Toast.LENGTH_SHORT).show();

                            mProgressDialog.dismiss();

                            //Direct to ManageStudentsActivity
                            Intent intent = new Intent(AddStudentActivity.this, ManageStudentsActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        } else {

                            try {

                                // Capture an display specific messages
                                JSONObject obj = new JSONObject(response.errorBody().string());
                                Toast.makeText(AddStudentActivity.this, obj.getString("message"), Toast.LENGTH_SHORT).show();

                            } catch (Exception e) {
                                Toast.makeText(AddStudentActivity.this, "An error occurred", Toast.LENGTH_SHORT).show();
                            }

                            mProgressDialog.dismiss();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(AddStudentActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                        mProgressDialog.dismiss();
                    }
                });
            }

        }else{
            Toast.makeText(AddStudentActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
            mProgressDialog.dismiss();
        }

    }
}