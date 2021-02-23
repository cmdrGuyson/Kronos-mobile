package com.guyson.kronos.adapter;

import android.app.ProgressDialog;
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
import com.guyson.kronos.ManageClassesActivity;
import com.guyson.kronos.ManageRoomsActivity;
import com.guyson.kronos.R;
import com.guyson.kronos.model.Room;
import com.guyson.kronos.service.RetrofitClientInstance;
import com.guyson.kronos.service.RoomClient;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.ViewHolder> implements Filterable {
    private Context context;
    private List<Room> rooms;
    private List<Room> filteredRooms;
    private String token;
    private ProgressDialog mProgressDialog;

    private RoomClient roomClient = RetrofitClientInstance.getRetrofitInstance().create(RoomClient.class);

    public RoomAdapter(Context context, List<Room> rooms, String token, ProgressDialog mProgressDialog) {
        this.context = context;
        this.rooms = rooms;
        this.token = token;
        this.mProgressDialog = mProgressDialog;
    }

    public void setRooms(final List<Room> rooms){
        if(this.rooms == null){
            this.rooms = rooms;
            this.filteredRooms = rooms;

            //Alert a change in items
            notifyItemChanged(0, filteredRooms.size());
        }
        //If updating items (previously not null)
        else {
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

    private void deleteRoom(final int id) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
        builder.setTitle("Delete room");
        builder.setMessage("Are you sure that you want delete "+id+" ?");

        //When "Delete" button is clicked
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(DialogInterface dialog, int which) {


                Call<ResponseBody> call = roomClient.deleteRoom(token, id);

                //Show progress
                mProgressDialog.setMessage("Deleting...");
                mProgressDialog.show();

                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                        //Successfully added
                        if (response.code()==200) {
                            Toast.makeText(context, "Successfully deleted!", Toast.LENGTH_SHORT).show();

                            //Reload room list
                            ManageRoomsActivity activity = (ManageRoomsActivity) context;
                            activity.getAllRooms();

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
