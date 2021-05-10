package com.guyson.kronos.adapter;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.guyson.kronos.AddLectureActivity;
import com.guyson.kronos.MainActivity;
import com.guyson.kronos.ManageLecturesActivity;
import com.guyson.kronos.R;
import com.guyson.kronos.model.Lecture;
import com.guyson.kronos.provider.BookmarksContentProvider;
import com.guyson.kronos.service.LectureClient;
import com.guyson.kronos.service.RetrofitClientInstance;

import org.json.JSONObject;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LectureAdapter extends RecyclerView.Adapter<LectureAdapter.ViewHolder> implements Filterable {

    private Context context;
    private List<Lecture> lectures;
    private List<Lecture> filteredLectures;
    private String role;
    private String token;

    private String username;
    private ProgressDialog mProgressDialog;

    private final int MY_PERMISSIONS_REQUEST_WRITE_CALENDAR = 1;

    private LectureClient lectureClient = RetrofitClientInstance.getRetrofitInstance().create(LectureClient.class);

    private SimpleDateFormat DATE_TIME_FORMATTER = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault());

    public LectureAdapter(Context context, List<Lecture> lectures, String role, String token, ProgressDialog mProgressDialog) {
        this.context = context;
        this.lectures = lectures;
        this.role = role;
        this.token = token;
        this.mProgressDialog = mProgressDialog;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setLectures(final List<Lecture> lectures){
        if(this.lectures == null){
            this.lectures = lectures;
            this.filteredLectures = lectures;

            //Alert change in items
            notifyItemChanged(0, filteredLectures.size());
        }
        //If updating items (previously not null)
        else {
            final DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                @Override
                public int getOldListSize() {
                    return LectureAdapter.this.lectures.size();
                }

                @Override
                public int getNewListSize() {
                    return lectures.size();
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    return LectureAdapter.this.lectures.get(oldItemPosition).getLectureID() == lectures.get(newItemPosition).getLectureID();
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {

                    Lecture newLecture = LectureAdapter.this.lectures.get(oldItemPosition);

                    Lecture oldLecture = lectures.get(newItemPosition);

                    return newLecture.getLectureID() == oldLecture.getLectureID() ;
                }
            });
            this.lectures = lectures;
            this.filteredLectures = lectures;
            result.dispatchUpdatesTo(this);
        }
    }

    @NonNull
    @Override
    public LectureAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.lecture_row, parent,false);
        return new LectureAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final LectureAdapter.ViewHolder holder, final int position) {

        holder.mModule.setText(filteredLectures.get(position).getModule().getName());
        holder.mRoom.setText(String.valueOf(filteredLectures.get(position).getRoom().getRoomID()));
        holder.mTime.setText(String.format("%s (%s hours)", filteredLectures.get(position).getStartTime(), filteredLectures.get(position).getDuration()));
        holder.mLecturer.setText(String.format("%s %s", filteredLectures.get(position).getModule().getLecturer().getFirstName(), filteredLectures.get(position).getModule().getLecturer().getLastName()));

        if(role.equals("admin")) {
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    deleteLecture(filteredLectures.get(position));
                    return false;
                }
            });
        }else{
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    handleStudentOnHold(filteredLectures.get(position));
                    return false;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if(filteredLectures != null ) return filteredLectures.size();
        return 0;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    filteredLectures = lectures;
                } else {
                    List<Lecture> filteredList = new ArrayList<>();
                    for (Lecture lecture : lectures) {
                        String searchKey = charString.toLowerCase();
                        if (lecture.getModule().getName().toLowerCase().contains(searchKey)||String.valueOf(lecture.getLectureID()).toLowerCase().contains(searchKey) || lecture.getModule().getLecturer().getLastName().toLowerCase().contains(searchKey) || lecture.getModule().getLecturer().getFirstName().toLowerCase().contains(searchKey)) {
                            filteredList.add(lecture);
                        }
                    }
                    filteredLectures = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredLectures;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filteredLectures = (ArrayList<Lecture>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView mModule, mTime, mRoom, mLecturer;
        CardView mCardView;

        public ViewHolder(View itemView) {
            super(itemView);

            mModule = itemView.findViewById(R.id.module);
            mLecturer = itemView.findViewById(R.id.lecturer);
            mTime = itemView.findViewById(R.id.time);
            mRoom = itemView.findViewById(R.id.room);

        }
    }

    private void deleteLecture(final Lecture lecture) {

        MaterialAlertDialogBuilder option_builder = new MaterialAlertDialogBuilder(context);
        option_builder.setTitle("Please select option");
        option_builder.setSingleChoiceItems(new CharSequence[] {"Update", "Delete"}, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //change selected category
                dialogInterface.dismiss();

                //Update lecture
                if(i==0) {

                    //Direct to Add Lecture activity with Lecture object to Update
                    Intent intent = new Intent(context, AddLectureActivity.class);
                    intent.putExtra("lecture_obj", (Serializable) lecture);
                    context.startActivity(intent);

                }
                //Delete Lecture
                else {
                    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
                    builder.setTitle("Delete Lecture");
                    builder.setMessage("Are you sure that you want lecture "+lecture.getLectureID()+" ?");

                    //When "Delete" button is clicked
                    builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                        @RequiresApi(api = Build.VERSION_CODES.O)
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Call<ResponseBody> call = lectureClient.deleteLecture(token, lecture.getLectureID());

                            //Show progress
                            mProgressDialog.setMessage("Deleting...");
                            mProgressDialog.show();

                            call.enqueue(new Callback<ResponseBody>() {
                                @Override
                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                                    //Successfully deleted
                                    if (response.code()==200) {
                                        Toast.makeText(context, "Successfully deleted!", Toast.LENGTH_SHORT).show();

                                        //Reload lecture list
                                        ManageLecturesActivity activity = (ManageLecturesActivity) context;
                                        activity.dateChangeHandler();
                                        activity.getAllLectures();

                                    }
                                    else {
                                        try {

                                            // Capture an display specific messages
                                            JSONObject obj = new JSONObject(response.errorBody().string());
                                            Toast.makeText(context, obj.getString("message"), Toast.LENGTH_SHORT).show();

                                        }catch(Exception e) {
                                            Toast.makeText(context, "An error occurred", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    mProgressDialog.dismiss();

                                }

                                @Override
                                public void onFailure(Call<ResponseBody> call, Throwable t) {
                                    Toast.makeText(context, "Something went wrong!", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });

                    //When cancel button is clicked
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.show();
                }
            }
        });
        option_builder.show();
    }

    private void handleStudentOnHold(final Lecture lecture) {

        MaterialAlertDialogBuilder option_builder = new MaterialAlertDialogBuilder(context);
        option_builder.setTitle("Please select option");
        option_builder.setSingleChoiceItems(new CharSequence[] {"Add to calendar", "Add to bookmarks"}, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //change selected category
                dialogInterface.dismiss();

                //Add to calendar
                if(i==0) {
                    try {
                        addLectureToCalendar(lecture);
                    } catch (ParseException e) {
                        Toast.makeText(context, "Something went wrong!", Toast.LENGTH_SHORT).show();
                    }
                }
                //Add to bookmarks
                else {
                    addToBookmarks(lecture);
                }
            }
        });
        option_builder.show();
    }

    private void addToBookmarks(Lecture lecture) {
        // class to add values in the database
        ContentValues values = new ContentValues();

        // Create values
        values.put(BookmarksContentProvider.DATE, lecture.getDate());
        values.put(BookmarksContentProvider.DURATION, lecture.getDuration());
        values.put(BookmarksContentProvider.OWNER, username);
        values.put(BookmarksContentProvider.PRIORITY, "normal");
        values.put(BookmarksContentProvider.START_TIME, lecture.getStartTime());
        values.put(BookmarksContentProvider.LECTURE_ID, lecture.getLectureID());
        values.put(BookmarksContentProvider.ROOM_ID, lecture.getRoomID());
        values.put(BookmarksContentProvider.MODULE, lecture.getModule().getName());
        values.put(BookmarksContentProvider.LECTURER, lecture.getModule().getLecturer().getFirstName() + " " + lecture.getModule().getLecturer().getLastName());


        try {

            // inserting into database through content URI
            context.getContentResolver().insert(BookmarksContentProvider.CONTENT_URI, values);

        }catch(SQLiteException ex){
            Toast.makeText(context, "Already bookmarked!", Toast.LENGTH_LONG).show();
        }


        // displaying a toast message
        Toast.makeText(context, "Added to bookmarks!", Toast.LENGTH_LONG).show();
    }

    private void addLectureToCalendar(Lecture lecture) throws ParseException {

        MainActivity activity = (MainActivity) context;

        //Request permissions
        //Request permissions
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            //Toast.makeText(context, "Please give calendar read permissions!", Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR }, MY_PERMISSIONS_REQUEST_WRITE_CALENDAR);
            return;
        }


        long startMillis = 0;
        long endMillis = 0;

        //Get start time in milliseconds
        Calendar start = Calendar.getInstance();
        start.setTime(Objects.requireNonNull(DATE_TIME_FORMATTER.parse(lecture.getDate() + " " + lecture.getStartTime())));
        startMillis = start.getTimeInMillis();

        //Get end time in milliseconds
        endMillis = startMillis + (lecture.getDuration()*60*60*1000);

        //Name of the event
        String eventTitle = lecture.getModule().getName()+" Lecture"+"-"+lecture.getLectureID();

        if (doesEventExist(startMillis, endMillis, eventTitle)) {
            Toast.makeText(context, "Calendar event already exists!", Toast.LENGTH_SHORT).show();
            return;
        }

        /** Add event to calendar **/

        long calID = 3;

        //Get content resolver and add setup calendar contract
        ContentResolver resolver = context.getContentResolver();
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.DTSTART, startMillis);
        values.put(CalendarContract.Events.DTEND, endMillis);
        values.put(CalendarContract.Events.TITLE, eventTitle);
        values.put(CalendarContract.Events.DESCRIPTION, "Kronos system. Lecture event");
        values.put(CalendarContract.Events.CALENDAR_ID, calID);
        values.put(CalendarContract.Events.EVENT_TIMEZONE, "Asia/Colombo");
        values.put(CalendarContract.Events.EVENT_COLOR, Color.RED);

        Log.i("Calendar", "Attempting to add");

        //Add calendar event
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED) {
            Uri uri = resolver.insert(CalendarContract.Events.CONTENT_URI, values);
            long eventID = Long.parseLong(uri.getLastPathSegment());
            Log.i("Calendar", "Event Created, ID: " + eventID);
            Toast.makeText(context, "Calendar event added!", Toast.LENGTH_SHORT).show();

        } else {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_CALENDAR}, MY_PERMISSIONS_REQUEST_WRITE_CALENDAR);
        }

    }

    //Method to check if calendar event already exists
    private boolean doesEventExist(long startMillis, long endMillis, String eventTitle) {
        final String[] INSTANCE_PROJECTION = new String[] {
                CalendarContract.Instances.EVENT_ID,      // 0
                CalendarContract.Instances.BEGIN,         // 1
                CalendarContract.Instances.TITLE          // 2
        };

        // The ID of the event whose instances searched for in the Instances table
        String selection = CalendarContract.Instances.TITLE + " = ?";
        String[] selectionArgs = new String[] {eventTitle};

        // Construct the query with the desired date range.
        Uri.Builder builder = CalendarContract.Instances.CONTENT_URI.buildUpon();
        ContentUris.appendId(builder, startMillis);
        ContentUris.appendId(builder, endMillis);

        // Submit the query
        Cursor cur =  context.getContentResolver().query(builder.build(), INSTANCE_PROJECTION, selection, selectionArgs, null);

        Log.i("Calendar", "Event exits");

        return cur.getCount() > 0;
    }
}

