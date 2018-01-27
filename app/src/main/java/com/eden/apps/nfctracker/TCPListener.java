package com.eden.apps.nfctracker;

public interface TCPListener {
    public void onTCPMessageReceived(String message);
    public void onTCPConnectionStatusChanged(boolean isConnectedNow);
}