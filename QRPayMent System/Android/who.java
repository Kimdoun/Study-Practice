package com.example.kimdoun.qr;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;
import java.util.Random;

import static android.support.constraint.Constraints.TAG;

public class who extends Activity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    public String Mailcode="";
    String Rcode;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.who);

        final Button sign = (Button)findViewById(R.id.Sign);
        Button signcode = (Button)findViewById(R.id.SignCode);
        final TextInputEditText name = (TextInputEditText)findViewById(R.id.Name);
        final TextInputEditText phone = (TextInputEditText)findViewById(R.id.Phone);
        final TextInputEditText mail = (TextInputEditText)findViewById(R.id.mail);
        final TextInputEditText code = (TextInputEditText)findViewById(R.id.Code);

        final String[] check = {"","","",""};
        final Intent Come = getIntent();

        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .permitDiskReads()
                .permitDiskWrites()
                .permitNetwork().build());


        sign.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                final DocumentReference test = db.document("priva/"+Come.getStringExtra("UserName"));
                test.get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                if(documentSnapshot.exists()){
                                    final Map<String, Object> UserList = documentSnapshot.getData();
                                    check[0] =name.getText().toString();
                                    check[1] =mail.getText().toString();
                                    check[2] =phone.getText().toString();
                                    check[3] = code.getText().toString();
                                    if(check[0].equals(UserList.get("name")) && check[1].equals(UserList.get("email")) && check[2].equals(UserList.get("phonenumber")) && check[3].equals(Mailcode)) {
                                        test.update("auth", "1");
                                        Intent CardRegView = new Intent(
                                                getApplicationContext(),
                                                QR_Main.class);
                                        Mailcode="";
                                        startActivity(CardRegView);
                                        finish();
                                    }else{
                                        Toast.makeText(getApplicationContext(), "인증에 실패했습니다",Toast.LENGTH_LONG).show();
                                    }
                                }else {

                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });
            }
        });

        signcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //SMTP 서버 라이브러리 추가후 코드 작성 미구현
                Random Mcode = new Random();
                for(int n=0;n<6;n++){
                    Rcode = Integer.toString(Mcode.nextInt(10));
                    if(!Mailcode.contains(Rcode)){
                        Mailcode += Rcode;
                    }else {
                        n-=1;
                    }
                }
                try{
                    SMTP smtp = new SMTP("ehdjs2134@gmail.com","Adpebajs12!");
                    smtp.sendMail("인증코드입니다.", Mailcode, mail.getText().toString());
                    Toast.makeText(getApplicationContext(), "이메일을 성공적으로 보냈습니다.", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    @Override
    public void onBackPressed(){
        finishAffinity();
        System.runFinalization();
        System.exit(0);
    }
}
