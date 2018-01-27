package com.eden.apps.nfctracker;


import android.annotation.SuppressLint;
import android.app.DialogFragment;
import android.app.Fragment;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * A simple {@link Fragment} subclass.
 */
public class CategoryFragment extends Fragment implements CreateItemFragment.NoticeDialogListenerItems, TCPListener {

    String mCategoryName;
    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    ItemAdapter mAdapter;
    Button mAddItem = null;

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

        mAddItem = view.findViewById(R.id.add_new_item);
        mAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateItemFragment mDialog = new CreateItemFragment(CategoryFragment.this);
                mDialog.show(getFragmentManager(), "CreateItemFragment");
            }
        });

        TCPCommunicator.getInstance().addListener(this);

        return view;
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        TCPCommunicator mTCPServer = TCPCommunicator.getInstance();
        JSONObject enterNewTagsState = new JSONObject();
        try {
            enterNewTagsState.put("state", "detect_new_tags");
            mTCPServer.writeToSocketFromServer(enterNewTagsState);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {

    }

    @Override
    public void onTCPMessageReceived(String message) {
        Log.d("PISTOL", "Received message " + message);

        JSONObject received;
        try {
            received = new JSONObject(message);
            if (received.getString("state").equals("detect_new_tags")) {
                String newTag = received.getString("tag");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onTCPConnectionStatusChanged(boolean isConnectedNow) {

    }

    @Override
    public void addReader(String reader) {

    }
}
