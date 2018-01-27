package com.eden.apps.nfctracker;


import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;


/**
 * A simple {@link Fragment} subclass.
 */
public class HubCategoriesFragment extends Fragment implements HubCategoriesAdapter.ItemClickListener, CreateCategoryFragment.NoticeDialogListener {

    HubCategoriesAdapter mAdapter;
    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;

    public HubCategoriesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_hub_categories, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.hub_categories_recyclerview);
        mLayoutManager = new GridLayoutManager(getActivity(), 2);
        mRecyclerView.setLayoutManager(mLayoutManager);

        Cursor initialData = addInitialData();

        mAdapter = new HubCategoriesAdapter(getActivity(), initialData);
        mAdapter.setClickListener(this);
        mRecyclerView.setAdapter(mAdapter);


        return view;
    }

    private ContentValues createCategory(String name, String parentName) {
        ContentValues cv = new ContentValues();

        cv.put("name", name);
        if (parentName == null) {
            cv.put("parentID", (Integer)null);
        } else {
            Log.d("PISTOL", "Here I am trying to add a cv subcategory");
            Uri categoriesURI = Uri.parse(CategoriesProvider.tableUri);
            Cursor cursor = getActivity().getApplicationContext().getContentResolver().query(categoriesURI, null, "name=?", new String[]{parentName}, "id");
            while (cursor.moveToNext()) {
                Integer parentId = cursor.getInt(cursor.getColumnIndex("id"));
                String parentN = cursor.getString(cursor.getColumnIndex("name"));
                Log.d("PISTOL", "Parent name is " + parentN + " and id is " + parentId.toString());
                cv.put("parentID", parentId);
                break;
            }
            Log.d("PISTOL", cursor.toString());
        }

        return cv;
    }

    private Cursor addInitialData() {

        ContentValues mInitialValue;

        mInitialValue = createCategory("Food", null);
        if (mInitialValue != null)
            getActivity().getApplicationContext().getContentResolver().insert(Uri.parse(CategoriesProvider.tableUri), mInitialValue);

        mInitialValue = createCategory("Drinks", null);
        if (mInitialValue != null)
            getActivity().getApplicationContext().getContentResolver().insert(Uri.parse(CategoriesProvider.tableUri), mInitialValue);

        mInitialValue = createCategory("Pasta", "Food");
        if (mInitialValue != null)
            getActivity().getApplicationContext().getContentResolver().insert(Uri.parse(CategoriesProvider.tableUri), mInitialValue);

        return getActivity().getApplicationContext().getContentResolver().query(Uri.parse(CategoriesProvider.tableUri), null, null, null, "id");
    }

    @Override
    public void onItemClick(View view, int position) {
        Log.i("TAG", "You clicked number " + mAdapter.getItem(position) + ", which is at cell position " + position);

        if (position == 0) {
            // a new category should be added

            CreateCategoryFragment mDialog = new CreateCategoryFragment(this);
            mDialog.show(getFragmentManager(), "CreateCategoryFragment");
        } else {
            Uri categoriesURI = Uri.parse(CategoriesProvider.tableUri);
            Cursor cursor = getActivity().getApplicationContext().getContentResolver().query(categoriesURI, null, null, null, "id");
            Cursor itemCursor = HubCategoriesAdapter.getItem(cursor, position);
            Fragment fragment = new CategoryFragment(itemCursor.getString(cursor.getColumnIndex("name")));
            getFragmentManager().beginTransaction()
                    .replace(R.id.hub_fragment_placeholder, fragment)
                    .commit();
        }
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        String mCategoryName = ((EditText)dialog.getDialog().findViewById(R.id.new_category)).getText().toString();
        ContentValues mNewCategory = createCategory(mCategoryName, null);
        if (mNewCategory != null)
            getActivity().getApplicationContext().getContentResolver().insert(Uri.parse(CategoriesProvider.tableUri), mNewCategory);
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {

    }
}
