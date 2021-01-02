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

public class ExtraUtilities {

    //Get list of CalendarDays to be used to highlight the calendar from lectures
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static List<CalendarDay> getCalendarDays(List<Lecture> lectures){
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
    public static String getStringDate(CalendarDay date){
        return String.format("%d-%d-%d", date.getDay(), date.getMonth(), date.getYear());
    }

}
