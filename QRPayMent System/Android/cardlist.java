package com.example.kimdoun.qr;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class cardlist extends Activity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private List list = new ArrayList<>();
    private String split;
    private String CN_1;
    private String CN_2;
    private String UN;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.cardlist);

        Intent UNAME = getIntent();
        UN=UNAME.getStringExtra("UNAME");

        DocumentReference cards = db.document("cards/"+UN);

        final ListView CardList = (ListView)findViewById(R.id.CardList);
        final ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, list);


        cards.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(final DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()){
                            split=String.valueOf(documentSnapshot.getData().keySet());
                            split= split.substring(1,(split.length()-1));
                            String[] split1 = split.split(",");
                            Arrays.sort(split1);
                            CardList.setAdapter(adapter);
                            for (int i=0;i<=split1.length-1;i++) {
                                split1[i]=split1[i].trim();
                                list.add(split1[i]);
                            }
                            CardList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                    CN_1=list.get(position).toString();
                                    CN_2=String.valueOf(documentSnapshot.getData().get(CN_1));
                                    CN_2 = CN_2.substring(1,(CN_2.length()-1));
                                    String[] CN_split = CN_2.split(",");

                                    for(int j=0;j<=CN_split.length-1;j++){
                                        CN_split[j]=CN_split[j].trim();
                                        CN_split[j]=CN_split[j].substring(CN_split[j].lastIndexOf("=")+1);
                                    }

                                    Intent send = new Intent();
                                    send.putExtra("cardname",list.get(position).toString());
                                    send.putExtra("cardnum",CN_split[4]);
                                    send.putExtra("cardterm",CN_split[5]);
                                    send.putExtra("bankname",CN_split[3]);
                                    send.putExtra("password",CN_split[1]);
                                    setResult(RESULT_OK, send);
                                    finish();
                                }
                            });

                        }
                    }
                });
    }
}
