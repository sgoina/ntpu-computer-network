package com.example.computer_network;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import java.io.IOException;

public class result extends AppCompatActivity {

    globalvariable gv;
    TextView tv_result;
    String scorebroad;


    public class send_result implements Runnable{
        @Override
        public void run() {
            try {
                gv.broadcast("END", 0);
                gv.broadcast(gv.firstplayername + " : " + String.valueOf(gv.firstplayerscore), 0);
                for (globalvariable.player p : gv.players){
                    gv.broadcast(p.playername + " : " + String.valueOf(p.score), 0);
                }
                gv.broadcast("no", 0);
            } catch (Exception e) {

            }
        }
    }

//    public class receive_result implements Runnable{
//
//        String msg, result;
//        @Override
//        public void run() {
//            result = "";
//            int i = 0;
//            try {
//                while(true){
//                    msg = gv.br.readLine();
//                    if (msg.equals("no"))
//                        break;
//                    if (i == 0)
//                        result = msg;
//                    else
//                        result = result + "\n" + msg;
//                    i++;
//                }
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        tv_result.setText(result);;
//                    }
//                });
//            } catch (Exception e) {
//
//            }
//        }
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        gv = (globalvariable)getApplicationContext();
        tv_result = (TextView) findViewById(R.id.tv_result);
        if (gv.mode.equals("server")) {
            scorebroad = gv.firstplayername + " : " + gv.firstplayerscore + "\n";
            for (globalvariable.player p : gv.players) {
                scorebroad += p.playername + " : " + p.score + "\n";
            }
            tv_result.setText(scorebroad);
            Thread thread = new Thread(new send_result());
            thread.start();
        }
        else{
            Bundle bundle = getIntent().getExtras();
            String sss = bundle.getString("Result");
            tv_result.setText(sss);
//            Thread thread = new Thread(new receive_result());
//            thread.start();
        }
    }
}