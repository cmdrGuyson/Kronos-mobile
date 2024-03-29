package com.guyson.kronos;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.guyson.kronos.adapter.ClassAdapter;
import com.guyson.kronos.adapter.LectureAdapter;
import com.guyson.kronos.model.Lecture;
import com.guyson.kronos.service.LectureClient;
import com.guyson.kronos.service.RetrofitClientInstance;
import com.guyson.kronos.util.AuthHandler;
import com.guyson.kronos.util.EventDecorator;
import com.guyson.kronos.util.ExtraUtilities;
import com.guyson.kronos.util.NavHandler;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ManageLecturesActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private ProgressDialog mProgressDialog;
    private MaterialCalendarView materialCalendarView;

    private RecyclerView recyclerView;
    private LectureAdapter lectureAdapter;
    private SearchView searchView;

    private List<Lecture> lectures;
    private boolean resultsRetrieved;
    private String token;
    private CalendarDay selectedDate;

    private LectureClient lectureClient = RetrofitClientInstance.getRetrofitInstance().create(LectureClient.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_lectures);

        //Check if authorization token is valid
        AuthHandler.validate(ManageLecturesActivity.this, "academic_admin");

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

        mProgressDialog = new ProgressDialog(this);

        mDrawerLayout.addDrawerListener(mActionBarDrawerToggle);
        mActionBarDrawerToggle.syncState();
        mNavigationView.setNavigationItemSelectedListener(this);

        materialCalendarView = findViewById(R.id.calendar_view);

        getAllLectures();

        //Setup lectures list
        resultsRetrieved = false;
        lectures = new ArrayList<>();
        recyclerView = findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        lectureAdapter = new LectureAdapter(this, lectures, "admin", token, mProgressDialog);
        recyclerView.setAdapter(lectureAdapter);

        //When a date is selected
        materialCalendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                selectedDate = date;
                dateChangeHandler();
            }
        });

        materialCalendarView.setDateSelected(CalendarDay.today(), true);
        selectedDate = CalendarDay.today();
        dateChangeHandler();
    }

    public void getAllLectures() {
        Call<List<Lecture>> call = lectureClient.getAllLectures(token);

        //Show progress
        mProgressDialog.setMessage("Loading timetable...");
        mProgressDialog.show();

        call.enqueue(new Callback<List<Lecture>>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(Call<List<Lecture>> call, Response<List<Lecture>> response) {
                lectures = response.body();
                if (lectures != null) {
                    //Toast.makeText(ManageLecturesActivity.this, "size: "+lectures.size(), Toast.LENGTH_SHORT).show();
                    resultsRetrieved = true;
                    setupTimetable();
                }else{
                    Toast.makeText(ManageLecturesActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                }
                mProgressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<List<Lecture>> call, Throwable t) {
                Toast.makeText(ManageLecturesActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                mProgressDialog.dismiss();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setupTimetable() {
        if(resultsRetrieved) {
            materialCalendarView.removeDecorators();
            materialCalendarView.addDecorators(new EventDecorator(Color.RED, ExtraUtilities.getCalendarDays(lectures)));
        }
    }

    public void dateChangeHandler() {

        Call<List<Lecture>> call = lectureClient.getAllLecturesByDate(token, ExtraUtilities.getStringDate(selectedDate));

        //Show progress
        mProgressDialog.setMessage("Loading lectures...");
        mProgressDialog.show();

        call.enqueue(new Callback<List<Lecture>>() {
            @Override
            public void onResponse(Call<List<Lecture>> call, Response<List<Lecture>> response) {
                lectures = response.body();
                if(lectures != null) {
                    if(lectures.size() == 0) {
                        Toast.makeText(ManageLecturesActivity.this, "No lectures for this day", Toast.LENGTH_SHORT).show();
                    }
                    lectureAdapter.setLectures(lectures);

                }else{
                    Toast.makeText(ManageLecturesActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                }
                mProgressDialog.dismiss();

            }

            @Override
            public void onFailure(Call<List<Lecture>> call, Throwable t) {
                Toast.makeText(ManageLecturesActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                mProgressDialog.dismiss();
            }
        });

        //Show progress
        mProgressDialog.setMessage("Loading timetable...");
        mProgressDialog.show();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        //Handle side drawer navigation
        NavHandler.handleAcademicAdminNav(item, ManageLecturesActivity.this);

        //close navigation drawer
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                lectureAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                lectureAdapter.getFilter().filter(query);
                return false;
            }
        });
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_search) {
            return true;
        }else if (id == R.id.add) {

            //Direct to AddClassActivity
            Intent intent = new Intent(ManageLecturesActivity.this, AddLectureActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Check if authorization token is valid
        AuthHandler.validate(ManageLecturesActivity.this, "academic_admin");
    }
}