package com.eden.apps.nfctracker;


import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


public class ReaderSettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    public static final String WIFI_KEY = "reader_settings_wifi_key";

    public static final String DISCOVERABLE_KEY = "reader_settings_discoverable_key";
    public static final String DISCOVERABLE_NAME_KEY = "reader_settings_discoverable_name_key";
    public static final String DISCOVERABLE_CHECK_KEY = "reader_settings_discoverable_check_key";

    public ReaderSettingsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.reader_preferences);

        showCurrentPreferences();
    }

    private void setWifiSummary() {
        ConnectivityManager connManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
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
        if (checked)
            mDiscoverable.setSummary(getResources().getText(R.string.on).toString());
        else
            mDiscoverable.setSummary(getResources().getText(R.string.off).toString());
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

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
        showCurrentPreferences();
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }
}
