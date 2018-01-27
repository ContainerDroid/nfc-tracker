package com.eden.apps.nfctracker;

import android.app.Activity;
import android.app.Fragment;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.MifareUltralight;
import android.nfc.tech.Ndef;
import android.nfc.tech.NfcA;
import android.nfc.tech.NfcB;
import android.nfc.tech.NfcBarcode;
import android.nfc.tech.NfcF;
import android.nfc.tech.NfcV;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;


public class ReaderActivity extends AppCompatActivity implements TCPListener {

    Toolbar mToolbar = null;
    BottomNavigationView mBottomNavigationView = null;
    public NfcAdapter mNfcAdapter;

    public static final String MIME_TEXT_PLAIN = "text/plain";
    public static final String TAG = "NfcDemo";


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

        // NFC setup
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        handleIntent(getIntent());

    }


    private boolean mSwitchFragment(MenuItem item, Fragment fragment) {
        item.setChecked(true);
        getFragmentManager().beginTransaction()
                .replace(R.id.reader_fragment_placeholder, fragment)
                .commit();
        return true;
    }


    @Override
    protected void onResume() {
        super.onResume();

        /**
         * It's important, that the activity is in the foreground (resumed). Otherwise
         * an IllegalStateException is thrown.
         */
        setupForegroundDispatch(this, mNfcAdapter);
    }

    @Override
    protected void onPause() {
        /**
         * Call this before onPause, otherwise an IllegalArgumentException is thrown as well.
         */
        stopForegroundDispatch(this, mNfcAdapter);

        super.onPause();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        /**
         * This method gets called, when a new Intent gets associated with the current activity instance.
         * Instead of creating a new activity, onNewIntent will be called. For more information have a look
         * at the documentation.
         *
         * In our case this method gets called, when the user attaches a Tag to the device.
         */
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (intent.getAction() == NfcAdapter.ACTION_TECH_DISCOVERED) {
            Toast.makeText(getApplicationContext(), "NFC Tag: " +
                            this.ByteArrayToHexString(intent.getByteArrayExtra(NfcAdapter.EXTRA_ID)),
                            Toast.LENGTH_LONG).show();
        }
    }

    // Converting byte[] to hex string:
    private String ByteArrayToHexString(byte [] inarray) {
        int i, j, in;
        String [] hex = {"0","1","2","3","4","5","6","7","8","9","A","B","C","D","E","F"};
        String out= "";
        for (j = 0; j < inarray.length; j++) {
            in = (int) inarray[j] & 0xff;
            i = (in >> 4) & 0x0f;
            out += hex[i];
            i = in & 0x0f;
            out += hex[i] + ":";
        }
        return out.substring(0, out.length() - 1);
    }

    /**
     * @param activity The corresponding {@link Activity} requesting the foreground dispatch.
     * @param adapter The {@link NfcAdapter} used for the foreground dispatch.
     */
    public static void setupForegroundDispatch(final Activity activity, NfcAdapter adapter) {
        final Intent intent = new Intent(activity.getApplicationContext(), activity.getClass());
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        final PendingIntent pendingIntent = PendingIntent.getActivity(activity.getApplicationContext(), 0, intent, 0);

        IntentFilter[] filters = new IntentFilter[] {
                new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED),
        };
        String[][] techList = new String[][] {
                new String[] { NfcA.class.getName() },
                new String[] { NfcB.class.getName() },
                new String[] { NfcF.class.getName() },
                new String[] { NfcV.class.getName() },
                new String[] { NfcBarcode.class.getName() },
                new String[] { MifareClassic.class.getName() },
                new String[] { MifareUltralight.class.getName() },
        };
        adapter.enableForegroundDispatch(activity, pendingIntent, filters, techList);
    }

    /**
     * @param activity The corresponding {@link BaseActivity} requesting to stop the foreground dispatch.
     * @param adapter The {@link NfcAdapter} used for the foreground dispatch.
     */
    public static void stopForegroundDispatch(final Activity activity, NfcAdapter adapter) {
        adapter.disableForegroundDispatch(activity);
    }

    @Override
    public void onTCPMessageReceived(String message) {

    }

    @Override
    public void onTCPConnectionStatusChanged(boolean isConnectedNow) {

    }

    @Override
    public void addReader(String reader) {

    }
}
