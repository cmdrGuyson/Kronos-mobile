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
import com.guyson.kronos.enums.RoomType;
import com.guyson.kronos.model.Class;
import com.guyson.kronos.model.Room;
import com.guyson.kronos.service.ClassClient;
import com.guyson.kronos.service.RetrofitClientInstance;
import com.guyson.kronos.service.RoomClient;
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

public class AddRoomActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private ProgressDialog mProgressDialog;
    private Button button;
    private EditText descriptionEditText;
    private AutoCompleteTextView typeDropdown;

    private String token;

    //Retrofit class client
    private final RoomClient roomClient = RetrofitClientInstance.getRetrofitInstance().create(RoomClient.class);

    //Dropdown attributes
    private List<String> types = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_room);//Check if authorization token is valid
        AuthHandler.validate(AddRoomActivity.this, "admin");

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
        descriptionEditText = findViewById(R.id.input_description);

        //Setup dropdown
        for (RoomType roomType: RoomType.values()) {
            types.add(roomType.getType());
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
        NavHandler.handleAdminNav(item, AddRoomActivity.this);

        //close navigation drawer
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    //Handle submit button click
    private void handleSubmit() {

        //Get user input
        String type = typeDropdown.getText().toString();
        String description = descriptionEditText.getText().toString();

        //Validate user input
        if (TextUtils.isEmpty(description) || TextUtils.isEmpty(type)) {
            Toast.makeText(this, "Please enter valid input", Toast.LENGTH_SHORT).show();
        }else if (!types.contains(ExtraUtilities.toTitleCase(type.toLowerCase()))) {
            Toast.makeText(this, "Invalid room type", Toast.LENGTH_SHORT).show();
        }else{
            //If user input is valid

            //Show progress
            mProgressDialog.setMessage("Adding class...");
            mProgressDialog.show();

            Room room = new Room();
            room.setDescription(description);
            room.setType(type);

            Call<ResponseBody> call = roomClient.addRoom(token, room);

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    //Successfully added
                    if (response.code()==201) {
                        Toast.makeText(AddRoomActivity.this, "Successfully added room!", Toast.LENGTH_SHORT).show();

                        mProgressDialog.dismiss();

                        //Direct to ManageClassesActivity
                        Intent intent = new Intent(AddRoomActivity.this, ManageRoomsActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                    else {

                        try {

                            // Capture an display specific messages
                            JSONObject obj = new JSONObject(response.errorBody().string());
                            Toast.makeText(AddRoomActivity.this, obj.getString("message"), Toast.LENGTH_SHORT).show();

                        }catch(Exception e) {
                            Toast.makeText(AddRoomActivity.this, "An error occurred", Toast.LENGTH_SHORT).show();
                        }

                        mProgressDialog.dismiss();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(AddRoomActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                    mProgressDialog.dismiss();
                }
            });
        }

    }
}