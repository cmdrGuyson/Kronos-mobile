package com.guyson.kronos.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.guyson.kronos.R;
import com.guyson.kronos.model.User;

import java.util.ArrayList;
import java.util.List;

public class LecturerAdapter extends RecyclerView.Adapter<LecturerAdapter.ViewHolder> implements Filterable {

    private Context context;
    private List<User> lecturers;
    private List<User> filteredLecturers;

    public LecturerAdapter(Context context, List<User> lecturers) {
        this.context = context;
        this.lecturers = lecturers;
    }

    public void setLecturers(final List<User> lecturers){
        if(this.lecturers == null){
            this.lecturers = lecturers;
            this.filteredLecturers = lecturers;
            notifyItemChanged(0, filteredLecturers.size());
        } else {
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
                    return LecturerAdapter.this.lecturers.get(oldItemPosition).getUsername() == lecturers.get(newItemPosition).getUsername();
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {

                    User newUser = LecturerAdapter.this.lecturers.get(oldItemPosition);

                    User oldUser = lecturers.get(newItemPosition);

                    return newUser.getUsername() == oldUser.getUsername() ;
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
    public void onBindViewHolder(@NonNull LecturerAdapter.ViewHolder holder, int position) {
        holder.mName.setText(filteredLecturers.get(position).getFirstName() + " " + filteredLecturers.get(position).getLastName());
        holder.mUsername.setText(filteredLecturers.get(position).getUsername());
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
                    List<User> filteredList = new ArrayList<>();
                    for (User user : lecturers) {
                        if (user.getFirstName().toLowerCase().contains(charString.toLowerCase())) {
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
                filteredLecturers = (ArrayList<User>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView mName, mUsername;

        public ViewHolder(View itemView) {
            super(itemView);

            mName = itemView.findViewById(R.id.name);
            mUsername = itemView.findViewById(R.id.username);

        }
    }
}
