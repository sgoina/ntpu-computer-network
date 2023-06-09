package com.example.computer_network;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;

public class result extends AppCompatActivity {

    globalvariable gv;
    TextView tv_result;
    String scorebroad;

    public void back_to_home_onclick(View view) {
        gv.close();
        Intent intent = new Intent();
        intent.setClass(result.this, MainActivity.class);
        startActivity(intent);
    }


    public class send_result implements Runnable{
        @Override
        public void run() {
            try {
                gv.broadcast("END", 0);
                gv.broadcast(gv.your_name + " : " + String.valueOf(gv.your_score), 0);
                for (globalvariable.player p : gv.players){
                    gv.broadcast(p.playername + " : " + String.valueOf(p.score), 0);
                }
                gv.broadcast("no", 0);
            } catch (Exception e) {

            }
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        getSupportActionBar().hide();
        gv = (globalvariable)getApplicationContext();
        tv_result = (TextView) findViewById(R.id.tv_result);
        if (gv.mode.equals("server")) {
            scorebroad = gv.your_name + " : " + gv.your_score + "\n";
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
        }
    }
}