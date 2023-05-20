package com.example.computer_network;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;

public class wait extends AppCompatActivity {

    globalvariable gv;

    TextView tv_status, tv_statistic;

    public class receive_client implements Runnable{

        String msg;

        @Override
        public void run() {
            try {
                while (true) {
                    msg = gv.br.readLine().toString();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tv_status.setText(msg);
                        }
                    });
                    if (msg.equals("wait_question")) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tv_status.setText("Wait for question");
                            }
                        });
                        continue;
                    }
                    else if (msg.equals("set_question")){
                        break;
                    }
                    else if (msg.equals("ready_question")){
                        break;
                    }
                    else if (msg.equals("END")){
                        break;
                    }
//                    else if (msg.equals("all_ok")){
//                        tv_status.setText(msg);
//                        String scorebroad = "";
//                        while(true){
//                            msg = gv.br.readLine().toString();
//                            if (msg.equals("end"))
//                                break;
//                            scorebroad = scorebroad + "\n" + msg;
//                        }
//                        tv_statistic.setText(scorebroad);
//                    }
//                    tv_status.setText(msg);
                }
                if (msg.equals("set_question")){
                    Intent intent = new Intent();
                    intent.setClass(wait.this, set_question.class);
                    startActivity(intent);
                }
                else if (msg.equals("ready_question")){
                    String question = gv.br.readLine().toString();
                    String ans_a = gv.br.readLine().toString();
                    String ans_b = gv.br.readLine().toString();
                    String ans_c = gv.br.readLine().toString();
                    String true_ans = gv.br.readLine().toString();
                    Bundle bundle = new Bundle();
                    bundle.putString("question", question);
                    bundle.putString("ans_a", ans_a);
                    bundle.putString("ans_b", ans_b);
                    bundle.putString("ans_c", ans_c);
                    bundle.putString("true_ans", true_ans);
                    Intent intent = new Intent();
                    intent.putExtras(bundle);
                    intent.setClass(wait.this, player_answer.class);
                    startActivity(intent);
                }
                else if (msg.equals("END")){
                    String scoreboard = gv.br.readLine().toString();
                    while (true){
                        String line = gv.br.readLine().toString();
                        if (line.equals("no"))
                            break;
                        scoreboard = scoreboard + "\n" + line;
                    }
                    Bundle bundle = new Bundle();
                    bundle.putString("Result", scoreboard);
                    Intent intent = new Intent();
                    intent.putExtras(bundle);
                    intent.setClass(wait.this, result.class);
                    startActivity(intent);
                }
            } catch (IOException e) {

            } catch (Exception e) {

            }
        }
    }

    public class receive_server implements Runnable{
        @Override
        public void run() {
            try {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tv_status.setText("Wait for question");
                    }
                });
                while (true) {
                    String msg = gv.players.get(gv.now_turn - 2).br.readLine().toString();
                    if (msg.equals("ready_question")){
                        break;
                    }
                }
                String question = gv.players.get(gv.now_turn - 2).br.readLine().toString();
                String ans_a = gv.players.get(gv.now_turn - 2).br.readLine().toString();
                String ans_b = gv.players.get(gv.now_turn - 2).br.readLine().toString();
                String ans_c = gv.players.get(gv.now_turn - 2).br.readLine().toString();
                String true_ans = gv.players.get(gv.now_turn - 2).br.readLine().toString();
                gv.broadcast("ready_question", gv.now_turn);
                gv.broadcast(question, gv.now_turn);
                gv.broadcast(ans_a, gv.now_turn);
                gv.broadcast(ans_b, gv.now_turn);
                gv.broadcast(ans_c, gv.now_turn);
                gv.broadcast(true_ans, gv.now_turn);

                Bundle bundle = new Bundle();
                bundle.putString("question", question);
                bundle.putString("ans_a", ans_a);
                bundle.putString("ans_b", ans_b);
                bundle.putString("ans_c", ans_c);
                bundle.putString("true_ans", true_ans);
                Intent intent = new Intent();
                intent.putExtras(bundle);
                intent.setClass(wait.this, player_answer.class);
                startActivity(intent);
            } catch (IOException e) {

            } catch (Exception e) {

            }
        }
    }

    private void next_turn(){
        gv.now_turn++;
        if (gv.now_turn > gv.num_people){
            gv.now_turn = 1;
            gv.now_round++;
        }
        if (gv.now_round > gv.num_round){
            Intent intent = new Intent();
            intent.setClass(wait.this, result.class);
            startActivity(intent);
        }
        else {
            if (gv.now_turn == 1) {
                gv.broadcast("wait_question", 0);
                Intent intent = new Intent();
                intent.setClass(wait.this, set_question.class);
                startActivity(intent);
            }
            else{
                try {
                    for (globalvariable.player p : gv.players){
                        if(p.playernumber == gv.now_turn) {
                            p.bw.write("set_question");
                            p.bw.newLine();
                            p.bw.flush();
                        }
                        else {
                            p.bw.write("wait_question");
                            p.bw.newLine();
                            p.bw.flush();
                        }

                    }
                    Thread thread = new Thread(new receive_server());
                    thread.start();
                }catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wait);
        gv = (globalvariable)getApplicationContext();
        tv_status = (TextView) findViewById(R.id.tv_status);
        tv_statistic = (TextView) findViewById(R.id.tv_statistic);
        Bundle bundle = getIntent().getExtras();
        String content = bundle.getString("content");
        if (content.equals("wait_question")){
            tv_status.setText("Wait for question");
            if (gv.mode.equals("server")){
                Thread thread = new Thread(new receive_server());
                thread.start();
            }
        }
        else if (content.equals("wait_ans")){
            tv_status.setText("等待玩家作答");
            if (gv.mode.equals("server")){
                Thread thread = new Thread(wait_threads);
                thread.start();
            }
        }
        else if (content.equals("set_question")){
            Intent intent = new Intent();
            intent.setClass(wait.this, set_question.class);
            startActivity(intent);
        }
        else{
            tv_status.setText(content);
        }
        if (gv.mode.equals("client")) {
            Thread thread = new Thread(new receive_client());
            thread.start();
        }
    }

    String scorebroad;
    private Runnable wait_threads = new Runnable(){
        @Override
        public void run() {
            gv.threadinit();
            for (int i = 0; i < gv.threads.size(); i++) {
                if (gv.now_turn == i + 2)
                    continue;
                gv.threads.get(i).start();
            }
            for (int i = 0; i < gv.threads.size(); i++) {
                try {
                    if (gv.now_turn == i + 2)
                        continue;
                    gv.threads.get(i).join();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
//            gv.broadcast("all_ok", 0);
            scorebroad = gv.firstplayername + " : " + gv.firstplayerscore + "\n";
//            gv.broadcast(gv.firstplayername + " : " + gv.firstplayerscore, 0);
            for (globalvariable.player p : gv.players){
                scorebroad += p.playername + " : " + p.score + "\n";
//                gv.broadcast(p.playername + " : " + p.score, 0);
            }
//            gv.broadcast("end", 0);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tv_statistic.setText(scorebroad);
                }
            });

            next_turn();
        }
    };
}