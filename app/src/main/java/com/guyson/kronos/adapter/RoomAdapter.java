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
import com.guyson.kronos.model.Room;

import java.util.ArrayList;
import java.util.List;

public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.ViewHolder> implements Filterable {
    private Context context;
    private List<Room> rooms;
    private List<Room> filteredRooms;

    public RoomAdapter(Context context, List<Room> rooms) {
        this.context = context;
        this.rooms = rooms;
    }

    public void setRooms(final List<Room> rooms){
        if(this.rooms == null){
            this.rooms = rooms;
            this.filteredRooms = rooms;
            notifyItemChanged(0, filteredRooms.size());
        } else {
            final DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                @Override
                public int getOldListSize() {
                    return RoomAdapter.this.rooms.size();
                }

                @Override
                public int getNewListSize() {
                    return rooms.size();
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    return RoomAdapter.this.rooms.get(oldItemPosition).getRoomID() == rooms.get(newItemPosition).getRoomID();
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {

                    Room newRoom = RoomAdapter.this.rooms.get(oldItemPosition);

                    Room oldRoom = rooms.get(newItemPosition);

                    return newRoom.getRoomID() == oldRoom.getRoomID() ;
                }
            });
            this.rooms = rooms;
            this.filteredRooms = rooms;
            result.dispatchUpdatesTo(this);
        }
    }

    @NonNull
    @Override
    public RoomAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.room_row, parent,false);
        return new RoomAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RoomAdapter.ViewHolder holder, final int position) {
        holder.mRoomID.setText(String.valueOf(filteredRooms.get(position).getRoomID()));
        holder.mDescription.setText(filteredRooms.get(position).getDescription());
        holder.mType.setText(filteredRooms.get(position).getType());

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                deleteRoom(filteredRooms.get(position).getRoomID());
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        if(filteredRooms != null ) return filteredRooms.size();
        return 0;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    filteredRooms = rooms;
                } else {
                    List<Room> filteredList = new ArrayList<>();
                    for (Room room : rooms) {
                        //Search through ID and type
                        if (String.valueOf(room.getRoomID()).contains(charString.toLowerCase()) || room.getType().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(room);
                        }
                    }
                    filteredRooms = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredRooms;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filteredRooms = (ArrayList<Room>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView mRoomID, mType, mDescription;

        public ViewHolder(View itemView) {
            super(itemView);

            mRoomID = itemView.findViewById(R.id.room_id);
            mType = itemView.findViewById(R.id.type);
            mDescription = itemView.findViewById(R.id.description);

        }
    }

    private void deleteRoom(int roomID) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
        builder.setTitle("Delete room");
        builder.setMessage("Are you sure that you want delete "+roomID+" ?");

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
