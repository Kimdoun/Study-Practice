package com.example.kimdoun.qr;
import android.util.Log;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

import static android.support.constraint.Constraints.TAG;

public class Bank {
    public static String BankResult(String BCH, String BLI, String CN, String CNU, String CT, String BN, String BNAME, String Account) {
        final String Data = BCH+","+BLI+","+CN+","+CNU+","+CT+","+BN+","+BNAME+","+Account+"\n";
        final String[] Result = {""};
        class SockThread extends Thread{
            public void run(){
                try {
                    Socket socket = new Socket("kimdountest.iptime.org", 1004);
                    BufferedReader in = new BufferedReader(new InputStreamReader(
                            socket.getInputStream()));
                    OutputStream out = socket.getOutputStream();
                    out.write(Data.getBytes());
                    Log.d(TAG, " TEST2" + Data);
                    out.flush();
                    Result[0] = in.readLine();
                    in.close();
                    out.close();
                    Log.d(TAG, " 성공여부"+ Result[0]);
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    Result[0] = "Fail";
                }
            }
        }
        SockThread sock;
        sock = new SockThread();
        sock.start();

        try {
            sock.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return Result[0];
    }
}
