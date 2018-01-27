package com.eden.apps.nfctracker;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class HubCategoriesAdapter extends RecyclerView.Adapter<HubCategoriesAdapter.ViewHolder> {

    private LayoutInflater mInflater;
    private Cursor cursor;
    private Context context;
    private ItemClickListener mClickListener;

    HubCategoriesAdapter(Context context, Cursor cursor) {
        this.mInflater = LayoutInflater.from(context);
        this.cursor = cursor;
        this.context = context;
    }

    // inflates the cell layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recyclerview_grid_item, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the textview in each cell
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        this.cursor = context.getContentResolver().query(Uri.parse(CategoriesProvider.tableUri), null, null, null, "id");
        String categoryName = getCategoryName(position, this.cursor);
        if (categoryName != null) {
            holder.myTextView.setText(categoryName);
        }
    }

    private String getCategoryName(int position, Cursor cursorV) {
        if (position == 0) {
            return "+";
        }

        String categoryName = null;
        Log.d("PISTOL", "trying to get category name for position " + position);

        while (cursorV.moveToNext() && position != 0) {
            categoryName = cursorV.getString(cursorV.getColumnIndex("name"));
            position--;
        }

        if (categoryName != null)
            Log.d("PISTOL", "category name = " + categoryName);
        return categoryName;
    }

    static public Cursor getItem(Cursor cursor, int position) {
        int size = 0;
        do {
            if (position == size)
                return cursor;
            size++;
        } while (cursor.moveToNext());
        return null;
    }


    private int getSize(Cursor cursorV) {
        int size = 0;
        while (cursorV.moveToNext()) {
            size++;
        }
        Log.d("PISTOL", "size is " + size);
        return size;
    }
    // total number of cells
    @Override
    public int getItemCount() {
        Log.d("PISTOL", "getItemCount");
        this.cursor = context.getContentResolver().query(Uri.parse(CategoriesProvider.tableUri), null, null, null, "id");
        return getSize(this.cursor) + 1;
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView myTextView;

        ViewHolder(View itemView) {
            super(itemView);
            myTextView = (TextView) itemView.findViewById(R.id.info_text);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    String getItem(int id) {
        return "";
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}