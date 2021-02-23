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
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.guyson.kronos.AddLecturerActivity;
import com.guyson.kronos.ManageLecturersActivity;
import com.guyson.kronos.R;
import com.guyson.kronos.model.Lecturer;
import com.guyson.kronos.service.LecturerClient;
import com.guyson.kronos.service.RetrofitClientInstance;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LecturerAdapter extends RecyclerView.Adapter<LecturerAdapter.ViewHolder> implements Filterable {

    private Context context;
    private List<Lecturer> lecturers;
    private List<Lecturer> filteredLecturers;
    private String token;
    private ProgressDialog mProgressDialog;

    //Lecturer Retrofit Client
    LecturerClient lecturerClient = RetrofitClientInstance.getRetrofitInstance().create(LecturerClient.class);

    public LecturerAdapter(Context context, List<Lecturer> lecturers, String token, ProgressDialog mProgressDialog) {
        this.context = context;
        this.lecturers = lecturers;
        this.token = token;
        this.mProgressDialog = mProgressDialog;
    }

    public void setLecturers(final List<Lecturer> lecturers){
        if(this.lecturers == null){
            this.lecturers = lecturers;
            this.filteredLecturers = lecturers;

            //Alert a change in items
            notifyItemChanged(0, filteredLecturers.size());

        }
        //If updating items (previously not null)
        else {
            final DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                @Override
                public int getOldListSize() {
                    return LecturerAdapter.this.lecturers.size();
                }

                @Override
                public int getNewListSize() {
                    return lecturers.size();
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    return LecturerAdapter.this.lecturers.get(oldItemPosition).getLecturerID() == lecturers.get(newItemPosition).getLecturerID();
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {

                    Lecturer newLecturer = LecturerAdapter.this.lecturers.get(oldItemPosition);

                    Lecturer oldLecturer = lecturers.get(newItemPosition);

                    return newLecturer.getLecturerID() == oldLecturer.getLecturerID() ;
                }
            });
            this.lecturers = lecturers;
            this.filteredLecturers = lecturers;
            result.dispatchUpdatesTo(this);
        }
    }

    @NonNull
    @Override
    public LecturerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.lecturer_row, parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LecturerAdapter.ViewHolder holder, final int position) {
        holder.mName.setText(String.format("%s %s", filteredLecturers.get(position).getFirstName(), filteredLecturers.get(position).getLastName()));
        holder.mEmail.setText(filteredLecturers.get(position).getEmail());
        holder.mID.setText(String.valueOf(filteredLecturers.get(position).getLecturerID()));
        holder.mType.setText(filteredLecturers.get(position).getType().toUpperCase());

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                deleteLecturer(filteredLecturers.get(position).getLecturerID());
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        if(filteredLecturers != null ) return filteredLecturers.size();
        return 0;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    filteredLecturers = lecturers;
                } else {
                    List<Lecturer> filteredList = new ArrayList<>();
                    for (Lecturer user : lecturers) {
                        String searchString = charString.toLowerCase();

                        //Filter through fields and add to filtered list
                        if (user.getFirstName().toLowerCase().contains(searchString) || user.getLastName().toLowerCase().contains(searchString) || user.getType().toLowerCase().contains(searchString) || String.valueOf(user.getLecturerID()).contains(searchString) || user.getEmail().toLowerCase().contains(searchString)) {
                            filteredList.add(user);
                        }
                    }
                    filteredLecturers = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredLecturers;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filteredLecturers = (ArrayList<Lecturer>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView mName, mEmail, mType, mID;

        public ViewHolder(View itemView) {
            super(itemView);

            mID = itemView.findViewById(R.id.id);
            mName = itemView.findViewById(R.id.name);
            mEmail = itemView.findViewById(R.id.email);
            mType = itemView.findViewById(R.id.type);

        }
    }

    //Method to delete lecturers
    private void deleteLecturer(final int id) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
        builder.setTitle("Delete lecturer");
        builder.setMessage("Are you sure that you want delete "+id+" ?");

        //When "Delete" button is clicked
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Call<ResponseBody> call = lecturerClient.deleteLecturer(token, id);

                //Show progress
                mProgressDialog.setMessage("Deleting...");
                mProgressDialog.show();

                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                        //Successfully added
                        if (response.code()==200) {
                            Toast.makeText(context, "Successfully deleted!", Toast.LENGTH_SHORT).show();

                            //Reload lecturer list
                            ManageLecturersActivity activity = (ManageLecturersActivity) context;
                            activity.getAllLecturers();

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
