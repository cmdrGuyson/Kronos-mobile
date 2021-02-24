package com.guyson.kronos;

import androidx.annotation.NonNull;
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
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.Toast;

import com.auth0.android.jwt.JWT;
import com.google.android.material.navigation.NavigationView;
import com.guyson.kronos.adapter.LecturerAdapter;
import com.guyson.kronos.adapter.ModuleAdapter;
import com.guyson.kronos.model.Module;
import com.guyson.kronos.service.RetrofitClientInstance;
import com.guyson.kronos.service.ModuleClient;
import com.guyson.kronos.util.AuthHandler;
import com.guyson.kronos.util.NavHandler;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyModulesActivity extends AppCompatActivity  implements NavigationView.OnNavigationItemSelectedListener {

    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private ProgressDialog mProgressDialog;

    private RecyclerView recyclerView;
    private ModuleAdapter moduleAdapter;
    private SearchView searchView;

    private List<Module> modules;
    private String token;

    private ModuleClient userClient = RetrofitClientInstance.getRetrofitInstance().create(ModuleClient.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_modules);

        //Check if authorization token is valid
        AuthHandler.validate(MyModulesActivity.this, "student");

        //Retrieve JWT Token
        SharedPreferences sharedPreferences = getSharedPreferences("auth_preferences", Context.MODE_PRIVATE);
        token = "Bearer "+sharedPreferences.getString("auth_token", null);

        mProgressDialog = new ProgressDialog(this);

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

        //Setup modules list
        modules = new ArrayList<>();
        recyclerView = findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        moduleAdapter = new ModuleAdapter(this, modules, "student", token, mProgressDialog);
        recyclerView.setAdapter(moduleAdapter);

        getAllModules();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        //Handle side drawer navigation
        NavHandler.handleStudentNav(item, MyModulesActivity.this);

        //close navigation drawer
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    private void getAllModules() {
        Call<List<Module>> call = userClient.getMyModules(token);

        //Show progress
        mProgressDialog.setMessage("Loading modules...");
        mProgressDialog.show();

        call.enqueue(new Callback<List<Module>>() {
            @Override
            public void onResponse(Call<List<Module>> call, Response<List<Module>> response) {
                modules = response.body();
                if(modules!=null){
                    moduleAdapter.setModules(modules);
                }else{
                    Toast.makeText(MyModulesActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                }
                mProgressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<List<Module>> call, Throwable t) {
                Toast.makeText(MyModulesActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                mProgressDialog.dismiss();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_options_menu, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                moduleAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                moduleAdapter.getFilter().filter(query);
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
        }

        return super.onOptionsItemSelected(item);
    }
}