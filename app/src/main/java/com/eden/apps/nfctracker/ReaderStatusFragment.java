package com.eden.apps.nfctracker;


import android.app.Fragment;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;


public class ReaderStatusFragment extends Fragment {


    public ReaderStatusFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.fragment_reader_status, container, false);
        TextView mReaderStatusText = (TextView) mView.findViewById(R.id.reader_status_text);
        NfcAdapter mNfcAdapter = ((ReaderActivity) getActivity()).mNfcAdapter;

        if (mNfcAdapter == null) {
            mReaderStatusText.setText(R.string.reader_status_no_nfc);
        } else if (!mNfcAdapter.isEnabled()) {
            mReaderStatusText.setText(R.string.reader_status_nfc_not_enabled);
        } else {
            mReaderStatusText.setText(R.string.reader_status_all_good);
        }
        return mView;
    }

}
