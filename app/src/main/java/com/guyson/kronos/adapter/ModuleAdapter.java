package com.guyson.kronos.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
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

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.guyson.kronos.ManageModulesActivity;
import com.guyson.kronos.MyModulesActivity;
import com.guyson.kronos.R;
import com.guyson.kronos.model.Module;
import com.guyson.kronos.service.ModuleClient;
import com.guyson.kronos.service.RetrofitClientInstance;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ModuleAdapter extends RecyclerView.Adapter<ModuleAdapter.ViewHolder> implements Filterable {

    private Context context;
    private List<Module> modules;
    private List<Module> filteredModules;
    private String role;

    private String token;
    private ProgressDialog mProgressDialog;

    private ModuleClient moduleClient = RetrofitClientInstance.getRetrofitInstance().create(ModuleClient.class);

    public ModuleAdapter(Context context, List<Module> modules, String role, String token, ProgressDialog mProgressDialog) {
        this.context = context;
        this.modules = modules;
        this.role = role;
        this.token = token;
        this.mProgressDialog = mProgressDialog;
    }

    public void setModules(final List<Module> modules){
        if(this.modules == null){
            this.modules = modules;
            this.filteredModules = modules;

            //Alert change in items
            notifyItemChanged(0, filteredModules.size());
        }
        //If updating items (previously not null)
        else {
            final DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                @Override
                public int getOldListSize() {
                    return ModuleAdapter.this.modules.size();
                }

                @Override
                public int getNewListSize() {
                    return modules.size();
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    return ModuleAdapter.this.modules.get(oldItemPosition).getModuleID() == modules.get(newItemPosition).getModuleID();
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {

                    Module newModule = ModuleAdapter.this.modules.get(oldItemPosition);

                    Module oldModule = modules.get(newItemPosition);

                    return newModule.getModuleID() == oldModule.getModuleID() ;
                }
            });
            this.modules = modules;
            this.filteredModules = modules;
            result.dispatchUpdatesTo(this);
        }
    }

    @NonNull
    @Override
    public ModuleAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        //If admin is viewing modules
        if(role.equals("admin")) {
            view = LayoutInflater.from(context).inflate(R.layout.module_row, parent,false);
        }
        //If student is viewing modules
        else{
            view = LayoutInflater.from(context).inflate(R.layout.student_module_row, parent,false);
        };
        return new ModuleAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ModuleAdapter.ViewHolder holder, final int position) {
        final int moduleID = filteredModules.get(position).getModuleID();

        holder.mNameID.setText("["+moduleID + "] " + filteredModules.get(position).getName());
        holder.mDescription.setText((filteredModules.get(position)).getDescription());
        holder.mCredits.setText("CREDITS " + filteredModules.get(position).getCredits());
        holder.mLecturer.setText(filteredModules.get(position).getLecturer().getFirstName() +" "+ filteredModules.get(position).getLecturer().getLastName());

        if(role.equals("admin")) {
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    deleteModule(filteredModules.get(position).getModuleID());
                    return false;
                }
            });
        }

        if(role.equals("student")) {

            boolean isEnrolled = filteredModules.get(position).isEnrolled();

            //If student is enrolled in the module
            if (!isEnrolled) {
                holder.mEnrollButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        handleEnroll(moduleID);
                    }
                });
            }else{
                holder.mEnrollButton.setText("UNROLL");
                holder.mEnrollButton.setBackgroundTintList(context.getResources().getColorStateList(R.color.buttonRed));
                holder.mEnrollButton.setIcon(context.getResources().getDrawable(R.drawable.ic_baseline_indeterminate_check_box_24));
                holder.mEnrollButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        handleUnroll(moduleID);
                    }
                });
            }
        }
    }

    //Enroll student in module
    private void handleEnroll(int id) {

        Call<ResponseBody> call = moduleClient.enroll(token, id);

        //Show progress
        mProgressDialog.setMessage("Enrolling...");
        mProgressDialog.show();

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                //Successfully enrolled
                if (response.code()==200) {
                    Toast.makeText(context, "Successfully enrolled!", Toast.LENGTH_SHORT).show();

                    //Direct to my modules
                    Intent intent = new Intent(context, MyModulesActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    context.startActivity(intent);

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

    //Unroll student from module
    private void handleUnroll(int id) {

        Call<ResponseBody> call = moduleClient.unroll(token, id);

        //Show progress
        mProgressDialog.setMessage("Unrolling...");
        mProgressDialog.show();

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                //Successfully unrolled
                if (response.code()==200) {
                    Toast.makeText(context, "Successfully unrolled!", Toast.LENGTH_SHORT).show();

                    //Direct to my modules
                    Intent intent = new Intent(context, MyModulesActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    context.startActivity(intent);

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

    @Override
    public int getItemCount() {
        if(filteredModules != null ) return filteredModules.size();
        return 0;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    filteredModules = modules;
                } else {
                    List<Module> filteredList = new ArrayList<>();
                    for (Module module : modules) {
                        String searchKey = charString.toLowerCase();
                        if (module.getName().toLowerCase().contains(searchKey) || String.valueOf(module.getModuleID()).contains(searchKey) || module.getLecturer().getFirstName().toLowerCase().contains(searchKey) ||  module.getLecturer().getLastName().toLowerCase().contains(searchKey)) {
                            filteredList.add(module);
                        }
                    }
                    filteredModules = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredModules;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filteredModules = (ArrayList<Module>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView mNameID, mLecturer, mCredits, mDescription;
        MaterialButton mEnrollButton;

        public ViewHolder(View itemView) {
            super(itemView);

            mNameID = itemView.findViewById(R.id.name_id);
            mLecturer = itemView.findViewById(R.id.lecturer);
            mCredits = itemView.findViewById(R.id.credits);
            mDescription = itemView.findViewById(R.id.description);

            if(role.equals("student")){
                mEnrollButton = itemView.findViewById(R.id.enroll_button);
            }


        }
    }

    private void deleteModule(final int id) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
        builder.setTitle("Delete module");
        builder.setMessage("Are you sure that you want delete "+id+" ?");

        //When "Delete" button is clicked
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Call<ResponseBody> call = moduleClient.deleteModule(token, id);

                //Show progress
                mProgressDialog.setMessage("Deleting...");
                mProgressDialog.show();

                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                        //Successfully deleted
                        if (response.code()==200) {
                            Toast.makeText(context, "Successfully deleted!", Toast.LENGTH_SHORT).show();

                            //Reload module list
                            ManageModulesActivity activity = (ManageModulesActivity) context;
                            activity.getAllModules();

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

