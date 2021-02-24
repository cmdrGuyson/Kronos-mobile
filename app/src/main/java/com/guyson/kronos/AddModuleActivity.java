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
import com.guyson.kronos.model.Lecture;
import com.guyson.kronos.model.Lecturer;
import com.guyson.kronos.model.Module;
import com.guyson.kronos.model.User;
import com.guyson.kronos.service.LecturerClient;
import com.guyson.kronos.service.ModuleClient;
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

public class AddModuleActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private ProgressDialog mProgressDialog;
    private Button button;
    private EditText nameEditText, descriptionEditText, creditsEditText;
    private AutoCompleteTextView lecturerDropdown;

    private LecturerClient lecturerClient = RetrofitClientInstance.getRetrofitInstance().create(LecturerClient.class);
    private ModuleClient moduleClient = RetrofitClientInstance.getRetrofitInstance().create(ModuleClient.class);

    private String token;
    private boolean dataLoaded;

    //Dropdown attributes
    private List<String> lecturers = new ArrayList<>();
    private List<Integer> ids = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_module);

        //Check if authorization token is valid
        AuthHandler.validate(AddModuleActivity.this, "admin");

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
        nameEditText = findViewById(R.id.input_name);
        descriptionEditText = findViewById(R.id.input_description);
        creditsEditText = findViewById(R.id.input_credits);
        lecturerDropdown = findViewById(R.id.lecturer_dropdown);

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
        NavHandler.handleAdminNav(item, AddModuleActivity.this);

        //close navigation drawer
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    //Configuration of classes dropdown
    private void setupDropdown() {

        Call<List<Lecturer>> call = lecturerClient.getLecturers(token);

        //Show progress
        mProgressDialog.setMessage("Setting up form...");
        mProgressDialog.show();

        call.enqueue(new Callback<List<Lecturer>>() {
            @Override
            public void onResponse(Call<List<Lecturer>> call, Response<List<Lecturer>> response) {
                List<Lecturer> lecturerList = response.body();
                if(lecturerList!=null) {

                    //Configure drop down
                    for (Lecturer l : lecturerList) {
                        lecturers.add(l.getFirstName()+" "+l.getLastName()+" "+l.getLecturerID());
                        ids.add(l.getLecturerID());
                    }

                    //Set adapter for dropdown
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(
                            AddModuleActivity.this,
                            R.layout.dropdown_menu_popup_item,
                            lecturers);

                    lecturerDropdown.setAdapter(adapter);

                    dataLoaded = true;

                }else{
                    Toast.makeText(AddModuleActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                }
                mProgressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<List<Lecturer>> call, Throwable t) {
                Toast.makeText(AddModuleActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                mProgressDialog.dismiss();
            }
        });
    }

    //Handle submit button click
    private void handleSubmit() {

        if(dataLoaded) {

            //Get user input
            String name = nameEditText.getText().toString();
            String description = descriptionEditText.getText().toString();
            int credits = 0;

            try {
                credits = Integer.parseInt(creditsEditText.getText().toString());
            }catch(Exception e) {
                Toast.makeText(this, "Invalid credit amount!", Toast.LENGTH_SHORT).show();
                return;
            }

            String lecturerSelection = lecturerDropdown.getText().toString();

            //Validate user input
            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(description) || TextUtils.isEmpty(lecturerSelection) || credits <= 0) {
                Toast.makeText(this, "Please enter valid input", Toast.LENGTH_SHORT).show();
            }else if (!lecturers.contains(lecturerSelection)) {
                Toast.makeText(this, "Invalid lecturer. Please select from dropdown", Toast.LENGTH_SHORT).show();
            }else {
                //If user input is valid

                //Show progress
                mProgressDialog.setMessage("Adding module...");
                mProgressDialog.show();

                //Create module object
                Module module = new Module();
                module.setCredits(credits);
                module.setDescription(description);
                module.setName(ExtraUtilities.toTitleCase(name));
                module.setLecturerID(ids.get(lecturers.indexOf(lecturerSelection)));

                Call<ResponseBody> call = moduleClient.addModule(token, module);

                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                        //Successfully added
                        if (response.code() == 201) {
                            Toast.makeText(AddModuleActivity.this, "Successfully added module!", Toast.LENGTH_SHORT).show();

                            mProgressDialog.dismiss();

                            //Direct to ManageModulesActivity
                            Intent intent = new Intent(AddModuleActivity.this, ManageModulesActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        } else {

                            try {

                                // Capture an display specific messages
                                JSONObject obj = new JSONObject(response.errorBody().string());
                                Toast.makeText(AddModuleActivity.this, obj.getString("message"), Toast.LENGTH_SHORT).show();

                            } catch (Exception e) {
                                Toast.makeText(AddModuleActivity.this, "An error occurred", Toast.LENGTH_SHORT).show();
                            }

                            mProgressDialog.dismiss();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(AddModuleActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                        mProgressDialog.dismiss();
                    }
                });
            }

        }else{
            Toast.makeText(this, "Form wasn't setup properly!", Toast.LENGTH_SHORT).show();
        }

    }
}