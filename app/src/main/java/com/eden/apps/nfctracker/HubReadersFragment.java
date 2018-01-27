package com.eden.apps.nfctracker;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import static android.content.Context.CONNECTIVITY_SERVICE;
import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class HubReadersFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    public static final String WIFI_KEY = "hub_settings_wifi_key";

    public static final String DISCOVERABLE_KEY = "hub_settings_discoverable_key";
    public static final String DISCOVERABLE_NAME_KEY = "hub_settings_discoverable_name_key";
    public static final String DISCOVERABLE_CHECK_KEY = "hub_settings_discoverable_check_key";

    public static final String HUB_CONNECTED_KEY = "hub_settings_hub_connected";
    public static final String HUB_AVAILABLE_KEY = "hub_settings_hub_available";

    public static final String HUB_IP_ADDRESS_KEY = "hub_ip_address";

    private BroadcastReceiver mWifiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (WifiManager.SUPPLICANT_STATE_CHANGED_ACTION .equals(action)) {
                SupplicantState state = intent.getParcelableExtra(WifiManager.EXTRA_NEW_STATE);
                if (SupplicantState.isValidState(state)
                        && state == SupplicantState.COMPLETED) {

                    Context mContext = getActivity().getApplicationContext();
                    String mIP;

                    WifiManager wifiManager =
                            (WifiManager) mContext.getSystemService(mContext.WIFI_SERVICE);

                    WifiInfo wifi = wifiManager.getConnectionInfo();
                    if (wifi != null) {
                        mIP = mIpFromInt(wifi.getIpAddress());
                    } else {
                        mIP = "none";
                    }
                    Preference mHubIpAddress = findPreference(HUB_IP_ADDRESS_KEY);
                    mHubIpAddress.setTitle("IP address");
                    mHubIpAddress.setSummary(mIP);
                }
            }
        }
    };

    private static String mIpFromInt(int ip) {
        return ( ip        & 0xFF) + "." +
               ((ip >>  8) & 0xFF) + "." +
               ((ip >> 16) & 0xFF) + "." +
               ((ip >> 24) & 0xFF);
    }

    public HubReadersFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.hub_readers_preferences);
        showCurrentPreferences();
    }

    @Override
    public void onResume() {
        super.onResume();

        getActivity().registerReceiver(mWifiReceiver,
                new IntentFilter(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION));


        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
        showCurrentPreferences();
    }

    @Override
    public void onPause() {
        super.onPause();

        getActivity().unregisterReceiver(mWifiReceiver);

        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    private void setWifiSummary() {
        ConnectivityManager connManager = (ConnectivityManager) getActivity().getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (mWifi.isConnected()) {
            WifiManager wifiManager = (WifiManager) getActivity().getApplicationContext().getSystemService (Context.WIFI_SERVICE);
            WifiInfo info = wifiManager.getConnectionInfo ();
            String mWifiSSID  = info.getSSID().replace("\"", "");
            findPreference(WIFI_KEY).setSummary(mWifiSSID);
        }
    }

    private void setDiscoverableSummary(SharedPreferences sharedPreferences) {
        PreferenceScreen mDiscoverable = (PreferenceScreen) findPreference(DISCOVERABLE_KEY);
        Boolean checked = sharedPreferences.getBoolean(DISCOVERABLE_CHECK_KEY, false);
        String mText;

        if (checked) {
            mText = getResources().getText(R.string.on).toString();
        } else {
            mText = getResources().getText(R.string.off).toString();
        }
        mDiscoverable.setSummary(mText);
        ((BaseAdapter)getPreferenceScreen().getRootAdapter()).notifyDataSetChanged();
    }

    private void setDiscoverableNameSummary(SharedPreferences sharedPreferences) {
        Preference mDiscoverableName = findPreference(DISCOVERABLE_NAME_KEY);
        mDiscoverableName.setSummary(sharedPreferences.getString(DISCOVERABLE_NAME_KEY, getResources().getText(R.string.not_set).toString()));
    }

    private void showCurrentPreferences() {
        setWifiSummary();
        setDiscoverableSummary(getPreferenceScreen().getSharedPreferences());
        setDiscoverableNameSummary(getPreferenceScreen().getSharedPreferences());
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        switch (key) {
            case DISCOVERABLE_NAME_KEY:
                setDiscoverableNameSummary(sharedPreferences);
                break;
            case DISCOVERABLE_CHECK_KEY:
                setDiscoverableSummary(sharedPreferences);
                break;
            default:
                break;
        }
    }
}
