package com.guyson.kronos.util;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.guyson.kronos.model.Lecture;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class ExtraUtilities {

    //Get list of CalendarDays to be used to highlight the calendar from lectures
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static List<CalendarDay> getCalendarDays(List<Lecture> lectures) {
        List<CalendarDay> calendarDays = new ArrayList<>();
        for (Lecture lecture : lectures) {

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            LocalDate date = LocalDate.parse(lecture.getDate(), formatter);
            //Log.i("DATE", date.toString());
            calendarDays.add(CalendarDay.from(date.getYear(), date.getMonthValue(), date.getDayOfMonth()));
        }

        return calendarDays;
    }

    //Get string date to be used in API call from CalendarDay
    public static String getStringDate(CalendarDay date) {

        String day = date.getDay() < 10 ? "0"+date.getDay() : String.valueOf(date.getDay());
        String month = date.getMonth() < 10 ? "0"+date.getMonth() : String.valueOf(date.getMonth());

        return day+"-"+month+"-"+date.getYear();
    }

    //Method to validate emails
    public static boolean isEmailValid(String email) {
        final Pattern EMAIL_REGEX = Pattern.compile("[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?", Pattern.CASE_INSENSITIVE);
        return EMAIL_REGEX.matcher(email).matches();
    }

    //Capitalize first letter of string
    public static String capitalize(String str)
    {
        if(str == null) return null;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    //Capitalize first letter of all words
    public static String toTitleCase(String givenString) {
        String[] arr = givenString.split(" ");
        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < arr.length; i++) {
            sb.append(Character.toUpperCase(arr[i].charAt(0)))
                    .append(arr[i].substring(1)).append(" ");
        }
        return sb.toString().trim();
    }

}
