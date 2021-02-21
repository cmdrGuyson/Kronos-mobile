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
import com.guyson.kronos.ManageClassesActivity;
import com.guyson.kronos.ManageLecturersActivity;
import com.guyson.kronos.R;
import com.guyson.kronos.model.Class;
import com.guyson.kronos.service.ClassClient;
import com.guyson.kronos.service.LecturerClient;
import com.guyson.kronos.service.RetrofitClientInstance;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ClassAdapter extends RecyclerView.Adapter<ClassAdapter.ViewHolder> implements Filterable {

    private Context context;
    private List<Class> classes;
    private List<Class> filteredClasses;
    private String token;
    private ProgressDialog mProgressDialog;

    //Class Retrofit Client
    ClassClient classClient = RetrofitClientInstance.getRetrofitInstance().create(ClassClient.class);

    public ClassAdapter(Context context, List<Class> classes, String token, ProgressDialog mProgressDialog) {
        this.context = context;
        this.classes = classes;
        this.token = token;
        this.mProgressDialog = mProgressDialog;
    }

    public void setClasses(final List<Class> classes){
        if(this.classes == null){
            this.classes = classes;
            this.filteredClasses = classes;
            //Alert a change in items if lectures
            notifyItemChanged(0, filteredClasses.size());
        }
        //If updating items (previously not null)
        else {
            final DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                @Override
                public int getOldListSize() {
                    return ClassAdapter.this.classes.size();
                }

                @Override
                public int getNewListSize() {
                    return classes.size();
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    return ClassAdapter.this.classes.get(oldItemPosition).getClassID() == classes.get(newItemPosition).getClassID();
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {

                    Class newClass = ClassAdapter.this.classes.get(oldItemPosition);

                    Class oldClass = classes.get(newItemPosition);

                    return newClass.getClassID() == oldClass.getClassID() ;
                }
            });
            this.classes = classes;
            this.filteredClasses = classes;
            result.dispatchUpdatesTo(this);
        }
    }

    @NonNull
    @Override
    public ClassAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.class_row, parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClassAdapter.ViewHolder holder, final int position) {
        holder.mClassID.setText(String.valueOf(filteredClasses.get(position).getClassID()));
        holder.mDescription.setText(filteredClasses.get(position).getDescription());
        holder.mType.setText(filteredClasses.get(position).getType());

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                deleteClass(filteredClasses.get(position).getClassID());
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        if(filteredClasses != null ) return filteredClasses.size();
        return 0;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    filteredClasses = classes;
                } else {
                    List<Class> filteredList = new ArrayList<>();
                    for (Class _class : classes) {
                        //Search through ID and type
                        if (String.valueOf(_class.getClassID()).contains(charString.toLowerCase()) || _class.getType().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(_class);
                        }
                    }
                    filteredClasses = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredClasses;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filteredClasses = (ArrayList<Class>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView mClassID, mType, mDescription;

        public ViewHolder(View itemView) {
            super(itemView);

            mClassID = itemView.findViewById(R.id.class_id);
            mType = itemView.findViewById(R.id.type);
            mDescription = itemView.findViewById(R.id.description);

        }
    }

    private void deleteClass(final int id) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
        builder.setTitle("Delete class");
        builder.setMessage("Are you sure that you want delete "+id+" ?");

        //When "Delete" button is clicked
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(DialogInterface dialog, int which) {


                Call<ResponseBody> call = classClient.deleteClass(token, id);

                //Show progress
                mProgressDialog.setMessage("Deleting...");
                mProgressDialog.show();

                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                        //Successfully added
                        if (response.code()==200) {
                            Toast.makeText(context, "Successfully deleted!", Toast.LENGTH_SHORT).show();

                            //Reload class list
                            ManageClassesActivity activity = (ManageClassesActivity) context;
                            activity.getAllClasses();

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
