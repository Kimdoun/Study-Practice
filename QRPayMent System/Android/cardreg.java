package com.example.kimdoun.qr;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import static android.support.constraint.Constraints.TAG;


public class cardreg extends Activity{
    TextView cardname;
    TextView cardnum;
    TextView cardterm;
    TextView bankname;
    Button buy;
    TextInputEditText cardpassword;
    private String UN;
    String BCH;
    String BLI;
    String CN;
    String CNU;
    String CT;
    String BN;
    String CP;
    String Result;
    String BNAME;
    String Account;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.card_reg);
        Button cardselect = (Button)findViewById(R.id.CardSelect);
        buy= (Button)findViewById(R.id.buy);
        cardname = (TextView)findViewById(R.id.CardName);
        TextView buych = (TextView)findViewById(R.id.BuyCh);
        TextView buysh = (TextView)findViewById(R.id.BuySh);
        cardnum = (TextView)findViewById(R.id.CardNum);
        cardterm = (TextView)findViewById(R.id.CardTerm);
        bankname = (TextView)findViewById(R.id.BankName);
        cardpassword = (TextInputEditText)findViewById(R.id.CardPassword);

        TextView a = (TextView)findViewById(R.id.textView2);
        TextView b = (TextView)findViewById(R.id.textView);

        Intent BuyLi= getIntent();
        final Intent UNAME = getIntent();
        UN=UNAME.getStringExtra("UNAME");

        buych.setText(BuyLi.getStringExtra("BuyCH"));
        buysh.setText(BuyLi.getStringExtra("BuyLI"));

        Account = BuyLi.getStringExtra("Account");
        BNAME = BuyLi.getStringExtra("BuyNA");
        BCH = BuyLi.getStringExtra("BuyCH");
        BLI = BuyLi.getStringExtra("BuyLI");

        cardselect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(cardreg.this, cardlist.class);
                intent.putExtra("UNAME",UN);
                startActivityForResult(intent,1);
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent send){
        super.onActivityResult(requestCode, resultCode, send);
        if(resultCode != RESULT_OK)
            return;
        if(requestCode == 1){
            CN = send.getStringExtra("cardname");
            CNU = send.getStringExtra("cardnum");
            CT = send.getStringExtra("cardterm");
            BN = send.getStringExtra("bankname");
            CP = send.getStringExtra("password");
            cardname.setText(CN);
            cardnum.setText(CNU);
            cardterm.setText(CT);
            bankname.setText(BN);
            final AlertDialog.Builder Suc = new AlertDialog.Builder(this);
            final AlertDialog.Builder Fail = new AlertDialog.Builder(this);


            buy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(cardpassword.getText().toString().equals(CP)) {
                        Result = Bank.BankResult(BCH, BLI, CN, CNU, CT, BN, BNAME, Account);
                        if (Result.equals("Success")){
                            Suc.setTitle("결제완료");
                        Suc.setMessage("결제가 성공적으로 되었습니다.");
                        Suc.setPositiveButton("확인했습니다.", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent Main = new Intent(cardreg.this, QR_Main.class);
                                startActivity(Main);
                                finish();
                            }
                        });
                        AlertDialog dialog = Suc.create();
                        dialog.show();
                    }else {
                            Fail.setTitle("결재실패");
                            Fail.setMessage("오류가 발생했습니다.");
                            Fail.setPositiveButton("다시시도하겠습니다.", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });
                            AlertDialog dialog = Fail.create();
                            dialog.show();
                        }
                    }else {

                                Fail.setTitle("결재실패");
                                Fail.setMessage("비밀번호가 틀렸습니다.");
                                Fail.setPositiveButton("다시시도하겠습니다.", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                });
                                AlertDialog dialog = Fail.create();
                                dialog.show();

                    }
                }
            });
        }
    }
}
