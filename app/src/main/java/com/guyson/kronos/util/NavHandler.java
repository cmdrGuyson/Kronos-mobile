package com.guyson.kronos.util;

import android.content.Context;
import android.content.Intent;
import android.view.MenuItem;

import com.guyson.kronos.MainActivity;
import com.guyson.kronos.ManageClassesActivity;
import com.guyson.kronos.ManageLecturersActivity;
import com.guyson.kronos.ManageLecturesActivity;
import com.guyson.kronos.ManageModulesActivity;
import com.guyson.kronos.ManageRoomsActivity;
import com.guyson.kronos.ManageStudentsActivity;
import com.guyson.kronos.MyModulesActivity;
import com.guyson.kronos.R;
import com.guyson.kronos.SettingsActivity;
import com.guyson.kronos.ViewModulesActivity;

public class NavHandler {

    public static void handleAdminNav(MenuItem item, Context context) {
        switch (item.getItemId()) {

            case R.id.nav_logout: {
                //Logout button
                AuthHandler.logout(context);
                break;
            }
            case R.id.nav_manage_classes: {
                //Manage Classes button
                Intent intent = new Intent(context, ManageClassesActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(intent);
                break;
            }
            case R.id.nav_manage_rooms: {
                //Manage Lecturers button
                Intent intent = new Intent(context, ManageRoomsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(intent);
                break;
            }
            case R.id.nav_manage_students: {
                //Manage Students button
                Intent intent = new Intent(context, ManageStudentsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(intent);
                break;
            }
            case R.id.nav_settings: {
                //Settings button
                Intent intent = new Intent(context, SettingsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(intent);
                break;
            }

        }
    }

    public static void handleAcademicAdminNav(MenuItem item, Context context) {
        switch (item.getItemId()) {

            case R.id.nav_logout: {
                //Logout button
                AuthHandler.logout(context);
                break;
            }
            case R.id.nav_manage_lecturers: {
                //Manage Lecturers button
                Intent intent = new Intent(context, ManageLecturersActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(intent);
                break;
            }
            case R.id.nav_manage_modules: {
                //Manage Modules Button
                Intent intent = new Intent(context, ManageModulesActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(intent);
                break;
            } case R.id.nav_settings: {
                //Settings button
                Intent intent = new Intent(context, SettingsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(intent);
                break;
            } case R.id.nav_manage_lectures: {
                //Manage Lectures Button
                Intent intent = new Intent(context, ManageLecturesActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(intent);
                break;
            }

        }
    }

    public static void handleStudentNav(MenuItem item, Context context) {
        switch (item.getItemId()) {

            case R.id.nav_logout: {
                //Logout button
                AuthHandler.logout(context);
                break;
            }
            case R.id.nav_settings: {
                //Settings button
                Intent intent = new Intent(context, SettingsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(intent);
                break;
            }
            case R.id.nav_timetable: {
                //Timetable button
                Intent intent = new Intent(context, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(intent);
                break;
            }
            case R.id.nav_enroll: {
                //Enroll button
                Intent intent = new Intent(context, ViewModulesActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(intent);
                break;
            }
            case R.id.nav_my_modules: {
                //My Modules Button
                Intent intent = new Intent(context, MyModulesActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(intent);
                break;
            }
        }
    }

}
