package com.guyson.kronos.adapter;

import android.content.Context;
import android.content.DialogInterface;
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

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.guyson.kronos.R;
import com.guyson.kronos.model.Module;

import java.util.ArrayList;
import java.util.List;

public class ModuleAdapter extends RecyclerView.Adapter<ModuleAdapter.ViewHolder> implements Filterable {

    private Context context;
    private List<Module> modules;
    private List<Module> filteredModules;

    public ModuleAdapter(Context context, List<Module> modules) {
        this.context = context;
        this.modules = modules;
    }

    public void setModules(final List<Module> modules){
        if(this.modules == null){
            this.modules = modules;
            this.filteredModules = modules;
            notifyItemChanged(0, filteredModules.size());
        } else {
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
        View view = LayoutInflater.from(context).inflate(R.layout.module_row, parent,false);
        return new ModuleAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ModuleAdapter.ViewHolder holder, final int position) {
        holder.mNameID.setText("["+filteredModules.get(position).getModuleID() + "] " + filteredModules.get(position).getName());
        holder.mDescription.setText((filteredModules.get(position)).getDescription());
        holder.mCredits.setText("CREDITS " + filteredModules.get(position).getCredits());
        holder.mLecturer.setText(filteredModules.get(position).getLecturer());

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                deleteModule(filteredModules.get(position).getModuleID());
                return false;
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
                        if (module.getName().toLowerCase().contains(searchKey) || String.valueOf(module.getModuleID()).contains(searchKey) || module.getLecturer().toLowerCase().contains(searchKey)) {
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

        public ViewHolder(View itemView) {
            super(itemView);

            mNameID = itemView.findViewById(R.id.name_id);
            mLecturer = itemView.findViewById(R.id.lecturer);
            mCredits = itemView.findViewById(R.id.credits);
            mDescription = itemView.findViewById(R.id.description);


        }
    }

    private void deleteModule(int moduleID) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
        builder.setTitle("Delete module");
        builder.setMessage("Are you sure that you want delete "+moduleID+" ?");

        //When "Delete" button is clicked
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(context, "Deleted!", Toast.LENGTH_SHORT).show();
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

