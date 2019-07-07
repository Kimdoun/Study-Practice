package com.example.kimdoun.iot;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final EditText u_id = (EditText)findViewById(R.id.u_id);
        final EditText u_password= (EditText)findViewById(R.id.u_password);
        final Button SignIn = (Button)findViewById(R.id.SignIn);
        SignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String userID = u_id.getText().toString();
                final String userPassword =u_password.getText().toString();
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        /*MySQL 연동 로그인
                            아이디 비밀번호 데이터 불러오기*/
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");
                            // 로그인 성공시 php 에서 불러온 DB 변수 저장
                            if(success){
                                Intent intent = new Intent(MainActivity.this, success.class);
                                MainActivity.this.startActivity(intent);
                            }
                            else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                builder.setMessage("인증 실패 하였습니다.")
                                        .setNegativeButton("다시 시도",null)
                                        .create()
                                        .show();
                            }
                        } catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                    }
                };
                // 웹기반 ReQuest를 이용한 데이터 전송.
                LoginRequest loginRequest = new LoginRequest(userID  , userPassword, responseListener);
                RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
                queue.add(loginRequest);
            }
        });
    }
}
