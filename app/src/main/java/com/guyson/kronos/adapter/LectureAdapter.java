package com.guyson.kronos.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
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
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.guyson.kronos.AddLectureActivity;
import com.guyson.kronos.ManageClassesActivity;
import com.guyson.kronos.ManageLecturesActivity;
import com.guyson.kronos.R;
import com.guyson.kronos.model.Lecture;
import com.guyson.kronos.service.LectureClient;
import com.guyson.kronos.service.RetrofitClientInstance;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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
    private ProgressDialog mProgressDialog;

    private LectureClient lectureClient = RetrofitClientInstance.getRetrofitInstance().create(LectureClient.class);

    public LectureAdapter(Context context, List<Lecture> lectures, String role, String token, ProgressDialog mProgressDialog) {
        this.context = context;
        this.lectures = lectures;
        this.role = role;
        this.token = token;
        this.mProgressDialog = mProgressDialog;
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
}

