package com.guyson.kronos.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import com.guyson.kronos.ManageLecturersActivity;
import com.guyson.kronos.ManageStudentsActivity;
import com.guyson.kronos.R;
import com.guyson.kronos.model.User;
import com.guyson.kronos.service.RetrofitClientInstance;
import com.guyson.kronos.service.UserClient;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.ViewHolder> implements Filterable {

    private Context context;
    private List<User> students;
    private List<User> filteredStudents;
    private String token;
    private ProgressDialog mProgressDialog;

    private UserClient userClient = RetrofitClientInstance.getRetrofitInstance().create(UserClient.class);

    public StudentAdapter(Context context, List<User> students, String token, ProgressDialog mProgressDialog) {
        this.context = context;
        this.students = students;
        this.token = token;
        this.mProgressDialog = mProgressDialog;
    }

    public void setStudents(final List<User> students){
        if(this.students == null){
            this.students = students;
            this.filteredStudents = students;

            //Alert a change in items
            notifyItemChanged(0, filteredStudents.size());
        }
        //If updating items (previously not null)
        else {
            final DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                @Override
                public int getOldListSize() {
                    return StudentAdapter.this.students.size();
                }

                @Override
                public int getNewListSize() {
                    return students.size();
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    return StudentAdapter.this.students.get(oldItemPosition).getUsername() == students.get(newItemPosition).getUsername();
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {

                    User newUser = StudentAdapter.this.students.get(oldItemPosition);

                    User oldUser = students.get(newItemPosition);

                    return newUser.getUsername() == oldUser.getUsername() ;
                }
            });
            this.students = students;
            this.filteredStudents = students;
            result.dispatchUpdatesTo(this);
        }
    }

    @NonNull
    @Override
    public StudentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.student_row, parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentAdapter.ViewHolder holder, final int position) {
        holder.mName.setText(filteredStudents.get(position).getFirstName() + " " + filteredStudents.get(position).getLastName());
        holder.mUsername.setText(filteredStudents.get(position).getUsername());
        holder.mClassID.setText(String.valueOf(filteredStudents.get(position).getClassID()));

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                deleteStudent(filteredStudents.get(position).getUsername());
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        if(filteredStudents != null ) return filteredStudents.size();
        return 0;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    filteredStudents = students;
                } else {
                    List<User> filteredList = new ArrayList<>();
                    for (User user : students) {
                        if (user.getFirstName().toLowerCase().contains(charString.toLowerCase()) || user.getLastName().toLowerCase().contains(charString.toLowerCase()) || user.getUsername().toLowerCase().contains(charString.toLowerCase()) || String.valueOf(user.getClassID()).contains(charString.toLowerCase())) {
                            filteredList.add(user);
                        }
                    }
                    filteredStudents = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredStudents;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filteredStudents = (ArrayList<User>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView mName, mUsername, mClassID;

        public ViewHolder(View itemView) {
            super(itemView);

            mName = itemView.findViewById(R.id.name);
            mUsername = itemView.findViewById(R.id.username);
            mClassID = itemView.findViewById(R.id.class_id);

        }
    }

    private void deleteStudent(final String username) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
        builder.setTitle("Delete student");
        builder.setMessage("Are you sure that you want delete "+username+" ?");

        //When "Delete" button is clicked
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Call<ResponseBody> call = userClient.deleteStudent(token, username);

                //Show progress
                mProgressDialog.setMessage("Deleting...");
                mProgressDialog.show();

                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                        //Successfully deleted
                        if (response.code()==200) {
                            Toast.makeText(context, "Successfully deleted!", Toast.LENGTH_SHORT).show();

                            //Reload student list
                            ManageStudentsActivity activity = (ManageStudentsActivity) context;
                            activity.getAllStudents();

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
