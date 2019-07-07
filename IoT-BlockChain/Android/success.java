package com.example.kimdoun.iot;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

// WIFI import
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.EditText;

import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.Socket;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.security.CryptoPrimitive;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static android.os.StrictMode.*;
import static java.lang.Thread.sleep;

public class success extends AppCompatActivity{

    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private Button Connect, Disconnect, Exit, N_poweroff, N_sleep, N_start;
    public String Command, Result, key, Num, Prkey;
    public static int Cnt=0;
    TextView Stat;
    String Re[];
    String kRe[]={"Num","key","Command","Prkey"};

    @Override
    protected  void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success);

        Connect = (Button)findViewById(R.id.Connect);
        Disconnect = (Button)findViewById(R.id.Disconnect);
        Exit = (Button)findViewById(R.id.Exit);
        N_start = (Button)findViewById(R.id.N_start);
        N_sleep = (Button)findViewById(R.id.N_sleep);
        N_poweroff = (Button)findViewById(R.id.N_poweroff);
        Stat = (TextView)findViewById(R.id.Stat);

        //버튼 비활성화
        Disconnect.setEnabled(false);
        N_sleep.setEnabled(false);
        N_poweroff.setEnabled(false);
        N_start.setEnabled(false);

        // 인증 로그.
        AlertDialog.Builder builder = new AlertDialog.Builder(success.this);
        builder.setMessage(" 인증되었습니다.")
                .setNegativeButton("확인",null)
                .create()
                .show();

        //쓰레드 권한부여
        ThreadPolicy policy = new ThreadPolicy.Builder().permitAll().build();
        setThreadPolicy(policy);

        //객체생성
        Re =new String[4];

        //소켓 연결 버튼
        Connect.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                //쓰레드 생성
                Thread worker = new Thread() {
                    public void run() {
                        try {
                            //최초 블럭 생성
                            Num = "0";
                            Command = "Connect";
                            Prkey="0";
                            key = SHA256.SHA256(Num,Command,Prkey);
                            Response.Listener<String> hash = new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    try {
                                        JSONObject jsonResponse = new JSONObject(response);
                                        boolean success = jsonResponse.getBoolean("success");
                                    } catch (JSONException e)
                                    {
                                        e.printStackTrace();
                                    }
                                }
                            };
                            HashKey hashkey = new HashKey(Num, key, Command, Prkey, hash);
                            RequestQueue queue = Volley.newRequestQueue(success.this);
                            queue.add(hashkey);

                            socket = new Socket("172.30.98.101", 5500);
                            out = new DataOutputStream(socket.getOutputStream());
                            in = new DataInputStream(socket.getInputStream());
                            //서버 연결하고 전송 되는지 확인.
                            Log.w("Test", "" + Command);
                            out.writeUTF(Command);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Stat.setText("서버와 연결 되었습니다.");
                                    N_sleep.setEnabled(true);
                                    N_poweroff.setEnabled(true);
                                    N_start.setEnabled(true);
                                    Connect.setEnabled(false);
                                    Exit.setEnabled(false);
                                    Disconnect.setEnabled(true);
                                }
                            });
                            } catch (IOException e) {
                            e.printStackTrace();
                            Log.d("Fail Log","Socket Create");
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Stat.setText("서버가 열려있지 않습니다.");
                                }
                            });
                        }
                        }
                };
                worker.start();
            }
        });
        //소켓 연결 해제 버튼
        Disconnect.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Connect.setEnabled(true);
                Command = "Disconnect";
                //key = SHA256.SHA256(Command);
                Num = SHA256.Num;
                Response.Listener<String> hash = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");
                        } catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                    }
                };
                HashKey hashkey = new HashKey(Num, key, Command, Prkey, hash);
                RequestQueue queue = Volley.newRequestQueue(success.this);
                queue.add(hashkey);
                try{
                    Log.w("Test",""+Command);
                    out.writeUTF(key);
                    try {
                        Result = in.readUTF();
                        Stat.setText(Result);
                    } catch (Exception e) {
                    }

                    N_sleep.setEnabled(false);
                    N_poweroff.setEnabled(false);
                    N_start.setEnabled(false);
                    Disconnect.setEnabled(false);
                    Exit.setEnabled(true);
                }catch (Exception e){
                    Log.d("Fail Log","DisConnect");
                    Stat.setText("연결 되어 있지 않습니다.\n연결 해제 할 수 없습니다.");
                    N_sleep.setEnabled(false);
                    N_poweroff.setEnabled(false);
                    N_start.setEnabled(false);
                    Exit.setEnabled(true);
                }
            }
        });
        //노트북 실행
        N_start.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {// 버튼 클릭시 발생하는 이벤트
                Command = "Start"; // Start 커맨드 저장
                Response.Listener<String> check = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {// 어떠한 이벤트가발생시 실행되는 요청 -> DB에서 가져온후 비교
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");
                            if (success) {
                                for (int i = 0; i < 4; i++) {// DB Server에서 모든 데이터 변수저장
                                    Re[i] = jsonResponse.getString(kRe[i]);
                                }
                                if(Re[1].equals(key)){ // Server 로부터 받은 Hash 값과 DB에 저장된 마지막 블럭 Hash값 비교
                                    Stat.setText("실행 중입니다.");
                                    Prkey = key;
                                    Cnt=Integer.parseInt(Re[0]);
                                    Cnt++;
                                    Re[0] = String.valueOf(Cnt);
                                    Re[1] = SHA256.SHA256(Re[0],Command,Re[1]);// 다시 암호화
                                    Response.Listener<String> hash = new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response2) {// -> DB에 데이터 블럭들을 체크
                                            try {
                                                JSONObject jsonResponse = new JSONObject(response2);
                                                boolean success = jsonResponse.getBoolean("success");
                                            } catch (JSONException e)
                                            {
                                                e.printStackTrace();
                                            }
                                        }
                                    };
                                    HashKey hashkey = new HashKey(Re[0], Re[1], Command, key, hash);
                                    RequestQueue queue = Volley.newRequestQueue(success.this);
                                    queue.add(hashkey);
                                }
                            } else {
                                Stat.setText("해쉬값 변조가 이루어졌습니다.");
                            }
                            out.writeUTF(Re[1]);// -> Server로 전송
                            try{

                                key = in.readUTF();//-> Server로 부터 받은 데이터 저장

                                if(key.equals("fail")) {
                                    Stat.setText("노트북을 실행을 실패했습니다.");
                                }else {
                                    try {
                                        Response.Listener<String> check = new Response.Listener<String>() {
                                            @Override
                                            public void onResponse(String response1) { // DB Server 데이터를 가져와 비교검증
                                                try {
                                                    JSONObject jsonResponse = new JSONObject(response1);
                                                    boolean success = jsonResponse.getBoolean("success");
                                                    if (success) {
                                                        for (int i = 0; i < 4; i++) {
                                                            Re[i] = jsonResponse.getString(kRe[i]);
                                                        }
                                                        if(Re[1].equals(key)) {
                                                            Stat.setText("실행 됬습니다.");
                                                            Command = "Success";
                                                            Prkey = key;
                                                            Cnt = Integer.parseInt(Re[0]);
                                                            Cnt++;
                                                            Re[0] = String.valueOf(Cnt);
                                                            Re[1] = SHA256.SHA256(Re[0], Command, Re[1]);
                                                            key = Re[1];
                                                            Response.Listener<String> hash = new Response.Listener<String>() {
                                                                @Override
                                                                public void onResponse(String response2) {
                                                                    try {
                                                                        JSONObject jsonResponse = new JSONObject(response2);
                                                                        boolean success = jsonResponse.getBoolean("success");
                                                                    } catch (JSONException e) {
                                                                        e.printStackTrace();
                                                                    }
                                                                }
                                                            };
                                                            HashKey hashkey = new HashKey(Re[0], Re[1], Command, Prkey, hash);
                                                            RequestQueue queue2 = Volley.newRequestQueue(success.this);
                                                            queue2.add(hashkey);
                                                        }else{
                                                            Stat.setText("노트북을 실행을 실패했습니다.");
                                                        }
                                                    }
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                    Stat.setText("해쉬값 변조가 이루어졌습니다.");
                                                }
                                            }
                                        };
                                        HashCheck hashcheck = new HashCheck(Num, key, Command, Prkey, check);
                                        RequestQueue queue1 = Volley.newRequestQueue(success.this);
                                        queue1.add(hashcheck);
                                    } catch (Exception e) {
                                        Stat.setText("해쉬값 변조가 이루어졌습니다.");
                                    }
                                }

                            }catch (Exception e) {
                                Log.d("Fail Log", "N_Start");
                                Stat.setText("노트북을 실행을 실패했습니다.");
                            }

                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }

                    }
                };
                HashCheck hashcheck = new HashCheck(Num,key,Command,Prkey, check);
                RequestQueue queue = Volley.newRequestQueue(success.this);
                queue.add(hashcheck);
            }
        });
        //노트북 절전
        N_sleep.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Command = "Sleep";
                Response.Listener<String> check = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");
                            if (success) {
                                for (int i = 0; i < 4; i++) {
                                    Re[i] = jsonResponse.getString(kRe[i]);
                                }
                                if(Re[1].equals(key)){
                                    Stat.setText("절전 중입니다.");
                                    Prkey = key;
                                    Cnt=Integer.parseInt(Re[0]);
                                    Cnt++;
                                    Re[0] = String.valueOf(Cnt);
                                    Re[1] = SHA256.SHA256(Re[0],Command,Re[1]);
                                    Response.Listener<String> hash = new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response2) {
                                            try {
                                                JSONObject jsonResponse = new JSONObject(response2);
                                                boolean success = jsonResponse.getBoolean("success");
                                            } catch (JSONException e)
                                            {
                                                e.printStackTrace();
                                            }
                                        }
                                    };
                                    HashKey hashkey = new HashKey(Re[0], Re[1], Command, key, hash);
                                    RequestQueue queue = Volley.newRequestQueue(success.this);
                                    queue.add(hashkey);
                                }
                            } else {
                                Stat.setText("해쉬값 변조가 이루어졌습니다.");
                            }
                            out.writeUTF(Re[1]);
                            try{

                                key = in.readUTF();
                                if(key.equals("fail")){
                                    Stat.setText("노트북을 절전을 실패했습니다.");
                                }else {
                                    try {
                                        Response.Listener<String> check = new Response.Listener<String>() {
                                            @Override
                                            public void onResponse(String response1) {
                                                try {
                                                    JSONObject jsonResponse = new JSONObject(response1);
                                                    boolean success = jsonResponse.getBoolean("success");
                                                    if (success) {
                                                        for (int i = 0; i < 4; i++) {
                                                            Re[i] = jsonResponse.getString(kRe[i]);
                                                        }
                                                        if(Re[1].equals(key)) {
                                                            Stat.setText("절전 됬습니다.");
                                                            Command = "Success";
                                                            Prkey = key;
                                                            Cnt = Integer.parseInt(Re[0]);
                                                            Cnt++;
                                                            Re[0] = String.valueOf(Cnt);
                                                            Re[1] = SHA256.SHA256(Re[0], Command, Re[1]);
                                                            key = Re[1];
                                                            Response.Listener<String> hash = new Response.Listener<String>() {
                                                                @Override
                                                                public void onResponse(String response2) {
                                                                    try {
                                                                        JSONObject jsonResponse = new JSONObject(response2);
                                                                        boolean success = jsonResponse.getBoolean("success");
                                                                    } catch (JSONException e) {
                                                                        e.printStackTrace();
                                                                    }
                                                                }
                                                            };
                                                            HashKey hashkey = new HashKey(Re[0], Re[1], Command, Prkey, hash);
                                                            RequestQueue queue2 = Volley.newRequestQueue(success.this);
                                                            queue2.add(hashkey);
                                                        }else
                                                        {
                                                            Stat.setText("노트북을 절전을 실패했습니다.");
                                                        }
                                                    }
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        };
                                        HashCheck hashcheck = new HashCheck(Num, key, Command, Prkey, check);
                                        RequestQueue queue1 = Volley.newRequestQueue(success.this);
                                        queue1.add(hashcheck);
                                    } catch (Exception e) {
                                    }
                                }

                            }catch (Exception e) {
                                Log.d("Fail Log", "N_Start");
                                Stat.setText("노트북을 절전을 실패했습니다.");
                            }


                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                };
                HashCheck hashcheck = new HashCheck(Num,key,Command,Prkey, check);
                RequestQueue queue = Volley.newRequestQueue(success.this);
                queue.add(hashcheck);
            }
        });
        //노트북 종료
        N_poweroff.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Command = "Poweroff";
                Response.Listener<String> check = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");
                            if (success) {
                                for (int i = 0; i < 4; i++) {
                                    Re[i] = jsonResponse.getString(kRe[i]);
                                }
                                if(Re[1].equals(key)){
                                    Stat.setText("종료 중입니다.");
                                    Prkey = key;
                                    Cnt=Integer.parseInt(Re[0]);
                                    Cnt++;
                                    Re[0] = String.valueOf(Cnt);
                                    Re[1] = SHA256.SHA256(Re[0],Command,Re[1]);
                                    Response.Listener<String> hash = new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response2) {
                                            try {
                                                JSONObject jsonResponse = new JSONObject(response2);
                                                boolean success = jsonResponse.getBoolean("success");
                                            } catch (JSONException e)
                                            {
                                                e.printStackTrace();
                                            }
                                        }
                                    };
                                    HashKey hashkey = new HashKey(Re[0], Re[1], Command, key, hash);
                                    RequestQueue queue = Volley.newRequestQueue(success.this);
                                    queue.add(hashkey);
                                }
                            } else {
                                Stat.setText("해쉬값 변조가 이루어졌습니다.");
                            }
                            out.writeUTF(Re[1]);
                            try{

                                key = in.readUTF();
                                if(key.equals("fail")){
                                    Stat.setText("노트북을 종료을 실패했습니다.");
                                }else {
                                    try {
                                        Response.Listener<String> check = new Response.Listener<String>() {
                                            @Override
                                            public void onResponse(String response1) {
                                                try {
                                                    JSONObject jsonResponse = new JSONObject(response1);
                                                    boolean success = jsonResponse.getBoolean("success");
                                                    if (success) {
                                                        for (int i = 0; i < 4; i++) {
                                                            Re[i] = jsonResponse.getString(kRe[i]);
                                                        }
                                                        if (Re[1].equals(key)) {
                                                            Stat.setText("종료 됬습니다.");
                                                            Command = "Success";
                                                            Prkey = key;
                                                            Cnt = Integer.parseInt(Re[0]);
                                                            Cnt++;
                                                            Re[0] = String.valueOf(Cnt);
                                                            Re[1] = SHA256.SHA256(Re[0], Command, Re[1]);
                                                            key = Re[1];
                                                            Response.Listener<String> hash = new Response.Listener<String>() {
                                                                @Override
                                                                public void onResponse(String response2) {
                                                                    try {
                                                                        JSONObject jsonResponse = new JSONObject(response2);
                                                                        boolean success = jsonResponse.getBoolean("success");
                                                                    } catch (JSONException e) {
                                                                        e.printStackTrace();
                                                                    }
                                                                }
                                                            };
                                                            HashKey hashkey = new HashKey(Re[0], Re[1], Command, Prkey, hash);
                                                            RequestQueue queue2 = Volley.newRequestQueue(success.this);
                                                            queue2.add(hashkey);
                                                        }else{
                                                            Stat.setText("노트북을 실행을 실패했습니다.");
                                                        }
                                                    }
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        };
                                        HashCheck hashcheck = new HashCheck(Num, key, Command, Prkey, check);
                                        RequestQueue queue1 = Volley.newRequestQueue(success.this);
                                        queue1.add(hashcheck);
                                    } catch (Exception e) {
                                    }
                                }

                            }catch (Exception e) {
                                Log.d("Fail Log", "N_Start");
                                Stat.setText("노트북을 종료을 실패했습니다.");
                            }

                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                };
                HashCheck hashcheck = new HashCheck(Num,key,Command,Prkey, check);
                RequestQueue queue = Volley.newRequestQueue(success.this);
                queue.add(hashcheck);
            }
        });
        //어플 종료
        Exit.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                finishAffinity();
                System.runFinalization();
                System.exit(0);
            }
        });
    }
}

