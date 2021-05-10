package com.guyson.kronos.adapter;

import android.app.ProgressDialog;
import android.content.ContentValues;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.guyson.kronos.BookmarkedActivity;
import com.guyson.kronos.ManageClassesActivity;
import com.guyson.kronos.ManageLecturersActivity;
import com.guyson.kronos.R;
import com.guyson.kronos.model.Bookmark;
import com.guyson.kronos.model.Class;
import com.guyson.kronos.provider.BookmarksContentProvider;
import com.guyson.kronos.service.ClassClient;
import com.guyson.kronos.service.LecturerClient;
import com.guyson.kronos.service.RetrofitClientInstance;

import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookmarkAdapter extends RecyclerView.Adapter<BookmarkAdapter.ViewHolder> implements Filterable {

    private Context context;
    private List<Bookmark> bookmarks;
    private List<Bookmark> filteredBookmarks;
    private ProgressDialog mProgressDialog;

    public BookmarkAdapter(Context context, List<Bookmark> bookmarks, ProgressDialog mProgressDialog) {
        this.context = context;
        this.bookmarks = bookmarks;
        this.mProgressDialog = mProgressDialog;
    }

    public void setBookmarks(final List<Bookmark> bookmarks){
        if(this.bookmarks == null){
            this.bookmarks = bookmarks;
            this.filteredBookmarks = bookmarks;
            //Alert a change in items
            notifyItemChanged(0, filteredBookmarks.size());
        }
        //If updating items (previously not null)
        else {
            final DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                @Override
                public int getOldListSize() {
                    return BookmarkAdapter.this.bookmarks.size();
                }

                @Override
                public int getNewListSize() {
                    return bookmarks.size();
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    return BookmarkAdapter.this.bookmarks.get(oldItemPosition).getLectureID() == bookmarks.get(newItemPosition).getLectureID();
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {

                    Bookmark newBookmark = BookmarkAdapter.this.bookmarks.get(oldItemPosition);

                    Bookmark oldBookmark = bookmarks.get(newItemPosition);

                    return newBookmark.getLectureID() == oldBookmark.getLectureID() ;
                }
            });
            this.bookmarks = bookmarks;
            this.filteredBookmarks = bookmarks;
            result.dispatchUpdatesTo(this);
        }
    }

    @NonNull
    @Override
    public BookmarkAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.bookmark_row, parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookmarkAdapter.ViewHolder holder, final int position) {
        holder.mRoomID.setText(String.valueOf(filteredBookmarks.get(position).getRoomID()));
        holder.mModule.setText(filteredBookmarks.get(position).getModule());
        holder.mDate.setText(filteredBookmarks.get(position).getDate());
        holder.mTime.setText(filteredBookmarks.get(position).getStartTime() + " (" + filteredBookmarks.get(position).getDuration()+ " hours)");

        //Change icon color on priority
        if (filteredBookmarks.get(position).getPriority().equals("high")) holder.mImageView.setColorFilter(ContextCompat.getColor(context, R.color.buttonRed), android.graphics.PorterDuff.Mode.SRC_IN);

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                handleOnHold(filteredBookmarks.get(position));
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        if(filteredBookmarks != null ) return filteredBookmarks.size();
        return 0;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    filteredBookmarks = bookmarks;
                } else {
                    List<Bookmark> filteredList = new ArrayList<>();
                    for (Bookmark b : bookmarks) {
                        //Search through ID and type
                        if (b.getModule().toLowerCase().contains(charString.toLowerCase()) || b.getDate().toLowerCase().contains(charString.toLowerCase()) || b.getStartTime().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(b);
                        }
                    }
                    filteredBookmarks = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredBookmarks;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filteredBookmarks = (ArrayList<Bookmark>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView mModule, mRoomID, mDate, mTime;
        ImageView mImageView;

        public ViewHolder(View itemView) {
            super(itemView);

            mImageView = itemView.findViewById(R.id.icon);
            mModule = itemView.findViewById(R.id.module);
            mRoomID = itemView.findViewById(R.id.room_id);
            mDate = itemView.findViewById(R.id.date);
            mTime = itemView.findViewById(R.id.time);

        }
    }

    private void handleOnHold(final Bookmark bookmark) {
        MaterialAlertDialogBuilder option_builder = new MaterialAlertDialogBuilder(context);
        option_builder.setTitle("Please select option");

        CharSequence[] options = new CharSequence[] {"Set as important", "Remove bookmark"};
        String settablePriority = "high";

        //Change options with priority
        if (bookmark.getPriority().equals("high")) {
            options = new CharSequence[] {"Set as normal", "Remove bookmark"};
            settablePriority = "normal";
        }

        final String finalSettablePriority = settablePriority;
        option_builder.setSingleChoiceItems(options, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //change selected category
                dialogInterface.dismiss();

                //Change priority
                if(i==0) {
                    try {
                        changePriority(bookmark, finalSettablePriority);
                    } catch (Exception e) {
                        Toast.makeText(context, "Something went wrong!", Toast.LENGTH_SHORT).show();
                    }
                }
                //Remove bookmark
                else {
                    try {
                        removeBookmark(bookmark);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(context, "Something went wrong!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        option_builder.show();
    }

    private void removeBookmark(Bookmark bookmark) {
        //Delete bookmark
        context.getContentResolver().delete(BookmarksContentProvider.CONTENT_URI, "lectureID=?", new String[]{String.valueOf(bookmark.getLectureID())});
        Toast.makeText(context, "Bookmark deleted successfully!", Toast.LENGTH_SHORT).show();

        //Redirect to bookmarks activity
        Intent intent = new Intent(context, BookmarkedActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }

    private void changePriority(Bookmark bookmark, String priority) {

        // class to add values in the database
        ContentValues values = new ContentValues();
        values.put(BookmarksContentProvider.PRIORITY, priority);

        //Update bookmark
        context.getContentResolver().update(BookmarksContentProvider.CONTENT_URI, values, "lectureID=?", new String[]{String.valueOf(bookmark.getLectureID())});
        Toast.makeText(context, "Priority changed successfully!", Toast.LENGTH_SHORT).show();

        //Redirect to bookmarks activity
        Intent intent = new Intent(context, BookmarkedActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }
}
