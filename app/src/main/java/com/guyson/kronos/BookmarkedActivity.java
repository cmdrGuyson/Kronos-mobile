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
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.guyson.kronos.adapter.BookmarkAdapter;
import com.guyson.kronos.adapter.ClassAdapter;
import com.guyson.kronos.model.Bookmark;
import com.guyson.kronos.model.Class;
import com.guyson.kronos.model.Lecture;
import com.guyson.kronos.service.ClassClient;
import com.guyson.kronos.service.RetrofitClientInstance;
import com.guyson.kronos.util.AuthHandler;
import com.guyson.kronos.util.NavHandler;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookmarkedActivity extends AppCompatActivity  implements NavigationView.OnNavigationItemSelectedListener {

    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private ProgressDialog mProgressDialog;

    private RecyclerView recyclerView;
    private SearchView searchView;
    private BookmarkAdapter bookmarkAdapter;

    private List<Bookmark> bookmarks;

    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmarked);

        //Check if authorization token is valid
        AuthHandler.validate(BookmarkedActivity.this, "student");

        //Retrieve username
        SharedPreferences sharedPreferences = getSharedPreferences("auth_preferences", Context.MODE_PRIVATE);
        username = sharedPreferences.getString("username", null);

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

        //Setup classes list
        bookmarks = new ArrayList<>();
        recyclerView = findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        bookmarkAdapter = new BookmarkAdapter(this, bookmarks, mProgressDialog);
        recyclerView.setAdapter(bookmarkAdapter);

        getAllBookmarks();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        //Handle side drawer navigation
        NavHandler.handleStudentNav(item, BookmarkedActivity.this);

        //close navigation drawer
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    private void getAllBookmarks() {
        //Show progress
        mProgressDialog.setMessage("Loading bookmarks...");
        mProgressDialog.show();

        Cursor cursor = getContentResolver().query(Uri.parse("content://com.guyson.kronos.provider/bookmarks"), null, null, null, null);

        // iteration of the cursor
        // to print whole table
        if(cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                if(cursor.getString(cursor.getColumnIndex("owner")).equals(username)) {
                    Bookmark bookmark = new Bookmark();
                    bookmark.setLectureID(cursor.getInt(cursor.getColumnIndex("lectureID")));
                    bookmark.setRoomID(cursor.getInt(cursor.getColumnIndex("roomID")));
                    bookmark.setDuration(cursor.getInt(cursor.getColumnIndex("duration")));
                    bookmark.setDate(cursor.getString(cursor.getColumnIndex("date")));
                    bookmark.setStartTime(cursor.getString(cursor.getColumnIndex("startTime")));
                    bookmark.setPriority(cursor.getString(cursor.getColumnIndex("priority")));
                    bookmark.setModule(cursor.getString(cursor.getColumnIndex("module")));
                    bookmarks.add(bookmark);
                }
                cursor.moveToNext();
            }
            bookmarkAdapter.setBookmarks(bookmarks);
        }
        else {
            Toast.makeText(this, "You have no bookmarks!", Toast.LENGTH_SHORT).show();
        }

        if (bookmarks.size()==0) Toast.makeText(this, "You have no bookmarks!", Toast.LENGTH_SHORT).show();
        mProgressDialog.dismiss();
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
                bookmarkAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                bookmarkAdapter.getFilter().filter(query);
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
