package com.example.computer_network;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.*;
import java.util.ArrayList;

public class wait extends AppCompatActivity {

    globalvariable gv;
    private DataInputStream dataInputStream;

    TextView tv_status, tv_statistic;
    Button bt_OK;

    public void OK_onclick(View view) {
        bt_OK.setVisibility(View.INVISIBLE);
        bt_OK.setEnabled(false);
        if (gv.mode.equals("server")){
            Thread thread = new Thread(new server_ok());
            thread.start();
        }
        else {
            Thread thread = new Thread(new client_ok());
            thread.start();
        }
    }

    public class server_ok implements Runnable{
        @Override
        public void run() {
            for (int i = 0; i < gv.threads.size(); i++) {
                try {
                    gv.threads.get(i).join();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            next_turn();
        }
    }

    public class client_ok implements Runnable{
        @Override
        public void run() {
            gv.send_to_server("OKOK");
            Thread.currentThread().interrupt();
        }
    }

    public class receive_client implements Runnable{

        String msg;
        String statistic;

        @Override
        public void run() {
            try {
                while (true) {
                    msg = gv.input.readUTF().toString();
                    if (msg.equals("wait_question")) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tv_status.setText("等待玩家出題");
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
                    else if (msg.equals("all_ok")){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tv_status.setText("所有玩家作答完畢");
                            }
                        });
                        statistic = gv.input.readUTF();
                        int playernumber = gv.input.readInt();
                        for (int i = 0; i < playernumber; i++){
                            String playerscore = gv.input.readUTF();
                            statistic = statistic + "\n" + playerscore;
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tv_statistic.setText(statistic);
                                bt_OK.setVisibility(View.VISIBLE);
                                bt_OK.setEnabled(true);
                            }
                        });
                        continue;
                    }
                    else if (msg.equals("1-1")){
                        statistic = gv.input.readUTF();
                        int playernumber = gv.input.readInt();
                        for (int i = 0; i < playernumber; i++){
                            String playerscore = gv.input.readUTF();
                            statistic = statistic + "\n" + playerscore;
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tv_statistic.setText(statistic);
                            }
                        });
                        continue;
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tv_status.setText(msg);
                        }
                    });
                }
                if (msg.equals("set_question")){
                    Intent intent = new Intent();
                    intent.setClass(wait.this, set_question.class);
                    startActivity(intent);
                }
                else if (msg.equals("ready_question")){
                    String question = gv.input.readUTF();
                    String have_image = gv.input.readUTF();
                    if (have_image.equals("have_image")){
                        int len= gv.input.readInt();
                        gv.imagebuffer = new byte[len];
                        if (len > 0) {
                            gv.input.readFully(gv.imagebuffer,0,gv.imagebuffer.length);
                        }
                    }
                    String ans_a = gv.input.readUTF();
                    String ans_b = gv.input.readUTF();
                    String ans_c = gv.input.readUTF();
                    String true_ans = gv.input.readUTF();
                    Bundle bundle = new Bundle();
                    bundle.putString("question", question);
                    bundle.putString("have_image", have_image);
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

                    String scoreboard = gv.input.readUTF();

                    while (true){
                        String line = gv.input.readUTF();
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
                        tv_status.setText("等待玩家出題");
                    }
                });
                while (true) {
                    String msg = gv.players.get(gv.now_turn - 2).input.readUTF().toString();
                    if (msg.equals("ready_question")){
                        break;
                    }
                }
                String question = gv.players.get(gv.now_turn - 2).input.readUTF().toString();
                String have_image = gv.players.get(gv.now_turn - 2).input.readUTF();
                if (have_image.equals("have_image")){
                    int len= gv.players.get(gv.now_turn - 2).input.readInt();
                    gv.imagebuffer = new byte[len];
                    if (len > 0) {
                        gv.players.get(gv.now_turn - 2).input.readFully(gv.imagebuffer,0,gv.imagebuffer.length);
                    }
                }
                String ans_a = gv.players.get(gv.now_turn - 2).input.readUTF().toString();
                String ans_b = gv.players.get(gv.now_turn - 2).input.readUTF().toString();
                String ans_c = gv.players.get(gv.now_turn - 2).input.readUTF().toString();
                String true_ans = gv.players.get(gv.now_turn - 2).input.readUTF().toString();
                gv.broadcast("ready_question", gv.now_turn);
                gv.broadcast(question, gv.now_turn);
                gv.broadcast(have_image, gv.now_turn);
                if (have_image.equals("have_image")) {
                    int size = gv.imagebuffer.length;
                    for (globalvariable.player p : gv.players) {
                        if (p.playernumber == gv.now_turn)
                            continue;
                        p.output.writeInt(size);
                        p.output.flush();
                        p.output.write(gv.imagebuffer, 0, gv.imagebuffer.length);
                        p.output.flush();
                    }
                }
                gv.broadcast(ans_a, gv.now_turn);
                gv.broadcast(ans_b, gv.now_turn);
                gv.broadcast(ans_c, gv.now_turn);
                gv.broadcast(true_ans, gv.now_turn);

                Bundle bundle = new Bundle();
                bundle.putString("question", question);
                bundle.putString("have_image", have_image);
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
                            p.output.writeUTF("set_question");
                            p.output.flush();
                        }
                        else {
                            p.output.writeUTF("wait_question");
                            p.output.flush();
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
        getSupportActionBar().hide();
        gv = (globalvariable)getApplicationContext();
        bt_OK = (Button)findViewById(R.id.bt_ok);
        tv_status = (TextView) findViewById(R.id.tv_status);
        tv_statistic = (TextView) findViewById(R.id.tv_statistic);
        bt_OK.setEnabled(false);
        bt_OK.setVisibility(View.INVISIBLE);
        Bundle bundle = getIntent().getExtras();
        String content = bundle.getString("content");
        if (content.equals("wait_question")){
            tv_status.setText("等待玩家出題");
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
            gv.broadcast("all_ok", 0);
            if (gv.num_people == gv.now_turn && gv.now_round < gv.num_round) {
                scorebroad = "---> " + gv.your_name + " : " + gv.your_score + "\n";
                gv.broadcast("---> " + gv.your_name + " : " + gv.your_score , 0);
            }
            else {
                scorebroad = "      " + gv.your_name + " : " + gv.your_score + "\n";
                gv.broadcast("      " + gv.your_name + " : " + gv.your_score , 0);
            }
            for (globalvariable.player p : gv.players){
                try {
                    p.output.writeInt(gv.num_people - 1);
                    p.output.flush();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            for (globalvariable.player p : gv.players){
                if (p.playernumber == gv.now_turn + 1) {
                    scorebroad += "---> " + p.playername + " : " + p.score + "\n";
                    gv.broadcast("---> " + p.playername + " : " + p.score, 0);
                }
                else {
                    scorebroad += "      " + p.playername + " : " + p.score + "\n";
                    gv.broadcast("      " + p.playername + " : " + p.score, 0);
                }
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tv_status.setText("所有玩家作答完畢");
                    tv_statistic.setText(scorebroad);
                    bt_OK.setVisibility(View.VISIBLE);
                    bt_OK.setEnabled(true);
                }
            });
            gv.threadinit();
            for (int i = 0; i < gv.threads.size(); i++) {
                gv.threads.get(i).start();
            }
        }
    };
}