package com.eden.apps.nfctracker;

import android.app.Fragment;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;


public class ReaderActivity extends AppCompatActivity {

    Toolbar mToolbar = null;
    BottomNavigationView mBottomNavigationView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reader);

        // setup toolbar
        mToolbar = findViewById(R.id.reader_toolbar);
        mToolbar.setTitle(getResources().getText(R.string.reader_title_status));
        setSupportActionBar(mToolbar);


        // set bottom navigation view
        mBottomNavigationView = findViewById(R.id.reader_bottom_navigation);
        mBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment;
                switch (item.getItemId()) {
                    case R.id.reader_action_status:
                        fragment = new ReaderStatusFragment();
                        break;
                    case R.id.reader_action_settings:
                        fragment = new ReaderSettingsFragment();
                        break;
                    default:
                        return false;
                }
                return mSwitchFragment(item, fragment);
            }
        });

        // set readers setting page as the default one
        mSwitchFragment(mBottomNavigationView.getMenu().findItem(R.id.reader_action_settings), new ReaderSettingsFragment());
    }


    private boolean mSwitchFragment(MenuItem item, Fragment fragment) {
        item.setChecked(true);
        getFragmentManager().beginTransaction()
                .replace(R.id.reader_fragment_placeholder, fragment)
                .commit();
        return true;
    }
}
