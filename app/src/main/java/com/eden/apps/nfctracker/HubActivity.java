package com.eden.apps.nfctracker;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

public class HubActivity extends AppCompatActivity implements TCPListener {

    Toolbar mToolbar = null;
    BottomNavigationView mBottomNavigationView = null;
    TCPCommunicator mTCPServer = null;
    public static final Integer SERVER_PORT = 12345;

    private boolean mSwitchFragment(MenuItem item, Fragment fragment) {
        item.setChecked(true);
        getFragmentManager().beginTransaction()
                .replace(R.id.hub_fragment_placeholder, fragment)
                .commit();
        return true;
    }

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
                Fragment fragment;
                switch (item.getItemId()) {
                    case R.id.hub_action_readers:
                        fragment = new HubReadersFragment();
                        break;
                    case R.id.hub_action_enclosures:
                        fragment = new HubEnclosuresFragment();
                        break;
                    case R.id.hub_action_categories:
                        fragment = new HubCategoriesFragment();
                        break;
                    default:
                        return false;
                }
                return mSwitchFragment(item, fragment);
            }
        });

        // set readers setting page as the default one
        mSwitchFragment(mBottomNavigationView.getMenu().findItem(R.id.hub_action_readers), new HubReadersFragment());


        // start the TCP server
        mTCPServer = TCPCommunicator.getInstance();
        TCPCommunicator.addListener(this);
        mTCPServer.initServer(SERVER_PORT);

    }

    @Override
    public void onTCPMessageReceived(String message) {
        Log.d("PISTOL", "Received message " + message);
    }

    @Override
    public void onTCPConnectionStatusChanged(boolean isConnectedNow) {

    }
}
