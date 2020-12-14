package com.guyson.kronos.adapter;

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
import com.guyson.kronos.R;
import com.guyson.kronos.model.Class;

import java.util.ArrayList;
import java.util.List;

public class ClassAdapter extends RecyclerView.Adapter<ClassAdapter.ViewHolder> implements Filterable {

    private Context context;
    private List<Class> classes;
    private List<Class> filteredClasses;

    public ClassAdapter(Context context, List<Class> classes) {
        this.context = context;
        this.classes = classes;
    }

    public void setClasses(final List<Class> classes){
        if(this.classes == null){
            this.classes = classes;
            this.filteredClasses = classes;
            notifyItemChanged(0, filteredClasses.size());
        } else {
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

    private void deleteClass(int username) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
        builder.setTitle("Delete class");
        builder.setMessage("Are you sure that you want delete "+username+" ?");

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
