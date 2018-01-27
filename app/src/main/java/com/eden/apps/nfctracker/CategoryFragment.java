package com.eden.apps.nfctracker;


import android.annotation.SuppressLint;
import android.app.Fragment;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class CategoryFragment extends Fragment {

    String mCategoryName;
    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    ItemAdapter mAdapter;

    public CategoryFragment() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public CategoryFragment(String categoryName) {
        this.mCategoryName = categoryName;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_category, container, false);

        TextView tw = (TextView )view.findViewById(R.id.category_text);
        tw.setText("Items in category " + this.mCategoryName + ":");

        mRecyclerView = (RecyclerView) view.findViewById(R.id.category_item_list);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new ItemAdapter(getActivity());
        mRecyclerView.setAdapter(mAdapter);


        return view;
    }
}
