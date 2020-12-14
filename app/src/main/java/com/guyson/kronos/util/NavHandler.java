package com.guyson.kronos.util;

import android.content.Context;
import android.content.Intent;
import android.view.MenuItem;

import com.guyson.kronos.ManageClassesActivity;
import com.guyson.kronos.ManageLecturersActivity;
import com.guyson.kronos.R;

public class NavHandler {

    public static void handleAdminNav(MenuItem item, Context context) {
        switch (item.getItemId()) {

            case R.id.nav_logout: {
                //Logout button
                AuthHandler.logout(context);
                break;
            }
            case R.id.nav_manage_classes: {
                //Manage Lecturers button
                Intent intent = new Intent(context, ManageClassesActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(intent);
            }
            case R.id.nav_manage_lecturers: {
                //Manage Lecturers button
                Intent intent = new Intent(context, ManageLecturersActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(intent);
            }
        }
    }

}
