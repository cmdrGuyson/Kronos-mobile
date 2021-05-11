package com.guyson.kronos;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.guyson.kronos.model.Lecture;
import com.guyson.kronos.model.Lecturer;
import com.guyson.kronos.model.Module;
import com.guyson.kronos.model.Room;
import com.guyson.kronos.service.LectureClient;
import com.guyson.kronos.service.LecturerClient;
import com.guyson.kronos.service.ModuleClient;
import com.guyson.kronos.service.RetrofitClientInstance;
import com.guyson.kronos.service.RoomClient;
import com.guyson.kronos.util.AuthHandler;
import com.guyson.kronos.util.NavHandler;

import org.json.JSONObject;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddLectureActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private ProgressDialog mProgressDialog;
    private Button button;
    private EditText dateEditText, timeEditText, durationEditText;
    private AutoCompleteTextView moduleDropdown, roomDropdown;

    private final ModuleClient moduleClient = RetrofitClientInstance.getRetrofitInstance().create(ModuleClient.class);
    private final RoomClient roomClient = RetrofitClientInstance.getRetrofitInstance().create(RoomClient.class);
    private final LectureClient lectureClient = RetrofitClientInstance.getRetrofitInstance().create(LectureClient.class);

    private String token;
    private boolean modulesLoaded, roomsLoaded;
    private boolean dateSelected, timeSelected;

    private String date, time;

    private boolean updateFlag;
    private Lecture lecture_obj;

    //Dropdown attributes
    private List<String> modules = new ArrayList<>();
    private List<Integer> module_ids = new ArrayList<>();

    private List<String> rooms = new ArrayList<>();
    private List<Integer> room_ids = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_lecture);

        //Check if authorization token is valid
        AuthHandler.validate(AddLectureActivity.this, "academic_admin");

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
        dateEditText = findViewById(R.id.input_date);
        timeEditText = findViewById(R.id.input_time);
        durationEditText = findViewById(R.id.input_duration);
        roomDropdown = findViewById(R.id.room_dropdown);
        moduleDropdown = findViewById(R.id.module_dropdown);

        //Show progress
        mProgressDialog.setMessage("Setting up form...");
        mProgressDialog.show();

        //Handle date and time pickers
        dateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleDatePicker();
            }
        });

        timeEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleTimePicker();
            }
        });

        //Setup rooms dropdown
        setupRoomDropdown();

        //Setup modules dropdown
        setupModuleDropdown();

        //Handle submit
        button = findViewById(R.id.add_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleSubmit();
            }
        });

        //If UPDATING already created lecture
        try {

            setPreviousData();

        }catch(Exception ignored){}

    }

    private void setPreviousData() {
        lecture_obj = (Lecture) getIntent().getSerializableExtra("lecture_obj");

        if (lecture_obj != null) {
            updateFlag = true;

            date = lecture_obj.getDate();
            time = lecture_obj.getStartTime();
            dateEditText.setText(lecture_obj.getDate());
            timeEditText.setText(lecture_obj.getStartTime());
            durationEditText.setText(String.valueOf(lecture_obj.getDuration()));
            timeSelected = true;
            dateSelected = true;
            button.setText("Update Lecture");
            mToolbar.setTitle("Update Lecture");

        }
    }

    //Method to handle date picker
    private void handleDatePicker() {

        //Setup material date picker
        MaterialDatePicker.Builder builder = MaterialDatePicker.Builder.datePicker();
        builder.setTitleText("Select date");
        MaterialDatePicker picker = builder.build();

        picker.show(getSupportFragmentManager(), "DATE_PICKER");

        //When submitted
        picker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onPositiveButtonClick(Object selection) {
                Long dateLong = (Long) selection;
                LocalDate local_date = Instant.ofEpochMilli(dateLong).atZone(ZoneId.systemDefault()).toLocalDate();
                DateTimeFormatter date_formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                date = date_formatter.format(local_date);

                dateSelected = true;
                dateEditText.setText(date);
            }
        });
    }

    //Method to handle time picker
    private void handleTimePicker() {

        Calendar currentTime = Calendar.getInstance();
        int hour = currentTime.get(Calendar.HOUR_OF_DAY);
        int minute = currentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(AddLectureActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {

                String h = selectedHour < 10 ? "0"+selectedHour : String.valueOf(selectedHour);
                String m = selectedMinute < 10 ? "0"+selectedMinute : String.valueOf(selectedMinute);

                time = h+":"+m;

                timeSelected = true;
                timeEditText.setText(time);

            }
        }, hour, minute, true);//Yes 24 hour time
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        //Handle side drawer navigation
        NavHandler.handleAcademicAdminNav(item, AddLectureActivity.this);

        //close navigation drawer
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    private void setupModuleDropdown() {
        Call<List<Module>> call = moduleClient.getModules(token);

        call.enqueue(new Callback<List<Module>>() {
            @Override
            public void onResponse(Call<List<Module>> call, Response<List<Module>> response) {
                List<Module> moduleList = response.body();
                if(moduleList!=null){

                    //Configure drop down
                    for (Module m : moduleList) {
                        modules.add(m.getName() + " " + m.getModuleID());
                        module_ids.add(m.getModuleID());
                    }

                    //Set adapter for dropdown
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(
                            AddLectureActivity.this,
                            R.layout.dropdown_menu_popup_item,
                            modules);

                    moduleDropdown.setAdapter(adapter);
                    modulesLoaded = true;

                    //If updating lecture set existing module
                    if(updateFlag) {
                        moduleDropdown.setText(modules.get(module_ids.indexOf(lecture_obj.getModule().getModuleID())));
                    }

                    //If both modules and rooms are loaded dismiss loading dialog
                    if (roomsLoaded) mProgressDialog.dismiss();

                }else{
                    Toast.makeText(AddLectureActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                    mProgressDialog.dismiss();
                }

            }

            @Override
            public void onFailure(Call<List<Module>> call, Throwable t) {
                Toast.makeText(AddLectureActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                mProgressDialog.dismiss();
            }
        });
    }

    private void setupRoomDropdown() {
        Call<List<Room>> call = roomClient.getRooms(token);

        call.enqueue(new Callback<List<Room>>() {
            @Override
            public void onResponse(Call<List<Room>> call, Response<List<Room>> response) {
                List<Room> roomList = response.body();
                if(roomList!=null){

                    //Configure drop down
                    for (Room r : roomList) {
                        rooms.add(r.getType()+" "+r.getRoomID());
                        room_ids.add(r.getRoomID());
                    }

                    //Set adapter for dropdown
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(
                            AddLectureActivity.this,
                            R.layout.dropdown_menu_popup_item,
                            rooms);

                    roomDropdown.setAdapter(adapter);
                    roomsLoaded = true;

                    //If updating lecture set existing room
                    if(updateFlag) {
                        roomDropdown.setText(rooms.get(room_ids.indexOf(lecture_obj.getRoom().getRoomID())));
                    }

                    //If both modules and rooms are loaded dismiss loading dialog
                    if (modulesLoaded) mProgressDialog.dismiss();

                }else{
                    Toast.makeText(AddLectureActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                    mProgressDialog.dismiss();
                }

            }

            @Override
            public void onFailure(Call<List<Room>> call, Throwable t) {
                Toast.makeText(AddLectureActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                mProgressDialog.dismiss();
            }
        });
    }

    //Handle submit button click
    private void handleSubmit() {

        //If all data was loaded and form was setup properly
        if (modulesLoaded && roomsLoaded) {

            //Get user input
            int duration = 0;

            try {
                duration = Integer.parseInt(durationEditText.getText().toString());
            }catch(Exception e) {
                Toast.makeText(this, "Invalid duration!", Toast.LENGTH_SHORT).show();
                return;
            }

            String roomSelection = roomDropdown.getText().toString();
            String moduleSelection = moduleDropdown.getText().toString();

            //Validate input
            if(TextUtils.isEmpty(roomSelection) || TextUtils.isEmpty(moduleSelection) || duration <= 0) {
                Toast.makeText(this, "Please enter valid input", Toast.LENGTH_SHORT).show();
            }else if(!dateSelected || !timeSelected) {
                Toast.makeText(this, "Please select valid date/time", Toast.LENGTH_SHORT).show();
            }else if(!modules.contains(moduleSelection)) {
                Toast.makeText(this, "Invalid module. Please select from dropdown", Toast.LENGTH_SHORT).show();
            }else if(!rooms.contains(roomSelection)) {
                Toast.makeText(this, "Invalid room. Please select from dropdown", Toast.LENGTH_SHORT).show();
            }
            //Valid user input
            else{

                //Show progress
                mProgressDialog.setMessage("Adding lecture...");

                if(updateFlag) mProgressDialog.setMessage("Updating lecture...");

                mProgressDialog.show();

                //Create lecture object
                Lecture lecture = new Lecture();
                lecture.setDate(date);
                lecture.setStartTime(time);
                lecture.setDuration(duration);
                lecture.setModuleID(module_ids.get(modules.indexOf(moduleSelection)));
                lecture.setRoomID(room_ids.get(rooms.indexOf(roomSelection)));

                Call<ResponseBody> call = lectureClient.addLecture(token, lecture);

                //If updating existing lecture
                if(updateFlag) {
                    call = lectureClient.updateLecture(token, lecture_obj.getLectureID(), lecture);
                    lecture.setLectureID(lecture_obj.getLectureID());
                }

                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        //Successfully added
                        if (response.code() == 201 || response.code() == 200) {

                            if(updateFlag){
                                Toast.makeText(AddLectureActivity.this, "Successfully updated lecture!", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(AddLectureActivity.this, "Successfully added lecture!", Toast.LENGTH_SHORT).show();
                            }

                            mProgressDialog.dismiss();

                            //Direct to ManageModulesActivity
                            Intent intent = new Intent(AddLectureActivity.this, ManageLecturesActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        } else {

                            try {

                                // Capture an display specific messages
                                JSONObject obj = new JSONObject(response.errorBody().string());
                                Toast.makeText(AddLectureActivity.this, obj.getString("message"), Toast.LENGTH_SHORT).show();

                            } catch (Exception e) {
                                Toast.makeText(AddLectureActivity.this, "An error occurred", Toast.LENGTH_SHORT).show();
                            }

                            mProgressDialog.dismiss();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(AddLectureActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                        mProgressDialog.dismiss();
                    }
                });

            }

        }else {
            Toast.makeText(this, "Form wasn't setup properly!", Toast.LENGTH_SHORT).show();
        }

    }
}