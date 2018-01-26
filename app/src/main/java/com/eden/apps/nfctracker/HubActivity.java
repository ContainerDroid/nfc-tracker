package com.eden.apps.nfctracker;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

public class HubActivity extends AppCompatActivity {

    Toolbar mToolbar = null;
    BottomNavigationView mBottomNavigationView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hub);

        // setup toolbar
        mToolbar = findViewById(R.id.hub_toolbar);
        mToolbar.setTitle(getResources().getText(R.string.hub_title_status));
        setSupportActionBar(mToolbar);

        // set bottom navigation view
        mBottomNavigationView = findViewById(R.id.hub_bottom_navigation);
        mBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.hub_action_readers:
                        item.setChecked(true);
                        return true;
                    case R.id.hub_action_enclosures:
                        item.setChecked(true);
                        return true;
                    case R.id.hub_action_categories:
                        item.setChecked(true);
                        return true;
                    default:
                        return false;
                }
            }
        });

        // set status page as the default one
        mBottomNavigationView.getMenu().findItem(R.id.hub_action_readers).setChecked(true);
        getFragmentManager().beginTransaction()
                .replace(R.id.hub_fragment_placeholder, new HubReadersFragment())
                .commit();
    }

}
