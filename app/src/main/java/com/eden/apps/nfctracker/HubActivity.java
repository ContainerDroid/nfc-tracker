package com.eden.apps.nfctracker;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

public class HubActivity extends AppCompatActivity implements TCPListener, SharedPreferences.OnSharedPreferenceChangeListener {

    Toolbar mToolbar = null;
    BottomNavigationView mBottomNavigationView = null;
    TCPCommunicator mTCPServer = null;
    public static final Integer SERVER_PORT = 12345;
    Fragment fragment;

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
                HubActivity.this.fragment = fragment;
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

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {

    }
}
