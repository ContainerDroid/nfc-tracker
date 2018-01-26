package com.eden.apps.nfctracker;

class Hub {
    private String name;
    private String IP;


    public Hub(String name, String IP) {
        this.name = name;
        this.IP = IP;
    }

    public String getIP() {
        return IP;
    }

    public String getName() {
        return name;
    }
}
