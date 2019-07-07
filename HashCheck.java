package com.example.kimdoun.iot;

import android.util.Log;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;


import java.util.Hashtable;

public class HashCheck extends StringRequest{
    final  static  private String URL = "http://172.30.98.123:80/Check.php";
    private Hashtable<String,String> parameters;
    public HashCheck(String Num,String key,String Command, String Prkey, Response.Listener<String> listener)
    {
        // hashMap 을 이용해서 데이터 DB로 전송.
        super(Method.POST, URL,listener,null);
        parameters = new Hashtable<>();
        parameters.put("Num",Num);
        parameters.put("key",key);
        parameters.put("Command",Command);
        parameters.put("Prkey",Prkey);
    }
    @Override
    public Hashtable<String, String> getParams() {
        return parameters;
    }
}
