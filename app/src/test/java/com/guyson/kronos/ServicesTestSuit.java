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

    private final String ADMIN_USERNAME = "ADMIN001";
    private final String ADMIN_PASSWORD = "password";
    private final String ACADEMIC_ADMIN_USERNAME = "ADMIN002";
    private final String ACADEMIC_ADMIN_PASSWORD = "password";
    private final String STUDENT_USERNAME = "CB6964";
    private final String STUDENT_PASSWORD = "CB6964";

    @Test
    public void testLoginStudent() {
        UserClient userClient = RetrofitClientInstance.getRetrofitInstance().create(UserClient.class);
        LoginCredentials loginCredentials = new LoginCredentials(STUDENT_USERNAME, STUDENT_PASSWORD);
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
    public void testLoginWithInvalidUsername() {
        UserClient userClient = RetrofitClientInstance.getRetrofitInstance().create(UserClient.class);
        LoginCredentials loginCredentials = new LoginCredentials("std", STUDENT_PASSWORD);
        Call<User> call = userClient.login(loginCredentials);

        try {
            Response<User> response = call.execute();

            assertTrue("Login with Invalid Username", response.code() == 403);

            System.out.println("Login with Invalid Username:\tPASSED");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testLoginWithInvalidPassword() {
        UserClient userClient = RetrofitClientInstance.getRetrofitInstance().create(UserClient.class);
        LoginCredentials loginCredentials = new LoginCredentials(STUDENT_USERNAME, "pass");
        Call<User> call = userClient.login(loginCredentials);

        try {
            Response<User> response = call.execute();

            assertTrue("Login with Invalid Password", response.code() == 403);

            System.out.println("Login with Invalid Password:\tPASSED");

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testLoginAdmin() {
        UserClient userClient = RetrofitClientInstance.getRetrofitInstance().create(UserClient.class);
        LoginCredentials loginCredentials = new LoginCredentials(ADMIN_USERNAME, ADMIN_PASSWORD);
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

        UserClient userClient = RetrofitClientInstance.getRetrofitInstance().create(UserClient.class);
        LoginCredentials loginCredentials = new LoginCredentials(ACADEMIC_ADMIN_USERNAME, ACADEMIC_ADMIN_PASSWORD);

        Call<User> login_call = userClient.login(loginCredentials);

        try {

            Response<User> login_response = login_call.execute();
            String token = login_response.body().getToken();

            Call<List<Lecturer>> call = lecturerClient.getLecturers(token);

            Response<List<Lecturer>> response = call.execute();
            List<Lecturer> lecturers = response.body();

            assertTrue("Get All Lecturers",response.isSuccessful());

            System.out.println("Get All Lecturers:\tPASSED");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetClasses() {
        ClassClient classClient = RetrofitClientInstance.getRetrofitInstance().create(ClassClient.class);

        UserClient userClient = RetrofitClientInstance.getRetrofitInstance().create(UserClient.class);
        LoginCredentials loginCredentials = new LoginCredentials(ADMIN_USERNAME, ADMIN_PASSWORD);

        Call<User> login_call = userClient.login(loginCredentials);

        try {

            Response<User> login_response = login_call.execute();
            String token = login_response.body().getToken();

            Call<List<Class>> call = classClient.getClasses(token);

            Response<List<Class>> response = call.execute();
            List<Class> classes = response.body();

            assertTrue("Get All Classes",response.isSuccessful());

            System.out.println("Get All Classes:\tPASSED");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetLectures() {
        LectureClient lectureClient = RetrofitClientInstance.getRetrofitInstance().create(LectureClient.class);

        UserClient userClient = RetrofitClientInstance.getRetrofitInstance().create(UserClient.class);
        LoginCredentials loginCredentials = new LoginCredentials(ACADEMIC_ADMIN_USERNAME, ACADEMIC_ADMIN_PASSWORD);

        Call<User> login_call = userClient.login(loginCredentials);


        try {

            Response<User> login_response = login_call.execute();
            String token = login_response.body().getToken();

            Call<List<Lecture>> call = lectureClient.getAllLectures(token);

            Response<List<Lecture>> response = call.execute();
            List<Lecture> lectures = response.body();

            assertTrue("Get All Lectures",response.isSuccessful());

            System.out.println("Get All Lectures:\tPASSED");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetModules() {
        ModuleClient moduleClient = RetrofitClientInstance.getRetrofitInstance().create(ModuleClient.class);

        UserClient userClient = RetrofitClientInstance.getRetrofitInstance().create(UserClient.class);
        LoginCredentials loginCredentials = new LoginCredentials(ACADEMIC_ADMIN_USERNAME, ACADEMIC_ADMIN_PASSWORD);

        Call<User> login_call = userClient.login(loginCredentials);

        try {
            Response<User> login_response = login_call.execute();
            String token = login_response.body().getToken();

            Call<List<Module>> call = moduleClient.getModules(token);

            Response<List<Module>> response = call.execute();
            List<Module> modules = response.body();

            assertTrue("Get All Modules",response.isSuccessful());

            System.out.println("Get All Modules:\tPASSED");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetStudentModules() {
        ModuleClient moduleClient = RetrofitClientInstance.getRetrofitInstance().create(ModuleClient.class);

        UserClient userClient = RetrofitClientInstance.getRetrofitInstance().create(UserClient.class);
        LoginCredentials loginCredentials = new LoginCredentials(STUDENT_USERNAME, STUDENT_PASSWORD);

        Call<User> login_call = userClient.login(loginCredentials);

        try {
            Response<User> login_response = login_call.execute();
            String token = login_response.body().getToken();

            Call<List<Module>> call = moduleClient.getStudentModules(token);

            Response<List<Module>> response = call.execute();
            List<Module> modules = response.body();

            assertTrue("Get Student Modules",response.isSuccessful());

            System.out.println("Get Student Modules:\tPASSED");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetMyModules() {
        ModuleClient moduleClient = RetrofitClientInstance.getRetrofitInstance().create(ModuleClient.class);

        UserClient userClient = RetrofitClientInstance.getRetrofitInstance().create(UserClient.class);
        LoginCredentials loginCredentials = new LoginCredentials(STUDENT_USERNAME, STUDENT_PASSWORD);

        Call<User> login_call = userClient.login(loginCredentials);

        try {
            Response<User> login_response = login_call.execute();
            String token = login_response.body().getToken();

            Call<List<Module>> call = moduleClient.getMyModules(token);

            Response<List<Module>> response = call.execute();
            List<Module> modules = response.body();

            assertTrue("Get My Modules",response.isSuccessful());

            System.out.println("Get My Modules:\tPASSED");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetRooms() {
        RoomClient roomClient = RetrofitClientInstance.getRetrofitInstance().create(RoomClient.class);

        UserClient userClient = RetrofitClientInstance.getRetrofitInstance().create(UserClient.class);
        LoginCredentials loginCredentials = new LoginCredentials(ADMIN_USERNAME, ADMIN_PASSWORD);

        Call<User> login_call = userClient.login(loginCredentials);

        try {

            Response<User> login_response = login_call.execute();
            String token = login_response.body().getToken();

            Call<List<Room>> call = roomClient.getRooms(token);

            Response<List<Room>> response = call.execute();
            List<Room> rooms = response.body();

            assertTrue("Get All Rooms",response.isSuccessful());

            System.out.println("Get All Rooms:\t\tPASSED");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetStudents() {
        UserClient userClient = RetrofitClientInstance.getRetrofitInstance().create(UserClient.class);
        LoginCredentials loginCredentials = new LoginCredentials(ADMIN_USERNAME, ADMIN_PASSWORD);

        Call<User> login_call = userClient.login(loginCredentials);

        try {

            Response<User> login_response = login_call.execute();
            String token = login_response.body().getToken();

            Call<List<User>> call = userClient.getStudents(token);

            Response<List<User>> response = call.execute();
            List<User> students = response.body();

            assertTrue("Get All Students",response.isSuccessful());

            System.out.println("Get All Students:\tPASSED");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}