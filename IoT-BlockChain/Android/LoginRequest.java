package com.example.kimdoun.iot;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class LoginRequest extends StringRequest{
    final  static  private String URL = "http://172.30.98.123:80/Login.php";
    private Map<String, String> parameters;
    public LoginRequest(String userID, String userPassword, Response.Listener<String> listener)
    {
        // hashMap 을 이용해서 데이터 DB로 전송.
        super(Method.POST, URL,listener,null);
        parameters = new HashMap<>();
        parameters.put("userID", userID);
        parameters.put("userPassword",userPassword);
    }
    @Override
    public Map<String, String> getParams() {
        return parameters;
    }
}
