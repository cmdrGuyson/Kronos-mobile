package com.guyson.kronos;


import android.util.Log;

import com.guyson.kronos.model.Class;
import com.guyson.kronos.model.Lecture;
import com.guyson.kronos.model.Lecturer;
import com.guyson.kronos.model.LoginCredentials;
import com.guyson.kronos.model.Module;
import com.guyson.kronos.model.Room;
import com.guyson.kronos.model.User;
import com.guyson.kronos.service.ClassClient;
import com.guyson.kronos.service.LectureClient;
import com.guyson.kronos.service.LecturerClient;
import com.guyson.kronos.service.ModuleClient;
import com.guyson.kronos.service.RetrofitClientInstance;
import com.guyson.kronos.service.RoomClient;
import com.guyson.kronos.service.UserClient;

import org.junit.Test;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ServicesTestSuit {
    @Test
    public void testLoginStudent() {
        UserClient userClient = RetrofitClientInstance.getRetrofitInstance().create(UserClient.class);
        LoginCredentials loginCredentials = new LoginCredentials("TEST_STUDENT", "password");
        Call<User> call = userClient.login(loginCredentials);

        try {
            Response<User> response = call.execute();
            User user = response.body();

            assertTrue("Login as Student",response.isSuccessful() && user.getRole().equals("student"));

            System.out.println("Login as Student:\tPASSED");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testLoginAdmin() {
        UserClient userClient = RetrofitClientInstance.getRetrofitInstance().create(UserClient.class);
        LoginCredentials loginCredentials = new LoginCredentials("TEST_ADMIN", "password");
        Call<User> call = userClient.login(loginCredentials);

        try {
            Response<User> response = call.execute();
            User user = response.body();

            assertTrue("Login as Admin", response.isSuccessful() && user.getRole().equals("admin"));

            System.out.println("Login as Admin:\t\tPASSED");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetLecturers() {
        LecturerClient lecturerClient = RetrofitClientInstance.getRetrofitInstance().create(LecturerClient.class);
        Call<List<Lecturer>> call = lecturerClient.getLecturers();

        try {
            Response<List<Lecturer>> response = call.execute();
            List<Lecturer> lecturers = response.body();

            assertTrue("Get All Lecturers",response.isSuccessful() && lecturers.size()==2);

            System.out.println("Get All Lecturers:\tPASSED");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetClasses() {
        ClassClient classClient = RetrofitClientInstance.getRetrofitInstance().create(ClassClient.class);
        Call<List<Class>> call = classClient.getClasses();

        try {
            Response<List<Class>> response = call.execute();
            List<Class> classes = response.body();

            assertTrue("Get All Classes",response.isSuccessful() && classes.size()==2);

            System.out.println("Get All Classes:\tPASSED");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetLectures() {
        LectureClient lectureClient = RetrofitClientInstance.getRetrofitInstance().create(LectureClient.class);
        Call<List<Lecture>> call = lectureClient.getAllLectures();

        try {
            Response<List<Lecture>> response = call.execute();
            List<Lecture> lectures = response.body();

            assertTrue("Get All Lectures",response.isSuccessful() && lectures.size()==4);

            System.out.println("Get All Lectures:\tPASSED");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetModules() {
        ModuleClient userClient = RetrofitClientInstance.getRetrofitInstance().create(ModuleClient.class);
        Call<List<Module>> call = userClient.getModules();

        try {
            Response<List<Module>> response = call.execute();
            List<Module> modules = response.body();

            assertTrue("Get All Modules",response.isSuccessful() && modules.size()==2);

            System.out.println("Get All Modules:\tPASSED");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetRooms() {
        RoomClient roomClient = RetrofitClientInstance.getRetrofitInstance().create(RoomClient.class);
        Call<List<Room>> call = roomClient.getRooms();

        try {
            Response<List<Room>> response = call.execute();
            List<Room> rooms = response.body();

            assertTrue("Get All Rooms",response.isSuccessful() && rooms.size()==2);

            System.out.println("Get All Rooms:\t\tPASSED");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetStudents() {
        UserClient userClient = RetrofitClientInstance.getRetrofitInstance().create(UserClient.class);
        Call<List<User>> call = userClient.getStudents();

        try {
            Response<List<User>> response = call.execute();
            List<User> students = response.body();

            assertTrue("Get All Students",response.isSuccessful() && students.size()==3);

            System.out.println("Get All Students:\tPASSED");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}