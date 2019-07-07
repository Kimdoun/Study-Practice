package com.example.kimdoun.qr;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.Map;

import static android.content.ContentValues.TAG;

public class QR_Main extends AppCompatActivity {

    private String PhoneNum;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    String check = "0";

    private String[] BuyLI;
    private String NA;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TelephonyManager telManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, "android.permission.READ_PHONE_NUMBERS") != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        PhoneNum = telManager.getLine1Number();
        Log.w(TAG,"PHONE"+PhoneNum);
        if(PhoneNum.startsWith("+82")){
            PhoneNum = PhoneNum.replace("+82", "0");
        }
        Log.w(TAG,"PHONE"+PhoneNum);
        DocumentReference test1 = db.document("phone/"+PhoneNum);


        test1.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()){
                            Map<String, Object> PH = documentSnapshot.getData();
                            NA=PH.get("name").toString();
                            DocumentReference test = db.document("priva/"+NA);

                            test.get()
                                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            if(documentSnapshot.exists()){
                                                Map<String, Object> UserList = documentSnapshot.getData();
                                                if(check.equals(UserList.get("auth"))){
                                                    Intent whoView = new Intent(
                                                            getApplicationContext(),
                                                            who.class);
                                                    whoView.putExtra("UserName",UserList.get("name").toString());
                                                    startActivity(whoView);
                                                    finish();
                                                }
                                            }else {
                                                Log.d(TAG, "실패다");

                                            }
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {

                                        }
                                    });
                        }

                    }
                });
       //QR코드 스캔 삽입//

        IntentIntegrator intentIntegrator = new IntentIntegrator(this);
        intentIntegrator.setBeepEnabled(true);//바코드 인식시 소리
        intentIntegrator.setOrientationLocked(false);//세로
        intentIntegrator.setCaptureActivity(QR_Code_Read.class);
        intentIntegrator.initiateScan();
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
                finish();
            } else {
                // 화면전환 삽입.
                //데이터넘기는거 삽입
                BuyLI=result.getContents().split(",");
                for(int h=0;h<=BuyLI.length-1;h++){
                    BuyLI[h]=BuyLI[h].trim();
                }

                Intent CardregView = new Intent(
                        getApplicationContext(),
                        cardreg.class);


                /*
                계좌번호, 0
                계좌소유주, 1
                결제매장, 2
                결제금액, 3
                 */
                CardregView.putExtra("Account",BuyLI[0]);
                CardregView.putExtra("BuyNA",BuyLI[1]);
                CardregView.putExtra("BuyCH",BuyLI[3]);
                CardregView.putExtra("BuyLI",BuyLI[2]);
                CardregView.putExtra("UNAME",NA);
                startActivity(CardregView);
                finish();


            }

        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
    @Override
    public void onBackPressed(){
        finishAffinity();
        System.runFinalization();
        System.exit(0);
    }
}
