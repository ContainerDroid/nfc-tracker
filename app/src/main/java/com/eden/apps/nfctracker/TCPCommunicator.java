package com.eden.apps.nfctracker;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class TCPCommunicator {
    private static String serverHost;
    private static TCPCommunicator uniqInstance;
    private static int serverPort;
    private static List<TCPListener> allListeners;
    private static ServerSocket ss;
    private static Socket s;
    private static BufferedReader in;
    private static BufferedWriter out = null;
    private static OutputStream outputStream;
    private static Handler handler = new Handler();
    private static Handler UIHandler;
    private static Context appContext;

    private TCPCommunicator() {
        allListeners = new ArrayList<TCPListener>();
    }

    public static TCPCommunicator getInstance() {
        if(uniqInstance==null)
        {
            uniqInstance = new TCPCommunicator();
        }
        return uniqInstance;
    }

    public  TCPWriterErrors initServer(int port) {
        setServerPort(port);
        InitTCPServerTask task = new InitTCPServerTask();
        task.execute(new Void[0]);
        return TCPWriterErrors.OK;
    }

    public  TCPWriterErrors initClient(String host, int port) {
        setServerHost(host);
        setServerPort(port);
        InitTCPClientTask task = new InitTCPClientTask();
        task.execute(new Void[0]);
        return TCPWriterErrors.OK;
    }

    public static  TCPWriterErrors writeToSocketFromServer(JSONObject obj)
    {
        try
        {
            out.write(obj.toString() + System.getProperty("line.separator"));
            out.flush();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return TCPWriterErrors.OK;

    }

    public static  TCPWriterErrors writeToSocketFromClient(final JSONObject obj,Handler handle,Context context)
    {
        UIHandler=handle;
        appContext=context;

        Log.d("PISTOL", "trying to send a message");

        Runnable runnable = new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                try
                {
                    // kill some time until the connection is established
                    if (out == null) {
                        Thread.sleep(1000);
                    }
                    String outMsg = obj.toString() + System.getProperty("line.separator");
                    out.write(outMsg);
                    out.flush();
                    Log.d("PISTOL", "sent: " + outMsg);
                }
                catch(Exception e)
                {
                    UIHandler.post(new Runnable() {

                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            Toast.makeText(appContext ,"a problem has occured, the app might not be able to reach the server", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

        };
        Thread thread = new Thread(runnable);
        thread.start();
        return TCPWriterErrors.OK;

    }

    public static void addListener(TCPListener listener) {
        allListeners.add(listener);
    }

    public static void removeAllListeners() {
        allListeners.clear();
    }

    public static int getServerPort() {
        return serverPort;
    }

    public static void setServerPort(int serverPort) {
        TCPCommunicator.serverPort = serverPort;
    }


    public class InitTCPServerTask extends AsyncTask<Void, Void, Void> {
        public InitTCPServerTask() {

        }

        @Override
        protected Void doInBackground(Void... params) {


            try {
                ss = new ServerSocket(TCPCommunicator.getServerPort());
                s = ss.accept();

                for(TCPListener listener:allListeners)
                    listener.addReader(s.getRemoteSocketAddress().toString());

                // new client connected. should update list
                handler.post(new Runnable() {
                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            for(TCPListener listener:allListeners)
                                listener.addReader(s.getRemoteSocketAddress().toString());
                            Log.d("PISTOL", "from runnable");
                        }
                    });

                in = new BufferedReader(new InputStreamReader(s.getInputStream()));
                outputStream = s.getOutputStream();
                out = new BufferedWriter(new OutputStreamWriter(outputStream));

                Log.d("PISTOL", s.toString());
                Log.d("PISTOL", in.toString());


                //receive a message
                String incomingMsg;
                while((incomingMsg=in.readLine())!=null)
                {
                    final String finalMessage=incomingMsg;
                    Log.d("PISTOL", finalMessage);

                    handler.post(new Runnable() {

                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            for(TCPListener listener:allListeners)
                                listener.onTCPMessageReceived(finalMessage);
                            Log.e("TCP", finalMessage);
                        }
                    });
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;

        }
    }

    public static void closeStreams() {
        // TODO Auto-generated method stub
        try
        {
            s.close();
            ss.close();
            out.close();
            in.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public static String getServerHost() {
        return serverHost;
    }

    public static void setServerHost(String serverHost) {
        TCPCommunicator.serverHost = serverHost;
    }

    public class InitTCPClientTask extends AsyncTask<Void, Void, Void>
    {
        public InitTCPClientTask()
        {

        }

        @Override
        protected Void doInBackground(Void... params) {
            try
            {
                s = new Socket(getServerHost(), getServerPort());
                in = new BufferedReader(new InputStreamReader(s.getInputStream()));
                out = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));

                Log.d("PISTOL", s.toString());
                Log.d("PISTOL", in.toString());
                Log.d("PISTOL", out.toString());

                for(TCPListener listener:allListeners)
                    listener.onTCPConnectionStatusChanged(true);
                while(true)
                {
                    String inMsg = in.readLine();
                    if(inMsg!=null)
                    {
                        Log.d("TcpClient", "received: " + inMsg);
                        for(TCPListener listener:allListeners)
                            listener.onTCPMessageReceived(inMsg);
                    }
                }

            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;

        }

    }

    public enum TCPWriterErrors {
        UnknownHostException,
        IOException,
        otherProblem,
        OK}
}