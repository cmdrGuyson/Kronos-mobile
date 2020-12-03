package com.guyson.kronos;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import com.auth0.android.jwt.JWT;
import com.google.android.material.navigation.NavigationView;

public class ManageLecturersActivity extends AppCompatActivity  implements NavigationView.OnNavigationItemSelectedListener {

    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;

    private String token;
    private SharedPreferences sharedPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_lecturers);

        //Check if authorization token is valid
        validateToken();

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
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {

            case R.id.nav_logout: {
                //Logout button

                SharedPreferences.Editor editor = sharedPrefs.edit();
                editor.putString("auth_token", null);
                editor.putString("role", null);
                editor.apply();

                Intent accountIntent = new Intent(ManageLecturersActivity.this, LoginActivity.class);
                accountIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(accountIntent);
                break;
            }
        }

        //close navigation drawer
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    private void validateToken() {
        sharedPrefs = ManageLecturersActivity.this.getSharedPreferences("auth_preferences", Context.MODE_PRIVATE);
        token = sharedPrefs.getString("auth_token", null);

        if(token!=null){
            JWT jwt = new JWT(token);
            boolean isExpired = jwt.isExpired(10);

            //Check if JWT has expired
            if(isExpired){
                Intent intent = new Intent(ManageLecturersActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }

        }else{
            Intent intent = new Intent(ManageLecturersActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }
}